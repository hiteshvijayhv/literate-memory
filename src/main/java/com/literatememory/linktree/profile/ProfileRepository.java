package com.literatememory.linktree.profile;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);
    Optional<Profile> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
