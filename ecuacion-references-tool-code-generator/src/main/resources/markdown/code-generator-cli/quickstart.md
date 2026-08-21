This assumes [Setup](page?id=code-generator-cli/setup&lang=en) has been completed.

## Steps

### 1. Minimum Configuration of the DB Definition Book

See [DB Definition Book Quick Start](page?id=excel-format/quickstart&lang=en) for the minimum settings required
before running the tool — this part is the same whether you use `code-generator-cli` or `code-generator-web`.

### 2. Run the CLI

Run the following from the directory where the JAR is placed:

```bash
java -jar ecuacion-tool-code-generator-cli-x.x.x.jar
```

### 3. Check the Output

After execution, Java source files and resource files are written to:

```
products/<SYSTEM_NAME>/
```

Example output structure:

```
products/my-project/
  src/
    main/
      java/
        jp/example/myapp/base/
          entity/
          record/
          repository/
          bl/
          enums/
          converter/
          datatype/
      resources/
        item_names_base.properties
        messages_base.properties
```
