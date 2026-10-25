Defines one housekeeping task (soft / hard delete, or abnormal data check) per row. Tasks are executed from top to bottom.

## Basic Columns

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Identifies the task |
| DB Connection ID | ○ | Must match an ID in the DB Connection Settings sheet |
| Process Type | ○ | Display value (`Soft Delete`, `Hard Delete` or `Abnormal Data Check`) |
| Process Type (internal value) | ○ | Value read by the tool: `SOFT_DELETE`, `HARD_DELETE` or `ABNORMAL_DATA_CHECK` |
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

Only used when `Process Type` is `Soft Delete` (`Soft Delete Column Name` can also optionally be specified for `Hard Delete` and `Abnormal Data Check` - see below).

| Column | Required | Description |
| --- | --- | --- |
| Soft Delete Column Name | ○ (soft delete) | Name of the boolean delete flag column. Set to `true` on soft delete |
| Soft Delete: Update Timestamp Column Name | — | Timestamp column to update on soft delete. The value is automatically set to the current time |
| Soft Delete: Update User ID Column Name | — | User ID column to update on soft delete |
| Soft Delete: Update User ID Column Literal Symbol | △ | Whether the user ID column value needs quoting: `(none)` or `quotes(')` |
| Soft Delete: Update User ID Column Value | △ | Value to set in the user ID column |

The three "Update User ID Column" fields must all be set together or all left empty.

## Specifying a Soft Delete Column Name for Hard Delete

Even when `Process Type` is `Hard Delete`, `Soft Delete Column Name` can still be specified. In that case, only records where the soft delete column is `true` are targeted for hard deletion (records with `false` are excluded). This is useful for permanently deleting records that have already been soft-deleted.

## Abnormal Data Check

When `Process Type` is `Abnormal Data Check`, the records matching the conditions are not deleted; instead, **their very existence is treated as something gone wrong and reported as a warning**. Use it to detect records that should never exist, such as "records still not marked as processed 90 days later".

- The conditions are specified the same way as for soft / hard delete: records matching the [Search Condition Settings](page?id=housekeep-db/excel-settings/search-condition-settings&lang=en) sheet and the (optional) expiration-based filtering are targeted
- When `Soft Delete Column Name` is specified, records already soft-deleted (delete flag = `true`) are excluded from the check
- The "Soft Delete: Update ..." columns are not used and must be left empty
- The [Related Table Settings](page?id=housekeep-db/excel-settings/related-table-settings&lang=en) sheet cannot be used (a row linked to an abnormal data check task is an error)
- No record is ever updated or deleted

When matching records are found, the count and IDs are logged at `WARN` level per task, and after all tasks have run, the findings are sent together in **one warning email** (per task: the count and the first 10 IDs in ID order, noting when there are more). See [Configuration](page?id=housekeep-db/config&lang=en) for the email recipients, subject, and other settings.

> **Note:** Even when records are found, the tool ends normally (exit code `0`). Check the findings through the warning email or the `WARN` level log.

### Combining with Delete Tasks

The term and conditions of an abnormal data check are set independently from delete tasks. For example, if you want to soft-delete records soon after they're processed, but how long it takes for them to be processed varies, put two tasks side by side:

| Task ID | Process Type | Expiration Check: Validity Days | Condition in the Search Condition Settings sheet |
| --- | --- | --- | --- |
| task-1 | Soft Delete | 7 | `processed = true` |
| task-2 | Abnormal Data Check | 90 | `processed = false` |

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

| Task ID | DB Connection ID | Process Type | Table Name | ID Column Name | ID Column Literal Symbol |
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

| Task ID | DB Connection ID | Process Type | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | test_table | num1 | (none) |

| Expiration Check: Timestamp Column Name | Expiration Check: Timestamp Column Data Type | Expiration Check: Validity Days |
| --- | --- | --- |
| last_updated | LocalDateTime | 3 |

Running the tool deletes only the `num1=123` record, whose `last_updated` is more than 3 days old; the `num1=456` record is left alone.

> **How "Validity Days" is evaluated:** this is **time-based, not calendar-date-based**. Internally, the tool compares the current timestamp against `last_updated` and checks whether the difference exceeds `72 hours` (3 days), not whether the calendar date has changed 3 times. So depending on exactly when a day boundary falls, a record that's "3 calendar days old" may not yet qualify, while one that's only "2 calendar days old" may already qualify if more than 72 hours have actually elapsed.

### Abnormal Data Check

#### 1. Prepare the Test Table and Data

```sql
CREATE TABLE test_table (
    num1 integer,
    processed boolean,
    last_updated timestamp,
    PRIMARY KEY (num1)
);

-- Left unprocessed since 100 days ago (detected)
INSERT INTO test_table (num1, processed, last_updated) VALUES (123, false, now() - interval '100 days');
-- Processed 100 days ago (not detected)
INSERT INTO test_table (num1, processed, last_updated) VALUES (456, true, now() - interval '100 days');
-- Registered 1 day ago, not processed yet (not detected)
INSERT INTO test_table (num1, processed, last_updated) VALUES (789, false, now() - interval '1 days');
```

#### 2. Configure the Excel File

##### Housekeep DB Settings Sheet

| Task ID | DB Connection ID | Process Type | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Abnormal Data Check | test_table | num1 | (none) |

| Expiration Check: Timestamp Column Name | Expiration Check: Timestamp Column Data Type | Expiration Check: Validity Days |
| --- | --- | --- |
| last_updated | LocalDateTime | 90 |

##### Search Condition Settings Sheet

| Task ID | Search Condition Column Name | Search Condition Operator | Search Condition Column Literal Symbol | Search Condition Column Value |
| --- | --- | --- | --- | --- |
| task-1 | processed | equal | (none) | false |

Running the tool detects only the `num1=123` record and sends a warning email like the following (no record is updated or deleted):

```
hostname: your-server

Record(s) that should not exist were found by the abnormal data check task(s) below:

- Task ID: task-1
  Table  : test_table
  Count  : 1
  num1: 123
```
