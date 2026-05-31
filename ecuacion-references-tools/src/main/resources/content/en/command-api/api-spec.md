# command-api API Spec

## Endpoint

### Execute Script

```
GET /ecuacion-tool-command-api/api/public/executeScript
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

Returned when the script ID specified by `scriptId` is not registered in `ecuacion-tool-command-api.properties`.

### HTTP 500

Returned in the following cases.

**`ecuacion-tool-command-api.properties` not found on CLASSPATH:**

```json
{
    "type": "about:blank",
    "title": "Internal Server Error",
    "status": 500,
    "detail": "'ecuacion-tool-command-api.properties' not found on classpath.",
    "instance": "/ecuacion-tool-command-api/api/public/executeScript"
}
```

**Script file not found:**

```json
{
    "type": "about:blank",
    "title": "Internal Server Error",
    "status": 500,
    "detail": "scriptFilePath '/path/to/script/directory/sayHello.sh' not found.",
    "instance": "/ecuacion-tool-command-api/api/public/executeScript"
}
```

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

## ecuacion-tool-command-api.properties Reference

This properties file is placed in the CLASSPATH directory.

### Registering Scripts

```properties
script.<script-id>=<absolute path to script>
```

Example:

```properties
script.say-hello=/opt/scripts/sayHello.sh
script.daily-batch=/opt/scripts/dailyBatch.sh
```

### Using Environment Variables

Environment variables can be used in script paths (`${ENV_VAR}` syntax):

```properties
script.say-hello=${SCRIPT_DIR}/sayHello.sh
```

### Alive Check Endpoint

Use the following endpoint to verify that the server is running:

```
GET /ecuacion-tool-command-api/api/public/alive
```

Response (HTTP 200):

```json
{
    "result": "alive"
}
```
