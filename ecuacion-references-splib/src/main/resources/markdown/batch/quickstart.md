Assuming the dependency from [Setup](page?id=batch/setup&lang=en) is in place,
this page wires up the required configuration classes and adds one job that actually runs.

## 1. Enable `SplibBatchConfig`

`SplibBatchConfig` component-scans the packages that make `ecuacion-splib-batch` work
(`jp.ecuacion.splib.core.config`, `jp.ecuacion.splib.batch.advice`, `jp.ecuacion.splib.batch.listener`,
`jp.ecuacion.splib.batch.exceptionhandler`). Import it from your application configuration:

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.batch.config")
public class AppConfig {
}
```

## 2. Write the application's main class

`SplibBatchApplication` provides the `main` method logic every batch app needs (running
`SpringApplication` and exiting with its result code), but it cannot be the class Java actually
launches — the `java` command does not follow a `main` method inherited from a parent class. Your
application therefore needs its own class with its own `main` method that simply delegates:

```java
@SpringBootApplication
public class BatchApplication {

  public static void main(String[] args) {
    SplibBatchApplication.main(BatchApplication.class, args);
  }
}
```

## 3. Extend `SplibAppParentBatchConfig` and define a job

`SplibAppParentBatchConfig` is an abstract class that gives your job configuration pre-wired
`JobBuilder`/`TaskletStepBuilder` factory methods. Its constructor takes the listener/exception-handler
beans `SplibBatchConfig` registers; declare them as constructor parameters and pass them straight to
`super(...)`.

A `Tasklet` to run:

```java
@Component
public class HelloTasklet implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    System.out.println("Hello, world!");
    return RepeatStatus.FINISHED;
  }
}
```

A job configuration that builds a one-step job around it, using `preparedJobBuilder`/
`preparedStepBuilder` instead of constructing `JobBuilder`/`StepBuilder` directly:

```java
@Configuration
public class AppBatchConfig extends SplibAppParentBatchConfig {

  public AppBatchConfig(SplibJobExecutionListener jobExecutionListener,
      SplibStepExecutionListener stepExecutionListener, SplibExceptionHandler exceptionHandler) {
    super(jobExecutionListener, stepExecutionListener, exceptionHandler);
  }

  @Bean
  Job helloJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      HelloTasklet helloTasklet) {
    return preparedJobBuilder("helloJob", jobRepository)
        .start(preparedStepBuilder("helloStep", jobRepository, transactionManager, helloTasklet)
            .build())
        .build();
  }
}
```

## 4. Run it

Spring Boot's batch auto-configuration runs every `Job` bean found in the context when the
application starts, so starting the application is enough — no separate trigger is needed:

```
mvn spring-boot:run
```

```
START: job-name: helloJob
Hello, world!
END  : job-name: helloJob [NORMAL END]
```

The `START`/`END` lines come from the job listener wired in by `preparedJobBuilder` — see
[Logging](page?id=batch/logging&lang=en).

From here: [Job and Step Builders](page?id=batch/job-and-step-builders&lang=en)
covers what `preparedJobBuilder`/`preparedStepBuilder` set up in more detail.
[Exception Handling](page?id=batch/exception-handling&lang=en) covers what
happens when a tasklet throws, including how to run your own side effect (such as sending an alert
email) on uncaught exceptions — optional, and not needed to get a job running.
[Current Execution Context](page?id=batch/current-execution-context&lang=en)
explains the job/step/tasklet tracking mechanism the two features above build on.
