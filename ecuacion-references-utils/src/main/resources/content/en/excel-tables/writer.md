# Writer

This page explains how to use each Writer class.

## Writer Class List

| Class | Data type | Format |
| --- | --- | --- |
| `StringOneLineHeaderExcelTableWriter` | String | Header (1-row) |
| `StringOneLineHeaderExcelTableFromBeanWriter` | String | Header (1-row) + write from Bean |
| `StringHeaderExcelTableWriter` | String | Header (multi-row) |
| `StringHeaderExcelTableFromBeanWriter` | String | Header (multi-row) + write from Bean |
| `StringFreeExcelTableWriter` | String | Free |
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
set in advance. When writing, the first data row's cell style is reused for
subsequent rows (to stay within Excel's 64,000-style limit).

## Common: `write()` Method

```java
void write(String templateFilePath, String destFilePath, List<List<T>> data)
    throws IOException;
```

| Argument | Description |
| --- | --- |
| `templateFilePath` | Path to the template Excel file |
| `destFilePath` | Path to the output file |
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
