package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.models.Product;
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

    @Autowired
    private AuthenticationHelper autenticationHelper;


    private final String urlPath = "/api/v1/permission";

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
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

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

    // Fail tests

    @Test
    void permissionShouldFailToGetOne_DueToInvalidID() throws Exception {
        // Arrange
        List<Permission> products = permissionRepository.findAll();
        Optional<Permission> greatestId = products.stream().max(Comparator.comparing(Permission::getId));
        long invalidId = 1L;
        if(greatestId.isPresent()) invalidId = greatestId.get().getId() + 1;

        String requestUrl = "http://localhost:" + port + urlPath + "/" + invalidId;
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
    void permissionShouldNotGet_DueLackOfPermissionOfAuthentication() throws Exception {
        // Arrange
        Permission permission = permissionRepository.findById(2L).orElseThrow();

        String requestUrl = "http://localhost:" + port + urlPath + "/" + permission.getId();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().remove("Authorization");
            return execution.execute(request, body);
        })));
        ResponseEntity<Object> response = restTemplate.getForEntity(requestUrl, Object.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
    }

}
