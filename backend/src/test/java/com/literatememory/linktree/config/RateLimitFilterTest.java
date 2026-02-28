package com.literatememory.linktree.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class RateLimitFilterTest {

    @Test
    void blocksAfterLimitExceeded() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();
        FilterChain chain = mock(FilterChain.class);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/events/view");
        request.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse response = null;
        for (int i = 0; i < 121; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(request, response, chain);
        }

        assertEquals(429, response.getStatus());
    }
}
