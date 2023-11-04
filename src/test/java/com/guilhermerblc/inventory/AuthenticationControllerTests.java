package com.guilhermerblc.inventory;


import com.guilhermerblc.inventory.exceptions.ApiError;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void signingShouldReturnAuthenticationToken() throws Exception {
        // Arrange
        String url = "http://localhost:" + port + "/api/v1/auth/signing";
        SigningRequest request = new SigningRequest("gerente", "1234");

        // Act
        ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity(
                url,
                request,
                JwtAuthenticationResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void signingShouldReturnAuthenticationError() throws Exception {
        // Arrange
        String url = "http://localhost:" + port + "/api/v1/auth/signing";
        SigningRequest request = new SigningRequest("gerente", "12345");

        // Act
        ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString().contains("Invalid username or password.")).isTrue();
    }


}
