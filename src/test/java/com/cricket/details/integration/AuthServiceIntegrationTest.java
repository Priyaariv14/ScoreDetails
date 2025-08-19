package com.cricket.details.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cricket.details.model.AuthRequest;
import com.cricket.details.model.AuthResponse;
import com.cricket.details.model.User;
import com.cricket.details.repository.UserRepository;
import com.cricket.details.service.impl.AuthServiceImpl;
import com.cricket.details.util.JwtUtil;

@DataJpaTest
@Import({ AuthServiceImpl.class, JwtUtil.class, PasswordEncoder.class })
public class AuthServiceIntegrationTest {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthServiceImpl authService;

    @Autowired
    AuthServiceIntegrationTest(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authService = new AuthServiceImpl(userRepository, passwordEncoder,
                jwtUtil);
    }

    @Test
    void register_ShouldPersistUser_AndReturnValidToken() {
        AuthRequest request = new AuthRequest("integrationUser", "mypassword");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isNotBlank();
        assertThat(userRepository.findByUsername("integrationUser")).isPresent();

        User savedUser = userRepository.findByUsername("integrationUser").get();
        assertThat(passwordEncoder.matches("mypassword",
                savedUser.getPassword())).isTrue();
    }
}
