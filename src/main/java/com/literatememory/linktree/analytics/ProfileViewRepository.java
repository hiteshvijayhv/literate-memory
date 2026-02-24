package com.literatememory.linktree.analytics;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ProfileViewRepository extends MongoRepository<ProfileViewEvent, String> {
    List<ProfileViewEvent> findByProfileIdAndTimestampBetween(String profileId, Instant from, Instant to);
}
