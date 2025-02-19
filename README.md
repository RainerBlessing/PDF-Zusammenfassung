# PDF Summarization Project

This Java project provides automated PDF summarization capabilities using OpenAI's GPT model through LangChain4j. It processes PDFs from a specified directory, extracts their text content, and generates concise summaries.

## Features

- Environment variable validation for API keys
- PDF file discovery and size validation
- Text extraction from PDF files
- Text summarization (currently mocked, real GPT integration planned)
- Markdown summary output generation

## Project Structure

```
src/
├── main/java/com/aimitjava/
│   ├── EnvValidator.java           # Validates environment variables
│   ├── MissingApiKeyException.java # Custom exception for missing API key
│   ├── PdfFileFinder.java         # Handles PDF file discovery
│   ├── PdfTooLargeException.java  # Custom exception for oversized PDFs
│   ├── PdfExtractor.java          # Extracts text from PDFs
│   ├── PdfExtractionException.java # Custom exception for extraction failures
│   ├── Summarizer.java            # Interface for text summarization
│   └── OpenAiSummarizer.java      # Implementation of summarization (currently mocked)
└── test/java/com/aimitjava/
    ├── EnvValidatorTest.java
    ├── PdfFileFinderTest.java
    ├── PdfExtractorTest.java
    └── OpenAiSummarizerTest.java
```

## Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- OpenAI API key (for future GPT integration)

## Setup

1. Clone the repository:
```bash
git clone [repository-url]
```

2. Build the project:
```bash
./gradlew build
```

3. Set up environment variables:
```bash
export OPENAI_API_KEY=your-api-key-here
```

## Usage

1. Place PDF files in the `./pdfs/` directory
2. Ensure files are under 5MB in size
3. Run the application (implementation pending)

## Running Tests

Execute all tests:
```bash
./gradlew test
```

## Limitations

- Maximum PDF file size: 5MB
- Currently uses mocked summarization
- Real GPT integration pending

## Dependencies

- Apache PDFBox: PDF text extraction
- JUnit 5: Testing framework
- Hamcrest: Test assertions
- Mockito: Test mocking

## Future Enhancements

- [ ] Integration with OpenAI GPT
- [ ] Markdown summary output
- [ ] Batch processing capabilities
- [ ] Progress reporting
- [ ] Configuration options

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

[License information pending]