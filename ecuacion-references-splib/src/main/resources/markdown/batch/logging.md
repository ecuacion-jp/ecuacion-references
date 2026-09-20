`SplibJobExecutionListener` and `SplibStepExecutionListener` are attached automatically by
[`preparedJobBuilder`/`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=en)
— no extra wiring is needed once your job configuration extends `SplibAppParentBatchConfig`.

## `SplibJobExecutionListener`

Logs to a dedicated logger named `"summary-logger"` (configure it separately from your regular
application log if you want job start/end lines routed elsewhere):

- `beforeJob` — logs `START: job-name: <name>`.
- `afterJob` — logs `END  : job-name: <name> [NORMAL END]` on success, or
  `END  : job-name: <name> [ABNORMAL END] exit status: <exitCode>` (at `ERROR` level) on failure.

It also records the running job's name via
[`SplibBatchAdvice`](page?id=batch/current-execution-context&lang=en), so that
name is available to the exception handler if a failure occurs partway through.

## `SplibStepExecutionListener`

Records the running step's name via the same mechanism, in `beforeStep`. It does not log anything on
its own — step-level detail shows up as part of the exception-handling output described in
[Exception Handling](page?id=batch/exception-handling&lang=en), rather than as a
per-step summary line.
