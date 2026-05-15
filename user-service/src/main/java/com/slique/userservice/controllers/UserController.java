package com.slique.userservice.controllers;

import com.slique.userservice.mapper.UserMapper;
import com.slique.userservice.model.User;
import com.slique.userservice.payload.dto.UserDto;
import com.slique.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserFromJwtToken(
            @RequestHeader("Authorization") String jwt) {

        User user = userService.getUserFromJwtToken(jwt);
        UserDto userDTO = UserMapper.mapToUserDto(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        UserDto userDTO = UserMapper.mapToUserDto(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }


//    @PutMapping("/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userRequest) {
//        return ResponseEntity.ok(userService.updateUser(id, userRequest));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }

}
