package com.aimitjava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PdfSummarizationAppIntegrationTest {

    private PdfFileFinder fileFinder;
    private PdfExtractor extractor;
    private Summarizer summarizer;
    private MarkdownWriter writer;
    private PdfSummarizationApp app;
    private File testPdf;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary directory and PDF file
        tempDir = Files.createTempDirectory("pdf-test");
        testPdf = new File(tempDir.toFile(), "test.pdf");
        Files.createFile(testPdf.toPath());

        // Set up mocks
        fileFinder = mock(PdfFileFinder.class);
        extractor = mock(PdfExtractor.class);
        summarizer = mock(Summarizer.class);
        writer = mock(MarkdownWriter.class);

        // Configure mock behavior
        when(fileFinder.getPdfFiles()).thenReturn(List.of(testPdf));
        doNothing().when(fileFinder).validateFileSize(any());
        when(extractor.extractText(any())).thenReturn("Extracted text");
        when(summarizer.summarize(any())).thenReturn("Mocked summary");
        doNothing().when(writer).writeSummary(any(), any());

        // Create app with mocked dependencies
        app = new PdfSummarizationApp(fileFinder, extractor, summarizer, writer);
    }

    @AfterEach
    void tearDown() {
        try {
            if (Files.exists(tempDir)) {
                try (Stream<Path> pathStream = Files.walk(tempDir)) {
                    pathStream
                            .sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    System.err.println("Failed to delete: " + path);
                                }
                            });
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to clean up test directory: " + e.getMessage());
        }
        System.clearProperty("OPENAI_API_KEY");
    }

    @Test
    void shouldProcessPdfSuccessfully() throws IOException {
        // Given
        System.setProperty("OPENAI_API_KEY", "test-key");

        // When
        app.run();

        // Then
        verify(fileFinder).getPdfFiles();
        verify(fileFinder).validateFileSize(testPdf);
        verify(extractor).extractText(testPdf);
        verify(summarizer).summarize("Extracted text");
        verify(writer).writeSummary("Mocked summary", testPdf);
    }

    @Test
    void shouldHandleNoPdfsFound() {
        // Given
        System.setProperty("OPENAI_API_KEY", "test-key");
        when(fileFinder.getPdfFiles()).thenReturn(Collections.emptyList());

        // When
        app.run();

        // Then
        verify(fileFinder).getPdfFiles();
        verifyNoInteractions(extractor, summarizer, writer);
    }

    @Test
    void shouldTerminateOnLargePdf() {
        // Given
        System.setProperty("OPENAI_API_KEY", "test-key");
        doThrow(new PdfTooLargeException("File too large"))
                .when(fileFinder).validateFileSize(any());

        // When/Then
        ApplicationTerminationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ApplicationTerminationException.class,
                () -> app.run()
        );

        assertThat(exception.getMessage(), containsString("File too large"));
        assertThat(exception.getExitCode(), is(1));
        verify(fileFinder).getPdfFiles();
        verify(fileFinder).validateFileSize(any());
        verifyNoInteractions(extractor, summarizer, writer);
    }

    @Test
    void shouldTerminateOnMissingApiKey() {
        // Given
        System.clearProperty("OPENAI_API_KEY");

        // When/Then
        ApplicationTerminationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ApplicationTerminationException.class,
                () -> app.run()
        );

        assertThat(exception.getMessage(), containsString("OPENAI_API_KEY environment variable is not set"));
        assertThat(exception.getExitCode(), is(1));
        verifyNoInteractions(fileFinder, extractor, summarizer, writer);
    }
}
