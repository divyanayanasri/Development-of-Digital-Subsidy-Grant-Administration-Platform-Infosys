package com.training.module4_analytics.controller;

import com.training.common.entity.User;
import com.training.module1_masterdata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.training.common.service.AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(userRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        auditLogService.log("CREATE_USER", "User", saved.getId(), "Created user: " + saved.getEmail() + " with role: " + saved.getRole());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
