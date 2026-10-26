Sets the columns to rewrite, and the values to rewrite them with, for tasks whose `Process Type` in the Housekeep DB Settings sheet is `Update`. Write one column per row; to rewrite multiple columns in one task, add multiple rows with the same Task ID.

An `Update` task needs at least one row. Conversely, a row linked to a task other than `Update` is an error. Leave the sheet empty if no `Update` task is used.

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Must match a task ID in the Housekeep DB Settings sheet |
| Update Column Name | ○ | Name of the column to rewrite |
| Update Value Type | ○ | Display value (`Fixed Value`, `Current Timestamp` or `NULL`) |
| Update Value Type (internal value) | ○ | Value read by the tool: `FIXED_VALUE`, `CURRENT_TIMESTAMP` or `NULL` |
| Update Column Literal Symbol | △ | Whether the value needs quoting: `(none)` or `quotes(')` |
| Update Column Value | △ | Value to set in the column |

The column is rewritten as follows, depending on the Update Value Type:

| Update Value Type | Value written | Update Column Literal Symbol / Update Column Value |
| --- | --- | --- |
| Fixed Value | The value in Update Column Value | Both required |
| Current Timestamp | The date and time the tool runs (only the date part for a date column) | Both must be left empty |
| NULL | NULL | Both must be left empty |

## Records Already Rewritten

An `Update` task **does not automatically exclude records it has already rewritten**. As long as a record matches the conditions, it is rewritten again on every run.

To avoid processing the same record twice, include a column that marks the record as processed (e.g., an "anonymized" flag) among the columns to rewrite, and write a condition excluding such records on that column in the [Search Condition Settings](page?id=housekeep-db/excel-settings/search-condition-settings&lang=en) sheet (see the example below).

> **Warning:** If you forget that condition, every target record is rewritten on every run. A `Current Timestamp` column is overwritten each time, and related table records are deleted each time as well.

## Example

This example anonymizes inquiries closed more than 90 days ago: it rewrites the reference to the general user with a system account ID and the phone number with NULL, and sets the anonymized flag and timestamp. It also hard-deletes the linked attachment rows.

### 1. Prepare the Test Tables and Data

```sql
CREATE TABLE inquiry (
    id integer,
    status varchar,
    closed_at timestamp,
    user_ref varchar,
    phone varchar,
    anonymized boolean NOT NULL DEFAULT false,
    anonymized_at timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE inquiry_attachment (
    id integer,
    inquiry_id integer,
    PRIMARY KEY (id)
);

INSERT INTO inquiry (id, status, closed_at, user_ref, phone) VALUES (1, 'CLOSED', now() - interval '100 days', 'U001', '090-0000-0000');
INSERT INTO inquiry (id, status, closed_at, user_ref, phone) VALUES (2, 'CLOSED', now() - interval '10 days', 'U002', '080-0000-0000');
INSERT INTO inquiry_attachment (id, inquiry_id) VALUES (11, 1);
INSERT INTO inquiry_attachment (id, inquiry_id) VALUES (12, 2);
```

### 2. Configure the Excel File

#### Housekeep DB Settings Sheet

| Task ID | DB Connection ID | Process Type | Table Name | ID Column Name | ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Update | inquiry | id | (none) |

| Expiration Check: Date / Timestamp Column Name | Expiration Check: Validity Days |
| --- | --- |
| closed_at | 90 |

#### Search Condition Settings Sheet

| Task ID | Search Condition Column Name | Search Condition Operator | Search Condition Column Literal Symbol | Search Condition Column Value |
| --- | --- | --- | --- | --- |
| task-1 | status | equal | quotes(') | CLOSED |
| task-1 | anonymized | not equal | (none) | true |

The second row is the condition excluding records already anonymized.

#### Update Column Settings Sheet

| Task ID | Update Column Name | Update Value Type | Update Column Literal Symbol | Update Column Value |
| --- | --- | --- | --- | --- |
| task-1 | user_ref | Fixed Value | quotes(') | SYS001 |
| task-1 | phone | NULL | | |
| task-1 | anonymized | Fixed Value | (none) | true |
| task-1 | anonymized_at | Current Timestamp | | |

#### Related Table Settings Sheet

| Task ID | Related Table Process Pattern | Target Table Column Name | Related Table Name | Related Table ID Column Name | Related Table ID Column Literal Symbol |
| --- | --- | --- | --- | --- | --- |
| task-1 | Delete | id | inquiry_attachment | inquiry_id | (none) |

As Related Table ID Column Name, specify the attachment's column referencing the inquiry (`inquiry_id`). All the attachments linked to one inquiry are deleted, however many there are.

Running the tool rewrites only the inquiry `id=1`, closed more than 90 days ago (`user_ref` becomes `SYS001`, `phone` NULL, `anonymized` `true` and `anonymized_at` the time of the run), and hard-deletes its attachment `id=11`. The inquiry `id=2` and the attachment `id=12` are left as they are. Running it again does not rewrite `id=1`, since `anonymized = true` excludes it.
