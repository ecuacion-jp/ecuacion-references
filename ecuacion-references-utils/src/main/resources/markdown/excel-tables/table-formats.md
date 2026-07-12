# Table Formats

Reader and Writer classes are divided into two groups based on the table format:
**Header format** and **Free format**.

## Header Format (`IfFormatHeaderExcelTable`)

The table has one or more header rows at the top. The expected header labels are
provided to the constructor and validated automatically on read.

### Auto-Detection of Table Start Position

When `tableStartRowNumber` is not set (default `null`), the **leftmost header value**
is searched top-to-bottom in the sheet to locate the table start row.

```
If the sheet contains multiple tables → set tableStartRowNumber explicitly.
```

### Multi-Row Headers

For tables with two or more header rows, supply a `String[][]`:

```java
// 2-row header example
//  Row 1: | Personal Info | Personal Info | Contact    |
//  Row 2: | Name          | Age           | Email      |
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"Personal Info", "Personal Info", "Contact"},
        {"Name",          "Age",           "Email"}
    });
```

Merged cells in the header area are automatically expanded before validation.

### Allowing Extra Header Columns

By default, an `ExcelTableException` is thrown if the Excel header has more
columns than specified. Use `withIgnoresAdditionalColumnsOfHeaderData(true)` to
ignore extra columns:

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Name", "Age"})
    .withIgnoresAdditionalColumnsOfHeaderData(true);
```

### Transposed Tables (Header on the Left)

For tables where the header is on the left side instead of the top:

```java
reader.withVerticalAndHorizontalOpposite(true);
```

## Free Format (`IfFormatFreeExcelTable`)

Use this for headerless tables or tables where header validation is unnecessary.
Specify the start position and read range directly with fluent setters.

```java
import jp.ecuacion.util.excel.table.reader.concrete.StringFreeExcelTableReader;

StringFreeExcelTableReader reader = new StringFreeExcelTableReader("Sheet1")
    .tableStartRowNumber(3)      // start at row 3 (1-based)
    .tableStartColumnNumber(2)   // start at column B (1-based)
    .tableRowSize(10)            // read at most 10 rows
    .tableColumnSize(4);         // read 4 columns

List<List<String>> data = reader.read("/path/to/file.xlsx");
```

When `tableRowSize` and `tableColumnSize` are omitted, they are auto-detected
(read until a fully-empty row / until the first empty header cell).

## Which Should I Use?

| Situation | Recommended |
| --- | --- |
| Table has a header and column order must be guaranteed | **Header** |
| Read an arbitrary data range at a known position | **Free** |
| Header exists but validation is not needed | **Free** (or Header with `.withIgnoresAdditionalColumnsOfHeaderData(true)`) |
