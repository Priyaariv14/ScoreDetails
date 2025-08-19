package com.cricket.details.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cricket.details.model.ScoreRequest;
import com.cricket.details.model.ScoreResponse;

public interface ScoreService {

    public void addScore(String username, ScoreRequest scoreRequest);

    public List<ScoreResponse> getScores(String username);

}
