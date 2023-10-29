package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.repository.ConfigurationRepository;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigurationControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String urlPath = "/api/v1/configuration";

    String authenticate() throws Exception {
        String signingUrl = "http://localhost:" + port + "/api/v1/auth/signing";

        SigningRequest signingRequest = new SigningRequest("gerente", "1234");

        ResponseEntity<JwtAuthenticationResponse> responseAuth = restTemplate.postForEntity(signingUrl, signingRequest, JwtAuthenticationResponse.class);

        assertThat(responseAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseAuth.getBody()).isNotNull();

        return responseAuth.getBody().getToken();
    }

    @Test
    void configurationShouldListAll() throws Exception {
        // Arrange
        List<Configuration> configurationList = configurationRepository.findAll();
        Set<String> nameList = Set.of(
                "COMPANY_NAME", "COMPANY_LOGO", "ALERT_EMAIL"
        );
        String requestUrl = "http://localhost:" + port + urlPath;
        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<List> response = restTemplate.getForEntity(requestUrl, List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(configurationList.size());

        assertThat(response.getBody().stream().allMatch(
                a -> nameList.stream().anyMatch(
                        b -> b.equals( ((LinkedHashMap<String, Object>)a).get("name") )
                )
        )).isTrue();

    }

    @Test
    void configurationShouldGetOne() throws Exception {
        // Arrange
        Configuration configuration = configurationRepository.findById(1L).orElseThrow();

        String requestUrl = "http://localhost:" + port + urlPath + "/" + configuration.getId();
        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<Configuration> response = restTemplate.getForEntity(requestUrl, Configuration.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(configuration.getName());
        assertThat(response.getBody().getData()).isEqualTo(configuration.getData());
        assertThat(response.getBody().getCreated()).isNotNull();
    }

    @Test
    void configurationShouldUpdate() throws Exception {
        // Arrange
        Configuration configuration = configurationRepository.findById(1L).orElseThrow();

        configuration.setData("Modificado");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + configuration.getId();
        String authenticationToken = authenticate();

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Configuration> httpEntity = new HttpEntity<>(configuration, headers);
        ResponseEntity<Configuration> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.PUT,
                httpEntity,
                Configuration.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(configuration.getName());
        assertThat(response.getBody().getData()).isEqualTo(configuration.getData());
        assertThat(response.getBody().getCreated()).isNotNull();
    }


}
