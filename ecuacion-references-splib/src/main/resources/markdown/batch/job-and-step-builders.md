`SplibAppParentBatchConfig` (see [Quickstart](page?id=batch/quickstart&lang=en)) provides
two protected factory methods that return `ecuacion-splib` standard, pre-wired builders.
Constructing `JobBuilder`/`StepBuilder` directly, the standard way, still works fine — using
these instead simplifies configuration by wiring in the listeners and exception handler for you.

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

## Combining multiple Tasklets into one Job

To run several Steps (each wrapping one Tasklet) in sequence as a single Job, chain `.next(...)`
after `.start(...)`.

```java
@Bean
Job importAndNotifyJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
    Tasklet importTasklet, Tasklet notifyTasklet) {
  return preparedJobBuilder("importAndNotifyJob", jobRepository)
      .start(preparedStepBuilder("importStep", jobRepository, transactionManager, importTasklet)
          .build())
      .next(preparedStepBuilder("notifyStep", jobRepository, transactionManager, notifyTasklet)
          .build())
      .build();
}
```

Extracting each Step into its own `@Bean` method lets it be reused both as part of the combined
Job and as a standalone Job of its own.
