package com.example.booking;

import com.example.booking.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {
    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void login_returns_token() {
        var body = java.util.Map.of("username", "user", "password", "userpass");
        var req = new HttpEntity<>(body);
        ResponseEntity<String> resp = rest.postForEntity("http://localhost:" + port + "/auth/login", req, String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("token");
    }
}
