package com.example.bookingservice.controller;

import com.example.bookingservice.dto.*;
import com.example.bookingservice.mapper.BookingMapper;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.BookingStatus;
import com.example.bookingservice.model.SalonReport;
import com.example.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings/")
@RequiredArgsConstructor
public class BookingController {
	private final BookingService bookingService;
	
	@PostMapping
	public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest booking, @RequestParam Long salonId) {
		UserDto userDto = new UserDto();
		userDto.setId(1L);
		SalonDto salonDto = new SalonDto();
		salonDto.setSalonId(salonId);
		
		Set<ServiceDto> serviceDtoSet = new HashSet<>();
		ServiceDto service = new ServiceDto();
		
		service.setId(1L);
		service.setName("Hair Cut");
		service.setDescription("Basic hair cutting service");
		service.setSalonId(101L);
		service.setCategoryId(10L);
		service.setImage("haircut.jpg");
		service.setDuration(30);
		service.setPrice(199.99);
		serviceDtoSet.add(service);
		
		Booking savedBooking = bookingService.createBooking(booking, userDto, salonDto, serviceDtoSet);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
		
	}
	
	
	@GetMapping("/customer")
	public ResponseEntity<Set<BookingDto>> getBookingByCustomer() {
		UserDto userDto = new UserDto();
		userDto.setId(1L);
		List<Booking> bookingList = bookingService.getBookingByCustomer(1L);
		Set<BookingDto> bookingDtos = getBookingDtos(bookingList);
		return ResponseEntity.status(HttpStatus.OK).body(bookingDtos);
	}
	
	
	@GetMapping("/salon")
	public ResponseEntity<Set<BookingDto>> getBookingBySalon() {
		UserDto userDto = new UserDto();
		userDto.setId(1L);
		List<Booking> bookingList = bookingService.getBookingBySalon(1L);
		Set<BookingDto> bookingDtos = getBookingDtos(bookingList);
		return ResponseEntity.status(HttpStatus.OK).body(bookingDtos);
	}
	
	
	
	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
		return ResponseEntity.status(HttpStatus.OK).body(BookingMapper.mapToEntity(bookingService.getBookingById(bookingId)));
	}
	
	
	@GetMapping("/{bookingId}/status")
	public ResponseEntity<BookingDto> updateBooking(@PathVariable Long bookingId, @RequestParam BookingStatus bookingStatus) {
		Booking booking = bookingService.updateBooking(bookingId, bookingStatus);
		return ResponseEntity.status(HttpStatus.OK).body(BookingMapper.mapToEntity(booking));
	}
	
	
	@GetMapping("/slots/salon/{salonId}/date/{date}")
	public ResponseEntity<List<BookingSlotDto>> getBookingSlot(@PathVariable Long salonId, @RequestParam LocalDateTime date) {
		List<Booking> bookings = bookingService.getBookingByDate(date, salonId);
		List<BookingSlotDto> slotsDto = bookings.stream().map(booking -> {
			BookingSlotDto bookingSlotDto = new BookingSlotDto();
			bookingSlotDto.setStartDate(booking.getStartTime());
			bookingSlotDto.setEndDate(booking.getEndTime());
			return bookingSlotDto;
		}).toList();
		
		return ResponseEntity.status(HttpStatus.OK).body(slotsDto);
	}
	
	
	@GetMapping("/report")
	public ResponseEntity<SalonReport> getSalonReport() {
		SalonReport salonReport = bookingService.getSalonReport(1L);
		return ResponseEntity.status(HttpStatus.OK).body(salonReport);
	}
	
	
	private Set<BookingDto> getBookingDtos(List<Booking> bookingList) {
		return bookingList.stream().map(BookingMapper::mapToEntity).collect(Collectors.toSet());
	}
}
