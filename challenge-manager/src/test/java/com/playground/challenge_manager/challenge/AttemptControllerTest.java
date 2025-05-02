package com.playground.challenge_manager.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.challenge_manager.challenge.api.controllers.AttemptController;
import com.playground.challenge_manager.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.challenge_manager.challenge.api.dto.ChallengeResultDTO;
import com.playground.challenge_manager.challenge.services.interfaces.AttemptService;
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
    private AttemptService challengeService;

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
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 276;
        var correct = 276;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, game);
        var result = new ChallengeResultDTO(userId.toString(), firstNumber, secondNumber, guess, correct, true, game);
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
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 6789;
        var correct = 276;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, game);
        var result = new ChallengeResultDTO(userId.toString(), firstNumber, secondNumber, guess, correct, false, game);
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
    void testMakeAttempt_missingUserId() throws Exception {
        //given
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 6789;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(null, firstNumber, secondNumber, guess, game);

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
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 6789;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(userId, firstNumber, secondNumber, guess, game);
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
    void testMakeAttempt_missingFirstNumber() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var secondNumber = 23;
        var guess = 6789;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(userId.toString(), null, secondNumber, guess, game);

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
    void testMakeAttempt_missingSecondNumber() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var firstNumber = 12;
        var guess = 6789;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, null, guess, game);

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
    void testMakeAttempt_missingGame() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 6789;
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, null);

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
    void testMakeAttempt_invalidGame() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 6789;
        var game = "invalidGame";
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, game);

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
    void testMakeAttempt_missingGuess() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var firstNumber = 12;
        var secondNumber = 23;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, null, game);

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
    void whenNumbersHaveDifferentDigitCount_thenReturnBadRequest() throws Exception {
        // given
        var userId = UUID.randomUUID();
        var firstNumber = 12;    // 2 digits
        var secondNumber = 345;  // 3 digits
        var guess = 357;
        var game = "addition";
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, game);

        // when
        var response = mockMvc.perform(post("/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        // then
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }
}