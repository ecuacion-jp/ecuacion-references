# Bean Mapping

`StringHeaderExcelTableToBeanReader` automatically converts each Excel row
into a Java object (Bean). Jakarta Validation is integrated.

## Overview

While a normal Reader returns `List<List<String>>`, this class returns
a list of Beans that extend `StringExcelTableBean`.

```
StringHeaderExcelTableToBeanReader.readToBean(filePath)
  → List<T extends StringExcelTableBean>
```

## Defining the Bean Class

### Using `@ExcelColumn` Annotations (Recommended)

Extend `StringExcelTableBean` and annotate each field with
`@ExcelColumn` to specify the matching header label.

```java
import jp.ecuacion.util.excel.table.bean.StringExcelTableBean;
import jp.ecuacion.util.excel.table.bean.ExcelColumn;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class ProductBean extends StringExcelTableBean {

    @ExcelColumn("Product Code")
    @NotEmpty
    @Pattern(regexp = "[A-Z][0-9]{3}")
    private String productCode;

    @ExcelColumn("Product Name")
    @NotEmpty
    private String productName;

    @ExcelColumn("Price")
    private Integer price;          // automatically converted to Integer

    public ProductBean(List<String> colList) {
        super(colList);
    }

    // getters / setters ...
}
```

With `@ExcelColumn`, the column order in Excel does not matter.
Each annotated field is matched to the column whose header label equals
the annotation value.

### Automatic Type Conversion

`StringExcelTableBean` automatically converts String values to the declared field type:

| Field type | Conversion |
| --- | --- |
| `String` | As-is |
| `Integer` / `int` | `Integer.valueOf(value)` |
| `Long` / `long` | `Long.valueOf(value)` |
| `BigDecimal` | `new BigDecimal(value)` |
| `BigInteger` | `new BigInteger(value)` |
| `Boolean` / `boolean` | `Boolean.valueOf(value)` |
| `LocalDate` | `LocalDate.parse(value, formatter)` |
| `LocalDateTime` | `LocalDateTime.parse(value)` |
| `LocalTime` | `LocalTime.parse(value)` |

`null` or empty string is converted to `null` (except for `String` fields).

Override `getDateTimeFormatter()` to customise the date format:

```java
@Override
protected DateTimeFormatter getDateTimeFormatter() {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd");
}
```

### Using `getFieldNameArray()` Override

If you prefer column-order-based mapping instead of `@ExcelColumn`:

```java
@Override
protected String[] getFieldNameArray() {
    return new String[] {"productCode", "productName", "price"};
}
```

Pass `null` to skip a column:

```java
return new String[] {"productCode", null, "price"}; // skip column 2
```

## Reading with `readToBean()`

```java
StringHeaderExcelTableToBeanReader<ProductBean> reader =
    new StringHeaderExcelTableToBeanReader<>(
        ProductBean.class,
        "Sheet1",
        new String[] {"Product Code", "Product Name", "Price"});

List<ProductBean> products = reader.readToBean("/path/to/file.xlsx");
```

`readToBean()` runs Jakarta Validation automatically.
A `ViolationException` is thrown when there are validation errors.

Pass `false` as the second argument to skip validation:
`readToBean(filePath, false)`.

## `afterReading()` Hook

Called after each Bean is validated. Use it for cross-field checks:

```java
@Override
public void afterReading() {
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
        throw new RuntimeException("Start date must not be after end date.");
    }
}
```

## Highlighting Error Cells: `highlightErrors()`

When a `ViolationException` occurs, you can save a copy of the Excel file
with the error cells highlighted in red:

```java
try {
    reader.readToBean(filePath);
} catch (ViolationException ex) {
    reader.highlightErrors(filePath, ex.getViolations(), "/path/to/error-output.xlsx");
    throw ex;
}
```

When `@ExcelColumn` is used, only the cells of violated fields are highlighted.
Otherwise, all data cells in the error row are highlighted.

## Multi-Row Headers with `@ExcelColumn`

For multi-row headers, provide one element per header row in `@ExcelColumn`
(top row first):

```java
// 2-row header: row 1 "Personal Info" / row 2 "Name"
@ExcelColumn({"Personal Info", "Name"})
private String name;

// Vertically merged column (same label in all rows) — single element is fine
@ExcelColumn("#")
private Integer rowNumber;
```
