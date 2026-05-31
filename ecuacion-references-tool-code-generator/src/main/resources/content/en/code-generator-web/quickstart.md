# code-generator-web Quick Start

This assumes [Setup](/public/en/article?id=code-generator-web/setup) has been completed.

## Running Locally

### 1. Start the Application

```bash
cd ecuacion-tool-code-generator/ecuacion-tool-code-generator-web
mvn spring-boot:run
```

Once started, open the following URL in your browser:

```
http://localhost:8080/public/sourceDownload
```

### 2. Upload the Excel File

Use the file selection button on the screen to choose your DB Definition Book (xlsx),
then click the "Download" button.

Only `.xlsx` files are accepted.

### 3. Download the ZIP

When code generation completes, the download of `source.zip` begins automatically.

The ZIP contains the generated Java source files:

```
source.zip
  <SYSTEM_NAME>/
    src/
      base/
        java/
          <package path>/base/
            entity/
            record/
            repository/
            ...
```

### 4. Integrate the Generated Code

Extract the ZIP and copy the contents of `src/base/java/` into the corresponding directory of your target project.

---

## Checking Errors

If the uploaded Excel file has configuration issues, an error message is displayed at the top of the page.
Review the error message and fix the DB Definition Book accordingly.

Detailed logs are available in the application log file (or console).
