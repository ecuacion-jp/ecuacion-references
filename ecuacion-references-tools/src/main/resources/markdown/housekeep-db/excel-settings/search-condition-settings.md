Adds conditions to the search against the table named in the Housekeep DB Settings sheet. Leave the sheet empty if not needed.

For example, use this to delete only records where `exit_code` is `COMPLETED`.

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | ID of the task the condition applies to |
| Search Condition Column Name | ○ | Column name used in the WHERE condition |
| Search Condition Column Literal Symbol | ○ | Whether the column value needs quoting: `(none)` or `quotes(')` |
| Search Condition Column Value | ○ | Value used in the WHERE condition |

To set multiple conditions on one task, add multiple rows with the same Task ID (combined with AND).

## Example

### 1. Prepare the Test Table and Data

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    exit_code varchar,
    PRIMARY KEY (num1)
);

INSERT INTO test_table (num1, char1, exit_code) VALUES (123, 'abc', 'COMPLETED');
INSERT INTO test_table (num1, char1, exit_code) VALUES (456, 'def', 'FAILED');
```

### 2. Configure the Excel File

#### Housekeep DB Settings Sheet

| Task ID | DB Connection ID | Soft / Hard Delete | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | test_table | num1 | (none) |

#### Search Condition Settings Sheet

| Task ID | Search Condition Column Name | Search Condition Column Literal Symbol | Search Condition Column Value |
| --- | --- | --- | --- |
| task-1 | exit_code | quotes(') | COMPLETED |

Running the tool targets only `num1=123`, whose `exit_code` is `COMPLETED`; `num1=456`, with `FAILED`, is excluded.
