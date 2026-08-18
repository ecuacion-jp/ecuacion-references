`ecuacion-splib-cli` は、コマンドライン（CUI）アプリケーション — ユーザーが直接実行し、
対話的に結果を見るプログラム — を構築するための ecuacion-splib モジュールです。
スケジューラーが無人でトリガーし、失敗した場合は開発者が後からログで調査する
`ecuacion-splib-batch` とは対照的な位置づけです。

## 提供する機能

- **最小限の Spring Boot 起動処理** — `SplibCliApplication` が Spring コンテキストを起動し、
  アプリの `SplibCliRunner` Bean を1回だけ実行して、適切なプロセス終了コードで終了します。
  `ecuacion-splib-batch` と異なり、Job/Step/JobRepository の仕組みは一切ありません。
- **単一のエントリーポイント契約** — `SplibCliRunner`。アプリはメソッド1つ
  （`execute(String[] args)`）を実装するだけです。
  [クイックスタート](page?id=cli/quickstart&lang=ja) を参照してください。
- **`execute`実行中の「実行中です...」インジケーター** — コンソールにアニメーションする
  ローカライズ済みステータス行を表示し、`execute`が終わると自動的に消えます。
  出力が対話的なターミナルでない場合（ファイルへのリダイレクト等）は完全にスキップされるため、
  非対話的な出力を汚しません。
- **コンソール中心の例外処理** — `SplibExceptionHandler` は自動登録されます
  （アプリ側で `@ComponentScan` を書く必要はありません）。ユーザー向けの短いローカライズ済み
  メッセージを表示します。詳細は常に `LogUtil.logSystemError` に渡されますが、デフォルトでは
  どこにも出力されません（コンソールを静かに保つデフォルト設定については
  [クイックスタート](page?id=cli/quickstart&lang=ja)を参照）——アプリが実際の
  logger/appenderを設定した瞬間にコストゼロで動き始めます。
  [例外処理](page?id=cli/exception-handling&lang=ja) を参照してください。

## 依存関係

`ecuacion-splib-cli` は `ecuacion-splib-core` と `ecuacion-splib-ui`（必須項目の
バリデーションエラーによる他エラーのマスキングなど、UI表示に関わる共通ロジックを置くモジュール。
[例外処理](page?id=cli/exception-handling&lang=ja) で使用）に依存し、
`spring-boot-starter` を取り込みます。
