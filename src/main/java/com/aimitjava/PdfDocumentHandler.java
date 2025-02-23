package com.aimitjava;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PdfDocumentHandler {
    private final DocumentParser parser;

    public PdfDocumentHandler() {
        this.parser = new ApachePdfBoxDocumentParser();
    }

    public Document parseDocument(File pdfFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(pdfFile)) {
            return parser.parse(fis);
        }
    }

    public String extractText(File pdfFile) throws IOException {
        Document document = parseDocument(pdfFile);
        return document.text();
    }

    public String getMetadata(File pdfFile) throws IOException {
        Document document = parseDocument(pdfFile);
        return document.metadata().toString();
    }
}