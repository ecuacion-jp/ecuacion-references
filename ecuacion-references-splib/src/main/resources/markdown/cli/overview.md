`ecuacion-splib-cli` is the ecuacion-splib module for building command-line (CUI) applications:
programs a user runs directly and watches interactively — as opposed to `ecuacion-splib-batch`,
which is built for jobs a scheduler triggers unattended, with a developer investigating any
failure later via logs.

## What it provides

- **Minimal Spring Boot bootstrap** — `SplibCliApplication` starts the Spring context, runs your
  app's single `SplibCliRunner` bean once, and exits with the right process exit code. There is no
  Job/Step/JobRepository machinery, unlike `ecuacion-splib-batch`.
- **Single entry-point contract** — `SplibCliRunner`, one method (`execute(String[] args)`) your
  app implements. See [Quickstart](page?id=cli/quickstart&lang=en).
- **A "running..." indicator while `execute` is in progress** — an animated, localized status
  line on the console, cleared automatically once `execute` returns; skipped entirely when the
  output isn't an interactive terminal (e.g. redirected to a file), so it never pollutes
  non-interactive output.
- **Console-first exception handling** — `SplibExceptionHandler`, registered automatically (no
  `@ComponentScan` needed on your side), prints a short, localized, user-facing message. The full
  detail is always passed to `LogUtil.logSystemError`, but by default that reaches no destination
  at all (see [Quickstart](page?id=cli/quickstart&lang=en) on the quiet-console default) — it
  costs nothing and starts working the moment your app configures a real logger/appender. See
  [Exception Handling](page?id=cli/exception-handling&lang=en).

## Dependencies

`ecuacion-splib-cli` depends on `ecuacion-splib-core` and `ecuacion-splib-ui` (shared UI-facing
logic, such as the required-field violation filtering used by
[Exception Handling](page?id=cli/exception-handling&lang=en)), and pulls in
`spring-boot-starter`.
