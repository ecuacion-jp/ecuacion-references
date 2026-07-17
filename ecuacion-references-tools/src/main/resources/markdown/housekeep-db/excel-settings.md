housekeep-db is entirely controlled by a single Excel file.
The Excel file contains the following four sheets:

| Sheet Name | Purpose |
| --- | --- |
| DB Connection Settings | Database connection information |
| Housekeep DB Settings | Housekeeping task definitions |
| Related Table Settings | Related table processing configuration |
| Search Condition Settings | Additional WHERE conditions |

---

## DB Connection Settings Sheet

Defines database connections used by tasks. Multiple connections can be registered.

| Column | Required | Description |
| --- | --- | --- |
| DB Connection ID | ○ | Identifier for the connection. Referenced from the Housekeep DB Settings sheet |
| Driver Name | ○ | JDBC driver name (e.g., `postgresql`) |
| Connection URL: Protocol | ○ | Protocol part of the connection URL (e.g., `postgresql`) |
| Connection URL: Server | ○ | Database server hostname or IP address |
| Connection URL: Port | ○ | Port number |
| Connection URL: Database | ○ | Database name |
| Connection URL: Schema | — | Schema name (optional) |
| Username | ○ | Database username |
| Password | ○ | Database password |

---

## Housekeep DB Settings Sheet

Defines one deletion task per row. Tasks are executed from top to bottom.

### Basic Columns

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

### Expiration-Based Filtering (Optional)

Configure these columns to target only records older than a specified number of days.
All three columns must be set together or left empty together.

| Column | Description |
| --- | --- |
| Expiration Check: Timestamp Column Name | Name of the timestamp column (e.g., `last_updated`) |
| Expiration Check: Timestamp Column Data Type | Column data type: `LocalDateTime` (timestamp without time zone) or `OffsetDateTime` (timestamp with time zone) |
| Expiration Check: Validity Days | Records older than this many days are targeted |

### Soft Delete Columns (Soft Delete Only)

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

## Related Table Settings Sheet

Configures processing of related tables. Leave the sheet empty if not needed.

Two processing patterns are available:

| Pattern | Internal Value | Behavior |
| --- | --- | --- |
| Delete | `DELETE` | Delete related table records first, then delete the target table record |
| Check and Skip Delete | `CHECK_AND_SKIP_DELETE` | Skip deletion of the target record if a related record exists |

### Column Reference

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Must match a Task ID in the Housekeep DB Settings sheet |
| Soft / Hard Delete (internal value) | ○ | `SOFT_DELETE` or `HARD_DELETE`. Must match the corresponding task |
| Related Table Process Pattern | ○ | Display pattern name (`Delete` or `Check and Skip Delete`) |
| Related Table Process Pattern (internal value) | ○ | `DELETE` or `CHECK_AND_SKIP_DELETE` |
| Target Table Column Name | ○ | Column in the target table used to join with the related table |
| Related Table Name | ○ | Name of the related table |
| Related Table ID Column Name | ○ | ID column (primary key or unique index) of the related table |
| Related Table ID Column Literal Symbol | ○ | Whether the related table ID column needs quoting: `(none)` or `quotes(')` |

**Soft Delete Columns for Related Table (Delete Pattern, Optional)**

When soft-deleting related table records, configure these columns:

| Column | Description |
| --- | --- |
| Soft Delete Column Name | Delete flag column of the related table |
| Soft Delete: Update Timestamp Column Name | Timestamp column to update on soft delete |
| Soft Delete: Update User ID Column Name | User ID column to update on soft delete |
| Soft Delete: Update User ID Column Literal Symbol | Whether the user ID column value needs quoting |
| Soft Delete: Update User ID Column Value | Value to set in the user ID column |

---

## Search Condition Settings Sheet

Adds additional WHERE conditions to a task. Leave the sheet empty if not needed.

For example, to delete only records where `exit_code` is `COMPLETED`:

| Task ID | Search Condition Column Name | Search Condition Column Literal Symbol | Search Condition Column Value |
| --- | --- | --- | --- |
| task-1 | exit_code | quotes(') | COMPLETED |

Multiple rows with the same Task ID are combined with AND.

---

## Transaction Behavior

- A commit is issued after each task in the Housekeep DB Settings sheet completes.
- When a single task targets more than 1,000 records, commits are issued every 1,000 records.
