package com.playground.analytics_manager.dataaccess.repository;

import com.playground.analytics_manager.dataaccess.entity.ChallengeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ChallengeRepository extends CrudRepository<ChallengeEntity, UUID> {
}
