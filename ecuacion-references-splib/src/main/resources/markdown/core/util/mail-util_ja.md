`SplibMailUtil`（`jp.ecuacion.splib.core.util.SplibMailUtil`）は、Spring Boot 標準の `spring.mail.*`
設定でメールを送信するユーティリティです。`@Component` として登録されているので、アプリケーション側では
DI で受け取って使います。

## 使い方

現状、公開メソッドは `sendErrorMail(Throwable)` のみです。システムエラー発生時の管理者通知に特化しています。

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

`ecuacion-splib-rest` の [例外処理](page?id=rest/exception-handling&lang=ja) における
`SplibRestExceptionHandlerAction` の実装例です。`ecuacion-splib-batch` の
[例外処理](page?id=batch/exception-handling&lang=ja) にある実装例も `implements` の型が違うだけで同じ形になりますが、そちらは `ecuacion-splib-web` と同じ `SplibExceptionHandlerAction`
を使います。REST だけが専用のインターフェースを持っています。

## 未設定時の挙動

`spring.mail.host`・`spring.mail.username`・`spring.mail.password`・
`jp.ecuacion.splib.mail.address-csv-on-system-error` のいずれかが未設定の場合、例外を投げずに黙ってスキップされます（`INFO` レベルでログには記録されます）。開発環境でメール設定をしていなくても、
このメソッドを呼ぶ経路（例外処理）自体は問題なく動きます。

## application.properties の設定

### SMTP 接続（`spring.mail.*`）

| プロパティ | 説明 | デフォルト |
| --- | --- | --- |
| `spring.mail.host` | SMTP サーバーのホスト名 | なし（未設定なら送信をスキップ） |
| `spring.mail.port` | SMTP ポート番号 | `587` |
| `spring.mail.username` | 送信元アドレス（SMTP ログインユーザー名） | なし（未設定なら送信をスキップ） |
| `spring.mail.password` | SMTP パスワード（Gmail の場合はアプリパスワード） | なし（未設定なら送信をスキップ） |
| `spring.mail.properties.mail.smtp.auth` | SMTP 認証の有無 | `true` |
| `spring.mail.properties.mail.smtp.ssl.enable` | SSL（ポート465）を使う場合は `true`。STARTTLS（ポート587）の場合は `false`（STARTTLS 自体はこの場合自動的に有効になります） | `false` |

### アプリ設定（`jp.ecuacion.splib.mail.*`）

| プロパティ | 説明 | デフォルト |
| --- | --- | --- |
| `jp.ecuacion.splib.mail.address-csv-on-system-error` | `sendErrorMail` の送信先（カンマ区切りで複数可） | なし（未設定なら送信をスキップ） |
| `jp.ecuacion.splib.mail.title-prefix` | メール件名の先頭に付与する文字列（例: 環境名） | `""` |
| `jp.ecuacion.splib.mail.smtp.starttls-required` | STARTTLS（ポート587）時、サーバーが非対応なら平文フォールバックせず接続自体を失敗させるか ※1 | `true` |
| `jp.ecuacion.splib.mail.smtp.bounce-address` | バウンスメールの受信アドレス | 未設定 |
| `jp.ecuacion.splib.mail.debug` | JavaMail のデバッグログ出力 | `false` |

※1 `false` に設定するとセキュリティリスクがあります。サーバーが STARTTLS に対応していない場合、
SMTP 認証（パスワードを含む）が平文で送信されてしまいます。STARTTLS 非対応と分かっているサーバー（ローカルのテスト用リレーなど）向けにのみ `false` を設定し、本番環境では使用しないでください。

## 設定例（ポート 587 / STARTTLS、Gmail）

> Gmail の SMTP を使うには、Google アカウントで 2 段階認証を有効にし、
> [アプリパスワード](https://myaccount.google.com/apppasswords) を発行してください。

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-account@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=false

jp.ecuacion.splib.mail.title-prefix=[MyApp: staging]
jp.ecuacion.splib.mail.address-csv-on-system-error=admin@example.com
```
