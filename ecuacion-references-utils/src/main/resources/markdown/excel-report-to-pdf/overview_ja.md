`ecuacion-util-excel-report-to-pdf` は、Excel ファイルの印刷設定（印刷範囲・ページ区切り）を元に PDF を生成するユーティリティです。Apache POI で Excel を読み込み、
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
