package com.example.bookingservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SalonReport {
	private Long salonId;
	private String salonName;
	private Double totalEarning;
	private Integer totalBooking;
	private Integer cancelledBooking;
	private Double totalRefund;
}
