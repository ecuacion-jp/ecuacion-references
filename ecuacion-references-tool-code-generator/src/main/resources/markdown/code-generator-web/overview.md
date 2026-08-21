`ecuacion-tool-code-generator-web` is a Web UI module that lets you upload a DB Definition Book (Excel) from a browser and download the generated Java source code as a ZIP file.

## How It Works

1. Upload the DB Definition Book (xlsx) from a browser
2. The file is saved to a temporary directory on the server
3. The code generation engine runs
4. The output is zipped into `source.zip` and returned to the client

\* Extract the ZIP and move the `src/main/java/` and `src/main/resources/` directories into your target project to use them as code.
