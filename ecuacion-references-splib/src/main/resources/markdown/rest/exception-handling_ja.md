`SplibRestExceptionHandler`（`@RestControllerAdvice`、Spring の `ResponseEntityExceptionHandler` を継承）は、
`SplibRestConfig` をインポートすると自動的に登録されます（
[クイックスタート](page?id=rest/quickstart&lang=ja) 参照）。次の 2 つのケースを処理します。

## `HttpStatusException`

`jp.ecuacion.splib.rest.exception.HttpStatusException` を throw すると、ボディなしで指定した HTTP ステータスを
返せます。

```java
throw new HttpStatusException(HttpStatus.NOT_FOUND);
```

これは `ResponseEntity.status(exception.getHttpStatus()).build()` にそのまま変換されます。

## それ以外の未捕捉の `Throwable`

ハンドラーに到達したそれ以外の例外は、以下の順に処理されます。

1. `LogUtil.logSystemError` でログ出力される。
2. アプリケーションが `SplibRestExceptionHandlerAction` の Bean を登録していれば、それに渡される（下記参照）。
3. HTTP ステータス `501`、メッセージ `"Internal Server Error..."` の `ErrorResponse` に変換される。

## 未捕捉の例外発生時に独自処理を実行する

`jp.ecuacion.splib.core.exceptionhandler.SplibRestExceptionHandlerAction` を実装した Bean を登録すると、
上記手順 2 のタイミングで独自処理を実行できます。処理内容は `execute(Throwable th)` に自由に記述してください。
任意設定のため、Bean を登録しなければ手順 2 は単にスキップされます。

例えば、アラートメールで stack trace を送信したい場合は次のように書けます。

```java
@Component
public class AppExceptionHandlerAction implements SplibRestExceptionHandlerAction {

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

`ecuacion-splib-web` や `ecuacion-splib-batch` にも同じ拡張ポイントがあり、どちらも
`SplibExceptionHandlerAction` を使います。バッチアプリは常にそれ単体の独立したプロセスとして
動くため、web と区別する必要がありません。例外は REST で、web フロントエンドと同じプロセスで
動くことが多く、フロントエンドごとに挙動を変えたい場合があるため、専用の
`SplibRestExceptionHandlerAction` を持っています。
