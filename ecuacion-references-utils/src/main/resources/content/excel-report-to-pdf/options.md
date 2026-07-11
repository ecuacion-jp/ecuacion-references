# Options

`PdfGenerateOptions` holds optional parameters for PDF generation.
Build an instance using the Builder pattern.

## Available Options

| Option | Type | Required | Description |
| --- | --- | --- | --- |
| `useSystemFonts` | boolean | Optional | When `true`, automatically searches OS and Office fonts. Default `false` |
| `regularFontPath` | Path or null | Required when `useSystemFonts` is `false` | Path to the TTF file used for regular text. Acts as a fallback when `useSystemFonts` is `true` |
| `boldFontPath` | Path or null | Optional | Path to the TTF file used for bold text. Falls back to `regularFontPath` if omitted |
| `excelPassword` | String or null | Optional | Password to open the source Excel file |
| `pdfPassword` | String or null | Optional | Password to open the output PDF (user password) |
| `pdfOwnerPassword` | String or null | Optional | Owner password for the output PDF. Defaults to `pdfPassword` when omitted |
| `dateLocale` | Locale or null | Optional | Locale for date format resolution. Defaults to the JVM default locale |

## Using the Builder

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;

PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .boldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // optional
    .excelPassword("excel-pass")
    .pdfPassword("pdf-pass")
    .build();

ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```

`regularFontPath` is required when `useSystemFonts` is `false` (default).
All other options are optional.

## System Fonts (`useSystemFonts`)

With `useSystemFonts(true)`, the library searches the OS font directories
(including fonts installed by Microsoft Office) for the workbook's default font.

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .useSystemFonts(true)
    .build();
```

If no matching system font is found, a `PdfGenerateException` is thrown.
You can also set `regularFontPath` as a fallback:

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .useSystemFonts(true)
    .regularFontPath(Path.of("/path/to/fallback.ttf"))  // fallback
    .build();
```

> **Font licensing notice:** The located system font is embedded in the output PDF.
> Confirm that the font's licence permits embedding and distribution before enabling this option.

## Font File

Specify a TTF font file in `regularFontPath`.
For documents containing Japanese text, use a Japanese-compatible font (e.g. Noto Sans JP).

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .boldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // optional
    .build();
```

## Password-Protected Excel File

To open a password-protected Excel file, set `excelPassword`:

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/regular.ttf"))
    .excelPassword("secret123")
    .build();
```

## Password-Protecting the Output PDF

### User Password (`pdfPassword`)

To require a password to open the generated PDF, set `pdfPassword`:

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/regular.ttf"))
    .pdfPassword("view-only")
    .build();
```

### Owner Password (`pdfOwnerPassword`)

PDFs support two distinct passwords:

| Password | Role |
| --- | --- |
| User password (`pdfPassword`) | Required to open the PDF |
| Owner password (`pdfOwnerPassword`) | Required to modify the PDF's security settings (e.g. add print or copy restrictions using an external tool) |

When `pdfOwnerPassword` is omitted, `pdfPassword` is used as the owner password as well — meaning anyone who can open the PDF can also modify its security settings.

Set `pdfOwnerPassword` separately when the person generating the PDF and the person managing security restrictions are different — for example, a system that generates the PDF with `pdfPassword` for end-user access, while an administrator later applies print/copy restrictions using a separately managed `pdfOwnerPassword`.

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/regular.ttf"))
    .pdfPassword("view-only")          // password to open the PDF
    .pdfOwnerPassword("admin-secret")  // password to modify security settings
    .build();
```

> **Note:** This library does not itself apply print or copy restrictions to the output PDF.
> To restrict operations, use an external tool (e.g. `qpdf` or Adobe Acrobat) on the generated PDF.
> The owner password will be required to make those changes.

## Date Locale (`dateLocale`)

Specifies the locale used to resolve date cell formats.
When not set, `Locale.getDefault()` is used.

```java
import java.util.Locale;

PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/font.ttf"))
    .dateLocale(Locale.JAPAN)
    .build();
```
