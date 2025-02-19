Now we need to generate Markdown summary files. Continue from the existing code.

1. Create a “MarkdownWriter” class with a method:
    - “writeSummary(String summary, File originalPdf)”
      This method should:
    - Create a .md file in the same directory as “originalPdf”.
    - Name the file similarly (e.g., “example.pdf” -> “example.pdf.md”).
    - Write the following structure:

      # Summary for {filename}

      **Original File:** {filename}.pdf  
      **Generated on:** {timestamp}

      ---

      ## Summary

      {summary}

      ---

      _Generated using LangChain4j & OpenAI GPT_

2. Write a “MarkdownWriterTest” that:
    - Mocks or uses a temporary file to verify the resulting .md file is created.
    - Confirms the file content is correct (filename, timestamp, summary).

3. Keep everything else the same; only add this Markdown-writing functionality.
