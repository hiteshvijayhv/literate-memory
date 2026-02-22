package com.literatememory.linktree.analytics;

import com.literatememory.linktree.profile.Profile;
import com.literatememory.linktree.profile.ProfileRepository;
import com.literatememory.linktree.link.Link;
import com.literatememory.linktree.link.LinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsQueryServiceTest {
    @Mock ProfileRepository profileRepository;
    @Mock ProfileViewRepository profileViewRepository;
    @Mock LinkClickRepository linkClickRepository;
    @Mock LinkRepository linkRepository;

    @InjectMocks AnalyticsQueryService analyticsQueryService;

    @Test
    void aggregatesTotalsUniqueDailyAndTopLinks() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setUserId("u1");

        Link link = new Link();
        link.setId("l1");
        link.setTitle("My Link");

        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-01-03T00:00:00Z");

        ProfileViewEvent v1 = new ProfileViewEvent();
        v1.setProfileId("p1");
        v1.setVisitorId("visitor-a");
        v1.setTimestamp(Instant.parse("2026-01-01T10:00:00Z"));
        ProfileViewEvent v2 = new ProfileViewEvent();
        v2.setProfileId("p1");
        v2.setVisitorId("visitor-a");
        v2.setTimestamp(Instant.parse("2026-01-01T11:00:00Z"));
        ProfileViewEvent v3 = new ProfileViewEvent();
        v3.setProfileId("p1");
        v3.setVisitorId("visitor-b");
        v3.setTimestamp(Instant.parse("2026-01-02T11:00:00Z"));

        LinkClickEvent c1 = new LinkClickEvent();
        c1.setProfileId("p1");
        c1.setLinkId("l1");
        c1.setTimestamp(Instant.parse("2026-01-02T12:00:00Z"));

        when(profileRepository.findByUserId("u1")).thenReturn(Optional.of(profile));
        when(profileViewRepository.findByProfileIdAndTimestampBetween("p1", from, to)).thenReturn(List.of(v1, v2, v3));
        when(linkClickRepository.findByProfileIdAndTimestampBetween("p1", from, to)).thenReturn(List.of(c1));
        when(linkRepository.findByProfileIdOrderByPositionAsc("p1")).thenReturn(List.of(link));

        AnalyticsDtos.AnalyticsResponse response = analyticsQueryService.getMyAnalytics("u1", from, to);

        assertEquals(3, response.totalViews());
        assertEquals(2, response.uniqueVisitors());
        assertEquals(1, response.totalClicks());
        assertEquals(2, response.daily().size());
        assertEquals(1, response.topLinks().size());
        assertEquals("My Link", response.topLinks().get(0).title());
    }
}
