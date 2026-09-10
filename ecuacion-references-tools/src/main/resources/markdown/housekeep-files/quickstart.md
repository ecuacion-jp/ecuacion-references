This guide walks you through the simplest example: moving a local file to another directory.

## Prerequisites

- The JAR file and the sample Excel configuration file have been prepared following the [Setup](page?id=housekeep-files/setup&lang=en) instructions.

## Steps

### 1. Create Test Files

Move into the directory where you placed the JAR, and create these using paths relative to it.

**Linux / macOS:**

```bash
mkdir -p hkf-test/from hkf-test/to
touch hkf-test/from/sample.txt
```

**Windows (PowerShell):**

```powershell
New-Item -ItemType Directory -Force hkf-test/from, hkf-test/to
New-Item -ItemType File hkf-test/from/sample.txt
```

### 2. Configure the Excel File

Open the sample Excel file and fill in the Task Settings sheet (leave the Server Auth Settings sheet empty since this example doesn't use SFTP).

#### Task Settings Sheet

Basic info:

| Task ID | Task Name | Task Pattern (display) | Task Pattern | Remote Server |
| --- | --- | --- | --- | --- |
| task-1 | Sample Move | Move | MOVE | (empty) |

Source path fields:

| Source Path | Is Src Dir | Src Path Pending Days | Action if No Src |
| --- | --- | --- | --- |
| hkf-test/from/sample.txt | false | 0 | ERROR |

Destination path fields:

| Dest Path | Is Dest Dir | Overwrite Dest | Action if Dest Exists |
| --- | --- | --- | --- |
| hkf-test/to/ | true | true | IGNORE |

**Points:**
- `Task Pattern`: specify `MOVE`
- `Src Path Pending Days`: `0` means all files are processed regardless of age
- `Is Dest Dir`: when `true`, the destination is treated as a directory, and the source filename is preserved
- Relative source/destination paths are resolved against the current directory `java -jar` is run from (the directory where you created `hkf-test` in step 1)

### 3. Configure application.properties

Next to the JAR, create (or edit) `application.properties` and point it at the excel file you just configured.

```properties
jp.ecuacion.tool.housekeep-files.excel-path=/path/to/your-settings.xlsx
```

### 4. Run the Tool

```bash
java -jar ecuacion-tool-housekeep-files-x.x.x.jar
```

If `hkf-test/from/sample.txt` is moved to `hkf-test/to/sample.txt`, the run was successful.
