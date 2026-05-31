# command-api Quick Start

This guide walks you through executing a simple shell script via the Web API.

## Prerequisites

- [Setup](/public/en/article?id=command-api/setup) has been completed.

## Steps

### 1. Prepare the Script

Create `sayHello.sh` in any directory:

```bash
#!/bin/bash

touch /path/to/script/directory/touch.file
echo "Touch done."
```

Grant execute permission to the application server's runtime user:

```bash
chmod +x /path/to/script/directory/sayHello.sh
```

### 2. Register the Script in the Properties File

Add the following to `ecuacion-tool-command-api.properties`:

```properties
script.say-hello=/path/to/script/directory/sayHello.sh
```

**Format**: `script.<script-id>=<absolute path to script>`

The script ID corresponds to the `scriptId` query parameter in the request.

### 3. Restart the Application Server

Restart the application server to apply the properties file changes.

### 4. Call the API

Access the following URL:

```
http://yourdomain.com/ecuacion-tool-command-api/api/public/executeScript?scriptId=script.say-hello
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
http://yourdomain.com/ecuacion-tool-command-api/api/public/executeScript?scriptId=script.say-hello&parameter=param1,param2
```

The above request executes `sayHello.sh param1 param2`.

Multiple parameters are separated by commas. Passing a comma character as part of a parameter value is currently not supported.

---

## Using Environment Variables in Script Paths

Environment variables can be used in script paths using the `${ENV_VAR}` syntax:

```properties
script.say-hello=${USER_HOME}/script/directory/sayHello.sh
```

If the `USER_HOME` environment variable is set on the application server, it will be resolved at startup.
