package com.cricket.details.service.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.cricket.details.exception.UserNotFoundException;
import com.cricket.details.mapper.ScoreMapper;
import com.cricket.details.model.OutboxEvent;
import com.cricket.details.model.Score;
import com.cricket.details.model.ScoreRequest;
import com.cricket.details.model.ScoreResponse;
import com.cricket.details.model.User;
import com.cricket.details.repository.OutboxRepository;
import com.cricket.details.repository.ScoreRepository;
import com.cricket.details.repository.UserRepository;
import com.cricket.details.service.ScoreService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;

/**
 * Implementation of {@Link ScoreService }to handle business logic
 * related to user scores.
 * <p>
 * This service manages adding new scores and retrieving scores for
 * the currently authenticated user, interacting with the
 * {@link UserRepository} and {@link ScoreRepository}.
 * </p>
 */
@Service
@Validated
public class ScoreServiceImpl implements ScoreService {

    private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);

    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new ScoreServiceImpl with the required repositories.
     *
     * @param userRepository  repository to manage User entities
     * @param scoreRepository repository to manage Score entities
     */

    public ScoreServiceImpl(UserRepository userRepository, ScoreRepository scoreRepository,
            OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Adds a new score record associated with the currently authenticated user.
     * <p>
     * This method retrieves the username from the security context, verifies
     * that the user exists, then creates and persists a new Score entity.
     * </p>
     *
     * @param scoreRequest the score details to add; must be valid
     * @throws UserNotFoundException if the authenticated user is not found
     */

    @Transactional
    @Override
    @RateLimiter(name = "myServiceLimiter")
    public void addScore(String username, ScoreRequest scoreRequest) {
        // String username =
        // SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found: " +
                        username));
        Score score = new Score(null, scoreRequest.runs(), scoreRequest.result(), scoreRequest.match(), user);
        Score savedScore = scoreRepository.save(score);

        // save to outbox event also now

        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("score", savedScore.getRuns());
        payload.put("match", savedScore.getMatch());
        payload.put("result", savedScore.getResult());
        payload.put("user", savedScore.getUser().getUsername());

        try {
            String payloadString = objectMapper.writeValueAsString(payload);
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setEventId(UUID.randomUUID().toString());
            outboxEvent.setEventType("SCORE CREATED");
            outboxEvent.setAggregateId(String.valueOf(savedScore.getId()));
            outboxEvent.setAggregateType("SCORE");
            outboxEvent.setPayload(payloadString);
            outboxEvent.setStatus("PENDING");
            outboxEvent.setRetryCount(0);
            outboxEvent.setCreatedAt(Instant.now());
            outboxRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to serialize outbox payload , rolling back", e);
            throw new RuntimeException("Failed to create outbox payload", e);
        }
    }

    /**
     * Retrieves a list of scores for the currently authenticated user.
     * <p>
     * This method fetches the username from the security context, verifies
     * user existence, and returns the scores mapped to {@link ScoreResponse} DTOs.
     * </p>
     *
     * @param scoreRequest filters or parameters related to the request (not
     *                     currently used)
     * @return a list of ScoreResponse DTOs representing the user's scores
     * @throws UserNotFoundException if the authenticated user is not found
     */

    @Override
    public List<ScoreResponse> getScores(String username) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found:  " + username));
        return scoreRepository.findByUser_Username(username).stream()
                .map(ScoreMapper::toDto)
                .toList();

    }

}
