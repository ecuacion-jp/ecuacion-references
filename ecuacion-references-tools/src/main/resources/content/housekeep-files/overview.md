# housekeep-files Overview

`ecuacion-tool-housekeep-files` is a batch tool that performs routine file and directory management (housekeeping) tasks based on an Excel configuration file.

## Key Features

- Supports a wide range of file operations: copy, move, delete, compress, decompress
- Supports file transfer with remote servers via SFTP
- Wildcard path patterns for source files
- Flexible path management using path variables (`${VAR_NAME}` syntax)
- Time-based filtering (process only files older than a specified number of days)
- Configurable behavior for missing source / conflicting destination: `IGNORE` / `WARN` / `ERROR`

## How It Works

All configuration is managed in a single Excel file. When the tool starts, you pass the path of the Excel file as an argument, and the tasks listed in the task sheet are executed from top to bottom.

```
java -jar ecuacion-tool-housekeep-files-x.x.x.jar excelPath=/path/to/settings.xlsx
```

## Downloading the Module

The JAR file is available at:

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-housekeep-files/
```

## Documentation

- [Setup](/public/showMarkdown/page?id=housekeep-files/setup&lang=en)
- [Quick Start](/public/showMarkdown/page?id=housekeep-files/quickstart&lang=en)
- [Excel Settings](/public/showMarkdown/page?id=housekeep-files/excel-settings&lang=en)
- [Task Patterns](/public/showMarkdown/page?id=housekeep-files/task-patterns&lang=en)
