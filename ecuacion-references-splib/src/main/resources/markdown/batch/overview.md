`ecuacion-splib-batch` is the ecuacion-splib module for building Spring Batch jobs. It builds on
`ecuacion-splib-core` and adds the pieces a batch job needs on top of plain Spring Batch: standard
`Job`/`Step` builders, execution logging, and a common exception handler.

## What it provides

- **Standard Job/Step builders** — `SplibAppParentBatchConfig`, an abstract class your job
  configuration extends, that returns pre-wired `JobBuilder`/`TaskletStepBuilder` instances (listeners
  and exception handler already attached). See
  [Job and Step Builders](page?id=batch/job-and-step-builders&lang=en).
- **Execution logging** — a job/step listener pair that logs job start/end (success or failure) to a
  dedicated logger. See
  [Logging](page?id=batch/logging&lang=en).
- **Common exception handling** — every tasklet built through `SplibAppParentBatchConfig` shares one
  `ExceptionHandler` that logs the current job/step/tasklet context before the exception propagates.
  See [Exception Handling](page?id=batch/exception-handling&lang=en).
- **Current execution context tracking** — an AspectJ-based mechanism that keeps track of which
  job/step/tasklet is currently running, so the logging and exception-handling features above can
  report it. See
  [Current Execution Context](page?id=batch/current-execution-context&lang=en).

## Dependencies

`ecuacion-splib-batch` depends on `ecuacion-splib-core`, and pulls in `spring-boot-starter-batch` and
`spring-boot-starter-aspectj` (required for the current-execution-context tracking, which is
implemented as an AspectJ `@Aspect`).
