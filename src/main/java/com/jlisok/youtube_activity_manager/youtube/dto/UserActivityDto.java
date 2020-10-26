package com.jlisok.youtube_activity_manager.youtube.dto;

import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class UserActivityDto<T> {

    private final List<T> youTubeActivities;
    private final SynchronizationState lastState;
    private final Instant stateCreatedAt;

    public UserActivityDto(List<T> youTubeActivities, SynchronizationState lastState, Instant stateCreatedAt) {
        this.youTubeActivities = youTubeActivities;
        this.lastState = lastState;
        this.stateCreatedAt = stateCreatedAt;
    }

    public List<T> getYouTubeActivities() {
        return youTubeActivities;
    }

    public SynchronizationState getLastState() {
        return lastState;
    }

    public Instant getStateCreatedAt() {
        return stateCreatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActivityDto<?> that = (UserActivityDto<?>) o;
        return Objects.equals(youTubeActivities, that.youTubeActivities) &&
                lastState == that.lastState &&
                Objects.equals(stateCreatedAt, that.stateCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(youTubeActivities, lastState, stateCreatedAt);
    }
}
