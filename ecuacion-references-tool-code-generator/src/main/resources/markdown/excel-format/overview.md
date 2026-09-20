The DB Definition Book is the input file for `ecuacion-tool-code-generator`.
A single Excel file (`.xlsx`) defines the data model for a project.

## File Naming

```
db-column-definitions_fmt-v5.0.0-en_<project-name>.xlsx
```

## Sheet Structure

| Sheet Name | Purpose | Details |
| --- | --- | --- |
| General Settings | Project-wide settings (package names, etc.) | [General Settings Sheet](page?id=excel-format/general-settings&lang=en) |
| DataType Definition | Field type definitions (`DT_XXXX` format) | [DataType Definition Sheet](page?id=excel-format/data-type-sheet&lang=en) |
| DB Definition | Table and column definitions | [DB Definition Sheet](page?id=excel-format/db-definition-sheet&lang=en) |
| DB Common Item Definition | Columns applied to every table (e.g. audit columns, soft-delete flag, optimistic-lock version),<br>without repeating them on each table | Same column layout as DB Definition.<br>See the [DB Definition Sheet](page?id=excel-format/db-definition-sheet&lang=en) for details |
| Table List | Table display names per language. Populated automatically from the table names used in DB Definition | — |
| Enum Definition | Enumeration value definitions | [Enum Definition Sheet](page?id=excel-format/enum-sheet&lang=en) |

## Creating a File for a New Project

1. **Download the template and rename it**
   - Download the template from [the `excel-format/` directory in the GitHub repository](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/tree/main/excel-format)
   - Rename to `db-column-definitions_fmt-v5.0.0-en_<new-project-name>.xlsx`
   - For a filled-in example, see [qiita-data-viewer's `excel-format/`](https://github.com/ecuacion-jp/qiita-data-viewer/tree/main/excel-format)

2. **Update the General Settings sheet** (highest priority)
   - See [DB Definition Book Quick Start](page?id=excel-format/quickstart&lang=en) for the minimum settings
     required for a new project, and the [General Settings sheet](page?id=excel-format/general-settings&lang=en)
     for the full reference

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
