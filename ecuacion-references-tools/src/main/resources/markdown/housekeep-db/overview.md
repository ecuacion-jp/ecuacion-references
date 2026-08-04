`ecuacion-tool-housekeep-db` is a batch tool that automatically housekeeps (cleans up) records in a database.
It performs soft delete or hard delete based on an Excel configuration file.

## Key Features

- **Hard Delete**: Physically deletes records matching the conditions
- **Soft Delete**: Updates a boolean "deleted" column to `true` instead of physically deleting
- **Expiration-based filtering**: Targets only records where a timestamp column exceeds a specified number of days
- **Related table handling**: Delete related table records together, or skip deletion when related records exist
- **Additional WHERE conditions**: Filter records by column values

## Constraints

- Tables must have a single-column primary key or unique index (composite keys are not supported)
- The soft-delete flag column must be of type `bool` (`true` means deleted)
- Only **PostgreSQL** is supported at this time

## How It Works

All configuration is managed in a single Excel file. When the tool starts, you pass the Excel file path as an argument, and the tasks in the Housekeep DB Settings sheet are executed from top to bottom.
