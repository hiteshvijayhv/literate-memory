package com.literatememory.linktree.link;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends MongoRepository<Link, String> {
    List<Link> findByProfileIdOrderByPositionAsc(String profileId);
    Optional<Link> findByIdAndProfileId(String id, String profileId);
    long countByProfileId(String profileId);
    void deleteByIdAndProfileId(String id, String profileId);
}
