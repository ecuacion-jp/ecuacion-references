# Quick Start

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
import java.nio.file.Paths;
import java.util.List;

Path excelPath  = Paths.get("/path/to/report.xlsx");
Path outputPath = Paths.get("/path/to/output.pdf");

PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))
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

## When a Sheet Name Does Not Exist

If a specified sheet name does not exist in the Excel file, a
`PdfGenerateException` is thrown.

```java
// "NonExistentSheet" not found → PdfGenerateException
ExcelToPdfUtil.generate(excelPath, List.of("NonExistentSheet"), outputPath, options);
```
