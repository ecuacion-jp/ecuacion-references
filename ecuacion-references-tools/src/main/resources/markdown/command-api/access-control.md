## Access Control

Two properties control access to `execute` (see [API Spec](page?id=command-api/api-spec&lang=en) for the resulting HTTP behavior).

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `true` (the default applied when unset): `api/public/execute` is rejected (403). Call `api/key/execute` instead, which always requires a valid `X-Api-Key` header.<br>`false`: `api/public/execute` is enabled. Use this only on trusted internal networks. (On both endpoints, which HTTP method(s) a script accepts is controlled by its HTTP-method prefix; see [Configuration Files](page?id=command-api/config&lang=en#restricting-the-allowed-http-method).) |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | Path to a file containing the shared secret compared against the `X-Api-Key` header on `api/key/execute`. Supports `${ENV_VAR}` resolution, same as script paths in [Configuration Files](page?id=command-api/config&lang=en). Optional — see [ecuacion-tool-command-api-keys.txt](#ecuacion-tool-command-api-keys-txt) below for the default applied when unset. |

Add these to `application.properties`, for example:

```properties
jp.ecuacion.tool.command-api.api-key-required=true
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

---

## ecuacion-tool-command-api-keys.txt

The file containing the shared secret compared against the `X-Api-Key` header on `api/key/execute` (see [Access Control](#access-control) above) is resolved in the following priority order.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `jp.ecuacion.tool.command-api.api-key-file-path` |
| 2 | `config/ecuacion-tool-command-api-keys.txt`, relative to the current directory |
| 3 (lowest) | `ecuacion-tool-command-api-keys.txt`, directly in the current directory |

Priorities 2 and 3 are a convenient zero-config default for casual/local use, with no `api-key-file-path` setting required at all.

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── ecuacion-tool-command-api-keys.txt   ← priority 3
└── config/
    └── ecuacion-tool-command-api-keys.txt   ← priority 2, higher
```

To specify an explicit path instead, add this to `application.properties`:

```properties
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

For production, prefer an explicit path outside the deployment directory (e.g. a secrets volume, or a location with tighter file permissions), so the key isn't bundled, backed up, or overwritten alongside the app.

> **Note:** Under the [Tomcat deployment options](page?id=command-api/config&lang=en#deploying-to-an-existing-tomcat), `user.dir` is Tomcat's own working directory, unrelated to the WAR or `app-conf` overlay directories, so this default won't reliably find a file there — set `api-key-file-path` explicitly in that case.

The file's content is read (with surrounding whitespace/newlines trimmed) on every request, so the key can be rotated by replacing the file's content without restarting the app.

### Comparison Mode: Plain Text vs. bcrypt

By default, every line in the file must be a bcrypt hash. To store plain-text keys instead — **not recommended**, since the raw keys then sit at rest, readable by anything with file access — set the following property in `application.properties`.

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | `BCRYPT` (default): every line is a bcrypt hash, compared via `BCryptPasswordEncoder.matches`.<br>`PLAIN`: every line is a plain-text key, compared directly. An unrecognized value throws an exception, failing every `api/key/execute` request until the property is corrected. |

```properties
jp.ecuacion.tool.command-api.api-key-comparison-mode=PLAIN
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
$2y$10$grqMx6p/rxba/w46AociPeuyEjQ978WNGcukQmvU/y3j4GQ.MY8lu

# key for client B
$2y$10$wfghrHt4Y708ET1T359xk.igcZL2Ep9RjS3dwG8olNCOZsmLCI23W
```

A long, random value is recommended for each key. For `BCRYPT` (the default), hash it first before appending (see the hashing command example above); for `PLAIN`, append the value as-is. What callers must send in `X-Api-Key` is the pre-hash value, not the hash stored in the file.

Either way, a request is authenticated as long as its `X-Api-Key` header matches any one of the file's lines (after trimming surrounding whitespace/newlines).
