package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.models.Report;
import com.guilhermerblc.inventory.repository.PermissionRepository;
import com.guilhermerblc.inventory.repository.ReportRepository;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PermissionControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String urlPath = "/api/v1/permission";

    String authenticate() throws Exception {
        String signingUrl = "http://localhost:" + port + "/api/v1/auth/signing";

        SigningRequest signingRequest = new SigningRequest("gerente", "1234");

        ResponseEntity<JwtAuthenticationResponse> responseAuth = restTemplate.postForEntity(signingUrl, signingRequest, JwtAuthenticationResponse.class);

        assertThat(responseAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseAuth.getBody()).isNotNull();

        return responseAuth.getBody().getToken();
    }

    @Test
    void permissionShouldListAll() throws Exception {
        // Arrange
        List<Permission> permissionList = new ArrayList<>(permissionRepository.findAll());
        Set<String> descriptionList = Set.of(
                "VIEW_USERS", "EDIT_USERS", "DELETE_USERS",
                "VIEW_PRODUCTS", "EDIT_PRODUCTS", "DELETE_PRODUCTS",
                "VIEW_INPUTS", "EDIT_INPUTS", "DELETE_INPUTS",
                "VIEW_OUTPUTS", "EDIT_OUTPUTS", "DELETE_OUTPUTS",
                "GENERATE_REPORTS", "EDIT_CONFIGURATIONS"
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
        assertThat(response.getBody().size()).isEqualTo(permissionList.size());

        assertThat(response.getBody().stream().allMatch(
                a -> descriptionList.stream().anyMatch(
                        b -> b.equals( ((LinkedHashMap<String, Object>)a).get("description") )
                )
        )).isTrue();

    }

    @Test
    void permissionShouldGetOne() throws Exception {
        // Arrange
        Permission permission = permissionRepository.findById(2L).orElseThrow();

        String requestUrl = "http://localhost:" + port + urlPath + "/" + permission.getId();
        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<Permission> response = restTemplate.getForEntity(requestUrl, Permission.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo(permission.getDescription());
        assertThat(response.getBody().getCreated()).isNotNull();
    }

}
