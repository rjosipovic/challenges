package com.playground.gamification_manager.game.dataaccess.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity(name = "scores")
@Data
@AllArgsConstructor
public class ScoreEntity {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "score")
    private int score;

    @CreationTimestamp
    @Column(name = "score_at")
    private ZonedDateTime scoreAt;

    public ScoreEntity() {
        this.id = UUID.randomUUID();
    }
}
