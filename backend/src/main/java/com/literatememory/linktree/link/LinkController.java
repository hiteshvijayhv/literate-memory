package com.literatememory.linktree.link;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/links")
public class LinkController {
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public List<LinkDtos.LinkResponse> listMine(Authentication authentication) {
        return linkService.listMine(authentication.getName());
    }

    @PostMapping
    public LinkDtos.LinkResponse createMine(Authentication authentication,
                                            @Valid @RequestBody LinkDtos.CreateLinkRequest request) {
        return linkService.createMine(authentication.getName(), request);
    }

    @PatchMapping("/{id}")
    public LinkDtos.LinkResponse updateMine(Authentication authentication,
                                            @PathVariable String id,
                                            @Valid @RequestBody LinkDtos.UpdateLinkRequest request) {
        return linkService.updateMine(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMine(Authentication authentication, @PathVariable String id) {
        linkService.deleteMine(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public List<LinkDtos.LinkResponse> reorderMine(Authentication authentication,
                                                   @Valid @RequestBody LinkDtos.ReorderLinksRequest request) {
        return linkService.reorderMine(authentication.getName(), request);
    }
}
