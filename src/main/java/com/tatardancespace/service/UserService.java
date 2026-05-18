package com.tatardancespace.service;

import com.tatardancespace.dto.request.RegisterRequest;
import com.tatardancespace.entity.Role;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.DuplicateEmailException;
import com.tatardancespace.exception.DuplicateUsernameException;
import com.tatardancespace.exception.PasswordMismatchException;
import com.tatardancespace.exception.UserNotFoundException;
import com.tatardancespace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.error("Passwords don't match");
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already exists");
            throw new DuplicateEmailException(request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username already exists");
            throw new DuplicateUsernameException(request.getUsername());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setRole(Role.USER);
        user.setAvatarEmoji("🕺");  // эмодзи по умолчанию
        log.info("Registering user " + user.getUsername());
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        log.info("Finding user by email " + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Transactional
    public User updateUsername(Long userId, String newUsername) {
        User user = findById(userId);
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new DuplicateUsernameException(newUsername);
        }
        user.setUsername(newUsername);
        return userRepository.save(user);
    }
    @Transactional
    public User findOrCreateOAuthUser(String email, String username) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Creating new user via OAuth: {}", email);

                    User user = new User();
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRole(Role.USER);
                    user.setAvatarEmoji("💃");

                    return userRepository.save(user);
                });
    }

    @Transactional
    public void updateAvatarEmoji(Long userId, String avatarEmoji) {
        User user = findById(userId);
        user.setAvatarEmoji(avatarEmoji);
        userRepository.save(user);
    }
}