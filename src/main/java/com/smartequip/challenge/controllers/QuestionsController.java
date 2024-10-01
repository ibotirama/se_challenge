package com.smartequip.challenge.controllers;

import com.smartequip.challenge.inbound.AnswerRequest;
import com.smartequip.challenge.outbound.QuestionResponse;
import com.smartequip.challenge.services.QuestionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionsController {

  private final QuestionsService questionsService;

  public QuestionsController(QuestionsService questionsService) {
    this.questionsService = questionsService;
  }

  @GetMapping("/questions")
  public ResponseEntity<QuestionResponse> getQuestions() {
    return ResponseEntity.ok(questionsService.getQuestion());
  }

  @PostMapping("/answers")
  public ResponseEntity<Object> validateQuestion(@RequestBody AnswerRequest questionResponse) {
    var isValid = questionsService.validateQuestion(questionResponse);
    return isValid ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
  }
}
