## 依存の追加

`pom.xml` に以下を追加します。

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-report-to-pdf</artifactId>
    <version>（バージョン）</version>
</dependency>
```

## フォントの設定

PDF 生成時には、テキストレンダリングに使用するフォントを設定する必要があります。
`PdfGenerateOptions` で以下の 2 通りの方法を選択できます。

### 方法 1：システムフォントを使用する（`builderForSystemFonts()`）

`builderForSystemFonts()` を使用すると、OS にインストールされているフォントおよび
Microsoft Office 付属のフォントをワークブックのデフォルトフォント名で自動検索します。

```java
PdfGenerateOptions options = PdfGenerateOptions.builderForSystemFonts()
    .build();
```

システムフォントが見つからない場合は `PdfGenerateException` がスローされます。
見つからなかった場合のフォールバックとして、`regularFontPath` も合わせて指定できます。

> **フォントライセンスに注意:** システムフォントは出力 PDF に埋め込まれます。
> 使用するフォントのライセンスが埋め込みおよび再配布を許可していることを確認してください。

### 方法 2：フォントファイルのパスを直接指定する（`builderForExplicitFont(Path)`）

`builderForExplicitFont` に TTF フォントファイルのパスを明示的に渡すと、
システムフォント検索を行わず常にそのフォントで描画します。

```java
PdfGenerateOptions options =
    PdfGenerateOptions.builderForExplicitFont(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .boldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))  // 省略可
    .build();
```

日本語テキストを含む場合は日本語対応フォントを使用してください。
例えば **Noto Sans JP** は [Google Fonts](https://fonts.google.com/noto/specimen/Noto+Sans+JP) から無償でダウンロードできます。
