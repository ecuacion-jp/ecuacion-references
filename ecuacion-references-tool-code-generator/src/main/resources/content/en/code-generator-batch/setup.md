# code-generator-batch Setup

`code-generator-batch` is managed as a Maven project.
Since it is not published to a public Maven repository, clone the repository directly.

## 1. Clone the Repository

```bash
git clone https://github.com/ecuacion-jp/ecuacion-tool-code-generator.git
```

Place the cloned directory in the same parent directory as other ecuacion repositories.
The batch module references parent modules (such as `ecuacion-splib`) via relative paths,
so **other ecuacion repositories must also exist in the same parent directory**.

```
/path/to/dev/
  ecuacion-tool-code-generator/   ← cloned here
  ecuacion-splib/                 ← also required
  ecuacion-lib/                   ← also required
```

## 2. Prepare the DB Definition Book (Excel)

Sample files are already included in the `ecuacion-tool-code-generator-excel-format/` directory.

To create a file for a new project, copy an existing Excel file and rename it:

```
DB項目定義書(fmt-v4.10.0)_cloud-server-manager.xlsx  ← copy source
DB項目定義書(fmt-v4.10.0)_myproject.xlsx             ← renamed
```

For editing instructions, see [DB Definition Book (Excel) Specification](/public/en/article?id=excel-format/overview).

## 3. Build

Build the entire project from the repository root:

```bash
cd ecuacion-tool-code-generator
mvn clean install -DskipTests
```

## System Requirements

- JDK 21 or above
- Maven 3.x
