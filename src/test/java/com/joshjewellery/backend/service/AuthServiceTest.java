package com.joshjewellery.backend.service;

import com.joshjewellery.backend.constant.RoleType;
import com.joshjewellery.backend.dto.AuthDTOs;
import com.joshjewellery.backend.entity.Role;
import com.joshjewellery.backend.entity.User;
import com.joshjewellery.backend.exception.BadRequestException;
import com.joshjewellery.backend.repository.RoleRepository;
import com.joshjewellery.backend.repository.UserRepository;
import com.joshjewellery.backend.security.JwtTokenProvider;
import com.joshjewellery.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role customerRole;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder().id(1).name(RoleType.ROLE_CUSTOMER).build();
    }

    @Test
    void testRegisterNewUserSuccess() {
        AuthDTOs.RegisterRequest request = new AuthDTOs.RegisterRequest("John Doe", "john@example.com", "password123", "+919876543210");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleType.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("john@example.com")
                .role(customerRole)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");

        AuthDTOs.AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterExistingUserThrowsBadRequest() {
        AuthDTOs.RegisterRequest request = new AuthDTOs.RegisterRequest("John Doe", "existing@example.com", "password123", "+919876543210");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
