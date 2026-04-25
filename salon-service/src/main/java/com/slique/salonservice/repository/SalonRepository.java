package com.slique.salonservice.repository;

import com.slique.salonservice.model.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {
	
	List<Salon> findByownerId(Long ownerId);
	
	@Query("SELECT s FROM Salon s WHERE LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Salon> searchSalonByCity(@Param("keyword") String keyword);
	
	
	
}
