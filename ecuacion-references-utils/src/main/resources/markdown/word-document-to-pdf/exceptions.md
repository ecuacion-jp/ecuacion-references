## Exceptions of `WordToPdfUtil.generate()`

All the exceptions are unchecked.

| Exception class | Thrown when |
| --- | --- |
| `SystemFontNotFoundException` | With `builderForSystemFonts()`, the default font of the document is not installed, and no fallback font is registered. |
| `FontLoadFailedException` | A system font file was located but could not be loaded. |
| `CharacterNotRenderableException` | A character in the document cannot be encoded by any configured font. |
| `FileFormatNotSupportedException` | The file is not a Word file of a supported format (supported: docx, docm, dotx, dotm), e.g. a legacy `.doc` file, a file of another application, a broken or empty file. |
| `FilePasswordRequiredException` | The file is password-protected, and `wordPassword` is not set. |
| `FilePasswordIncorrectException` | The file is password-protected, and `wordPassword` is wrong. |
| `java.io.UncheckedIOException` | A technical failure while reading the Word file or writing the PDF file (e.g. a missing file, a disk I/O error). `getCause()` returns the underlying `IOException`. |

All but the last extend `PdfGenerateException`, and live in `jp.ecuacion.util.pdf.common.exception`
(module `ecuacion-util-pdf-common`). They are the same classes as those thrown by
`ecuacion-util-excel-report-to-pdf`.

## Catching the Exception

```java
import jp.ecuacion.util.pdf.common.exception.PdfGenerateException;
import jp.ecuacion.util.pdf.common.exception.SystemFontNotFoundException;

try {
    WordToPdfUtil.generate(wordPath, outputPath, options);
} catch (SystemFontNotFoundException ex) {
    // e.g. reclassify as an environment problem
} catch (PdfGenerateException ex) {
    // the other cases: the file (format, password) or the fonts
    String messageId = ex.getMessageId();
} catch (UncheckedIOException ex) {
    // purely technical failure; ex.getCause() is an IOException
}
```

`PdfGenerateException` carries a `messageId` for i18n display to the end user.
The message text is defined in `messages_util_pdf_common.properties` (with a file for every
supported language, e.g. `_ja`).

Elements which are not supported (shapes, text boxes, footnotes etc.) do not throw an exception:
they are skipped. See [Notes](/public/showMarkdown/page?id=word-document-to-pdf/notes&lang=en).
