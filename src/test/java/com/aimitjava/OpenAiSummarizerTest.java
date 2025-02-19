package com.aimitjava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class OpenAiSummarizerTest {

    private Summarizer summarizer;

    @BeforeEach
    void setUp() {
        summarizer = new OpenAiSummarizer();
    }

    private static Stream<Arguments> emptyOrInvalidTextCases() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of((String)null),
                Arguments.of("   \n   \t   ")
        );
    }

    private static Stream<Arguments> validTextCases() {
        return Stream.of(
                Arguments.of(
                        "This is a sample text that we want to summarize. " +
                                "It contains multiple sentences and should receive a mocked summary response."
                ),
                Arguments.of("Hello"),
                Arguments.of("Text with 123 numbers and !@#$ special characters.")
        );
    }

    @ParameterizedTest(name = "should return no content message for empty text #{index}")
    @MethodSource("emptyOrInvalidTextCases")
    void shouldReturnNoContentMessageForInvalidInput(String input) {
        String summary = summarizer.summarize(input);
        assertThat(summary, is("No content to summarize."));
    }

    @ParameterizedTest(name = "should return mock summary for valid text #{index}")
    @MethodSource("validTextCases")
    void shouldReturnMockSummaryForValidInput(String input) {
        String summary = summarizer.summarize(input);

        assertThat(summary, containsString("mocked summary"));
        assertThat(summary, not(equalTo("No content to summarize.")));
    }
}