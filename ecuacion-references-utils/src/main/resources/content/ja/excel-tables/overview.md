# excel-tables 概要

`ecuacion-util-excel-table` は、Apache POI を使って Excel ファイルのテーブルデータを
読み書きするためのライブラリです。
テーブルの位置自動検出・ヘッダー検証・空セル処理・Bean 変換などを抽象化し、
定型的なコードを大幅に削減できます。

## 用途

- Excel ファイルからデータをまとめて読み込みたい
- Java オブジェクト（Bean）のリストを Excel に書き出したい
- ヘッダー行のラベルを検証しながら安全に読み込みたい

## クラスの 2 軸分類

クラスは **データ型** と **テーブル形式** の 2 軸で分類されています。

### データ型

| インターフェース | 取得するデータ型 | 用途 |
| --- | --- | --- |
| `IfDataTypeStringExcelTable` | `String` | セル値を文字列として扱う（一般的） |
| `IfDataTypeCellExcelTable` | Apache POI `Cell` | セルのスタイルや型情報も参照したい場合 |

詳細は[データ型の選択](/public/ja/article?id=excel-tables/data-types)を参照してください。

### テーブル形式

| インターフェース | 特徴 |
| --- | --- |
| `IfFormatHeaderExcelTable` | 先頭に1行以上のヘッダー行があるテーブル（ヘッダー検証あり） |
| `IfFormatFreeExcelTable` | ヘッダーなし・任意位置のテーブル |

詳細は[テーブル形式の選択](/public/ja/article?id=excel-tables/table-formats)を参照してください。

## 使用するクラスの選び方

2 軸の組み合わせから、目的に合ったクラスを選びます。

### Reader（読み込み）クラス

| クラス名 | データ型 | テーブル形式 | 備考 |
| --- | --- | --- | --- |
| `StringOneLineHeaderExcelTableReader` | String | Header（1行） | ヘッダー1行。最も一般的。 |
| `StringOneLineHeaderExcelTableToBeanReader` | String | Header（1行） | 各行を Bean に自動変換。 |
| `StringHeaderExcelTableReader` | String | Header（複数行） | ヘッダーが2行以上の場合。 |
| `StringHeaderExcelTableToBeanReader` | String | Header（複数行） | 各行を Bean に自動変換。 |
| `StringFreeExcelTableReader` | String | Free | ヘッダーなし・任意位置。 |
| `CellOneLineHeaderExcelTableReader` | Cell | Header（1行） | Cell 型で取得。 |
| `CellHeaderExcelTableReader` | Cell | Header（複数行） | Cell 型・ヘッダーが2行以上の場合。 |
| `CellFreeExcelTableReader` | Cell | Free | Cell 型・ヘッダーなし。 |

> **Cell 型に ToBeanReader がない理由：** Bean 変換は文字列値をもとに型変換する仕組み（`StringExcelTableBean`）のため、Cell 型との組み合わせは提供していません。Cell 型が必要な場面ではスタイルや型情報をそのまま扱う方が自然なためです。

### Writer（書き込み）クラス

| クラス名 | データ型 | テーブル形式 | 備考 |
| --- | --- | --- | --- |
| `StringOneLineHeaderExcelTableWriter` | String | Header（1行） | ヘッダー1行。 |
| `StringOneLineHeaderExcelTableFromBeanWriter` | String | Header（1行） | Bean リストから書き込み。 |
| `StringHeaderExcelTableWriter` | String | Header（複数行） | ヘッダーが2行以上の場合。 |
| `StringHeaderExcelTableFromBeanWriter` | String | Header（複数行） | Bean リストから書き込み。 |
| `StringFreeExcelTableWriter` | String | Free | |
| `CellOneLineHeaderExcelTableWriter` | Cell | Header（1行） | |
| `CellHeaderExcelTableWriter` | Cell | Header（複数行） | Cell 型・ヘッダーが2行以上の場合。 |
| `CellFreeExcelTableWriter` | Cell | Free | |

依存の追加については[クイックスタート](/public/ja/article?id=excel-tables/quickstart)を参照してください。
