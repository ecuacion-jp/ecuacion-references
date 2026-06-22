# code-generator-batch Overview

`ecuacion-tool-code-generator-batch` is a batch execution module that reads a DB Definition Book (Excel) and auto-generates Java source code for the **base module** of a Spring Boot + JPA application.

## How It Works

1. The DB Definition Book (xlsx) placed in `excel-format/` is read
2. The batch is executed via `java -jar`
3. Java source files are written to `products/<SYSTEM_NAME>/`
