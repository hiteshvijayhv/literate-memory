package com.literatememory.linktree.profile;

import com.literatememory.linktree.common.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock ProfileRepository profileRepository;
    @InjectMocks ProfileService profileService;

    @Test
    void getMineThrowsWhenMissing() {
        when(profileRepository.findByUserId("u1")).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> profileService.getMine("u1"));
    }

    @Test
    void upsertMineRejectsTakenSlug() {
        Profile existing = new Profile();
        existing.setUserId("other");
        when(profileRepository.findBySlug("taken_slug")).thenReturn(Optional.of(existing));

        ProfileDtos.UpdateProfileRequest request = new ProfileDtos.UpdateProfileRequest(
                "taken_slug", "Title", "Bio", null, "{}"
        );

        assertThrows(ApiException.class, () -> profileService.upsertMine("u1", request));
    }

    @Test
    void upsertMineCreatesNewProfile() {
        when(profileRepository.findBySlug("myslug")).thenReturn(Optional.empty());
        when(profileRepository.findByUserId("u1")).thenReturn(Optional.empty());
        when(profileRepository.save(org.mockito.ArgumentMatchers.any(Profile.class))).thenAnswer(i -> {
            Profile p = i.getArgument(0);
            p.setId("p1");
            return p;
        });

        ProfileDtos.ProfileResponse response = profileService.upsertMine("u1",
                new ProfileDtos.UpdateProfileRequest("myslug", "My Title", "Bio", null, "{}"));

        assertEquals("p1", response.id());
        assertEquals("myslug", response.slug());
        assertEquals("u1", response.userId());
    }
}
