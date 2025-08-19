package com.cricket.details.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be atleast 6 characters") String password) {

}
