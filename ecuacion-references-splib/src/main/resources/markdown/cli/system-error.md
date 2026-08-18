`ecuacion-splib-cli` provides a built-in way to deliberately trigger a system error, for testing
the [exception handling](page?id=cli/exception-handling&lang=en) behavior (console output,
logging, your own `SplibExceptionHandlerAction`, and so on) without requiring an actual bug — the
CLI counterpart of `ecuacion-splib-batch`'s built-in `ecuacionSystemErrorJob`.

## Running it

Run your app with `--ecuacion-system-error` among its arguments:

```
java -jar your-app.jar --ecuacion-system-error
```

`SplibCliApplication#main` checks for this flag before calling your app's
`SplibCliRunner#execute`, and if present, throws a `RuntimeException` in its place. The failure
then goes through the same `SplibExceptionHandler` path as any other uncaught exception from your
app.

Unlike `ecuacion-splib-batch`'s `spring.batch.job.name=ecuacionSystemErrorJob` (which selects
between multiple `Job` beans), a CLI app only ever has one `SplibCliRunner` bean, so this is a
plain flag checked directly in `main`, rather than a Spring-managed alternative bean.
