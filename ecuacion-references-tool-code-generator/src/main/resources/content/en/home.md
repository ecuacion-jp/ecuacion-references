# ecuacion-tool-code-generator reference

This is the reference page for `ecuacion-tool-code-generator`.
It covers how to use the tool that auto-generates base module Java code for Spring Boot + JPA applications from a DB Definition Book (Excel).

## What This Tool Does

Based on table and column definitions in an Excel file, the tool generates boilerplate code
such as JPA Entities, Repositories, and business logic classes.

By generating this commonly error-prone and inconsistent code from a single unified source of truth,
the effort required when adding new tables is significantly reduced.

## Generated Code

Based on the table and column definitions in the DB Definition Book, the following files are generated:

| Category | Package | Description |
| --- | --- | --- |
| Entity | `*.base.entity` | JPA Entity classes (`@Entity`, `@Table`) |
| Record | `*.base.record` | Input/output DTOs (Entity ⇔ Record conversion) |
| Repository | `*.base.repository` | Spring Data JPA interfaces |
| RepositoryImpl | `*.base.repositoryimpl` | Custom query implementations |
| BL | `*.base.bl` | Business logic (CRUD, validation, duplicate checks, etc.) |
| Enum | `*.base.enums` | Enumeration types |
| Converter | `*.base.converter` | Enum ⇔ DB conversion (JPA `@Converter`) |
| DataTypeValidator | `*.base.datatype` | Field-specific validators |

## Key Built-in Features

The generated code includes these framework-standard features:

- **Soft delete**: Logical deletion via `DEL_FLG` column, transparently applied with a Hibernate filter
- **Group filter**: Multi-tenant support via `ACC_GROUP_ID`
- **Optimistic locking**: `VERSION` column
- **Audit fields**: `CREATE_ACC_ID`, `CREATE_TIME`, `LST_UPD_ACC_ID`, `LST_UPD_TIME`

## Execution Methods

The tool provides two execution methods:

| Method | Description |
| --- | --- |
| `code-generator-batch` | Run from the command line (`java -jar`). Place Excel locally and execute |
| `code-generator-web` | Upload Excel from a browser → download generated code as a ZIP |

Both methods use the same DB Definition Book (Excel) format.

## Navigation

| Menu | Contents |
| --- | --- |
| code-generator-batch | Overview, setup, and usage for the batch execution module |
| code-generator-web | Overview, setup, and usage for the Web UI module |
| DB Definition Book (Excel) | Sheet structure and column reference for the input Excel file |
