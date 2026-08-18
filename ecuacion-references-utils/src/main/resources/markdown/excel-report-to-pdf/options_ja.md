`PdfGenerateOptions` は PDF 生成時のオプションを保持するクラスです。
Builder パターンで構築します。フォントの解決方法に応じて、2つの静的ファクトリメソッドのどちらかから始めます。

## オプション一覧

| オプション | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| エントリポイント | `builderForSystemFonts()` または `builderForExplicitFont(Path)` | 必須 | フォント解決モードを選択する。詳細は下記[システムフォントの使用](#システムフォントの使用builderforsystemfonts)を参照 |
| `addRegularFontPath` | Path | 任意・複数回指定可 | 通常テキストに使用する TTF フォントファイルを登録する（登録順に優先）。<br>`builderForExplicitFont` では引数で渡したフォントが自動的に1件目として登録されるため、本メソッドは2件目以降のフォールバックフォントを追加する場合にのみ呼び出す。<br>`builderForSystemFonts` ではシステムフォントが見つからなかった場合のフォールバックとして指定する。<br>詳細は下記[フォールバックフォントの指定](#フォールバックフォントの指定addregularfontpath--addboldfontpath)を参照 |
| `addBoldFontPath` | Path | 任意・複数回指定可 | 太字テキストに使用する TTF フォントファイルを登録する（登録順に優先、尽きたら上記の通常フォントへフォールスルー）。<br>一度も呼ばない場合、太字テキストは全て通常フォントで描画される |
| `excelPassword` | String または null | 任意 | 入力 Excel ファイルのパスワード |
| `pdfPassword` | String または null | 任意 | 出力 PDF を開くためのパスワード（ユーザーパスワード） |
| `pdfOwnerPassword` | String または null | 任意 | 出力 PDF のオーナーパスワード。省略時は `pdfPassword` と同じ値になる |
| `dateLocale` | Locale または null | 任意 | Excel の組み込み日付書式（書式コード14。「日付」の標準形式を選んだ際に使われる `yyyy/m/d` 相当の短い日付形式）をPDFに描画する際に使用するロケール。<br>省略時は JVM デフォルトロケール。<br>詳細は下記[日付ロケールの指定](#日付ロケールの指定datelocale)を参照 |

## Builder の使い方

```java
import jp.ecuacion.util.pdf.excel.report.options.PdfGenerateOptions;

PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))  // 省略可
    .excelPassword("excel-pass")
    .pdfPassword("pdf-pass")
    .build();

ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
```

`builderForExplicitFont` の引数は必須です。渡したフォントが自動的に `regularFontPaths` の
1件目として登録されるため、`addRegularFontPath` を別途呼び出す必要はありません。
それ以外のオプションは設定しなくても構いません。

## システムフォントの使用（`builderForSystemFonts`）

`builderForSystemFonts()` を使用すると、OS にインストールされているフォントおよび
Microsoft Office 付属のフォントをワークブックのデフォルトフォント名で自動検索します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .build();
```

システムフォントが見つからない場合は `PdfGenerateException` がスローされます。
見つからなかった場合のフォールバックとして、`addRegularFontPath` も合わせて指定できます。

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .addRegularFontPath(Path.of("/path/to/fallback.ttf"))  // フォールバック
    .build();
```

> **フォントライセンスに注意:** システムフォントは出力 PDF に埋め込まれます。
> 使用するフォントのライセンスが埋め込みおよび再配布を許可していることを確認してください。

フォントファミリーが複数のウェイトを持つ場合（例: 游ゴシック Light / Medium / Regular）、
通常（非太字）テキストの解決では Light よりも **Medium** ウェイトを優先します。これは、
macOS 版 Excel が CJK フォントの既定表示ウェイトとして Medium を使用する挙動に合わせたものです。
また、通常（立体）フォントを要求した際には **イタリック**・**ボールド** のバリアントを正しく除外するため、Calibri のようなフォントも確実に Regular 書体へ解決されます。

ワークブックの既定フォントが CJK グリフを含まない場合（例: Calibri）、セル内の CJK 文字は設定済みのフォールバックフォントを使って、1文字単位で自動的に描画されます。

## セル単位フォント解決

セルのフォントがワークブックの既定フォントと異なる場合（例: レポートの大部分は Calibri だが、
一部のセルだけローカライズされたコンテンツ用に日本語フォントが明示的に設定されている場合）、
常にワークブックの既定フォントを使うのではなく、そのセル自身のフォントが OS のフォントディレクトリから解決されて使用されます。これはワークブック内に存在する、既定フォントとは異なるフォント名ごとに適用され、上記と同じウェイト選択・TTC 検索ルールに従います。

セルのフォントがシステム上に見つからない場合、そのセルは（警告をログ出力した上で）ワークブックの既定フォントにフォールバックし、PDF 生成自体は失敗しません。生成が失敗するのは、
ワークブックの既定フォント自体が解決できず、かつ `addRegularFontPath` によるフォールバックフォントも設定されていない場合のみです。

## フォントファイルの指定

通常テキストに使用する TTF フォントファイルを `builderForExplicitFont` の引数に指定します。
日本語テキストを含む場合は日本語対応フォント（例：Noto Sans JP）を使用してください。

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))  // 省略可
    .build();
```

## フォールバックフォントの指定（`addRegularFontPath` / `addBoldFontPath`）

`addRegularFontPath`・`addBoldFontPath` はそれぞれ何度でも呼び出せます。呼んだ順が優先順位になり、ある1件でエンコードできない文字は、同じリストの次の1件が試されます。

文字ごとの解決順序:

1. 通常テキスト: `regularFontPaths`（登録順。explicit-fontモードでは
   `builderForExplicitFont` に渡したフォントが常に1件目）
2. 太字テキスト: `boldFontPaths`（登録順）→ 尽きたら上記の `regularFontPaths` へフォールスルー。`addBoldFontPath` を一度も呼ばない場合、太字テキストは全て通常フォントで描画される

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))
    .addRegularFontPath(Path.of("/path/to/NotoSansKR-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansKR-Bold.ttf"))
    .addRegularFontPath(Path.of("/path/to/NotoSansSC-Regular.ttf"))
    // NotoSansSC 用の addBoldFontPath は呼ばない
    // → この文字が太字で使われる場合、上の regularFontPaths のこのエントリへフォールスルー
    .build();
```

## Excel ファイルのパスワード保護

パスワード保護された Excel ファイルを読み込む場合、
`excelPassword` にパスワード文字列を設定します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/regular.ttf"))
    .excelPassword("secret123")
    .build();
```

## PDF ファイルのパスワード設定

### ユーザーパスワード（`pdfPassword`）

出力する PDF を開く際にパスワードを要求したい場合、`pdfPassword` を設定します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/regular.ttf"))
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
PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/regular.ttf"))
    .pdfPassword("view-only")         // PDF を開くパスワード
    .pdfOwnerPassword("admin-secret") // セキュリティ設定を変更するパスワード
    .build();
```

> **注意:** このライブラリ自体は印刷禁止・コピー禁止などの操作制限を設定しません。
> 操作制限が必要な場合は、生成した PDF に対して外部ツール（`qpdf` や Adobe Acrobat など）で後から設定してください。その際にオーナーパスワードが必要になります。

## 日付ロケールの指定（`dateLocale`）

`dateLocale` は、Excel の組み込み日付書式（書式コード14。セルの書式設定で「日付」の標準形式を選んだときに使われる、`yyyy/m/d` 相当の短い日付形式）を PDF に描画する際のロケールを指定します。

Excel の日付セルは内部的にはシリアル値（数値）として保持されており、書式コード14が設定されているだけでは「どのロケールで表示するか」の情報は含まれません。Excel は表示時に OS/アプリのロケールを参照し、日本語環境なら `2026/3/14`、英語環境なら
`3/14/26` のように動的に描画を切り替えます。一方 POI は、書式コード14に対して常に英語(米国)形式の書式文字列（`m/d/yy`）しか返さないため、本ライブラリはその情報だけでは
Excel 上の実際の表示を再現できません。`dateLocale` はこのギャップを埋めるために、
どのロケールで描画するかを明示的に指定するオプションです。

省略した場合は `Locale.getDefault()`（JVM デフォルトロケール）が使用されます。

なお、`[$-411]` のような地域コードを含むカスタム日付書式（例: `yyyy"年"m"月"d"日"`）は書式文字列自体からロケールを解決するため、`dateLocale` の指定に関係なく書式文字列どおりに描画されます。

```java
import java.util.Locale;

PdfGenerateOptions options = PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/font.ttf"))
    .dateLocale(Locale.JAPAN)
    .build();
```
