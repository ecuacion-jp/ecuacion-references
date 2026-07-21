`SplibExceptionHandler` implements Spring Batch's `ExceptionHandler` and is attached to every step
built through
[`preparedStepBuilder`](/public/showMarkdown/page?id=batch/job-and-step-builders&lang=en) — no extra
wiring is needed once your job configuration extends `SplibAppParentBatchConfig`.

Unlike `ecuacion-splib-web` and `ecuacion-splib-rest`, where using their respective exception handler
is optional, batch apps always go through this one path: the framework doesn't need per-app
variation the way a web response format might.

## What happens when a tasklet throws

1. The current job / step / tasklet-or-chunk name is logged at `INFO`, sourced from
   [`SplibBatchAdvice`](/public/showMarkdown/page?id=batch/current-execution-context&lang=en) — e.g.
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

Register a bean implementing `jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` to
run custom logic — sending an alert email, notifying a monitoring system, and so on — whenever step 3
above runs. It is optional: if no such bean is registered, step 3 is simply skipped.

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  @Override
  public void execute(@Nullable Throwable th) {
    // e.g. MailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
```

This is the same extension point `ecuacion-splib-web` and `ecuacion-splib-rest` use for their own
exception handlers, so an application with multiple front ends (a batch job plus a REST API, for
example) can share one implementation.
