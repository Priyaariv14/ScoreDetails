package com.cricket.details.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import com.cricket.details.exception.InvalidCredentialsException;
import com.cricket.details.exception.UserAlreadyExistsException;
import com.cricket.details.exception.UserNotFoundException;
import com.cricket.details.model.AuthRequest;
import com.cricket.details.model.AuthResponse;
import com.cricket.details.model.User;
import com.cricket.details.repository.UserRepository;
import com.cricket.details.service.AuthService;
import com.cricket.details.util.JwtUtil;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;

/**
 * Implementation of {@link AuthService} providing user registration and
 * authentication.
 * <p>
 * Handles creating new users with encoded passwords and authenticating existing
 * users,
 * issuing JWT tokens upon successful operations.
 * </p>
 */

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    /**
     * Constructs an AuthServiceImpl with the required dependencies.
     *
     * @param userRepository  repository for user persistence
     * @param passwordEncoder encoder for secure password storage
     * @param jwtUtil         utility for generating JWT tokens
     */

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    /**
     * Registers a new user with the provided credentials.
     * <p>
     * Checks if the username already exists and throws an exception if taken.
     * Passwords are securely encoded before saving.
     * On success, returns a JWT token for the new user.
     * </p>
     *
     * @param authRequest contains the username and password for registration; must
     *                    be valid
     * @return an AuthResponse containing the JWT token
     * @throws UserAlreadyExistsException if the username is already taken
     */

    @Override
    @Transactional
    public AuthResponse register(AuthRequest authRequest) {
        log.info("registering user {}", authRequest.username());
        if (userRepository.findByUsername(authRequest.username()).isPresent()) {
            throw new UserAlreadyExistsException("Username already taken");
        }

        User user = new User(null, authRequest.username(), passwordEncoder.encode(authRequest.password()),
                "ROLE_USER", true);

        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(authRequest.username()));
    }

    /**
     * Authenticates a user with the provided credentials.
     * <p>
     * Throws an exception if the user is not found or the password is invalid.
     * On success, returns a JWT token for the authenticated user.
     * </p>
     *
     * @param authRequest contains the username and password for login; must be
     *                    valid
     * @return an AuthResponse containing the JWT token
     * @throws InvalidCredentialsException if the password does not match
     * @throws RuntimeException            if the user is not found
     */

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        log.info("Login attempt for user {}", authRequest.username());
        User user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        return new AuthResponse(jwtUtil.generateToken(authRequest.username()));

    }

    @Override
    @Retry(name = "userServiceRetry", fallbackMethod = "fallbackGetUser")
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "fallbackGetUser")
    public String getLoginMessage(@Valid AuthRequest authRequest) {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8082/getNotification",
                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to fetch from notification service");
        }
        return response.getBody();
    }

    // Fallback method handles both Retry and Circuit Breaker failures
    public String fallbackGetUser(String userId, Throwable t) {
        log.error("Failed to fetch user {}. Reason: {}", userId, t.getMessage());
        // Optionally fetch last known value from cache/DB
        return "default-user";
    }

}
