# command-api API Spec

The paths below assume a standalone deployment (root context). If deployed to an existing Tomcat, the context path (e.g. `/ecuacion-tool-command-api`) is prepended.

## Endpoint

### Execute Script

```
GET /api/public/executeScript
```

### Query Parameters

| Parameter | Required | Description |
| --- | --- | --- |
| `scriptId` | ○ | The script ID defined in `ecuacion-tool-command-api.properties` |
| `parameter` | — | Parameters to pass to the script (comma-separated for multiple values) |

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

Returned when the URL is incorrect.

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
