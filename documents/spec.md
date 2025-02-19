# **Developer-Ready Specification: Automated PDF Summarization with LangChain4j & OpenAI GPT**

## **1. Overview**

This application processes **all PDFs from a fixed directory**, extracts their full text, generates an **abstractive summary** using **OpenAI GPT**, and saves the summary as a **Markdown file** in the same directory.

## **2. Requirements**

### **2.1 Functional Requirements**

- Read **all PDFs** from a specified directory (`./pdfs/`).
- Extract **full text** from each PDF.
- Generate a **maximum 10-sentence abstractive summary** using OpenAI GPT.
- Save the summary as a **Markdown (`.md`) file** in the same directory as the original PDF.
- Output **progress messages in the console**.
- **Terminate execution upon errors** (e.g., API failures, file size limits).

### **2.2 Non-Functional Requirements**

- Sequential processing (no multithreading).
- No external dependencies beyond **LangChain4j** and **OpenAI API**.
- Minimal logging (console only, no log files).
- Fixed directory path (`./pdfs/`, cannot be changed via configuration).
- No subdirectory traversal (only processes files in the root `./pdfs/` directory).

## **3. Architecture**

### **3.1 System Components**

- **PDF Reader**: Extracts text from PDFs.
- **Summarization Engine**: Calls OpenAI GPT API to generate an abstractive summary.
- **File Manager**: Handles reading/writing Markdown summaries.
- **CLI Interface**: Displays progress and errors in the console.

### **3.2 Processing Flow**

1. **Application Start:**

   - Validates the existence of the OpenAI API key (from `OPENAI_API_KEY` environment variable).
   - If missing, application **terminates with an error**.

2. **PDF Discovery & Validation:**

   - Reads all `.pdf` files in `./pdfs/`.
   - **Skips files larger than 5MB** and **terminates execution**.
   - Skips processing if no PDFs are found.

3. **Text Extraction & Summarization:**

   - Extracts full text from each PDF.
   - Sends extracted text to **OpenAI GPT**, requesting a **maximum 10-sentence summary**.
   - If OpenAI API fails, **terminates execution with an error**.

4. **Markdown File Generation:**

   - Saves summary in `./pdfs/{filename}.md` with the following format:

     ```markdown
     # Summary for {filename}

     **Original File:** {filename}.pdf
     **Generated on:** {timestamp}

     ---

     ## Summary

     {Generated Summary}

     ---

     _Generated using LangChain4j & OpenAI GPT_
     ```

5. **Completion & Exit:**
   - Outputs success message to the console:
     ```
     ✅ Summary for {filename}.pdf successfully saved!
     ```
   - Terminates execution automatically when all files are processed.

## **4. Data Handling**

### **4.1 Input Data**

- PDF files **from `./pdfs/` directory**.
- **No subdirectories** are scanned.
- **Max file size: 5MB** (larger files trigger termination).

### **4.2 Output Data**

- Markdown summaries (`.md`) saved in the same directory.
- **No storage of extracted full text** beyond summarization.
- **No logs beyond console output**.

## **5. Error Handling**

### **5.1 OpenAI API Errors**

- If API request fails, **terminate execution with an error message**.
- Example:
  ```
  ❌ Error: Failed to generate summary for {filename}.pdf. Application is terminating.
  ```

### **5.2 Missing API Key**

- Checked **at startup**.
- If missing, **terminate execution with an error**:
  ```
  ❌ Error: Missing OpenAI API key. Set the OPENAI_API_KEY environment variable.
  ```

### **5.3 File Handling Errors**

- **File Not Found / No PDFs:** Application simply exits.
- **PDF Too Large (>5MB):**
  - **Terminates execution**.
  - Example:
    ```
    ❌ Error: File {filename}.pdf exceeds 5MB. Processing aborted.
    ```

## **6. Configuration & Environment**

### **6.1 Fixed Configuration**

- **PDF Directory:** `./pdfs/`
- **Max PDF Size:** 5MB
- **Output Format:** Markdown (`.md`)

### **6.2 OpenAI GPT API Configuration**

- **Model:** Configurable in `application.properties`.
- **API Key:** Retrieved from the environment variable `OPENAI_API_KEY`.
- **Max Sentences:** 10 (relies on OpenAI to enforce this limit).

## **7. Testing Plan**

### **7.1 Unit Tests**

- **PDF Text Extraction**: Verify text can be extracted correctly.
- **Markdown File Generation**: Ensure correct file formatting and content.
- **OpenAI GPT Call**: Mock API response to validate correct summarization handling.

### **7.2 Integration Tests**

- **Process Various PDFs**: Test different file sizes and content.
- **Missing API Key**: Ensure application exits correctly.
- **Large PDF Handling**: Ensure the app terminates on files >5MB.

### **7.3 Edge Cases**

- **Empty PDF**: Ensure app handles gracefully.
- **OpenAI API Timeout**: Simulate failure.
- **Corrupt PDF File**: Ensure app does not crash.

## **8. Future Enhancements**

This system is designed for **simplicity and reliability**. Future improvements could include:

- 🔹 **Configurable directory path** via `application.properties`.
- 🔹 **Background directory monitoring** for new PDFs.
- 🔹 **Parallel processing** for faster summarization.
- 🔹 **Storage of full extracted text** for auditing.
- 🔹 **Support for other document types** (e.g., Word, TXT).

## **9. Conclusion**

This specification provides **a clear roadmap** for implementation. The focus is on **stability, simplicity, and robustness**, ensuring a **developer can start coding immediately**. 🚀

---

This document is **ready for handoff** to a developer. Let me know if you need refinements! 😊
