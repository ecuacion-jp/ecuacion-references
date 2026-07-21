## 1. 依存関係の追加

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-batch</artifactId>
</dependency>
```

## 2. `SplibBatchConfig` を有効化する

`SplibBatchConfig` は `ecuacion-splib-batch` の動作に必要なパッケージ
（`jp.ecuacion.splib.core.config`・`jp.ecuacion.splib.batch.advice`・`jp.ecuacion.splib.batch.listener`・
`jp.ecuacion.splib.batch.exceptionhandler`）をコンポーネントスキャンします。
アプリケーションの設定クラスからインポートしてください。

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.batch.config")
public class AppConfig {
}
```

## 3. アプリケーションの main クラスを書く

`SplibBatchApplication` は、すべてのバッチアプリに必要な `main` メソッドのロジック
（`SpringApplication` を実行し、その結果コードで終了する）を提供していますが、
`java` コマンドは親クラスから継承した `main` メソッドを実行できないため、
これ自体を起動対象のクラスにすることはできません。そのため、アプリケーション側で
自身の `main` メソッドを持つクラスを用意し、単純に処理を委譲してください。

```java
@SpringBootApplication
public class BatchApplication {

  public static void main(String[] args) {
    SplibBatchApplication.main(BatchApplication.class, args);
  }
}
```

## 4. `SplibAppParentBatchConfig` を継承する

`SplibAppParentBatchConfig` は抽象クラスで、ジョブ設定クラスに対して
あらかじめ組み込み済みの `JobBuilder`/`TaskletStepBuilder` ファクトリメソッドを提供します。
[Job / Step ビルダー](/public/showMarkdown/page?id=batch/job-and-step-builders&lang=ja) を参照してください。
コンストラクターは `SplibBatchConfig` が登録するリスナー・例外ハンドラーの Bean を受け取るので、
それらをコンストラクター引数として宣言し、そのまま `super(...)` に渡してください。

```java
@Configuration
public class AppBatchConfig extends SplibAppParentBatchConfig {

  public AppBatchConfig(SplibJobExecutionListener jobExecutionListener,
      SplibStepExecutionListener stepExecutionListener, SplibExceptionHandler exceptionHandler) {
    super(jobExecutionListener, stepExecutionListener, exceptionHandler);
  }

  // ここに preparedJobBuilder(...) / preparedStepBuilder(...) を使った @Bean Job / Step メソッドを定義する
}
```

## 5. （任意）独自アクションで例外を処理する

未捕捉の例外がバッチ用の例外ハンドラーに到達した際に、任意の副作用（アラートメール送信など）を
実行したい場合は `SplibExceptionHandlerAction` を実装した Bean を登録してください。
[例外処理](/public/showMarkdown/page?id=batch/exception-handling&lang=ja) を参照してください。
