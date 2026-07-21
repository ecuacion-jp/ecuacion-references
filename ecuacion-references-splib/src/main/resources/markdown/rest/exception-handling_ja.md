`SplibRestExceptionHandler`（`@RestControllerAdvice`、Spring の `ResponseEntityExceptionHandler` を継承）は、
`SplibRestConfig` をインポートすると自動的に登録されます（
[セットアップ](/public/showMarkdown/page?id=rest/setup&lang=ja) 参照）。次の 2 つのケースを処理します。

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
2. アプリケーションが `SplibExceptionHandlerAction` の Bean を登録していれば、それに渡される（下記参照）。
3. HTTP ステータス `501`、メッセージ `"Internal Server Error..."` の `ErrorResponse` に変換される。

## 未捕捉の例外発生時に独自処理を実行する

`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` を実装した Bean を登録すると、
上記手順 2 のタイミングでアラートメール送信や監視システムへの通知といった独自処理を実行できます。
任意設定のため、Bean を登録しなければ手順 2 は単にスキップされます。

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  @Override
  public void execute(@Nullable Throwable th) {
    // 例: MailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
```

これは `ecuacion-splib-web` が自身の（HTML 向け）例外ハンドラーで使っているものと同じ拡張ポイントのため、
Web と REST の両方のフロントエンドを持つアプリケーションでは 1 つの実装を共有できます。
