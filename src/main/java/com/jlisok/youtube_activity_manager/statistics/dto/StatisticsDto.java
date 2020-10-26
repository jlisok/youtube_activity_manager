package com.jlisok.youtube_activity_manager.statistics.dto;

import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class StatisticsDto<T> {

    private final List<T> statistics;
    private final SynchronizationState state;
    private final Instant stateCreatedAt;

    public StatisticsDto(List<T> statistics, SynchronizationState state, Instant stateCreatedAt) {
        this.statistics = statistics;
        this.state = state;
        this.stateCreatedAt = stateCreatedAt;
    }

    public List<T> getStatistics() {
        return statistics;
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
        StatisticsDto<?> that = (StatisticsDto<?>) o;
        return Objects.equals(statistics, that.statistics) &&
                state == that.state &&
                Objects.equals(stateCreatedAt, that.stateCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statistics, state, stateCreatedAt);
    }
}
