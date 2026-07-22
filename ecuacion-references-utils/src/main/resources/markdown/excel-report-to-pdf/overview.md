`ecuacion-util-excel-report-to-pdf` generates PDF files from Excel files based
on each sheet's print settings (print area, page breaks). It reads Excel with
Apache POI and writes PDF with Apache PDFBox.

## Key Features

- Outputs each sheet's print area as PDF pages
- Reproduces cell values, text styles, background colours, borders, and merged cells
- Automatically searches OS system fonts, or specify any TTF font file path (configured via `PdfGenerateOptions`)
- Supports password-protected Excel files
- Supports password-protecting the output PDF

## Public API

| Class | Role |
| --- | --- |
| `ExcelToPdfUtil` | Main utility for PDF generation (`static` method) |
| `PdfGenerateOptions` | Optional parameters such as passwords (Builder pattern) |
| `PdfGenerateException` | Exception representing errors during PDF generation |
