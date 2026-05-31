# DB Definition Book (Excel) Overview

The DB Definition Book is the input file for `ecuacion-tool-code-generator`.
A single Excel file (`.xlsx`) defines the data model for a project.

## File Naming

```
DB項目定義書(fmt-v4.10.0)_<project-name>.xlsx
```

## Sheet Structure

| Sheet Name | Purpose | Details |
| --- | --- | --- |
| General Settings | Project-wide settings (package names, etc.) | [General Settings Sheet](/public/en/article?id=excel-format/general-settings) |
| DataType Definition | Field type definitions (`DT_XXXX` format) | [DataType Definition Sheet](/public/en/article?id=excel-format/data-type-sheet) |
| DB Definition | Table and column definitions | [DB Definition Sheet](/public/en/article?id=excel-format/db-definition-sheet) |
| Enum Definition | Enumeration value definitions | [Enum Definition Sheet](/public/en/article?id=excel-format/enum-sheet) |

## Creating a File for a New Project

1. **Copy and rename an existing Excel file**
   - Use the samples in `ecuacion-tool-code-generator-batch/ecuacion-tool-code-generator-excel-format/`
   - Rename to `DB項目定義書(fmt-v4.10.0)_<new-project-name>.xlsx`

2. **Update the General Settings sheet** (highest priority)
   - `SYSTEM_NAME` (row 8): new project name
   - `BASE_PACKAGE` (row 9): new Java package
   - `TABLE_NAMES_WITHOUT_GROUPING` (row 31): list of tables without group filtering

3. **Add project-specific DataTypes to the DataType Definition sheet**
   - Standard built-in DataTypes are already included; just add the new ones

4. **Add table definitions to the DB Definition sheet**
   - Keep existing authentication tables (ACC, ACC_ADMIN, etc.) as-is
   - Append new tables immediately after existing rows

5. **Add enum values to the Enum Definition sheet** (if needed)

## Generated Code Overview

From the DB Definition Book, the tool generates:

- **Entity**: One class per table
- **Record**: Input/output DTO for Entity
- **Repository**: JPA repository per table
- **BL**: CRUD, duplicate checks, optimistic lock validation, etc.
- **Enum / Converter**: Type conversion for ENUM columns
- **DataTypeValidator**: Field validation based on DataType
