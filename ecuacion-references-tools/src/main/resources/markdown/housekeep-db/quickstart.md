This guide walks you through the simplest example: hard-deleting all records in a table.

## Prerequisites

- The JAR file and the sample Excel configuration file have been prepared following the [Setup](page?id=housekeep-db/setup&lang=en) instructions.
- Either PostgreSQL or MySQL / MariaDB is available.

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

Configure one of the following, depending on which database you're using.

**For PostgreSQL**

| DB Connection ID | Driver Name | Connection URL: Protocol | Connection URL: Server | Connection URL: Port | Connection URL: Database | Connection URL: Schema | Username | Password |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| test-conn | org.postgresql.Driver | postgresql | localhost | 5432 | mydb | public | myuser | mypassword |

**For MySQL / MariaDB**

| DB Connection ID | Driver Name | Connection URL: Protocol | Connection URL: Server | Connection URL: Port | Connection URL: Database | Connection URL: Schema | Username | Password |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| test-conn | org.mariadb.jdbc.Driver | mysql | localhost | 3306 | mydb | | myuser | mypassword |

#### Housekeep DB Settings Sheet

| Task ID | DB Connection ID | Soft / Hard Delete | Table Name | ID Column Name | ID Column Literal Symbol | (remaining columns empty) |
| --- | --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | test_table | num1 | (none) | |

**Points:**
- `ID Column Literal Symbol`: `num1` is an integer, so no quotes are needed → specify `(none)`
  - For varchar columns, specify `quotes(')`

### 3. Configure application.properties

Next to the JAR, create (or edit) `application.properties` and point it at the excel file you just configured.

```properties
jp.ecuacion.tool.housekeep-db.excel-path=/path/to/your-settings.xlsx
```

### 4. Run the Tool

```bash
java -jar ecuacion-tool-housekeep-db-x.x.x.jar
```

If the record inserted earlier has been deleted from `test_table`, the run was successful.
