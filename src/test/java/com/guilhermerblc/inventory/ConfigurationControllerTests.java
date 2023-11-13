package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.repository.ConfigurationRepository;
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

    @Autowired
    private AuthenticationHelper autenticationHelper;

    private final String urlPath = "/api/v1/configuration";

    // Success tests.

    @Test
    void configurationShouldListAll() throws Exception {
        // Arrange
        List<Configuration> configurationList = configurationRepository.findAll();
        Set<String> nameList = Set.of(
                "COMPANY_NAME", "COMPANY_LOGO", "ALERT_EMAIL"
        );
        String requestUrl = "http://localhost:" + port + urlPath;
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

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
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

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
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

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


    @Test
    void configurationShouldFailToListAll_DueLackOfAuthentication() throws Exception {
        // Arrange
        List<Configuration> configurationList = configurationRepository.findAll();
        Set<String> nameList = Set.of(
                "COMPANY_NAME", "COMPANY_LOGO", "ALERT_EMAIL"
        );
        String requestUrl = "http://localhost:" + port + urlPath;

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().remove("Authorization");
            return execution.execute(request, body);
        })));
        ResponseEntity<Object> response = restTemplate.getForEntity(requestUrl, Object.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
        //assertThat(response.getBody().toString().contains("")).isTrue();

    }

    // Fail tests

    @Test
    void configurationShouldFailToGetOne_DueToID() throws Exception {
        // Arrange

        String requestUrl = "http://localhost:" + port + urlPath + "/5";
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<Object> response = restTemplate.getForEntity(requestUrl, Object.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString().contains("Item not found.")).isTrue();
    }

    @Test
    void configurationShouldNotUpdate_DueInvalidData() throws Exception {
        // Arrange
        Configuration configuration = configurationRepository.findById(1L).orElseThrow();

        configuration.setData("Large Than 1024 characters".repeat(1024));

        String requestUrl = "http://localhost:" + port + urlPath + "/" + configuration.getId();
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Configuration> httpEntity = new HttpEntity<>(configuration, headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.PUT,
                httpEntity,
                Object.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString().contains("Invalid data.")).isTrue();
    }

    @Test
    void configurationShouldNotUpdate_DueDifferentIDs() throws Exception {
        // Arrange
        Configuration configuration = configurationRepository.findById(1L).orElseThrow();

        configuration.setData("Valid Data!");

        // The ID in path must be the same in object. It should cause a fail.
        String requestUrl = "http://localhost:" + port + urlPath + "/" + (configuration.getId() + 1);
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Configuration> httpEntity = new HttpEntity<>(configuration, headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.PUT,
                httpEntity,
                Object.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString().contains("IDs must be the same in path and body.")).isTrue();
    }

    @Test
    void configurationShouldNotUpdate_DueLackOfPermissionOfUser() throws Exception {
        // Arrange
        Configuration configuration = configurationRepository.findById(1L).orElseThrow();

        configuration.setData("Valid Data!");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + configuration.getId();

        // The current user should have no permission to update this entity.
        String authenticationToken = autenticationHelper.authenticateLowPermissions(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Configuration> httpEntity = new HttpEntity<>(configuration, headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.PUT,
                httpEntity,
                Object.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
    }


}
