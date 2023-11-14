package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.models.Report;
import com.guilhermerblc.inventory.models.Status;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.PermissionRepository;
import com.guilhermerblc.inventory.repository.ReportRepository;
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
public class ReportControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthenticationHelper autenticationHelper;

    private final String urlPath = "/api/v1/report";

    @Test
    void reportShouldBeCreated() throws Exception {
        // Arrange
        String requestUrl = "http://localhost:" + port + urlPath;

        Report requestObject = new Report(
                null,
                "Relatório 1",
                "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                null,
                null
        );

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<Report> response = restTemplate.postForEntity(requestUrl, requestObject, Report.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo(requestObject.getDescription());
        assertThat(response.getBody().getFilters()).isEqualTo(requestObject.getFilters());
        assertThat(response.getBody().getCreated()).isNotNull();
        assertThat(response.getBody().getModified()).isNull();
    }

    @Test
    void reportShouldBeUpdated() throws Exception {
        // Arrange

        Report requestObject = new Report(
                null,
                "Relatório 1",
                "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                LocalDateTime.now(),
                null
        );

        requestObject = reportRepository.save(requestObject);

        requestObject.setDescription("Relatório Modificado");
        requestObject.setFilters("{'initial-date': null, 'end-date': null}");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + requestObject.getId();

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Report> httpEntity = new HttpEntity<>(requestObject, headers);
        ResponseEntity<Report> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.PUT,
                httpEntity,
                Report.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo(requestObject.getDescription());
        assertThat(response.getBody().getFilters()).isEqualTo(requestObject.getFilters());
        assertThat(response.getBody().getCreated()).isNotNull();
        assertThat(response.getBody().getModified()).isNotNull();
    }

    @Test
    void reportShouldListAll() throws Exception {
        // Arrange
        List<Report> reportList = new ArrayList<>(reportRepository.findAll());
        for (int i = 0; i < 10; i++) {
            reportList.add(new Report(
                    null,
                    "Relatório " + i,
                    "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                    LocalDateTime.now(),
                    null
            ));
        }
        reportRepository.saveAll(reportList);

        String requestUrl = "http://localhost:" + port + urlPath;

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<List> responseProduct = restTemplate.getForEntity(requestUrl, List.class);

        // Assert
        assertThat(responseProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseProduct.getBody()).isNotNull();
        assertThat(responseProduct.getBody().size()).isEqualTo(reportList.size());
    }

    @Test
    void reportShouldGetOne() throws Exception {
        // Arrange
        List<Report> reportList = new ArrayList<>(reportRepository.findAll());
        for (int i = 0; i < 10; i++) {
            reportList.add(new Report(
                    null,
                    "Relatório " + i,
                    "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                    LocalDateTime.now(),
                    null
            ));
        }
        reportList = reportRepository.saveAll(reportList);

        Report requestObject = reportList.get(5);
        String requestUrl = "http://localhost:" + port + urlPath + "/" + requestObject.getId();

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + authenticationToken);
            return execution.execute(request, body);
        })));
        ResponseEntity<Report> response = restTemplate.getForEntity(requestUrl, Report.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo(requestObject.getDescription());
        assertThat(response.getBody().getFilters()).isEqualTo(requestObject.getFilters());
        assertThat(response.getBody().getCreated()).isNotNull();
        assertThat(response.getBody().getModified()).isNull();
    }

    @Test
    void reportShouldBeDeleted() throws Exception {
        // Arrange

        Report requestObject = new Report(
                null,
                "Relatório 1",
                "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                LocalDateTime.now(),
                null
        );

        requestObject = reportRepository.save(requestObject);

        String requestUrl = "http://localhost:" + port + urlPath + "/" + requestObject.getId();

        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Report> httpEntity = new HttpEntity<>(requestObject, headers);
        ResponseEntity<Report> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.DELETE,
                httpEntity,
                Report.class
        );

        Optional<Report> databaseReport = reportRepository.findById(requestObject.getId());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        assertThat(databaseReport.isPresent()).isFalse();
    }

    // Fail tests

    @Test
    void reportShouldFailToGetOne_DueToInvalidID() throws Exception {
        // Arrange

        List<Report> reports = reportRepository.findAll();
        Optional<Report> greatestId = reports.stream().max(Comparator.comparing(Report::getId));
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
    void reportShouldNotUpdate_DueInvalidData() throws Exception {
        // Arrange

        List<Report> reports = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            reports.add(new Report(
                    null,
                    "Relatório " + i,
                    "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                    LocalDateTime.now(),
                    null
            ));
        }
        reports = reportRepository.saveAll(reports);

        Report report = reports.get(0);

        report.setDescription("Larger Than 125 characters".repeat(125));
        report.setFilters("Larger Than 1024 characters".repeat(1024));

        String requestUrl = "http://localhost:" + port + urlPath + "/" + report.getId();
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Report> httpEntity = new HttpEntity<>(report, headers);
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
    void reportShouldNotUpdate_DueDifferentIDs() throws Exception {
        // Arrange

        List<Report> reports = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            reports.add(new Report(
                    null,
                    "Relatório " + i,
                    "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                    LocalDateTime.now(),
                    null
            ));
        }
        reports = reportRepository.saveAll(reports);

        Report report = reports.get(0);

        report.setDescription("Lower Than 125 characters");
        report.setFilters("Lower Than 1024 characters");

        // The ID in path must be the same in object. It should cause a fail.
        String requestUrl = "http://localhost:" + port + urlPath + "/" + (report.getId() + 1);
        String authenticationToken = autenticationHelper.authenticate(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Report> httpEntity = new HttpEntity<>(report, headers);
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
    void reportShouldNotUpdate_DueLackOfPermissionOfUser() throws Exception {
        // Arrange

        List<Report> reports = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            reports.add(new Report(
                    null,
                    "Relatório " + i,
                    "{'initial-date': '00/00/0000', 'end-date': '99/99/99999'}",
                    LocalDateTime.now(),
                    null
            ));
        }
        reports = reportRepository.saveAll(reports);

        Report report = reports.get(0);

        report.setDescription("Lower Than 125 characters");
        report.setFilters("Lower Than 1024 characters");

        String requestUrl = "http://localhost:" + port + urlPath + "/" + report.getId();

        // The current user should have no permission to update this entity.
        String authenticationToken = autenticationHelper.authenticateLowPermissions(port, restTemplate);

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authenticationToken);

        HttpEntity<Report> httpEntity = new HttpEntity<>(report, headers);
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
