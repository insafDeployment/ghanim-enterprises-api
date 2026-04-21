package lk.ghanim.api.service;

import lk.ghanim.api.entity.User;
import lk.ghanim.api.exception.DuplicateResourceException;
import lk.ghanim.api.repository.UserRepository;
import lk.ghanim.api.dto.response.AuthResponse;
import lk.ghanim.api.dto.request.LoginRequest;
import lk.ghanim.api.dto.request.RegisterRequest;
import lk.ghanim.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail()
            );
        }

        User.Role role = "wholesale".equalsIgnoreCase(request.getAccountType())
                ? User.Role.WHOLESALE
                : User.Role.RETAIL;

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .businessName(request.getBusinessName())
                .active(true)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .message("Registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }
}
