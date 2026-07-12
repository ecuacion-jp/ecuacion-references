# クイックスタート

このページでは `StringOneLineHeaderExcelTableReader` を使った最もシンプルな読み込み例を紹介します。

依存の追加については[セットアップ](/public/showMarkdown/page?id=excel-tables/setup&lang=ja)を参照してください。

## Excel ファイルの準備

「Sheet1」シートに次のような構造のテーブルがあるとします。

| 名前 | 年齢 | メール |
| --- | --- | --- |
| 山田太郎 | 30 | taro@example.com |
| 鈴木花子 | 25 | hanako@example.com |

## コード例

```java
import jp.ecuacion.util.excel.table.reader.concrete.StringOneLineHeaderExcelTableReader;
import java.util.List;

StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"名前", "年齢", "メール"});

List<List<String>> data = reader.read("/path/to/file.xlsx");

for (List<String> row : data) {
    String name  = row.get(0);
    String age   = row.get(1);
    String email = row.get(2);
    System.out.println(name + " / " + age + " / " + email);
}
```

## 動作の仕組み

1. **テーブル位置の自動検出**: ヘッダーの最左列値（`"名前"`）をシート内で検索し、
   テーブルの開始行を自動的に特定します。
2. **ヘッダーの検証**: 指定したヘッダーラベル配列と Excel のヘッダー行が一致することを確認します。
   一致しない場合は `ExcelTableException` がスローされます。
3. **データ行の読み込み**: 全列が空の行が現れるまで読み込みを続け、
   `List<List<String>>` として返します。

## 戻り値について

- `read()` の戻り値にはヘッダー行は含まれません（複数行の場合はすべてのヘッダー行が検証後に除去されます）。
- 空セルの値はデフォルトで `null` になります
  （詳細は[データ型の選択](/public/showMarkdown/page?id=excel-tables/data-types&lang=ja)を参照）。
