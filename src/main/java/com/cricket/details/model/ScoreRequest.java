package com.cricket.details.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScoreRequest(
                @NotNull(message = "Runs cannot be blank") @Min(value = 0, message = "Runs must be >= 0") int runs,
                @NotBlank(message = "Result cannot be blank") String result,
                @NotBlank(message = "Match cannot be blank") String match) {
}
