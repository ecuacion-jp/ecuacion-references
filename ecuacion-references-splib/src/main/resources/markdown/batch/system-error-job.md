`ecuacion-splib-batch` provides a built-in job, `ecuacionSystemErrorJob`, for testing the
[exception handling](page?id=batch/exception-handling&lang=en) behavior (logging, your own
`SplibExceptionHandlerAction`, and so on) without requiring an actual bug.

Its single step runs a tasklet that unconditionally throws a `RuntimeException`, so the failure
goes through the same `SplibExceptionHandler` / `SplibJobExecutionListener` path as any other job
built with [`preparedJobBuilder`/`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=en).

## Running it

The job bean is registered only when `spring.batch.job.name=ecuacionSystemErrorJob` is explicitly
set. If it were always registered alongside your app's own job, Spring Boot couldn't tell which
job to run automatically, and startup would fail with
`Job name must be specified in case of multiple jobs`.

So specify it explicitly at run time, either via:

```properties
spring.batch.job.name=ecuacionSystemErrorJob
```

or as a command-line argument:

```
--spring.batch.job.name=ecuacionSystemErrorJob
```
