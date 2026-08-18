`SplibExceptionHandler` は、`SplibCliApplication#main` がアプリの `SplibCliRunner#execute` を
1回だけ呼び出す処理をラップしています — 追加の配線は不要で、Spring Boot の自動設定機構により
自ら登録されます。

`ecuacion-splib-batch` では例外ハンドラーが現在の job/step/tasklet コンテキストをログ出力します
（開発者が後からログで失敗を調査するのが前提のため）が、CLIアプリは実行している本人が
直接見ているため、デフォルトではコンソールに表示するメッセージはスタックトレースを出さず、
ユーザー向けの簡潔なものに留めています。

## `execute` が例外を投げたときの挙動

1. 例外が `ViolationException` の場合、まず簡潔なローカライズ済みの見出し
   （例：「実行中にエラーが発生しました。」）を表示し、続けて各バリデーションメッセージを
   箇条書きで表示します（フォールバックロケールで）。表示前に、同じ項目に対する必須エラー
   （`BusinessViolation`）によってマスクされている `ConstraintViolation` を除外します
   — これは `ecuacion-splib-web` 自身のエラー表示と同じルールです
   （`ecuacion-splib-ui` の `SplibViolationUtil` を参照）。それ以外の例外の場合は、
   簡潔な汎用メッセージを表示します。
2. 例外は常に `LogUtil.logSystemError` に渡されます。`ecuacion-splib-cli` 推奨のデフォルト
   logback設定（[クイックスタート](page?id=cli/quickstart&lang=ja)参照）では、
   これはどこにも出力されません——設計上、CLIアプリはアプリ側で独自の
   logger/appenderを設定しない限りログファイルを書きません（なぜこれで問題ないかは
   下記「詳細を見るには：`--verbose`」を参照）。
3. アプリケーションの `SplibExceptionHandlerAction` Bean が登録されていれば、それが呼び出されます。
   action自体が例外を投げた場合はキャッチしてログに残すのみで、元の失敗をマスクしません。
4. プロセスは非ゼロの終了コードで終了します。

## 詳細を見るには：`--verbose`

アプリを `--verbose` 付きで実行すると、上記の簡潔なメッセージに加えて、完全なスタックトレースが
`System.err` にも表示されます——logbackのレベル・appender設定とは無関係な、
`SplibExceptionHandler`内の素朴で明示的なチェックです（logback側は変わらずデフォルトで
オフのままです）。この出力を残しておきたい場合は、他のコマンドラインツールと同じように
自分でリダイレクトしてください。

```
java -jar your-app.jar --verbose 2> error-report.txt
```

これは、logbackのログレベルを実行時に動的に変える方式より意図的にシンプルにしています。
CLIアプリは実行している本人が直接見ているため、「後から誰かがログファイルで調査する」という
ユースケースのための仕組みをわざわざ作る必要がないからです。もしそれでもアプリとして
常時ログファイルを残しておきたい場合（例：社内ツールで常にログを残したい）は、
[クイックスタート](page?id=cli/quickstart&lang=ja)にある素の`<root level="OFF" />`の代わりに、
自分のlogback-spring.xmlに独自のlogger/appenderを追加してください
——上記ステップ2の`LogUtil.logSystemError`の呼び出し自体は
既に行われているので、コード変更なしにそのまま機能し始めます。

## 失敗時に独自処理を実行する

`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` を実装した Bean を
登録すると、上記ステップ3のタイミングで独自処理を実行できます
— `execute(Throwable th)` の中に必要な処理を書いてください。任意設定で、
Bean が登録されていなければステップ3は単にスキップされます。

これは `ecuacion-splib-web` や `ecuacion-splib-batch` が自身の例外処理で使っているのと
同じインターフェースです。「失敗を開発者に通知して調査してもらうか、どう通知するか」を
`ecuacion-splib-cli` 側であらかじめ決めてしまわないのは意図的な設計で、
まさにこの拡張ポイントのためにあります。例えば社内ツールであれば、
これを使ってアラートメールを送る、といった使い方ができます。実際にメールを送るのに必要な
設定は [SplibMailUtil](page?id=core/util/mail-util&lang=ja) を参照してください。

## 実際のバグなしにテストする

上記の挙動（独自の `SplibExceptionHandlerAction` を含む）を、わざと失敗するコードを書くことなく
確認するには、組み込みの
[`--ecuacion-system-error` フラグ](page?id=cli/system-error&lang=ja) を付けてアプリを実行してください。

## 配布時の注意：jarの中身は秘匿されない

`--verbose` はオプトインで、何もしなければコンソールにスタックトレースは出ません。とはいえ、
「そもそも `--verbose` でスタックトレースをそのまま見せてしまってよいのか」という疑問は
もっともです。

アプリ側で `main` メソッドが `SplibCliApplication.main` に渡す前に `args` から
`--verbose` を取り除いてしまえば、この機能自体を黙らせることはできます。
ただ、それにはあまり意味がありません。
`java -jar your-app.jar` で配布する fat jar（依存関係を1つにまとめた実行可能jar）は、
展開して各 `.class` ファイルをデコンパイルすれば、ロジックのほとんどがそのまま読める
配布形式だからです。アプリに秘匿性の高いロジック（社外秘のビジネスルールやアルゴリズムなど）が
含まれるなら、それを fat jar のままクライアントに渡している時点で、`--verbose` の有無に
関わらずリスクはすでに存在します。

秘匿性が要件になる場合は、`--verbose` を隠すのではなく、配布形式そのものを見直してください。

- GraalVM などによるネイティブイメージ化 — バイトコードをそのまま渡さずに済むため、
  解析のハードルを大きく上げられます（解析を不可能にするわけではありません）。
- クライアント・サーバー方式への変更 — 秘匿したいロジックをサーバー側に残し、
  クライアントには結果だけを返すことで、ロジック自体を配布物に含めずに済みます。

`ecuacion-splib-cli` 自体は「実行ファイルをそのまま配布して手元で動かす」利用形態を前提としており、
上記のような配布形式の選択はアプリ側の設計判断になります。
