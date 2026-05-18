# Reader

This page explains how to use each Reader class.
For class selection guidance, see [Overview](/public/en/article?id=excel-tables/overview).

## Common: `read()` Method

All Reader classes provide two `read()` overloads:

```java
// Open and close the file automatically
List<List<T>> read(String filePath) throws IOException;

// Read from an already-open Workbook
List<List<T>> read(Workbook workbook) throws IOException;
```

`T` is the data type (`String` or `Cell`). The outer `List` represents rows;
the inner `List` represents cell values in one row. The header row is **not**
included in the return value.

## Common: Fluent Setters

| Setter | Type | Default | Description |
| --- | --- | --- | --- |
| `tableStartRowNumber(Integer)` | Integer or null | null (auto-detect) | Table start row (1-based) |
| `tableStartColumnNumber(int)` | int | 1 | Table start column (1-based) |
| `tableRowSize(Integer)` | Integer or null | null (auto-detect) | Maximum rows to read |
| `tableColumnSize(Integer)` | Integer or null | null (auto-detect) | Maximum columns to read |
| `withIgnoresAdditionalColumnsOfHeaderData(boolean)` | boolean | false | Ignore extra header columns |
| `withVerticalAndHorizontalOpposite(boolean)` | boolean | false | Transposed table support |

## `StringHeaderExcelTableReader`

The most commonly used class for reading a header-bearing table as String data.

### Single-Row Header

```java
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Code", "Name", "Price"});

List<List<String>> data = reader.read("/path/to/file.xlsx");
// data.get(0) → ["A001", "Sample Product", "1000"]
// data.get(1) → ["A002", "Another Product", null]  ← empty cell = null
```

### Multi-Row Header

```java
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"Product",       "Product", "Price"},
        {"Product Code",  "Name",    "List Price"}
    });

List<List<String>> data = reader.read("/path/to/file.xlsx");
```

### Explicit Table Position

Use when the sheet contains multiple tables or the position must be fixed:

```java
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Name", "Amount"})
    .tableStartRowNumber(5)     // table starts at row 5
    .tableStartColumnNumber(3); // table starts at column C
```

### String-Type Specific Setters

| Setter | Default | Description |
| --- | --- | --- |
| `noDataString(NoDataString)` | `NoDataString.NULL` | Value for empty cells |
| `defaultDateTimeFormat(DateTimeFormatter)` | `yyyy-MM-dd` | Date format for all columns |
| `columnDateTimeFormat(int, DateTimeFormatter)` | — | Date format for a specific column (1-based absolute column number) |

## `StringFreeExcelTableReader`

Reads a headerless or position-specified table as String data.

```java
StringFreeExcelTableReader reader = new StringFreeExcelTableReader("Sheet1")
    .tableStartRowNumber(2)    // start at row 2
    .tableColumnSize(3);       // read only 3 columns

List<List<String>> data = reader.read("/path/to/file.xlsx");
```

When `tableStartRowNumber` is omitted, reading starts from row 1.
Reading continues until a fully-empty row is encountered.

## `CellOneLineHeaderExcelTableReader`

Reads a header-bearing table as POI `Cell` objects. Use when cell details such
as style or numeric type are needed.

```java
import org.apache.poi.ss.usermodel.Cell;

CellOneLineHeaderExcelTableReader reader = new CellOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Product Name", "Price"});

List<List<Cell>> data = reader.read("/path/to/file.xlsx");

for (List<Cell> row : data) {
    String name  = row.get(0).getStringCellValue();
    double price = row.get(1).getNumericCellValue();
}
```

## `CellFreeExcelTableReader`

Reads a headerless or position-specified table as POI `Cell` objects.

```java
CellFreeExcelTableReader reader = new CellFreeExcelTableReader("Sheet1")
    .tableStartRowNumber(3)
    .tableStartColumnNumber(2)
    .tableRowSize(20)
    .tableColumnSize(5);

List<List<Cell>> data = reader.read("/path/to/file.xlsx");
```

## Large Files: `IterableReader`

When loading all rows into a `List` would consume too much memory,
use `getIterable()` to process rows one at a time:

```java
try (ExcelTableReader.IterableReader<String> iter =
        reader.getIterable("/path/to/file.xlsx")) {
    for (List<String> row : iter) {
        // process one row at a time
    }
}
```

The file-path overload closes the Workbook automatically via try-with-resources.
When passing an existing `Workbook` (`getIterable(Workbook)`), the caller owns it.
