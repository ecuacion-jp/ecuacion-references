# ecuacion-tools reference

こちらは ecuacion-tools（モジュール名：`ecuacion-tool-xxx`）のリファレンスページです。
サーバ運用で役立つバッチ・API ツール群の使い方をまとめています。

## ecuacion-tools とは

ecuacion-tools は、アプリケーションの運用・保守で必要となる定型作業を自動化するためのツール群です。
各ツールは独立した JAR または WAR として提供され、既存システムへの組み込みは不要です。

## モジュール構成

| モジュール名 | 役割 |
| --- | --- |
| `ecuacion-tool-housekeep-files` | ファイルおよびディレクトリの定型管理（コピー・移動・削除・圧縮・SFTP 転送など） |
| `ecuacion-tool-housekeep-db` | データベースレコードの定期削除（ソフトデリート・ハードデリート） |
| `ecuacion-tool-command-api` | Web API 経由でサーバ上のシェルスクリプトを実行 |

詳細はナビゲーションの各ページを参照してください。
