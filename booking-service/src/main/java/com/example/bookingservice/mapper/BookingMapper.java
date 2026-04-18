package com.example.bookingservice.mapper;

import com.example.bookingservice.dto.BookingDto;
import com.example.bookingservice.model.Booking;

public class BookingMapper {
	private BookingMapper(){
	
	}
	public static BookingDto mapToEntity(Booking booking){
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
				.build();
	}
}
