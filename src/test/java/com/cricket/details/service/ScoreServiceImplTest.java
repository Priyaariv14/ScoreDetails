package com.cricket.details.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cricket.details.model.Score;
import com.cricket.details.model.ScoreRequest;
import com.cricket.details.model.User;
import com.cricket.details.repository.OutboxRepository;
import com.cricket.details.repository.ScoreRepository;
import com.cricket.details.repository.UserRepository;
import com.cricket.details.service.impl.ScoreServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class ScoreServiceImplTest {

    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ScoreServiceImpl scoreServiceImpl;

    ScoreServiceImplTest(@Mock UserRepository userRepository,
            @Mock ScoreRepository scoreRepository,
            @Mock OutboxRepository outboxRepository,
            @Mock ObjectMapper objectMapper) {
        this.scoreServiceImpl = new ScoreServiceImpl(userRepository, scoreRepository, outboxRepository, objectMapper);
    }

    @Test
    void test_AddScore() throws Exception {
        String userName = "test";
        User user = new User(1L, "test", "Test@123", "ROLE_USER", true);
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));

        ScoreRequest scoreRequest = new ScoreRequest(8, "Win", "G2G");
        scoreServiceImpl.addScore("test", scoreRequest);

        verify(scoreRepository).save(any(Score.class));
    }
}
