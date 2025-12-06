package com.smart.shop.controller;

import com.smart.shop.dto.UserCreateDTO;
import com.smart.shop.dto.UserResponseDTO;
import com.smart.shop.entity.User;
import com.smart.shop.mapper.UserMapper;
import com.smart.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> creerUser(@Valid @RequestBody UserCreateDTO dto) {
        User user = userService.creerUser(dto);
        UserResponseDTO responseDto = userMapper.toDto(user);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserCreateDTO dto) {

        User updatedUser = userService.updateUser(id, dto);
        UserResponseDTO responseDto = userMapper.toDto(updatedUser);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }
}

