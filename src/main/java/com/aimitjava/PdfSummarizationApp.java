package com.aimitjava;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfSummarizationApp {
    private static final String ERROR_PREFIX = "❌ Error: ";

    private final PdfFileFinder fileFinder;
    private final PdfDocumentHandler documentHandler;
    private final Summarizer summarizer;
    private final MarkdownWriter writer;

    public PdfSummarizationApp() {
        this.fileFinder = new PdfFileFinder();
        this.documentHandler = new PdfDocumentHandler();
        this.summarizer = new OpenAiSummarizer();
        this.writer = new MarkdownWriter();
    }

    // Constructor for testing with mocked dependencies
    PdfSummarizationApp(PdfFileFinder fileFinder, PdfDocumentHandler documentHandler,
                        Summarizer summarizer, MarkdownWriter writer) {
        this.fileFinder = fileFinder;
        this.documentHandler = documentHandler;
        this.summarizer = summarizer;
        this.writer = writer;
    }

    private static void logError(String message) {
        System.err.println(ERROR_PREFIX + message);
    }

    public void run() {
        try {
            runInternal();
        } catch (Exception e) {
            logError(e.getMessage());
            throw new ApplicationTerminationException(e.getMessage(), 1);
        }
    }

    private void runInternal() {
        try {
            // Validate environment
            EnvValidator.validateApiKey();

            // Find PDF files
            List<File> pdfFiles = fileFinder.getPdfFiles();
            if (pdfFiles.isEmpty()) {
                System.out.println("No PDFs found in ./pdfs/");
                return;
            }

            // Process each PDF
            for (File pdf : pdfFiles) {
                processPdf(pdf);
            }
        } catch (MissingApiKeyException e) {
            throw new ApplicationTerminationException(e.getMessage(), 1);
        } catch (PdfTooLargeException e) {
            throw new ApplicationTerminationException(e.getMessage() + " Processing aborted.", 1);
        }
    }

    private void processPdf(File pdf) {
        // Validate file size
        fileFinder.validateFileSize(pdf);

        try {
            // Extract text
            String text = documentHandler.extractText(pdf);

            // Generate summary
            String summary = summarizer.summarize(text);

            // Write markdown file
            writer.writeSummary(summary, pdf);

            // Print success message
            System.out.println("✅ Summary for " + pdf.getName() + " created!");

        } catch (PdfExtractionException e) {
            String errorMsg = "Failed to extract text from " + pdf.getName() + ": " + e.getMessage();
            logError(errorMsg);
            throw new ApplicationTerminationException(errorMsg, 1);
        } catch (IOException e) {
            String errorMsg = "Failed to process " + pdf.getName() + ": " + e.getMessage();
            logError(errorMsg);
            throw new ApplicationTerminationException(errorMsg, 1);
        }
    }

    public static void main(String[] args) {
        try {
            new PdfSummarizationApp().run();
        } catch (ApplicationTerminationException e) {
            System.exit(e.getExitCode());
        } catch (Exception e) {
            logError("Application terminated due to error: " + e.getMessage());
            System.exit(1);
        }
    }
}