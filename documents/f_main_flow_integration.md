At this stage, we wire everything together in a main pipeline. Continue from the existing codebase.

1. Create a “PdfSummarizationApp” class (or “Main”) with a “run” method that:
    - Calls EnvValidator to confirm OPENAI_API_KEY is set.
    - Uses PdfFileFinder to get all PDF files.
        - If none found, print a message like “No PDFs found in ./pdfs/.” and exit.
        - For each PDF file, call validateFileSize().
            - If it’s too large, print an error and terminate the entire application.
    - For each PDF file that passes size validation:
        - Extract text using PdfExtractor.
        - Summarize text using OpenAiSummarizer (still the mocked version).
        - Write the summary with MarkdownWriter.
        - Print a success message “✅ Summary for {filename} created!”

2. Write an “IntegrationTest” that:
    - Places a small sample PDF in ./pdfs/
    - Runs “PdfSummarizationApp.run()”
    - Verifies a corresponding .md file is created with the placeholder summary.

3. Do not yet integrate real OpenAI GPT calls. Just ensure the pipeline works with the current mocks.
