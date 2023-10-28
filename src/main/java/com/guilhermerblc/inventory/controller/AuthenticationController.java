package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.service.AuthenticationService;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signing")
    public ResponseEntity<JwtAuthenticationResponse> signing(@RequestBody SigningRequest request) {
        return ResponseEntity.ok(authenticationService.signing(request));
    }

}
