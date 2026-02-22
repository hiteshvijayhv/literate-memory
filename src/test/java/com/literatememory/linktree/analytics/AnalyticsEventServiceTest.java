package com.literatememory.linktree.analytics;

import com.literatememory.linktree.link.Link;
import com.literatememory.linktree.link.LinkRepository;
import com.literatememory.linktree.profile.Profile;
import com.literatememory.linktree.profile.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsEventServiceTest {
    @Mock ProfileRepository profileRepository;
    @Mock LinkRepository linkRepository;
    @Mock ProfileViewRepository profileViewRepository;
    @Mock LinkClickRepository linkClickRepository;

    @InjectMocks AnalyticsEventService analyticsEventService;

    @Test
    void trackViewSavesEvent() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setSlug("alice");
        when(profileRepository.findBySlug("alice")).thenReturn(Optional.of(profile));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("User-Agent", "Mozilla/5.0 Mobile");

        analyticsEventService.trackView(new AnalyticsDtos.TrackProfileViewRequest("alice", null, "US", null), request);

        verify(profileViewRepository).save(any(ProfileViewEvent.class));
    }

    @Test
    void trackClickSavesEvent() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setSlug("alice");
        when(profileRepository.findBySlug("alice")).thenReturn(Optional.of(profile));

        Link link = new Link();
        link.setId("l1");
        link.setProfileId("p1");
        when(linkRepository.findByIdAndProfileId("l1", "p1")).thenReturn(Optional.of(link));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("User-Agent", "Mozilla/5.0");

        analyticsEventService.trackClick(new AnalyticsDtos.TrackLinkClickRequest("alice", "l1", null, "US", null), request);

        verify(linkClickRepository).save(any(LinkClickEvent.class));
    }
}
