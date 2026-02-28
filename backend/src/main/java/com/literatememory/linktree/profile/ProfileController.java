package com.literatememory.linktree.profile;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileDtos.ProfileResponse getMine(Authentication authentication) {
        return profileService.getMine(authentication.getName());
    }

    @PatchMapping
    public ProfileDtos.ProfileResponse updateMine(Authentication authentication,
                                                  @Valid @RequestBody ProfileDtos.UpdateProfileRequest request) {
        return profileService.upsertMine(authentication.getName(), request);
    }
}
