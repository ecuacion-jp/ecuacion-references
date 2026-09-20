`SplibExceptionHandler` is an abstract `@ControllerAdvice` your application extends. If no
`SplibExceptionHandler` bean is registered at all,
[Overview](page?id=web/overview&lang=en)'s `SplibWebExceptionHandlerAutoConfiguration` provides a
minimal fallback that handles only `ViolationException`; extend `SplibExceptionHandler` once the
application needs the full set below.

```java
@ControllerAdvice
public class AppExceptionHandler extends SplibExceptionHandler {

  public AppExceptionHandler(HttpServletRequest request,
      @Nullable SplibExceptionHandlerAction actionOnThrowable, SplibLoginStateUtil loginStateUtil) {
    super(request, actionOnThrowable, loginStateUtil);
  }
}
```

## What it handles

| Exception | Treatment |
| --- | --- |
| `ViolationWarningException` | A confirmation-style warning (e.g. "this will overwrite existing data — continue?"). Re-renders the same page with the warning message and a marker for which button was pressed, rather than redirecting — the submit did not complete. |
| `ViolationException` | See "Two redirect paths" below. |
| `ConstraintViolationException` | Wrapped into a `ViolationException` and handled the same way. |
| `NoResourceFoundException` | No `@RequestMapping` matches the requested URL. Redirected to the home page with a "not found" message. |
| `RedirectException` (and subclasses like `RedirectToHomePageException`) | An application-thrown signal to redirect to a specific path with an optional message — e.g. "the requested record no longer exists." |
| `OverlappingFileLockException` | Optimistic-locking-style conflict. On an edit page, redirected back to the record's view page with a message; elsewhere, handled as a `ViolationException`. |
| `MaxUploadSizeExceededException` | An uploaded file exceeded `spring.servlet.multipart.max-file-size` / `max-request-size`. This fires before the controller's `prepare()` runs (no model/forms yet), so it redirects to the referring page (see [Open Redirect Protection](page?id=web/security/open-redirect-protection&lang=en)) with a flash message, the same way `ViolationException` does without a controller. |
| Any other `Throwable` | A genuinely unanticipated exception (a bug, not a reported failure). Logged via `LogUtil.logSystemError`, passed to your registered `SplibExceptionHandlerAction` bean (see below) if any, then rendered as the generic `error` view with HTTP status `500`. |

## Two redirect paths for `ViolationException`

Which of these runs depends on whether a `SplibGeneralController` (and its forms) is present in
the model:

- **Controller present** (the common case — a real page built on `SplibGeneral1FormController` or
  similar): violation messages are attached to the relevant field's `BindingResult` (or shown at
  the top of the page, depending on the property settings below), and the browser is redirected
  back to the same page to re-render it with those errors.
- **Controller absent** (a plain `@Controller` / `SplibBaseController`, with no forms): there is no
  `BindingResult` to attach anything to, so the messages are passed via a flash attribute and the
  browser is redirected back to the referring page instead (via
  [Open Redirect Protection](page?id=web/security/open-redirect-protection&lang=en), since the
  target comes from the `Referer` header).

## Message display: at-item vs at-top

Two `application.properties` keys control how a `ViolationException`'s messages are shown, and at
least one must be `true`:

```properties
jp.ecuacion.splib.web.process-result-message.shown-at-each-item=true
jp.ecuacion.splib.web.process-result-message.shown-at-the-top=false
```

`shown-at-each-item` places the message next to the offending field; `shown-at-the-top` places it
in a summary at the top of the page. A violation with no specific field to attach to (a
class-level constraint, or a message with no property path) is always shown at the top regardless
of these settings, since there is nowhere else to put it. When at least one field-level message is
shown, a summary line pointing users to scroll down is also added at the top automatically.

## Running your own logic on uncaught exceptions

Register a bean implementing
`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` to run custom logic whenever
an uncaught `Throwable` reaches the handler — write whatever's needed inside `execute(Throwable
th)`. This is the same shared extension point `ecuacion-splib-rest` and `ecuacion-splib-batch`
use; see [Exception Handling](page?id=rest/exception-handling&lang=en) under **rest** for a
sample implementation (sending the stack trace as an alert email via `SplibMailUtil`).
