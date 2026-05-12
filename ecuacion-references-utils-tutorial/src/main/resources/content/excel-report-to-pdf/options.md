# オプション設定

`PdfGenerateOptions` は PDF 生成時のオプションを保持するクラスです。
Builder パターンで構築します。

## オプション一覧

| オプション | 型 | 説明 |
| --- | --- | --- |
| `excelPassword` | String または null | 入力 Excel ファイルのパスワード |
| `pdfPassword` | String または null | 出力 PDF ファイルのパスワード |

## Builder の使い方

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;

PdfGenerateOptions options = PdfGenerateOptions.builder()
    .excelPassword("excel-pass")
    .pdfPassword("pdf-pass")
    .build();

ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```

不要なオプションは設定しなくても構いません（デフォルト値は `null`）。

## Excel ファイルのパスワード保護

パスワード保護された Excel ファイルを読み込む場合、
`excelPassword` にパスワード文字列を設定します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .excelPassword("secret123")
    .build();
```

## PDF ファイルのパスワード設定

出力する PDF にアクセス制限をかける場合、`pdfPassword` を設定します。
設定すると PDF を開く際にパスワードが必要になります。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .pdfPassword("view-only")
    .build();
```

## オプションなしの場合

オプションが不要な場合は `null` を渡します（`PdfGenerateOptions` を構築する必要はありません）。

```java
ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, null);
```
