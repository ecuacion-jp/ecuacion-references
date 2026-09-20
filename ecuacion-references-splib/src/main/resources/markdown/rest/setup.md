Assuming the [Setup](page?id=home&lang=en) on the home page is done (declaring
`ecuacion-splib-parent` as the parent POM, or importing it as a BOM), add the dependency. No
`<version>` tag is needed — it comes from `ecuacion-splib-parent`'s `dependencyManagement`.

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-rest</artifactId>
</dependency>
```

Add `spring-boot-starter-tomcat` with `provided` scope as well if the application is packaged as a
WAR (i.e. deployed into an existing Tomcat):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

Continue to [Quickstart](page?id=rest/quickstart&lang=en) to wire up the
required configuration classes and add your first endpoint.
