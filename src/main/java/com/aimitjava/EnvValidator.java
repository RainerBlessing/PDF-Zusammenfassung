package com.aimitjava;
public class EnvValidator {
    private EnvValidator() {
        // prevent instantiation
    }

    public static void validateApiKey() {
       String apiKey = System.getProperty("OPENAI_API_KEY");
       if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new MissingApiKeyException("OPENAI_API_KEY environment variable is not set");
        }
    }
}
