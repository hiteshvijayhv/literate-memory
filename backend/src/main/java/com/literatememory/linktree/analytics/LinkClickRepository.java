package com.literatememory.linktree.analytics;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface LinkClickRepository extends MongoRepository<LinkClickEvent, String> {
    List<LinkClickEvent> findByProfileIdAndTimestampBetween(String profileId, Instant from, Instant to);
}
