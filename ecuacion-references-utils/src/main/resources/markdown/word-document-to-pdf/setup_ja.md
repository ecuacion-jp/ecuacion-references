## 依存関係の追加

`pom.xml` に以下を追加します。

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-word-document-to-pdf</artifactId>
    <version>(version)</version>
</dependency>
```

`ecuacion-util-pdf-common`（フォント解決と、`ecuacion-util-excel-report-to-pdf` と共通の例外）は依存として自動的に読み込まれます。

## 互換性

| ecuacion-utils | ecuacion-lib |
| --- | --- |
| 5.x | 16.x |

## フォントの設定

PDF の文字の描画に使うフォントの設定が必要です。
`builderForSystemFonts()` では、文書で指定されたフォントを OS にインストールされたフォント（Microsoft Office がインストールしたフォントを含む）から検索します。
フォントファイルを使う場合は TTF ファイルを用意し、`builderForExplicitFont(Path)` を使います。

詳細は [オプション設定](/public/showMarkdown/page?id=word-document-to-pdf/options&lang=ja) を参照してください。
