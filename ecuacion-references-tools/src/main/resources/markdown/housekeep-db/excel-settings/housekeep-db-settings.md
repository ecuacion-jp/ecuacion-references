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
If left empty and soft/hard delete is performed, the elapsed time since record creation/update is not considered.

| Column | Description |
| --- | --- |
| Expiration Check: Timestamp Column Name | Name of the timestamp column (e.g., `last_updated`) |
| Expiration Check: Timestamp Column Data Type | Column data type: `LocalDateTime` (timestamp without time zone) or `OffsetDateTime` (timestamp with time zone) |
| Expiration Check: Validity Days | Records older than this many days are targeted |

## Soft Delete Columns (Soft Delete Only)

Only used when `Soft / Hard Delete` is `Soft Delete`.

| Column | Required | Description |
| --- | --- | --- |
| Soft Delete Column Name | ○ (soft delete) | Name of the boolean delete flag column. Set to `true` on soft delete |
| Soft Delete: Update Timestamp Column Name | — | Timestamp column to update on soft delete. The value is automatically set to the current time |
| Soft Delete: Update User ID Column Name | — | User ID column to update on soft delete |
| Soft Delete: Update User ID Column Literal Symbol | △ | Whether the user ID column value needs quoting: `(none)` or `quotes(')` |
| Soft Delete: Update User ID Column Value | △ | Value to set in the user ID column |

The three "Update User ID Column" fields must all be set together or all left empty.

## Specifying a Soft Delete Column Name for Hard Delete

Even when `Soft / Hard Delete` is `Hard Delete`, `Soft Delete Column Name` can still be specified. In that case, only records where the soft delete column is `true` are targeted for hard deletion (records with `false` are excluded). This is useful for permanently deleting records that have already been soft-deleted.

## Examples

### Soft Delete

#### 1. Prepare the Test Table and Data

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    is_deleted boolean DEFAULT false,
    updated_at timestamp,
    updated_by varchar,
    PRIMARY KEY (num1)
);

INSERT INTO test_table (num1, char1) VALUES (123, 'abc');
```

#### 2. Configure the Excel File

| Task ID | DB Connection ID | Soft / Hard Delete | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Soft Delete | test_table | num1 | (none) |

| Soft Delete Column Name | Soft Delete: Update Timestamp Column Name | Soft Delete: Update User ID Column Name | Soft Delete: Update User ID Column Literal Symbol | Soft Delete: Update User ID Column Value |
| --- | --- | --- | --- | --- |
| is_deleted | updated_at | updated_by | quotes(') | batch-user |

Running the tool sets `is_deleted` to `true`, `updated_at` to the run time, and `updated_by` to `batch-user` (the record itself is not deleted).

### Expiration-Based Filtering

#### 1. Prepare the Test Table and Data

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    last_updated timestamp,
    PRIMARY KEY (num1)
);

-- Recorded as updated 5 days ago (should be deleted)
INSERT INTO test_table (num1, char1, last_updated) VALUES (123, 'abc', now() - interval '5 days');
-- Recorded as updated 1 day ago (should NOT be deleted)
INSERT INTO test_table (num1, char1, last_updated) VALUES (456, 'def', now() - interval '1 days');
```

#### 2. Configure the Excel File

| Task ID | DB Connection ID | Soft / Hard Delete | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | test_table | num1 | (none) |

| Expiration Check: Timestamp Column Name | Expiration Check: Timestamp Column Data Type | Expiration Check: Validity Days |
| --- | --- | --- |
| last_updated | LocalDateTime | 3 |

Running the tool deletes only the `num1=123` record, whose `last_updated` is more than 3 days old; the `num1=456` record is left alone.

> **How "Validity Days" is evaluated:** this is **time-based, not calendar-date-based**. Internally, the tool compares the current timestamp against `last_updated` and checks whether the difference exceeds `72 hours` (3 days), not whether the calendar date has changed 3 times. So depending on exactly when a day boundary falls, a record that's "3 calendar days old" may not yet qualify, while one that's only "2 calendar days old" may already qualify if more than 72 hours have actually elapsed.
