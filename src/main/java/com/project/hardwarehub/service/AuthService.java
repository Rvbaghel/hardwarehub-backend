package com.project.hardwarehub.service;

import com.project.hardwarehub.dto.LoginRequestDTO;
import com.project.hardwarehub.dto.SignupRequestDTO;
import com.project.hardwarehub.entity.User;
import com.project.hardwarehub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public String signup(SignupRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(), // Note: In production we will hash this with BCrypt
                request.getBio(),
                request.getAge(),
                request.getMobileNumber()
        );

        userRepository.save(user);
        return "User registered successfully!";
    }

    public User login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password!"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password!");
        }

        return user;
    }
}
