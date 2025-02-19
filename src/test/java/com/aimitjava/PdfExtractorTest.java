package com.aimitjava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PdfExtractorTest {

    private PdfExtractor extractor;
    private File testDirectory;

    @BeforeEach
    void setUp() throws IOException {
        extractor = new PdfExtractor();
        testDirectory = new File("./test-pdfs");
        if (!testDirectory.exists() && !testDirectory.mkdirs()) {
            throw new IOException("Failed to create test directory");
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (testDirectory.exists()) {
            File[] files = testDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getName());
                    }
                }
            }
            if (!testDirectory.delete()) {
                throw new IOException("Failed to delete test directory");
            }
        }
    }

    @Test
    void shouldExtractTextFromValidPdf() throws IOException {
        // Create a PDF with known content
        File validPdf = new File(testDirectory, "valid.pdf");
        createPdfWithText(validPdf, "Hello, this is a test PDF!");

        String extractedText = extractor.extractText(validPdf);

        assertThat(extractedText, containsString("Hello, this is a test PDF!"));
    }

    @Test
    void shouldThrowExceptionForCorruptPdf() throws IOException {
        // Create a corrupt PDF file
        File corruptPdf = new File(testDirectory, "corrupt.pdf");
        Files.write(corruptPdf.toPath(), "This is not a valid PDF file".getBytes());

        try {
            extractor.extractText(corruptPdf);
            org.junit.jupiter.api.Assertions.fail("Expected PdfExtractionException to be thrown");
        } catch (PdfExtractionException e) {
            assertThat(e.getMessage(), containsString("Failed to extract text from PDF"));
        }
    }

    @Test
    void shouldReturnEmptyStringForPdfWithNoText() throws IOException {
        // Create a PDF with no text content
        File emptyPdf = new File(testDirectory, "empty.pdf");
        createEmptyPdf(emptyPdf);

        String extractedText = extractor.extractText(emptyPdf);

        assertThat(extractedText, is(emptyString()));
    }

    @Test
    void shouldThrowExceptionForNonexistentFile() {
        File nonexistentPdf = new File(testDirectory, "nonexistent.pdf");

        try {
            extractor.extractText(nonexistentPdf);
            org.junit.jupiter.api.Assertions.fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("PDF file does not exist"));
        }
    }

    private void createPdfWithText(File file, String text) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(text);
                contentStream.endText();
            }

            document.save(file);
            if (!file.exists()) {
                throw new IOException("Failed to create PDF file: " + file.getName());
            }
        }
    }

    private void createEmptyPdf(File file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.save(file);
            if (!file.exists()) {
                throw new IOException("Failed to create empty PDF file: " + file.getName());
            }
        }
    }
}
