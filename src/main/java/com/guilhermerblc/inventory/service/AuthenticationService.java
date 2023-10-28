package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;

public interface AuthenticationService {

    JwtAuthenticationResponse signing(SigningRequest request);

}
