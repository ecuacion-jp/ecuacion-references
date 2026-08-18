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

#### Input / Output Directories

| Property | Description | Default |
| --- | --- | --- |
| `input-dir` | Directory containing the Excel specification files. Multiple directories can be specified as a comma-separated list (e.g. `./dir1,./dir2`) | `./excel-format` |
| `output-dir` | Root directory for generated Java source files | `./products/` |

You can also place multiple Excel files directly in the same `input-dir`. When the CLI runs, all xlsx files in the
directory are processed and separate output is generated for each `SYSTEM_NAME`.

> **Note:** Unlike `code-generator-web`, the CLI jar has no built-in mail notification on failure —
> there is no `spring.mail.*` / `jp.ecuacion.splib.mail.*` configuration to set here. For scripted or
> scheduled runs, check the process exit code (`0` on success, `1` on failure) and wire up notification
> in your own wrapper script if needed. See **Troubleshooting** below for how failures are reported on
> the console.

---

## Troubleshooting

If the Excel configuration is invalid, execution stops and a concise, bulleted list of validation messages is
printed to the console, identifying which item is problematic. For an unexpected error, a short message is shown;
re-run with `--verbose` to also print the full stack trace.
