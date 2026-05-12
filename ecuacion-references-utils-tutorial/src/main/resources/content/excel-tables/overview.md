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

詳細は[データ型の選択](/public/article?id=excel-tables/data-types)を参照してください。

### テーブル形式

| インターフェース | 特徴 |
| --- | --- |
| `IfFormatOneLineHeaderExcelTable` | 先頭行がヘッダーのテーブル（ヘッダー検証あり） |
| `IfFormatFreeExcelTable` | ヘッダーなし・任意位置のテーブル |

詳細は[テーブル形式の選択](/public/article?id=excel-tables/table-formats)を参照してください。

## 使用するクラスの選び方

2 軸の組み合わせから、目的に合ったクラスを選びます。

### Reader（読み込み）クラス

| クラス名 | データ型 | テーブル形式 | 備考 |
| --- | --- | --- | --- |
| `StringHeaderExcelTableReader` | String | OneLineHeader | ヘッダーあり。最も一般的。 |
| `StringHeaderExcelTableToBeanReader` | String | OneLineHeader | 各行を Bean に自動変換。 |
| `StringFreeExcelTableReader` | String | Free | ヘッダーなし・任意位置。 |
| `CellOneLineHeaderExcelTableReader` | Cell | OneLineHeader | Cell 型で取得。 |
| `CellFreeExcelTableReader` | Cell | Free | Cell 型・ヘッダーなし。 |

### Writer（書き込み）クラス

| クラス名 | データ型 | テーブル形式 |
| --- | --- | --- |
| `StringHeaderExcelTableWriter` | String | OneLineHeader |
| `StringFreeExcelTableWriter` | String | Free |
| `CellOneLineHeaderExcelTableWriter` | Cell | OneLineHeader |
| `CellFreeExcelTableWriter` | Cell | Free |

## 依存の追加

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-table</artifactId>
    <version>（バージョン）</version>
</dependency>
```

Apache POI（`poi` および `poi-ooxml`）は推移的依存として自動的に含まれます。
