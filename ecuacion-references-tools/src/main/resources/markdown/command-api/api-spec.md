The paths below assume a standalone deployment (root context). If deployed to an existing Tomcat, the context path (e.g. `/ecuacion-tool-command-api`) is prepended.

## Endpoint

### Execute Script

```
GET  /api/public/executeScript   # only enabled when jp.ecuacion.tool.command-api.api-key-required=false
POST /api/public/executeScript   # only enabled when jp.ecuacion.tool.command-api.api-key-required=false
GET  /api/key/executeScript      # always requires X-Api-Key header authentication
POST /api/key/executeScript      # always requires X-Api-Key header authentication
```

`api/public/executeScript` is disabled by default (rejected with 403). `api/key/executeScript` always requires `X-Api-Key` header authentication.

On both endpoints, whether `GET` or `POST` is allowed is controlled per-script by that script's `GET:` / `POST:` / `ALL:` prefix (see [Configuration Files](page?id=command-api/config&lang=en); omitting the prefix allows `POST` only). The only difference between the two endpoints is whether `X-Api-Key` authentication is required, not this method restriction.

See [Access Control](#access-control) below for details.

### Parameters

| Parameter | Required | Description |
| --- | --- | --- |
| `scriptId` | ○ | The script ID defined in `ecuacion-tool-command-api.properties` |
| `parameters` | — | Parameters to pass to the script (comma-separated for multiple values) |
| `X-Api-Key` (HTTP header) | Required on `api/key/executeScript` | The shared secret compared against the server-side api-key file. Not used on `api/public/executeScript`. |

### Response

**Success (HTTP 200)**

```json
{
    "returnCode": "0",
    "stdout": "...",
    "stderr": "..."
}
```

`returnCode` is the exit status of the shell script (the value of `$?`). `stdout` / `stderr` are the script's captured standard output and standard error, joined with newlines (an empty string when there's no output).

HTTP 200 is returned even if the script itself exits with a non-zero code.
Check `returnCode` to determine whether the script succeeded.

---

## Error Responses

### HTTP 400

Returned for the following request-side causes.

- The `scriptId` value doesn't match the regular expression `^[a-zA-Z0-9.\-_]*$`
- The script ID specified by `scriptId` is not registered in `ecuacion-tool-command-api.properties`
- The `parameters` value doesn't match the regular expression `^[a-zA-Z0-9 ./:_=@\-]*$`

### HTTP 401

Returned for a request to `api/key/executeScript` in the following cases. All of these causes return the identical response so a caller cannot distinguish a server misconfiguration from a wrong key — check the server-side log to tell them apart.

- `X-Api-Key` is missing
- The presented `X-Api-Key` value doesn't match the server-side api-key file
- The api-key file itself is missing/unreadable/unconfigured

### HTTP 403

Returned in the following cases (see [Access Control](#access-control)).

- A request arrives at `api/public/executeScript` while `jp.ecuacion.tool.command-api.api-key-required` is not `false` (the default)
- A request to `api/public/executeScript` (once enabled) or `api/key/executeScript` (carrying a valid `X-Api-Key`) targets a script whose definition (`GET:` / `POST:` / `ALL:` prefix) doesn't allow that HTTP method

### HTTP 404

Returned when the URL is incorrect.

### HTTP 500

Returned for the following server-side configuration causes.

- The script file path registered for the `scriptId` doesn't match the regular expression `^[a-zA-Z0-9.\-_/${}]*$` (a misconfiguration)
- The registered script file doesn't actually exist
- The registered script file isn't executable
- A `${...}` environment variable reference in the script file path is malformed (unmatched braces), or the referenced environment variable isn't set
- The OS itself failed to start the script (e.g. a bad shebang) even though it's executable

### Error Response Body Format

For 400, 403, and 500, the response body is in the following format ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) ProblemDetail).

```json
{
    "type": "problemDetail.type.org.springframework.web.server.ResponseStatusException",
    "title": "problemDetail.title.org.springframework.web.server.ResponseStatusException",
    "status": 500,
    "detail": "problemDetail.org.springframework.web.server.ResponseStatusException",
    "instance": "/api/public/executeScript"
}
```

> **Note:** Currently, `title` / `detail` show the same fixed text regardless of the actual error, and the response body doesn't contain a cause-specific message. To identify the cause, use the `status` value together with the server-side logs (which record the `scriptId` / `scriptFilePath` values).

> **Note:** 401 (see [Access Control](#access-control)) is not covered by this format. It's returned from the servlet filter layer, via `HttpServletResponse.sendError()`, before the request ever reaches Spring MVC — so instead of the ProblemDetail shape above, it comes back as Spring Boot's default error page (JSON with `timestamp` / `status` / `error` / `path`).

---

## Security

### Script Pre-Registration

Only scripts registered in `ecuacion-tool-command-api.properties` can be executed.
Callers cannot specify arbitrary script paths in the request.

However, you remain responsible for the content of the scripts you register.
Even though arbitrary paths cannot be specified, a registered script can still contain destructive operations.

### Input Validation

The `scriptId` parameter value is validated against the regular expression `^[a-zA-Z0-9.\-_]*$`.

Script file paths are validated against the regular expression `^[a-zA-Z0-9.\-_/${}]*$`.

### Access Control

`X-Api-Key` is a **shared secret** compared against a file placed on the server — it is **not** an asymmetric (public/private) key pair, and the value the client sends is never treated as a private key. See [Access Control](page?id=command-api/access-control&lang=en) for the full property reference (`api-key-required`, `api-key-comparison-mode`) and how the key file is managed.

---

## Script Registration and Configuration Files

For how to register scripts in `ecuacion-tool-command-api.properties`, where to place configuration files, and logging setup, see [Configuration Files](page?id=command-api/config&lang=en).

---

## Alive Check Endpoint

Use the following endpoint to verify that the server is running. It's a shared endpoint provided by `ecuacion-splib-rest`, not something specific to command-api.

```
GET  /api/ecuacion-splib/public/aliveCheck
POST /api/ecuacion-splib/public/aliveCheck
```

Returns HTTP 200 with body `{"status": "OK"}`.
