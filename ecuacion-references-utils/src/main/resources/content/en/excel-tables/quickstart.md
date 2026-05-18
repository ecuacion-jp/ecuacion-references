# Quick Start

This page shows the simplest possible example using `StringHeaderExcelTableReader`.

## Adding the Dependency

Add the following to `pom.xml` (see the Overview page for details).

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-table</artifactId>
    <version>(version)</version>
</dependency>
```

## Preparing the Excel File

Assume the sheet "Sheet1" contains a table like this:

| Name | Age | Email |
| --- | --- | --- |
| Taro Yamada | 30 | taro@example.com |
| Hanako Suzuki | 25 | hanako@example.com |

## Code Example

```java
import jp.ecuacion.util.excel.table.reader.concrete.StringHeaderExcelTableReader;
import java.util.List;

StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[] {"Name", "Age", "Email"});

List<List<String>> data = reader.read("/path/to/file.xlsx");

for (List<String> row : data) {
    String name  = row.get(0);
    String age   = row.get(1);
    String email = row.get(2);
    System.out.println(name + " / " + age + " / " + email);
}
```

## How It Works

1. **Auto-detect table position**: The leftmost header value (`"Name"`) is searched
   top-to-bottom in the sheet to locate the table start row automatically.
2. **Header validation**: The specified header-label array is compared with the
   actual header row. A mismatch throws `ExcelAppException`.
3. **Read data rows**: Rows are read until a fully-empty row is encountered.
   The result is returned as `List<List<String>>`.

## Notes

- The return value does **not** include the header row (it is removed after validation).
- Empty cells default to `null` (`NoDataString.NULL`).
  See [Data Types](/public/en/article?id=excel-tables/data-types) for details.
