# housekeep-files 概要

`ecuacion-tool-housekeep-files` は、ファイルおよびディレクトリに対する定型的な管理処理（ハウスキーピング）を
Excel 設定ファイルに基づいて自動実行するバッチツールです。

## 主な特徴

- コピー・移動・削除・圧縮・解凍など幅広いファイル操作に対応
- SFTP によるリモートサーバとのファイル転送に対応
- 操作対象パスのワイルドカード指定に対応
- パス変数（`${VAR_NAME}` 形式）による柔軟なパス管理
- 経過時間によるフィルタリング（指定日数以前のファイルのみ操作）
- ソース未存在・転送先衝突時の動作を `IGNORE` / `WARN` / `ERROR` で選択可能

## ツールの仕組み

設定は Excel ファイル 1 枚で管理します。ツール起動時にその Excel ファイルのパスを引数として渡すと、
タスク設定シートに記載されたタスクが上から順番に実行されます。

```
java -jar ecuacion-tool-housekeep-files-x.x.x.jar excelPath=/path/to/settings.xlsx
```

## モジュール取得先

JAR ファイルは以下から取得できます。

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-housekeep-files/
```

## ドキュメント

- [セットアップ](/public/showMarkdown/page?id=housekeep-files/setup&lang=ja)
- [クイックスタート](/public/showMarkdown/page?id=housekeep-files/quickstart&lang=ja)
- [Excel設定ファイル](/public/showMarkdown/page?id=housekeep-files/excel-settings&lang=ja)
- [処理パターン](/public/showMarkdown/page?id=housekeep-files/task-patterns&lang=ja)
