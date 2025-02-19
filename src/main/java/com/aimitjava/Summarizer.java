package com.aimitjava;

public interface Summarizer {
    /**
     * Summarizes the provided text content.
     *
     * @param fullText The text to be summarized
     * @return A summary of the input text
     */
    String summarize(String fullText);
}
