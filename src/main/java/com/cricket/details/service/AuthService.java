package com.cricket.details.service;

import com.cricket.details.model.AuthRequest;
import com.cricket.details.model.AuthResponse;

import jakarta.validation.Valid;

public interface AuthService {
    public AuthResponse register(@Valid AuthRequest authRequest);

    public AuthResponse login(@Valid AuthRequest authRequest);

    public String getLoginMessage(@Valid AuthRequest authRequest);

}
