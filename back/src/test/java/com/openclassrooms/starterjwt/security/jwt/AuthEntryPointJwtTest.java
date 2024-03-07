package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Mock
    private AuthenticationException mockAuthException;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    void shouldHandleAuthException() throws Exception {
        // Arrange
        String errorMessage = "Unauthorized error";
        when(mockAuthException.getMessage()).thenReturn(errorMessage);
        when(mockHttpRequest.getServletPath()).thenReturn("/api/test");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        authEntryPointJwt.commence(mockHttpRequest, response, mockAuthException);

        // Assert
        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Map<String, Object> expectedResponseBody = new HashMap<>();
        expectedResponseBody.put("status", 401);
        expectedResponseBody.put("error", "Unauthorized");
        expectedResponseBody.put("message", errorMessage);
        expectedResponseBody.put("path", "/api/test");

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResponseBody);

        assertEquals(expectedJson, response.getContentAsString());
    }
}