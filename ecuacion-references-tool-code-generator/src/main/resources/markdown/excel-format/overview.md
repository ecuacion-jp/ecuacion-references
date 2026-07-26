The DB Definition Book is the input file for `ecuacion-tool-code-generator`.
A single Excel file (`.xlsx`) defines the data model for a project.

## File Naming

```
db-definition-book-fmt-v4.11.0_<project-name>_en.xlsx
```

## Sheet Structure

| Sheet Name | Purpose | Details |
| --- | --- | --- |
| General Settings | Project-wide settings (package names, etc.) | [General Settings Sheet](page?id=excel-format/general-settings&lang=en) |
| DataType Definition | Field type definitions (`DT_XXXX` format) | [DataType Definition Sheet](page?id=excel-format/data-type-sheet&lang=en) |
| DB Definition | Table and column definitions | [DB Definition Sheet](page?id=excel-format/db-definition-sheet&lang=en) |
| Enum Definition | Enumeration value definitions | [Enum Definition Sheet](page?id=excel-format/enum-sheet&lang=en) |

## Creating a File for a New Project

1. **Download the template and rename it**
   - Download the template from [the `excel-format/` directory in the GitHub repository](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/tree/main/excel-format)
   - Rename to `db-definition-book-fmt-v4.11.0_<new-project-name>_en.xlsx`
   - For a filled-in example, see [qiita-data-viewer's `excel-format/`](https://github.com/ecuacion-jp/qiita-data-viewer/tree/main/excel-format)

2. **Update the General Settings sheet** (highest priority)
   - `SYSTEM_NAME` (row 8): new project name
   - `BASE_PACKAGE` (row 9): new Java package
   - `TABLE_NAMES_WITHOUT_GROUPING` (row 31): list of tables without group filtering

3. **Define all required DataTypes in the DataType Definition sheet**

4. **Define all table definitions in the DB Definition sheet**

5. **Add enum values to the Enum Definition sheet** (if needed)

## Generated Code Overview

From the DB Definition Book, the tool generates:

- **Entity**: One class per table
- **Record**: Input/output DTO for Entity
- **Repository**: JPA repository per table
- **BL**: CRUD, duplicate checks, optimistic lock validation, etc.
- **Enum / Converter**: Type conversion for ENUM columns
- **DataTypeValidator**: Field validation based on DataType
