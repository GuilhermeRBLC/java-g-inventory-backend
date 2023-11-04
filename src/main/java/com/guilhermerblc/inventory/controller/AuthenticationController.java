package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.service.AuthenticationService;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Controller", description = "An API to authenticate in the system.")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signing")
    @Operation(summary = "Get the authentication token", description = "Given a valid username and password returns a bearer token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication, a valid token is generated."),
            @ApiResponse(responseCode = "403", description = "Invalid username or password."),
            @ApiResponse(responseCode = "400", description = "Some other error has ocurrend on authentication phase.")
    })
    public ResponseEntity<JwtAuthenticationResponse> signing(@Valid @RequestBody SigningRequest request) {
        return ResponseEntity.ok(authenticationService.signing(request));
    }

}
