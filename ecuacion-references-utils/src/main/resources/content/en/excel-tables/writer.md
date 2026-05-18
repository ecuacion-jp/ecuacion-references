# Writer

This page explains how to use each Writer class.

## How Writing Works: Template File

All Writer classes **write into a copy of a template Excel file**.

1. Specify the template file
2. Validate the template file's header against the expected labels
3. Write data starting from the row after the header
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

## `StringHeaderExcelTableWriter`

Writes String data into a header-bearing table.

### Basic Usage

```java
import jp.ecuacion.util.excel.table.writer.concrete.StringHeaderExcelTableWriter;
import java.util.Arrays;
import java.util.List;

StringHeaderExcelTableWriter writer = new StringHeaderExcelTableWriter(
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

### Multi-Row Header Writing

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

### Explicit Table Position

```java
StringHeaderExcelTableWriter writer = new StringHeaderExcelTableWriter(
    "Sheet1",
    new String[] {"Name", "Amount"})
    .tableStartRowNumber(3)
    .tableStartColumnNumber(2);
```

## `StringFreeExcelTableWriter`

Writes String data into a headerless table.

```java
import jp.ecuacion.util.excel.table.writer.concrete.StringFreeExcelTableWriter;

StringFreeExcelTableWriter writer = new StringFreeExcelTableWriter("Sheet1")
    .tableStartRowNumber(5)
    .tableStartColumnNumber(2);

writer.write("/path/to/template.xlsx", "/path/to/output.xlsx", data);
```

## `CellOneLineHeaderExcelTableWriter` / `CellFreeExcelTableWriter`

Used when writing POI `Cell` objects. The `data` type becomes `List<List<Cell>>`.
Usage is the same as the String counterparts.

## Fluent Setter Summary

| Setter | Description |
| --- | --- |
| `tableStartRowNumber(Integer)` | Table start row (1-based, null for auto-detect) |
| `tableStartColumnNumber(int)` | Table start column (1-based) |
| `withIgnoresAdditionalColumnsOfHeaderData(boolean)` | Ignore extra header columns |
| `withVerticalAndHorizontalOpposite(boolean)` | Transposed table support |
