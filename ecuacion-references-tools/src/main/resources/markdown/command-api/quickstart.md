[Setup](page?id=command-api/setup&lang=en) is assumed to be complete.

## Run It Standalone

### 1. Start the App

Run the following in the directory where you placed the WAR:

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

### 2. Prepare the Script

On Linux or macOS, create `sayHello.sh` in any directory:

```bash
#!/bin/bash

touch /path/to/script/directory/touch.file
echo "Touch done."
```

Grant execute permission to the application's runtime user:

```bash
chmod +x /path/to/script/directory/sayHello.sh
```

On Windows, create `sayHello.bat` instead (no execute permission step is needed):

```bat
@echo off
type nul > C:\path\to\script\directory\touch.file
echo Touch done.
```

### 3. Register the Script in the Properties File

Create `ecuacion-tool-command-api.properties` next to the WAR and add the following (see [Configuration Files](page?id=command-api/config&lang=en) for every supported location):

```properties
script.say-hello=/path/to/script/directory/sayHello.sh
```

(On Windows, point to the `.bat` file instead, e.g. `script.say-hello=C:\\path\\to\\script\\directory\\sayHello.bat`.)

**Format**: `script.<script-id>=<absolute path to script>`

The script ID corresponds to the `scriptId` query parameter in the request. After changing the properties file, restart the app to apply the change.

### 4. Allow Access for This Quickstart

By default, `GET` is disabled and `POST` requires an `apiKey` (see [Access Control](page?id=command-api/config&lang=en#access-control)). For this local quickstart, add the following to `application.properties` (placed as described in [Configuration Files](page?id=command-api/config&lang=en)) and restart the app:

```properties
jp.ecuacion.tool.command-api.allow-insecure-access=true
```

(For production use, keep this unset/`false` and call the API with `POST` + `apiKey` instead — see [API Spec](page?id=command-api/api-spec&lang=en).)

### 5. Call the API

Access the following URL:

```
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello
```

On success, you will receive a JSON response like:

```json
{
    "returnCode": "0"
}
```

If `/path/to/script/directory/touch.file` has been created, the setup is working correctly.

---

## Passing Parameters

Use the `parameter` query parameter to pass arguments to the script:

```
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello&parameter=param1,param2
```

The above request executes `sayHello.sh param1 param2` (or `sayHello.bat param1 param2` on Windows).

Multiple parameters are separated by commas. Passing a comma character as part of a parameter value is currently not supported.

---

## Using Environment Variables in Script Paths

Environment variables can be used in script paths using the `${ENV_VAR}` syntax:

```properties
script.say-hello=${USER_HOME}/script/directory/sayHello.sh
```

If the `USER_HOME` environment variable is set in the app's runtime environment, it will be resolved at startup.

---

## Checking Errors

If a request fails, check the HTTP status code and response body (see [API Spec](page?id=command-api/api-spec&lang=en) for details).

For detailed logs, check the application's log file (or console).
