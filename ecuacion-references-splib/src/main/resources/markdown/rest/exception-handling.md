`SplibRestExceptionHandler` (`@RestControllerAdvice`, extending Spring's
`ResponseEntityExceptionHandler`) is registered automatically once `SplibRestConfig` is imported — see
[Quickstart](page?id=rest/quickstart&lang=en). It handles three kinds of exceptions, each with a
deliberately different treatment because each has a different audience.

## `ViolationException`

Throw `jp.ecuacion.lib.core.exception.ViolationException` for a business/validation failure whose
message is meant to reach an actual human end user (e.g. a local/desktop app that calls this API on
a user's behalf and displays the failure to them):

```java
throw new ViolationException(...);
```

Every violation the exception carries is included (not just the first), each message is localized to
the request's locale, and the response is always `400 Bad Request` — no variety of statuses is needed
because nothing on the calling side is expected to branch on it, only display the text. The response
body is a `ViolationsResponse` with a `messages` field holding the localized messages.

## `ResponseStatusException` (and other exceptions Spring's built-in handling covers)

Throw Spring's own `org.springframework.web.server.ResponseStatusException` for a failure whose
message is meant for the developer/system on the other end of the API call (e.g. a server calling
this API programmatically), not a human end user:

```java
throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...");
```

The message is used as-is, not localized, and the throwing code picks whichever status (any
`4xx`/`5xx`) fits — callers are expected to branch on it. This also covers anything else
`ResponseEntityExceptionHandler`'s built-in handling deals with (e.g.
`MethodArgumentNotValidException`, `HttpMessageNotReadableException`).

## Any other uncaught `Throwable`

A genuinely unanticipated exception (a bug, not a reported failure) is:

1. Logged via `LogUtil.logSystemError`.
2. Passed to your application's `SplibRestExceptionHandlerAction` bean, if one is registered (see
   below).
3. Turned into an `ErrorResponse` with HTTP status `501` and the message `"Internal Server Error..."`.

## Running your own logic on uncaught exceptions

Register a bean implementing
`jp.ecuacion.splib.core.exceptionhandler.SplibRestExceptionHandlerAction` to run custom logic
whenever step 2 above runs — write whatever you need inside `execute(Throwable th)`. It is optional:
if no such bean is registered, step 2 is simply skipped.

For example, here's what it looks like to send the stack trace as an alert email:

```java
@Component
public class AppExceptionHandlerAction implements SplibRestExceptionHandlerAction {

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

`ecuacion-splib-web` and `ecuacion-splib-batch` have the same extension point, and both use
`SplibExceptionHandlerAction` — a batch app is always its own standalone process, so there's no
need to differentiate it from web. REST is the exception: it has its own
`SplibRestExceptionHandlerAction`, since a REST API frontend commonly runs in the same process as
a web frontend and an app may want different behavior for each.
