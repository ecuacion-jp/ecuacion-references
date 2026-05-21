# Data Types

Reader and Writer classes are divided into two groups based on the data type
obtained from Excel cells: **String type** and **Cell type**.

## String Type (`IfDataTypeStringExcelTable`)

Cell values are obtained as `String`. Numbers, dates, and text are all returned
as strings. This is the most common choice.

### Empty Cells: `NoDataString` Enum

For String-type classes you can control what value an empty cell returns
via the `NoDataString` enum.

| Value | Meaning |
| --- | --- |
| `NoDataString.NULL` | Returns `null` (default) |
| `NoDataString.EMPTY_STRING` | Returns `""` |

Use `NoDataString.NULL` in most cases.
`@NotEmpty` treats both `null` and `""` as violations, so required-field validation
works correctly with either value. However, format validators such as `@Pattern`
skip `null` but are applied to `""`. Returning `""` for an empty cell could cause
a misleading "invalid format" error when the cell is simply blank.
Returning `null` avoids this problem.

Change it with the fluent setter:

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Name", "Age"})
    .noDataString(NoDataString.EMPTY_STRING);  // if you want "" for empty cells
```

### String Conversion for Date/Number Cells

Numeric and date cells can also be read as strings. The default date format is
`yyyy-MM-dd`. You can override the format globally or per-column:

```java
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Name", "RegisteredDate"})
    .defaultDateTimeFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    .columnDateTimeFormat(2, DateTimeFormatter.ofPattern("MM/dd"));
```

The first argument of `columnDateTimeFormat` is the 1-based absolute column
number on the sheet (not relative to the table start).

## Cell Type (`IfDataTypeCellExcelTable`)

Returns Apache POI `Cell` objects. Use this when you need type information
(numeric, string, date) or style information (background colour, font, etc.).

```java
import jp.ecuacion.util.excel.table.reader.concrete.CellOneLineHeaderExcelTableReader;
import org.apache.poi.ss.usermodel.Cell;
import java.util.List;

CellOneLineHeaderExcelTableReader reader = new CellOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Name", "Amount"});

List<List<Cell>> data = reader.read("/path/to/file.xlsx");

for (List<Cell> row : data) {
    String name   = row.get(0).getStringCellValue();
    double amount = row.get(1).getNumericCellValue();
}
```

For string conversion, `ExcelReadUtil.getStringFromCell(cell, dateTimeFormatter)` is available.

## Which Should I Use?

| Use case | Recommended |
| --- | --- |
| Read data and process it as-is | **String type** |
| Convert to a Bean and use Jakarta Validation | **String type** |
| Need cell style or type information | **Cell type** |
| Perform arithmetic on numeric values | **Cell type** (reading as String and parsing with `parseInt` can fail depending on the cell's Excel format) |
