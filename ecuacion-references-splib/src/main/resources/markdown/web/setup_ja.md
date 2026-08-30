ホームページの[セットアップ](page?id=home&lang=ja)（`ecuacion-splib-parent` を親POMにする、またはBOMとしてインポートする）が済んでいる前提で、依存関係を追加します。`<version>` タグは不要です。`ecuacion-splib-parent` の `dependencyManagement` から自動的に決まります。

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-web</artifactId>
</dependency>
```

アプリケーションをWARとしてパッケージする（既存のTomcatにデプロイする）場合は、`spring-boot-starter-tomcat` を `provided` スコープで追加してください。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

続いて[クイックスタート](page?id=web/quickstart&lang=ja)で、必要な設定クラスを組み込みます。
