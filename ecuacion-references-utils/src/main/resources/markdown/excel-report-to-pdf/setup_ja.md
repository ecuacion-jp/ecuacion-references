## 依存の追加

`pom.xml` に以下を追加します。

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-report-to-pdf</artifactId>
    <version>（バージョン）</version>
</dependency>
```

## 互換性

| ecuacion-utils | ecuacion-lib |
| --- | --- |
| 5.x | 16.x |

## フォントの設定

PDF 生成時には、テキストレンダリングに使用するフォントを設定する必要があります。
通常は OS にインストールされているシステムフォントがそのまま使用されます。
システムフォント以外のフォントを使いたい場合は、フォントファイル（TTF）を用意して配置してください。

フォントの設定方法の詳細は [オプション設定](/public/showMarkdown/page?id=excel-report-to-pdf/options&lang=ja) を参照してください。
