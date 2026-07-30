`ecuacion-splib-batch` は、組み込みのJob `ecuacionSystemErrorJob` を提供しています。実際のバグを
起こすことなく、[例外処理](page?id=batch/exception-handling&lang=ja)の挙動（ログ出力、独自の
`SplibExceptionHandlerAction` など）をテストするためのものです。

このJobの唯一のStepは、無条件に `RuntimeException` をスローするタスクレットを実行するだけです。
そのため、[`preparedJobBuilder`/`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=ja)
で構築した他のJobと同様に、`SplibExceptionHandler` / `SplibJobExecutionListener` という同じ経路で
失敗が処理されます。

## 実行方法

このJob Beanは常に登録されているため、他のJobと同様に Spring Boot 標準のJob選択用プロパティで
指定するだけで実行できます。

```properties
spring.batch.job.name=ecuacionSystemErrorJob
```

またはコマンドライン引数として：

```
--spring.batch.job.name=ecuacionSystemErrorJob
```
