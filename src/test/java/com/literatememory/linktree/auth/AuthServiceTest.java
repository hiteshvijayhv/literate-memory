package com.literatememory.linktree.auth;

import com.literatememory.linktree.common.ApiException;
import com.literatememory.linktree.config.JwtService;
import com.literatememory.linktree.user.User;
import com.literatememory.linktree.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(ApiException.class, () -> authService.register(new AuthDtos.RegisterRequest("test@example.com", "password123")));
    }

    @Test
    void loginReturnsTokenOnValidCredentials() {
        User u = new User();
        u.setId("u1");
        u.setEmail("test@example.com");
        u.setPasswordHash("hash");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);
        when(jwtService.generateToken("u1", "test@example.com")).thenReturn("jwt");

        AuthDtos.AuthResponse response = authService.login(new AuthDtos.LoginRequest("test@example.com", "password123"));
        assertEquals("jwt", response.accessToken());
        assertEquals("u1", response.userId());
    }
}
