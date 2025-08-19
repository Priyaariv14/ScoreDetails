package com.cricket.details.mapper;

import com.cricket.details.model.Score;
import com.cricket.details.model.ScoreResponse;

public final class ScoreMapper {
    private ScoreMapper() {
    }

    public static ScoreResponse toDto(Score s) {
        return new ScoreResponse(s.getId(), s.getMatch(), s.getRuns(), s.getResult(), s.getUser().getUsername());
    }

}
