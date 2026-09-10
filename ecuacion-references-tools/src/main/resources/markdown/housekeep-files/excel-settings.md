housekeep-files is entirely controlled by a single Excel file.
The Excel file contains the following two sheets (there is also a Basic Settings sheet holding `format-version` etc., but it's a hidden sheet you normally don't need to touch):

| Sheet Name | Purpose |
| --- | --- |
| Task Settings | List of file operation tasks to execute |
| Server Auth Settings | Authentication settings for SFTP servers |

---

## Task Settings Sheet

Each row defines one file operation task. Tasks are executed from top to bottom.

### Column Reference

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Identifies the task. Alphanumerics, spaces, and some symbols, max 10 characters |
| Task Name | ○ | Human-readable name for the task. Max 40 characters |
| Task Pattern (display) | — | Human-readable task pattern name (ignored during reading) |
| Task Pattern | ○ | Type of operation. Specify a `TaskPtnEnum` value (see [Task Patterns](page?id=housekeep-files/task-patterns&lang=en)) |
| Remote Server | △ | Required for SFTP tasks. Must match the server name in the Server Auth Settings sheet |
| Source Path | △ | Source file or directory path. Path variables (`${VAR_NAME}`) and wildcards (`*`, `?`) are supported |
| Is Src Dir | △ | `true` / `false`. Set `true` when the source path is a directory |
| Src Path Pending Days | △ | Number of days since last modification to target (0–1000). `0` targets all files regardless of age |
| Action if No Src | △ | Action when the source path does not exist: `IGNORE` / `WARN` / `ERROR` |
| Dest Path | △ | Destination file or directory path. Path variables (`${VAR_NAME}`) are supported (wildcards not allowed) |
| Is Dest Dir | △ | `true` / `false`. Set `true` to place the file inside the destination directory using the original filename |
| Overwrite Dest | △ | `true` / `false`. Whether to overwrite if the destination already exists |
| Action if Dest Exists | △ | Action when the destination already exists: `IGNORE` / `WARN` / `ERROR` |

△ = required, optional, or prohibited depending on the task pattern (see [Task Patterns](page?id=housekeep-files/task-patterns&lang=en)).

### Input Rule for Source Path Fields

The four fields — Source Path, Is Src Dir, Src Path Pending Days, Action if No Src — must be **all filled or all empty**. Filling in only some of them causes an error.

### Input Rule for Destination Path Fields

Similarly, the four fields — Dest Path, Is Dest Dir, Overwrite Dest, Action if Dest Exists — must be **all filled or all empty**.

### Wildcard Support

Wildcards (`*`, `?`) can be used in the Source Path. When a wildcard is included, the task is executed for every matching file.

```
/data/logs/*.log         # All .log files
/data/backup/202?-*.zip  # zip files starting with 202x
```

### Path Notation Rules

Use `/` as the path separator so paths also work on Linux (`\` is automatically converted to `/` internally, so it will still work, but `/` is recommended).

The Source Path and Dest Path fields can also embed the following built-in variables using `${VAR_NAME}` notation:

| Variable | Value |
| --- | --- |
| `${TASK_NAME}` | The name of the task currently being executed |
| `${HOSTNAME}` | The hostname of the machine running the tool |
| `${YYYYMMDD}` | The current date (`YYYYMMDD` format, 8 digits) |
| `${TIMESTAMP}` | The current timestamp (`YYYYMMDD-HHMMSS.sss` format) |

For any other variable name, define it as a path variable in `application.properties` (or OS environment variables, JVM system properties, or any other source Spring Boot's Environment can resolve). See [Configuration](page?id=housekeep-files/config&lang=en) for details. If a property has the same name as a built-in variable, the built-in variable's value takes precedence.

### Task Pattern Display Name

The "Task Pattern (display)" column is for readability only; it is ignored when the tool reads the file. The tool only uses the "Task Pattern" column. See [Task Patterns](page?id=housekeep-files/task-patterns&lang=en) for the list of valid values.

### Action Values

| Value | Behavior |
| --- | --- |
| `IGNORE` | Silently skip and move on to the next task |
| `WARN` | Log a warning and move on to the next task |
| `ERROR` | Throw an error and stop the batch |

---

## Server Auth Settings Sheet

Only needed when using SFTP tasks. Leave the sheet empty if SFTP is not used.

| Column | Required | Description |
| --- | --- | --- |
| Server Name | ○ | Identifier for the server. Must match "Remote Server" in the Task Settings sheet. Max 40 characters |
| protocol | ○ | Connection protocol. Currently only `SFTP` is supported |
| port | ○ | Connection port number (0–99999) |
| Auth Type | ○ | `PASSWORD` (password auth) / `KEY` (public key auth) / `KERBEROS` (Kerberos auth) |
| Username | △ | Login username (max 40 characters) |
| password / passphrase | △ | Password, or passphrase for the private key (max 30 characters) |
| Key Path | △ | Path to the private key file for `KEY` auth (max 300 characters) |

### Input Requirements by Auth Type

| Auth Type | Username | password / passphrase | Key Path |
| --- | --- | --- | --- |
| `PASSWORD` | Required | Enter password | Not needed |
| `KEY` | Required | Enter passphrase if any | Enter key path |
| `KERBEROS` | Not needed | Not needed | Not needed |

---

## Empty Rows as Data Delimiters

Within any sheet's table, an empty row signals the end of data. Rows after the empty row are ignored.
You can leave unused configuration rows after an empty row without affecting execution.
