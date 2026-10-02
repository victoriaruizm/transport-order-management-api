package com.vruiz.order.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

class JwtServiceTest {

    private static final String SECRET = "test-secret-with-at-least-32-bytes!!";

    @Test
    void tokenIsSignedWithSecretAndCarriesSubject() {
        String token = new JwtService(SECRET).createToken("admin");

        Jwt jwt = NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
                .build()
                .decode(token);

        assertEquals("admin", jwt.getSubject());
        assertNotNull(jwt.getExpiresAt());
    }
}
