package com.aimitjava;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

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
        mockModel = Mockito.mock(ChatLanguageModel.class);
        mockConfig = Mockito.mock(Configuration.class);
        promptCaptor = ArgumentCaptor.forClass(String.class);

        // Standardverhalten für den Mock definieren
        when(mockModel.generate(anyString())).thenReturn("Mocked summary response");

        // Mock-Konfiguration vorbereiten
        when(mockConfig.getOpenAiApiKey()).thenReturn("test-api-key");
        when(mockConfig.getOpenAiModelName()).thenReturn("gpt-3.5-turbo-test");
        when(mockConfig.getOpenAiTemperature()).thenReturn(0.7);

        // Summarizer mit Mock-Objekten erstellen
        summarizer = new OpenAiSummarizer(mockModel, mockConfig);
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
        when(mockModel.generate(anyString())).thenReturn(expectedSummary);

        // When
        String result = summarizer.summarize(shortText);

        // Then
        verify(mockModel).generate(promptCaptor.capture());
        String prompt = promptCaptor.getValue();

        assertThat(result, is(expectedSummary));
        assertThat(prompt, containsString("maximum 5 sentences"));
        assertThat(prompt, containsString(shortText));
    }

    @Test
    void shouldChunkAndCombineLargeText() {
        // Given
        String largeText = generateLargeText();

        // Mock Antworten für jeden generate()-Aufruf
        when(mockModel.generate(anyString()))
                .thenReturn("Chunk summary 1")   // Erste Chunk-Zusammenfassung
                .thenReturn("Chunk summary 2")   // Zweite Chunk-Zusammenfassung
                .thenReturn("Final combined summary");  // Finale Zusammenfassung

        // When
        String result = summarizer.summarize(largeText);

        // Then
        // Verify exact 3 calls to generate()
        verify(mockModel, times(3)).generate(promptCaptor.capture());

        List<String> prompts = promptCaptor.getAllValues();

        // Prüfe Anzahl der Aufrufe
        assertThat(prompts.size(), is(3));

        // Überprüfe Prompts für Chunk-Zusammenfassungen
        assertThat(prompts.subList(0, 2),
                everyItem(allOf(
                        containsString("maximum 5 sentences"),
                        not(containsString("unified summary"))
                )));

        // Überprüfe finalen Prompt
        String finalPrompt = prompts.get(2);
        assertThat(finalPrompt, allOf(
                containsString("unified summary"),
                containsString("maximum 10 sentences")
        ));

        // Überprüfe Ergebnis
        assertThat(result, is("Final combined summary"));
    }

    // Restliche Methoden bleiben unverändert...

    private String generateLargeText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            text.append("Paragraph ").append(i + 1).append(": ");
            text.append("This is a sample paragraph that contains multiple sentences. ");
            text.append("It includes various information that needs to be summarized. ");
            text.append("The content is designed to test the chunking functionality. ");
            text.append("Each paragraph adds to the overall length of the text. ");
            text.append("\n\n");
        }
        return text.toString();
    }
}