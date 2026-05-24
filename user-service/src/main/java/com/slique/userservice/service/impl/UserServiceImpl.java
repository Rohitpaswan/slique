package com.slique.userservice.service.impl;


import com.slique.userservice.model.User;
import com.slique.userservice.payload.dto.KeycloakUserDto;
import com.slique.userservice.repository.UserRepository;
import com.slique.userservice.service.KeycloakService;
import com.slique.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        User user=userRepository.findByEmail(email);
        if(user==null){
            throw new RuntimeException("User not found with email: "+email);
        }
        return user;
    }

    public User getUserById(Long id)  {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User updateUser(Long id, User userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        User updatedUser = User.builder()
                .id(user.getId()) // must include ID
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .userRole(userRequest.getUserRole())
                .createdAt(user.getCreatedAt()) // keep original
                .updatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(updatedUser);
    }

    @Override
    public User getUserFromJwtToken(String jwt) {
        KeycloakUserDto userinfo = keycloakService.fetchUserProfileByJwt(jwt);
        return userRepository.findByEmail(userinfo.getEmail());

    }

    @Override
    public void deleteUser(Long id) {
         userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.deleteById(id);
        
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) { return List.of(); }
        return userRepository.findByIdIn(ids);
    }
}
