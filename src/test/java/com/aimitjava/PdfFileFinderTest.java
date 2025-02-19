package com.aimitjava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PdfFileFinderTest {

    private PdfFileFinder finder;
    private File pdfDirectory;

    @BeforeEach
    void setUp() throws IOException {
        finder = new PdfFileFinder();
        pdfDirectory = new File("./pdfs");
        if (!pdfDirectory.exists() && !pdfDirectory.mkdirs()) {
            throw new IOException("Failed to create PDF directory: " + pdfDirectory.getPath());
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (pdfDirectory.exists()) {
            File[] files = pdfDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getPath());
                    }
                }
            }
            if (!pdfDirectory.delete()) {
                throw new IOException("Failed to delete PDF directory: " + pdfDirectory.getPath());
            }
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoPdfsPresent() {
        List<File> pdfFiles = finder.getPdfFiles();
        assertThat(pdfFiles, is(empty()));
    }

    @Test
    void shouldReturnCorrectNumberOfPdfFiles() throws IOException {
        // Create some test PDF files
        File test1 = new File(pdfDirectory, "test1.pdf");
        File test2 = new File(pdfDirectory, "test2.pdf");
        File notPdf = new File(pdfDirectory, "notapdf.txt");

        if (!test1.createNewFile()) {
            throw new IOException("Failed to create test file: " + test1.getPath());
        }
        if (!test2.createNewFile()) {
            throw new IOException("Failed to create test file: " + test2.getPath());
        }
        if (!notPdf.createNewFile()) {
            throw new IOException("Failed to create test file: " + notPdf.getPath());
        }

        List<File> pdfFiles = finder.getPdfFiles();
        assertThat(pdfFiles, hasSize(2));
        assertThat(
                pdfFiles.stream().map(File::getName).toList(),
                containsInAnyOrder("test1.pdf", "test2.pdf")
        );
    }

    @Test
    void shouldAcceptFileUnder5MB() throws IOException {
        File smallPdf = new File(pdfDirectory, "small.pdf");
        byte[] bytes = new byte[1024 * 1024]; // 1 MB
        Files.write(smallPdf.toPath(), bytes);

        try {
            finder.validateFileSize(smallPdf);
            assertThat(true, is(true)); // If we get here, no exception was thrown
        } catch (PdfTooLargeException e) {
            org.junit.jupiter.api.Assertions.fail("Should not throw exception for small file");
        }
    }

    @Test
    void shouldThrowExceptionForFileOver5MB() throws IOException {
        File largePdf = new File(pdfDirectory, "large.pdf");
        byte[] bytes = new byte[6 * 1024 * 1024]; // 6 MB
        Files.write(largePdf.toPath(), bytes);

        try {
            finder.validateFileSize(largePdf);
            org.junit.jupiter.api.Assertions.fail("Expected PdfTooLargeException to be thrown");
        } catch (PdfTooLargeException e) {
            assertThat(e.getMessage(), containsString("exceeds maximum size of 5 MB"));
            assertThat(e.getMessage(), containsString("large.pdf"));
        }
    }
}
