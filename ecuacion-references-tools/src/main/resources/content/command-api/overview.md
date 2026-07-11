# command-api Overview

`ecuacion-tool-command-api` is a WAR module that executes shell scripts on the server via a Web API.
It can run as a standalone executable WAR, or be deployed to an existing application server such as Tomcat.

## Key Features

- Execute server-side scripts with a single HTTP GET request
- Only scripts pre-registered in `ecuacion-tool-command-api.properties` can be executed (security measure)
- Supports environment variable references (`${ENV_VAR}`) in script paths
- Supports passing parameters to scripts
- Returns the script's exit code in the response

## Use Cases

- Triggering batch processing on the server via HTTP calls from other systems
- Launching server operations that cannot be performed through a web app directly (e.g., file generation and placement)

## Downloading the Module

The WAR file is available at:

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-command-api/
```

## Documentation

- [Setup](/public/showMarkdown/page?id=command-api/setup&lang=en)
- [Quick Start](/public/showMarkdown/page?id=command-api/quickstart&lang=en)
- [Configuration Files](/public/showMarkdown/page?id=command-api/config&lang=en)
- [API Spec](/public/showMarkdown/page?id=command-api/api-spec&lang=en)
