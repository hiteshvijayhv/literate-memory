package com.literatememory.linktree.safety;

import org.springframework.stereotype.Service;

import java.net.*;
import java.util.Set;

@Service
public class UrlSafetyService {
    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    public UrlSafetyDtos.UrlCheckResponse check(String rawUrl) {
        try {
            URI uri = URI.create(rawUrl.trim());
            if (uri.getScheme() == null || !ALLOWED_SCHEMES.contains(uri.getScheme().toLowerCase())) {
                return new UrlSafetyDtos.UrlCheckResponse(false, "Only http/https URLs are allowed");
            }
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                return new UrlSafetyDtos.UrlCheckResponse(false, "URL host is required");
            }

            String host = uri.getHost().toLowerCase();
            if (host.equals("localhost") || host.endsWith(".local")) {
                return new UrlSafetyDtos.UrlCheckResponse(false, "Localhost and .local hosts are blocked");
            }

            InetAddress addr = InetAddress.getByName(host);
            if (addr.isAnyLocalAddress() || addr.isLoopbackAddress() || addr.isLinkLocalAddress() ||
                    addr.isSiteLocalAddress() || addr.isMulticastAddress()) {
                return new UrlSafetyDtos.UrlCheckResponse(false, "Private or local network addresses are blocked");
            }

            return new UrlSafetyDtos.UrlCheckResponse(true, "ok");
        } catch (IllegalArgumentException ex) {
            return new UrlSafetyDtos.UrlCheckResponse(false, "Malformed URL");
        } catch (UnknownHostException ex) {
            return new UrlSafetyDtos.UrlCheckResponse(false, "Host cannot be resolved");
        } catch (Exception ex) {
            return new UrlSafetyDtos.UrlCheckResponse(false, "URL check failed");
        }
    }
}
