package com.playground.challenge_manager.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.challenge_manager.challenge.api.controllers.AttemptController;
import com.playground.challenge_manager.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.challenge_manager.challenge.api.dto.ChallengeResultDTO;
import com.playground.challenge_manager.challenge.services.interfaces.ChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class AttemptControllerTest {

    @Mock
    private ChallengeService challengeService;

    @InjectMocks
    private AttemptController attemptController;

    private MockMvc mockMvc;

    private JacksonTester<ChallengeAttemptDTO> jsonChallengeAttempt;
    private JacksonTester<ChallengeResultDTO> jsonChallengeResult;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders
                .standaloneSetup(attemptController)
                .build();//This allows us to test the controller in isolation, without having to create a full Spring Boot application.
    }

    @Test
    void testMakeAttempt() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 23;
        var guess = 276;
        var correct = 276;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);
        var result = new ChallengeResultDTO(userId.toString(), factorA, factorB, guess, correct, true);
        when(challengeService.verifyAttempt(attempt)).thenReturn(result);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType()),
                () -> assertEquals(jsonChallengeResult.write(result).getJson(), response.getContentAsString())
        );
    }

    @Test
    void testMakeAttempt_Failure() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 23;
        var guess = 6789;
        var correct = 276;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);
        var result = new ChallengeResultDTO(userId.toString(), factorA, factorB, guess, correct, false);
        when(challengeService.verifyAttempt(attempt)).thenReturn(result);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType()),
                () -> assertEquals(jsonChallengeResult.write(result).getJson(), response.getContentAsString())
        );
    }

    @Test
    void testMakeAttempt_missingUserAlias() throws Exception {
        //given
        var factorA = 12;
        var factorB = 23;
        var guess = 6789;
        var attempt = new ChallengeAttemptDTO(null, factorA, factorB, guess);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void testMakeAttempt_factorAOutOfRange() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var factorA = 9;
        var factorB = 23;
        var guess = 6789;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void testMakeAttempt_factorBOutOfRange() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 102;
        var guess = 6789;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void testMakeAttempt_negativeGuess() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 23;
        var guess = -1234;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void testMakeAttempt_invalidUserId() throws Exception {
        //given
        var userId = "shouldBeUUID";
        var factorA = 12;
        var factorB = 23;
        var guess = 6789;
        var attempt = new ChallengeAttemptDTO(userId, factorA, factorB, guess);
        //when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }
}