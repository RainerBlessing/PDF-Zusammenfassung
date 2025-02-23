package com.aimitjava;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OpenAiSummarizerTest {

    private ChatLanguageModel mockModel;
    private Configuration mockConfig;
    private Summarizer summarizer;
    private ArgumentCaptor<String> promptCaptor;

    @BeforeEach
    void setUp() {
        mockModel = mock(ChatLanguageModel.class);
        mockConfig = mock(Configuration.class);
        promptCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConfig.getOpenAiApiKey()).thenReturn("test-api-key");
        when(mockConfig.getOpenAiModelName()).thenReturn("gpt-3.5-turbo-test");
        when(mockConfig.getOpenAiTemperature()).thenReturn(0.7);

        summarizer = new OpenAiSummarizer(mockModel);
    }

    private static Stream<Arguments> emptyOrInvalidTextCases() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of((String)null),
                Arguments.of("   \n   \t   ")
        );
    }

    @ParameterizedTest(name = "should return no content message for empty text #{index}")
    @MethodSource("emptyOrInvalidTextCases")
    void shouldReturnNoContentMessageForInvalidInput(String input) {
        String summary = summarizer.summarize(input);
        assertThat(summary, is("No content to summarize."));
    }

    @Test
    void shouldGenerateSingleSummaryForShortText() {
        // Given
        String shortText = "This is a sample text that needs to be summarized.";
        String expectedSummary = "Short text summary.";
        when(mockModel.chat(anyString())).thenReturn(expectedSummary);

        // When
        summarizer.summarize(shortText);

        // Then
        verify(mockModel, times(1)).chat(promptCaptor.capture());
        String prompt = promptCaptor.getValue();
        assertThat(prompt, containsString("maximum 5 sentences"));
        assertThat(prompt, containsString(shortText));
    }

    @Test
    void shouldHandleLongTextWithMultipleChunks() {
        // Given
        String longText = generateLongText(10000); // Text longer than chunk size
        String chunkSummary = "Chunk summary";
        String finalSummary = "Final combined summary";

        when(mockModel.chat(contains("Text to summarize")))
                .thenReturn(chunkSummary);
        when(mockModel.chat(contains("Summaries to combine")))
                .thenReturn(finalSummary);

        // When
        String result = summarizer.summarize(longText);

        // Then
        verify(mockModel, atLeast(2)).chat(promptCaptor.capture());
        List<String> prompts = promptCaptor.getAllValues();

        // Verify chunk summaries were generated
        assertThat(prompts.subList(0, prompts.size() - 1),
                everyItem(both(containsString("maximum 5 sentences"))
                        .and(containsString("Text to summarize"))));

        // Verify final summary was generated
        String finalPrompt = prompts.getLast();
        assertThat(finalPrompt, containsString("unified summary"));
        assertThat(finalPrompt, containsString("maximum 10 sentences"));
        assertThat(finalPrompt, containsString(chunkSummary));

        // Verify final result
        assertThat(result, is(finalSummary));
    }

    private String generateLongText(int approxLength) {
        StringBuilder text = new StringBuilder(approxLength);
        String sentence = "This is a sample sentence for testing purposes. ";
        while (text.length() < approxLength) {
            text.append(sentence);
        }
        return text.toString();
    }
}