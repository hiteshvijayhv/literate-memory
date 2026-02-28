package com.literatememory.linktree.analytics;

import com.literatememory.linktree.common.ApiException;
import com.literatememory.linktree.link.Link;
import com.literatememory.linktree.link.LinkRepository;
import com.literatememory.linktree.profile.Profile;
import com.literatememory.linktree.profile.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class AnalyticsEventService {
    private final ProfileRepository profileRepository;
    private final LinkRepository linkRepository;
    private final ProfileViewRepository profileViewRepository;
    private final LinkClickRepository linkClickRepository;

    public AnalyticsEventService(ProfileRepository profileRepository,
                                 LinkRepository linkRepository,
                                 ProfileViewRepository profileViewRepository,
                                 LinkClickRepository linkClickRepository) {
        this.profileRepository = profileRepository;
        this.linkRepository = linkRepository;
        this.profileViewRepository = profileViewRepository;
        this.linkClickRepository = linkClickRepository;
    }

    public void trackView(AnalyticsDtos.TrackProfileViewRequest request, HttpServletRequest httpRequest) {
        Profile profile = profileRepository.findBySlug(request.slug())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Profile not found"));

        ProfileViewEvent event = new ProfileViewEvent();
        event.setProfileId(profile.getId());
        event.setTimestamp(Instant.now());
        event.setVisitorId(resolveVisitorId(request.visitorId(), httpRequest));
        event.setCountry(request.country());
        event.setReferrer(resolveReferrer(request.referrer(), httpRequest));
        event.setDevice(resolveDevice(httpRequest));
        profileViewRepository.save(event);
    }

    public void trackClick(AnalyticsDtos.TrackLinkClickRequest request, HttpServletRequest httpRequest) {
        Profile profile = profileRepository.findBySlug(request.slug())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Profile not found"));

        Link link = linkRepository.findByIdAndProfileId(request.linkId(), profile.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Link not found for profile"));

        LinkClickEvent event = new LinkClickEvent();
        event.setProfileId(profile.getId());
        event.setLinkId(link.getId());
        event.setTimestamp(Instant.now());
        event.setVisitorId(resolveVisitorId(request.visitorId(), httpRequest));
        event.setCountry(request.country());
        event.setReferrer(resolveReferrer(request.referrer(), httpRequest));
        event.setDevice(resolveDevice(httpRequest));
        linkClickRepository.save(event);
    }

    private String resolveReferrer(String providedReferrer, HttpServletRequest request) {
        if (providedReferrer != null && !providedReferrer.isBlank()) return providedReferrer;
        String header = request.getHeader("Referer");
        return header == null ? "unknown" : header;
    }

    private String resolveDevice(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) return "unknown";
        String lower = ua.toLowerCase();
        if (lower.contains("mobile")) return "mobile";
        if (lower.contains("tablet")) return "tablet";
        return "desktop";
    }

    private String resolveVisitorId(String providedVisitorId, HttpServletRequest request) {
        if (providedVisitorId != null && !providedVisitorId.isBlank()) return providedVisitorId;
        String ip = request.getRemoteAddr() == null ? "unknown-ip" : request.getRemoteAddr();
        String ua = request.getHeader("User-Agent") == null ? "unknown-ua" : request.getHeader("User-Agent");
        return sha256(ip + "|" + ua);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception ex) {
            return "unknown-visitor";
        }
    }
}
