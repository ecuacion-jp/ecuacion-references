`SplibExceptionHandler` implements Spring Batch's `ExceptionHandler` and is attached to every step
built through
[`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=en) — no extra
wiring is needed once your job configuration extends `SplibAppParentBatchConfig`.

Unlike `ecuacion-splib-web` and `ecuacion-splib-rest`, where using their respective exception handler
is optional, batch apps always go through this one path: the framework doesn't need per-app
variation the way a web response format might.

## What happens when a tasklet throws

1. The current job / step / tasklet-or-chunk name is logged at `INFO`, sourced from
   [`SplibBatchAdvice`](page?id=batch/current-execution-context&lang=en) — e.g.
   `job: importJob, step: importStep, tasklet or chunk: ImportTasklet`. Any piece not available yet
   (the failure happened before the corresponding advice ran) is reported as such instead of left
   blank.
2. The exception itself is logged via `LogUtil.logSystemError`.
3. Your application's `SplibExceptionHandlerAction` bean, if one is registered, is invoked. An
   exception thrown from the action itself is caught and logged, not allowed to mask the original
   failure.
4. If the exception is a `ViolationException`, every violation message it carries is logged
   individually (in the fallback locale) between `==========` markers, so all of them are visible in
   the log even though only one exception is thrown.
5. The original exception is re-thrown — this handler observes and reports, it never swallows a
   failure or changes the job's outcome.

## Running your own logic on failure

Register a bean implementing
`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` to run custom logic
whenever step 3 above runs — write whatever you need inside `execute(Throwable th)`. It is optional:
if no such bean is registered, step 3 is simply skipped.

For example, here's what it looks like to send the stack trace as an alert email:

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  private final SplibMailUtil mailUtil;

  public AppExceptionHandlerAction(SplibMailUtil mailUtil) {
    this.mailUtil = mailUtil;
  }

  @Override
  public void execute(@Nullable Throwable th) {
    mailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
```

Sending mail is just one example of what you can do here. See
[SplibMailUtil](page?id=core/util/mail-util&lang=en) for what it takes to actually send mail (the
`spring.mail.*` and `jp.ecuacion.splib.mail.*` settings) — without them, the call above is silently
skipped.

This is the same interface `ecuacion-splib-web` uses for its own exception handler — but since a
batch app is always its own standalone process (never combined with a web/REST app in the same
running application), there's no need to differentiate; both simply share
`SplibExceptionHandlerAction`. `ecuacion-splib-rest` is the exception: it has its own
`SplibRestExceptionHandlerAction`, since a REST API frontend commonly runs in the same process as
a web frontend.

## Testing it without a real bug

To exercise everything described above — including your own `SplibExceptionHandlerAction` — without
writing a tasklet that fails on purpose, use the built-in
[System Error Job](page?id=batch/system-error-job&lang=en).
