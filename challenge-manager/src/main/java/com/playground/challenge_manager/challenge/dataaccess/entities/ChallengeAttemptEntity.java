package com.playground.challenge_manager.challenge.dataaccess.entities;

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
    @Column(name = "first_number", length = 2)
    private int firstNumber;
    @Column(name = "second_number", length = 2)
    private int secondNumber;
    @Column(name = "result_attempt", length = 4)
    private int resultAttempt;
    @Column(name = "correct")
    private boolean correct;
    @Column(name = "game")
    private String game;
    @CreationTimestamp
    @Column(name = "attempt_date")
    private ZonedDateTime attemptDate;
}
