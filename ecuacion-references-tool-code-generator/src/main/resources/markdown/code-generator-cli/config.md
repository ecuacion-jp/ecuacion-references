## application.properties

Spring Boot external configuration files are loaded in the following priority order (higher entries override lower ones):

| Priority | Location |
| --- | --- |
| 1 (highest) | Path specified by `-Dspring.config.location=...` |
| 2 | `./config/application.properties` (in a `config/` subdirectory of the working directory) |
| 3 (lowest) | `./application.properties` (in the working directory, next to the JAR) |

> **Note:** In the `application.properties` you create, you only need to write the settings you want to change. Any setting you don't write keeps the default value listed below.

### Using a custom application.properties

Place your file in the same directory as the JAR or in a `config/` subdirectory:

```
/your-work-dir/
├── ecuacion-tool-code-generator-cli-x.x.x.jar
├── application.properties          ← overrides embedded settings
└── config/
    └── application.properties      ← alternatively, place it here (higher priority)
```

You can also specify the config file location explicitly:

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-cli-x.x.x.jar
```

### Available properties

Additional settings should be written in `application.properties`.

#### Input / Output Files and Directories

| Property | Description | Default |
| --- | --- | --- |
| `input-file` | Excel specification file(s) to read. Multiple files can be specified as a comma-separated list (e.g. `./excel-format/foo.xlsx,./excel-format/bar.xlsx`) | None (required) |
| `output-dir` | Root directory for generated Java source files | `./products/` |

> **Warning:** `output-dir` is **recursively deleted in full** before generation starts. Be careful not to point it at an existing important directory (e.g. your home directory) by mistake.
