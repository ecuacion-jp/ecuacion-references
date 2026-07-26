`SplibRestExceptionHandler` (`@RestControllerAdvice`, extending Spring's
`ResponseEntityExceptionHandler`) is registered automatically once `SplibRestConfig` is imported — see
[Quickstart](page?id=rest/quickstart&lang=en). It handles two cases.

## `HttpStatusException`

Throw `jp.ecuacion.splib.rest.exception.HttpStatusException` to return a specific HTTP status with no
body:

```java
throw new HttpStatusException(HttpStatus.NOT_FOUND);
```

This is translated directly into `ResponseEntity.status(exception.getHttpStatus()).build()`.

## Any other uncaught `Throwable`

Any other exception that reaches the handler is:

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
