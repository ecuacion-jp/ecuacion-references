# housekeep-db Quick Start

This guide walks you through the simplest example: hard-deleting all records in a table.

## Prerequisites

- The JAR file and the sample Excel configuration file have been prepared following the [Setup](/public/showMarkdown/page?id=housekeep-db/setup&lang=en) instructions.
- PostgreSQL is available.

## Steps

### 1. Prepare the Test Table and Data

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    PRIMARY KEY (num1)
);

INSERT INTO test_table (num1, char1) VALUES (123, 'abc');
```

### 2. Configure the Excel File

Open the sample Excel file and fill in the following two sheets.

#### DB Connection Settings Sheet

| DB Connection ID | Driver Name | Connection URL: Protocol | Connection URL: Server | Connection URL: Port | Connection URL: Database | Connection URL: Schema | Username | Password |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| test-conn | postgresql | postgresql | localhost | 5432 | mydb | public | myuser | mypassword |

#### Housekeep DB Settings Sheet

| Task ID | DB Connection ID | Soft / Hard Delete | Soft / Hard Delete (internal value) | Table Name | ID Column Name | ID Column Literal Symbol | (remaining columns empty) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | HARD_DELETE | test_table | num1 | (none) | |

**Points:**
- `Soft / Hard Delete (internal value)`: the value the tool reads — enter `HARD_DELETE`
- `ID Column Literal Symbol`: `num1` is an integer, so no quotes are needed → specify `(none)`
  - For varchar columns, specify `quotes(')`

### 3. Run the Tool

```bash
java -jar ecuacion-tool-housekeep-db-x.x.x.jar excelPath=/path/to/your-settings.xlsx
```

If the record inserted earlier has been deleted from `test_table`, the run was successful.

---

## Filtering by Expiration Days

To target only records older than a certain number of days, configure the `Expiration Check` columns.

Example — delete records where `last_updated` column (LocalDateTime type) is 28 or more days ago:

| Expiration Check: Timestamp Column Name | Expiration Check: Timestamp Column Data Type | Expiration Check: Validity Days |
| --- | --- | --- |
| last_updated | LocalDateTime | 28 |

Timestamp column data types:
- `LocalDateTime`: `timestamp without time zone`
- `OffsetDateTime`: `timestamp with time zone`

---

## Performing Soft Delete

To perform soft delete instead of hard delete:

1. Change `Soft / Hard Delete` to `Soft Delete` and `Soft / Hard Delete (internal value)` to `SOFT_DELETE`
2. Specify the delete flag column name in `Soft Delete Column Name` (e.g., `deleted`)

The `deleted` column of the matching records will be set to `true`.
