`SplibExceptionHandler` は Spring Batch の `ExceptionHandler` を実装しており、
[`preparedStepBuilder`](/public/showMarkdown/page?id=batch/job-and-step-builders&lang=ja)
で構築されたすべてのステップにアタッチされます。ジョブ設定クラスが `SplibAppParentBatchConfig` を
継承していれば、追加の配線は不要です。

`ecuacion-splib-web` や `ecuacion-splib-rest` ではそれぞれの例外ハンドラーの利用が任意であるのに対し、
バッチアプリでは常にこの一本の経路を通ります。Web レスポンスの形式のようなアプリごとのバリエーションが
バッチには不要なためです。

## Tasklet が例外を throw したときの挙動

1. 現在の job / step / tasklet-or-chunk 名が
   [`SplibBatchAdvice`](/public/showMarkdown/page?id=batch/current-execution-context&lang=ja)
   から取得され `INFO` レベルでログ出力されます（例：
   `job: importJob, step: importStep, tasklet or chunk: ImportTasklet`）。まだ取得できていない項目
   （該当する advice が実行される前に失敗が発生した場合）は、空欄ではなくその旨が出力されます。
2. 例外自体が `LogUtil.logSystemError` でログ出力されます。
3. アプリケーションが `SplibExceptionHandlerAction` の Bean を登録していれば呼び出されます。
   このアクション自体から例外が投げられた場合はキャッチしてログ出力するのみとし、
   元の失敗をマスクしないようにしています。
4. 例外が `ViolationException` の場合、それが保持するすべての違反メッセージを
   （フォールバックロケールで）`==========` マーカーの間に個別にログ出力します。
   例外自体は 1 つしか投げられませんが、ログ上ではすべてのメッセージを確認できます。
5. 元の例外を再 throw します。このハンドラーはあくまで観測・報告のみを行い、
   失敗を握りつぶしたりジョブの結果を変えたりすることはありません。

## 失敗時に独自処理を実行する

`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` を実装した Bean を登録すると、
上記手順 3 のタイミングでアラートメール送信や監視システムへの通知といった独自処理を実行できます。
任意設定のため、Bean を登録しなければ手順 3 は単にスキップされます。

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  @Override
  public void execute(@Nullable Throwable th) {
    // 例: MailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
```

これは `ecuacion-splib-web` や `ecuacion-splib-rest` が自身の例外ハンドラーで使っているものと同じ拡張ポイントのため、
バッチジョブと REST API など複数のフロントエンドを持つアプリケーションでは 1 つの実装を共有できます。
