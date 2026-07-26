`SplibExceptionHandler` は Spring Batch の `ExceptionHandler` を実装しており、
[`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=ja)
で構築されたすべてのステップにアタッチされます。ジョブ設定クラスが `SplibAppParentBatchConfig` を
継承していれば、追加の配線は不要です。

`ecuacion-splib-web` や `ecuacion-splib-rest` ではそれぞれの例外ハンドラーの利用が任意であるのに対し、
バッチアプリでは常にこの一本の経路を通ります。Web レスポンスの形式のようなアプリごとのバリエーションが
バッチには不要なためです。

## Tasklet が例外を throw したときの挙動

1. 現在の job / step / tasklet-or-chunk 名が
   [`SplibBatchAdvice`](page?id=batch/current-execution-context&lang=ja)
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

`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` を実装した Bean を
登録すると、上記手順 3 のタイミングで独自処理を実行できます。処理内容は
`execute(Throwable th)` に自由に記述してください。
任意設定のため、Bean を登録しなければ手順 3 は単にスキップされます。

例えば、アラートメールで stack trace を送信したい場合は次のように書けます。

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  private final SplibMailUtil mailUtil;

  public AppExceptionHandlerAction(SplibMailUtil mailUtil) {
    this.mailUtil = mailUtil;
  }

  @Override
  public void execute(@Nullable Throwable th) {
    mailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
```

メール送信はあくまで一例です。実際にメールを送信する場合に必要な設定（`spring.mail.*`・
`jp.ecuacion.splib.mail.*`）は [SplibMailUtil](page?id=core/util/mail-util&lang=ja) を参照してください。
未設定の場合、上記の呼び出しは黙ってスキップされます。

これは `ecuacion-splib-web` が自身の例外ハンドラーで使っているものと同じインターフェースです。
バッチアプリは常にそれ単体の独立したプロセスとして動く（web/REST アプリと同一プロセスで
併用されることがない）ため、区別する必要がなく、どちらも単純に
`SplibExceptionHandlerAction` を共有します。例外は `ecuacion-splib-rest` で、
REST フロントエンドは web フロントエンドと同じプロセスで動くことが多いため、
専用の `SplibRestExceptionHandlerAction` を持っています。
