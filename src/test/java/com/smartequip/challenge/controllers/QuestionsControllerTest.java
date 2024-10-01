package com.smartequip.challenge.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartequip.challenge.inbound.AnswerRequest;
import com.smartequip.challenge.outbound.QuestionResponse;
import com.smartequip.challenge.services.QuestionsService;
import com.smartequip.challenge.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionsControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private QuestionsService questionsService;

  @MockBean
  private JwtUtil jwtUtil;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testGetQuestion() throws Exception {
    QuestionResponse mockQuestion = new QuestionResponse("Please sum the numbers 5,10.", "mockToken");
    Mockito.when(questionsService.getQuestion()).thenReturn(mockQuestion);
    Mockito.when(jwtUtil.generateToken(mockQuestion.question())).thenReturn("mockToken");

    mockMvc.perform(get("/questions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.question").value("Please sum the numbers 5,10."))
        .andExpect(jsonPath("$.token").value("mockToken"));
  }

  @Test
  void testSubmitCorrectAnswer() throws Exception {
    QuestionResponse originalQuestion = new QuestionResponse("Please sum the numbers 5,10.", "validToken");
    Mockito.when(jwtUtil.validateTokenAndGetQuestion("validToken")).thenReturn(originalQuestion.question());
    AnswerRequest answerRequest = new AnswerRequest("Please sum the numbers 5,10.", 15, "validToken");
    Mockito.when(questionsService.validateQuestion(answerRequest)).thenReturn(true);

    mockMvc.perform(post("/answers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(answerRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void testSubmitIncorrectAnswer() throws Exception {
    QuestionResponse originalQuestion = new QuestionResponse("Please sum the numbers 5,10.", "validToken");
    Mockito.when(jwtUtil.validateTokenAndGetQuestion(originalQuestion.token())).thenReturn(originalQuestion.question());
    AnswerRequest answerRequest = new AnswerRequest("Please sum the numbers 5,10.", 20, "validToken");
    Mockito.when(questionsService.validateQuestion(answerRequest)).thenReturn(false);

    mockMvc.perform(post("/answers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(answerRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testSubmitInvalidToken() throws Exception {
    Mockito.when(jwtUtil.validateTokenAndGetQuestion("invalidToken")).thenThrow(new SecurityException("Invalid JWT token"));
    AnswerRequest answerRequest = new AnswerRequest("Please sum the numbers 5,10.", 15, "invalidToken");

    mockMvc.perform(post("/answers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(answerRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testSubmitInvalidQuestion() throws Exception {
    QuestionResponse originalQuestion = new QuestionResponse("Please sum the numbers 5,10.", "validToken");
    Mockito.when(jwtUtil.validateTokenAndGetQuestion("validToken")).thenReturn(originalQuestion.question());
    AnswerRequest answerRequest = new AnswerRequest("Please sum the numbers 7,8.", 15, "validToken");
    Mockito.when(questionsService.validateQuestion(answerRequest)).thenReturn(false);

    mockMvc.perform(post("/answers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(answerRequest)))
        .andExpect(status().isBadRequest());
  }

}
