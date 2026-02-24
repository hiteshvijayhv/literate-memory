package com.literatememory.linktree.safety;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlSafetyServiceTest {
    private final UrlSafetyService service = new UrlSafetyService();

    @Test
    void allowsHttpsPublicHost() {
        UrlSafetyDtos.UrlCheckResponse response = service.check("https://example.com/path");
        assertTrue(response.allowed());
    }

    @Test
    void blocksLocalhost() {
        UrlSafetyDtos.UrlCheckResponse response = service.check("http://localhost:8080");
        assertFalse(response.allowed());
    }

    @Test
    void blocksUnsupportedScheme() {
        UrlSafetyDtos.UrlCheckResponse response = service.check("javascript:alert(1)");
        assertFalse(response.allowed());
    }
}
