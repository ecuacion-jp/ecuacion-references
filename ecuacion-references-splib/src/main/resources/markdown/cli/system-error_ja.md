`ecuacion-splib-cli` は、意図的にシステムエラーを発生させる仕組みを組み込みで提供しています。
実際のバグを起こすことなく、[例外処理](page?id=cli/exception-handling&lang=ja)の挙動
（コンソール出力、ログ出力、独自の `SplibExceptionHandlerAction` など）をテストするためのもので、
`ecuacion-splib-batch` の組み込みJob `ecuacionSystemErrorJob` のCLI版に相当します。

## 実行方法

アプリの引数に `--ecuacion-system-error` を含めて実行してください。

```
java -jar your-app.jar --ecuacion-system-error
```

`SplibCliApplication#main` はアプリの `SplibCliRunner#execute` を呼び出す前にこのフラグを
チェックしており、指定されていれば代わりに `RuntimeException` をスローします。その後の失敗処理は、
アプリから投げられた他の未捕捉例外と同じ `SplibExceptionHandler` の経路をたどります。

`ecuacion-splib-batch` の `spring.batch.job.name=ecuacionSystemErrorJob`（複数の `Job` Bean から
選択する仕組み）とは異なり、CLIアプリは `SplibCliRunner` Beanを常に1つしか持たないため、これは
Spring管理の代替Beanではなく、`main` 内で直接チェックする単純なフラグとして実装されています。
