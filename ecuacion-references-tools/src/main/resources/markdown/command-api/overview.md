`ecuacion-tool-command-api` is a WAR module that executes shell scripts on the server via a Web API.
It can run as a standalone executable WAR, or be deployed to an existing application server such as Tomcat.

## Key Features

- Execute server-side scripts with a single HTTP GET request
- Only scripts pre-registered in `ecuacion-tool-command-api-scripts.properties` can be executed (security measure)
- Supports environment variable references (`${ENV_VAR}`) in script paths
- Supports passing parameters to scripts
- Returns the script's exit code in the response

## Use Cases

- Triggering batch processing on the server via HTTP calls from other systems
