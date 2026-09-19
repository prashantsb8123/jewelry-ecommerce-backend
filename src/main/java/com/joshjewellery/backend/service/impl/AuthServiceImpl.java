package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.constant.RoleType;
import com.joshjewellery.backend.dto.AuthDTOs;
import com.joshjewellery.backend.entity.Role;
import com.joshjewellery.backend.entity.User;
import com.joshjewellery.backend.exception.BadRequestException;
import com.joshjewellery.backend.repository.RoleRepository;
import com.joshjewellery.backend.repository.UserRepository;
import com.joshjewellery.backend.security.JwtTokenProvider;
import com.joshjewellery.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User already exists with email: " + request.getEmail());
        }

        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_CUSTOMER).build()));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(customerRole)
                .isEnabled(true)
                .isEmailVerified(true)
                .rewardPoints(0)
                .build();

        user = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);

        return AuthDTOs.AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(customerRole.getName().name())
                .build();
    }

    @Override
    public AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return AuthDTOs.AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName().name())
                .build();
    }
}
