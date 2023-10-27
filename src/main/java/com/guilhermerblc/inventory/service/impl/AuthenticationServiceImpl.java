package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.UserRepository;
import com.guilhermerblc.inventory.service.AuthenticationService;
import com.guilhermerblc.inventory.service.JwtService;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAutenticationResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public JwtAutenticationResponse signing(SigningRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        var jwt = jwtService.generateToken(user);
        return new JwtAutenticationResponse(jwt, user.getId());
    }

}
