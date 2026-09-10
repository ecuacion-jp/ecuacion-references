`ecuacion-tool-housekeep-files` is a batch tool that performs routine file and directory management (housekeeping) tasks based on an Excel configuration file.

## Key Features

- Supports a wide range of file operations: copy, move, delete, compress, decompress
- Supports file transfer with remote servers via SFTP
- Wildcard path patterns for source files
- Flexible path management using path variables (`${VAR_NAME}` syntax, defined in `application.properties` etc.)
- Time-based filtering (process only files older than a specified number of days)
- Configurable behavior for missing source / conflicting destination: `IGNORE` / `WARN` / `ERROR`

## How It Works

All configuration is managed in a single Excel file. Set its path in `application.properties` (see [Configuration](page?id=housekeep-files/config&lang=en)); when the tool starts, the tasks in the Task Settings sheet are executed from top to bottom.

The Excel file contains the following two sheets:

| Sheet Name | Purpose |
| --- | --- |
| [Task Settings](page?id=housekeep-files/excel-settings&lang=en#task-settings-sheet) | List of file operation tasks to execute |
| [Server Auth Settings](page?id=housekeep-files/excel-settings&lang=en#server-auth-settings-sheet) | Authentication settings for SFTP servers |
