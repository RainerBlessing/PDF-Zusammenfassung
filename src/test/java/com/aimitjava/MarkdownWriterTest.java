package com.aimitjava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class MarkdownWriterTest {

    private MarkdownWriter writer;
    private File tempPdfFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        writer = new MarkdownWriter();
        tempPdfFile = tempDir.resolve("test.pdf").toFile();
        Files.createFile(tempPdfFile.toPath());
    }

    @Test
    void shouldCreateMarkdownFileWithCorrectContent() throws IOException {
        // Given
        String summary = "This is a test summary.";

        // When
        writer.writeSummary(summary, tempPdfFile);

        // Then
        File markdownFile = new File(tempPdfFile.getParentFile(), "test.pdf.md");
        assertThat(markdownFile.exists(), is(true));

        String content = Files.readString(markdownFile.toPath());

        // Verify main sections
        assertThat(content, containsString("# Summary for test.pdf"));
        assertThat(content, containsString("**Original File:** test.pdf"));
        assertThat(content, containsString("## Summary"));
        assertThat(content, containsString(summary));
        assertThat(content, containsString("*Generated using LangChain4j & OpenAI GPT*"));

        // Verify timestamp format
        assertThat(content, containsString("**Generated on:** "));
        String timestamp = content.split("\\*\\*Generated on:\\*\\* ")[1].split("\\n")[0];
        // Verify it's a valid ISO timestamp
        LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Test
    void shouldThrowExceptionForNonexistentFile() {
        // Given
        File nonexistentFile = new File("nonexistent.pdf");
        String summary = "Test summary";

        // When/Then
        try {
            writer.writeSummary(summary, nonexistentFile);
            org.junit.jupiter.api.Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Original PDF file must exist"));
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.fail("Unexpected IOException");
        }
    }

    @Test
    void shouldThrowExceptionForNullFile() {
        // Given
        String summary = "Test summary";

        // When/Then
        try {
            writer.writeSummary(summary, null);
            org.junit.jupiter.api.Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Original PDF file must exist"));
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.fail("Unexpected IOException");
        }
    }
}