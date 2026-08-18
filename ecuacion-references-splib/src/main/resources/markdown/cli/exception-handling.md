`SplibExceptionHandler` wraps the single call to your app's `SplibCliRunner#execute` made by
`SplibCliApplication#main` — no extra wiring is needed; it registers itself via Spring Boot
auto-configuration.

Unlike `ecuacion-splib-batch`, where the exception handler logs the current job/step/tasklet
context (since a developer typically investigates a failure later via logs), a CLI app is watched
directly by the person running it, so the console message is kept short and user-facing by
default, without a stack trace.

## What happens when `execute` throws

1. If the exception is a `ViolationException`, a short localized header (e.g. "An error occurred
   while running.") is printed, followed by each violation message as a bullet list (in the
   fallback locale) — after filtering out any `ConstraintViolation` already masked by a
   required-field `BusinessViolation` on the same item, the same rule `ecuacion-splib-web` uses
   for its own error display (see `SplibViolationUtil` in `ecuacion-splib-ui`). For any other
   exception, a short generic message is printed instead.
2. The exception is always passed to `LogUtil.logSystemError`. With ecuacion-splib-cli's
   recommended default logback config (see [Quickstart](page?id=cli/quickstart&lang=en)), this
   reaches no destination at all — by design, a CLI app writes no log file unless the app
   configures its own logger/appender (see "Getting more detail" below for why this is fine).
3. Your application's `SplibExceptionHandlerAction` bean, if one is registered, is invoked. An
   exception thrown from the action itself is caught and logged, not allowed to mask the original
   failure.
4. The process exits with a non-zero exit code.

## Getting more detail: `--verbose`

Run the app with `--verbose` and, on top of the concise message above, the full stack trace is
printed to `System.err` too — a plain, explicit check in `SplibExceptionHandler`, unrelated to
logback's own level/appender configuration (which stays off by default either way). If you need
to keep that output, redirect it yourself the way you would with any other command-line tool:

```
java -jar your-app.jar --verbose 2> error-report.txt
```

This is deliberately simpler than trying to change logback's log level at runtime: a CLI app is
watched directly by whoever runs it, so there is no "someone investigates via log files later" use
case to build extra machinery for. If your app wants that anyway (e.g. an internal tool where you
always want a log file kept), add your own logger/appender to your app's `logback-spring.xml`
instead of the plain `<root level="OFF" />` from [Quickstart](page?id=cli/quickstart&lang=en) —
`LogUtil.logSystemError` in step 2 above already calls through to it, so no code changes are
needed for that to start working.

## Running your own logic on failure

Register a bean implementing
`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` to run custom logic whenever
step 3 above runs — write whatever you need inside `execute(Throwable th)`. It is optional: if no
such bean is registered, step 3 is simply skipped.

This is the same interface `ecuacion-splib-web` and `ecuacion-splib-batch` use for their own
exception handlers. `ecuacion-splib-cli` deliberately does not decide for you whether (or how) a
developer should be notified of a failure so they can investigate — that's exactly what this
extension point is for. For example, an in-house tool might use it to send an alert email; see
[SplibMailUtil](page?id=core/util/mail-util&lang=en) for what it takes to actually send mail.

## Testing it without a real bug

To exercise everything described above — including your own `SplibExceptionHandlerAction` —
without writing code that fails on purpose, run your app with the built-in
[`--ecuacion-system-error` flag](page?id=cli/system-error&lang=en).

## A note on distribution: a jar's contents aren't confidential

`--verbose` is opt-in — the console shows no stack trace unless someone asks for one. Still, it's
a fair question whether printing the full trace via `--verbose` at all is something you want to
allow.

Your app's own `main` could strip `--verbose` out of `args` before handing them to
`SplibCliApplication.main`, silencing the flag entirely. That wouldn't actually fix much, though.
A fat jar (a single executable jar bundling all dependencies) — the kind `java -jar your-app.jar`
runs — is a distribution format whose logic can mostly be read back out just by unpacking it and
decompiling the `.class` files. If your app contains logic that genuinely needs to stay
confidential (proprietary business rules, an algorithm, etc.), shipping it to clients as a fat jar
already carries that risk, regardless of whether `--verbose` exists.

If confidentiality is an actual requirement, address the distribution format itself rather than
hiding `--verbose`:

- Native-image compilation (e.g. via GraalVM) — you no longer ship raw bytecode, which raises the
  bar for reverse-engineering considerably (it doesn't make it impossible).
- A client-server split — keep the logic that must stay confidential on a server you control, and
  have the client only receive results, so the logic itself never leaves your hands.

`ecuacion-splib-cli` itself assumes the "ship the executable, run it locally" distribution model;
choosing among the alternatives above is an application-level decision.
