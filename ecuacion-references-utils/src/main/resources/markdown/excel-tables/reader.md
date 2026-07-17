This page explains how to use each Reader class.

## Reader Class List

| Class | Data type | Format |
| --- | --- | --- |
| `StringOneLineHeaderExcelTableReader` | String | Header (1-row) |
| `StringOneLineHeaderExcelTableToBeanReader` | String | Header (1-row) + Bean |
| `StringHeaderExcelTableReader` | String | Header (multi-row) |
| `StringHeaderExcelTableToBeanReader` | String | Header (multi-row) + Bean |
| `StringFreeExcelTableReader` | String | Free |
| `TypedOneLineHeaderExcelTableReader` | Typed | Header (1-row) |
| `TypedOneLineHeaderExcelTableToBeanReader` | Typed | Header (1-row) + Bean |
| `TypedHeaderExcelTableReader` | Typed | Header (multi-row) |
| `TypedHeaderExcelTableToBeanReader` | Typed | Header (multi-row) + Bean |
| `CellOneLineHeaderExcelTableReader` | Cell | Header (1-row) |
| `CellHeaderExcelTableReader` | Cell | Header (multi-row) |
| `CellFreeExcelTableReader` | Cell | Free |

> **Why there is no ToBeanReader for Cell type:** Bean conversion relies on `StringExcelTableBean` or `TypedExcelTableBean`, which map values to typed fields. Combining this with Cell type is not supported. When Cell type is needed, it is more natural to work directly with `Cell` objects to access style and type information.

## Common: `read()` Method

All Reader classes provide two `read()` overloads:

```java
// Open and close the file automatically
List<List<T>> read(String filePath) throws IOException;

// Read from an already-open Workbook
List<List<T>> read(Workbook workbook) throws IOException;
```

`T` is the data type (`String` or `Cell`). The outer `List` represents rows;
the inner `List` represents cell values in one row. Header rows are **not**
included in the return value (all header rows are removed when there are multiple).

## Common: Fluent Setters

| Setter | Type | Default | Description |
| --- | --- | --- | --- |
| `tableStartRowNumber(Integer)` | Integer or null | null (auto-detect) | Table start row (1-based) |
| `tableStartColumnNumber(int)` | int | 1 | Table start column (1-based) |
| `tableRowSize(Integer)` | Integer or null | null (auto-detect) | Maximum rows to read |
| `tableColumnSize(Integer)` | Integer or null | null (auto-detect) | Maximum columns to read |
| `withIgnoresAdditionalColumnsOfHeaderData(boolean)` | boolean | false | Ignore extra header columns |
| `withVerticalAndHorizontalOpposite(boolean)` | boolean | false | Transposed table support |

## `StringOneLineHeaderExcelTableReader`

The most commonly used class for reading a single-row-header table as String data.

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Code", "Name", "Price"});

List<List<String>> data = reader.read("/path/to/file.xlsx");
// data.get(0) → ["A001", "Sample Product", "1000"]
// data.get(1) → ["A002", "Another Product", null]  ← empty cell = null
```

### Explicit Table Position

Use when the sheet contains multiple tables or the position must be fixed:

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
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
| `columnDateTimeFormat(int, DateTimeFormatter)` | Falls back to `defaultDateTimeFormat` | Date format for a specific column (1-based absolute column number). Columns without an explicit setting use `defaultDateTimeFormat` |

## `StringHeaderExcelTableReader`

Used when the table has two or more header rows. Pass headers as `String[][]`.

```java
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"Product",       "Product", "Price"},
        {"Product Code",  "Name",    "List Price"}
    });

List<List<String>> data = reader.read("/path/to/file.xlsx");
```

Merged cells in the header area are automatically expanded before validation.

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

> **Note on ToBean conversion:** There is no ToBeanReader for the Free format.
> ToBeanReader works by matching `@ExcelColumn` annotation values against the Excel header row
> to map columns to Bean fields. Because Free-format tables have no header row,
> this mechanism cannot be applied.

## `TypedOneLineHeaderExcelTableReader`

Reads a single-row-header table, returning each cell's value as its native
Java type (`String`, `Double`, `LocalDate`, `LocalDateTime`, `Boolean`, or
`null`) instead of as a string. See [Data Types](/public/showMarkdown/page?id=excel-tables/data-types&lang=en)
for the cell-to-type conversion table.

```java
import java.time.LocalDate;
import java.util.List;

TypedOneLineHeaderExcelTableReader reader = new TypedOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Name", "Score", "Birthday"});

List<List<Object>> data = reader.read("/path/to/file.xlsx");

String name      = (String) data.get(0).get(0);
Double score     = (Double) data.get(0).get(1);
LocalDate birth  = (LocalDate) data.get(0).get(2);
```

The fluent setters are the same as the [common setters](#common-fluent-setters)
above; there is no String-type-specific setter such as `noDataString` or
`defaultDateTimeFormat`, since the cell's own type and format determine the
returned Java type.

## `TypedHeaderExcelTableReader`

Used when the table has two or more header rows. Pass headers as `String[][]`;
otherwise behaves the same as `TypedOneLineHeaderExcelTableReader`.

```java
TypedHeaderExcelTableReader reader = new TypedHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"Product",       "Product", "Price"},
        {"Product Code",  "Name",    "List Price"}
    });

List<List<Object>> data = reader.read("/path/to/file.xlsx");
```

Merged cells in the header area are automatically expanded before validation.

> **`TypedOneLineHeaderExcelTableToBeanReader` / `TypedHeaderExcelTableToBeanReader`:**
> These map each row to a `TypedExcelTableBean` subclass while preserving native
> types (and converting numeric values to the field's declared numeric type,
> rounding when necessary). See [Bean Mapping](/public/showMarkdown/page?id=excel-tables/bean-mapping&lang=en).

## `CellOneLineHeaderExcelTableReader`

Reads a single-row-header table as POI `Cell` objects. Use when cell details such
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

## `CellHeaderExcelTableReader`

Reads a table with two or more header rows as POI `Cell` objects.
Pass headers as `String[][]`.

```java
CellHeaderExcelTableReader reader = new CellHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"Product",       "Product", "Price"},
        {"Product Code",  "Name",    "List Price"}
    });

List<List<Cell>> data = reader.read("/path/to/file.xlsx");
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
