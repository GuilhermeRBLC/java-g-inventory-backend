package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductInputRepository;
import com.guilhermerblc.inventory.repository.ProductOutputRepository;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductOutputControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOutputRepository productOutputRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthenticationHelper autenticationHelper;

    private final String urlPath = "/api/v1/product-output";


    @Test
    void productOutputShouldBeCreated() throws Exception {
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


        ProductOutput productOutputRequest = new ProductOutput(
                null,
                product,
                "123456789",
                "Marcos Feirante",
                BigDecimal.valueOf(560.30),
                LocalDateTime.now(),
                10L,
                "Frutas frescas.",
                null,
                null,
                null
        );

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<ProductOutput> responseProduct = restTemplate.postForEntity(requestUrl, productOutputRequest, ProductOutput.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getBarcode()).isEqualTo(productOutputRequest.getBarcode());
        assertThat(responseProduct.getBody().getBuyer()).isEqualTo(productOutputRequest.getBuyer());
        assertThat(responseProduct.getBody().getSaleValue()).isEqualTo(productOutputRequest.getSaleValue());
        assertThat(responseProduct.getBody().getSaleDate()).isEqualTo(productOutputRequest.getSaleDate());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productOutputRequest.getObservations());
    }

    @Test
    void productOutputShouldBeUpdated() throws Exception {
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


        ProductOutput productOutputRequest = new ProductOutput(
                null,
                product,
                "123456789",
                "Marcos Feirante",
                BigDecimal.valueOf(560.30),
                LocalDateTime.now(),
                10L,
                "Frutas frescas.",
                user,
                LocalDateTime.now(),
                null
        );

        productOutputRequest = productOutputRepository.save(productOutputRequest);

        productOutputRequest.setBarcode("000000000");
        productOutputRequest.setBuyer("Antonio");
        productOutputRequest.setSaleValue(BigDecimal.valueOf(1200));
        productOutputRequest.setSaleDate(LocalDateTime.now());
        productOutputRequest.setQuantity(25L);
        productOutputRequest.setObservations("Modificado");

        String productUrl = "http://localhost:" + port + urlPath + "/" + productOutputRequest.getId();

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<ProductOutput> httpEntity = new HttpEntity<ProductOutput>(productOutputRequest, headers);
        ResponseEntity<ProductOutput> responseProduct = restTemplate.exchange(productUrl, HttpMethod.PUT, httpEntity, ProductOutput.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getBarcode()).isEqualTo(productOutputRequest.getBarcode());
        assertThat(responseProduct.getBody().getBuyer()).isEqualTo(productOutputRequest.getBuyer());
        assertThat(responseProduct.getBody().getSaleValue()).isEqualTo(productOutputRequest.getSaleValue());
        assertThat(responseProduct.getBody().getSaleDate()).isEqualTo(productOutputRequest.getSaleDate());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productOutputRequest.getObservations());
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

        List<ProductOutput> productOutputs = new ArrayList<>(productOutputRepository.findAll());
        for (int i = 0; i < 10; i++) {
            productOutputs.add(new ProductOutput(
                    null,
                    product,
                    "123456789",
                    "Marcos Feirante",
                    BigDecimal.valueOf(560.30),
                    LocalDateTime.now(),
                    10L,
                    "Frutas frescas.",
                    user,
                    LocalDateTime.now(),
                    null
            ));
        }
        productOutputRepository.saveAll(productOutputs);

        String productUrl = "http://localhost:" + port + urlPath;

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<List> responseProduct = restTemplate.getForEntity(productUrl, List.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().size()).isEqualTo(productOutputs.size());
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

        List<ProductOutput> productOutputs = new ArrayList<>(productOutputRepository.findAll());
        for (int i = 0; i < 10; i++) {
            productOutputs.add(new ProductOutput(
                    null,
                    product,
                    "123456789",
                    "Marcos Feirante",
                    new BigDecimal("560.30"),
                    LocalDateTime.now(),
                    10L,
                    "Frutas frescas.",
                    user,
                    LocalDateTime.now(),
                    null
            ));
        }
        productOutputs = productOutputRepository.saveAll(productOutputs);

        ProductOutput productOutputRequest = productOutputs.get(5);
        String productUrl = "http://localhost:" + port + urlPath + "/" + productOutputRequest.getId();

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<ProductOutput> responseProduct = restTemplate.getForEntity(productUrl, ProductOutput.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().getBarcode()).isEqualTo(productOutputRequest.getBarcode());
        assertThat(responseProduct.getBody().getBuyer()).isEqualTo(productOutputRequest.getBuyer());
        assertThat(responseProduct.getBody().getSaleValue()).isEqualTo(productOutputRequest.getSaleValue());
        // assertThat(responseProduct.getBody().getSaleDate()).isEqualTo(productOutputRequest.getSaleDate());
        assertThat(responseProduct.getBody().getObservations()).isEqualTo(productOutputRequest.getObservations());
    }

    @Test
    void productOutputShouldBeDeleted() throws Exception {
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


        ProductOutput productOutputRequest = new ProductOutput(
                null,
                product,
                "123456789",
                "Marcos Feirante",
                BigDecimal.valueOf(560.30),
                LocalDateTime.now(),
                10L,
                "Frutas frescas.",
                user,
                LocalDateTime.now(),
                null
        );

        productOutputRequest = productOutputRepository.save(productOutputRequest);

        String productUrl = "http://localhost:" + port + urlPath + "/" + productOutputRequest.getId();

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<ProductOutput> httpEntity = new HttpEntity<>(productOutputRequest, headers);
        ResponseEntity<ProductOutput> responseProduct = restTemplate.exchange(
                productUrl,
                HttpMethod.DELETE,
                httpEntity,
                ProductOutput.class
        );

        Optional<ProductOutput> databaseProductOutput = productOutputRepository.findById(productOutputRequest.getId());


        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(responseProduct.getBody()).isNull();

        assertThat(databaseProductOutput.isPresent()).isFalse();

    }

    // Fail tests

    @Test
    void productOutputShouldFailToGetOne_DueToInvalidID() throws Exception {
        // Arrange

        List<ProductOutput> productInputs = productOutputRepository.findAll();
        Optional<ProductOutput> greatestId = productInputs.stream().max(Comparator.comparing(ProductOutput::getId));
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
    void productOutputInputShouldNotUpdate_DueInvalidData() throws Exception {
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

        List<ProductOutput> productOutputs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            productOutputs.add(new ProductOutput(
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
        productOutputs = productOutputRepository.saveAll(productOutputs);

        ProductOutput productOutput = productOutputs.get(0);

        productOutput.setBarcode("Larger than 20 characters".repeat(10));
        productOutput.setBuyer("Larger than 125 characters".repeat(10));
        productOutput.setSaleValue(BigDecimal.valueOf(-1.0));
        productOutput.setQuantity(-1L);
        productOutput.setObservations("Larger than 1024 characters".repeat(10));

        String requestUrl = "http://localhost:" + port + urlPath + "/" + productOutput.getId();
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<ProductOutput> httpEntity = new HttpEntity<>(productOutput, headers);
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
    void productOutputShouldNotUpdate_DueDifferentIDs() throws Exception {
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

        List<ProductOutput> productOutputs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            productOutputs.add(new ProductOutput(
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
        productOutputs = productOutputRepository.saveAll(productOutputs);

        ProductOutput productOutput = productOutputs.get(0);

        productOutput.setBarcode("Lower than 20 chars");
        productOutput.setBuyer("Lower than 125 characters");
        productOutput.setSaleValue(BigDecimal.valueOf(200.0));
        productOutput.setQuantity(50L);
        productOutput.setObservations("Lower than 1024 characters");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + (productOutput.getId() + 1);
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<ProductOutput> httpEntity = new HttpEntity<>(productOutput, headers);
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
    void productOutputShouldNotUpdate_DueLackOfPermissionOfUser() throws Exception {
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

        List<ProductOutput> productOutputs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            productOutputs.add(new ProductOutput(
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
        productOutputs = productOutputRepository.saveAll(productOutputs);

        ProductOutput productOutput = productOutputs.get(0);

        productOutput.setBarcode("Lower than 20 chars");
        productOutput.setBuyer("Lower than 125 characters");
        productOutput.setSaleValue(BigDecimal.valueOf(200.0));
        productOutput.setQuantity(50L);
        productOutput.setObservations("Lower than 1024 characters");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + productOutput.getId();
        String authenticationToken = autenticationHelper.authenticateLowPermissions(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<ProductOutput> httpEntity = new HttpEntity<>(productOutput, headers);
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
