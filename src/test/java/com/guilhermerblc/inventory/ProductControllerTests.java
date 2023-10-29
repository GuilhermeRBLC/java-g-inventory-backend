package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductRepository;
import com.guilhermerblc.inventory.repository.UserRepository;
import com.guilhermerblc.inventory.service.request.SigningRequest;
import com.guilhermerblc.inventory.service.response.JwtAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String urlPath = "/api/v1/product";

    String authenticate() throws Exception {
        String signingUrl = "http://localhost:" + port + "/api/v1/auth/signing";

        SigningRequest signingRequest = new SigningRequest("gerente", "1234");

        ResponseEntity<JwtAuthenticationResponse> responseAuth = restTemplate.postForEntity(
                signingUrl,
                signingRequest,
                JwtAuthenticationResponse.class
        );

        assertThat(responseAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseAuth.getBody()).isNotNull();

        return responseAuth.getBody().getToken();
    }

    @Test
    void productShouldBeCreated() throws Exception {
        // Arrange
        String productUrl = "http://localhost:" + port + urlPath;
        Product productRequest = new Product(
                null,
                "Morango",
                "fruta",
                5,
                10,
                "Uma fruta vermelha.",
                null,
                null,
                null
        );

        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<Product> responseProduct = restTemplate.postForEntity(
                productUrl,
                productRequest,
                Product.class
        );

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getDescription()).isEqualTo(productRequest.getDescription());
        assertThat(responseProduct.getBody().getType()).isEqualTo(productRequest.getType());
        assertThat(responseProduct.getBody().getInventoryMaximum()).isEqualTo(productRequest.getInventoryMaximum());
        assertThat(responseProduct.getBody().getInventoryMinimum()).isEqualTo(productRequest.getInventoryMinimum());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productRequest.getObservations());
    }

    @Test
    void productShouldBeUpdated() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        Product productRequest = new Product(
                null,
                "Morango",
                "fruta",
                5,
                10,
                "Uma fruta vermelha.",
                user,
                LocalDateTime.now(),
                null
        );
        productRequest = productRepository.save(productRequest);

        productRequest.setDescription("Morango modificado");
        productRequest.setType("modificado");
        productRequest.setObservations("modificado");
        productRequest.setInventoryMinimum(25);
        productRequest.setInventoryMaximum(50);

        String productUrl = "http://localhost:" + port + urlPath + "/" + productRequest.getId();

        String authenticationToken = authenticate();

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Product> httpEntity = new HttpEntity<Product>(productRequest, headers);
        ResponseEntity<Product> responseProduct = restTemplate.exchange(
                productUrl,
                HttpMethod.PUT,
                httpEntity,
                Product.class
        );

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getDescription()).isEqualTo(productRequest.getDescription());
        assertThat(responseProduct.getBody().getType()).isEqualTo(productRequest.getType());
        assertThat(responseProduct.getBody().getInventoryMaximum()).isEqualTo(productRequest.getInventoryMaximum());
        assertThat(responseProduct.getBody().getInventoryMinimum()).isEqualTo(productRequest.getInventoryMinimum());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productRequest.getObservations());
        assertThat(responseProduct.getBody().getModified()).isNotNull();
    }

    @Test
    void productShouldListAll() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        List<Product> products = new ArrayList<>(productRepository.findAll());
        for (int i = 0; i < 10; i++) {
            products.add(new Product(
                    null,
                    "Morango " + i,
                    "fruta",
                    5,
                    10,
                    "Uma fruta vermelha.",
                    user,
                    LocalDateTime.now(),
                    null
            ));
        }
        productRepository.saveAll(products);

        String productUrl = "http://localhost:" + port + urlPath;

        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<List> responseProduct = restTemplate.getForEntity(productUrl, List.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().size()).isEqualTo(products.size());
    }

    @Test
    void productShouldGetOne() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            products.add(new Product(
                    null,
                    "Morango" + i,
                    "fruta",
                    5,
                    10,
                    "Uma fruta vermelha.",
                    user,
                    LocalDateTime.now(),
                    null
            ));
        }
        products = productRepository.saveAll(products);

        Product productRequest = products.get(5);
        String productUrl = "http://localhost:" + port + urlPath + "/" + productRequest.getId();

        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<Product> responseProduct = restTemplate.getForEntity(productUrl, Product.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getDescription()).isEqualTo(productRequest.getDescription());
        assertThat(responseProduct.getBody().getType()).isEqualTo(productRequest.getType());
        assertThat(responseProduct.getBody().getInventoryMaximum()).isEqualTo(productRequest.getInventoryMaximum());
        assertThat(responseProduct.getBody().getInventoryMinimum()).isEqualTo(productRequest.getInventoryMinimum());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productRequest.getObservations());
    }

    @Test
    void productShouldBeDeleted() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        Product productRequest = new Product(
                null,
                "Morango",
                "fruta",
                5,
                10,
                "Uma fruta vermelha.",
                user,
                LocalDateTime.now(),
                null
        );
        productRequest = productRepository.save(productRequest);

        String productUrl = "http://localhost:" + port + urlPath + "/" + productRequest.getId();

        String authenticationToken = authenticate();

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Product> httpEntity = new HttpEntity<>(productRequest, headers);
        ResponseEntity<Product> responseProduct = restTemplate.exchange(
                productUrl,
                HttpMethod.DELETE,
                httpEntity,
                Product.class
        );

        Optional<Product> databaseProduct = productRepository.findById(productRequest.getId());

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(responseProduct.getBody()).isNull();

        assertThat(databaseProduct.isPresent()).isFalse();
    }

}
