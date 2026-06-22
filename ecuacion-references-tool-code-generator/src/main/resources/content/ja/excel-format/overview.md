# DB項目定義書（Excel）概要

DB項目定義書は `ecuacion-tool-code-generator` の入力ファイルです。
Excel ファイル（`.xlsx`）1 枚でプロジェクトのデータモデルを定義します。

## ファイル名

```
db-definition-book-fmt-v4.11.0_<プロジェクト名>_ja.xlsx
```

## シート構成

| シート名 | 役割 | 詳細 |
| --- | --- | --- |
| 各種設定 | プロジェクト全体の設定（パッケージ名など） | [各種設定シート](/public/ja/article?id=excel-format/general-settings) |
| dataType定義 | フィールドの型定義（`DT_XXXX` 形式） | [dataType定義シート](/public/ja/article?id=excel-format/data-type-sheet) |
| DB項目定義 | テーブルとカラムの定義 | [DB項目定義シート](/public/ja/article?id=excel-format/db-definition-sheet) |
| enum定義 | ENUM 型の値定義 | [enum定義シート](/public/ja/article?id=excel-format/enum-sheet) |

## 新規プロジェクトでの作成手順

1. **テンプレートをダウンロードしてリネーム**
   - [GitHub リポジトリの `excel-format/`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/tree/main/excel-format) からテンプレートをダウンロード
   - `db-definition-book-fmt-v4.11.0_<新プロジェクト名>_ja.xlsx` にリネーム
   - 記入例は [qiita-data-viewer の `excel-format/`](https://github.com/ecuacion-jp/qiita-data-viewer/tree/main/excel-format) を参照

2. **「各種設定」シートを修正**（最優先）
   - `SYSTEM_NAME`（行 8）: 新プロジェクト名
   - `BASE_PACKAGE`（行 9）: 新しい Java パッケージ
   - `TABLE_NAMES_WITHOUT_GROUPING`（行 31）: グループフィルタなしのテーブル一覧

3. **「dataType定義」シートに必要な DataType をすべて記述する**

4. **「DB項目定義」シートにテーブル定義を記述する**

5. **（必要に応じて）「enum定義」シートに enum 値を追加**

## 生成されるコードの概要

DB項目定義書の内容からツールが生成するコードには以下が含まれます。

- **Entity**: テーブル 1 件につき 1 クラス
- **Record**: Entity の入出力 DTO
- **Repository**: テーブル単位の JPA リポジトリ
- **BL**: CRUD・重複チェック・楽観的ロック確認等
- **Enum / Converter**: ENUM 列の型変換
- **DataTypeValidator**: DataType に基づくフィールドバリデーション
