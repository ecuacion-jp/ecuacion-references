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
- **Spring Bootではなく専用の起動バナー** — コンパクトなブランドマーク
  （ecuacion-splibのバージョン、任意でアプリ自身の名前とバージョンも表示可能）が、
  Spring BootのASCIIアート調バナーと起動ログ行を置き換えます。コンソールは最初の行から
  静かで意図的な状態を保てます。[クイックスタート](page?id=cli/quickstart&lang=ja) を参照してください。
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

## 命名についての補足：`Command` ではなく `Runner`

`SplibCliRunner` という名前は、`SplibCliCommand` ではなく、Spring Boot 自身の
`CommandLineRunner`/`ApplicationRunner`（「起動後に1回だけ動く」）に寄せた意図的な選択です。
これは、将来 interactive・REPL 形式の CLI エントリーポイントを追加する可能性を見込んでのもので、
その世界では "Command" は「read-eval-print ループの中で処理される、複数あるコマンドの1つ」を
自然に意味する語になります — これは、今回のような「アプリ全体を1回だけ実行するエントリーポイント」
とは異なる概念です。interactive モードは現時点では存在せず、本モジュールが現在サポートするのは
「1回実行して終了する」形式の CLI アプリのみです。

## 依存関係

`ecuacion-splib-cli` は `ecuacion-splib-core` と `ecuacion-splib-ui`（必須項目の
バリデーションエラーによる他エラーのマスキングなど、UI表示に関わる共通ロジックを置くモジュール。
[例外処理](page?id=cli/exception-handling&lang=ja) で使用）に依存し、
`spring-boot-starter` を取り込みます。
