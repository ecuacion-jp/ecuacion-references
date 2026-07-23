## `ExcelToPdfUtil.generate()`

PDF を生成するメインメソッドです。

```java
public static void generate(
    Path excelPath,
    List<String> sheetNames,
    Path outputPath,
    PdfGenerateOptions options) throws PdfGenerateException
```

| 引数 | 説明 |
| --- | --- |
| `excelPath` | 変換元 Excel ファイルのパス |
| `sheetNames` | PDF に出力するシート名のリスト（記載順に出力） |
| `outputPath` | 出力 PDF ファイルのパス |
| `options` | フォントパスなどのオプション（必須） |

## コード例

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;
import jp.ecuacion.util.pdf.excel.report.util.ExcelToPdfUtil;
import jp.ecuacion.util.pdf.excel.report.exception.PdfGenerateException;
import java.nio.file.Path;
import java.util.List;

Path excelPath  = Path.of("/path/to/report.xlsx");
Path outputPath = Path.of("/path/to/output.pdf");

PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .build();

try {
    ExcelToPdfUtil.generate(
        excelPath,
        List.of("表紙", "明細"),   // 変換するシート名
        outputPath,
        options);
} catch (PdfGenerateException ex) {
    System.err.println("PDF 生成に失敗しました: " + ex.getMessage());
}
```

> **注意:** `builderForSystemFonts()` は、ローカルにインストールされているフォント（OS および
> Microsoft Office 付属のフォント）を検索します。該当するフォントが見つからない場合は
> `PdfGenerateException` がスローされます。
>
> **注意:** 上記コードは簡便のためシステムフォントを使用する例を掲載しています。システムフォントは
> 出力 PDF に埋め込まれるため、使用するフォントのライセンスが埋め込みおよび再配布を許可していることを
> 確認してください。

## 複数シートの出力

`sheetNames` に複数のシート名を渡すと、指定した順番で PDF の各ページに出力されます。
シートごとの印刷範囲設定がページ分割の基準になります。

```java
ExcelToPdfUtil.generate(
    excelPath,
    List.of("表紙", "集計", "明細1", "明細2"),
    outputPath,
    options);
```
