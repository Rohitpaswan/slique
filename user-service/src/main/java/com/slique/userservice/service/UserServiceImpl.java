package com.slique.userservice.service;


import com.slique.userservice.model.User;
import com.slique.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User userRequest) {
        
        return userRepository.save(userRequest);
    }

    @Override
    public User updateUser(Long id, User userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        User updatedUser = User.builder()
                .id(user.getId()) // must include ID
                .fullName(userRequest.getFullName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .role(userRequest.getRole())
                .createdAt(user.getCreatedAt()) // keep original
                .updatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
         userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.deleteById(id);
        
    }
}
