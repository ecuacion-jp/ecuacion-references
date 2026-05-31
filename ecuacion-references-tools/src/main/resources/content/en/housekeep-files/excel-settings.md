# housekeep-files Excel Settings

housekeep-files is entirely controlled by a single Excel file.
The Excel file contains the following four sheets:

| Sheet Name | Purpose |
| --- | --- |
| Basic Settings | Tool-wide settings (system name, etc.) |
| Task Settings | List of file operation tasks to execute |
| Path Settings | Path variable definitions |
| Server Auth Settings | Authentication settings for SFTP servers |

---

## Basic Settings Sheet

Entries are written as key-value pairs.

| Key | Description |
| --- | --- |
| `env-name` | Name to identify the system / environment. Used in log output and warning email subjects. Alphanumerics, hyphens, spaces, etc. (some symbols excluded), max 40 characters |

---

## Task Settings Sheet

Each row defines one file operation task. Tasks are executed from top to bottom.

### Column Reference

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Identifies the task. Alphanumerics, spaces, and some symbols, max 10 characters |
| Task Name | ○ | Human-readable name for the task. Max 40 characters |
| Task Pattern (display) | — | Human-readable task pattern name (ignored during reading) |
| Task Pattern | ○ | Type of operation. Specify a `TaskPtnEnum` value (see [Task Patterns](/public/en/article?id=housekeep-files/task-patterns)) |
| Remote Server | △ | Required for SFTP tasks. Must match the server name in the Server Auth Settings sheet |
| Source Path | △ | Source file or directory path. Path variables (`${VAR_NAME}`) and wildcards (`*`, `?`) are supported |
| Is Src Dir | △ | `true` / `false`. Set `true` when the source path is a directory |
| Expiration Unit | △ | Currently only `DAY` is supported |
| Expiration Value | △ | Number of days since last modification (0–1000). `0` targets all files regardless of age |
| Action if No Src | △ | Action when the source path does not exist: `IGNORE` / `WARN` / `ERROR` |
| Dest Path | △ | Destination file or directory path. Path variables (`${VAR_NAME}`) are supported (wildcards not allowed) |
| Is Dest Dir | △ | `true` / `false`. Set `true` to place the file inside the destination directory using the original filename |
| Overwrite Dest | △ | `true` / `false`. Whether to overwrite if the destination already exists |
| Action if Dest Exists | △ | Action when the destination already exists: `IGNORE` / `WARN` / `ERROR` |
| options | — | Reserved for future use |

△ = required, optional, or prohibited depending on the task pattern (see [Task Patterns](/public/en/article?id=housekeep-files/task-patterns)).

### Input Rule for Source Path Fields

The five fields — Source Path, Is Src Dir, Expiration Unit, Expiration Value, Action if No Src — must be **all filled or all empty**. Filling in only some of them causes an error.

### Input Rule for Destination Path Fields

Similarly, the four fields — Dest Path, Is Dest Dir, Overwrite Dest, Action if Dest Exists — must be **all filled or all empty**.

### Wildcard Support

Wildcards (`*`, `?`) can be used in the Source Path. When a wildcard is included, the task is executed for every matching file.

```
/data/logs/*.log         # All .log files
/data/backup/202?-*.zip  # zip files starting with 202x
```

### Task Pattern Display Name

The "Task Pattern (display)" column is for readability only; it is ignored when the tool reads the file. The tool only uses the "Task Pattern" column. See [Task Patterns](/public/en/article?id=housekeep-files/task-patterns) for the list of valid values.

### Action Values

| Value | Behavior |
| --- | --- |
| `IGNORE` | Silently skip and move on to the next task |
| `WARN` | Log a warning and move on to the next task |
| `ERROR` | Throw an error and stop the batch |

---

## Path Settings Sheet

Defines path variables that can be referenced in the Task Settings sheet.

| Column | Required | Description |
| --- | --- | --- |
| Variable Name | ○ | Uppercase letters, digits, and underscores only (e.g., `BASE_DIR`, `LOG_PATH`). Max 50 characters |
| Value | ○ | The actual path. Max 300 characters |

Reference a variable as `${BASE_DIR}` in task path fields.
Nested variable references in path values are not supported.

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
