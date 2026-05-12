# セットアップ

## 依存の追加

`pom.xml` に以下を追加します。

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-report-to-pdf</artifactId>
    <version>（バージョン）</version>
</dependency>
```

## フォントファイルの配置

日本語テキストを正しく PDF に出力するために、
**Noto Sans JP** フォントファイルが必要です。

以下のパスにファイルを配置します。

```
src/main/resources/fonts/NotoSansJP/NotoSansJP-Regular.ttf
src/main/resources/fonts/NotoSansJP/NotoSansJP-Bold.ttf
```

### Noto Sans JP の入手方法

Google Fonts（https://fonts.google.com/noto/specimen/Noto+Sans+JP）から
無償でダウンロードできます。

1. ページ上部の「Download family」ボタンをクリック
2. ダウンロードした ZIP を展開
3. `NotoSansJP-Regular.ttf` と `NotoSansJP-Bold.ttf` を上記パスに配置

> フォントファイルはリポジトリにコミットしないよう
> `.gitignore` に追加することを検討してください。
