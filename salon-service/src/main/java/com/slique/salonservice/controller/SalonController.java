package com.slique.salonservice.controller;

import com.slique.salonservice.mapper.SalonMapper;
import com.slique.salonservice.model.Salon;
import com.slique.salonservice.payload.dto.SalonDto;
import com.slique.salonservice.payload.dto.UserDto;
import com.slique.salonservice.service.SalonService;
import com.slique.salonservice.service.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * REST Controller for managing Salon-related operations.
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/salons")
public class SalonController {

    private final SalonService salonService;
    private final UserFeignClient userFeignClient;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<SalonDto>> getAllSalon() {
        List<Salon> salons = salonService.getAllSalon();
        List<SalonDto> salonDtos = salons.stream()
                .map(SalonMapper::mapToSalonDto)
                .toList();

        return new ResponseEntity<>(salonDtos, HttpStatus.OK);
    }


    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SalonDto> createSalon(@RequestBody SalonDto salonDto, @RequestHeader("Authorization") String jwt) {

        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        Salon salon = salonService.createSalon(salonDto, userDto);
        SalonDto addSalonDto = SalonMapper.mapToSalonDto(salon);

        return new ResponseEntity<>(addSalonDto, HttpStatus.CREATED);
    }


    @PutMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SalonDto> updateSalon(@RequestBody SalonDto salonDto, Long salonId, @RequestHeader("Authorization") String jwt) {
        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        Salon salon = salonService.update(salonDto, userDto, salonId);
        SalonDto updatedSalon = SalonMapper.mapToSalonDto(salon);

        return new ResponseEntity<>(updatedSalon, HttpStatus.OK);
    }


    @GetMapping(path = "/{salonId}", produces = "application/json")
    public ResponseEntity<SalonDto> getSalonById(@PathVariable Long salonId) {
        Salon salon = salonService.getSalonBYId(salonId);
        return new ResponseEntity<>(SalonMapper.mapToSalonDto(salon), HttpStatus.OK);
    }


    @GetMapping(path = "/owner", produces = "application/json")
    public ResponseEntity<List<SalonDto>> getSalonBYOwnerId(@RequestHeader("Authorization") String jwt) {
        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        if (userDto == null) throw new RuntimeException("User not found");

        List<Salon> salons = salonService.getSalonBYOwnerId(userDto.getId());
        List<SalonDto> salonDtos = salons.stream()
                .map(SalonMapper::mapToSalonDto)
                .toList();

        return new ResponseEntity<>(salonDtos, HttpStatus.OK);
    }


    @GetMapping(path = "/search", produces = "application/json")
    public ResponseEntity<List<SalonDto>> searchSalonByCity(@RequestParam("city") String city) {
        List<Salon> salons = salonService.searchSalonByCity(city);

        List<SalonDto> salonDto = salons.stream()
                .map(SalonMapper::mapToSalonDto)
                .toList();

        return new ResponseEntity<>(salonDto, HttpStatus.OK);
    }


    @DeleteMapping(path = "/delete/{salonId}", produces = "application/json")
    public ResponseEntity<Map<String, String>> deleteSalon(
            @PathVariable Long salonId,
            @RequestHeader("Authorization") String jwt) {

        UserDto userDto = userFeignClient.getUserFromJwtToken(jwt).getBody();
        if (userDto == null) throw new RuntimeException("User not found");

        salonService.deleteSalon(salonId, userDto);

        return new ResponseEntity<>(
                Map.of("message", "Salon deleted successfully"),
                HttpStatus.OK
        );
    }


    @PostMapping("/batch")
    public ResponseEntity<List<SalonDto>> getSalonsByIds(@RequestBody Set<Long> salonIds) {
        List<Salon> salons = salonService.getSalonsByIds(salonIds);
        List<SalonDto> salonDtos = salons.stream().map(SalonMapper::mapToSalonDto).toList();
        return ResponseEntity.ok(salonDtos);
    }
}