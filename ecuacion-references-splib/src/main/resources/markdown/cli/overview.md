`ecuacion-splib-cli` is the ecuacion-splib module for building command-line (CUI) applications:
programs a user runs directly and watches interactively — as opposed to `ecuacion-splib-batch`,
which is built for jobs a scheduler triggers unattended, with a developer investigating any
failure later via logs.

## What it provides

| Feature | Description |
| --- | --- |
| Minimal Spring Boot bootstrap | `SplibCliApplication` starts the Spring context, runs your app's single `SplibCliRunner` bean once,<br>and exits with the right process exit code.<br>There is no Job/Step/JobRepository machinery, unlike `ecuacion-splib-batch`. |
| Single entry-point contract | Your app only implements one method (`execute(String[] args)`) on `SplibCliRunner`.<br>See [Quickstart](page?id=cli/quickstart&lang=en). |
| A "running..." indicator while `execute` is in progress | An animated, localized status line on the console, cleared automatically once `execute` returns; skipped entirely when the output isn't an interactive terminal (e.g. redirected to a file), so it never pollutes<br>non-interactive output. |
| Console-first exception handling | `SplibExceptionHandler`, registered automatically (no `@ComponentScan` needed on your side), prints a short, localized, user-facing message.<br>See [Exception Handling](page?id=cli/exception-handling&lang=en). |

## Dependencies

`ecuacion-splib-cli` depends on `ecuacion-splib-core` and `ecuacion-splib-ui` (shared UI-facing
logic, such as the required-field violation filtering used by
[Exception Handling](page?id=cli/exception-handling&lang=en)), and pulls in
`spring-boot-starter`.
