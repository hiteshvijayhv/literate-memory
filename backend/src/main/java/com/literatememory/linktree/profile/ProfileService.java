package com.literatememory.linktree.profile;

import com.literatememory.linktree.common.ApiException;
import com.literatememory.linktree.profile.ProfileDtos.ProfileResponse;
import com.literatememory.linktree.profile.ProfileDtos.UpdateProfileRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileResponse getMine(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Profile not found"));
        return toResponse(profile);
    }

    @CacheEvict(value = {"publicProfileBySlug", "publicProfileLinks"}, allEntries = true)
    public ProfileResponse upsertMine(String userId, UpdateProfileRequest request) {
        profileRepository.findBySlug(request.slug())
                .filter(p -> !p.getUserId().equals(userId))
                .ifPresent(p -> { throw new ApiException(HttpStatus.CONFLICT, "Slug already taken"); });

        Profile profile = profileRepository.findByUserId(userId).orElseGet(Profile::new);
        profile.setUserId(userId);
        profile.setSlug(request.slug());
        profile.setTitle(request.title());
        profile.setBio(request.bio());
        profile.setAvatarUrl(request.avatarUrl());
        profile.setThemeJson(request.themeJson());

        return toResponse(profileRepository.save(profile));
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getSlug(),
                profile.getTitle(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getThemeJson()
        );
    }
}
