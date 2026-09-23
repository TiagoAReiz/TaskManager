package com.tiagoreiz.projeto.Infra.Security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String TEST_SECRET = "unit-test-only-secret-with-at-least-32-bytes";

    private JwtService jwtService;
    private UserDetails ana;

    @BeforeEach
    void setUp() {
        jwtService = newService(TEST_SECRET, 60_000L);
        ana = new User("ana@mail.com", "hash", List.of());
    }

    private static JwtService newService(String secret, long expirationMs) {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "jwtSecret", secret);
        ReflectionTestUtils.setField(service, "jwtExpiration", expirationMs);
        return service;
    }

    @Test
    void tokenCarriesSubjectAndUserId() {
        String token = jwtService.generateTokenWithUserId(ana, 42L);

        assertThat(jwtService.extractUsername(token)).isEqualTo("ana@mail.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
        assertThat(jwtService.isTokenValid(token, ana)).isTrue();
    }

    @Test
    void tokenIsNotValidForAnotherUser() {
        String token = jwtService.generateToken(ana);
        UserDetails bob = new User("bob@mail.com", "hash", List.of());

        assertThat(jwtService.isTokenValid(token, bob)).isFalse();
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() {
        String forged = newService("another-secret-that-is-also-32-bytes-long", 60_000L).generateToken(ana);

        assertThatThrownBy(() -> jwtService.extractUsername(forged)).isInstanceOf(JwtException.class);
    }

    @Test
    void expiredTokenIsRejected() {
        String expired = newService(TEST_SECRET, -1_000L).generateToken(ana);

        assertThatThrownBy(() -> jwtService.isTokenValid(expired, ana)).isInstanceOf(JwtException.class);
    }
}
