package com.cricket.details.service;

import com.cricket.details.exception.InvalidCredentialsException;
import com.cricket.details.exception.UserAlreadyExistsException;
import com.cricket.details.exception.UserNotFoundException;
import com.cricket.details.model.AuthRequest;
import com.cricket.details.model.AuthResponse;
import com.cricket.details.model.User;
import com.cricket.details.repository.UserRepository;
import com.cricket.details.service.impl.AuthServiceImpl;
import com.cricket.details.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthServiceImpl authService;

    AuthServiceImplTest(@Mock UserRepository userRepository,
            @Mock PasswordEncoder passwordEncoder,
            @Mock JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("register() - should save user and return token when username is available")
    void register_ShouldSaveUser_WhenUsernameAvailable() {
        // Arrange
        AuthRequest request = new AuthRequest("newuser", "password123");
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(jwtUtil.generateToken("newuser")).thenReturn("mock-jwt");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response.token()).isEqualTo("mock-jwt");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() - should throw UserAlreadyExistsException when username taken")
    void register_ShouldThrow_WhenUsernameTaken() {
        // Arrange
        AuthRequest request = new AuthRequest("existing", "pass");
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login() - should return token when credentials are valid")
    void login_ShouldReturnToken_WhenCredentialsValid() {
        // Arrange
        AuthRequest request = new AuthRequest("validUser", "rawPass");
        User user = new User(1L, "validUser", "encodedPass", "ROLE_USER", true);

        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken("validUser")).thenReturn("login-jwt");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response.token()).isEqualTo("login-jwt");
    }

    @Test
    @DisplayName("login() - should throw UserNotFoundException when username not found")
    void login_ShouldThrow_WhenUserNotFound() {
        // Arrange
        AuthRequest request = new AuthRequest("ghost", "any");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("login() - should throw InvalidCredentialsException when password mismatch")
    void login_ShouldThrow_WhenPasswordInvalid() {
        // Arrange
        AuthRequest request = new AuthRequest("user", "wrongPass");
        User user = new User(1L, "user", "encodedPass", "ROLE_USER", true);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid Credentials");
    }
}