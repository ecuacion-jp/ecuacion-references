The paths below assume a standalone deployment (root context). If deployed to an existing Tomcat, the context path (e.g. `/ecuacion-tool-command-api`) is prepended.

## Endpoint

### Execute Script

```
GET  /api/public/execute   # only enabled when jp.ecuacion.tool.command-api.api-key-required=false
POST /api/public/execute   # only enabled when jp.ecuacion.tool.command-api.api-key-required=false
GET  /api/key/execute      # always requires X-Api-Key header authentication
POST /api/key/execute      # always requires X-Api-Key header authentication
```

`api/public/execute` is disabled by default (rejected with 403). `api/key/execute` always requires `X-Api-Key` header authentication.

On both endpoints, whether `GET` or `POST` is allowed is controlled per-script by that script's `GET:` / `POST:` / `ALL:` prefix (see [Configuration Files](page?id=command-api/config&lang=en); omitting the prefix allows `POST` only). The only difference between the two endpoints is whether `X-Api-Key` authentication is required, not this method restriction.

See [Access Control](page?id=command-api/access-control&lang=en) for details.

### Parameters

| Parameter | Required | Description |
| --- | --- | --- |
| `scriptId` | ○ | The script ID defined in `ecuacion-tool-command-api-scripts.properties` |
| `parameters` | — | Parameters to pass to the script (comma-separated for multiple values) |
| `X-Api-Key` (HTTP header) | Required on `api/key/execute` | The shared secret compared against the server-side api-key file. Not used on `api/public/execute`. |

### Response

**Success (HTTP 200)**

```json
{
    "returnCode": "0",
    "stdout": "...",
    "stderr": "...",
    "stdoutTruncated": "false",
    "stderrTruncated": "false"
}
```

`returnCode` is the exit status of the shell script (the value of `$?`). `stdout` / `stderr` are the script's captured standard output and standard error, joined with newlines (an empty string when there's no output).

HTTP 200 is returned even if the script itself exits with a non-zero code.
Check `returnCode` to determine whether the script succeeded.

`stdoutTruncated` / `stderrTruncated` indicate whether output was cut off after exceeding `jp.ecuacion.tool.command-api.script-max-output-bytes` (see [Configuration Files](page?id=command-api/config&lang=en); default 1 MiB). When truncated, `stdout` / `stderr` contain only the content up to that cap, and everything after it is discarded (the script itself still runs to completion).

---

## Error Responses

### HTTP 400

Returned for the following request-side causes.

- The `scriptId` value doesn't match the regular expression `^[a-zA-Z0-9.\-_]*$`
- The script ID specified by `scriptId` is not registered in `ecuacion-tool-command-api-scripts.properties`
- The `parameters` value doesn't match the regular expression `^[a-zA-Z0-9 ./:_=@\-]*$`

### HTTP 401

Returned for a request to `api/key/execute` in the following cases. All of these causes return the identical response so a caller cannot distinguish a server misconfiguration from a wrong key — check the server-side log to tell them apart.

- `X-Api-Key` is missing
- The presented `X-Api-Key` value doesn't match the server-side api-key file
- The api-key file itself is missing/unreadable/unconfigured

### HTTP 403

Returned in the following cases (see [Access Control](page?id=command-api/access-control&lang=en)).

- A request arrives at `api/public/execute` while `jp.ecuacion.tool.command-api.api-key-required` is not `false` (the default)
- A request to `api/public/execute` (once enabled) or `api/key/execute` (carrying a valid `X-Api-Key`) targets a script whose definition (`GET:` / `POST:` / `ALL:` prefix) doesn't allow that HTTP method

### HTTP 404

Returned when the URL is incorrect.

### HTTP 500

Returned for the following server-side configuration causes.

- The script file path registered for the `scriptId` doesn't match the regular expression `^[a-zA-Z0-9.\-_/${}]*$` (a misconfiguration)
- The registered script file doesn't actually exist
- The registered script file isn't executable
- A `${...}` environment variable reference in the script file path is malformed (unmatched braces), or the referenced environment variable isn't set
- The OS itself failed to start the script (e.g. a bad shebang) even though it's executable

### HTTP 504

Returned when the script's execution exceeds `jp.ecuacion.tool.command-api.script-timeout-seconds` (see [Configuration Files](page?id=command-api/config&lang=en); default 60 seconds). The script is forcibly killed.

### Error Response Body Format

For 400, 403, 500, and 504, the response body is in the following format ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) ProblemDetail).

```json
{
    "type": "problemDetail.type.org.springframework.web.server.ResponseStatusException",
    "title": "problemDetail.title.org.springframework.web.server.ResponseStatusException",
    "status": 500,
    "detail": "problemDetail.org.springframework.web.server.ResponseStatusException",
    "instance": "/api/public/execute"
}
```

> **Note:** Currently, `title` / `detail` show the same fixed text regardless of the actual error, and the response body doesn't contain a cause-specific message. To identify the cause, use the `status` value together with the server-side logs (which record the `scriptId` / `scriptFilePath` values).

> **Note:** 401 (see [Access Control](page?id=command-api/access-control&lang=en)) is not covered by this format. It's returned from the servlet filter layer, via `HttpServletResponse.sendError()`, before the request ever reaches Spring MVC — so instead of the ProblemDetail shape above, it comes back as Spring Boot's default error page (JSON with `timestamp` / `status` / `error` / `path`).

See [Security](page?id=command-api/security&lang=en) for script pre-registration, input validation, and access control.
