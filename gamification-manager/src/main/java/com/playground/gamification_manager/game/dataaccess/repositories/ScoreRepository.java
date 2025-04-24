package com.playground.gamification_manager.game.dataaccess.repositories;

import com.playground.gamification_manager.game.dataaccess.domain.ScoreEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScoreRepository extends CrudRepository<ScoreEntity, UUID> {

    @Query("SELECT SUM(s.score) FROM scores s WHERE s.userId = :userId")
    Integer totalScoreByUserId(@Param("userId") UUID userId);
}
