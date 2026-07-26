This is the tutorial site for ecuacion-utils (module names: `ecuacion-util-xxx`).
It covers utility features commonly needed in application development,
such as reading/writing Excel files and generating PDFs.

## What is ecuacion-utils?

ecuacion-utils is a collection of general-purpose utility libraries.
Currently it consists of two modules focused on Excel operations.

## Module Structure

| Module | Role |
| --- | --- |
| `ecuacion-util-excel-table` | Utility for reading and writing table data from/to Excel files |
| `ecuacion-util-excel-report-to-pdf` | Utility for converting Excel files to PDF |

See the navigation links for details on each module.

---

## Setup

### 1. Import the BOM

By importing `ecuacion-util-parent` as a BOM, you do not need to specify the version of each module individually.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.util</groupId>
            <artifactId>ecuacion-util-parent</artifactId>
            <version>(version)</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. Add the required modules

See the following pages for each module's dependency:

- `ecuacion-util-excel-table`: [Setup](page?id=excel-tables/setup&lang=en)
- `ecuacion-util-excel-report-to-pdf`: [Setup](page?id=excel-report-to-pdf/setup&lang=en)
