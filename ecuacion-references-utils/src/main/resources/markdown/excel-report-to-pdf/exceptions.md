## `PdfGenerateException`

A checked exception (`extends Exception`) thrown when an error occurs
during PDF generation.

### When It Is Thrown

| Situation | Message example |
| --- | --- |
| Specified sheet name does not exist in Excel | `Sheet not found: 'NonExistentSheet'` |
| Failed to read the Excel file | `Failed to generate PDF from '...'` |
| Other I/O errors | The cause exception is wrapped |

### Catching the Exception

```java
try {
    ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
} catch (PdfGenerateException ex) {
    System.err.println("PDF generation error: " + ex.getMessage());
    Throwable cause = ex.getCause();
    if (cause != null) {
        cause.printStackTrace();
    }
}
```

### Inspecting the Cause

`PdfGenerateException` may wrap an underlying `IOException`.
Use `getCause()` to inspect it:

```java
} catch (PdfGenerateException ex) {
    if (ex.getCause() instanceof IOException ioEx) {
        // file access error, etc.
    }
}
```
