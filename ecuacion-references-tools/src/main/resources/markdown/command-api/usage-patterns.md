This page covers additional usage patterns for `api/public/execute` and `api/key/execute`, building on the [Quickstart](page?id=command-api/quickstart&lang=en).

## Passing Parameters

Use the `parameters` query parameter to pass arguments to the script:

```
http://localhost:8080/api/public/execute?scriptId=script.say-hello&parameters=param1,param2
```

The above request executes `sayHello.sh param1 param2` (or `sayHello.bat param1 param2` on Windows).

Multiple parameters are separated by commas. Passing a comma character as part of a parameter value is currently not supported.

---

## Using Variable References in Script Paths

Script paths can use `${VAR_NAME}` variable references:

```properties
script.say-hello=${USER_HOME}/script/directory/sayHello.sh
```

If `USER_HOME` is set in `application.properties`, as an OS environment variable, or the like, it will be resolved at request time.
