`ecuacion-tool-code-generator-cli` is a command-line module that reads a DB Definition Book (Excel) and auto-generates Java source code for the **base module** of a Spring Boot + JPA application.

## How It Works

1. It is executed via `java -jar`, specifying the DB Definition Book (xlsx) to read via the `input-file` property
2. Java source files are written to `products/<SYSTEM_NAME>/`

\* Move the generated files into your target project's `src/main/java/` and `src/main/resources/` directories to use them as code.
