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
| `IfFormatHeaderExcelTable` | Table whose first row(s) are headers (with validation) |
| `IfFormatFreeExcelTable` | Headerless table at an arbitrary position |

## Choosing the Right Class

Pick the concrete class from the combination of the two axes.

### Reader Classes

| Class | Data type | Format | Notes |
| --- | --- | --- | --- |
| `StringOneLineHeaderExcelTableReader` | String | Header (1-row) | Most common choice |
| `StringOneLineHeaderExcelTableToBeanReader` | String | Header (1-row) | Maps each row to a Bean |
| `StringHeaderExcelTableReader` | String | Header (multi-row) | For 2 or more header rows |
| `StringHeaderExcelTableToBeanReader` | String | Header (multi-row) | Maps each row to a Bean |
| `StringFreeExcelTableReader` | String | Free | No header; arbitrary position |
| `CellOneLineHeaderExcelTableReader` | Cell | Header (1-row) | Returns POI `Cell` objects |
| `CellHeaderExcelTableReader` | Cell | Header (multi-row) | Cell type; 2 or more header rows |
| `CellFreeExcelTableReader` | Cell | Free | Cell type; no header |

> **Why there is no ToBeanReader for Cell type:** Bean conversion relies on `StringExcelTableBean`, which maps string values to typed fields. Combining this with Cell type is not supported. When Cell type is needed, it is more natural to work directly with `Cell` objects to access style and type information.

### Writer Classes

| Class | Data type | Format | Notes |
| --- | --- | --- | --- |
| `StringOneLineHeaderExcelTableWriter` | String | Header (1-row) | |
| `StringOneLineHeaderExcelTableFromBeanWriter` | String | Header (1-row) | Write from Bean list |
| `StringHeaderExcelTableWriter` | String | Header (multi-row) | For 2 or more header rows |
| `StringHeaderExcelTableFromBeanWriter` | String | Header (multi-row) | Write from Bean list |
| `StringFreeExcelTableWriter` | String | Free | |
| `CellOneLineHeaderExcelTableWriter` | Cell | Header (1-row) | |
| `CellHeaderExcelTableWriter` | Cell | Header (multi-row) | Cell type; 2 or more header rows |
| `CellFreeExcelTableWriter` | Cell | Free | |

For dependency setup, see the [Quick Start](/public/en/article?id=excel-tables/quickstart) page.
