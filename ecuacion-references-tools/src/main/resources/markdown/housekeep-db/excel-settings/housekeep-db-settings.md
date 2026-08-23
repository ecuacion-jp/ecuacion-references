Defines one deletion task per row. Tasks are executed from top to bottom.

## Basic Columns

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Identifies the task |
| DB Connection ID | ○ | Must match an ID in the DB Connection Settings sheet |
| Soft / Hard Delete | ○ | Display value (`Soft Delete` or `Hard Delete`) |
| Soft / Hard Delete (internal value) | ○ | Value read by the tool: `SOFT_DELETE` or `HARD_DELETE` |
| Table Name | ○ | Name of the table to housekeep |
| ID Column Name | ○ | Name of the single-column primary key or unique index column |
| ID Column Literal Symbol | ○ | Whether the ID column value needs SQL quoting: `(none)` or `quotes(')` |

**ID Column Literal Symbol**

| Data Type | Value |
| --- | --- |
| integer, boolean, and other non-string types | `(none)` |
| varchar and other string types | `quotes(')` |

## Expiration-Based Filtering (Optional)

Configure these columns to target only records older than a specified number of days.
All three columns must be set together or left empty together.

| Column | Description |
| --- | --- |
| Expiration Check: Timestamp Column Name | Name of the timestamp column (e.g., `last_updated`) |
| Expiration Check: Timestamp Column Data Type | Column data type: `LocalDateTime` (timestamp without time zone) or `OffsetDateTime` (timestamp with time zone) |
| Expiration Check: Validity Days | Records older than this many days are targeted |

## Soft Delete Columns (Soft Delete Only)

Only used when `Soft / Hard Delete (internal value)` is `SOFT_DELETE`.

| Column | Required | Description |
| --- | --- | --- |
| Soft Delete Column Name | ○ (soft delete) | Name of the boolean delete flag column. Set to `true` on soft delete |
| Soft Delete: Update Timestamp Column Name | — | Timestamp column to update on soft delete |
| Soft Delete: Update User ID Column Name | — | User ID column to update on soft delete |
| Soft Delete: Update User ID Column Literal Symbol | △ | Whether the user ID column value needs quoting: `(none)` or `quotes(')` |
| Soft Delete: Update User ID Column Value | △ | Value to set in the user ID column |

The three "Update User ID Column" fields must all be set together or all left empty.

---

## Transaction Behavior

- A commit is issued after each task in this sheet completes.
- The main SELECT is looped and committed every `jp.ecuacion.tool.housekeep-db.max-select-lines` rows (default `1000`), to limit memory usage and processing time. See [Configuration](page?id=housekeep-db/config&lang=en).
- SQLs for per-record soft / hard delete are logged at "debug" level, given how numerous they can be.
- If the main SELECT retrieves exactly 1 record, its SQL is logged twice: the loop always runs the SELECT once more to confirm the result count has reached zero before it ends.
