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

## Template File

Like other Writer classes, `FromBeanWriter` writes into a copy of a template Excel file.
Prepare the template with header rows, formatting, and column widths in advance.
See [Writer](/public/en/article?id=excel-tables/writer) for details.
