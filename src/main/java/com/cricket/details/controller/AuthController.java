package com.cricket.details.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cricket.details.model.AuthRequest;
import com.cricket.details.model.AuthResponse;
import com.cricket.details.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller responsible for user authentication endpoints such as registration
 * and login
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user
     * 
     * @param authRequest the request containing username and password ; must be
     *                    valid
     * @return a ReponseEntity containing a AuthResponse with JWT token on
     *         successful registration
     */

    @Operation(summary = "Register a new user", description = "Creates a new account and returns a JWT token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Request received for register : {} ", authRequest.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(authRequest));
    }

    /**
     * Authenticates an user
     * 
     * @param authRequest the request containing username and password ; must be
     *                    valid
     * @return a ResponseEntity containing a AuthResponse with JWT token on
     *         successful registration
     */
    @Operation(summary = "Login", description = "Authenticates a user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Request received for login {} ", authRequest.username());
        return ResponseEntity.ok().body(authService.login(authRequest));

    }

    @Operation(summary = "Login Message", description = "Returns login message")
    @GetMapping("/message")
    public ResponseEntity<String> getLoginMessage(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Request received for login {} ", authRequest.username());
        return ResponseEntity.ok().body(authService.getLoginMessage(authRequest));

    }
}
