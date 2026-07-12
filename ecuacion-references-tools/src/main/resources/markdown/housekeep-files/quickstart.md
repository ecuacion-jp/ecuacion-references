# housekeep-files Quick Start

This guide walks you through the simplest example: moving a local file to another directory.

## Prerequisites

- The JAR file and the sample Excel configuration file have been prepared following the [Setup](/public/showMarkdown/page?id=housekeep-files/setup&lang=en) instructions.

## Steps

### 1. Create Test Files

```bash
mkdir -p /tmp/hkf-test/from
mkdir -p /tmp/hkf-test/to
touch /tmp/hkf-test/from/sample.txt
```

### 2. Configure the Excel File

Open the sample Excel file and fill in the following three sheets.

#### Basic Settings Sheet

| Key | Value |
| --- | --- |
| env-name | my-system |

#### Task Settings Sheet

| Task ID | Task Name | Task Pattern (display) | Task Pattern | Remote Server | Source Path | Is Src Dir | Expiration Unit | Expiration Value | Action if No Src | Dest Path | Is Dest Dir | Overwrite Dest | Action if Dest Exists | options |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| task-1 | Sample Move | Move | MOVE | (empty) | /tmp/hkf-test/from/sample.txt | false | DAY | 0 | ERROR | /tmp/hkf-test/to/ | true | true | IGNORE | (empty) |

**Points:**
- `Task Pattern`: specify `MOVE`
- `Expiration Value`: `0` means all files are processed regardless of age
- `Is Dest Dir`: when `true`, the destination is treated as a directory, and the source filename is preserved

#### Path Settings Sheet

Leave empty — no path variables are used in this example.

### 3. Run the Tool

```bash
java -jar ecuacion-tool-housekeep-files-x.x.x.jar excelPath=/path/to/your-settings.xlsx
```

If `/tmp/hkf-test/from/sample.txt` is moved to `/tmp/hkf-test/to/sample.txt`, the run was successful.

---

## Using Path Variables

When paths are long or you want to reuse a common prefix, register path variables in the **Path Settings Sheet** and reference them as `${VAR_NAME}` in the task's source/destination path fields.

#### Path Settings Sheet

| Variable Name | Value |
| --- | --- |
| BASE_DIR | /tmp/hkf-test |

#### Source / Destination Path in Task Settings Sheet

```
${BASE_DIR}/from/sample.txt   # Source path
${BASE_DIR}/to/               # Destination path
```

Variable names must consist of uppercase letters, digits, and underscores only (e.g., `BASE_DIR`, `LOG_PATH`).
