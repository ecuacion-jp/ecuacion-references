# code-generator-batch Overview

`ecuacion-tool-code-generator-batch` is a batch execution module that reads a DB Definition Book (Excel) and auto-generates Java source code for the **base module** of a Spring Boot + JPA application.

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

## How It Works

1. The DB Definition Book (xlsx) placed in `ecuacion-tool-code-generator-excel-format/` is read
2. The batch is executed via `mvn spring-boot:run`
3. Java source files are written to `products/<SYSTEM_NAME>/`

## Documentation

- [Setup](/public/en/article?id=code-generator-batch/setup)
- [Quick Start](/public/en/article?id=code-generator-batch/quickstart)
- [DB Definition Book (Excel) Specification](/public/en/article?id=excel-format/overview)
