ホームの [セットアップ](page?id=home&lang=ja)（`ecuacion-splib-parent` を親 POM として指定、または
BOM としてインポート）が完了している前提で、依存関係を追加します。`ecuacion-splib-parent` の
`dependencyManagement` により、`<version>` タグは不要です。

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-batch</artifactId>
</dependency>
```

続けて [クイックスタート](page?id=batch/quickstart&lang=ja) で、必要な設定クラスを揃えて最初のジョブを実行します。
