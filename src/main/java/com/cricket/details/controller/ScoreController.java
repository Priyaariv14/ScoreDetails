package com.cricket.details.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cricket.details.model.ScoreRequest;
import com.cricket.details.model.ScoreResponse;
import com.cricket.details.service.ScoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller responsible for adding score and fetching list of scores of a user
 */
@RestController
@RequestMapping("/scores")
public class ScoreController {

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    /**
     * Add score for currently authenticated user
     * 
     * @param scoreRequest the score details to add;must be valid
     * @return HTTP 201 created status with empty body on success
     */
    @Operation(summary = "Adding score", description = "API endpoint to add  new score for the user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/addScore")
    public ResponseEntity<Void> addScore(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ScoreRequest scoreRequest) {
        log.info("Request to add score {}", scoreRequest);
        scoreService.addScore(userDetails.getUsername(), scoreRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * Retrieves the list of scores for the currently authenticated user.
     *
     * @param scoreRequest filters or pagination info (if any); must be valid
     * @return list of ScoreResponse objects representing the user's scores
     */
    @Operation(summary = "Get Scores", description = "API to get the scores of the user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/getScores")
    public ResponseEntity<List<ScoreResponse>> getScores(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to get the scores of the user {}", userDetails.getUsername());
        return ResponseEntity.ok(scoreService.getScores(userDetails.getUsername()));
    }

}
