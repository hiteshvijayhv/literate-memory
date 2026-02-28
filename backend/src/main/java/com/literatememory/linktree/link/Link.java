package com.literatememory.linktree.link;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "links")
@CompoundIndexes({
        @CompoundIndex(name = "profile_position_idx", def = "{'profileId': 1, 'position': 1}", unique = true),
        @CompoundIndex(name = "profile_enabled_idx", def = "{'profileId': 1, 'isEnabled': 1}")
})
public class Link {
    @Id
    private String id;
    private String profileId;
    private String title;
    private String url;
    private boolean isEnabled = true;
    private int position;
    private Instant startsAt;
    private Instant endsAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }
}
