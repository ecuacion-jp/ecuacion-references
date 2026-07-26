`SplibAppParentBatchConfig` (see [Setup](page?id=batch/setup&lang=en)) provides
two protected factory methods that return `ecuacion-splib` standard, pre-wired builders. Use them
instead of constructing `JobBuilder`/`StepBuilder` directly.

## `preparedJobBuilder`

```java
protected JobBuilder preparedJobBuilder(String jobName, JobRepository jobRepository)
```

Returns a `JobBuilder` with:

- `.incrementer(new RunIdIncrementer())` — so the job can be re-run with a new instance each time.
- `.listener(jobExecutionListener)` — the `SplibJobExecutionListener` passed to the constructor. See
  [Logging](page?id=batch/logging&lang=en).

## `preparedStepBuilder`

```java
protected TaskletStepBuilder preparedStepBuilder(String stepName, JobRepository jobRepository,
    PlatformTransactionManager transactionManager, Tasklet... tasklets)
```

Returns a `TaskletStepBuilder` with:

- `.listener(stepExecutionListener)` — the `SplibStepExecutionListener` passed to the constructor.
- `.exceptionHandler(exceptionHandler)` — the shared `SplibExceptionHandler`. See
  [Exception Handling](page?id=batch/exception-handling&lang=en).

It accepts one or more `Tasklet`s and chains them onto the same step builder, so a step made of
several tasklets in sequence can be built in one call.

## Example

```java
@Bean
Job importJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
    Tasklet importTasklet) {
  return preparedJobBuilder("importJob", jobRepository)
      .start(preparedStepBuilder("importStep", jobRepository, transactionManager, importTasklet)
          .build())
      .build();
}
```

Because both builders already have the listener(s) and exception handler attached, jobs and steps
built this way automatically get the logging described in
[Logging](page?id=batch/logging&lang=en) and the exception handling described in
[Exception Handling](page?id=batch/exception-handling&lang=en) without any extra
wiring per job.
