### Handling Large Files

Apache POI loads the entire Excel file (xlsx) into memory when opening it.
This library does not enforce any file size limit internally, so processing
very large files may cause out-of-memory errors.

When processing files uploaded by users, it is recommended to check the file
size before calling this library:

```java
if (Files.size(excelPath) > 50 * 1024 * 1024) { // e.g. 50 MB
    throw new IllegalArgumentException("File size exceeds the allowed limit");
}
ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```
