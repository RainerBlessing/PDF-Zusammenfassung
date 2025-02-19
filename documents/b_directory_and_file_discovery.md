Continue from the existing codebase. Now, add functionality to discover PDF files:

1. Create a class “PdfFileFinder” with a method “getPdfFiles()” that:
    - Looks for a directory named “./pdfs/”.
    - Returns a list of File objects for every file ending in “.pdf”.
    - If the directory is empty or non-existent, it returns an empty list.

2. In the same class, add a method “validateFileSize(File pdf)” that:
    - Checks if the file size is <= 5 MB.
    - If it is larger, throws a “PdfTooLargeException” to indicate we must terminate.

3. Create a test class “PdfFileFinderTest” that verifies:
    - getPdfFiles() returns an empty list when no PDFs are present.
    - getPdfFiles() returns the correct number of PDF files if present.
    - validateFileSize() behaves correctly for a file under 5 MB and for a file over 5 MB.

Ensure the new methods integrate smoothly with the existing project structure, reusing the existing test framework.
