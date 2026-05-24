package com.slique.userservice.service;


import com.slique.userservice.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAllUsers();
    User updateUser(Long id, User userRequest);
    User getUserById(Long id);
    User getUserFromJwtToken(String jwt);
    void deleteUser(Long id);

    List<User> getUsersByIds(Set<Long> ids);


}
