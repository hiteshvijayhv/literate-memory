package com.literatememory.linktree.analytics;

import com.literatememory.linktree.profile.Profile;
import com.literatememory.linktree.profile.ProfileRepository;
import com.literatememory.linktree.link.Link;
import com.literatememory.linktree.link.LinkRepository;
import com.literatememory.linktree.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalyticsQueryService {
    private final ProfileRepository profileRepository;
    private final ProfileViewRepository profileViewRepository;
    private final LinkClickRepository linkClickRepository;
    private final LinkRepository linkRepository;

    public AnalyticsQueryService(ProfileRepository profileRepository,
                                 ProfileViewRepository profileViewRepository,
                                 LinkClickRepository linkClickRepository,
                                 LinkRepository linkRepository) {
        this.profileRepository = profileRepository;
        this.profileViewRepository = profileViewRepository;
        this.linkClickRepository = linkClickRepository;
        this.linkRepository = linkRepository;
    }

    public AnalyticsDtos.AnalyticsResponse getMyAnalytics(String userId, Instant from, Instant to) {
        if (!to.isAfter(from)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "to must be after from");
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Profile not found for user"));

        List<ProfileViewEvent> views = profileViewRepository.findByProfileIdAndTimestampBetween(profile.getId(), from, to);
        List<LinkClickEvent> clicks = linkClickRepository.findByProfileIdAndTimestampBetween(profile.getId(), from, to);
        Map<String, Link> linksById = linkRepository.findByProfileIdOrderByPositionAsc(profile.getId()).stream()
                .collect(Collectors.toMap(Link::getId, Function.identity()));

        long uniqueVisitors = views.stream()
                .map(ProfileViewEvent::getVisitorId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        Map<LocalDate, long[]> daily = new TreeMap<>();
        for (ProfileViewEvent view : views) {
            LocalDate date = LocalDate.ofInstant(view.getTimestamp(), ZoneOffset.UTC);
            daily.computeIfAbsent(date, d -> new long[]{0, 0})[0]++;
        }
        for (LinkClickEvent click : clicks) {
            LocalDate date = LocalDate.ofInstant(click.getTimestamp(), ZoneOffset.UTC);
            daily.computeIfAbsent(date, d -> new long[]{0, 0})[1]++;
        }

        List<AnalyticsDtos.DailyMetric> dailyMetrics = daily.entrySet().stream()
                .map(e -> new AnalyticsDtos.DailyMetric(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();

        Map<String, Long> clickCountsByLink = clicks.stream()
                .collect(Collectors.groupingBy(LinkClickEvent::getLinkId, Collectors.counting()));

        List<AnalyticsDtos.TopLinkMetric> topLinks = clickCountsByLink.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Link link = linksById.get(e.getKey());
                    String title = link == null ? "deleted-link" : link.getTitle();
                    return new AnalyticsDtos.TopLinkMetric(e.getKey(), title, e.getValue());
                })
                .toList();

        return new AnalyticsDtos.AnalyticsResponse(
                views.size(),
                uniqueVisitors,
                clicks.size(),
                dailyMetrics,
                topLinks
        );
    }
}
