package com.example.bookingservice.service;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.SalonDto;
import com.example.bookingservice.dto.ServiceDto;
import com.example.bookingservice.dto.UserDto;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.BookingStatus;
import com.example.bookingservice.model.SalonReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingService {
	Booking createBooking(BookingRequest booking, UserDto userDto,
	                             SalonDto salonDto, Set<ServiceDto> serviceDtoSet);
	
	Booking updateBooking(Long bookingId, BookingStatus bookingStatus);
	
	List<Booking> getBookingByCustomer(Long customerId);
	List<Booking> getBookingBySalon(Long salonId);
	Booking getBookingById(Long id);
	List<Booking> getBookingByDate(LocalDateTime date, Long salonId);
	SalonReport getSalonReport(Long salonId);
	
	
	
}
