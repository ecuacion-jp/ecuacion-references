`ecuacion-splib-cli` は、コマンドライン（CUI）アプリケーション — ユーザーが直接実行し、
対話的に結果を見るプログラム — を構築するための ecuacion-splib モジュールです。
スケジューラーが無人でトリガーし、失敗した場合は開発者が後からログで調査する
`ecuacion-splib-batch` とは対照的な位置づけです。

## 提供する機能

| 機能 | 説明 |
| --- | --- |
| 最小限の Spring Boot 起動処理 | `SplibCliApplication` が Spring コンテキストを起動し、アプリの `SplibCliRunner` Bean を1回だけ実行して、<br>適切なプロセス終了コードで終了します。<br>`ecuacion-splib-batch` と異なり、Job/Step/JobRepository の仕組みは一切ありません。 |
| 単一のエントリーポイント契約 | アプリは `SplibCliRunner` にメソッド1つ（`execute(String[] args)`）を実装するだけです。<br>[クイックスタート](page?id=cli/quickstart&lang=ja) を参照してください。 |
| `execute`実行中の「実行中です...」インジケーター | コンソールにアニメーションするローカライズ済みステータス行を表示し、`execute`が終わると自動的に消えます。<br>出力が対話的なターミナルでない場合（ファイルへのリダイレクト等）は完全にスキップされるため、<br>非対話的な出力を汚しません。 |
| コンソール中心の例外処理 | `SplibExceptionHandler` は自動登録されます（アプリ側で `@ComponentScan` を書く必要はありません）。<br>ユーザー向けの短いローカライズ済みメッセージを表示します。<br>[例外処理](page?id=cli/exception-handling&lang=ja) を参照してください。 |

## 依存関係

`ecuacion-splib-cli` は `ecuacion-splib-core` と `ecuacion-splib-ui`（必須項目のバリデーションエラーによる他エラーのマスキングなど、UI表示に関わる共通ロジックを置くモジュール。
[例外処理](page?id=cli/exception-handling&lang=ja) で使用）に依存し、
`spring-boot-starter` を取り込みます。
