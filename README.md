# PDF Summarization with OpenAI GPT

This Java application processes PDF files, extracts their text content, and generates concise summaries using OpenAI's GPT model through LangChain4j. Each summary is saved as a Markdown file alongside the original PDF.

## Features

- 📂 Automatic PDF discovery and processing
- 📝 Text extraction from PDF files
- 🤖 GPT-powered text summarization
- 📊 Smart text chunking for large documents
- 🔍 Environment and configuration management
- ✨ Markdown summary output

## Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- OpenAI API key

## Setup

1. Clone the repository:
```bash
git clone [repository-url]
cd [repository-name]
```

2. Set your OpenAI API key:
```bash
export OPENAI_API_KEY=your-api-key-here
```

Or add it to `src/main/resources/application.properties`:
```properties
openai.api.key=your-api-key-here
```

## Configuration

You can configure the application through environment variables or `application.properties`:

```properties
# OpenAI Configuration
openai.api.key=             # Your OpenAI API key
openai.model.name=gpt-3.5-turbo
openai.temperature=0.7

# PDF Processing
pdf.max.size.mb=5
pdf.directory=./pdfs/
```

Environment variables take precedence over properties file settings.

## Usage

1. Place your PDF files in the `pdfs` directory
2. Run the application:
```bash
./gradlew runApp
```

The application will:
- Process each PDF file under 5MB
- Generate a summary using GPT
- Create a .md file next to each PDF with the summary

Example output:
```
pdfs/
├── document.pdf
└── document.pdf.md
```

## Building

Build the project:
```bash
./gradlew clean build
```

Run tests:
```bash
./gradlew test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/aimitjava/
│   │   ├── Configuration.java        # Application configuration
│   │   ├── EnvironmentProvider.java  # Environment variable handling
│   │   ├── PdfExtractor.java        # PDF text extraction
│   │   ├── OpenAiSummarizer.java    # GPT integration
│   │   └── ...
│   └── resources/
│       └── application.properties    # Default configuration
├── test/
│   └── java/com/aimitjava/
│       └── ...                      # Test classes
```

## Features in Detail

### PDF Processing
- Supports PDF files up to 5MB
- Extracts text while maintaining structure
- Handles various PDF formats

### Text Summarization
- Uses OpenAI's GPT-3.5-turbo model
- Smart text chunking for large documents
- Maximum 10-sentence summaries
- Maintains context across chunks

### Configuration Management
- Environment variable support
- Properties file configuration
- Runtime configuration changes

## Error Handling

The application handles various error cases:
- Missing API key
- PDF size limits
- Extraction failures
- API rate limits
- Token limit exceeded

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Troubleshooting

### Common Issues

1. "No PDFs found":
    - Ensure PDFs are in the `pdfs` directory
    - Check file permissions

2. "API key not found":
    - Set OPENAI_API_KEY environment variable
    - Or configure in application.properties

3. "Token limit exceeded":
    - Large PDFs are automatically chunked
    - Check PDF content size

## License

[License information]

## Acknowledgments

- Apache PDFBox for PDF processing
- LangChain4j for OpenAI integration
- OpenAI for GPT API