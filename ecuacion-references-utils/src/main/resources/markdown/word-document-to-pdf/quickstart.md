## `WordToPdfUtil.generate()`

The method for generating a PDF.

```java
public static void generate(Path wordPath, Path outputPath, PdfGenerateOptions options)
```

| Argument | Description |
| --- | --- |
| `wordPath` | Path to the source Word (`.docx`) file |
| `outputPath` | Path for the output PDF file |
| `options` | PDF generation options including fonts (required) |

## Code Example

```java
import jp.ecuacion.util.pdf.common.exception.PdfGenerateException;
import jp.ecuacion.util.pdf.word.document.options.PdfGenerateOptions;
import jp.ecuacion.util.pdf.word.document.util.WordToPdfUtil;
import java.nio.file.Path;

Path wordPath   = Path.of("/path/to/contract.docx");
Path outputPath = Path.of("/path/to/contract.pdf");

PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .build();

try {
    WordToPdfUtil.generate(wordPath, outputPath, options);
} catch (PdfGenerateException ex) {
    System.err.println("PDF generation failed: " + ex.getMessageId());
}
```

> **Note:** `builderForSystemFonts()` searches for fonts installed locally (OS and
> Microsoft Office fonts). If the default font of the document is not found and no fallback
> font is registered, `SystemFontNotFoundException` (a `PdfGenerateException`) is thrown.
>
> **Caution:** System fonts are embedded in the output PDF. Make sure the
> font's license permits embedding and redistribution before using it this way.

## Using a Font File

To render with a font file regardless of the fonts the document specifies:

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // optional
    .build();
```

## Tables of Contents

A table of contents made with Word's "Table of Contents" feature is output with the page numbers
of the PDF, even when the table of contents in the Word file has not been updated.
Each entry links to its heading, and headings become the outline (bookmarks) of the PDF.
