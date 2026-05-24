package com.example.bookingservice.mapper;

import com.example.bookingservice.dto.BookingDto;
import com.example.bookingservice.dto.SalonDto;
import com.example.bookingservice.dto.ServiceOfferingDto;
import com.example.bookingservice.dto.UserDto;
import com.example.bookingservice.model.Booking;

import java.util.Set;

public class BookingMapper {
	private BookingMapper(){
	
	}
	public static BookingDto toDto(Booking booking, UserDto customer, SalonDto salonDto, Set<ServiceOfferingDto> bookingServices){
		if(booking == null) return null;
		
		return BookingDto.builder()
				.id(booking.getId())
				.customerId(booking.getCustomerId())
				.status(booking.getStatus())
				.salonId(booking.getSalonId())
				.serviceIds(booking.getServiceIds())
				.startTime(booking.getStartTime())
				.endTime(booking.getEndTime())
				.totalPrice(booking.getTotalPrice())
				.services(bookingServices)
				.customer(customer)
				.salon(salonDto)
				.build();
	}
}
