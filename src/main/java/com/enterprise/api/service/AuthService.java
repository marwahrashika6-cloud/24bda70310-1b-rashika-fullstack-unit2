package com.enterprise.api.service;

import com.enterprise.api.dto.request.LoginRequest;
import com.enterprise.api.dto.request.RefreshTokenRequest;
import com.enterprise.api.dto.request.RegisterRequest;
import com.enterprise.api.dto.response.AuthResponse;
import com.enterprise.api.dto.response.UserProfileResponse;
import com.enterprise.api.entity.Role;
import com.enterprise.api.entity.User;
import com.enterprise.api.exception.BadRequestException;
import com.enterprise.api.exception.DuplicateResourceException;
import com.enterprise.api.exception.TokenRefreshException;
import com.enterprise.api.repository.UserRepository;
import com.enterprise.api.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        Set<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.ROLE_USER);
        }

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                roles
        );

        User savedUser = userRepository.save(user);

        return new UserProfileResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                savedUser.isEnabled(),
                savedUser.getCreatedAt()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new AuthResponse(
                accessToken,
                refreshToken,
                tokenProvider.getAccessTokenExpirationMs(),
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                roles
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenRefreshException("Refresh token is invalid or has expired");
        }

        String email = tokenProvider.getUsernameFromJwt(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenRefreshException("User not found associated with refresh token"));

        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                tokenProvider.getAccessTokenExpirationMs(),
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                roles
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new BadRequestException("No authenticated user session found");
        }

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
