# excel-report-to-pdf Overview

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

For dependency setup, see the [Setup](/public/showMarkdown/page?id=excel-report-to-pdf/setup&lang=en) page.

## Notes

### Handling Large Files

Apache POI loads the entire Excel file (xlsx) into memory when opening it.
This library does not enforce any file size limit internally, so processing
very large files may cause out-of-memory errors.

When processing files uploaded by users, it is recommended to check the file
size before calling this library:

```java
if (Files.size(excelPath) > 50 * 1024 * 1024) { // e.g. 50 MB
    throw new IllegalArgumentException("File size exceeds the allowed limit");
}
ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```
