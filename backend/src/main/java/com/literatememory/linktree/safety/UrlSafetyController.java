package com.literatememory.linktree.safety;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/safety")
public class UrlSafetyController {
    private final UrlSafetyService urlSafetyService;

    public UrlSafetyController(UrlSafetyService urlSafetyService) {
        this.urlSafetyService = urlSafetyService;
    }

    @PostMapping("/url-check")
    public UrlSafetyDtos.UrlCheckResponse check(@Valid @RequestBody UrlSafetyDtos.UrlCheckRequest request) {
        return urlSafetyService.check(request.url());
    }
}
