package com.playground.challenge_manager.challenge;

import com.playground.challenge_manager.auth.AuthConfig;
import com.playground.challenge_manager.auth.JwtUserPrincipal;
import com.playground.challenge_manager.challenge.api.controllers.AttemptController;
import com.playground.challenge_manager.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.challenge_manager.challenge.api.dto.ChallengeResultDTO;
import com.playground.challenge_manager.challenge.services.interfaces.AttemptService;
import com.playground.challenge_manager.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
@WebMvcTest(AttemptController.class)
@Import(SecurityConfig.class)
class AttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttemptService challengeService;
    @MockitoBean
    private AuthConfig authConfig;

    @Autowired
    private JacksonTester<ChallengeAttemptDTO> jsonChallengeAttempt;
    @Autowired
    private JacksonTester<ChallengeResultDTO> jsonChallengeResult;

    @Test
    void testMakeAttempt() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 276;
        var correct = 276;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(null, firstNumber, secondNumber, guess, game);
        var attemptWithUserId = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, game);
        var result = new ChallengeResultDTO(userId.toString(), firstNumber, secondNumber, guess, correct, true, game);
        when(challengeService.verifyAttempt(attemptWithUserId)).thenReturn(result);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        //when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
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
        var attempt = new ChallengeAttemptDTO(null, firstNumber, secondNumber, guess, game);
        var attemptWithUserId = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, game);
        var result = new ChallengeResultDTO(userId.toString(), firstNumber, secondNumber, guess, correct, false, game);
        when(challengeService.verifyAttempt(attemptWithUserId)).thenReturn(result);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, null);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
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
    void testMakeAttempt_missingFirstNumber() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var secondNumber = 23;
        var guess = 6789;
        var game = "multiplication";
        var attempt = new ChallengeAttemptDTO(null, null, secondNumber, guess, game);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, null);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
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
        var attempt = new ChallengeAttemptDTO(null, firstNumber, null, guess, game);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, null);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
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
        var attempt = new ChallengeAttemptDTO(null, firstNumber, secondNumber, guess, null);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, null);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
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
        var attempt = new ChallengeAttemptDTO(null, firstNumber, secondNumber, guess, game);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, null);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
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
        var attempt = new ChallengeAttemptDTO(null, firstNumber, secondNumber, null, game);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, null);

        //when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
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
        var attempt = new ChallengeAttemptDTO(null, firstNumber, secondNumber, guess, game);
        var principal = new JwtUserPrincipal(null, Map.of("userId", userId.toString()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, null);

        // when
        var response = mockMvc.perform(post("/attempts")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChallengeAttempt.write(attempt).getJson()))
                .andReturn().getResponse();

        // then
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }
}