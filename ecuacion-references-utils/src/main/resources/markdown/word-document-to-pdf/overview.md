`ecuacion-util-word-document-to-pdf` generates PDF files from Word documents (`.docx`, and also `.docm`, `.dotx` and `.dotm`:
macros are not run).
It reads the document with Apache POI and writes PDF with Apache PDFBox, without Microsoft Word.

It is meant for text-centered documents such as contracts and terms of service, and lays them
out as closely as possible to a PDF file exported by Word: where lines and pages break and where
each line is placed are verified against PDF files exported by Word for Mac.

## Key Features

- Paragraph and character formatting, styles, numbered headings and lists (e.g. `第1条`, `1.1`, `(1)`)
- Tables, headers / footers, page numbers, sections with their own page settings
- Tables of contents and cross-references: the page numbers are recalculated from the PDF layout, and link to the headings
- Headings are output as the outline (bookmarks) of the PDF
- Japanese line breaking rules (kinsoku, hanging punctuation, compression of punctuation)
- Automatically searches OS system fonts, or specify any TTF font file path (configured via `PdfGenerateOptions`)
- Supports password-protected Word files, and password-protecting the output PDF

Shapes, text boxes, charts, footnotes, multiple columns and vertical writing are not supported.
See [Features](/public/showMarkdown/page?id=word-document-to-pdf/features&lang=en) and
[Notes](/public/showMarkdown/page?id=word-document-to-pdf/notes&lang=en).

## Public API

| Class | Role |
| --- | --- |
| `WordToPdfUtil` | Main utility for PDF generation (`static` method) |
| `PdfGenerateOptions` | Fonts and passwords (Builder pattern) |
| `PdfGenerateException` | Common superclass of the exceptions of PDF generation (in `ecuacion-util-pdf-common`) |
