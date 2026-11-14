## `WordToPdfUtil.generate()`

PDF を生成するメソッドです。

```java
public static void generate(Path wordPath, Path outputPath, PdfGenerateOptions options)
```

| 引数 | 説明 |
| --- | --- |
| `wordPath` | 変換元の Word（`.docx`）ファイルのパス |
| `outputPath` | 出力する PDF ファイルのパス |
| `options` | フォントなどの PDF 生成オプション（必須） |

## コード例

```java
import jp.ecuacion.util.pdf.common.exception.PdfGenerateException;
import jp.ecuacion.util.pdf.word.document.options.PdfGenerateOptions;
import jp.ecuacion.util.pdf.word.document.util.WordToPdfUtil;
import java.nio.file.Path;

Path wordPath   = Path.of("/path/to/contract.docx");
Path outputPath = Path.of("/path/to/contract.pdf");

PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .build();

try {
    WordToPdfUtil.generate(wordPath, outputPath, options);
} catch (PdfGenerateException ex) {
    System.err.println("PDF 生成に失敗しました: " + ex.getMessageId());
}
```

> **注意:** `builderForSystemFonts()` はローカルにインストールされたフォント（OS および
> Microsoft Office のフォント）を検索します。文書の既定フォントが見つからず、フォールバックフォントも
> 登録されていない場合は `SystemFontNotFoundException`（`PdfGenerateException` のサブクラス）がスローされます。
>
> **ライセンスに注意:** システムフォントは出力 PDF に埋め込まれます。フォントのライセンスが
> 埋め込み・再配布を許可していることを確認してから使用してください。

## フォントファイルを使う

文書で指定されたフォントに関係なく、フォントファイルで描画する場合：

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .addBoldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))   // 任意
    .build();
```

## 目次

Word の「目次」機能で作った目次は、Word ファイル内の目次が更新されていなくても、PDF のページ番号で出力されます。
各項目は見出しへのリンクになり、見出しは PDF のしおり（アウトライン）になります。
