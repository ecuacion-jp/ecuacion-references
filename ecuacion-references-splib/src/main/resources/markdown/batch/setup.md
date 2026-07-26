## 1. Add the dependency

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-batch</artifactId>
</dependency>
```

## 2. Enable `SplibBatchConfig`

`SplibBatchConfig` component-scans the packages that make `ecuacion-splib-batch` work
(`jp.ecuacion.splib.core.config`, `jp.ecuacion.splib.batch.advice`, `jp.ecuacion.splib.batch.listener`,
`jp.ecuacion.splib.batch.exceptionhandler`). Import it from your application configuration:

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.batch.config")
public class AppConfig {
}
```

## 3. Write the application's main class

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

## 4. Extend `SplibAppParentBatchConfig`

`SplibAppParentBatchConfig` is an abstract class that gives your job configuration pre-wired
`JobBuilder`/`TaskletStepBuilder` factory methods — see
[Job and Step Builders](page?id=batch/job-and-step-builders&lang=en). Its
constructor takes the listener/exception-handler beans `SplibBatchConfig` registers; declare them as
constructor parameters and pass them straight to `super(...)`:

```java
@Configuration
public class AppBatchConfig extends SplibAppParentBatchConfig {

  public AppBatchConfig(SplibJobExecutionListener jobExecutionListener,
      SplibStepExecutionListener stepExecutionListener, SplibExceptionHandler exceptionHandler) {
    super(jobExecutionListener, stepExecutionListener, exceptionHandler);
  }

  // define @Bean Job / Step methods here, using preparedJobBuilder(...) / preparedStepBuilder(...)
}
```

## 5. (Optional) Handle exceptions from your own actions

Register a bean implementing `SplibExceptionHandlerAction` if you want a side effect (such as sending
an alert email) to run whenever an uncaught exception reaches the batch exception handler. See
[Exception Handling](page?id=batch/exception-handling&lang=en).
