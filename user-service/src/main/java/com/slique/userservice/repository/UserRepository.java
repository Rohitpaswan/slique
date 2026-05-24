package com.slique.userservice.repository;

import com.slique.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository

public interface UserRepository extends JpaRepository<User, Long > {
    User findByEmail(String emil);
    List<User> findByIdIn(Set<Long> ids);
}
