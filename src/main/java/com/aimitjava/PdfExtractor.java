package com.aimitjava;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfExtractor {

    public String extractText(File pdf) {
        if (!pdf.exists()) {
            throw new IllegalArgumentException("PDF file does not exist: " + pdf.getPath());
        }

        try (PDDocument document = PDDocument.load(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // If the text is null, return empty string
            return text != null ? text.trim() : "";

        } catch (IOException e) {
            throw new PdfExtractionException(
                    String.format("Failed to extract text from PDF: %s - %s",
                            pdf.getName(),
                            e.getMessage()
                    ),
                    e
            );
        }
    }
}
