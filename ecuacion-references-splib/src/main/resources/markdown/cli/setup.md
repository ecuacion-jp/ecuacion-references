Assuming the [Setup](page?id=home&lang=en) on the home page is done (declaring
`ecuacion-splib-parent` as the parent POM, or importing it as a BOM), add the dependency. No
`<version>` tag is needed — it comes from `ecuacion-splib-parent`'s `dependencyManagement`.

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-cli</artifactId>
</dependency>
```

Continue to [Quickstart](page?id=cli/quickstart&lang=en) to write your app's entry point and
run it.
