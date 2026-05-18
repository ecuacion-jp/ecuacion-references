# excel-tables Overview

`ecuacion-util-excel-table` is a library for reading and writing Excel table data
using Apache POI. It abstracts away table-position detection, header validation,
empty-cell handling, and Bean conversion, significantly reducing boilerplate code.

## Use Cases

- Read data from an Excel file in bulk
- Write a list of Java objects (Beans) to Excel
- Read safely while validating header-row labels

## Two-Axis Class Classification

Classes are organised along two axes: **data type** and **table format**.

### Data Type

| Interface | Data type obtained | When to use |
| --- | --- | --- |
| `IfDataTypeStringExcelTable` | `String` | Treat every cell as a string (most common) |
| `IfDataTypeCellExcelTable` | Apache POI `Cell` | When you also need style or type information |

### Table Format

| Interface | Characteristics |
| --- | --- |
| `IfFormatOneLineHeaderExcelTable` | Table whose first row(s) are headers (with validation) |
| `IfFormatFreeExcelTable` | Headerless table at an arbitrary position |

## Choosing the Right Class

Pick the concrete class from the combination of the two axes.

### Reader Classes

| Class | Data type | Format | Notes |
| --- | --- | --- | --- |
| `StringHeaderExcelTableReader` | String | OneLineHeader | Most common choice |
| `StringHeaderExcelTableToBeanReader` | String | OneLineHeader | Maps each row to a Bean |
| `StringFreeExcelTableReader` | String | Free | No header; arbitrary position |
| `CellOneLineHeaderExcelTableReader` | Cell | OneLineHeader | Returns POI `Cell` objects |
| `CellFreeExcelTableReader` | Cell | Free | Cell type; no header |

### Writer Classes

| Class | Data type | Format |
| --- | --- | --- |
| `StringHeaderExcelTableWriter` | String | OneLineHeader |
| `StringFreeExcelTableWriter` | String | Free |
| `CellOneLineHeaderExcelTableWriter` | Cell | OneLineHeader |
| `CellFreeExcelTableWriter` | Cell | Free |

## Adding the Dependency

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-table</artifactId>
    <version>(version)</version>
</dependency>
```

Apache POI (`poi` and `poi-ooxml`) is included transitively.
