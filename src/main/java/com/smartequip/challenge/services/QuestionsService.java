package com.smartequip.challenge.services;

import com.smartequip.challenge.inbound.AnswerRequest;
import com.smartequip.challenge.util.JwtUtil;
import com.smartequip.challenge.outbound.QuestionResponse;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class QuestionsService {

  private final JwtUtil jwtService;
  private final Random random = new Random();

  QuestionsService(JwtUtil jwtService) {
    this.jwtService = jwtService;
  }

  public QuestionResponse getQuestion() {
    int[] numbers = random.ints(3, 1, 100).toArray();
    var question =  String.format("Please sum the numbers - %s",
        Arrays.stream(numbers)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining(", ")));

    return new QuestionResponse(question, jwtService.generateToken(question));
  }

  public boolean validateQuestion(AnswerRequest answerRequest) {
    int[] numbers = answerRequest.extractNumbersFromQuestion();
    int sum = Arrays.stream(numbers).sum();
    String questionDecrypted = jwtService.validateTokenAndGetQuestion(answerRequest.token());

    return sum == answerRequest.answer() && questionDecrypted.equals(answerRequest.question());
  }
}
