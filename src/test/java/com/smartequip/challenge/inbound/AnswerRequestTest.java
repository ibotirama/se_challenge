package com.smartequip.challenge.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Nested
@DisplayName("Tests for extractNumbersFromQuestion method")
class AnswerRequestTest {

  @Test
  @DisplayName("Should correctly extract numbers from a well-formed question")
  void testExtractNumbersFromQuestion_WellFormed() {
    // Given
    String question = "Please sum the numbers - 12, 34, 56";
    AnswerRequest answerRequest = new AnswerRequest(question, 102, "token123");

    // When
    int[] numbers = answerRequest.extractNumbersFromQuestion();

    // Then
    assertThat(numbers).isNotNull()
        .hasSize(3)
        .containsExactly(12, 34, 56);
  }

  @Test
  @DisplayName("Should return an empty array when there are no numbers in the question")
  void testExtractNumbersFromQuestion_NoNumbers() {
    // Given
    String question = "Please sum the numbers - none";
    AnswerRequest answerRequest = new AnswerRequest(question, 0, "token123");

    // When
    int[] numbers = answerRequest.extractNumbersFromQuestion();

    // Then
    assertThat(numbers).isNotNull()
        .isEmpty();
  }

  @ParameterizedTest(name = "{index} => question=\"{0}\", expectedNumbers={1}")
  @MethodSource("provideQuestionsForExtraction")
  @DisplayName("Should correctly extract numbers from various question formats")
  void testExtractNumbersFromQuestion_VariousFormats(String question, int[] expectedNumbers) {
    // Given
    AnswerRequest answerRequest = new AnswerRequest(question, 0, "token123");

    // When
    int[] numbers = answerRequest.extractNumbersFromQuestion();

    // Then
    assertThat(numbers).isNotNull()
        .containsExactly(expectedNumbers);
  }

  static Stream<Arguments> provideQuestionsForExtraction() {
    return Stream.of(
        Arguments.of("Please sum the numbers - 7, 14, 21", new int[]{7, 14, 21}),
        Arguments.of("Sum these: 1,2,3", new int[]{1, 2, 3}),
        Arguments.of("Total: 100 200 300", new int[]{100, 200, 300}),
        Arguments.of("Numbers -  8,   16 ,24 ", new int[]{8, 16, 24}),
        Arguments.of("Compute: 0, 0, 0", new int[]{0, 0, 0}),
        Arguments.of("Mix of text and numbers 5 apples, 10 oranges", new int[]{5, 10}),
        Arguments.of("Negative numbers -5, -10, -15", new int[]{5, 10, 15}), // Note: regex \d+ doesn't capture negative sign
        Arguments.of("Decimal numbers 1.5, 2.5, 3.0", new int[]{1, 5, 2, 5, 3, 0}),
        Arguments.of("", new int[]{}),
        Arguments.of("No numbers here!", new int[]{})
    );
  }

  @Test
  @DisplayName("Should handle questions with negative numbers by ignoring the negative sign")
  void testExtractNumbersFromQuestion_NegativeNumbers() {
    // Given
    String question = "Please sum the numbers - -5, -10, -15";
    AnswerRequest answerRequest = new AnswerRequest(question, -30, "token123");

    // When
    int[] numbers = answerRequest.extractNumbersFromQuestion();

    // Then
    // Note: The regex "\\d+" does not capture negative signs, so numbers are extracted as 5,10,15
    assertThat(numbers).isNotNull()
        .hasSize(3)
        .containsExactly(5, 10, 15);
  }

  @Test
  @DisplayName("Should handle questions with decimal numbers by extracting integer parts")
  void testExtractNumbersFromQuestion_DecimalNumbers() {
    // Given
    String question = "Please sum the numbers - 1.5, 2.5, 3.0";
    AnswerRequest answerRequest = new AnswerRequest(question, 6, "token123");

    // When
    int[] numbers = answerRequest.extractNumbersFromQuestion();

    // Then
    // The regex "\\d+" will extract '1', '5', '2', '5', '3', '0'
    assertThat(numbers).isNotNull()
        .hasSize(6)
        .containsExactly(1, 5, 2, 5, 3, 0);
  }

  @Test
  @DisplayName("Should throw NullPointerException when question is null")
  void testExtractNumbersFromQuestion_NullQuestion() {
    // Given
    AnswerRequest answerRequest = new AnswerRequest(null, 0, "token123");

    // When & Assert
    assertThatThrownBy(() -> answerRequest.extractNumbersFromQuestion())
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Should handle questions with mixed delimiters")
  void testExtractNumbersFromQuestion_MixedDelimiters() {
    // Given
    String question = "Numbers: 10;20,30|40";
    AnswerRequest answerRequest = new AnswerRequest(question, 100, "token123");

    // When
    int[] numbers = answerRequest.extractNumbersFromQuestion();

    // Then
    assertThat(numbers).isNotNull()
        .hasSize(4)
        .containsExactly(10, 20, 30, 40);
  }
}
