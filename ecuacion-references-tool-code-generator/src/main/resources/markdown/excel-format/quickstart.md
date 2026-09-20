This page covers the minimum setup needed for a new DB Definition Book (Excel) — the input file shared by both
`code-generator-cli` and `code-generator-web`.

## General Settings

Open the Excel file and go to the **General Settings** sheet. Rows 6-11, under the "System Common" category, are
all required — loading the sheet with any of them blank causes an error.

Of those, the following two must always be changed for a new project:

| Row | Field | Description | Example |
| --- | --- | --- | --- |
| 7 | System name | Project identifier. Becomes the output folder name under `products/` | `my-project` |
| 8 | Common part of the package name | Common Java package prefix for generated code | `jp.example.myapp` |

The remaining required rows in this category (6, 9-11) — template version, framework type, character encoding, and
default language — ship with reasonable defaults, so they can normally be left as-is. Just don't delete their
values.

One more required field lives outside this block: under the "Optimistic Locking" category, a column name is
required for the optimistic-lock version column. The template ships with `VERSION` already filled in — leave it
as-is unless that name conflicts with something in your own schema (this page uses that default below).

## DataType and DB Definition (Also Required)

Filling in General Settings alone is not enough to generate any code — the **DB Definition** sheet needs at least
one table, and every table needs a surrogate-key column plus a column matching the optimistic-lock version name
set above. This section walks through the smallest table that satisfies both.

### 1. Add three DataTypes

The blank template's **DataType Definition** sheet ships empty — nothing is pre-registered, so even a plain number
type needs a row here before you can use it. Add the following three rows:

| DataType Name | Type | Max Length | Data Pattern |
| --- | --- | --- | --- |
| `DT_SERIAL` | `LONG` | | |
| `DT_DB_UPD_VER` | `LONG` | | |
| `DT_NAME` | `STRING` | `100` | `All Characters` |

### 2. Add a table with three columns

Add the following rows to the **DB Definition** sheet:

| Table Name | Column Name | dataType | Surrogate key | Auto Numbering |
| --- | --- | --- | --- | --- |
| `USER` | `ID` | `DT_SERIAL` | ○ | ○ |
| `USER` | `NAME` | `DT_NAME` | | |
| `USER` | `VERSION` | `DT_DB_UPD_VER` | | |

- `ID` is the surrogate key — every table needs exactly one.
- `NAME` is an ordinary column, using the `DT_NAME` DataType from step 1.
- `VERSION` matches the optimistic-lock column name from General Settings above, so it's automatically treated as
  the version column — no extra flag needed.

This is enough to generate code. In a real project with multiple tables, defining `VERSION` (along with soft-delete
and audit columns, if used) once in the **DB Common Item Definition** sheet, instead of repeating it on every
table, is more common — see [qiita-data-viewer](https://github.com/ecuacion-jp/qiita-data-viewer) for a working
example of that pattern.

For the full sheet references, see [General Settings Sheet](page?id=excel-format/general-settings&lang=en),
[DataType Definition Sheet](page?id=excel-format/data-type-sheet&lang=en), and
[DB Definition Sheet](page?id=excel-format/db-definition-sheet&lang=en).
