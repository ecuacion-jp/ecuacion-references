# Bean Mapping (Writer)

`StringOneLineHeaderExcelTableFromBeanWriter` (single header row) or
`StringHeaderExcelTableFromBeanWriter` (multiple header rows) writes a list of
`StringExcelTableBean` instances to an Excel file.

This is the counterpart to the
[ToBeanReader](/public/en/article?id=excel-tables/bean-mapping).

## Overview

Fields annotated with `@ExcelColumn` are matched against header labels, and their
values are converted to strings and written to the corresponding columns.

```
List<T extends StringExcelTableBean>
  → StringOneLineHeaderExcelTableFromBeanWriter.writeFromBean()
    → Excel file
```

## Writing with `writeFromBean()`

For a single header row, use `StringOneLineHeaderExcelTableFromBeanWriter`:

```java
List<ProductBean> beans = ...; // list of StringExcelTableBean subclass instances

new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[] {"Code", "Name", "Price"})
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

For two or more header rows, use `StringHeaderExcelTableFromBeanWriter`:

```java
new StringHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[][] {
        {"Product", "Product", "Price"},
        {"Code",    "Name",    "List Price"}
    })
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

## Field-to-Column Mapping

### Using `@ExcelColumn` Annotations (Recommended)

Annotate each field with `@ExcelColumn` specifying the header label.
The mapping is by label, independent of column order.

```java
public class ProductBean extends StringExcelTableBean {

    @ExcelColumn("Code")
    private String productCode;

    @ExcelColumn("Name")
    private String productName;

    @ExcelColumn("Price")
    private Integer price;

    public ProductBean(List<String> colList) {
        super(colList);
    }
}
```

### Overriding `getFieldNameArray()`

Without `@ExcelColumn`, override `getFieldNameArray()` for positional mapping.
The field order must match the column order in the header.

```java
@Override
protected String[] getFieldNameArray() {
    return new String[] {"productCode", "productName", "price"};
}
```

## Field Type to String Conversion

Each field value is converted to a string when writing.

| Field type | Result |
| --- | --- |
| `String` | As-is |
| `Integer` / `int` / `Long` etc. | `toString()` |
| `BigDecimal` / `BigInteger` | `toString()` |
| `Boolean` / `boolean` | `"true"` or `"false"` |
| `LocalDate` | Formatted with `defaultDateTimeFormat` (default `yyyy-MM-dd`) |
| `LocalDateTime` | Formatted with `defaultDateTimeFormat` |
| `LocalTime` | `toString()` (ISO format) |
| `null` | `null` (written as empty cell) |

## Customising the Date Format

Use the fluent setter to change the format for date fields:

```java
new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[] {"Code", "Registration Date"})
    .defaultDateTimeFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

## Typed FromBeanWriter: `TypedOneLineHeaderExcelTableFromBeanWriter` / `TypedHeaderExcelTableFromBeanWriter`

`TypedOneLineHeaderExcelTableFromBeanWriter` (single header row) or
`TypedHeaderExcelTableFromBeanWriter` (multiple header rows) writes a list of
`TypedExcelTableBean` instances to an Excel file — the Typed-type counterpart
to the writers described above.

### Overview

The key difference from the String-type FromBeanWriter is that **each field's
value is written to the cell as its native Java type**, not converted to a
string first:

```
List<T extends TypedExcelTableBean>
  → TypedOneLineHeaderExcelTableFromBeanWriter.writeFromBean()
    → Excel file (each cell holds a native-typed value)
```

For example, an `Integer` field is written as a numeric cell (not a string
that merely *looks* like a number), and — most importantly — a `LocalDate` /
`LocalDateTime` field is **guaranteed to be written into a date-formatted
cell**, so the output is recognisable as a date when opened in Excel.

### Writing with `writeFromBean()`

Usage is the same as the String-type FromBeanWriter — only the Bean's
superclass changes from `StringExcelTableBean` to `TypedExcelTableBean`:

```java
List<PersonBean> beans = ...; // list of TypedExcelTableBean subclass instances

new TypedOneLineHeaderExcelTableFromBeanWriter<PersonBean>(
    "Sheet1",
    new String[] {"Name", "Age", "Birthday"})
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

For two or more header rows, use `TypedHeaderExcelTableFromBeanWriter` and
pass headers as `String[][]`, exactly like `StringHeaderExcelTableFromBeanWriter`.

### Field-to-Column Mapping

`@ExcelColumn` annotations and `getFieldNameArray()` work exactly the same way
as described above for the String-type FromBeanWriter.

### Native-Type Cell Writing

Each field's value is written to the cell according to its Java type:

| Field type | Cell written as |
| --- | --- |
| `String` | String cell |
| `Double` / `Integer` / `Long` / `BigDecimal` / `BigInteger` etc. | Numeric cell (`setCellValue(double)`) |
| `Boolean` | Boolean cell |
| `LocalDate` | Date-formatted numeric cell (see below) |
| `LocalDateTime` | Date/time-formatted numeric cell (see below) |
| `null` | Blank cell |

### Date Cell Format Guarantee

This is the headline feature of the Typed FromBeanWriter: **whatever cell
style the template provides, a `LocalDate` / `LocalDateTime` value always ends
up in a cell that Excel recognises as a date** —

- **If the template cell already has a date format** (`DateUtil.isCellDateFormatted`
  returns `true`), that format is preserved as-is. Your carefully designed
  template formatting (e.g. `yyyy/mm/dd`, `yyyy年MM月dd日`) is respected.
- **If the template cell has no date format**, a default format is applied
  automatically — `yyyy-mm-dd` for `LocalDate`, `yyyy-mm-dd hh:mm:ss` for
  `LocalDateTime`. You no longer need to remember to format date columns in
  the template; the writer guarantees the value is recognisable as a date.

You can override the defaults applied in the second case with fluent setters:

```java
new TypedOneLineHeaderExcelTableFromBeanWriter<PersonBean>(
    "Sheet1",
    new String[] {"Name", "Birthday"})
    .defaultDateFormat("yyyy/mm/dd")
    .defaultDateTimeFormat("yyyy/mm/dd hh:mm:ss")
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

| Setter | Default | Description |
| --- | --- | --- |
| `defaultDateFormat(String)` | `"yyyy-mm-dd"` | Format pattern applied to `LocalDate` cells that have no existing date format |
| `defaultDateTimeFormat(String)` | `"yyyy-mm-dd hh:mm:ss"` | Format pattern applied to `LocalDateTime` cells that have no existing date format |

These setters take a POI cell-format pattern string (e.g. `"yyyy/mm/dd"`),
not a `DateTimeFormatter`.

## Template File

Like other Writer classes, `FromBeanWriter` writes into a copy of a template Excel file.
Prepare the template with header rows, formatting, and column widths in advance.
See [Writer](/public/en/article?id=excel-tables/writer) for details.
