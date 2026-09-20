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
| BL | `*.base.bl` | Business logic (CRUD, validation, duplicate checks, etc.) |
| Enum | `*.base.enums` | Enumeration types |
| Converter | `*.base.converter` | Enum ⇔ DB conversion (JPA `@Converter`) |
| DataTypeValidator | `*.base.datatype` | Field-specific validators |

## Key Built-in Features

The generated code includes these framework-standard features.
For each feature, the column name is not fixed — you can specify any name in the DB Definition Book (the names below are just examples).

- **Soft delete**: Logical deletion via a delete-flag column (e.g. `DEL_FLG`), transparently applied with a Hibernate filter
- **Group filter**: Multi-tenant support via a group-ID column (e.g. `ACC_GROUP_ID`)
- **Optimistic locking**: A version column (e.g. `VERSION`)
- **Audit fields**: Columns for creator, creation time, last updater, and last update time (e.g. `CREATE_ACC_ID`, `CREATE_TIME`, `LST_UPD_ACC_ID`, `LST_UPD_TIME`)

## Execution Methods

The tool provides two execution methods:

| | code-generator-cli | code-generator-web |
| --- | --- | --- |
| How to run | Command line (`java -jar`) | File upload via browser |
| Workflow | Place Excel in local directory and run | Upload Excel, download ZIP |
| Best for | Developers running it locally | Sharing with teams, including non-developers |

Both methods use the same DB Definition Book (Excel) format.

## Navigation

| Menu | Contents |
| --- | --- |
| code-generator-cli | Overview, setup, and usage for the command-line module |
| code-generator-web | Overview, setup, and usage for the Web UI module |
| DB Definition Book (Excel) | Sheet structure and column reference for the input Excel file |
