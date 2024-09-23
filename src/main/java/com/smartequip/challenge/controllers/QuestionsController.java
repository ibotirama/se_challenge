package com.smartequip.challenge.controllers;

import com.smartequip.challenge.inbound.QuestionRequest;
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
  public ResponseEntity<String> getQuestions() {
    return ResponseEntity.ok(questionsService.getQuestions());
  }

  @PostMapping("/questions")
  public ResponseEntity<?> validateQuestion(@RequestBody QuestionRequest questionRequest) {
    var isValid = questionsService.validateQuestion(questionRequest);
    return isValid ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
  }
}
