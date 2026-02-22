package com.literatememory.linktree.link;

import com.literatememory.linktree.common.ApiException;
import com.literatememory.linktree.profile.Profile;
import com.literatememory.linktree.profile.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {
    @Mock LinkRepository linkRepository;
    @Mock ProfileRepository profileRepository;
    @InjectMocks LinkService linkService;

    @Test
    void createMineSetsSequentialPosition() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setUserId("u1");
        when(profileRepository.findByUserId("u1")).thenReturn(Optional.of(profile));
        when(linkRepository.countByProfileId("p1")).thenReturn(2L);
        when(linkRepository.save(any(Link.class))).thenAnswer(i -> {
            Link l = i.getArgument(0);
            l.setId("l3");
            return l;
        });

        LinkDtos.LinkResponse response = linkService.createMine("u1",
                new LinkDtos.CreateLinkRequest("GitHub", "https://github.com", true, null, null));

        assertEquals(2, response.position());
        assertEquals("l3", response.id());
    }

    @Test
    void reorderMineRejectsIncompletePayload() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setUserId("u1");

        Link l1 = new Link(); l1.setId("l1"); l1.setProfileId("p1"); l1.setPosition(0);
        Link l2 = new Link(); l2.setId("l2"); l2.setProfileId("p1"); l2.setPosition(1);

        when(profileRepository.findByUserId("u1")).thenReturn(Optional.of(profile));
        when(linkRepository.findByProfileIdOrderByPositionAsc("p1")).thenReturn(List.of(l1, l2));

        LinkDtos.ReorderLinksRequest request = new LinkDtos.ReorderLinksRequest(
                List.of(new LinkDtos.ReorderItem("l1", 0))
        );

        assertThrows(ApiException.class, () -> linkService.reorderMine("u1", request));
    }

    @Test
    void listPublicActiveBySlugFiltersByScheduleAndEnabled() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setSlug("alice");
        when(profileRepository.findBySlug("alice")).thenReturn(Optional.of(profile));

        Instant now = Instant.now();
        Link active = new Link();
        active.setId("a");
        active.setProfileId("p1");
        active.setEnabled(true);
        active.setPosition(0);
        active.setStartsAt(now.minusSeconds(60));
        active.setEndsAt(now.plusSeconds(60));

        Link disabled = new Link();
        disabled.setId("b");
        disabled.setProfileId("p1");
        disabled.setEnabled(false);
        disabled.setPosition(1);

        Link expired = new Link();
        expired.setId("c");
        expired.setProfileId("p1");
        expired.setEnabled(true);
        expired.setPosition(2);
        expired.setEndsAt(now.minusSeconds(1));

        when(linkRepository.findByProfileIdOrderByPositionAsc("p1")).thenReturn(List.of(active, disabled, expired));

        List<LinkDtos.LinkResponse> links = linkService.listPublicActiveBySlug("alice");
        assertEquals(1, links.size());
        assertEquals("a", links.get(0).id());
    }
}
