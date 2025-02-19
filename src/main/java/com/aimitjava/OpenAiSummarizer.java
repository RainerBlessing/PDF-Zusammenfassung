package com.aimitjava;

public class OpenAiSummarizer implements Summarizer {

    private static final String EMPTY_TEXT_SUMMARY = "No content to summarize.";
    private static final String MOCK_SUMMARY = "This is a mocked summary. In a future implementation, " +
            "this will be replaced with an actual GPT-generated summary.";

    @Override
    public String summarize(String fullText) {
        if (fullText == null || fullText.trim().isEmpty()) {
            return EMPTY_TEXT_SUMMARY;
        }

        // In a future implementation, this will be replaced with actual GPT API calls
        return MOCK_SUMMARY;
    }
}
