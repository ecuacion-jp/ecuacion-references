ホームページの[セットアップ](page?id=home&lang=ja)（`ecuacion-splib-parent` を親POMにする、
またはBOMとしてインポートする）が済んでいる前提で、依存関係を追加します。
`<version>` タグは不要です — `ecuacion-splib-parent` の `dependencyManagement` から取得されます。

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-cli</artifactId>
</dependency>
```

続けて[クイックスタート](page?id=cli/quickstart&lang=ja)で、アプリのエントリーポイントを
書いて実行してみましょう。
