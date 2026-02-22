package com.literatememory.linktree.analytics;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public class AnalyticsDtos {
    public record TrackProfileViewRequest(
            @NotBlank String slug,
            String visitorId,
            String country,
            String referrer
    ) {}

    public record TrackLinkClickRequest(
            @NotBlank String slug,
            @NotBlank String linkId,
            String visitorId,
            String country,
            String referrer
    ) {}

    public record EventAcceptedResponse(String status) {}

    public record DailyMetric(LocalDate date, long views, long clicks) {}

    public record TopLinkMetric(String linkId, String title, long clicks) {}

    public record AnalyticsResponse(
            long totalViews,
            long uniqueVisitors,
            long totalClicks,
            List<DailyMetric> daily,
            List<TopLinkMetric> topLinks
    ) {}
}
