`PdfGenerateOptions` holds the parameters for PDF generation.
Build an instance using the Builder pattern, starting from one of two static factory methods
depending on how the rendering fonts are resolved.
It has the same form as `PdfGenerateOptions` of `ecuacion-util-excel-report-to-pdf`
(the package is `jp.ecuacion.util.pdf.word.document.options`).

## Available Options

| Option | Type | Required | Description |
| --- | --- | --- | --- |
| entry point | `builderForSystemFonts()` or `builderForExplicitFont(Path)` | Required | Selects the font resolution mode |
| `addRegularFontPath` | Path | Optional, repeatable | Registers a TTF / TTC file used for regular text, tried in registration order. With `builderForExplicitFont`, the argument is already the first entry |
| `addBoldFontPath` | Path | Optional, repeatable | Registers a TTF / TTC file used for bold text, tried before the regular fonts |
| `wordPassword` | String or null | Optional | Password to open a password-protected Word file |
| `pdfPassword` | String or null | Optional | Password to open the output PDF (user password). The PDF is encrypted with 256-bit AES |
| `pdfOwnerPassword` | String or null | Optional | Owner password for the output PDF. Defaults to `pdfPassword` when omitted |

## System Fonts (`builderForSystemFonts`)

The fonts the document specifies are looked up from the OS font directories (including fonts
installed by Microsoft Office). A Word document specifies separate fonts for Latin and East Asian
text, and each piece of text uses its own fonts.

- A font which is not installed is replaced by the default font of the document (a warning is logged).
- When the default font of the document itself is not installed, the fonts registered via
  `addRegularFontPath` are used. When none is registered, `SystemFontNotFoundException` is thrown.
- Characters which the fonts of the text do not have are tried against the fonts registered via
  `addRegularFontPath` / `addBoldFontPath`.
- As in Word, a bold face is used only when the font family has one of its own (e.g. Yu Gothic,
  Arial, Meiryo). Otherwise (e.g. Yu Mincho, Century, MS Mincho) the regular face is emboldened.

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .addRegularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))  // fallback
    .build();
```

> **Font licensing notice:** The located system fonts are embedded in the output PDF.
> Confirm that their licences permit embedding and distribution.

To get the same layout as Word, install the fonts the documents use (e.g. the fonts which come with
Microsoft Office) on the server.

## Font File (`builderForExplicitFont`)

The given font file is used for all the text, whatever fonts the document specifies.
The layout differs from Word's as much as the font differs from the fonts of the document.

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // optional
    .build();
```

When `addBoldFontPath` is not called, bold text is the regular font emboldened.
A TrueType Collection (`.ttc`) file can be given as well: its first font is used.

## Passwords

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .wordPassword("word-pass")         // to open the Word file
    .pdfPassword("view-only")          // to open the output PDF
    .pdfOwnerPassword("admin-secret")  // to change the security settings of the PDF
    .build();
```

For a protected file, a missing `wordPassword` throws `FilePasswordRequiredException`, and a
wrong one `FilePasswordIncorrectException`. A `wordPassword` for a file which is not protected
is ignored. For the user and owner passwords of the PDF, see
[Options of excel-report-to-pdf](/public/showMarkdown/page?id=excel-report-to-pdf/options&lang=en),
which work the same way.
