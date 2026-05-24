package com.example.bookingservice.service;

import com.example.bookingservice.dto.BookingDto;
import com.example.bookingservice.dto.SalonDto;
import com.example.bookingservice.dto.ServiceOfferingDto;
import com.example.bookingservice.dto.UserDto;
import com.example.bookingservice.mapper.BookingMapper;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.service.client.SalonFeignClient;
import com.example.bookingservice.service.client.ServiceOfferingFeignClient;
import com.example.bookingservice.service.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingAggregationService {

    private final UserFeignClient userFeignClient;
    private final ServiceOfferingFeignClient serviceOfferingFeignClient;
    private final SalonFeignClient salonFeignClient;

    public Set<BookingDto> buildBookingDtos(List<Booking> bookingList) {

        //extract the all of the list
        Set<Long> customerIds = bookingList.stream().map(Booking::getCustomerId).collect(Collectors.toSet());
        Set<Long> salonIds = bookingList.stream().map(Booking::getSalonId).collect(Collectors.toSet());
        Set<Long> serviceIds = bookingList.stream().flatMap(booking -> booking.getServiceIds().stream()).collect(Collectors.toSet());

        List<UserDto> users = userFeignClient.getUsersByIds(customerIds).getBody();

        List<SalonDto> salonDtos = salonFeignClient.getSalonsByIds(salonIds).getBody();

        Set<ServiceOfferingDto> services = serviceOfferingFeignClient.getServicesByIds(serviceIds).getBody();

        //covert list to map
        Map<Long, UserDto> userMap = users.stream().collect(Collectors.toMap(UserDto::getId, u -> u));

        Map<Long, SalonDto> salonMap = salonDtos.stream().collect(Collectors.toMap(SalonDto::getSalonId, s -> s));

        Map<Long, ServiceOfferingDto> serviceMap = services.stream().collect(Collectors.toMap(ServiceOfferingDto::getId, s -> s));
        return bookingList.stream().map(booking -> {
            UserDto customer = userMap.get(booking.getCustomerId());
            SalonDto salonDto = salonMap.get(booking.getSalonId());
            Set<ServiceOfferingDto> bookingServices = booking.getServiceIds().stream().map(serviceMap::get).collect(Collectors.toSet());

            return BookingMapper.toDto(booking, customer, salonDto, bookingServices);

        }).collect(Collectors.toSet());


    }


    public BookingDto buildBookingDto(Booking booking) {

        UserDto customer = userFeignClient.getUserById(booking.getCustomerId()).getBody();

        SalonDto salonDto = salonFeignClient.getSalonById(booking.getSalonId()).getBody();

        Set<ServiceOfferingDto> bookingServices = serviceOfferingFeignClient.getServicesByIds(booking.getServiceIds()).getBody();

        return BookingMapper.toDto(booking, customer, salonDto, bookingServices);
    }


}
