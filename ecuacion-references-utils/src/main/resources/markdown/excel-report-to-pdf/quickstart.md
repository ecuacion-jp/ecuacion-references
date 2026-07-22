## `ExcelToPdfUtil.generate()`

The main method for generating a PDF.

```java
public static void generate(
    Path excelPath,
    List<String> sheetNames,
    Path outputPath,
    PdfGenerateOptions options) throws PdfGenerateException
```

| Argument | Description |
| --- | --- |
| `excelPath` | Path to the source Excel file |
| `sheetNames` | Sheet names to include in the PDF (in order) |
| `outputPath` | Path for the output PDF file |
| `options` | PDF generation options including font path (required) |

## Code Example

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;
import jp.ecuacion.util.pdf.excel.report.util.ExcelToPdfUtil;
import jp.ecuacion.util.pdf.excel.report.exception.PdfGenerateException;
import java.nio.file.Path;
import java.util.List;

Path excelPath  = Path.of("/path/to/report.xlsx");
Path outputPath = Path.of("/path/to/output.pdf");

PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .build();

try {
    ExcelToPdfUtil.generate(
        excelPath,
        List.of("Cover", "Details"),   // sheet names to convert
        outputPath,
        options);
} catch (PdfGenerateException ex) {
    System.err.println("PDF generation failed: " + ex.getMessage());
}
```

> **Note:** `builderForSystemFonts()` searches for fonts installed locally (OS and
> Microsoft Office fonts). If no matching font is found, a `PdfGenerateException`
> is thrown.
>
> **Caution:** System fonts are embedded in the output PDF. Make sure the
> font's license permits embedding and redistribution before using it this way.

## Multiple Sheets

Pass multiple sheet names to produce a multi-page PDF in the specified order.
Each sheet's print area determines how many PDF pages are generated.

```java
ExcelToPdfUtil.generate(
    excelPath,
    List.of("Cover", "Summary", "Detail 1", "Detail 2"),
    outputPath,
    options);
```
