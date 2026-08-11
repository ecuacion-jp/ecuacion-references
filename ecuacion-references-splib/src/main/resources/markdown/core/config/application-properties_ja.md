`application.properties` は、完全にSpring Bootが管理します。`PropertiesFileUtil.getApplication(key)`
（および `hasApplication`/`getApplicationOrElse`）は、SpringのEnvironmentにそのまま委譲される
ため、`@Value("${key}")` や `Environment.getProperty(key)` と全く同じ値が返ります。外部化
された `file:./config/application.properties`、環境変数、`-D` システムプロパティ、有効な
プロファイルによってマージされた値も同様に反映されます。

## どのAPIを使うべきか

以下の3つは、キーが存在する限り同じ値を返します。

```java
@Value("${my.key}")
private String fromValue;

// Beanとして注入する場合:
env.getProperty("my.key");

// ecuacion独自の静的API:
PropertiesFileUtil.getApplication("my.key");
```

通常のSpring Beanでは`@Value`/`Environment`を使ってください。`PropertiesFileUtil.getApplication()`
は、Springコンテキストなしでも動く必要があるコード（`ecuacion-lib-core`自身がこの用途で
使っています）や、`Environment`を注入するより静的呼び出しの方が都合が良い場面向けです。

## ここでは `#{...}` 相互参照は使えません

他のファイル種別では、値の中で `#{fileKind:key}` によって別キーを参照できます
（例: `messages.properties`から`#{application:app.url.root}`で`application.properties`の
値を埋め込む）。しかし`application.properties`自身の値の中でこの構文は使えません。
`PropertiesFileUtil.getApplication()`経由で読んだ場合も含め、常に単なるリテラル文字列
として扱われます。

```properties
# 解決されません — app.url.login の値は文字通り "#{application:app.url.root}/login" になります
app.url.root=https://example.com
app.url.login=#{application:app.url.root}/login
```

これは、`#{...}` がSpring自身のSpEL式の区切り文字でもあり、`@Value`が使うためです。もし
`application.properties`の値に`#{fileKind:key}`が含まれていて、同じキーがどこかで`@Value`
経由でも読まれると、Springは`fileKind:key`をSpEL式として解釈しようとして起動に失敗します。

```
SpelParseException: Expression [fileKind:key] @...: EL1041E: After parsing a valid
expression, there is still more data in the expression: 'colon(:)'
```

1つのプロパティの値を別のプロパティから組み立てたい場合は、`application.properties`内で
はなく、Javaコード側で行ってください（例: `env.getProperty("app.url.root") + "/login"`）。

## キーが存在しない場合

`getApplication(key)`は、キーがどこにも存在しない場合（`application.properties`、外部化
されたファイル、環境変数のいずれにもない場合）に例外を投げます。事前に`hasApplication(key)`
で確認するか、デフォルト値が欲しい場合は`getApplicationOrElse(key, default)`を使ってください。
