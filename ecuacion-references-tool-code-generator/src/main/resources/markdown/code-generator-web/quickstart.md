This assumes [Setup](page?id=code-generator-web/setup&lang=en) has been completed.

## Running Locally

### 1. Minimum Configuration of the DB Definition Book

See [DB Definition Book Quick Start](page?id=excel-format/quickstart&lang=en) for the minimum settings required
before uploading the file — this part is the same whether you use `code-generator-cli` or `code-generator-web`.

### 2. Start the Application

Run the following from the directory where the WAR is placed:

```bash
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

Once started, open `http://localhost:8080` in your browser.

### 3. Upload the Excel File

Use the file selection button on the screen to choose your DB Definition Book (xlsx),
then click the "Download" button.

Only `.xlsx` files are accepted.

### 4. Download the ZIP

When code generation completes, the download of `source.zip` begins automatically.

The ZIP contains the generated Java source files and resource files:

```
source.zip
  <SYSTEM_NAME>/
    src/
      main/
        java/
          <package path>/base/
            entity/
            record/
            repository/
            ...
        resources/
          item_names_base.properties
          messages_base.properties
          (plus _<lang> variants such as messages_base_en.properties for each
          additional language configured in the General Settings sheet)
```

### 5. Integrate the Generated Code

Extract the ZIP and copy the contents of `src/main/java/` into the corresponding directory of your target project.
Likewise, copy the contents of `src/main/resources/` into the corresponding directory of your target project.

---

## Checking Errors

If the uploaded Excel file has configuration issues, an error message is displayed at the top of the page.
Review the error message and fix the DB Definition Book accordingly.

Detailed logs are available in the application log file (or console).
