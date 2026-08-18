## Access Control

Two properties control access to `executeScript` (see [API Spec](page?id=command-api/api-spec&lang=en) for the resulting HTTP behavior). They are intentionally **not** set in the embedded `application.properties`, so leaving either unconfigured is logged as a warning at startup instead of silently defaulting.

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `true` (the default applied when unset): `api/public/executeScript` is rejected (403). Call `api/key/executeScript` instead, which always requires a valid `X-Api-Key` header. `false`: `api/public/executeScript` is enabled. Use this only on trusted internal networks. (On both endpoints, which HTTP method(s) a script accepts is controlled by its HTTP-method prefix; see [Configuration Files](page?id=command-api/config&lang=en#restricting-the-allowed-http-method).) |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | Path to a file containing the shared secret compared against the `X-Api-Key` header on `api/key/executeScript`. Supports `${ENV_VAR}` resolution, same as script paths in [Configuration Files](page?id=command-api/config&lang=en). Optional — see [ecuacion-tool-command-api-key.txt](#ecuacion-tool-command-api-key.txt) below for the default applied when unset. |

Example:

```properties
jp.ecuacion.tool.command-api.api-key-required=true
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

---

## ecuacion-tool-command-api-key.txt

The file containing the shared secret compared against the `X-Api-Key` header on `api/key/executeScript` (see [Access Control](#access-control) above) is resolved in the following priority order.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `jp.ecuacion.tool.command-api.api-key-file-path` |
| 2 | `config/ecuacion-tool-command-api-key.txt`, relative to the current directory |
| 3 (lowest) | `ecuacion-tool-command-api-key.txt`, directly in the current directory |

Priorities 2 and 3 are a convenient zero-config default for casual/local use, with no `api-key-file-path` setting required at all.

> **Note:** Unlike `application.properties` / `ecuacion-tool-command-api.properties`, priorities 2 and 3 are a plain filesystem check relative to the JVM's working directory (`user.dir`) — they do **not** go through Spring Boot's `spring.config.location` / `classpath:` search. They work as described for the standalone `java -jar` launch style (see [Getting Started](page?id=command-api/quickstart&lang=en)). Under the [Tomcat deployment options](page?id=command-api/config&lang=en#deploying-to-an-existing-tomcat), `user.dir` is Tomcat's own working directory, unrelated to the WAR or `app-conf` overlay directories, so this default won't reliably find a file there — set `api-key-file-path` explicitly in that case.

The file's content is read (with surrounding whitespace/newlines trimmed) on every request, so the key can be rotated by replacing the file's content without restarting the app.

### Using a Custom ecuacion-tool-command-api-key.txt

**Option 1 — place it in a `config/` subdirectory:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    └── ecuacion-tool-command-api-key.txt
```

**Option 2 — place it directly next to the WAR:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── ecuacion-tool-command-api-key.txt
```

**Option 3 — specify the path explicitly (recommended for production):**

```properties
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

For production, prefer this option with a path outside the deployment directory (e.g. a secrets volume, or a location with tighter file permissions), so the key isn't bundled, backed up, or overwritten alongside the app.

### Comparison Mode: Plain Text vs. bcrypt

By default, every line in the file is compared as plain text. To store bcrypt hashes instead (so the raw keys aren't kept at rest anywhere the application can read them back), set the following property.

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | `PLAIN` (default): every line is a plain-text key, compared directly. `BCRYPT`: every line is a bcrypt hash, compared via `BCryptPasswordEncoder.matches`. An unrecognized value is logged as a warning at startup and treated as `PLAIN`. |

```properties
jp.ecuacion.tool.command-api.api-key-comparison-mode=BCRYPT
```

> **Note:** This mode applies to the **whole file** — every line must be the same kind (all plain, or all bcrypt hashes). Mixing plain-text keys and bcrypt hashes in the same file is not supported.

To generate a bcrypt hash for a key, for example:

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

### Example

The file contains nothing but the shared secret(s) — no key name, no `properties` syntax. One key per line; if the file has more than one line, a request is accepted as long as it presents any one of them. Issuing one key per caller lets you revoke a single caller by deleting its line, without affecting the others.

Blank lines are skipped, and lines starting with `#` (after trimming leading whitespace) are treated as comments and skipped too — labeling which key belongs to which caller makes it easier to find the right line when a key needs revoking.

```
# key for client A
04f1befd704277c4b76afd01d655e6f1e8e36af9f74abe3a010d539ed3ac88cf

# key for client B
dcef325238aed9023681c8971d6df53080c536d0643692f9cad5a465118d5e79
```

A long, random value is recommended for each key. For `PLAIN` (the default), append the value as-is; for `BCRYPT`, hash it first before appending (see the hashing command example above). What callers must send in `X-Api-Key` is the pre-hash value, not the hash stored in the file.

Either way, a request is authenticated as long as its `X-Api-Key` header matches any one of the file's lines (after trimming surrounding whitespace/newlines).
