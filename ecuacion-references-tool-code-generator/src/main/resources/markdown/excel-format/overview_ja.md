DB項目定義書は `ecuacion-tool-code-generator` の入力ファイルです。
Excel ファイル（`.xlsx`）1 枚でプロジェクトのデータモデルを定義します。

## ファイル名

```
db-column-definitions_fmt-v5.0.0-ja_<プロジェクト名>.xlsx
```

## シート構成

| シート名 | 役割 | 詳細 |
| --- | --- | --- |
| 各種設定 | プロジェクト全体の設定（パッケージ名など） | [各種設定シート](page?id=excel-format/general-settings&lang=ja) |
| dataType定義 | フィールドの型定義（`DT_XXXX` 形式） | [dataType定義シート](page?id=excel-format/data-type-sheet&lang=ja) |
| DB項目定義 | テーブルとカラムの定義 | [DB項目定義シート](page?id=excel-format/db-definition-sheet&lang=ja) |
| DB共通項目定義 | 全テーブルに共通で付与するカラム（監査カラム・ソフトデリートフラグ・楽観的排他制御バージョン等）を、<br>テーブルごとに繰り返し定義せずに一括指定 | 列構成はDB項目定義と同じ。<br>詳細は[DB項目定義シート](page?id=excel-format/db-definition-sheet&lang=ja)を参照 |
| テーブル一覧 | テーブルの表示名（言語別）。DB項目定義で使われているテーブル名から自動で生成される | — |
| enum定義 | ENUM 型の値定義 | [enum定義シート](page?id=excel-format/enum-sheet&lang=ja) |

## 新規プロジェクトでの作成手順

1. **テンプレートをダウンロードしてリネーム**
   - [GitHub リポジトリの `excel-format/`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/tree/main/excel-format) からテンプレートをダウンロード
   - `db-column-definitions_fmt-v5.0.0-ja_<新プロジェクト名>.xlsx` にリネーム
   - 記入例は [qiita-data-viewer の `excel-format/`](https://github.com/ecuacion-jp/qiita-data-viewer/tree/main/excel-format) を参照

2. **「各種設定」シートを修正**（最優先）
   - 新規プロジェクトで必要な最低限の設定は [DB項目定義書 クイックスタート](page?id=excel-format/quickstart&lang=ja) を、
     全項目の詳細は [各種設定シート](page?id=excel-format/general-settings&lang=ja) を参照

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
