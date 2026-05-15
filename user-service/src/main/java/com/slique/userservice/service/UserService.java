package com.slique.userservice.service;


import com.slique.userservice.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User updateUser(Long id, User userRequest);
    User getUserById(Long id);
    User getUserFromJwtToken(String jwt);
    void deleteUser(Long id);
}
