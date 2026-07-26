`SplibBatchAdvice` は、各スレッドで現在実行中の job・step・tasklet-or-chunk の名前を追跡します。
これにより、
[ロギング](page?id=batch/logging&lang=ja) と
[例外処理](page?id=batch/exception-handling&lang=ja) は、
バッチのどこでログ行や失敗が発生したかを、それぞれのクラスに明示的に伝えることなく報告できます。
通常は自分で呼び出す必要はなく、ジョブの実行に伴って自動的に値が設定されます。

## 名前が記録される仕組み

3 つの名前はそれぞれ独立した `ThreadLocal<String>` に保持され、異なる仕組みによって設定されます。

| 名前 | 設定元 |
| --- | --- |
| ジョブ名 | `SplibJobExecutionListener.beforeJob` |
| ステップ名 | `SplibStepExecutionListener.beforeStep` |
| Tasklet-or-chunk 名 | `Tasklet.execute(..)`（および、下記の通り確実とは言えないものの `Chunk.execute(..)`）に対する AspectJ の `@Before` アドバイス |

Job/Step リスナーは
[`preparedJobBuilder`/`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=ja)
によって自動的にアタッチされます。tasklet-or-chunk のアドバイスは、アプリケーション全体でこれらのメソッドが
呼ばれるたびに実行されます。これが `ecuacion-splib-batch` が `spring-boot-starter-aspectj` に依存している理由です。

## 値の取得方法

```java
String job = SplibBatchAdvice.getCurrentJob();
String step = SplibBatchAdvice.getCurrentStep();
String taskletOrChunk = SplibBatchAdvice.getCurrentTaskletOrChunk();
```

これらは、Tasklet が例外を throw した際に
[例外処理](page?id=batch/exception-handling&lang=ja) がログ出力するのと同じ値です。
それぞれ単純な `ThreadLocal` であるため、あるスレッドで設定した値は別のスレッドからは参照できません。
ジョブが処理を別のスレッドプールに委譲する場合（ステップ自身のスレッドだけで完結しない場合）は注意してください。

## chunk 指向ステップに関する注意事項

`Chunk.execute(..)` に対するアドバイスはソースコード上には存在しますが、
その実装コメント自身が chunk 指向処理に対しては「未確定（not concreted）」と注記しています。
Spring Batch の chunk モデルを使うステップ（単純な `Tasklet` ではなく）では、
`SplibBatchAdvice.getCurrentTaskletOrChunk()` が期待通りの値を返すかどうかを、
依存する前に確認してください。
