package com.slique.userservice.service;


import com.slique.userservice.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User createUser(User userRequest);
    User updateUser(Long id, User userRequest);
    void deleteUser(Long id);
}
