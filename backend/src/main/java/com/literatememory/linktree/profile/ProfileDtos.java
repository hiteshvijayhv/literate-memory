package com.literatememory.linktree.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileDtos {
    public record UpdateProfileRequest(
            @NotBlank
            @Pattern(regexp = "^[a-z0-9_]{3,30}$", message = "slug must match ^[a-z0-9_]{3,30}$")
            String slug,
            @NotBlank @Size(max = 80) String title,
            @Size(max = 280) String bio,
            @Size(max = 500) String avatarUrl,
            String themeJson
    ) {}

    public record ProfileResponse(
            String id,
            String userId,
            String slug,
            String title,
            String bio,
            String avatarUrl,
            String themeJson
    ) {}
}
