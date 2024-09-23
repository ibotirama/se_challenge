package com.smartequip.challenge.services;

import com.smartequip.challenge.inbound.QuestionRequest;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class QuestionsService {

  public String getQuestions() {
    Random random = new Random();
    int[] numbers = random.ints(3, 1, 100).toArray();
    return String.format("Please sum the numbers - %s",
        Arrays.stream(numbers)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining(", ")));
  }

  public boolean validateQuestion(QuestionRequest questionRequest) {
    int[] numbers = questionRequest.getNumbers();
    int sum = Arrays.stream(numbers).sum();

    return sum == questionRequest.getAnswer();
  }
}
