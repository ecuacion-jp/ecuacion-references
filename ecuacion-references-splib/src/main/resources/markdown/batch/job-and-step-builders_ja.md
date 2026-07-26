`SplibAppParentBatchConfig`（[セットアップ](page?id=batch/setup&lang=ja) 参照）は、
ecuacion-splib 標準の、あらかじめ組み込み済みのビルダーを返す 2 つの protected ファクトリメソッドを提供します。
`JobBuilder`/`StepBuilder` を直接生成する代わりにこれらを使ってください。

## `preparedJobBuilder`

```java
protected JobBuilder preparedJobBuilder(String jobName, JobRepository jobRepository)
```

以下が設定された `JobBuilder` を返します。

- `.incrementer(new RunIdIncrementer())` — 実行のたびに新しいインスタンスとして再実行できるようにする。
- `.listener(jobExecutionListener)` — コンストラクターに渡された `SplibJobExecutionListener`。
  [ロギング](page?id=batch/logging&lang=ja) を参照してください。

## `preparedStepBuilder`

```java
protected TaskletStepBuilder preparedStepBuilder(String stepName, JobRepository jobRepository,
    PlatformTransactionManager transactionManager, Tasklet... tasklets)
```

以下が設定された `TaskletStepBuilder` を返します。

- `.listener(stepExecutionListener)` — コンストラクターに渡された `SplibStepExecutionListener`。
- `.exceptionHandler(exceptionHandler)` — 共有の `SplibExceptionHandler`。
  [例外処理](page?id=batch/exception-handling&lang=ja) を参照してください。

1 つ以上の `Tasklet` を受け取り、同じステップビルダーにチェーンして適用するため、
複数の Tasklet を順に実行する 1 つの Step を 1 回の呼び出しで構築できます。

## 使用例

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

両ビルダーにはあらかじめリスナーと例外ハンドラーが組み込まれているため、この方法で構築した
Job / Step は、ジョブごとに追加の配線をすることなく
[ロギング](page?id=batch/logging&lang=ja) で説明するログ出力と
[例外処理](page?id=batch/exception-handling&lang=ja) で説明する例外処理を自動的に得られます。
