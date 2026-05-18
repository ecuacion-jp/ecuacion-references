# Basic Usage

## `ExcelToPdfUtil.generate()`

The main method for generating a PDF.

```java
public static void generate(
    Path excelPath,
    List<String> sheetNames,
    Path outputPath,
    @Nullable PdfGenerateOptions options) throws PdfGenerateException
```

| Argument | Description |
| --- | --- |
| `excelPath` | Path to the source Excel file |
| `sheetNames` | Sheet names to include in the PDF (in order) |
| `outputPath` | Path for the output PDF file |
| `options` | Optional parameters (pass `null` if not needed) |

## Code Example

```java
import jp.ecuacion.util.pdf.excel.report.util.ExcelToPdfUtil;
import jp.ecuacion.util.pdf.excel.report.exception.PdfGenerateException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

Path excelPath  = Paths.get("/path/to/report.xlsx");
Path outputPath = Paths.get("/path/to/output.pdf");

try {
    ExcelToPdfUtil.generate(
        excelPath,
        List.of("Cover", "Details"),   // sheet names to convert
        outputPath,
        null);                          // no options
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
    null);
```

## When a Sheet Name Does Not Exist

If a specified sheet name does not exist in the Excel file, a
`PdfGenerateException` is thrown.

```java
// "NonExistentSheet" not found → PdfGenerateException
ExcelToPdfUtil.generate(excelPath, List.of("NonExistentSheet"), outputPath, null);
```
