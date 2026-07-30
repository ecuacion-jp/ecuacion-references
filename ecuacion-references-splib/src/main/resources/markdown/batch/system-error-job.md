`ecuacion-splib-batch` provides a built-in job, `ecuacionSystemErrorJob`, for testing the
[exception handling](page?id=batch/exception-handling&lang=en) behavior (logging, your own
`SplibExceptionHandlerAction`, and so on) without requiring an actual bug.

Its single step runs a tasklet that unconditionally throws a `RuntimeException`, so the failure
goes through the same `SplibExceptionHandler` / `SplibJobExecutionListener` path as any other job
built with [`preparedJobBuilder`/`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=en).

## Running it

The job bean is always registered, so select it like any other job via Spring Boot's standard
batch job-selection property:

```properties
spring.batch.job.name=ecuacionSystemErrorJob
```

or as a command-line argument:

```
--spring.batch.job.name=ecuacionSystemErrorJob
```
