`application.properties` は、完全にSpring Bootが管理します。`PropertiesFileUtil.getApplication(key)`
（および `hasApplication`/`getApplicationOrElse`）は、SpringのEnvironmentにそのまま委譲されるため、`@Value("${key}")` や `Environment.getProperty(key)` と全く同じ値が返ります。外部化された `file:./config/application.properties`、環境変数、`-D` システムプロパティ、有効なプロファイルによってマージされた値も同様に反映されます。

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
は、Springコンテキストなしでも動く必要があるコード（`ecuacion-lib-core`自身がこの用途で使っています）や、`Environment`を注入するより静的呼び出しの方が都合が良い場面向けです。

## ここでは `#{...}` 相互参照は使えません

他のファイル種別では、値の中で `#{fileKind:key}` によって別キーを参照できます（例: `messages.properties`から`#{application:app.url.root}`で`application.properties`の値を埋め込む）。しかし`application.properties`自身の値の中でこの構文は使えません。
`PropertiesFileUtil.getApplication()`経由で読んだ場合も含め、常に単なるリテラル文字列として扱われます。

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

1つのプロパティの値を別のプロパティから組み立てたい場合は、`application.properties`内ではなく、Javaコード側で行ってください（例: `env.getProperty("app.url.root") + "/login"`）。

## キーが存在しない場合

`getApplication(key)`は、キーがどこにも存在しない場合（`application.properties`、外部化されたファイル、環境変数のいずれにもない場合）に例外を投げます。事前に`hasApplication(key)`
で確認するか、デフォルト値が欲しい場合は`getApplicationOrElse(key, default)`を使ってください。

## 再起動せずにホットリロードする

`application.properties`はデフォルトでは起動時に一度だけ読み込まれ、`PropertiesFileUtil`側とSpring自身の`Environment`側の両方でキャッシュされます。`ecuacion-splib`は、アプリを再起動せずにこのキャッシュをクリアする、同等の組み込みエンドポイントを2つ用意しています。`POST /api/ecuacion-splib/key/clearPropertiesCache`（**rest**メニュー配下の[組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja)を参照）と、`POST /ecuacion-splib/admin/config/action?action=clearPropertiesCache`（**web**メニュー配下の[運用エンドポイント](page?id=web/operational-endpoints&lang=ja)を参照）です。どちらも内部的に同じ`SplibPropertiesCacheClearer`を使っているため、どちらを使うかは単に、アプリが既に依存しているモジュールと、設定済みの組み込み資格情報がどちらかという話に過ぎません。

これは`PropertiesFileUtil`のキャッシュを常にクリアします。Spring自身の`Environment`
（`@Value` / `Environment.getProperty()`が参照する値）**も**リフレッシュしたい場合は、
アプリ側で`spring-cloud-context`を自分の依存性として追加する必要があります。これは
`ecuacion-splib-core`のoptional依存性なので、自分で追加しない限り引き込まれません。

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-context</artifactId>
</dependency>
```

それでも、通常のシングルトンBeanは`@Value`をコンストラクタ時に一度だけ読むため、
`Environment`をリフレッシュしただけではBean内にキャッシュされたフィールドの値は更新されません。新しい値を実際に反映させたいBeanには`@RefreshScope`を付けてください。

```java
@Component
@RefreshScope
public class MyComponent {
  @Value("${my.key}")
  private String myKey;
}
```

**既知の制限（`spring-cloud-context` 5.0.1時点）**：このリフレッシュは`application.properties`
自体の変更しか確実には反映しません（executable WAR・外部Tomcat・フラットクラスパスのいずれでも同様に動作することを確認済み）。アプリが`spring.config.name`に追加の名前を指定している場合（例：`spring.config.name=application,my-app`）、その追加ファイルへの変更はこのリフレッシュでは反映されません。詳細は[組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja)を参照してください。
