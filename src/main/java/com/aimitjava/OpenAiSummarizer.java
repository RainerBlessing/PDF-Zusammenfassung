package com.aimitjava;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

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

    private final ChatLanguageModel model;
    private final DocumentSplitter splitter;

    public OpenAiSummarizer() {
        this(Configuration.getInstance());
    }

    OpenAiSummarizer(Configuration config) {
        this.model = OpenAiChatModel.builder()
                .apiKey(config.getOpenAiApiKey())
                .modelName(config.getOpenAiModelName())
                .temperature(config.getOpenAiTemperature())
                .build();

        // Configure document splitter with max tokens allowing for prompt space
        this.splitter = DocumentSplitters.recursive(8000, 500);
    }

    // Constructor for testing with mock model
    OpenAiSummarizer(ChatLanguageModel model) {
        this.model = model;
        this.splitter = DocumentSplitters.recursive(8000, 500);
    }

    @Override
    public String summarize(String fullText) {
        if (fullText == null || fullText.trim().isEmpty()) {
            return EMPTY_TEXT_SUMMARY;
        }

        // Create document and split if necessary
        Document document = Document.from(fullText);
        List<TextSegment> chunks = splitter.split(document);

        if (chunks.size() == 1) {
            String prompt = String.format(PROMPT_TEMPLATE, chunks.getFirst().text());
            return model.chat(prompt);
        }

        // Summarize each chunk
        List<String> chunkSummaries = chunks.stream()
                .map(chunk -> {
                    String prompt = String.format(PROMPT_TEMPLATE, chunk.text());
                    return model.chat(prompt);
                })
                .toList();

        // Create final summary from chunk summaries
        String combinedSummaries = String.join("\n\n", chunkSummaries);
        String finalPrompt = String.format(FINAL_SUMMARY_TEMPLATE, combinedSummaries);
        return model.chat(finalPrompt);
    }
}