`ecuacion-tool-housekeep-files` は、ファイルおよびディレクトリに対する定型的な管理処理（ハウスキーピング）を
Excel 設定ファイルに基づいて自動実行するバッチツールです。

## 主な特徴

- コピー・移動・削除・圧縮・解凍など幅広いファイル操作に対応
- SFTP によるリモートサーバとのファイル転送に対応
- 操作対象パスのワイルドカード指定に対応
- パス変数（`${VAR_NAME}` 形式、`application.properties` 等で定義）による柔軟なパス管理
- 経過時間によるフィルタリング（指定日数以前のファイルのみ操作）
- ソース未存在・転送先衝突時の動作を `IGNORE` / `WARN` / `ERROR` で選択可能

## ツールの仕組み

設定は Excel ファイル 1 枚で管理します。そのパスを `application.properties` に設定してください（[設定ファイル](page?id=housekeep-files/config&lang=ja)を参照）。ツール起動時に、タスク設定 シートに記載されたタスクが上から順番に実行されます。

Excel ファイルには以下の 2 つのシートがあります。

| シート名 | 役割 |
| --- | --- |
| [タスク設定](page?id=housekeep-files/excel-settings&lang=ja#タスク設定シート) | 実行するファイル操作タスクの一覧 |
| [サーバ認証設定](page?id=housekeep-files/excel-settings&lang=ja#サーバ認証設定シート) | SFTP 接続先サーバの認証情報 |
