`ecuacion-splib-batch` は Spring Batch のジョブを構築するための ecuacion-splib モジュールです。
`ecuacion-splib-core` の上に構築されており、素の Spring Batch に対してバッチジョブに必要な要素 —
標準化された `Job`/`Step` ビルダー、実行ログ出力、共通の例外ハンドラー — を追加します。

## 提供する機能

- **標準 Job/Step ビルダー** — ジョブ設定クラスが継承する抽象クラス `SplibAppParentBatchConfig` が、
  リスナーと例外ハンドラーがあらかじめ組み込まれた `JobBuilder`/`TaskletStepBuilder` を返します。
  [Job / Step ビルダー](page?id=batch/job-and-step-builders&lang=ja) を参照してください。
- **実行ログ出力** — ジョブの開始・終了（成功／失敗）を専用ロガーに出力する Job/Step リスナーのペア。
  [ロギング](page?id=batch/logging&lang=ja) を参照してください。
- **共通の例外処理** — `SplibAppParentBatchConfig` 経由で構築されたすべての Tasklet は、
  例外が伝播する前に現在の job/step/tasklet コンテキストをログ出力する共通の `ExceptionHandler` を共有します。
  [例外処理](page?id=batch/exception-handling&lang=ja) を参照してください。
- **実行中コンテキストの追跡** — 現在実行中の job/step/tasklet を AspectJ ベースで追跡する仕組みで、
  上記のロギング・例外処理機能はこれを利用して情報を出力しています。
  [実行中コンテキストの追跡](page?id=batch/current-execution-context&lang=ja) を参照してください。

## 依存関係

`ecuacion-splib-batch` は `ecuacion-splib-core` に依存し、`spring-boot-starter-batch` と
`spring-boot-starter-aspectj`（実行中コンテキストの追跡が AspectJ の `@Aspect` として実装されているため必須）を
取り込みます。
