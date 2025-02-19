You are a code-generation assistant. Please create a new Java project for me, with the following requirements:

1. Use Gradle as the build tool.
2. Use hamcrest and mockito for testing.
3. Create a minimal structure for a console application.
4. Provide a single “HelloWorldTest” to confirm that the testing framework is configured correctly.
5. Implement a class “EnvValidator” with a static method “validateApiKey()” that:

   - Checks if the environment variable OPENAI_API_KEY is set.
   - If not set, throws a custom exception “MissingApiKeyException”.

6. Create tests to ensure:
   - The exception is thrown when OPENAI_API_KEY is not set.
   - No exception is thrown when OPENAI_API_KEY is set.

Note: Use only the libraries needed to pass these tests. Do not add the entire LangChain4j library yet. The focus is on laying the project foundation and verifying environment variable checks.
