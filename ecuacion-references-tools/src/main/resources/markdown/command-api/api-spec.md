The paths below assume a standalone deployment (root context). If deployed to an existing Tomcat, the context path (e.g. `/ecuacion-tool-command-api`) is prepended.

## Endpoint

### Execute Script

```
GET  /api/public/executeScript
POST /api/public/executeScript
```

By default, `GET` is rejected and `POST` requires a matching `apiKey`. See [Access Control](#access-control) below.

### Parameters

| Parameter | Required | Description |
| --- | --- | --- |
| `scriptId` | ○ | The script ID defined in `ecuacion-tool-command-api.properties` |
| `parameter` | — | Parameters to pass to the script (comma-separated for multiple values) |
| `apiKey` | POST only, conditionally required | The shared secret compared against the server-side api-key file. Required unless `jp.ecuacion.tool.command-api.allow-insecure-access=true`. Ignored on `GET`. |

### Response

**Success (HTTP 200)**

```json
{
    "returnCode": "0"
}
```

`returnCode` is the exit status of the shell script (the value of `$?`).

HTTP 200 is returned even if the script itself exits with a non-zero code.
Check `returnCode` to determine whether the script succeeded.

---

## Error Responses

### HTTP 403 / 404

Returned when the URL is incorrect, or when a `GET` request arrives while `jp.ecuacion.tool.command-api.allow-insecure-access` is not `true` (see [Access Control](#access-control)).

### HTTP 401

Returned for a `POST` request when `apiKey` is missing, doesn't match the server-side api-key file, or the api-key file itself is missing/unreadable/unconfigured. All of these causes return the identical response so a caller cannot distinguish a server misconfiguration from a wrong key — check the server-side log to tell them apart.

### HTTP 400

Returned for the following request-side causes.

- The `scriptId` value doesn't match the regular expression `^[a-zA-Z0-9.\-_]*$`
- The script ID specified by `scriptId` is not registered in `ecuacion-tool-command-api.properties`

### HTTP 500

Returned for the following server-side configuration causes.

- The script file path registered for the `scriptId` doesn't match the regular expression `^[a-zA-Z0-9.\-_/${}]*$` (a misconfiguration)
- The registered script file doesn't actually exist

### Error Response Body Format

For both 400 and 500, the response body is in the following format ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) ProblemDetail).

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

By default, `GET` is disabled and `POST` requires a matching `apiKey`. `apiKey` is a **simple shared secret** compared against a file placed on the server — it is **not** an asymmetric (public/private) key pair, and the value the client sends is never treated as a private key.

`jp.ecuacion.tool.command-api.allow-insecure-access=true` allows `GET` and skips `apiKey` verification on `POST`; use this only on trusted internal networks. See [Configuration Files](/public/showMarkdown/page?id=command-api/config&lang=en) for the full property reference.

---

## Script Registration and Configuration Files

For how to register scripts in `ecuacion-tool-command-api.properties`, where to place configuration files, and logging setup, see [Configuration Files](/public/showMarkdown/page?id=command-api/config&lang=en).

---

## Alive Check Endpoint

Use the following endpoint to verify that the server is running:

```
GET /api/public/aliveCheck
```

Response (HTTP 200):

```json
{
    "returnCode": "0"
}
```
