package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor
@Getter
@Setter
@Component
public class AuthenticationHelper {

    public String authenticateUser(String username, String password, int port, TestRestTemplate restTemplate) {
        String signingUrl = "http://localhost:" + port + "/api/v1/auth/signing";

        SigningRequest signingRequest = new SigningRequest(username, password);

        ResponseEntity<JwtAuthenticationResponse> responseAuth = restTemplate.postForEntity(signingUrl, signingRequest, JwtAuthenticationResponse.class);

        assertThat(responseAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseAuth.getBody()).isNotNull();

        return responseAuth.getBody().getToken();
    }

    public String authenticate(int port, TestRestTemplate restTemplate) {
        return authenticateUser("gerente", "1234", port, restTemplate);
    }

    public String authenticateLowPermissions(int port, TestRestTemplate restTemplate) {
        return authenticateUser("limitado", "4321", port, restTemplate);
    }

}
