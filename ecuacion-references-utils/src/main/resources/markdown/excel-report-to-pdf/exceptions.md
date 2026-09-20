## `PdfGenerateException`

The common superclass (`abstract`, `extends ViolationException`) of exceptions thrown when
PDF generation fails for a reason that may originate from the given Excel file or from the
font environment it depends on. It is `abstract`, so it is never thrown directly — one of the
concrete subclasses below always is.

A purely technical failure while reading the Excel file or writing the PDF file (e.g. a
corrupt file, a disk I/O error) is not represented by `PdfGenerateException`; `generate()`
throws a `java.io.UncheckedIOException` (unchecked) for those instead, whose `getCause()`
returns the underlying `IOException`.

### Concrete Subclasses

| Exception class | Thrown when |
| --- | --- |
| `SheetNotExistException` | A sheet name passed to `generate()` does not exist in the Excel file. |
| `SheetHasNoPrintAreaException` | A sheet has neither a defined print area nor any data to infer one from. |
| `SystemFontNotFoundException` | `useSystemFonts` is enabled and the workbook's default font is not installed on the running system, with no fallback font configured. |
| `FontLoadFailedException` | A system font file was located but could not be loaded as a `TrueTypeFont`. |
| `CharacterNotRenderableException` | A character in a cell cannot be encoded by the primary font or any configured fallback font. |

All five live in `jp.ecuacion.util.pdf.excel.report.exception` and extend `PdfGenerateException`.

### Catching the Exception

Each failure case has its own exception class, so `catch` by type instead of branching on a
string `messageId`:

```java
import jp.ecuacion.util.pdf.excel.report.exception.PdfGenerateException;
import jp.ecuacion.util.pdf.excel.report.exception.SystemFontNotFoundException;

try {
    ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
} catch (SystemFontNotFoundException ex) {
    // handle this specific case differently, e.g. reclassify as an environment problem
} catch (PdfGenerateException ex) {
    // catches any of the other four cases generically
    String messageId = ex.getMessageId();
    System.err.println("PDF generation error: " + messageId);
} catch (UncheckedIOException ex) {
    // purely technical failure (corrupt file, disk error, etc.); ex.getCause() is an IOException
}
```

### Inspecting the messageId

Like `ExcelTableException`, `PdfGenerateException` carries a `messageId` (via `getMessageId()`,
inherited from `ViolationException`) intended for i18n display to the end user. The message
text is defined in `messages_util_excel_report_to_pdf.properties` /
`messages_util_excel_report_to_pdf_ja.properties`.

```java
} catch (PdfGenerateException ex) {
    String messageId = ex.getMessageId();
    Object[] args = ex.getViolations().getBusinessViolations().get(0).getMessageArgs();
}
```
