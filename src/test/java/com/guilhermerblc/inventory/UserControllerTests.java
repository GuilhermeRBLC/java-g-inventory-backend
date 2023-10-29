package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.*;
import com.guilhermerblc.inventory.repository.PermissionRepository;
import com.guilhermerblc.inventory.repository.UserRepository;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String urlPath = "/api/v1/user";

    String authenticate() throws Exception {
        String signingUrl = "http://localhost:" + port + "/api/v1/auth/signing";

        SigningRequest signingRequest = new SigningRequest("gerente", "1234");

        ResponseEntity<JwtAuthenticationResponse> responseAuth = restTemplate.postForEntity(signingUrl, signingRequest, JwtAuthenticationResponse.class);

        assertThat(responseAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseAuth.getBody()).isNotNull();

        return responseAuth.getBody().getToken();
    }

    @Test
    void gerenteUserShouldExist() throws Exception {
        // Arrange
        String username = "gerente";

        // Act
        Optional<User> gerenteUser = userRepository.findByUsername(username);

        // Assert
        assertThat(gerenteUser.isPresent()).isTrue();
    }

    @Test
    void userShouldBeCreated() throws Exception {
        // Arrange

        String requestUrl = "http://localhost:" + port + urlPath;

        List<Permission> permissionsList = permissionRepository.findAll();
        Set<Long> permissionsId = Set.of(4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L);
        List<Permission> estoquePermissions = permissionsList.stream().filter( a -> permissionsId.contains(a.getId()) ).toList();

        User requestObject = new User(
                null,
                "Novo Usuário",
                "Estoquista",
                "novo.estoquista",
                "987654",
                Status.ACTIVE,
                estoquePermissions,
                null,
                null
        );

        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<User> response = restTemplate.postForEntity(requestUrl, requestObject, User.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(requestObject.getName());
        assertThat(response.getBody().getRole()).isEqualTo(requestObject.getRole());
        assertThat(response.getBody().getUsername()).isEqualTo(requestObject.getUsername());
        assertThat(response.getBody().getPassword()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(requestObject.getStatus());

        assertThat(response.getBody().getPermissions().stream().allMatch(
                a -> estoquePermissions.stream().anyMatch(b -> Objects.equals(a.getId(), b.getId()))
        )).isTrue();

        assertThat(response.getBody().getCreated()).isNotNull();
        assertThat(response.getBody().getModified()).isNull();
    }

    @Test
    void userShouldBeUpdated() throws Exception {
        // Arrange

        List<Permission> permissionsList = permissionRepository.findAll();
        Set<Long> permissionsId = Set.of(4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L);
        List<Permission> estoquePermissions = permissionsList.stream().filter( a -> permissionsId.contains(a.getId()) ).toList();

        User requestObject = new User(
                null,
                "Novo Usuário",
                "Estoquista",
                "novo.estoquista",
                "987654",
                Status.ACTIVE,
                estoquePermissions,
                LocalDateTime.now(),
                null
        );

        requestObject = userRepository.save(requestObject);
        requestObject.setName("Modificado");
        requestObject.setRole("Estoquista 2");
        requestObject.setStatus(Status.DEACTIVE);
        requestObject.setUsername("mod.estoquista");
        requestObject.setPassword("123456");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + requestObject.getId();

        String authenticationToken = authenticate();

        // Act

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<User> httpEntity = new HttpEntity<>(requestObject, headers);
        ResponseEntity<User> response = restTemplate.exchange(requestUrl, HttpMethod.PUT, httpEntity, User.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(requestObject.getName());
        assertThat(response.getBody().getRole()).isEqualTo(requestObject.getRole());
        assertThat(response.getBody().getUsername()).isEqualTo(requestObject.getUsername());
        assertThat(response.getBody().getPassword()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(requestObject.getStatus());

        assertThat(response.getBody().getPermissions().stream().allMatch(
                a -> estoquePermissions.stream().anyMatch(b -> Objects.equals(a.getId(), b.getId()))
        )).isTrue();

        assertThat(response.getBody().getCreated()).isNotNull();
        assertThat(response.getBody().getModified()).isNotNull();
    }

    @Test
    void userShouldListAll() throws Exception {
        // Arrange

        List<Permission> permissionsList = permissionRepository.findAll();

        List<User> userList = new ArrayList<>(userRepository.findAll());
        for (int i = 0; i < 10; i++) {
            userList.add(new User(
                    null,
                    "Novo Usuário",
                    "Estoquista",
                    "novo.estoquista" + i,
                    "987654",
                    Status.ACTIVE,
                    permissionsList,
                    LocalDateTime.now(),
                    null
            ));
        }
        userRepository.saveAll(userList);

        String requestUrl = "http://localhost:" + port + urlPath;

        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<List> responseProduct = restTemplate.getForEntity(requestUrl, List.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().size()).isEqualTo(userList.size());

    }

    @Test
    void userShouldGetOne() throws Exception {
        // Arrange

        List<Permission> permissionsList = permissionRepository.findAll();

        List<User> userList = new ArrayList<>(userRepository.findAll());
        for (int i = 0; i < 10; i++) {
            userList.add(new User(
                    null,
                    "Novo Usuário",
                    "Estoquista",
                    "novo,estoquista" + i,
                    "987654",
                    Status.ACTIVE,
                    permissionsList,
                    LocalDateTime.now(),
                    null
            ));
        }
        userList = userRepository.saveAll(userList);

        User requestObject = userList.get(5);
        String requestUrl = "http://localhost:" + port + urlPath + "/" + requestObject.getId();

        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<User> response = restTemplate.getForEntity(requestUrl, User.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(requestObject.getName());
        assertThat(response.getBody().getRole()).isEqualTo(requestObject.getRole());
        assertThat(response.getBody().getUsername()).isEqualTo(requestObject.getUsername());
        assertThat(response.getBody().getPassword()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(requestObject.getStatus());

        assertThat(response.getBody().getPermissions().stream().allMatch(
                a -> permissionsList.stream().anyMatch(b -> Objects.equals(a.getId(), b.getId()))
        )).isTrue();

        assertThat(response.getBody().getCreated()).isNotNull();
        assertThat(response.getBody().getModified()).isNull();
    }

    @Test
    void userShouldBeDeleted() throws Exception {
        // Arrange

        List<Permission> permissionsList = permissionRepository.findAll();

        User requestObject = new User(
                null,
                "Novo Usuário",
                "Estoquista",
                "del.estoquista",
                "987654",
                Status.ACTIVE,
                permissionsList,
                LocalDateTime.now(),
                null
        );
        requestObject = userRepository.save(requestObject);

        String productUrl = "http://localhost:" + port + urlPath + "/" + requestObject.getId();

        String authenticationToken = authenticate();

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<User> httpEntity = new HttpEntity<>(requestObject, headers);
        ResponseEntity<User> responseProduct = restTemplate.exchange(
                productUrl,
                HttpMethod.DELETE,
                httpEntity,
                User.class
        );

        Optional<User> databaseUser = userRepository.findById(requestObject.getId());

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(responseProduct.getBody()).isNull();

        assertThat(databaseUser.isPresent()).isFalse();
    }

}
