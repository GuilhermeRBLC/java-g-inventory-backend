package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAutenticationResponse;

public interface AuthenticationService {

    JwtAutenticationResponse signing(SigningRequest request);

}
