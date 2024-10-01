package com.smartequip.challenge.inbound;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record AnswerRequest(String question, Integer answer, String token) {

  public int[] extractNumbersFromQuestion() {
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(question);
    return matcher.results()
        .mapToInt(match -> Integer.parseInt(match.group()))
        .toArray();
  }

}
