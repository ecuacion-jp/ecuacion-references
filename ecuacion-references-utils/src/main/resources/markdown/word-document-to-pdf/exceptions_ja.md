## `WordToPdfUtil.generate()` の例外

すべて非検査例外です。

| 例外クラス | スローされる場面 |
| --- | --- |
| `SystemFontNotFoundException` | `builderForSystemFonts()` で、文書の既定フォントがインストールされておらず、フォールバックフォントも登録されていない |
| `FontLoadFailedException` | システムフォントファイルは見つかったが読み込めなかった |
| `CharacterNotRenderableException` | 文書内の文字が、設定されたどのフォントでもエンコードできない |
| `FileFormatNotSupportedException` | 対応形式（docx, docm, dotx, dotm）のWord ファイルでない（旧形式の `.doc`、他のアプリケーションのファイル、壊れたファイル・空のファイルなど） |
| `FilePasswordRequiredException` | ファイルがパスワードで保護されており、`wordPassword` が指定されていない |
| `FilePasswordIncorrectException` | ファイルがパスワードで保護されており、`wordPassword` が誤っている |
| `java.io.UncheckedIOException` | Word ファイルの読み込み・PDF の書き出しでの技術的な失敗（ファイルが存在しない、ディスク I/O エラーなど）。`getCause()` で元の `IOException` を取得できる |

最後の 1 つ以外は `PdfGenerateException` を継承し、`jp.ecuacion.util.pdf.common.exception` パッケージ
（`ecuacion-util-pdf-common` モジュール）に属します。`ecuacion-util-excel-report-to-pdf` がスローするものと同じクラスです。

## キャッチ方法

```java
import jp.ecuacion.util.pdf.common.exception.PdfGenerateException;
import jp.ecuacion.util.pdf.common.exception.SystemFontNotFoundException;

try {
    WordToPdfUtil.generate(wordPath, outputPath, options);
} catch (SystemFontNotFoundException ex) {
    // 例: 環境の問題として扱い直す
} catch (PdfGenerateException ex) {
    // その他のケース：ファイル（形式・パスワード）やフォントの問題
    String messageId = ex.getMessageId();
} catch (UncheckedIOException ex) {
    // 純粋に技術的な失敗。ex.getCause() は IOException
}
```

`PdfGenerateException` は利用者向けの多言語表示のための `messageId` を持ちます。
メッセージ本文は `messages_util_pdf_common.properties`（`_ja` などの各言語版ファイルあり）に定義されています。

対応していない要素（図形・テキストボックス・脚注など）では例外はスローされず、その要素を飛ばします。[注意事項](/public/showMarkdown/page?id=word-document-to-pdf/notes&lang=ja) を参照してください。
