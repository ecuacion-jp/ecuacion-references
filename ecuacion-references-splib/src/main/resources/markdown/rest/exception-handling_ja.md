`SplibRestExceptionHandler`（`@RestControllerAdvice`、Spring の `ResponseEntityExceptionHandler` を継承）は、
`SplibRestConfig` をインポートすると自動的に登録されます（
[クイックスタート](page?id=rest/quickstart&lang=ja) 参照）。次の 3 種類の例外を処理し、
それぞれ想定する読み手（オーディエンス）が異なるため、意図的に扱いを変えています。

## `ViolationException`

`jp.ecuacion.lib.core.exception.ViolationException` は、業務/バリデーション上のエラーで、
そのメッセージを実際の人間のエンドユーザーに届けたい場合（例えば、ローカル/デスクトップアプリがユーザーの代わりにこの API を呼び出し、失敗内容をそのユーザーに表示するようなケース）に throw します。

```java
throw new ViolationException(...);
```

例外が保持するすべての違反（最初の 1 件だけでなく）がレスポンスに含まれ、各メッセージはリクエストのロケールに合わせてローカライズされ、ステータスは常に `400 Bad Request` になります（呼び出し側はステータスで分岐する想定がなく、テキストを表示するだけなので、複数種類のステータスは不要です）。レスポンスボディは `ViolationsResponse` で、ローカライズ済みメッセージを保持する
`messages` フィールドを持ちます。

## `ResponseStatusException`（および `ResponseEntityExceptionHandler` の組み込み処理が扱うその他の例外）

Spring 自身の `org.springframework.web.server.ResponseStatusException` は、メッセージを人間のエンドユーザーではなく、API 呼び出し元の開発者/システム（例えば、このAPIをプログラムから呼び出すサーバー）に向けたい失敗の場合に throw します。

```java
throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...");
```

メッセージはそのまま（ローカライズされずに）使われ、throw する側のコードが状況に応じたステータス（任意の `4xx`/`5xx`）を選びます（呼び出し側はステータスで分岐する想定です）。これには
`ResponseEntityExceptionHandler` の組み込み処理が扱うその他の例外（`MethodArgumentNotValidException`・
`HttpMessageNotReadableException` など）も含まれます。

## それ以外の未捕捉の `Throwable`

本当に想定外の例外（報告されたエラーではなく、バグ）は、以下の順に処理されます。

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
`SplibExceptionHandlerAction` を使います。バッチアプリは常にそれ単体の独立したプロセスとして動くため、web と区別する必要がありません。例外は REST で、web フロントエンドと同じプロセスで動くことが多く、フロントエンドごとに挙動を変えたい場合があるため、専用の
`SplibRestExceptionHandlerAction` を持っています。
