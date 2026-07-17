`ecuacion-util-excel-report-to-pdf` は、Excel ファイルの印刷設定（印刷範囲・ページ区切り）を
元に PDF を生成するユーティリティです。Apache POI で Excel を読み込み、
Apache PDFBox で PDF を出力します。

## 主な特徴

- Excel シートの印刷範囲を PDF ページとして出力
- セル値・テキストスタイル・背景色・罫線・結合セルを PDF に再現
- OS システムフォントの自動検索、または任意の TTF フォントファイルのパス指定に対応（`PdfGenerateOptions` で設定）
- Excel ファイルのパスワード保護に対応
- 出力 PDF へのパスワード設定に対応

## 公開 API

| クラス | 役割 |
| --- | --- |
| `ExcelToPdfUtil` | PDF 生成のメインユーティリティ（`static` メソッド） |
| `PdfGenerateOptions` | パスワードなどのオプション設定（Builder パターン） |
| `PdfGenerateException` | PDF 生成中のエラーを表す例外 |

依存の追加については[セットアップ](/public/showMarkdown/page?id=excel-report-to-pdf/setup&lang=ja)を参照してください。

## 注意事項

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
