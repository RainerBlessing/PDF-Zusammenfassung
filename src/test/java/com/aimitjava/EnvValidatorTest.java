package com.aimitjava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class EnvValidatorTest {

    @Test
    @DisabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
    void shouldThrowExceptionWhenApiKeyNotSet() {
        try {
            EnvValidator.validateApiKey();
            Assertions.fail("Expected MissingApiKeyException to be thrown");
        } catch (MissingApiKeyException exception) {
            assertThat(exception.getMessage(), is("OPENAI_API_KEY environment variable is not set"));
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
    void shouldNotThrowExceptionWhenApiKeyIsSet() {
        boolean exceptionThrown = false;
        try {
            EnvValidator.validateApiKey();
        } catch (MissingApiKeyException e) {
            exceptionThrown = true;
        }

        assertThat(exceptionThrown, is(false));
    }
}