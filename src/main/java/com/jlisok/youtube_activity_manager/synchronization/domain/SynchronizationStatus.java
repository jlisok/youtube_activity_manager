package com.jlisok.youtube_activity_manager.synchronization.domain;

import com.jlisok.youtube_activity_manager.database.enums.SynchronizationStateEnumTypePostgreSql;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "synchronization_statuses")
@TypeDef(name = "status", typeClass = SynchronizationStateEnumTypePostgreSql.class)
public class SynchronizationStatus {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Type(type = "status")
    private SynchronizationState status;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public SynchronizationStatus() {
    }

    public SynchronizationStatus(UUID id, SynchronizationState status, Instant modifiedAt, User user) {
        this.id = id;
        this.status = status;
        this.modifiedAt = modifiedAt;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SynchronizationState getStatus() {
        return status;
    }

    public void setStatus(SynchronizationState status) {
        this.status = status;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynchronizationStatus that = (SynchronizationStatus) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
