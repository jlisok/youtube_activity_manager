package com.jlisok.youtube_activity_manager.youtube.dto;

import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class UserActivityDto<T> {

    private final List<T> youTubeActivities;
    private final SynchronizationState state;
    private final Instant stateCreatedAt;

    public UserActivityDto(List<T> youTubeActivities, SynchronizationState state, Instant stateCreatedAt) {
        this.youTubeActivities = youTubeActivities;
        this.state = state;
        this.stateCreatedAt = stateCreatedAt;
    }

    public List<T> getYouTubeActivities() {
        return youTubeActivities;
    }

    public SynchronizationState getState() {
        return state;
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
                state == that.state &&
                Objects.equals(stateCreatedAt, that.stateCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(youTubeActivities, state, stateCreatedAt);
    }
}
