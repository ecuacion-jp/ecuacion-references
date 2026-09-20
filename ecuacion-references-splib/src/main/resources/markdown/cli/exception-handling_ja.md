`SplibExceptionHandler` は、`SplibCliApplication#main` がアプリの `SplibCliRunner#execute` を1回だけ呼び出す処理をラップしています — 追加の配線は不要で、Spring Boot の自動設定機構により自ら登録されます。

`ecuacion-splib-batch` では例外ハンドラーが現在の job/step/tasklet コンテキストをログ出力します（開発者が後からログで失敗を調査するのが前提のため）が、CLIアプリは実行している本人が直接見ているため、デフォルトではコンソールに表示するメッセージはスタックトレースを出さず、
ユーザー向けの簡潔なものに留めています。

## アプリから例外を投げる

ユーザー向けのローカライズ済みメッセージを表示したい場合は、`jp.ecuacion.lib.core.exception.ViolationException` をthrowしてください。詳細は`ecuacion-lib`を参照してください。

## 終了コード

`execute` が例外を投げると、プロセスは終了コード `1` で終了します。

`execute`の中などアプリ自身のコードから直接`System.exit(n)`を呼べば、`1`以外の終了コードにもできます。ただしその場合、`SplibCliApplication`側の後処理（完了メッセージの表示や`SplibExceptionHandler`によるハンドリングなど）は一切行われず、その場でプロセスが強制終了します。

## 失敗時に独自処理を実行する

`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` を実装した Bean を登録すると、`execute` が例外を投げて失敗した時に、独自処理を実行できます——`execute(Throwable th)` の中に必要な処理を書くだけです。任意設定で、Bean が登録されていなければ何も呼ばれません（action自体が例外を投げた場合はキャッチしてログに残すだけで、元の失敗をマスクしません）。

これは `ecuacion-splib-web` や `ecuacion-splib-batch` が自身の例外処理で使っているのと同じインターフェースです。

例えば社内ツールなら、失敗時にアラートメールを飛ばすのが典型的な使い方です。[`SplibMailUtil`](page?id=core/util/mail-util&lang=ja) を DI で受け取り、`sendErrorMail` を呼ぶだけで実装できます。

```java
@Component
public class AppExceptionHandlerAction implements SplibExceptionHandlerAction {

  private final SplibMailUtil mailUtil;

  public AppExceptionHandlerAction(SplibMailUtil mailUtil) {
    this.mailUtil = mailUtil;
  }

  @Override
  public void execute(Throwable th) {
    mailUtil.sendErrorMail(th);
  }
}
```

これで `execute` が投げた例外は毎回このBeanに渡り、管理者へのメール通知が自動的に行われます——実際にメールを送るのに必要な`application.properties`設定は[`SplibMailUtil`](page?id=core/util/mail-util&lang=ja) を参照してください。

## 実際のバグなしに例外処理の挙動をテストする

上記の挙動（独自の `SplibExceptionHandlerAction` を含む）を、わざと失敗するコードを書くことなく確認するには、組み込みの
[`--ecuacion-system-error` フラグ](page?id=cli/system-error&lang=ja) を付けてアプリを実行してください。
