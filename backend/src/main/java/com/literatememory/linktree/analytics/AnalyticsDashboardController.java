package com.literatememory.linktree.analytics;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/me/analytics")
public class AnalyticsDashboardController {
    private final AnalyticsQueryService analyticsQueryService;

    public AnalyticsDashboardController(AnalyticsQueryService analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    @GetMapping
    public AnalyticsDtos.AnalyticsResponse getMine(Authentication authentication,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return analyticsQueryService.getMyAnalytics(authentication.getName(), from, to);
    }
}
