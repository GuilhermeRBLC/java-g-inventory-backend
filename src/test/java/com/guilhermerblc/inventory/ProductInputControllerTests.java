package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductInputRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductInputControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInputRepository productInputRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String urlPath = "/api/v1/product-input";

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
    void productInputShouldBeCreated() throws Exception {
        // Arrange
        String requestUrl = "http://localhost:" + port + urlPath;

        User user = userRepository.findByUsername("gerente").orElseThrow();

        Product product = new Product(
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

        product = productRepository.save(product);


        ProductInput productInputRequest = new ProductInput(
                null,
                product,
                "123456789",
                "José Fazendeiro",
                BigDecimal.valueOf(560.30),
                LocalDateTime.now(),
                20L,
                "Frutas frescas.",
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
        ResponseEntity<ProductInput> responseProduct = restTemplate.postForEntity(
                requestUrl,
                productInputRequest,
                ProductInput.class
        );

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getBarcode()).isEqualTo(productInputRequest.getBarcode());
        assertThat(responseProduct.getBody().getSupplier()).isEqualTo(productInputRequest.getSupplier());
        assertThat(responseProduct.getBody().getPurchaseValue()).isEqualTo(productInputRequest.getPurchaseValue());
        assertThat(responseProduct.getBody().getPurchaseDate()).isEqualTo(productInputRequest.getPurchaseDate());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productInputRequest.getObservations());
    }

    @Test
    void productInputShouldBeUpdated() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        Product product = new Product(
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

        product = productRepository.save(product);


        ProductInput productInputRequest = new ProductInput(
                null,
                product,
                "123456789",
                "José Fazendeiro",
                BigDecimal.valueOf(560.30),
                LocalDateTime.now(),
                20L,
                "Frutas frescas.",
                user,
                LocalDateTime.now(),
                null
        );

        productInputRequest = productInputRepository.save(productInputRequest);

        productInputRequest.setBarcode("000000000");
        productInputRequest.setSupplier("Antonio");
        productInputRequest.setPurchaseValue(BigDecimal.valueOf(1200));
        productInputRequest.setPurchaseDate(LocalDateTime.now());
        productInputRequest.setQuantity(30L);
        productInputRequest.setObservations("Modificado");

        String productUrl = "http://localhost:" + port + urlPath + "/" + productInputRequest.getId();

        String authenticationToken = authenticate();

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<ProductInput> httpEntity = new HttpEntity<ProductInput>(productInputRequest, headers);
        ResponseEntity<ProductInput> responseProduct = restTemplate.exchange(
                productUrl,
                HttpMethod.PUT,
                httpEntity,
                ProductInput.class
        );

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getBarcode()).isEqualTo(productInputRequest.getBarcode());
        assertThat(responseProduct.getBody().getSupplier()).isEqualTo(productInputRequest.getSupplier());
        assertThat(responseProduct.getBody().getPurchaseValue()).isEqualTo(productInputRequest.getPurchaseValue());
        assertThat(responseProduct.getBody().getPurchaseDate()).isEqualTo(productInputRequest.getPurchaseDate());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productInputRequest.getObservations());
        assertThat(responseProduct.getBody().getModified()).isNotNull();
    }

    @Test
    void productShouldListAll() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        Product product = new Product(
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
        product = productRepository.save(product);

        List<ProductInput> productInputs = new ArrayList<>(productInputRepository.findAll());
        for (int i = 0; i < 10; i++) {
            productInputs.add(new ProductInput(
                    null,
                    product,
                    "123456789",
                    "José Fazendeiro",
                    BigDecimal.valueOf(560.30),
                    LocalDateTime.now(),
                    20L,
                    "Frutas frescas.",
                    user,
                    LocalDateTime.now(),
                    null
            ));
        }
        productInputRepository.saveAll(productInputs);

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
        assertThat(responseProduct.getBody().size()).isEqualTo(productInputs.size());
    }

    @Test
    void productShouldGetOne() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        Product product = new Product(
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
        product = productRepository.save(product);

        List<ProductInput> productInputs = new ArrayList<>(productInputRepository.findAll());
        for (int i = 0; i < 10; i++) {
            productInputs.add(new ProductInput(
                    null,
                    product,
                    "123456789",
                    "José Fazendeiro",
                    new BigDecimal("560.30"),
                    LocalDateTime.now(),
                    20L,
                    "Frutas frescas.",
                    user,
                    LocalDateTime.now(),
                    null
            ));
        }
        productInputs = productInputRepository.saveAll(productInputs);

        ProductInput productInputRequest = productInputs.get(5);
        String productUrl = "http://localhost:" + port + urlPath + "/" + productInputRequest.getId();

        String authenticationToken = authenticate();

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<ProductInput> responseProduct = restTemplate.getForEntity(productUrl, ProductInput.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getBarcode()).isEqualTo(productInputRequest.getBarcode());
        assertThat(responseProduct.getBody().getSupplier()).isEqualTo(productInputRequest.getSupplier());
        assertThat(responseProduct.getBody().getPurchaseValue()).isEqualTo(productInputRequest.getPurchaseValue());
        // assertThat(responseProduct.getBody().getPurchaseDate()).isEqualTo(productInputRequest.getPurchaseDate());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productInputRequest.getObservations());
    }


    @Test
    void productInputShouldBeDeleted() throws Exception {
        // Arrange
        User user = userRepository.findByUsername("gerente").orElseThrow();

        Product product = new Product(
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

        product = productRepository.save(product);


        ProductInput productInputRequest = new ProductInput(
                null,
                product,
                "123456789",
                "José Fazendeiro",
                BigDecimal.valueOf(560.30),
                LocalDateTime.now(),
                20L,
                "Frutas frescas.",
                user,
                LocalDateTime.now(),
                null
        );

        productInputRequest = productInputRepository.save(productInputRequest);

        String productUrl = "http://localhost:" + port + urlPath + "/" + productInputRequest.getId();

        String authenticationToken = authenticate();

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<ProductInput> httpEntity = new HttpEntity<ProductInput>(productInputRequest, headers);
        ResponseEntity<ProductInput> responseProduct = restTemplate.exchange(
                productUrl,
                HttpMethod.DELETE,
                httpEntity,
                ProductInput.class
        );

        Optional<ProductInput> databaseProductInput = productInputRepository.findById(productInputRequest.getId());

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(responseProduct.getBody()).isNull();

        assertThat(databaseProductInput.isPresent()).isFalse();
    }


}
