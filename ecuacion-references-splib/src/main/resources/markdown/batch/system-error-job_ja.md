`ecuacion-splib-batch` は、組み込みのJob `ecuacionSystemErrorJob` を提供しています。実際のバグを
起こすことなく、[例外処理](page?id=batch/exception-handling&lang=ja)の挙動（ログ出力、独自の
`SplibExceptionHandlerAction` など）をテストするためのものです。

このJobの唯一のStepは、無条件に `RuntimeException` をスローするタスクレットを実行するだけです。
そのため、[`preparedJobBuilder`/`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=ja)
で構築した他のJobと同様に、`SplibExceptionHandler` / `SplibJobExecutionListener` という同じ経路で
失敗が処理されます。

## 有効化

このJob Beanは、application.properties で
`jp.ecuacion.splib.batch.ecuacion-system-error-job.enabled` を明示的に `true` に設定した場合のみ
登録されます。

```properties
jp.ecuacion.splib.batch.ecuacion-system-error-job.enabled=true
```

本番環境では未設定（または `false`）のままにしてください。発火させても問題のない環境で、
一時的に有効化することを想定しています。

## 実行方法

有効化した後は、他のJobと同様に Spring Boot 標準のJob選択用プロパティで指定します。

```properties
spring.batch.job.name=ecuacionSystemErrorJob
```

またはコマンドライン引数として：

```
--spring.batch.job.name=ecuacionSystemErrorJob
```
