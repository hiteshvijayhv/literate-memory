package com.literatememory.linktree.safety;

import jakarta.validation.constraints.NotBlank;

public class UrlSafetyDtos {
    public record UrlCheckRequest(@NotBlank String url) {}
    public record UrlCheckResponse(boolean allowed, String reason) {}
}
