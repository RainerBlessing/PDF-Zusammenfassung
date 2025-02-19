package com.aimitjava;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.ArrayList;
import java.util.List;

public class OpenAiSummarizer implements Summarizer {
    private static final String EMPTY_TEXT_SUMMARY = "No content to summarize.";
    private static final String PROMPT_TEMPLATE = """
        Please provide a concise summary of the following text in maximum 5 sentences.
        Focus on the main points and key information.
        
        Text to summarize:
        %s
        """;

    private static final String FINAL_SUMMARY_TEMPLATE = """
        Please provide a unified summary of the following summaries in maximum 10 sentences.
        Focus on connecting the main themes and key points.
        
        Summaries to combine:
        %s
        """;

    // Reduzierte Chunk-Größe für sicherere Verarbeitung
    private static final int MAX_CHUNK_SIZE = 7500;

    private final ChatLanguageModel model;
    private final Configuration config;

    // Konstruktoren bleiben unverändert
    public OpenAiSummarizer() {
        this(Configuration.getInstance());
    }

    OpenAiSummarizer(Configuration config) {
        this.config = config;
        this.model = OpenAiChatModel.builder()
                .apiKey(config.getOpenAiApiKey())
                .modelName(config.getOpenAiModelName())
                .temperature(config.getOpenAiTemperature())
                .build();
    }

    OpenAiSummarizer(ChatLanguageModel model, Configuration config) {
        this.model = model;
        this.config = config;
    }

    @Override
    public String summarize(String fullText) {
        // Behandlung von null oder leeren Eingaben
        if (fullText == null || fullText.trim().isEmpty()) {
            return EMPTY_TEXT_SUMMARY;
        }

        // Text in Chunks aufteilen
        List<String> chunks = splitIntoChunks(fullText);

        // Wenn nur ein Chunk, direkt zusammenfassen
        if (chunks.size() == 1) {
            String prompt = String.format(PROMPT_TEMPLATE, chunks.get(0));
            return model.generate(prompt);
        }

        // Jeden Chunk einzeln zusammenfassen
        List<String> chunkSummaries = new ArrayList<>();
        for (String chunk : chunks) {
            String prompt = String.format(PROMPT_TEMPLATE, chunk);
            chunkSummaries.add(model.generate(prompt));
        }

        // Finale Zusammenfassung der Chunk-Zusammenfassungen
        String combinedSummaries = String.join("\n\n", chunkSummaries);
        String finalPrompt = String.format(FINAL_SUMMARY_TEMPLATE, combinedSummaries);
        return model.generate(finalPrompt);
    }

    /**
     * Teilt Text in Chunks auf, die die maximale Größe nicht überschreiten
     * @param text Vollständiger Eingabetext
     * @return Liste von Textchunks
     */
    private List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\\n\\s*\\n");
        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) continue;

            // Chunk-Größe prüfen und ggf. speichern
            if (currentChunk.length() + paragraph.length() > MAX_CHUNK_SIZE && currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }


            // Sehr große Paragraphen aufteilen
            if (paragraph.length() > MAX_CHUNK_SIZE) {
                chunks.addAll(splitLargeParagraph(paragraph));
                continue;
            }

            // Paragraph zum aktuellen Chunk hinzufügen
            if (currentChunk.length() > 0) {
                currentChunk.append("\n\n");
            }
            currentChunk.append(paragraph);
        }

        // Letzten Chunk hinzufügen
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * Teilt sehr große Paragraphen in kleinere Chunks
     * @param paragraph Großer Paragraph
     * @return Liste von Chunk-Strings
     */
    private List<String> splitLargeParagraph(String paragraph) {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        // In Sätze aufteilen
        String[] sentences = paragraph.split("(?<=[.!?])\\s+");

        for (String sentence : sentences) {
            // Chunk-Größe prüfen
            if (currentChunk.length() + sentence.length() > MAX_CHUNK_SIZE) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }

            // Satz zum aktuellen Chunk hinzufügen
            currentChunk.append(sentence).append(" ");
        }

        // Letzten Chunk hinzufügen
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    public Configuration getConfig() {
        return config;
    }
}