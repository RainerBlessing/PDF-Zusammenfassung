package com.aimitjava;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MarkdownWriter {
    private static final String TEMPLATE = """
        # Summary for %s

        **Original File:** %s  
        **Generated on:** %s

        ---

        ## Summary

        %s

        ---

        *Generated using LangChain4j & OpenAI GPT*
        """;

    /**
     * Writes a summary to a Markdown file in the same directory as the original PDF.
     *
     * @param summary The generated summary text
     * @param originalPdf The original PDF file
     * @throws IOException If there are issues writing the file
     * @throws IllegalArgumentException If the originalPdf is null or doesn't exist
     */
    public void writeSummary(String summary, File originalPdf) throws IOException {
        if (originalPdf == null || !originalPdf.exists()) {
            throw new IllegalArgumentException("Original PDF file must exist");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String filename = originalPdf.getName();

        // Create markdown content
        String markdownContent = String.format(TEMPLATE,
                filename,
                filename,
                timestamp,
                summary
        );

        // Create markdown file path
        Path markdownPath = originalPdf.toPath().resolveSibling(filename + ".md");

        // Write the file
        Files.writeString(markdownPath, markdownContent);
    }
}