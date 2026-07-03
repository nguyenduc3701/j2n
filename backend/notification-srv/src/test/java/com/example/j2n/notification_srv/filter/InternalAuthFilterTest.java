package com.example.j2n.notification_srv.filter;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.security.InternalUserAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalAuthFilterTest {

    @InjectMocks
    private InternalAuthFilter internalAuthFilter;

    private static final String INTERNAL_TOKEN = "secret-internal-token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(internalAuthFilter, "internalToken", INTERNAL_TOKEN);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_Success() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CommonConst.X_INTERNAL_TOKEN, INTERNAL_TOKEN);
        request.addHeader(CommonConst.X_USER_ID, "user-123");
        request.addHeader(CommonConst.X_USER_NAME, "john_doe");
        request.addHeader(CommonConst.X_ROLE_ID, "USER");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        internalAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof InternalUserAuthentication);
        
        InternalUserAuthentication auth = (InternalUserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        assertEquals("user-123", auth.getUserId());
        assertEquals("john_doe", auth.getName());
    }

    @Test
    void doFilterInternal_InvalidToken_ReturnsForbidden() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CommonConst.X_INTERNAL_TOKEN, "wrong-token");
        request.addHeader(CommonConst.X_USER_ID, "user-123");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        internalAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertEquals("Forbidden: Invalid Internal Token", response.getContentAsString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_MissingUserId_ReturnsUnauthorized() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CommonConst.X_INTERNAL_TOKEN, INTERNAL_TOKEN);
        // No user id header

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        internalAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Unauthorized: User info missing", response.getContentAsString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotFilter_SwaggerPaths_ReturnsTrue() {
        // Arrange
        MockHttpServletRequest swaggerUiRequest = new MockHttpServletRequest();
        swaggerUiRequest.setRequestURI("/swagger-ui/index.html");

        MockHttpServletRequest apiDocsRequest = new MockHttpServletRequest();
        apiDocsRequest.setRequestURI("/v3/api-docs");

        MockHttpServletRequest normalRequest = new MockHttpServletRequest();
        normalRequest.setRequestURI("/notifications");

        // Act & Assert
        assertTrue(internalAuthFilter.shouldNotFilter(swaggerUiRequest));
        assertTrue(internalAuthFilter.shouldNotFilter(apiDocsRequest));
        assertFalse(internalAuthFilter.shouldNotFilter(normalRequest));
    }
}
