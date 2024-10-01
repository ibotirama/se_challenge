package com.smartequip.challenge.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartequip.challenge.inbound.AnswerRequest;
import com.smartequip.challenge.outbound.QuestionResponse;
import com.smartequip.challenge.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class QuestionsServiceTest {

  @Mock
  private JwtUtil jwtService;

  @InjectMocks
  private QuestionsService questionsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("Tests for getQuestion method")
  class GetQuestionTests {

    @Test
    @DisplayName("Should generate a valid QuestionResponse")
    void testGetQuestion() {
      ArgumentCaptor<String> questionCaptor = ArgumentCaptor.forClass(String.class);
      when(jwtService.generateToken(anyString())).thenReturn("mockedToken");

      // When
      QuestionResponse response = questionsService.getQuestion();

      // Then
      assertNotNull(response, "Response should not be null");
      assertNotNull(response.question(), "Question should not be null");
      assertNotNull(response.token(), "Token should not be null");
      assertTrue(response.question().startsWith("Please sum the numbers - "),
          "Question should start with the expected prefix");

      // Verify that generateToken was called with the correct question
      verify(jwtService).generateToken(questionCaptor.capture());
      String capturedQuestion = questionCaptor.getValue();
      assertEquals(response.question(), capturedQuestion, "Captured question should match response question");

      // Additionally, verify the format of the numbers in the question
      String numbersPart = capturedQuestion.replace("Please sum the numbers - ", "");
      String[] numbersStr = numbersPart.split(", ");
      assertEquals(3, numbersStr.length, "There should be exactly three numbers in the question");

      for (String numStr : numbersStr) {
        int num = Integer.parseInt(numStr);
        assertTrue(num >= 1 && num < 100, "Number should be between 1 and 99");
      }

      // Verify the token in response
      assertEquals("mockedToken", response.token(), "Token should match the mocked token");
    }
  }

  @Nested
  @DisplayName("Tests for validateQuestion method")
  class ValidateQuestionTests {

    @Test
    @DisplayName("Should return true for valid answer and matching token")
    void testValidateQuestion_Success() {
      // Given
      String question = "Please sum the numbers - 10, 20, 30";
      String token = "validToken";
      int answer = 60;

      AnswerRequest answerRequest = new AnswerRequest(question, answer, token);

      when(jwtService.validateTokenAndGetQuestion(token)).thenReturn(question);

      // When
      boolean isValid = questionsService.validateQuestion(answerRequest);

      // Then
      assertTrue(isValid, "The answer should be valid");

      verify(jwtService).validateTokenAndGetQuestion(token);
    }

    @Test
    @DisplayName("Should return false for incorrect answer")
    void testValidateQuestion_IncorrectAnswer() {
      // Given
      String question = "Please sum the numbers - 5, 15, 25";
      String token = "validToken";
      int answer = 50;

      AnswerRequest answerRequest = new AnswerRequest(question, answer, token);

      when(jwtService.validateTokenAndGetQuestion(token)).thenReturn(question);

      // When
      boolean isValid = questionsService.validateQuestion(answerRequest);

      // Then
      assertFalse(isValid, "The answer should be invalid due to incorrect sum");

      verify(jwtService).validateTokenAndGetQuestion(token);
    }

    @Test
    @DisplayName("Should return false for mismatched token and question")
    void testValidateQuestion_MismatchedToken() {
      // Given
      String question = "Please sum the numbers - 7, 14, 21";
      String token = "invalidToken";
      int answer = 42;

      AnswerRequest answerRequest = new AnswerRequest(question, answer, token);

      String decryptedQuestion = "Please sum the numbers - 1, 2, 3"; // Different question
      when(jwtService.validateTokenAndGetQuestion(token)).thenReturn(decryptedQuestion);

      // When
      boolean isValid = questionsService.validateQuestion(answerRequest);

      // Then
      assertFalse(isValid, "The answer should be invalid due to mismatched token and question");

      verify(jwtService).validateTokenAndGetQuestion(token);
    }

    @Test
    @DisplayName("Should return false when token validation fails (throws exception)")
    void testValidateQuestion_TokenValidationFails() {
      // Given
      String question = "Please sum the numbers - 8, 16, 24";
      String token = "invalidToken";
      int answer = 48;

      AnswerRequest answerRequest = new AnswerRequest(question, answer, token);

      when(jwtService.validateTokenAndGetQuestion(token)).thenThrow(new RuntimeException("Invalid token"));

      // When & Then
      assertThrows(RuntimeException.class, () -> {
        questionsService.validateQuestion(answerRequest);
      }, "An exception should be thrown due to invalid token");

      verify(jwtService).validateTokenAndGetQuestion(token);
    }

    @Test
    @DisplayName("Should return false for malformed question format")
    void testValidateQuestion_MalformedQuestion() {
      // Given
      String question = "Sum these numbers: ten, twenty, thirty"; // Non-numeric
      String token = "validToken";
      int answer = 60;

      AnswerRequest answerRequest = new AnswerRequest(question, answer, token);

      when(jwtService.validateTokenAndGetQuestion(token)).thenReturn(question);

      // When
      boolean isValid = false;
      try {
        isValid = questionsService.validateQuestion(answerRequest);
      } catch (NumberFormatException e) {
        fail("NumberFormatException was thrown due to malformed question");
      }

      // Then
      assertFalse(isValid, "The answer should be invalid due to malformed question");

      verify(jwtService).validateTokenAndGetQuestion(token);
    }
  }
}
