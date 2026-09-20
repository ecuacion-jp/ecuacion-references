`PdfGenerateOptions` holds optional parameters for PDF generation.
Build an instance using the Builder pattern, starting from one of two static factory methods
depending on how the rendering font should be resolved.

## Available Options

| Option | Type | Required | Description |
| --- | --- | --- | --- |
| entry point | `builderForSystemFonts()` or `builderForExplicitFont(Path)` | Required | Selects the font resolution mode. See [System Fonts](#system-fonts-builderforsystemfonts) below |
| `addRegularFontPath` | Path | For `builderForExplicitFont`, registering the first entry is not needed (the given argument is automatically registered as the first entry); calling this adds further entries beyond it. Optional, repeatable for `builderForSystemFonts` | Registers a TTF file used for regular text, tried in registration order. See [Fallback Fonts](#fallback-fonts-addregularfontpath--addboldfontpath) below |
| `addBoldFontPath` | Path | Optional, repeatable | Registers a TTF file used for bold text, tried in registration order before falling through to the regular fonts above. When never called, bold text is rendered entirely with the regular fonts |
| `excelPassword` | String or null | Optional | Password to open the source Excel file |
| `pdfPassword` | String or null | Optional | Password to open the output PDF (user password) |
| `pdfOwnerPassword` | String or null | Optional | Owner password for the output PDF. Defaults to `pdfPassword` when omitted |
| `dateLocale` | Locale or null | Optional | Locale for date format resolution. Defaults to the JVM default locale |

## Using the Builder

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;

PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // optional
    .excelPassword("excel-pass")
    .pdfPassword("pdf-pass")
    .build();

ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```

The path passed to `builderForExplicitFont` is required and counts as the first call to
`addRegularFontPath`. All other options are optional.

## System Fonts (`builderForSystemFonts`)

With `builderForSystemFonts()`, the library searches the OS font directories
(including fonts installed by Microsoft Office) for the workbook's default font.

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .build();
```

If no matching system font is found, a `PdfGenerateException` is thrown.
You can also register a fallback via `addRegularFontPath`:

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .addRegularFontPath(Path.of("/path/to/fallback.ttf"))  // fallback
    .build();
```

> **Font licensing notice:** The located system font is embedded in the output PDF.
> Confirm that the font's licence permits embedding and distribution before enabling this option.

When a font family ships multiple weight variants (e.g. 游ゴシック Light / Medium / Regular),
the library prefers the **Medium** weight over Light for the regular (non-bold) font, matching
Excel on macOS which uses Medium as the default display weight for CJK fonts. Font selection also
correctly excludes **italic** and **bold** variants when a regular (upright) font is requested, so
fonts such as Calibri are reliably resolved to their Regular face.

When the workbook's default font does not contain CJK glyphs (e.g. Calibri), any CJK characters in
cell text are automatically rendered using the configured fallback font, on a character-by-character
basis.

## Per-Cell Font Resolution

When a cell's font differs from the workbook's default font (e.g. most of the report uses Calibri,
but a few cells are explicitly set to a Japanese font for localized content), that cell's own font
is resolved from the OS font directories and used for the cell, instead of always using the
workbook's default font. This applies to each distinct font name found in the workbook, following
the same weight-variant and TTC lookup rules described above.

If a cell's font cannot be found on the system, the cell falls back to the workbook's default font
(a warning is logged) rather than failing PDF generation — generation only fails when the
workbook's default font itself cannot be resolved and no fallback font is configured via
`addRegularFontPath`.

## Font File

Specify a TTF font file as the `builderForExplicitFont` argument.
For documents containing Japanese text, use a Japanese-compatible font (e.g. Noto Sans JP).

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // optional
    .build();
```

## Fallback Fonts (`addRegularFontPath` / `addBoldFontPath`)

`addRegularFontPath` and `addBoldFontPath` can each be called multiple times. The order
of calls is the priority order: when a character cannot be encoded by one entry, the next
one (in the same list) is tried.

Resolution order for a given character:

1. Regular text: `regularFontPaths`, in registration order (in explicit-font mode, the
   font passed to `builderForExplicitFont` is always the first entry).
2. Bold text: `boldFontPaths`, in registration order, then falls through to
   `regularFontPaths` above for any character not covered. When `addBoldFontPath` is
   never called, bold text is rendered entirely with the regular fonts.

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))
    .addRegularFontPath(Path.of("/path/to/NotoSansKR-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansKR-Bold.ttf"))
    .addRegularFontPath(Path.of("/path/to/NotoSansSC-Regular.ttf"))
    // no addBoldFontPath for NotoSansSC — bold text in that script falls through to
    // its own entry in regularFontPaths above
    .build();
```

## Password-Protected Excel File

To open a password-protected Excel file, set `excelPassword`:

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/regular.ttf"))
    .excelPassword("secret123")
    .build();
```

## Password-Protecting the Output PDF

### User Password (`pdfPassword`)

To require a password to open the generated PDF, set `pdfPassword`:

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/regular.ttf"))
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
PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/regular.ttf"))
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

PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/font.ttf"))
    .dateLocale(Locale.JAPAN)
    .build();
```
