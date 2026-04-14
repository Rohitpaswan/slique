package com.slique.serviceoffering.repository;

import com.slique.serviceoffering.model.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceOffering, Long> {
	Set<ServiceOffering> findBySalonId(Long salonId);
}
