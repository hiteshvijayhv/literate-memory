package com.literatememory.linktree.analytics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class AnalyticsEventController {
    private final AnalyticsEventService analyticsEventService;

    public AnalyticsEventController(AnalyticsEventService analyticsEventService) {
        this.analyticsEventService = analyticsEventService;
    }

    @PostMapping("/view")
    public AnalyticsDtos.EventAcceptedResponse trackView(@Valid @RequestBody AnalyticsDtos.TrackProfileViewRequest request,
                                                         HttpServletRequest httpRequest) {
        analyticsEventService.trackView(request, httpRequest);
        return new AnalyticsDtos.EventAcceptedResponse("accepted");
    }

    @PostMapping("/click")
    public AnalyticsDtos.EventAcceptedResponse trackClick(@Valid @RequestBody AnalyticsDtos.TrackLinkClickRequest request,
                                                          HttpServletRequest httpRequest) {
        analyticsEventService.trackClick(request, httpRequest);
        return new AnalyticsDtos.EventAcceptedResponse("accepted");
    }
}
