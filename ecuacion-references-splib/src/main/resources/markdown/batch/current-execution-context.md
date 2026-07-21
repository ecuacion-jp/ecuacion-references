`SplibBatchAdvice` tracks the name of the job, step, and tasklet-or-chunk currently executing on each
thread, so that
[Logging](/public/showMarkdown/page?id=batch/logging&lang=en) and
[Exception Handling](/public/showMarkdown/page?id=batch/exception-handling&lang=en) can report where
in the batch a log line or failure happened, without either of those classes having to be told
explicitly. You do not normally call it yourself — it is populated automatically as your job runs.

## How the name is captured

Each of the three names is held in its own `ThreadLocal<String>` and set by a different mechanism:

| Name | Set by |
| --- | --- |
| Job name | `SplibJobExecutionListener.beforeJob` |
| Step name | `SplibStepExecutionListener.beforeStep` |
| Tasklet-or-chunk name | An AspectJ `@Before` advice on `Tasklet.execute(..)` (and, less certainly — see below — `Chunk.execute(..)`) |

The job/step listeners are attached automatically by
[`preparedJobBuilder`/`preparedStepBuilder`](/public/showMarkdown/page?id=batch/job-and-step-builders&lang=en).
The tasklet-or-chunk advice runs on every call to those methods application-wide — this is why
`ecuacion-splib-batch` depends on `spring-boot-starter-aspectj`.

## Reading the values

```java
String job = SplibBatchAdvice.getCurrentJob();
String step = SplibBatchAdvice.getCurrentStep();
String taskletOrChunk = SplibBatchAdvice.getCurrentTaskletOrChunk();
```

These are the same values [Exception Handling](/public/showMarkdown/page?id=batch/exception-handling&lang=en)
logs when a tasklet throws. Since each is a plain `ThreadLocal`, a value set on one thread is not
visible from another — relevant if a job hands work off to a separate thread pool rather than running
everything on the step's own thread.

## A caveat on chunk-oriented steps

The advice on `Chunk.execute(..)` is present in the source, but its own documentation notes the
implementation is unconfirmed ("not concreted") for chunk-oriented processing. If your step uses
Spring Batch's chunk model rather than a plain `Tasklet`, verify that
`SplibBatchAdvice.getCurrentTaskletOrChunk()` reports what you expect before relying on it.
