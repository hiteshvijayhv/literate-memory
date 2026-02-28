package com.literatememory.linktree.link;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class LinkDtos {
    public record CreateLinkRequest(
            @NotBlank @Size(max = 80) String title,
            @NotBlank @Pattern(regexp = "^https?://.+", message = "url must start with http:// or https://") String url,
            Boolean isEnabled,
            Instant startsAt,
            Instant endsAt
    ) {}

    public record UpdateLinkRequest(
            @NotBlank @Size(max = 80) String title,
            @NotBlank @Pattern(regexp = "^https?://.+", message = "url must start with http:// or https://") String url,
            Boolean isEnabled,
            Instant startsAt,
            Instant endsAt
    ) {}

    public record ReorderItem(
            @NotBlank String id,
            int position
    ) {}

    public record ReorderLinksRequest(
            @NotEmpty @Valid List<ReorderItem> links
    ) {}

    public record LinkResponse(
            String id,
            String profileId,
            String title,
            String url,
            boolean isEnabled,
            int position,
            Instant startsAt,
            Instant endsAt
    ) {}
}
