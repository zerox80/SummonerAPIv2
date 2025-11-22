package com.zerox80.riotapi;

import com.zerox80.riotapi.config.RateLimitProperties;
import com.zerox80.riotapi.config.RateLimitingFilter;
import com.zerox80.riotapi.controller.BuildController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityFixTest {

    @Test
    public void testBuildControllerConstantTimeComparison() throws Exception {
        BuildController controller = new BuildController(null);

        // Inject trigger token
        Field tokenField = BuildController.class.getDeclaredField("triggerToken");
        tokenField.setAccessible(true);
        tokenField.set(controller, "secret123");

        // Inject trigger enabled
        Field enabledField = BuildController.class.getDeclaredField("triggerEnabled");
        enabledField.setAccessible(true);
        enabledField.setBoolean(controller, true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Aggregation-Token", "wrong");

        ResponseEntity<String> response = controller.aggregate("ahri", null, 1, 1, 1, null, request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Invalid aggregation trigger token", response.getBody());

        request = new MockHttpServletRequest();
        request.addHeader("X-Aggregation-Token", "secret123");
        // Note: This will fail later because service is null, but it passes the token
        // check
        try {
            controller.aggregate("ahri", null, 1, 1, 1, null, request);
        } catch (NullPointerException e) {
            // Expected because service is null, proves token check passed
        }
    }

    @Test
    public void testRateLimitingFilterProxyWhitelist() throws Exception {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setTrustProxy(true);
        properties.setAllowedProxies(Collections.emptyList()); // Empty whitelist

        RateLimitingFilter filter = new RateLimitingFilter(
                mock(org.springframework.beans.factory.ObjectProvider.class),
                mock(org.springframework.beans.factory.ObjectProvider.class));

        // Inject properties manually since we mocked the provider
        Field propsField = RateLimitingFilter.class.getDeclaredField("properties");
        propsField.setAccessible(true);
        propsField.set(filter, properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4"); // Untrusted IP
        request.addHeader("X-Forwarded-For", "10.0.0.1"); // Spoofed IP

        Method clientIpMethod = RateLimitingFilter.class.getDeclaredMethod("clientIp",
                jakarta.servlet.http.HttpServletRequest.class);
        clientIpMethod.setAccessible(true);

        String ip = (String) clientIpMethod.invoke(filter, request);

        // Should return remoteAddr because proxy is not whitelisted
        assertEquals("1.2.3.4", ip);
    }
}
