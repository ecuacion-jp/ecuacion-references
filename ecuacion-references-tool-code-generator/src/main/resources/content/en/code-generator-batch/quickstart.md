# code-generator-batch Quick Start

This assumes [Setup](/public/en/article?id=code-generator-batch/setup) has been completed.

## Steps

### 1. Minimum Configuration of the DB Definition Book

Open the Excel file in the `ecuacion-tool-code-generator-excel-format/` directory
and configure at least the following two entries in the **General Settings sheet**:

| Row | Key | Description | Example |
| --- | --- | --- | --- |
| 8 | `SYSTEM_NAME` | Project identifier. Becomes the output folder name under `products/` | `my-project` |
| 9 | `BASE_PACKAGE` | Common Java package prefix for generated code | `jp.example.myapp` |

For details on other settings and sheets, see [DB Definition Book (Excel) Specification](/public/en/article?id=excel-format/overview).

### 2. Run the Batch

```bash
cd ecuacion-tool-code-generator/ecuacion-tool-code-generator-batch
mvn spring-boot:run
```

### 3. Check the Output

After execution, Java source files are written to:

```
ecuacion-tool-code-generator-batch/products/<SYSTEM_NAME>/
```

Example output structure:

```
products/my-project/
  src/
    base/
      java/
        jp/example/myapp/base/
          entity/
          record/
          repository/
          repositoryimpl/
          bl/
          enums/
          converter/
          datatype/
```

### 4. Integrate the Generated Code

Copy the contents of `src/base/java/` into the corresponding directory of your target project.

---

## Processing Multiple Files

You can place multiple Excel files in the `ecuacion-tool-code-generator-excel-format/` directory.
When the batch runs, all xlsx files in the directory are processed and separate output is generated for each `SYSTEM_NAME`.

## Checking Logs

Batch execution logs are printed to the console.
If an error occurs, the error message identifies which part of the Excel configuration is problematic.
