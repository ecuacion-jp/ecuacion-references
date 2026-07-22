### 大きなファイルの扱い

Apache POI は Excel ファイル（xlsx）を開く際にファイル全体をメモリに展開します。
このライブラリ内部ではファイルサイズの上限チェックを行っていないため、
巨大なファイルを処理するとメモリ不足になる可能性があります。

ユーザーがアップロードしたファイルをそのまま処理するような用途では、
呼び出し元でファイルサイズを事前にチェックすることを推奨します。

```java
if (Files.size(excelPath) > 50 * 1024 * 1024) { // 例: 50MB
    throw new IllegalArgumentException("ファイルサイズが上限を超えています");
}
ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```
