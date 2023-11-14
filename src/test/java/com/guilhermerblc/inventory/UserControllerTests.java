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

    @Autowired
    private AuthenticationHelper autenticationHelper;

    private final String urlPath = "/api/v1/user";

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

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

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

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

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
        assertThat(response.getBody().size()).isEqualTo(userList.size());

    }

    @Test
    void userShouldGetOne() throws Exception {
        // Arrange

        List<User> reports = userRepository.findAll();
        Optional<User> greatestIdUser = reports.stream().max(Comparator.comparing(User::getId));
        long greatestId = 1L;
        if(greatestIdUser.isPresent()) greatestId = greatestIdUser.get().getId() + 1;


        List<Permission> permissionsList = permissionRepository.findAll();

        List<User> userList = new ArrayList<>(userRepository.findAll());
        for (int i = 0; i < 10; i++) {
            userList.add(new User(
                    null,
                    "Novo Usuário " + (i + greatestId),
                    "Estoquista",
                    "novo,estoquista" + (i + greatestId),
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

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

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

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<User> httpEntity = new HttpEntity<>(requestObject, headers);
        ResponseEntity<User> response = restTemplate.exchange(
                productUrl,
                HttpMethod.DELETE,
                httpEntity,
                User.class
        );

        Optional<User> databaseUser = userRepository.findById(requestObject.getId());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        assertThat(databaseUser.isPresent()).isFalse();
    }

    // Fail tests

    @Test
    void userShouldFailToGetOne_DueToInvalidID() throws Exception {
        // Arrange

        List<User> reports = userRepository.findAll();
        Optional<User> greatestId = reports.stream().max(Comparator.comparing(User::getId));
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
    void userShouldNotUpdate_DueInvalidData() throws Exception {
        // Arrange

        List<User> reports = userRepository.findAll();
        Optional<User> greatestIdUser = reports.stream().max(Comparator.comparing(User::getId));
        long greatestId = 1L;
        if(greatestIdUser.isPresent()) greatestId = greatestIdUser.get().getId() + 1;

        List<Permission> permissionsList = permissionRepository.findAll();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(new User(
                    null,
                    "Novo Usuário " + (greatestId + i),
                    "Estoquista",
                    "novo,estoquista" + (greatestId + i),
                    "987654",
                    Status.ACTIVE,
                    permissionsList,
                    LocalDateTime.now(),
                    null
            ));
        }
        users = userRepository.saveAll(users);

        User user = users.get(0);

        user.setName("Larger Than 125 characters".repeat(125));
        user.setRole("Larger Than 125 characters".repeat(125));
        user.setUsername("Larger Than 20 characters".repeat(20));

        String requestUrl = "http://localhost:" + port + urlPath + "/" + user.getId();
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
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
    void userShouldNotUpdate_DueDifferentIDs() throws Exception {
        // Arrange

        List<User> reports = userRepository.findAll();
        Optional<User> greatestIdUser = reports.stream().max(Comparator.comparing(User::getId));
        long greatestId = 1L;
        if(greatestIdUser.isPresent()) greatestId = greatestIdUser.get().getId() + 1;

        List<Permission> permissionsList = permissionRepository.findAll();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(new User(
                    null,
                    "Novo Usuário " + (greatestId + i),
                    "Estoquista",
                    "novo,estoquista" + (greatestId + i),
                    "987654",
                    Status.ACTIVE,
                    permissionsList,
                    LocalDateTime.now(),
                    null
            ));
        }
        users = userRepository.saveAll(users);

        User user = users.get(0);

        user.setName("Lower Than 125 characters");
        user.setRole("Lower Than 125 characters");
        user.setUsername("Lower Than 20 chars");

        // The ID in path must be the same in object. It should cause a fail.
        String requestUrl = "http://localhost:" + port + urlPath + "/" + (user.getId() + 1);
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
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
    void userShouldNotUpdate_DueLackOfPermissionOfUser() throws Exception {
        // Arrange

        List<User> reports = userRepository.findAll();
        Optional<User> greatestIdUser = reports.stream().max(Comparator.comparing(User::getId));
        long greatestId = 1L;
        if(greatestIdUser.isPresent()) greatestId = greatestIdUser.get().getId() + 1;

        List<Permission> permissionsList = permissionRepository.findAll();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(new User(
                    null,
                    "Novo Usuário " + (i + greatestId),
                    "Estoquista",
                    "novo,estoquista" + (i + greatestId),
                    "987654",
                    Status.ACTIVE,
                    permissionsList,
                    LocalDateTime.now(),
                    null
            ));
        }
        users = userRepository.saveAll(users);

        User user = users.get(0);

        user.setName("Lower Than 125 characters");
        user.setRole("Lower Than 125 characters");
        user.setUsername("Lower Than 20 chars");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + user.getId();

        // The current user should have no permission to update this entity.
        String authenticationToken = autenticationHelper.authenticateLowPermissions(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
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
