package com.example.bookingservice.service;

import com.example.bookingservice.request.BookingRequest;
import com.example.bookingservice.dto.SalonDto;
import com.example.bookingservice.dto.ServiceOfferingDto;
import com.example.bookingservice.dto.UserDto;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.BookingStatus;
import com.example.bookingservice.model.SalonReport;
import com.example.bookingservice.repository.BookingRepository;
import com.example.bookingservice.service.client.ServiceOfferingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final ServiceOfferingFeignClient serviceOffering;
	
	@Override
	public Booking createBooking(BookingRequest booking,
	                             UserDto userDto,
	                             SalonDto salonDto,
	                             Set<ServiceOfferingDto> serviceDtoSet) {
		
		int totoalDuration = serviceDtoSet.stream()
				.mapToInt(ServiceOfferingDto::getDuration)
				.sum();
		
		LocalDateTime bookingStartTime = booking.getStartTime();
		LocalDateTime bookingEndTime = booking.getStartTime().plusMinutes(totoalDuration);
		validateTimeSlot(bookingStartTime, bookingEndTime, salonDto);
		
		double totalPrice = serviceDtoSet.stream()
				.mapToDouble(ServiceOfferingDto::getPrice)
				.sum();
		
		Set<Long> idList = serviceDtoSet.stream()
				.map(ServiceOfferingDto::getId)
				.collect(Collectors.toSet());
		
		Booking newBooking = Booking.builder()
				.salonId(salonDto.getSalonId())
				.customerId(userDto.getId())
				.startTime(bookingStartTime)
				.endTime(bookingEndTime)
				.totalPrice(totalPrice)
				.status(BookingStatus.PENDING)
				.serviceIds(idList)
				.build();
		
		return bookingRepository.save(newBooking);
		
		
	}
	
	public void validateTimeSlot(LocalDateTime bookingStartTime,
	                             LocalDateTime bookingEndTime,
	                             SalonDto salonDto) {
		
		LocalDateTime openingTime = salonDto.getOpeningTime().atDate(bookingStartTime.toLocalDate());
		LocalDateTime closingTime = salonDto.getClosingTime().atDate(bookingStartTime.toLocalDate());
		
		List<Booking> existingBookings = getBookingBySalon(salonDto.getSalonId());
		
		// Rule 1: Same day only
		if (!bookingStartTime.toLocalDate().equals(bookingEndTime.toLocalDate())) {
			throw new RuntimeException("Booking cannot span multiple days");
		}
		
		//Rule 2: Within working hours
		if (bookingStartTime.isBefore(openingTime) || bookingEndTime.isAfter(closingTime)) {
			throw new RuntimeException("Outside working hours");
		}
		
		for (Booking existingBooking : existingBookings) {
			//Rule 3: Overlapping
			if ((bookingStartTime.isBefore(existingBooking.getEndTime()))
					&& (bookingEndTime.isAfter(existingBooking.getStartTime()))) {
				throw new RuntimeException("choice another slot");
			}
		}
	}
	
	
	@Override
	public Booking cancelPartialServices(Long bookingId, Set<Long> cancelServiceIds, UserDto userDto) {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->  new RuntimeException("booking not found"));

		if (!booking.getCustomerId().equals(userDto.getId())) {
			throw new RuntimeException("You are not allowed to modify this booking");
		}

		// Only PENDING or CONFIRMED bookings can be partially cancelled
		if (booking.getStatus() == BookingStatus.CANCEL) {
			throw new RuntimeException("Cannot modify an already cancelled booking");
		}

		// validate service ids belong to this booking
		if (!booking.getServiceIds().containsAll(cancelServiceIds)) {
			throw new RuntimeException("Some service IDs don't belong to this booking");
		}


		Set<Long> currentServiceIds = booking.getServiceIds();

		Set<Long> remainingServiceIds = currentServiceIds
										.stream()
										.filter(currentServiceId -> !cancelServiceIds.contains(currentServiceId))
										.collect(Collectors.toSet());

		//when all service being cancelled
		if (remainingServiceIds.isEmpty()) {
			booking.setStatus(BookingStatus.CANCEL);
			booking.setServiceIds(Set.of());
			booking.setTotalPrice(0.0);
			booking.setEndTime(booking.getStartTime());
			return bookingRepository.save(booking);
		}

		Set<ServiceOfferingDto> remainingServices =	serviceOffering.getServicesByIds(remainingServiceIds).getBody();
		Double newTotalPrice = remainingServices.stream()
				.mapToDouble(ServiceOfferingDto::getPrice).sum();

		int newDuration = remainingServices.stream()
				.mapToInt(ServiceOfferingDto::getDuration)
				.sum();

		booking.setServiceIds(remainingServiceIds);
		booking.setTotalPrice(newTotalPrice);
		booking.setEndTime(booking.getStartTime().plusMinutes(newDuration));
		return bookingRepository.save(booking);


	}

	@Override
	public List<Booking> getBookingByCustomer(Long customerId) {
		return bookingRepository.findByCustomerId(customerId);
	}
	
	@Override
	public List<Booking> getBookingBySalon(Long salonId) {
		return bookingRepository.findBySalonId(salonId);
	}
	
	@Override
	public Booking getBookingById(Long id) {
		Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));
		if (booking == null) throw new RuntimeException("Not found");
		return booking;
	}
	
	@Override
	public List<Booking> getBookingByDate(LocalDateTime date, Long salonId) {
		List<Booking> bookings = getBookingBySalon(salonId);
		if (bookings == null) throw new RuntimeException("No booking");
		return bookings.stream()
				.filter(booking -> date.equals(booking.getStartTime())
						|| date.equals(booking.getEndTime()))
				.toList();
	}
	
	@Override
	public SalonReport getSalonReport(Long salonId) {
		
		List<Booking> bookings = getBookingBySalon(salonId);
		Double totalEarning = bookings.stream()
				.mapToDouble(Booking::getTotalPrice)
				.sum();
		Integer totalBooking = bookings.size();
		List<Booking> totalcancelledBooking = bookings.stream()
				.filter(booking -> (BookingStatus.CANCEL).equals(booking.getStatus()))
				.toList();
		
		Integer cancelledBooking = totalcancelledBooking.size();
		Double totalRefund = totalcancelledBooking.stream()
				.mapToDouble(Booking::getTotalPrice)
				.sum();
		
		
		return SalonReport.builder()
				.salonId(salonId)
				.salonName("s")
				.totalBooking(totalBooking)
				.cancelledBooking(cancelledBooking)
				.totalEarning(totalEarning)
				.totalRefund(totalRefund)
				.build();
		
		
	}
	
}
