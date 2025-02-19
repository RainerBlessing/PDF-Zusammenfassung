# TODO: Automated PDF Summarization Project

This checklist outlines all tasks needed to implement the automated PDF summarization tool using LangChain4j and OpenAI GPT.  
Mark each item as complete (`[x]`) once done.

---

## **Stage A: Basic Project Setup**

- [x] **Initialize Project Structure**
    - Create a new Java/Kotlin project with Gradle or Maven.
    - Set up your preferred directory structure (e.g., `src/main/java`, `src/test/java`).

- [x] **Create “Hello World” Test**
    - Add a simple unit test (e.g., `HelloWorldTest`) to verify the testing framework is configured.

- [x] **Add EnvValidator**
    - Implement an `EnvValidator` class with `validateApiKey()`:
        - Checks for the `OPENAI_API_KEY` environment variable.
        - Throws a custom `MissingApiKeyException` if absent.

- [x] **Write Tests for EnvValidator**
    - Test that an exception is thrown when `OPENAI_API_KEY` is missing.
    - Test that no exception is thrown when `OPENAI_API_KEY` is set.

---

## **Stage B: Directory & File Discovery**

- [x] **PdfFileFinder Class**
    - Implement `getPdfFiles()`:
        - Scans `./pdfs/` for files ending in `.pdf`.
        - Returns a list of `File` objects.
        - If directory is missing or empty, return an empty list.

- [x] **Validate File Size**
    - Implement `validateFileSize(File pdf)` in `PdfFileFinder`:
        - Checks if the PDF is <= 5MB.
        - Throws `PdfTooLargeException` if the file exceeds 5MB.

- [x] **Write Tests: PdfFileFinderTest**
    - Check that `getPdfFiles()` returns:
        - An empty list if `./pdfs/` is nonexistent or empty.
        - The correct number of `.pdf` files when present.
    - Check `validateFileSize()`:
        - Passes for files under 5MB.
        - Throws exception for files over 5MB.

---

## **Stage C: PDF Text Extraction**

- [ ] **PdfExtractor Class**
    - Implement `String extractText(File pdf)`:
        - Uses an appropriate library (e.g., PDFBox) to read PDF text.
        - If PDF is corrupt or unreadable, throw `PdfExtractionException`.
        - If PDF is valid but has no text, return an empty string.

- [ ] **Write Tests: PdfExtractorTest**
    - Successfully extract text from a valid PDF.
    - Throw `PdfExtractionException` on a corrupt/unreadable PDF.
    - Return empty string for an empty PDF.

---

## **Stage D: Summarization Integration (Mocked)**

- [ ] **Summarizer Interface**
    - Create `Summarizer` with `String summarize(String fullText)`.

- [ ] **OpenAiSummarizer (Mocked)**
    - Implement the `summarize` method to return a placeholder string, e.g.:
        - `"This is a mocked summary."`
    - Handle empty text by returning something like `"No content to summarize."`

- [ ] **Write Tests: OpenAiSummarizerTest**
    - Verify the placeholder string is returned when the method is called with normal text.
    - Verify a different placeholder or message when called with empty text.

---

## **Stage E: Markdown Output**

- [ ] **MarkdownWriter Class**
    - Implement `writeSummary(String summary, File originalPdf)`:
        - Creates a `.md` file in the **same directory** as `originalPdf`.
        - Names the file like `filename.pdf.md` if the original PDF is `filename.pdf`.
        - Writes the following format:

          ```markdown
          # Summary for {filename}
    
          **Original File:** {filename}.pdf  
          **Generated on:** {timestamp}
    
          ---
    
          ## Summary
    
          {summary}
    
          ---
    
          _Generated using LangChain4j & OpenAI GPT_
          ```

- [ ] **Write Tests: MarkdownWriterTest**
    - Confirm `.md` file is created in the correct location.
    - Verify the content (filename, timestamp, summary) is correctly formatted.

---

## **Stage F: Main Flow Integration**

- [ ] **Create PdfSummarizationApp (or Main)**
    - **Startup**:
        - Call `EnvValidator.validateApiKey()` to ensure `OPENAI_API_KEY` is set.
        - If missing, terminate with a helpful console message.
    - **PDF Discovery**:
        - Use `PdfFileFinder` to locate `.pdf` files.
        - If none found, print “No PDFs found in ./pdfs/” and exit.
        - For each PDF, call `validateFileSize()`:
            - If any file is >5MB, throw error and terminate the entire application.
    - **Extraction & Summarization**:
        - Extract text using `PdfExtractor`.
        - Summarize with `OpenAiSummarizer` (mock).
        - Write summary with `MarkdownWriter`.
        - Print a success message (e.g., `✅ Summary for {filename} created!`).

- [ ] **IntegrationTest**
    - Place sample PDF(s) in `./pdfs/`.
    - Run `PdfSummarizationApp.run()`.
    - Check that each PDF produces a corresponding `.md` file with the mocked summary.

---

## **Stage G: Final Polishing & Real GPT Integration**

- [ ] **Replace Mock with Real OpenAI GPT**
    - In `OpenAiSummarizer`, implement a real call to OpenAI GPT with LangChain4j:
        - Retrieve `OPENAI_API_KEY` from environment.
        - Prompt for a max 10-sentence summary.
        - Handle network/timeout/API errors by throwing an exception if needed.

- [ ] **Update OpenAiSummarizerTest**
    - Incorporate or at least partially test real GPT calls (if environment is available).
    - Continue to offer mock testing if direct API calls are impractical for CI.

- [ ] **Refine Logging & Error Handling**
    - Ensure error messages match the specification (log to console, then terminate).
    - Verify that partial progress is not left incomplete if a file is large or an API call fails.

- [ ] **Final IntegrationTest**
    - Use a set of sample PDFs (valid, empty, possibly corrupt).
    - Validate correct `.md` summaries are generated, or that the application terminates gracefully on errors.

- [ ] **Code Cleanup**
    - Ensure no orphan classes, methods, or test scaffolding remain.
    - Check all docs, readme, and references are updated.

---

## **Additional Considerations**

1. **Edge Cases**
    - [ ] Large PDF >5MB triggers immediate termination.
    - [ ] Missing or invalid PDFs produce appropriate messages.
    - [ ] No PDF files found results in a simple exit with a console message.

2. **Testing**
    - [ ] Confirm all unit tests pass.
    - [ ] Confirm all integration tests pass.
    - [ ] Perform manual testing if feasible.

3. **Project Handoff**
    - [ ] Provide or update `README.md` instructions for running the app.
    - [ ] Include environment variable notes (`OPENAI_API_KEY`).

When all boxes are checked, you have a fully working PDF Summarization application with robust error handling, logging, and tested end-to-end functionality!
