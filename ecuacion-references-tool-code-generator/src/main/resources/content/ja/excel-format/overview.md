# DB項目定義書（Excel）概要

DB項目定義書は `ecuacion-tool-code-generator` の入力ファイルです。
Excel ファイル（`.xlsx`）1 枚でプロジェクトのデータモデルを定義します。

## ファイル名

```
DB項目定義書(fmt-v4.10.0)_<プロジェクト名>.xlsx
```

## シート構成

| シート名 | 役割 | 詳細 |
| --- | --- | --- |
| 各種設定 | プロジェクト全体の設定（パッケージ名など） | [各種設定シート](/public/ja/article?id=excel-format/general-settings) |
| dataType定義 | フィールドの型定義（`DT_XXXX` 形式） | [dataType定義シート](/public/ja/article?id=excel-format/data-type-sheet) |
| DB項目定義 | テーブルとカラムの定義 | [DB項目定義シート](/public/ja/article?id=excel-format/db-definition-sheet) |
| enum定義 | ENUM 型の値定義 | [enum定義シート](/public/ja/article?id=excel-format/enum-sheet) |

## 新規プロジェクトでの作成手順

1. **既存の Excel ファイルをコピーしてリネーム**
   - `ecuacion-tool-code-generator-batch/ecuacion-tool-code-generator-excel-format/` にあるサンプルを使用
   - `DB項目定義書(fmt-v4.10.0)_<新プロジェクト名>.xlsx` にリネーム

2. **「各種設定」シートを修正**（最優先）
   - `SYSTEM_NAME`（行 8）: 新プロジェクト名
   - `BASE_PACKAGE`（行 9）: 新しい Java パッケージ
   - `TABLE_NAMES_WITHOUT_GROUPING`（行 31）: グループフィルタなしのテーブル一覧

3. **「dataType定義」シートにプロジェクト固有の型を追加**
   - 標準組み込み DataType はすでに含まれているので追加分のみ記載

4. **「DB項目定義」シートにテーブル定義を追加**
   - `splib` 認証用テーブル（ACC, ACC_ADMIN 等）は既存のまま残す
   - 新テーブルを既存行の直後から追記

5. **（必要に応じて）「enum定義」シートに enum 値を追加**

## 生成されるコードの概要

DB項目定義書の内容からツールが生成するコードには以下が含まれます。

- **Entity**: テーブル 1 件につき 1 クラス
- **Record**: Entity の入出力 DTO
- **Repository**: テーブル単位の JPA リポジトリ
- **BL**: CRUD・重複チェック・楽観的ロック確認等
- **Enum / Converter**: ENUM 列の型変換
- **DataTypeValidator**: DataType に基づくフィールドバリデーション
