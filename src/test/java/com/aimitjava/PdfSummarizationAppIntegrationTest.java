package com.aimitjava;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    private PdfDocumentHandler documentHandler;
    private Summarizer summarizer;
    private MarkdownWriter writer;
    private PdfSummarizationApp app;
    private File testPdf;
    private Path tempDir;
    private boolean isSetup = false;

    @BeforeEach
    void setUp() throws IOException {
        // Skip if no API key
        if (Configuration.getInstance().getOpenAiApiKey() == null) {
            System.out.println("Skipping setup - no API key available");
            return;
        }

        // Create test directories
        tempDir = Files.createTempDirectory("integration-test");
        testPdf = createTestPdf(tempDir);

        // Set up mocks
        fileFinder = mock(PdfFileFinder.class);
        documentHandler = mock(PdfDocumentHandler.class);
        summarizer = mock(Summarizer.class);
        writer = mock(MarkdownWriter.class);

        // Configure mock behavior
        when(fileFinder.getPdfFiles()).thenReturn(List.of(testPdf));
        doNothing().when(fileFinder).validateFileSize(any());
        when(documentHandler.extractText(any())).thenReturn("Extracted text");
        when(summarizer.summarize(any())).thenReturn("Mocked summary");
        doNothing().when(writer).writeSummary(any(), any());

        // Create app with mocked dependencies
        app = new PdfSummarizationApp(fileFinder, documentHandler, summarizer, writer);
        isSetup = true;
    }

    @AfterEach
    void tearDown() {
        if (!isSetup) {
            return;
        }

        try {
            if (tempDir != null && Files.exists(tempDir)) {
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
    }

    @Test
    void shouldProcessPdfSuccessfully() throws IOException {
        // Given
        assumeSetup();

        // When
        app.run();

        // Then
        verify(fileFinder).getPdfFiles();
        verify(fileFinder).validateFileSize(testPdf);
        verify(documentHandler).extractText(testPdf);
        verify(summarizer).summarize("Extracted text");
        verify(writer).writeSummary("Mocked summary", testPdf);
    }

    @Test
    void shouldHandleNoPdfsFound() {
        // Given
        assumeSetup();
        when(fileFinder.getPdfFiles()).thenReturn(Collections.emptyList());

        // When
        app.run();

        // Then
        verify(fileFinder).getPdfFiles();
        verifyNoInteractions(documentHandler, summarizer, writer);
    }

    @Test
    void shouldTerminateOnLargePdf() {
        // Given
        assumeSetup();
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
        verifyNoInteractions(documentHandler, summarizer, writer);
    }

    private void assumeSetup() {
        org.junit.jupiter.api.Assumptions.assumeTrue(isSetup,
                "Test skipped - no API key available");
    }

    private File createTestPdf(Path dir) throws IOException {
        Path pdfPath = dir.resolve("test.pdf");
        Files.createFile(pdfPath);
        return pdfPath.toFile();
    }
}
