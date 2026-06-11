package com.example.bookingservice.controller;

import com.example.bookingservice.dto.*;
import com.example.bookingservice.exception.ResourceNotFoundException;
import com.example.bookingservice.exception.UnauthorizedBookingAccessException;
import com.example.bookingservice.request.BookingRequest;
import com.example.bookingservice.request.BookingUpdateRequest;
import com.example.bookingservice.service.BookingAggregationService;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.PaymentMethod;
import com.example.bookingservice.model.SalonReport;
import com.example.bookingservice.service.BookingService;
import com.example.bookingservice.service.client.PaymentFeignClient;
import com.example.bookingservice.service.client.SalonFeignClient;
import com.example.bookingservice.service.client.ServiceOfferingFeignClient;
import com.example.bookingservice.service.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/bookings/")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserFeignClient userFeignClient;
    private final ServiceOfferingFeignClient serviceOfferingFeignClient;
    private final SalonFeignClient salonFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final BookingAggregationService bookingAggregationService;

    @PostMapping("/create")
    public ResponseEntity<PaymentLinkResponse> createBooking(@RequestBody BookingRequest booking,
                                                             @RequestHeader("Authorization") String jwt,
                                                             @RequestParam Long salonId,
                                                             @RequestParam PaymentMethod paymentMethod,
                                                             @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        SalonDto salonDto = salonFeignClient.getSalonById(salonId).getBody();
        Set<ServiceOfferingDto> services = serviceOfferingFeignClient.getServicesByIds(booking.getServiceIds()).getBody();
        Booking savedBooking = bookingService.createBooking(booking, userDto, salonDto, services);
        PaymentLinkResponse paymentLinkResponse = paymentFeignClient.createPaymentLink(bookingAggregationService.buildBookingDto(savedBooking), paymentMethod, jwt, idempotencyKey).getBody();
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentLinkResponse);

    }


    @GetMapping("/customer")
    public ResponseEntity<Set<BookingDto>> getBookingByCustomer(@RequestHeader("Authorization") String jwt) {
        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        if (userDto == null || userDto.getId() == null) throw new ResourceNotFoundException("User Not found");
        List<Booking> bookingList = bookingService.getBookingByCustomer(userDto.getId());
        Set<BookingDto> bookingDtos = bookingAggregationService.buildBookingDtos(bookingList);
        return ResponseEntity.status(HttpStatus.OK).body(bookingDtos);
    }

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<Set<BookingDto>> getBookingBySalon(@RequestHeader("Authorization") String jwt, @PathVariable Long salonId) {
        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<SalonDto> ownedSalons = salonFeignClient.getSalonBYOwnerId(jwt).getBody();
        if (ownedSalons == null || ownedSalons.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean isOwnerSalon = ownedSalons.stream().anyMatch(salon -> salon.getSalonId().equals(salonId));

        if (!isOwnerSalon) {
            log.info("Unauthorized Access");
            throw new UnauthorizedBookingAccessException("Unauthorized Access: "  );
        }

        List<Booking> bookingList = bookingService.getBookingBySalon(salonId);
        Set<BookingDto> bookingDtos = bookingAggregationService.buildBookingDtos(bookingList);
        return ResponseEntity.status(HttpStatus.OK).body(bookingDtos);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingAggregationService.buildBookingDto(bookingService.getBookingById(bookingId)));
    }

    @PutMapping("/{bookingId}/cancel-services")
    public ResponseEntity<BookingDto> updateBookingStatus(@PathVariable Long bookingId, @RequestBody BookingUpdateRequest request, @RequestHeader("Authorization") String jwt) {

        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        if (userDto == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Booking updatedBooking = bookingService.cancelPartialServices(bookingId, request.getCancelServiceIds(), userDto);
        return ResponseEntity.ok(bookingAggregationService.buildBookingDto(updatedBooking));
    }


    @GetMapping("/slots/salon/{salonId}/date/{date}")
    public ResponseEntity<List<BookingSlotDto>> getBookingSlot(@PathVariable Long salonId, @PathVariable LocalDateTime date) {
        List<Booking> bookings = bookingService.getBookingByDate(date, salonId);

        List<BookingSlotDto> slotsDto = bookings.stream().map(booking -> {
            BookingSlotDto bookingSlotDto = new BookingSlotDto();
            bookingSlotDto.setStartDate(booking.getStartTime());
            bookingSlotDto.setEndDate(booking.getEndTime());
            return bookingSlotDto;
        }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(slotsDto);
    }


    @GetMapping("/report/{salonId}")
    public ResponseEntity<SalonReport> getSalonReport(@RequestHeader("Authorization") String jwt, @PathVariable Long salonId) {
        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<SalonDto> ownedSalons = salonFeignClient.getSalonBYOwnerId(jwt).getBody();
        if (ownedSalons == null || ownedSalons.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean isOwnerSalon = ownedSalons.stream().anyMatch(salon -> salon.getSalonId().equals(salonId));

        if (!isOwnerSalon) {
            log.info("Unauthorized Access ");
            throw new UnauthorizedBookingAccessException("Unauthorized Access: "  );
        }
        SalonReport salonReport = bookingService.getSalonReport(salonId);
        return ResponseEntity.status(HttpStatus.OK).body(salonReport);
    }



}
