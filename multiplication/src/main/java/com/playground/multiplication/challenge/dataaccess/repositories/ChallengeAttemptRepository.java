package com.playground.multiplication.challenge.dataaccess.repositories;

import com.playground.multiplication.challenge.dataaccess.entities.ChallengeAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeAttemptRepository extends JpaRepository<ChallengeAttemptEntity, Long> {
}
