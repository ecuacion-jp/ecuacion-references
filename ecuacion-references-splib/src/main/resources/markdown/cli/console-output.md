`ecuacion-splib-cli` uses a consistent, timestamped format for console output. This page covers
normal output, detailed error output, and a note on distributing your app.

## Timestamped normal output: `ConsoleUtil.printlnWithTimestamp`

`SplibCliApplication` itself uses this for the "Starting."/"Completed successfully." messages
printed around your `execute` call (see [Quickstart](page?id=cli/quickstart&lang=en)). Call it
directly inside `execute` to print your own messages in the same format.

```java
ConsoleUtil.printlnWithTimestamp(false, "Intermediate step completed.");
```

```
[2026-08-15 15:37:42] Intermediate step completed.
```

Pass `true` for the first argument (`isError`) to print to `System.err` instead of `System.out`.

## Getting more detail on the error: `--verbose`

When your app fails with an exception, only a short, user-facing message is printed by default,
with no stack trace. Run the app with `--verbose` and, on top of that concise message, the full
stack trace is printed to `System.err` too.

A CLI app is watched directly on screen by the person running it, so it favors console output over
log output.

## Reference: configuring log output

If your app wants that anyway (e.g. an internal tool where you always want a log file kept), add
your own logger/appender to your app's `logback-spring.xml` instead of the plain
`<root level="OFF" />` from [Quickstart](page?id=cli/quickstart&lang=en) — `LogUtil.logSystemError`
already calls through to it, so no code changes are needed for that to start working.

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
