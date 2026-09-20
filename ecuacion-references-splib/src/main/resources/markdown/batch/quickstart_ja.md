[セットアップ](page?id=batch/setup&lang=ja) で依存関係を追加済みであることを前提に、
このページでは必要な設定クラスを揃え、実際に動く最小のジョブを1つ追加します。

## 1. `SplibBatchConfig` を有効化する

`SplibBatchConfig` は `ecuacion-splib-batch` の動作に必要なパッケージ（`jp.ecuacion.splib.core.config`・`jp.ecuacion.splib.batch.advice`・`jp.ecuacion.splib.batch.listener`・
`jp.ecuacion.splib.batch.exceptionhandler`）をコンポーネントスキャンします。
アプリケーションの設定クラスからインポートしてください。

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.batch.config")
public class AppConfig {
}
```

## 2. アプリケーションの main クラスを書く

`SplibBatchApplication` は、すべてのバッチアプリに必要な `main` メソッドのロジック（`SpringApplication` を実行し、その結果コードで終了する）を提供していますが、
`java` コマンドは親クラスから継承した `main` メソッドを実行できないため、
これ自体を起動対象のクラスにすることはできません。そのため、アプリケーション側で自身の `main` メソッドを持つクラスを用意し、単純に処理を委譲してください。

```java
@SpringBootApplication
public class BatchApplication {

  public static void main(String[] args) {
    SplibBatchApplication.main(BatchApplication.class, args);
  }
}
```

## 3. `SplibAppParentBatchConfig` を継承してジョブを定義する

`SplibAppParentBatchConfig` は抽象クラスで、ジョブ設定クラスに対してあらかじめ組み込み済みの `JobBuilder`/`TaskletStepBuilder` ファクトリメソッドを提供します。
コンストラクターは `SplibBatchConfig` が登録するリスナー・例外ハンドラーの Bean を受け取るので、
それらをコンストラクター引数として宣言し、そのまま `super(...)` に渡してください。

実行する `Tasklet`：

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

それを1ステップだけのジョブとして組み立てるジョブ設定クラス。`JobBuilder`/`StepBuilder` を直接生成する代わりに `preparedJobBuilder`/`preparedStepBuilder` を使います。

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

## 4. 実行してみる

Spring Boot のバッチ自動設定は、アプリケーション起動時にコンテキスト上に存在するすべての
`Job` Bean を実行するため、アプリケーションを起動するだけで動きます。別途トリガーは不要です。

```
mvn spring-boot:run
```

```
START: job-name: helloJob
Hello, world!
END  : job-name: helloJob [NORMAL END]
```

`START`/`END` の行は `preparedJobBuilder` によって組み込まれるジョブリスナーによる出力です。
[ロギング](page?id=batch/logging&lang=ja) を参照してください。

続きとして、[Job / Step ビルダー](page?id=batch/job-and-step-builders&lang=ja)
では `preparedJobBuilder`/`preparedStepBuilder` が何を設定しているかをより詳しく扱っています。
[例外処理](page?id=batch/exception-handling&lang=ja) では Tasklet が例外を投げたときの挙動と、
未捕捉の例外発生時に独自処理（アラートメール送信など）を実行する方法を扱っています。
任意設定で、ジョブを動かすだけなら不要です。
[実行中コンテキストの追跡](page?id=batch/current-execution-context&lang=ja)
では、上記2つの機能が利用している job/step/tasklet の追跡の仕組みを説明しています。
