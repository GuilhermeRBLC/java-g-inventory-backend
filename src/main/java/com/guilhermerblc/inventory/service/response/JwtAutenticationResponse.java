package com.guilhermerblc.inventory.service.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class JwtAutenticationResponse {
    private String token;
    private Long userId;
}
