package org.example.enumtalentapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.enumtalentapi.entity.User;
import org.example.enumtalentapi.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class findAllUsers {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



}