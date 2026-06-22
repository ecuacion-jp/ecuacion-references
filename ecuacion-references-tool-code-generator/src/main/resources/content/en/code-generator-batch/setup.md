# code-generator-batch Setup

## 1. Download the JAR

Download the latest `ecuacion-tool-code-generator-batch-x.x.x.jar` from
[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/releases).

Place the JAR in any directory you prefer.

## 2. Prepare the DB Definition Book (Excel)

Download the template from [the `excel-format/` directory in the GitHub repository](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/tree/main/excel-format)
and rename it to match your project name.

Create an `excel-format/` directory in the same directory as the JAR,
and place the renamed file inside it.

```
/path/to/workdir/
  ecuacion-tool-code-generator-batch-x.x.x.jar
  excel-format/
    db-definition-book-fmt-v4.11.0_myproject_en.xlsx
```

For editing instructions, see [DB Definition Book (Excel) Specification](/public/en/article?id=excel-format/overview).

## System Requirements

- JDK 21 or above
