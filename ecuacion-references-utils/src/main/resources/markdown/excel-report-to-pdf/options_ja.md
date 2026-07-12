# オプション設定

`PdfGenerateOptions` は PDF 生成時のオプションを保持するクラスです。
Builder パターンで構築します。

## オプション一覧

| オプション | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| `useSystemFonts` | boolean | 任意 | `true` にすると OS および Office のフォントを自動検索。デフォルト `false` |
| `regularFontPath` | Path または null | `useSystemFonts` が `false` の場合に必須 | 通常テキストに使用する TTF フォントファイルのパス。`useSystemFonts` が `true` の場合はフォールバックとして機能 |
| `boldFontPath` | Path または null | 任意 | 太字テキストに使用する TTF フォントファイルのパス。省略時は `regularFontPath` で代替 |
| `excelPassword` | String または null | 任意 | 入力 Excel ファイルのパスワード |
| `pdfPassword` | String または null | 任意 | 出力 PDF を開くためのパスワード（ユーザーパスワード） |
| `pdfOwnerPassword` | String または null | 任意 | 出力 PDF のオーナーパスワード。省略時は `pdfPassword` と同じ値になる |
| `dateLocale` | Locale または null | 任意 | 日付フォーマット解決に使用するロケール。省略時は JVM デフォルトロケール |

## Builder の使い方

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;

PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .boldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))  // 省略可
    .excelPassword("excel-pass")
    .pdfPassword("pdf-pass")
    .build();

ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```

`useSystemFonts` が `false`（デフォルト）の場合は `regularFontPath` が必須です。
それ以外のオプションは設定しなくても構いません。

## システムフォントの使用（`useSystemFonts`）

`useSystemFonts(true)` を設定すると、OS にインストールされているフォントおよび
Microsoft Office 付属のフォントをワークブックのデフォルトフォント名で自動検索します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .useSystemFonts(true)
    .build();
```

システムフォントが見つからない場合は `PdfGenerateException` がスローされます。
見つからなかった場合のフォールバックとして、`regularFontPath` も合わせて指定できます。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .useSystemFonts(true)
    .regularFontPath(Path.of("/path/to/fallback.ttf"))  // フォールバック
    .build();
```

> **フォントライセンスに注意:** システムフォントは出力 PDF に埋め込まれます。
> 使用するフォントのライセンスが埋め込みおよび再配布を許可していることを確認してください。

## フォントファイルの指定

通常テキストに使用する TTF フォントファイルを `regularFontPath` に指定します。
日本語テキストを含む場合は日本語対応フォント（例：Noto Sans JP）を使用してください。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .boldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))  // 省略可
    .build();
```

## Excel ファイルのパスワード保護

パスワード保護された Excel ファイルを読み込む場合、
`excelPassword` にパスワード文字列を設定します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/regular.ttf"))
    .excelPassword("secret123")
    .build();
```

## PDF ファイルのパスワード設定

### ユーザーパスワード（`pdfPassword`）

出力する PDF を開く際にパスワードを要求したい場合、`pdfPassword` を設定します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/regular.ttf"))
    .pdfPassword("view-only")
    .build();
```

### オーナーパスワード（`pdfOwnerPassword`）

PDF には「ユーザーパスワード」と「オーナーパスワード」の2種類があります。

| パスワード | 役割 |
| --- | --- |
| ユーザーパスワード（`pdfPassword`） | PDF を開くために必要 |
| オーナーパスワード（`pdfOwnerPassword`） | PDF のセキュリティ設定（印刷禁止・コピー禁止など）を後から変更するために必要 |

`pdfOwnerPassword` を省略した場合、`pdfPassword` がオーナーパスワードとしても使用されます。
つまり、PDF を開ける人は誰でもセキュリティ設定も変更できます。

PDF の生成者とセキュリティ設定の管理者が異なる場合（例：システムが `pdfPassword` 付き PDF を生成し、後で管理者が印刷禁止などの設定を `qpdf` 等で追加する）は、`pdfOwnerPassword` を別途設定してください。

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/regular.ttf"))
    .pdfPassword("view-only")         // PDF を開くパスワード
    .pdfOwnerPassword("admin-secret") // セキュリティ設定を変更するパスワード
    .build();
```

> **注意:** このライブラリ自体は印刷禁止・コピー禁止などの操作制限を設定しません。
> 操作制限が必要な場合は、生成した PDF に対して外部ツール（`qpdf` や Adobe Acrobat など）で後から設定してください。その際にオーナーパスワードが必要になります。

## 日付ロケールの指定（`dateLocale`）

日付セルのフォーマット解決に使用するロケールを指定します。
省略した場合は `Locale.getDefault()` が使用されます。

```java
import java.util.Locale;

PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/font.ttf"))
    .dateLocale(Locale.JAPAN)
    .build();
```
