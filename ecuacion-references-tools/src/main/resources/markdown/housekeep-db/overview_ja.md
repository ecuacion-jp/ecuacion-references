`ecuacion-tool-housekeep-db` は、データベース上のレコードを自動的にハウスキーピング（整理・削除）するバッチツールです。
Excel 設定ファイルに基づいて、soft delete（論理削除）または hard delete（物理削除）を実行します。

## 主な特徴

- **Hard delete（物理削除）**: 条件に合致するレコードを削除
- **Soft delete（論理削除）**: レコードは削除せず、削除フラグ列を `true` に更新
- **経過日数によるフィルタリング**: タイムスタンプ列の値が指定日数以上経過したレコードのみを対象
- **関連テーブルの連動処理**: 関連テーブルのレコードを同時に削除、または関連レコードが存在する場合に削除をスキップ
- **検索条件の追加**: 列の値による絞り込み条件を追加可能

## 制約

- テーブルは単一列の主キーまたはユニークインデックス（ID 列）を持つ必要があります（複合主キーは非対応）
- soft delete の削除フラグ列のデータ型は `bool` である必要があります（`true` で削除済みを表す）
- 対応データベースは **PostgreSQL** および **MySQL / MariaDB**

## ツールの仕組み

設定は Excel ファイル 1 枚で管理します。そのパスを `application.properties` に設定してください（[設定ファイル](page?id=housekeep-db/config&lang=ja)を参照）。ツール起動時に、housekeep DB設定 シートに記載されたタスクが上から順番に実行されます。

Excel ファイルには以下の 4 つのシートがあります。

| シート名 | 役割 |
| --- | --- |
| [DB接続設定](page?id=housekeep-db/excel-settings/db-connection-settings&lang=ja) | データベース接続情報 |
| [housekeep DB設定](page?id=housekeep-db/excel-settings/housekeep-db-settings&lang=ja) | ハウスキーピングの実行タスク |
| [関連テーブル処理設定](page?id=housekeep-db/excel-settings/related-table-settings&lang=ja) | 関連テーブルの連動処理設定 |
| [データ検索条件設定](page?id=housekeep-db/excel-settings/search-condition-settings&lang=ja) | 追加の検索条件 |
