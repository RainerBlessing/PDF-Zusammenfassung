package com.aimitjava;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

class RealIntegrationTest {

    private Path tempDir;
    private Path pdfsDir;
    private File testPdf;
    private boolean isSetup = false;

    @BeforeEach
    void setUp() throws IOException {
        // Verwende Configuration statt System.getProperty
        Configuration config = Configuration.getInstance();
        if (config.getOpenAiApiKey() == null) {
            System.out.println("Skipping setup - no API key available");
            fail();
            return;
        }

        // Rest des Setups bleibt unverändert
        tempDir = Files.createTempDirectory("integration-test");
        pdfsDir = tempDir.resolve("pdfs");
        Files.createDirectory(pdfsDir);

        // Create test PDF
        testPdf = createTestPdf();
        isSetup = true;
    }

    @AfterEach
    void tearDown() {
        if (!isSetup) {
            return; // Skip cleanup if setup was skipped
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

    /**
     * Configure a valid OPENAI_API_KEY and a PDF file in the pdfs directory
     * before enabling and running this test.
     * @throws IOException
     */
    @Disabled("Configure a valid OPENAI_API_KEY and a PDF file in the pdfs directory.")
    @Test
    void shouldPerformFullWorkflow() throws IOException {
        // Verwende Configuration statt System.getProperty
        Configuration config = Configuration.getInstance();
        if (config.getOpenAiApiKey() == null) {
            System.out.println("Skipping real integration test - no API key available");
            fail();
            return;
        }

        // Rest des Tests bleibt unverändert
        PdfSummarizationApp app = new PdfSummarizationApp();
        app.run();

        File summaryFile = new File(testPdf.getParentFile(), testPdf.getName() + ".md");
        assertThat("Summary file should exist", summaryFile.exists(), is(true));

        String summaryContent = Files.readString(summaryFile.toPath());
        assertThat(summaryContent, containsString("# Summary for test.pdf"));
        assertThat(summaryContent, containsString("**Original File:** test.pdf"));
        assertThat(summaryContent, containsString("## Summary"));
        assertThat(summaryContent, not(containsString("No content to summarize")));
        assertThat(summaryContent, containsString("Generated using LangChain4j & OpenAI GPT"));
    }

    private File createTestPdf() throws IOException {
        File pdfFile = pdfsDir.resolve("test.pdf").toFile();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("This is a test PDF document created for integration testing.");
                contentStream.newLine();
                contentStream.showText("It contains multiple sentences to test the summarization capabilities.");
                contentStream.newLine();
                contentStream.showText("The summary should capture the main points while being concise.");
                contentStream.endText();
            }

            document.save(pdfFile);
        }

        return pdfFile;
    }
}