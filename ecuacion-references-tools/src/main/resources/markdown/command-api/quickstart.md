[Setup](page?id=command-api/setup&lang=en) is assumed to be complete.

## Run It Standalone

### 1. Prepare the Script

On Linux or macOS, create `sayHello.sh` in any directory:

```bash
#!/bin/bash

echo "Hello!"
touch /path/to/script/directory/touch.file
```

Grant execute permission to the application's runtime user:

```bash
chmod +x /path/to/script/directory/sayHello.sh
```

On Windows, create `sayHello.bat` instead (no execute permission step is needed):

```bat
@echo off
echo Hello!
type nul > C:\path\to\script\directory\touch.file
```

### 2. Register the Script in the Properties File

Create `ecuacion-tool-command-api.properties` next to the WAR and add the following (see [Configuration Files](page?id=command-api/config&lang=en) for every supported location):

```properties
script.say-hello=GET:/path/to/script/directory/sayHello.sh
```

(On Windows, point to the `.bat` file instead, e.g. `script.say-hello=GET:C:\\path\\to\\script\\directory\\sayHello.bat`.)

**Format**: `script.<script-id>=[GET:|POST:|ALL:]<absolute path to script>`

The leading `GET:` makes this script callable via `GET` (omitting the prefix would allow `POST` only, and step 5's `GET` call below would fail — see [Configuration Files](page?id=command-api/config&lang=en) for details). The script ID corresponds to the `scriptId` query parameter in the request.

### 3. Allow Access for This Quickstart

By default, access to `api/public/executeScript` is disabled (see [Access Control](page?id=command-api/config&lang=en#access-control)). For this local quickstart, add the following to `application.properties` (placed as described in [Configuration Files](page?id=command-api/config&lang=en)):

```properties
jp.ecuacion.tool.command-api.api-key-required=false
```

(For production use, don't set this to `false` — instead, explicitly set `jp.ecuacion.tool.command-api.api-key-required=true` (leaving it unset also defaults to `true`, but logs a warning at startup, so setting it explicitly is recommended) and call `api/key/executeScript` with an `X-Api-Key` header. See [API Spec](page?id=command-api/api-spec&lang=en).)

### 4. Start the App

Run the following in the directory where you placed the WAR. The `sayHello.sh`, `ecuacion-tool-command-api.properties`, and `application.properties` files placed in the steps above are read at startup (if you add or change these files after the app has started, restart the app to apply the change):

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

### 5. Call the API

Access the following URL:

```
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello
```

On success, you will receive a JSON response like:

```json
{
    "returnCode": "0",
    "stdout": "Hello!",
    "stderr": ""
}
```

If `stdout` contains `Hello!` and `/path/to/script/directory/touch.file` has been created, the setup is working correctly.

---

For more ways to call the API — passing parameters, using environment variables in script paths, and checking errors — see [Usage Patterns](page?id=command-api/usage-patterns&lang=en).
