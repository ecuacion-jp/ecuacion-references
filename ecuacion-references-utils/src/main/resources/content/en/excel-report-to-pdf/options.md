# Options

`PdfGenerateOptions` holds optional parameters for PDF generation.
Build an instance using the Builder pattern.

## Available Options

| Option | Type | Description |
| --- | --- | --- |
| `excelPassword` | String or null | Password to open the source Excel file |
| `pdfPassword` | String or null | Password to protect the output PDF |

## Using the Builder

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;

PdfGenerateOptions options = PdfGenerateOptions.builder()
    .excelPassword("excel-pass")
    .pdfPassword("pdf-pass")
    .build();

ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```

Options that are not set default to `null` and are simply ignored.

## Password-Protected Excel File

To open a password-protected Excel file, set `excelPassword`:

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .excelPassword("secret123")
    .build();
```

## Password-Protecting the Output PDF

To require a password to open the generated PDF, set `pdfPassword`:

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .pdfPassword("view-only")
    .build();
```

## No Options

When no options are needed, pass `null` directly:

```java
ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, null);
```
