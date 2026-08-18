`SplibJobExecutionListener` と `SplibStepExecutionListener` は、ジョブ設定クラスが
`SplibAppParentBatchConfig` を継承していれば
[`preparedJobBuilder`/`preparedStepBuilder`](page?id=batch/job-and-step-builders&lang=ja)
によって自動的にアタッチされます。追加の配線は不要です。

## `SplibJobExecutionListener`

`"summary-logger"` という専用ロガーにログを出力します（ジョブの開始・終了ログを通常のアプリケーションログとは別の出力先に振り分けたい場合は、このロガーを個別に設定してください）。

- `beforeJob` — `START: job-name: <name>` を出力する。
- `afterJob` — 成功時は `END  : job-name: <name> [NORMAL END]`、失敗時は
  `END  : job-name: <name> [ABNORMAL END] exit status: <exitCode>`（`ERROR` レベル）を出力する。

また、実行中のジョブ名を
[`SplibBatchAdvice`](page?id=batch/current-execution-context&lang=ja)
経由で記録するため、途中で失敗が発生した際に例外ハンドラーがそのジョブ名を参照できるようになります。

## `SplibStepExecutionListener`

同じ仕組みを使って、`beforeStep` で実行中のステップ名を記録します。それ自体では何もログ出力しません。
ステップレベルの詳細は、ステップ単位のサマリー行としてではなく、
[例外処理](page?id=batch/exception-handling&lang=ja) で説明する例外処理の出力の一部として現れます。
