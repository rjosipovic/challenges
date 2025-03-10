package com.playground.multiplication.challenge.dataaccess.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity(name = "challenge_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeAttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "factor_a", length = 2)
    private int factorA;
    @Column(name = "factor_b", length = 2)
    private int factorB;
    @Column(name = "result_attempt", length = 4)
    private int resultAttempt;
    @Column(name = "correct")
    private boolean correct;
    @CreationTimestamp
    @Column(name = "attempt_date")
    private ZonedDateTime attemptDate;
}
