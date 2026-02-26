package com.literatememory.linktree.analytics;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "profile_views")
@CompoundIndexes({
        @CompoundIndex(name = "profile_view_time_idx", def = "{'profileId': 1, 'timestamp': 1}"),
        @CompoundIndex(name = "profile_visitor_time_idx", def = "{'profileId': 1, 'visitorId': 1, 'timestamp': 1}")
})
public class ProfileViewEvent {
    @Id
    private String id;
    private String profileId;
    private Instant timestamp;
    private String visitorId;
    private String country;
    private String device;
    private String referrer;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getVisitorId() { return visitorId; }
    public void setVisitorId(String visitorId) { this.visitorId = visitorId; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }
    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }
}
