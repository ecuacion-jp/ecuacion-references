This page covers `ecuacion-tool-command-api`'s security model: pre-registered scripts, input validation, and access control.

## Script Pre-Registration

Only scripts registered in `ecuacion-tool-command-api-scripts.properties` can be executed.
Callers cannot specify arbitrary script paths in the request.

However, you remain responsible for the content of the scripts you register.
Even though arbitrary paths cannot be specified, a registered script can still contain destructive operations.

## Input Validation

The `scriptId` parameter value is validated against the regular expression `^[a-zA-Z0-9.\-_]*$`.

The `parameters` value is validated against the regular expression `^[a-zA-Z0-9 ./:_=@\-]*$`.

Script file paths (as registered in `ecuacion-tool-command-api-scripts.properties`) are validated against the regular expression `^[a-zA-Z0-9.\-_/${}]*$`.

## Access Control

`X-Api-Key` is a **shared secret** compared against a file placed on the server. See [Access Control](page?id=command-api/access-control&lang=en) for the full property reference (`api-key-required`, `api-key-comparison-mode`) and how the key file is managed.
