package com.example.j2n.report_srv.interceptor;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.report_srv.dto.InternalUserAuthentication;
import io.grpc.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrpcServerAuthInterceptorTest {

    private GrpcServerAuthInterceptor interceptor;

    @Mock
    private ServerCall<Object, Object> serverCall;

    @Mock
    private ServerCallHandler<Object, Object> next;

    @Mock
    private MethodDescriptor<Object, Object> methodDescriptor;

    @Mock
    private ServerCall.Listener<Object> listener;

    private Metadata headers;

    private static final Metadata.Key<String> X_INTERNAL_TOKEN = Metadata.Key.of(CommonConst.X_INTERNAL_TOKEN,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_USER_ID = Metadata.Key.of(CommonConst.X_USER_ID,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_USER_NAME = Metadata.Key.of(CommonConst.X_USER_NAME,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_ROLE_ID = Metadata.Key.of(CommonConst.X_ROLE_ID,
            Metadata.ASCII_STRING_MARSHALLER);

    @BeforeEach
    void setUp() {
        interceptor = new GrpcServerAuthInterceptor();
        ReflectionTestUtils.setField(interceptor, "internalToken", "valid-token");
        headers = new Metadata();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testInterceptCall_DiscoveryMethodBypass() {
        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("SomeService/GetApiCatalog");
        when(next.startCall(serverCall, headers)).thenReturn(listener);

        ServerCall.Listener<Object> result = interceptor.interceptCall(serverCall, headers, next);

        assertEquals(listener, result);
        verify(next).startCall(serverCall, headers);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testInterceptCall_InvalidInternalToken_Null() {
        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("SomeService/SomeMethod");
        // internal token not set in headers

        ServerCall.Listener<Object> result = interceptor.interceptCall(serverCall, headers, next);

        assertNotNull(result);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(serverCall).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.PERMISSION_DENIED, statusCaptor.getValue().getCode());
        assertEquals("Invalid internal token", statusCaptor.getValue().getDescription());
        verifyNoInteractions(next);
    }

    @Test
    void testInterceptCall_InvalidInternalToken_Mismatch() {
        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("SomeService/SomeMethod");
        headers.put(X_INTERNAL_TOKEN, "invalid-token");

        ServerCall.Listener<Object> result = interceptor.interceptCall(serverCall, headers, next);

        assertNotNull(result);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(serverCall).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.PERMISSION_DENIED, statusCaptor.getValue().getCode());
        verifyNoInteractions(next);
    }

    @Test
    void testInterceptCall_MissingUserId() {
        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("SomeService/SomeMethod");
        headers.put(X_INTERNAL_TOKEN, "valid-token");
        // missing user id

        ServerCall.Listener<Object> result = interceptor.interceptCall(serverCall, headers, next);

        assertNotNull(result);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(serverCall).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
        assertEquals("User mapping failed", statusCaptor.getValue().getDescription());
        verifyNoInteractions(next);
    }

    @Test
    void testInterceptCall_BlankUserId() {
        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("SomeService/SomeMethod");
        headers.put(X_INTERNAL_TOKEN, "valid-token");
        headers.put(X_USER_ID, "   "); // blank user id

        ServerCall.Listener<Object> result = interceptor.interceptCall(serverCall, headers, next);

        assertNotNull(result);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(serverCall).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
        verifyNoInteractions(next);
    }

    @Test
    void testInterceptCall_Success() {
        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("SomeService/SomeMethod");
        headers.put(X_INTERNAL_TOKEN, "valid-token");
        headers.put(X_USER_ID, "user-123");
        headers.put(X_USER_NAME, "john_doe");
        headers.put(X_ROLE_ID, "admin");
        when(next.startCall(any(), any())).thenReturn(listener);

        ServerCall.Listener<Object> result = interceptor.interceptCall(serverCall, headers, next);

        assertNotNull(result);

        // Verify Authentication was set
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth instanceof InternalUserAuthentication);
        InternalUserAuthentication internalAuth = (InternalUserAuthentication) auth;
        assertEquals("user-123", internalAuth.getPrincipal());

        // Also it invokes next.startCall via Contexts.interceptCall
        verify(next).startCall(any(), any());
    }

    @Test
    void testInterceptCall_InternalTokenIsNull() {
        ReflectionTestUtils.setField(interceptor, "internalToken", null);
        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("SomeService/SomeMethod");
        // header token whatever
        headers.put(X_INTERNAL_TOKEN, "any-token");

        ServerCall.Listener<Object> result = interceptor.interceptCall(serverCall, headers, next);

        assertNotNull(result);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(serverCall).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.PERMISSION_DENIED, statusCaptor.getValue().getCode());
        verifyNoInteractions(next);
    }
}
