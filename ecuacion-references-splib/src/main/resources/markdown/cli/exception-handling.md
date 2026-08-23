`SplibExceptionHandler` wraps the single call to your app's `SplibCliRunner#execute` made by
`SplibCliApplication#main` — no extra wiring is needed; it registers itself via Spring Boot
auto-configuration.

Unlike `ecuacion-splib-batch`, where the exception handler logs the current job/step/tasklet
context (since a developer typically investigates a failure later via logs), a CLI app is watched
directly by the person running it, so the console message is kept short and user-facing by
default, without a stack trace.

## Throwing from your app

To show the user a localized message, throw `jp.ecuacion.lib.core.exception.ViolationException`.
See `ecuacion-lib` for details.

## Exit code

If `execute` throws, the process exits with exit code `1`.

Your own code — inside `execute` or elsewhere in your app — can call `System.exit(n)` directly to
exit with any code other than `1`. In that case, none of `SplibCliApplication`'s own post-processing
(the completed message, `SplibExceptionHandler`, etc.) runs — the process terminates immediately.

## Running your own logic on failure

Register a bean implementing
`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` to run custom logic whenever
`execute` fails — write whatever you need inside `execute(Throwable th)`. It is optional: if no
such bean is registered, nothing extra runs (an exception thrown from the action itself is caught
and logged, not allowed to mask the original failure).

This is the same interface `ecuacion-splib-web` and `ecuacion-splib-batch` use for their own
exception handlers.

For an in-house tool, sending an alert email on failure is a typical use. Inject
[`SplibMailUtil`](page?id=core/util/mail-util&lang=en) and call `sendErrorMail`:

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  private final SplibMailUtil mailUtil;

  public AppExceptionHandlerAction(SplibMailUtil mailUtil) {
    this.mailUtil = mailUtil;
  }

  @Override
  public void execute(Throwable th) {
    mailUtil.sendErrorMail(th);
  }
}
```

With this in place, every exception `execute` throws goes through this bean and triggers an alert
email to the admin — see [`SplibMailUtil`](page?id=core/util/mail-util&lang=en) for the
`application.properties` settings needed to actually send mail.

## Testing the exception handling without a real bug

To exercise everything described above — including your own `SplibExceptionHandlerAction` —
without writing code that fails on purpose, run your app with the built-in
[`--ecuacion-system-error` flag](page?id=cli/system-error&lang=en).
