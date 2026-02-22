package com.literatememory.linktree.profile;

import com.literatememory.linktree.link.LinkDtos;
import com.literatememory.linktree.link.LinkService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/p")
public class PublicProfileController {
    private final ProfileRepository profileRepository;
    private final LinkService linkService;

    public PublicProfileController(ProfileRepository profileRepository, LinkService linkService) {
        this.profileRepository = profileRepository;
        this.linkService = linkService;
    }

    @GetMapping("/{slug}")
    @Cacheable(value = "publicProfileBySlug", key = "#slug")
    public PublicProfileResponse getPublicProfile(@PathVariable String slug) {
        Profile profile = profileRepository.findBySlug(slug)
                .orElseThrow(() -> new com.literatememory.linktree.common.ApiException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Profile not found"));
        List<LinkDtos.LinkResponse> links = linkService.listPublicActiveBySlug(slug);
        return new PublicProfileResponse(
                profile.getSlug(),
                profile.getTitle(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getThemeJson(),
                links
        );
    }

    public record PublicProfileResponse(
            String slug,
            String title,
            String bio,
            String avatarUrl,
            String themeJson,
            List<LinkDtos.LinkResponse> links
    ) {}
}
