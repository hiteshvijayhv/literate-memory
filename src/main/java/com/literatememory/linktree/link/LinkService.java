package com.literatememory.linktree.link;

import com.literatememory.linktree.common.ApiException;
import com.literatememory.linktree.profile.Profile;
import com.literatememory.linktree.profile.ProfileRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LinkService {
    private final LinkRepository linkRepository;
    private final ProfileRepository profileRepository;

    public LinkService(LinkRepository linkRepository, ProfileRepository profileRepository) {
        this.linkRepository = linkRepository;
        this.profileRepository = profileRepository;
    }

    public List<LinkDtos.LinkResponse> listMine(String userId) {
        Profile profile = getProfileByUserId(userId);
        return linkRepository.findByProfileIdOrderByPositionAsc(profile.getId())
                .stream().map(this::toResponse).toList();
    }

    @CacheEvict(value = "publicProfileBySlug", allEntries = true)
    public LinkDtos.LinkResponse createMine(String userId, LinkDtos.CreateLinkRequest request) {
        Profile profile = getProfileByUserId(userId);
        validateSchedule(request.startsAt(), request.endsAt());

        Link link = new Link();
        link.setProfileId(profile.getId());
        link.setTitle(request.title());
        link.setUrl(request.url());
        link.setEnabled(request.isEnabled() == null || request.isEnabled());
        link.setStartsAt(request.startsAt());
        link.setEndsAt(request.endsAt());
        link.setPosition((int) linkRepository.countByProfileId(profile.getId()));

        return toResponse(linkRepository.save(link));
    }

    @CacheEvict(value = "publicProfileBySlug", allEntries = true)
    public LinkDtos.LinkResponse updateMine(String userId, String linkId, LinkDtos.UpdateLinkRequest request) {
        Profile profile = getProfileByUserId(userId);
        validateSchedule(request.startsAt(), request.endsAt());

        Link link = linkRepository.findByIdAndProfileId(linkId, profile.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Link not found"));

        link.setTitle(request.title());
        link.setUrl(request.url());
        link.setEnabled(request.isEnabled() == null || request.isEnabled());
        link.setStartsAt(request.startsAt());
        link.setEndsAt(request.endsAt());

        return toResponse(linkRepository.save(link));
    }

    @CacheEvict(value = "publicProfileBySlug", allEntries = true)
    public void deleteMine(String userId, String linkId) {
        Profile profile = getProfileByUserId(userId);
        Link link = linkRepository.findByIdAndProfileId(linkId, profile.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Link not found"));

        linkRepository.deleteByIdAndProfileId(link.getId(), profile.getId());

        List<Link> remaining = linkRepository.findByProfileIdOrderByPositionAsc(profile.getId());
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition(i);
        }
        linkRepository.saveAll(remaining);
    }

    @CacheEvict(value = "publicProfileBySlug", allEntries = true)
    public List<LinkDtos.LinkResponse> reorderMine(String userId, LinkDtos.ReorderLinksRequest request) {
        Profile profile = getProfileByUserId(userId);
        List<Link> existing = linkRepository.findByProfileIdOrderByPositionAsc(profile.getId());

        if (existing.size() != request.links().size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Reorder payload must include all links exactly once");
        }

        Set<String> providedIds = request.links().stream().map(LinkDtos.ReorderItem::id).collect(Collectors.toSet());
        if (providedIds.size() != request.links().size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Reorder payload contains duplicate ids");
        }

        Set<String> existingIds = existing.stream().map(Link::getId).collect(Collectors.toSet());
        if (!existingIds.equals(providedIds)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Reorder payload ids do not match existing links");
        }

        Set<Integer> positions = new HashSet<>();
        for (LinkDtos.ReorderItem item : request.links()) {
            if (item.position() < 0 || item.position() >= existing.size() || !positions.add(item.position())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Positions must be unique and in [0..n-1]");
            }
        }

        Map<String, Link> byId = existing.stream().collect(Collectors.toMap(Link::getId, Function.identity()));
        for (LinkDtos.ReorderItem item : request.links()) {
            byId.get(item.id()).setPosition(item.position());
        }

        List<Link> updated = byId.values().stream().sorted(Comparator.comparingInt(Link::getPosition)).toList();
        return linkRepository.saveAll(updated).stream()
                .sorted(Comparator.comparingInt(Link::getPosition))
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(value = "publicProfileLinks", key = "#slug")
    public List<LinkDtos.LinkResponse> listPublicActiveBySlug(String slug) {
        Profile profile = profileRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Profile not found"));

        Instant now = Instant.now();
        return linkRepository.findByProfileIdOrderByPositionAsc(profile.getId()).stream()
                .filter(Link::isEnabled)
                .filter(link -> link.getStartsAt() == null || !link.getStartsAt().isAfter(now))
                .filter(link -> link.getEndsAt() == null || link.getEndsAt().isAfter(now))
                .map(this::toResponse)
                .toList();
    }

    private Profile getProfileByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Profile not found for user"));
    }

    private void validateSchedule(Instant startsAt, Instant endsAt) {
        if (startsAt != null && endsAt != null && !endsAt.isAfter(startsAt)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "endsAt must be after startsAt");
        }
    }

    private LinkDtos.LinkResponse toResponse(Link link) {
        return new LinkDtos.LinkResponse(
                link.getId(),
                link.getProfileId(),
                link.getTitle(),
                link.getUrl(),
                link.isEnabled(),
                link.getPosition(),
                link.getStartsAt(),
                link.getEndsAt()
        );
    }
}
