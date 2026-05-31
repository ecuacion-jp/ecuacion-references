# ecuacion-tool-code-generator reference

This is the reference page for `ecuacion-tool-code-generator`.
It covers how to use the tool that auto-generates base module Java code for Spring Boot + JPA applications from a DB Definition Book (Excel).

## What This Tool Does

Based on table and column definitions in an Excel file, the tool generates boilerplate code
such as JPA Entities, Repositories, and business logic classes.

By generating this commonly error-prone and inconsistent code from a single unified source of truth,
the effort required when adding new tables is significantly reduced.

## Execution Methods

The tool provides two execution methods:

| Method | Description |
| --- | --- |
| `code-generator-batch` | Run from the command line (`mvn spring-boot:run`). Place Excel locally and execute |
| `code-generator-web` | Upload Excel from a browser → download generated code as a ZIP |

Both methods use the same DB Definition Book (Excel) format.

## Navigation

| Menu | Contents |
| --- | --- |
| code-generator-batch | Overview, setup, and usage for the batch execution module |
| code-generator-web | Overview, setup, and usage for the Web UI module |
| DB Definition Book (Excel) | Sheet structure and column reference for the input Excel file |
