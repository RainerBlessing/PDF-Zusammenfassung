package com.aimitjava;

public class EnvValidator {
    private EnvValidator() {
        // prevent instantiation
    }

    public static void validateApiKey() {
        String apiKey = Configuration.getInstance().getOpenAiApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new MissingApiKeyException("OpenAI API key is not set in environment or properties");
        }
    }
}