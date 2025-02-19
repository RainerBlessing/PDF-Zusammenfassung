Continue from the existing codebase. Now, add PDF text extraction:

1. Create a “PdfExtractor” class with a method “extractText(File pdf)” that returns the full text content of a given PDF.
   - You can use a suitable library (like Apache PDFBox or any library available in our environment) to do this extraction.
   - If the PDF is corrupt or extraction fails, throw a “PdfExtractionException”.

2. Write a “PdfExtractorTest” class to ensure:
   - It successfully extracts text from a valid PDF.
   - It throws PdfExtractionException on a corrupt or unreadable PDF.
   - It returns an empty string if the PDF is valid but has no text.

3. Ensure no other code is changed except where needed to integrate this new class.
