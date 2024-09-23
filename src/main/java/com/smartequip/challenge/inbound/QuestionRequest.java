package com.smartequip.challenge.inbound;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionRequest {

  private String question;
  private int answer;
  private LocalDateTime createdAt;

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public int[] getNumbers() {
    return extractNumbersFromQuestion(question);
  }

  private int[] extractNumbersFromQuestion(String question) {
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(question);
    return matcher.results()
        .mapToInt(match -> Integer.parseInt(match.group()))
        .toArray();
  }

  public int getAnswer() {
    return answer;
  }

  public void setAnswer(int answer) {
    this.answer = answer;
  }
}
