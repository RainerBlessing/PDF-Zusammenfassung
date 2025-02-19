package com.aimitjava;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class EnvValidatorTest {

    @Test
    void shouldThrowExceptionWhenApiKeyNotSet() {
        System.clearProperty("OPENAI_API_KEY");  // Remove the environment variable

        try {
            EnvValidator.validateApiKey();
            org.junit.jupiter.api.Assertions.fail("Expected MissingApiKeyException to be thrown");
        } catch (MissingApiKeyException exception) {
            assertThat(exception.getMessage(), is("OPENAI_API_KEY environment variable is not set"));
        }
    }

    @Test
    void shouldNotThrowExceptionWhenApiKeyIsSet() {
        System.setProperty("OPENAI_API_KEY", "dummy-api-key");  // Set a test value

        boolean exceptionThrown = false;
        try {
            EnvValidator.validateApiKey();
        } catch (MissingApiKeyException e) {
            exceptionThrown = true;
        }

        assertThat(exceptionThrown, is(false));

        System.clearProperty("OPENAI_API_KEY");  // Clean up
    }
}