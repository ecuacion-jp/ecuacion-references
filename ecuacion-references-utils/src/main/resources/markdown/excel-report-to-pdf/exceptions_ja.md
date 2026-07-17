## `PdfGenerateException`

PDF 生成中に発生するエラーを表す検査例外（`Exception` のサブクラス）です。

### スロー場面

| 状況 | メッセージ例 |
| --- | --- |
| 指定したシート名が Excel に存在しない | `Sheet not found: '存在しないシート'` |
| Excel ファイルの読み込みに失敗した | `Failed to generate PDF from '...'` |
| その他の I/O エラー | 原因例外を cause として保持 |

### キャッチ方法

```java
try {
    ExcelToPdfUtil.generate(excelPath, sheetNames, outputPath, options);
} catch (PdfGenerateException ex) {
    System.err.println("PDF 生成エラー: " + ex.getMessage());
    Throwable cause = ex.getCause();
    if (cause != null) {
        cause.printStackTrace();
    }
}
```

### 詳細な原因の確認

`PdfGenerateException` には原因例外が設定されている場合があります。
`getCause()` で内包された `IOException` などを取得できます。

```java
} catch (PdfGenerateException ex) {
    if (ex.getCause() instanceof IOException ioEx) {
        // ファイルアクセスエラーなど
    }
}
```
