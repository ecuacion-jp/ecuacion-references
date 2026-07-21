`SplibRestExceptionHandler` (`@RestControllerAdvice`, extending Spring's
`ResponseEntityExceptionHandler`) is registered automatically once `SplibRestConfig` is imported — see
[Setup](/public/showMarkdown/page?id=rest/setup&lang=en). It handles two cases.

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
2. Passed to your application's `SplibExceptionHandlerAction` bean, if one is registered (see below).
3. Turned into an `ErrorResponse` with HTTP status `501` and the message `"Internal Server Error..."`.

## Running your own logic on uncaught exceptions

Register a bean implementing `jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` to
run custom logic — sending an alert email, notifying a monitoring system, and so on — whenever step 2
above runs. It is optional: if no such bean is registered, step 2 is simply skipped.

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  @Override
  public void execute(@Nullable Throwable th) {
    // e.g. MailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
```

This is the same extension point `ecuacion-splib-web` uses for its own (HTML) exception handler, so
an application that has both a web and a REST front end can share one implementation.
