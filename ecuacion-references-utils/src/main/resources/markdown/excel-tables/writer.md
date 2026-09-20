This page explains how to use each Writer class.

## Writer Class List

| Class | Data type | Format |
| --- | --- | --- |
| `StringOneLineHeaderExcelTableWriter` | String | Header (1-row) |
| `StringOneLineHeaderExcelTableFromBeanWriter` | String | Header (1-row) + write from Bean |
| `StringHeaderExcelTableWriter` | String | Header (multi-row) |
| `StringHeaderExcelTableFromBeanWriter` | String | Header (multi-row) + write from Bean |
| `StringFreeExcelTableWriter` | String | Free |
| `TypedHeaderExcelTableWriter` | Typed | Header (multi-row) |
| `TypedOneLineHeaderExcelTableFromBeanWriter` | Typed | Header (1-row) + write from Bean |
| `TypedHeaderExcelTableFromBeanWriter` | Typed | Header (multi-row) + write from Bean |
| `CellOneLineHeaderExcelTableWriter` | Cell | Header (1-row) |
| `CellHeaderExcelTableWriter` | Cell | Header (multi-row) |
| `CellFreeExcelTableWriter` | Cell | Free |

## How Writing Works: Template File

All Writer classes **write into a copy of a template Excel file**.

1. Specify the template file
2. Validate the template file's header against the expected labels
3. Write data starting from the row after the last header row
4. Save the result to the output file

Prepare the template file with header rows, formatting, column widths, etc.
set in advance.

> **Note:** `String`-type writers write via `Cell.setCellValue()` only and never create a new
> `CellStyle` — cells simply keep whatever style the template row already had. `Cell`-type and
> `Typed`-type (for date/time values) writers do create `CellStyle`s while writing, and cache/reuse
> them across rows/columns, since the number of `CellStyle`s in an Excel file has a limit (64,000).

## Common: `write()` Method

Every Writer class provides three overloads of `write()`, differing in how the
workbook is opened, saved, and closed:

```java
// Opens templateFilePath, writes data, saves to destFilePath, then closes the workbook.
void write(String templateFilePath, String destFilePath, List<List<T>> data)
    throws IOException;

// Opens templateFilePath, writes data, and returns the open Workbook.
// The caller is responsible for saving (Workbook#write) and closing it.
Workbook write(String templateFilePath, List<List<T>> data)
    throws IOException;

// Writes data into an already-open Workbook (e.g. one you opened yourself,
// or are reusing across multiple writes). The caller keeps ownership.
void write(Workbook workbook, List<List<T>> data)
    throws IOException;
```

| Argument | Description |
| --- | --- |
| `templateFilePath` | Path to the template Excel file |
| `destFilePath` | Path to the output file |
| `workbook` | An already-open template `Workbook` |
| `data` | Data to write (outer List = rows, inner List = columns) |

## `StringOneLineHeaderExcelTableWriter`

The most common class for writing String data into a single-row-header table.

```java
import jp.ecuacion.util.excel.table.writer.concrete.StringOneLineHeaderExcelTableWriter;
import java.util.Arrays;
import java.util.List;

StringOneLineHeaderExcelTableWriter writer = new StringOneLineHeaderExcelTableWriter(
    "Sheet1",
    new String[] {"Code", "Name", "Price"});

List<List<String>> data = Arrays.asList(
    Arrays.asList("A001", "Sample Product", "1000"),
    Arrays.asList("A002", "Another",        "2500")
);

writer.write(
    "/path/to/template.xlsx",
    "/path/to/output.xlsx",
    data);
```

### Explicit Table Position

```java
StringOneLineHeaderExcelTableWriter writer = new StringOneLineHeaderExcelTableWriter(
    "Sheet1",
    new String[] {"Name", "Amount"})
    .tableStartRowNumber(3)
    .tableStartColumnNumber(2);
```

## `StringHeaderExcelTableWriter`

Used when the table has two or more header rows.
Specifying `String[][]` automatically applies horizontal and vertical merges:

```java
StringHeaderExcelTableWriter writer = new StringHeaderExcelTableWriter(
    "Sheet1",
    new String[][] {
        {"Product",       "Product",  "Price"},
        {"Product Code",  "Name",     "List Price"}
    });
```

The template file's header must match the multi-row structure.

## `StringFreeExcelTableWriter`

Writes String data into a headerless table.

```java
import jp.ecuacion.util.excel.table.writer.concrete.StringFreeExcelTableWriter;

StringFreeExcelTableWriter writer = new StringFreeExcelTableWriter("Sheet1")
    .tableStartRowNumber(5)
    .tableStartColumnNumber(2);

writer.write("/path/to/template.xlsx", "/path/to/output.xlsx", data);
```

## `TypedHeaderExcelTableWriter`

Writes a `List<List<Object>>` of native Java values (`String`, `Double`,
`LocalDate`, `LocalDateTime`, `Boolean`, etc.) into a table with two or more
header rows. Each value is written to the cell as its native type — for
example, a `LocalDate` value produces a date-formatted cell rather than a
plain number or string. Pass headers as `String[][]`, the same as
`StringHeaderExcelTableWriter`.

```java
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

TypedHeaderExcelTableWriter writer = new TypedHeaderExcelTableWriter(
    "Sheet1",
    new String[][] {
        {"Product",       "Product",  "Released"},
        {"Product Code",  "Name",     "Release Date"}
    });

List<List<Object>> data = Arrays.asList(
    Arrays.asList("A001", "Sample Product", LocalDate.of(2026, 1, 15))
);

writer.write("/path/to/template.xlsx", "/path/to/output.xlsx", data);
```

There is no `TypedOneLineHeaderExcelTableWriter`; for a single-row header you
can pass a `String[][]` with a single inner array to `TypedHeaderExcelTableWriter`.

For how date/datetime cell formatting is determined (and how to customise it
with `defaultDateFormat` / `defaultDateTimeFormat`), see
[From-Bean Writer](page?id=excel-tables/from-bean-writer&lang=en)
— the same `IfDataTypeTypedExcelTableWriter` logic is shared by both the
plain Typed writer and the Typed FromBeanWriter classes.

## `StringOneLineHeaderExcelTableFromBeanWriter`

Writes a list of `StringExcelTableBean` instances into a single-row-header table.
Uses `@ExcelColumn` annotations to map fields to columns (the reverse of ToBean reading).

```java
List<ProductBean> beans = ...; // list of StringExcelTableBean subclass instances

new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[] {"Code", "Name", "Price"})
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

To customise the date format used when converting date fields to strings:

```java
new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(...)
    .defaultDateTimeFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    .writeFromBean(...);
```

## `StringHeaderExcelTableFromBeanWriter`

Writes a list of `StringExcelTableBean` instances into a table with two or more header rows.
Pass headers as `String[][]`.

```java
new StringHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[][] {
        {"Product", "Product", "Price"},
        {"Code",    "Name",    "List Price"}
    })
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

## `TypedOneLineHeaderExcelTableFromBeanWriter` / `TypedHeaderExcelTableFromBeanWriter`

Write a list of `TypedExcelTableBean` instances into a single-row-header or
multi-row-header table, respectively — the Typed-type counterparts of
`StringOneLineHeaderExcelTableFromBeanWriter` / `StringHeaderExcelTableFromBeanWriter`.
The key difference: each field's value is written to the cell as its native
type, and date/datetime fields are guaranteed to land in a date-formatted cell.
See [From-Bean Writer](page?id=excel-tables/from-bean-writer&lang=en)
for full details, including how the date cell format is determined and how
to customise it.

## `CellOneLineHeaderExcelTableWriter`

Writes Cell data into a single-row-header table.
The `data` type is `List<List<Cell>>`.
Usage mirrors `StringOneLineHeaderExcelTableWriter`, but you must prepare `Cell` objects.

## `CellHeaderExcelTableWriter`

Writes Cell data into a table with two or more header rows.
Pass headers as `String[][]`. Usage mirrors `StringHeaderExcelTableWriter`.

## `CellFreeExcelTableWriter`

Writes Cell data into a headerless table.
The `data` type is `List<List<Cell>>`.
Usage mirrors `StringFreeExcelTableWriter`.

## Fluent Setter Summary

| Setter | Description |
| --- | --- |
| `tableStartRowNumber(Integer)` | Table start row (1-based, null for auto-detect) |
| `tableStartColumnNumber(int)` | Table start column (1-based) |
| `withIgnoresAdditionalColumnsOfHeaderData(boolean)` | Ignore extra header columns |
| `withVerticalAndHorizontalOpposite(boolean)` | Transposed table support |

## Large Files: `IterableWriter`

When building the whole `List<List<T>>` in memory before writing would consume too much
memory, use `getIterable()` to write rows one at a time instead:

```java
try (ExcelTableWriter.IterableWriter<String> iter =
        writer.getIterable("/path/to/template.xlsx", "/path/to/output.xlsx")) {
    for (List<String> row : rowSource) {
        iter.write(row);
    }
}
```

The template/dest-path overload (`getIterable(String, String)`) owns the `Workbook` it opens;
`close()` saves it to the destination path and closes it — use try-with-resources.
When passing an existing `Workbook` (`getIterable(Workbook)`), the caller keeps ownership:
`close()` becomes a no-op, and the caller is responsible for saving and closing the workbook.
