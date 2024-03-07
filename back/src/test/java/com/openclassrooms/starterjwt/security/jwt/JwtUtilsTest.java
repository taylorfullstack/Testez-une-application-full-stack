package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static com.openclassrooms.starterjwt.testUtils.TestConstants.TEST_JWT_TOKEN;
import static com.openclassrooms.starterjwt.testUtils.TestConstants.TEST_USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {
    @Mock
    private Authentication mockAuthentication;

    @Mock
    private UserDetailsImpl mockUserDetails;

    @InjectMocks
    private JwtUtils jwtUtils;

    private static final String JWT_SECRET_FIELD_NAME = "jwtSecret";
    private static final String JWT_EXPIRATION_MS_FIELD_NAME = "jwtExpirationMs";
    private static final Integer TEST_JWT_EXPIRATION_MS = 86400000;
    private static final String TEST_USERNAME = "username";

    @Test
    void shouldValidateValidJwtToken() {
        // Arrange
        when(mockUserDetails.getUsername()).thenReturn(TEST_USER_EMAIL);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        ReflectionTestUtils.setField(jwtUtils, JWT_SECRET_FIELD_NAME, TEST_JWT_TOKEN);
        ReflectionTestUtils.setField(jwtUtils, JWT_EXPIRATION_MS_FIELD_NAME, TEST_JWT_EXPIRATION_MS);

        String generatedJwtToken = jwtUtils.generateJwtToken(mockAuthentication);

        // Act
        boolean isTokenValid = jwtUtils.validateJwtToken(generatedJwtToken);

        // Assert
        assertTrue(isTokenValid);
    }

    @Test
    void shouldNotValidateInvalidJwtToken() {
        // Arrange
        String invalidJwtToken = "invalidJwtToken";

        // Act
        boolean isTokenValid = jwtUtils.validateJwtToken(invalidJwtToken);

        // Assert
        assertFalse(isTokenValid);
    }

    @Test
    void shouldNotValidateExpiredJwtToken() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtils, JWT_SECRET_FIELD_NAME, TEST_JWT_TOKEN);
        String expiredJwtToken = Jwts.builder()
                .setSubject(TEST_USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() - 1800000)) // 30 minutes ago
                .signWith(SignatureAlgorithm.HS512, TEST_JWT_TOKEN)
                .compact();

        // Act
        boolean isTokenValid = jwtUtils.validateJwtToken(expiredJwtToken);

        // Assert
        assertFalse(isTokenValid);
    }

    @Test
    void shouldNotValidateJwtTokenWithInvalidSignature() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtils, JWT_SECRET_FIELD_NAME, TEST_JWT_TOKEN);
        String jwtTokenWithInvalidSignature = Jwts.builder()
                .setSubject(TEST_USERNAME)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 3600000)) // 1 hour from now
                .signWith(SignatureAlgorithm.HS512, "wrongSecretKey")
                .compact();

        // Act
        boolean isTokenValid = jwtUtils.validateJwtToken(jwtTokenWithInvalidSignature);

        // Assert
        assertFalse(isTokenValid);
    }

    @Test
    void shouldNotValidateJwtTokenWithEmptyClaimsString() {
        // Arrange
        String jwtTokenWithEmptyClaimsString = ".";

        // Act
        boolean isTokenValid = jwtUtils.validateJwtToken(jwtTokenWithEmptyClaimsString);

        // Assert
        assertFalse(isTokenValid);
    }

    @Test
    void shouldGetUsernameFromJwtToken() {
        // Arrange
        when(mockUserDetails.getUsername()).thenReturn(TEST_USERNAME);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        ReflectionTestUtils.setField(jwtUtils, JWT_SECRET_FIELD_NAME, TEST_JWT_TOKEN);
        ReflectionTestUtils.setField(jwtUtils, JWT_EXPIRATION_MS_FIELD_NAME, TEST_JWT_EXPIRATION_MS);

        String generatedJwtToken = jwtUtils.generateJwtToken(mockAuthentication);

        // Act
        String username = jwtUtils.getUserNameFromJwtToken(generatedJwtToken);

        // Assert
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void shouldNotValidateMalformedJwtToken() {
        // Arrange
        String malformedJwtToken = "invalidHeader.invalidPayload.85O6sDWR8GqxMgkXbQeS7qBvSKPrf9DGeP8lrJNhX5A";

        // Act
        boolean isTokenValid = jwtUtils.validateJwtToken(malformedJwtToken);

        // Assert
        assertFalse(isTokenValid);
    }

    @Test
    void shouldNotValidateUnsupportedJwtToken() {
        // Arrange
        String unsupportedJwtToken = "eyJhbGciOiAiSFMzODQifQ.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTYxNzU3OTc2MywiZXhwIjoxNjE3NTgzMzYzfQ.5KYtKQePY39zDSPuAKY6E-Cnwe3b7EEF1y2u-3IjlHk";

        // Act
        boolean isTokenValid = jwtUtils.validateJwtToken(unsupportedJwtToken);

        // Assert
        assertFalse(isTokenValid);
    }
}
