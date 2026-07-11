# セットアップ

## 依存の追加

`pom.xml` に以下を追加します。

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-table</artifactId>
    <version>（バージョン）</version>
</dependency>
```

Apache POI（`poi` および `poi-ooxml`）は推移的依存として自動的に含まれます。
