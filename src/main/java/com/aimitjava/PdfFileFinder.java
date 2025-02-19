package com.aimitjava;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfFileFinder {
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB in bytes
    private static final String PDF_DIRECTORY = "./pdfs/";

    public List<File> getPdfFiles() {
        File directory = new File(PDF_DIRECTORY);
        List<File> pdfFiles = new ArrayList<>();

        if (!directory.exists() || !directory.isDirectory()) {
            return pdfFiles;
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files != null) {
            Collections.addAll(pdfFiles, files);
        }

        return pdfFiles;
    }

    public void validateFileSize(File pdf) {
        if (!pdf.exists()) {
            throw new IllegalArgumentException("File does not exist: " + pdf.getPath());
        }

        if (pdf.length() > MAX_FILE_SIZE_BYTES) {
            throw new PdfTooLargeException(
                    String.format("PDF file exceeds maximum size of 5 MB: %s (%.2f MB)",
                            pdf.getName(),
                            pdf.length() / (1024.0 * 1024.0)
                    )
            );
        }
    }
}