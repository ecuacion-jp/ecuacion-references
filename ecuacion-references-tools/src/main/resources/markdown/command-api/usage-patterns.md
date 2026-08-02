This page covers additional usage patterns for `api/public/executeScript` and `api/key/executeScript`, building on the [Quickstart](page?id=command-api/quickstart&lang=en).

## Passing Parameters

Use the `parameters` query parameter to pass arguments to the script:

```
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello&parameters=param1,param2
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
