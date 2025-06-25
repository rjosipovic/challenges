package com.playground.analytics_manager.inbound.challenge;

import com.playground.analytics_manager.dataaccess.entity.ChallengeEntity;
import com.playground.analytics_manager.dataaccess.entity.UserAttempt;
import com.playground.analytics_manager.dataaccess.repository.ChallengeRepository;
import com.playground.analytics_manager.dataaccess.repository.UserRepository;
import com.playground.analytics_manager.inbound.messaging.events.ChallengeSolvedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    @Override
    public void process(ChallengeSolvedEvent event) {
        var userId = event.getUserId();
        var challengeAttemptId = event.getChallengeAttemptId();
        var firstNumber = event.getFirstNumber();
        var secondNumber = event.getSecondNumber();
        var correct = event.isCorrect();
        var game = event.getGame();
        var attemptDate = event.getAttemptDate();

        var userEntityOptional = userRepository.findById(UUID.fromString(userId));

        if (userEntityOptional.isEmpty()) {
            return;
        }

        var userEntity = userEntityOptional.get();
        var userAttempt = UserAttempt.builder()
                .attemptDate(attemptDate)
                .correct(correct)
                .user(userEntity)
                .build();
        //TODO add difficulty
        var challengeEntity = ChallengeEntity.builder()
                .id(UUID.fromString(challengeAttemptId))
                .firstNumber(firstNumber)
                .secondNumber(secondNumber)
                .game(game)
                .userAttempt(userAttempt)
                .build();

        challengeRepository.save(challengeEntity);
    }
}
