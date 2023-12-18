package com.bufferoverflow.mentorme.auth;

import com.bufferoverflow.mentorme.config.JWTService;
import com.bufferoverflow.mentorme.user.Role;
import com.bufferoverflow.mentorme.user.User;
import com.bufferoverflow.mentorme.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        User user = User
                .builder()
                .firstName(request.getName())
                .lastName(request.getSurname())
                .email(request.getUser())
                .password(passwordEncoder.encode(request.getPwd()))
                .role(Role.ROLE_USER)
                .reputation(0)
                .build();
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        Integer id = user.getId();
        return AuthenticationResponse.builder().token(jwtToken).uid(id).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByEmail(request.getUsername()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        Integer id = user.getId();
        return AuthenticationResponse.builder().token(jwtToken).uid(id).build();
    }
}
