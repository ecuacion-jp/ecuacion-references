依存関係を追加します。

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-rest</artifactId>
</dependency>
```

WAR としてパッケージする場合（既存の Tomcat に war を配置する場合）は `spring-boot-starter-tomcat` を
`provided` スコープで追加します。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

続けて [クイックスタート](page?id=rest/quickstart&lang=ja) で、必要な設定クラスを揃えて最初のエンドポイントを追加します。
