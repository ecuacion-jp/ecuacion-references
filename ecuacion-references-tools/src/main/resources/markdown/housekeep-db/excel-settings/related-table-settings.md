Configures processing of related tables. Leave the sheet empty if not needed.

Two processing patterns are available:

| Pattern | Behavior |
| --- | --- |
| Delete | Delete related table records first, then delete the target table record |
| Check and Skip Delete | Skip deletion of the target record if a related record exists |

## When to Use This Feature

### Delete

Soft-deleting or deleting records should ideally be handled per table on its own. However, the related table sometimes has no timestamp or similar column to determine whether its own records should be deleted, so the related table alone provides no basis for that decision. This pattern exists for that case: it deletes the related table's records in step with the deletion of the target table, which does have a basis for its own delete decision. Because of this, there is no way to specify delete conditions (such as a timestamp column) on the related table side.

Whether the related table is soft-deleted or hard-deleted cannot be set independently on this sheet - it always follows the task-wide "Soft / Hard Delete" column in the Housekeep DB Settings sheet. That is, when the task is Hard Delete, the related table is deleted (DELETE) just like the target table; when the task is Soft Delete, the related table is soft-deleted too (its Soft Delete Column is updated to true).

### Check and Skip Delete

Check and Skip Delete assumes that the related table has already been soft-deleted or deleted under appropriate conditions beforehand.

When the task is Soft Delete (that is, `Soft Delete` is selected in the "Soft / Hard Delete" column of the Housekeep DB Settings sheet), related records that are already soft-deleted are not counted as "existing": the soft delete of the target record is skipped only when a not-yet-soft-deleted related record exists. For this check, the Soft Delete Column Name is required when the task is Soft Delete. When the task is Hard Delete, plain record existence is checked.

## Column Reference

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Must match a Task ID in the Housekeep DB Settings sheet |
| Related Table Process Pattern | ○ | Display pattern name (`Delete` or `Check and Skip Delete`) |
| Target Table Column Name | ○ | Column in the target table used to join with the related table |
| Related Table Name | ○ | Name of the related table |
| Related Table ID Column Name | ○ | ID column (primary key or unique index) of the related table |
| Related Table ID Column Literal Symbol | ○ | Whether the related table ID column needs quoting: `(none)` or `quotes(')` |

**Soft Delete Columns**

Configure these columns as follows:

- Soft Delete Column Name: required whenever the task is Soft Delete, regardless of the process pattern. The Delete pattern uses it to soft-delete related table records; the Check and Skip Delete pattern uses it to exclude already-soft-deleted records from the existence check. When the task is Hard Delete it is optional: specifying it with the Delete pattern deletes only related records already soft-deleted (the column = true); the Check and Skip Delete pattern does not use it
- The other "Soft Delete: ..." columns: optional when the task is Soft Delete (set them when a timestamp/user ID column needs to be updated on soft delete). They must be left empty when the task is Hard Delete. The value of Soft Delete: Update Timestamp Column Name is automatically set to the current time. The three "Update User ID Column" fields must all be set together or all left empty

For details on these columns, see the "Soft Delete" example in [Housekeep DB Settings](page?id=housekeep-db/excel-settings/housekeep-db-settings&lang=en) (the columns work the same way there).

| Column | Description |
| --- | --- |
| Soft Delete Column Name | Delete flag column of the related table |
| Soft Delete: Update Timestamp Column Name | Timestamp column to update on soft delete |
| Soft Delete: Update User ID Column Name | User ID column to update on soft delete |
| Soft Delete: Update User ID Column Literal Symbol | Whether the user ID column value needs quoting |
| Soft Delete: Update User ID Column Value | Value to set in the user ID column |

## Examples

### Delete

#### 1. Prepare the Test Table and Data

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    PRIMARY KEY (num1)
);

CREATE TABLE related_table (
    id integer,
    parent_id integer,
    PRIMARY KEY (id)
);

INSERT INTO test_table (num1, char1) VALUES (123, 'abc');
INSERT INTO related_table (id, parent_id) VALUES (1, 123);
```

#### 2. Configure the Excel File

##### Housekeep DB Settings Sheet

| Task ID | DB Connection ID | Soft / Hard Delete | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | test_table | num1 | (none) |

##### Related Table Settings Sheet

| Task ID | Related Table Process Pattern | Target Table Column Name | Related Table Name | Related Table ID Column Name | Related Table ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | Delete | num1 | related_table | parent_id | (none) |

Running the tool first deletes the `related_table` record where `parent_id=123` (`id=1`), then deletes the `test_table` record where `num1=123`.

### Check and Skip Delete

#### 1. Prepare the Test Table and Data

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    PRIMARY KEY (num1)
);

CREATE TABLE related_table (
    id integer,
    parent_id integer,
    PRIMARY KEY (id)
);

INSERT INTO test_table (num1, char1) VALUES (123, 'abc');
INSERT INTO test_table (num1, char1) VALUES (456, 'def');
-- Only num1=123 has a related record
INSERT INTO related_table (id, parent_id) VALUES (1, 123);
```

#### 2. Configure the Excel File

##### Housekeep DB Settings Sheet

| Task ID | DB Connection ID | Soft / Hard Delete | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | test_table | num1 | (none) |

##### Related Table Settings Sheet

| Task ID | Related Table Process Pattern | Target Table Column Name | Related Table Name | Related Table ID Column Name | Related Table ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | Check and Skip Delete | num1 | related_table | parent_id | (none) |

Running the tool skips deletion of `num1=123`, which has a related record, and deletes only `num1=456`, which has none.
