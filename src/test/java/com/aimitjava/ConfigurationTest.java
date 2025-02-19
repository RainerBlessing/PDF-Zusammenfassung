package com.aimitjava;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

class ConfigurationTest {

    private EnvironmentProvider mockEnvironmentProvider;
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        mockEnvironmentProvider = Mockito.mock(EnvironmentProvider.class);
        EnvironmentProvider.setInstance(mockEnvironmentProvider);
        Configuration.reset();
    }

    @AfterEach
    void tearDown() {
        EnvironmentProvider.reset();
        Configuration.reset();
    }

    @Test
    void shouldLoadDefaultValues() {
        // Given
        when(mockEnvironmentProvider.getEnv(Mockito.anyString())).thenReturn(null);
        configuration = new Configuration(mockEnvironmentProvider);

        // Then
        assertThat(configuration.getOpenAiModelName(), is("gpt-3.5-turbo-test"));
        assertThat(configuration.getOpenAiTemperature(), is(0.5));
        assertThat(configuration.getPdfMaxSizeMb(), is(2));
        assertThat(configuration.getPdfDirectory(), is("./test-pdfs/"));
    }

    @Test
    void shouldPreferEnvironmentVariablesOverProperties() {
        // Given
        String testApiKey = "test-api-key-from-env";
        when(mockEnvironmentProvider.getEnv("OPENAI_API_KEY")).thenReturn(testApiKey);
        configuration = new Configuration(mockEnvironmentProvider);

        // Then
        assertThat(configuration.getOpenAiApiKey(), is(testApiKey));
    }

    @Test
    void shouldFallbackToPropertiesFileWhenEnvVarNotSet() {
        // Given
        when(mockEnvironmentProvider.getEnv("OPENAI_API_KEY")).thenReturn(null);
        configuration = new Configuration(mockEnvironmentProvider);

        // Then
        assertThat(configuration.getOpenAiApiKey(), is("test-key-from-properties"));
    }
}