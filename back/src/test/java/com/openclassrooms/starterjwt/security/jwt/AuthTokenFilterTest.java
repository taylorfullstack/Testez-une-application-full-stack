package com.openclassrooms.starterjwt.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TEST_JWT = "jwt";
    private static final String TEST_USERNAME = "username";

    @BeforeEach
    void setUp() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + TEST_JWT);
    }

    @Test
    void shouldDoFilterInternal() throws Exception {
        // Arrange
        when(jwtUtils.validateJwtToken(TEST_JWT)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(TEST_JWT)).thenReturn(TEST_USERNAME);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils).validateJwtToken(TEST_JWT);
        verify(jwtUtils).getUserNameFromJwtToken(TEST_JWT);
        verify(userDetailsService).loadUserByUsername(TEST_USERNAME);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotDoFilterInternalWhenJwtIsInvalid() throws Exception {
        // Arrange
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + TEST_JWT);
        when(jwtUtils.validateJwtToken(TEST_JWT)).thenReturn(false);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils).validateJwtToken(TEST_JWT);
        verify(jwtUtils, never()).getUserNameFromJwtToken(TEST_JWT);
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotDoFilterInternalWhenUserNotFound() throws Exception {
        // Arrange
        when(jwtUtils.validateJwtToken(TEST_JWT)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(TEST_JWT)).thenReturn(TEST_USERNAME);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenThrow(UsernameNotFoundException.class);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils).validateJwtToken(TEST_JWT);
        verify(jwtUtils).getUserNameFromJwtToken(TEST_JWT);
        verify(userDetailsService).loadUserByUsername(TEST_USERNAME);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnNullWhenAuthorizationHeaderIsNotSet() throws Exception {
        // Arrange
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils, never()).validateJwtToken(any());
        verify(jwtUtils, never()).getUserNameFromJwtToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnNullWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {
        // Arrange
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Invalid " + TEST_JWT);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils, never()).validateJwtToken(any());
        verify(jwtUtils, never()).getUserNameFromJwtToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }
}