## `PdfGenerateException`

PDF生成が、指定されたExcelファイルの内容や、それが依存するフォント環境に起因して失敗した場合にスローされる例外群の共通スーパークラスです（`abstract`、
`ViolationException` を継承）。`abstract` なので直接スローされることはなく、
必ず下記のいずれかの具象サブクラスがスローされます。

Excelファイルの読み込みやPDFファイルの書き込み中に起きる純粋に技術的な失敗（ファイル破損、ディスクI/Oエラーなど）は `PdfGenerateException` では表現されません。
`generate()` はその場合、`java.io.UncheckedIOException`（非検査例外）をスローします。
`getCause()` で元の `IOException` を取得できます。

### 具象サブクラス

| 例外クラス | スローされる場面 |
| --- | --- |
| `SheetNotExistException` | `generate()` に渡したシート名がExcelファイルに存在しない |
| `SheetHasNoPrintAreaException` | シートに印刷範囲もデータも存在せず、範囲を推測できない |
| `SystemFontNotFoundException` | `useSystemFonts` 有効時、workbookの既定フォントが実行環境にインストールされておらず、フォールバックフォントも設定されていない |
| `FontLoadFailedException` | システムフォントファイルは見つかったが、`TrueTypeFont` として読み込めなかった |
| `CharacterNotRenderableException` | セル内の文字が、既定フォント・設定済みのフォールバックフォントのいずれでもエンコードできない |

5つとも `jp.ecuacion.util.pdf.excel.report.exception` パッケージに属し、
`PdfGenerateException` を継承しています。

### キャッチ方法

失敗ケースごとに専用の例外クラスがあるため、messageId文字列で分岐するのではなく、
型でcatchします。

```java
import jp.ecuacion.util.pdf.excel.report.exception.PdfGenerateException;
import jp.ecuacion.util.pdf.excel.report.exception.SystemFontNotFoundException;

try {
    ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
} catch (SystemFontNotFoundException ex) {
    // このケースだけ個別に処理したい場合（例: 環境要因として再分類する等）
} catch (PdfGenerateException ex) {
    // 残り4ケースを包括的にキャッチ
    String messageId = ex.getMessageId();
    System.err.println("PDF生成エラー: " + messageId);
} catch (UncheckedIOException ex) {
    // 純粋に技術的な失敗（ファイル破損、ディスクエラーなど）。ex.getCause()がIOException
}
```

### messageId の確認

`ExcelTableException` と同様、`PdfGenerateException` も `messageId`（`getMessageId()`、
`ViolationException` から継承）を保持しており、エンドユーザー向けのi18n表示を意図しています。
メッセージ本文は `messages_util_excel_report_to_pdf.properties` /
`messages_util_excel_report_to_pdf_ja.properties` に定義されています。

```java
} catch (PdfGenerateException ex) {
    String messageId = ex.getMessageId();
    Object[] args = ex.getViolations().getBusinessViolations().get(0).getMessageArgs();
}
```
