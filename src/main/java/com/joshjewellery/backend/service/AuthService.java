package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.AuthDTOs;

public interface AuthService {
    AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request);
    AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request);
}
