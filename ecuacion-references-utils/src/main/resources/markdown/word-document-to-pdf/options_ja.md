`PdfGenerateOptions` は PDF 生成のパラメータを保持するクラスです。
フォントの解決方法に応じた 2 つの static ファクトリメソッドのどちらかから始めて、Builder パターンで作成します。
`ecuacion-util-excel-report-to-pdf` の `PdfGenerateOptions` と同じ形です
（パッケージは `jp.ecuacion.util.pdf.word.document.options`）。

## 指定できるオプション

| オプション | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| エントリポイント | `builderForSystemFonts()` または `builderForExplicitFont(Path)` | 必須 | フォント解決モードを選択する |
| `addRegularFontPath` | Path | 任意・複数回指定可 | 通常テキストに使う TTF / TTC ファイルを登録する（登録順に試す）。`builderForExplicitFont` では引数が 1 件目として登録済み |
| `addBoldFontPath` | Path | 任意・複数回指定可 | 太字テキストに使う TTF / TTC ファイルを登録する（通常フォントより先に試す） |
| `wordPassword` | String または null | 任意 | パスワード付き Word ファイルを開くパスワード |
| `pdfPassword` | String または null | 任意 | 出力 PDF を開くパスワード（ユーザーパスワード）。PDF は 256 ビット AES で暗号化される |
| `pdfOwnerPassword` | String または null | 任意 | 出力 PDF のオーナーパスワード。省略時は `pdfPassword` と同じ |

## システムフォントの使用（`builderForSystemFonts`）

文書で指定されたフォントを OS のフォントディレクトリ（Microsoft Office がインストールしたフォントを含む）から検索します。
Word 文書は欧文と和文に別々のフォントを指定でき、文字ごとにそのフォントが使われます。

- インストールされていないフォントは、文書の既定フォントで代替します（警告ログを出力）。
- 文書の既定フォント自体がインストールされていない場合は、`addRegularFontPath` で登録したフォントを使います。
  登録が無ければ `SystemFontNotFoundException` がスローされます。
- 指定フォントに無い文字は、`addRegularFontPath` / `addBoldFontPath` で登録したフォントで描画します。
- Word と同じく、フォントファミリー自身に太字書体がある場合だけそれを使います（例: 游ゴシック、Arial、メイリオ）。
  無い場合（例: 游明朝、Century、ＭＳ 明朝）は標準書体を太らせて描画します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .addRegularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))  // フォールバック
    .build();
```

> **フォントライセンスに注意:** 見つかったシステムフォントは出力 PDF に埋め込まれます。
> フォントのライセンスが埋め込み・再配布を許可していることを確認してください。

Word と同じレイアウトにするには、文書で使うフォント（Microsoft Office に付属するフォントなど）をサーバーにインストールしてください。

## フォントファイルの指定（`builderForExplicitFont`）

文書で指定されたフォントに関係なく、指定したフォントファイルですべての文字を描画します。
フォントが文書の指定と異なる分だけ、レイアウトは Word と異なります。

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // 任意
    .build();
```

`addBoldFontPath` を呼ばない場合、太字は通常フォントを太らせて描画します。
TrueType Collection（`.ttc`）ファイルも指定でき、その場合は先頭のフォントを使います。

## パスワード

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .wordPassword("word-pass")         // Word ファイルを開くパスワード
    .pdfPassword("view-only")          // 出力 PDF を開くパスワード
    .pdfOwnerPassword("admin-secret")  // PDF のセキュリティ設定を変更するパスワード
    .build();
```

保護された Word ファイルに対して `wordPassword` が未指定・誤っている場合は
`FilePasswordRequiredException`（未指定）・`FilePasswordIncorrectException`（誤り）がスローされます。保護されていないファイルでは `wordPassword` は無視されます。
PDF のユーザーパスワード・オーナーパスワードの考え方は
[excel-report-to-pdf のオプション設定](/public/showMarkdown/page?id=excel-report-to-pdf/options&lang=ja) と同じです。
