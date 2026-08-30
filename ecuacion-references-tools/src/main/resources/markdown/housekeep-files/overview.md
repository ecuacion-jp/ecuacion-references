`ecuacion-tool-housekeep-files` is a batch tool that performs routine file and directory management (housekeeping) tasks based on an Excel configuration file.

## Key Features

- Supports a wide range of file operations: copy, move, delete, compress, decompress
- Supports file transfer with remote servers via SFTP
- Wildcard path patterns for source files
- Flexible path management using path variables (`${VAR_NAME}` syntax)
- Time-based filtering (process only files older than a specified number of days)
- Configurable behavior for missing source / conflicting destination: `IGNORE` / `WARN` / `ERROR`

## How It Works

All configuration is managed in a single Excel file. Set its path in `application.properties` placed next to the JAR; when the tool starts, the tasks listed in the task sheet are executed from top to bottom.

```properties
jp.ecuacion.tool.housekeep-files.excel-path=/path/to/settings.xlsx
```

```
java -jar ecuacion-tool-housekeep-files-x.x.x.jar
```
