# excel-report-to-pdf 概要

`ecuacion-util-excel-report-to-pdf` は、Excel ファイルの印刷設定（印刷範囲・ページ区切り）を
元に PDF を生成するユーティリティです。Apache POI で Excel を読み込み、
Apache PDFBox で PDF を出力します。

## 主な特徴

- Excel シートの印刷範囲を PDF ページとして出力
- セル値・テキストスタイル・背景色・罫線・結合セルを PDF に再現
- 日本語フォント（Noto Sans JP）対応
- Excel ファイルのパスワード保護に対応
- 出力 PDF へのパスワード設定に対応

## 公開 API

| クラス | 役割 |
| --- | --- |
| `ExcelToPdfUtil` | PDF 生成のメインユーティリティ（`static` メソッド） |
| `PdfGenerateOptions` | パスワードなどのオプション設定（Builder パターン） |
| `PdfGenerateException` | PDF 生成中のエラーを表す例外 |

## 依存の追加

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-report-to-pdf</artifactId>
    <version>（バージョン）</version>
</dependency>
```

Apache PDFBox および Apache POI は推移的依存として自動的に含まれます。
