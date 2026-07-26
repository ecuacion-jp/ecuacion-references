This assumes [Setup](page?id=code-generator-web/setup&lang=en) has been completed.

## Running Locally

### 1. Start the Application

Run the following from the directory where the WAR is placed:

```bash
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

Once started, open `http://localhost:8080` in your browser.

### 2. Upload the Excel File

Use the file selection button on the screen to choose your DB Definition Book (xlsx),
then click the "Download" button.

Only `.xlsx` files are accepted.

### 3. Download the ZIP

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
          messages_project/
            base/
              messages_base.properties
              messages_base_ja.properties
              messages_base_en.properties
```

### 4. Integrate the Generated Code

Extract the ZIP and copy the contents of `src/main/java/` into the corresponding directory of your target project.
Likewise, copy the contents of `src/main/resources/` into the corresponding directory of your target project.

---

## Checking Errors

If the uploaded Excel file has configuration issues, an error message is displayed at the top of the page.
Review the error message and fix the DB Definition Book accordingly.

Detailed logs are available in the application log file (or console).
