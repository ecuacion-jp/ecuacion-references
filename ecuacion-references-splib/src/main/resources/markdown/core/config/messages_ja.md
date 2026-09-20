`messages.properties`の解決方式は、以下の1つの設定で2つのモードを切り替えられます。

```properties
jp.ecuacion.splib.core.messages.use-spring-native=false
```

| 設定値 | `MessageSource` bean | 意味 |
| --- | --- | --- |
| `false`（デフォルト） | `PropertiesFileUtil`をバックエンドとする`PropertiesFileUtilMessageSource` | `.default`/`.base`モジュール横断上書き、`#{fileKind:key}`相互参照、UTF-8読み込み保証 — ecuacion独自の解決 |
| `true` | Spring Boot標準の`ResourceBundleMessageSource` | 素のSpring Boot挙動。`spring.messages.*`設定がSpring Boot本来の仕様通りすべて機能する |

この設定は、Spring自身の`MessageSource` bean（Thymeleafの`#{...}`メッセージ式、Spring MVC
自身のメッセージ解決、`MessageSource`を注入して`getMessage(...)`を呼ぶコードなど）が何を参照するかだけを変更します。

## この設定で変わらないもの

`PropertiesFileUtil.getMessage(...)`（や`getItemName`/`getConstant`/`getEnumName`）を直接呼ぶ場合は、この設定値に関わらず常に`PropertiesFileUtil`自身の解決が使われます。
これにはecuacionモジュール自身が組み込みで持つメッセージ（例: `ecuacion-lib-core`が
`.default`サフィックス付きで提供している項目名フォーマット用メッセージ）も含まれ、これらは
Spring標準の`ResourceBundleMessageSource`が理解しない`.default`上書き機構に依存しています。

つまりこの設定は「Springから何が見えるか」を決めるものであり、「`PropertiesFileUtil`自体がどう動くか」を変えるものではありません。

## `true`に切り替える場合

Spring Boot標準の`ResourceBundleMessageSource`は、classpath上にbasename
（`spring.messages.basename`、デフォルトは`"messages"`）が見つかった場合にのみ有効化されます。
ecuacionの各モジュールは`messages_splib_core.properties`のようなモジュール別の名前で
`messages.properties`を提供しており、素の`messages.properties`は存在しないため、
`spring.messages.basename`を明示的に設定する必要があります。設定しないと、Springは空の何もしない`MessageSource`（`DelegatingMessageSource`）にフォールバックし、全てのメッセージが解決されなくなります。

```properties
jp.ecuacion.splib.core.messages.use-spring-native=true
spring.messages.basename=messages_myapp
```

`true`に切り替えると、`MessageSource`側では`PropertiesFileUtil`独自の機能（`.default`/`.base`上書き階層、`#{fileKind:key}`相互参照）も失われます。

## サポートされない設定（デフォルトモードのみ）

`use-spring-native`が`false`（デフォルト）の場合、以下の`spring.messages.*`設定には
`PropertiesFileUtil`側に対応する仕組みがなく、黙って無視されるのではなく、理由付きで起動時に失敗します。

- `spring.messages.cache-duration`
- `spring.messages.fallback-to-system-locale`
- `spring.messages.encoding`
- `spring.messages.always-use-message-format`
- `spring.messages.use-code-as-default-message`（`=false`のみ拒否。`=true`は既存の固定挙動と一致するため許可されます）
- `spring.messages.common-messages`

これらが必要な場合は`use-spring-native=true`に切り替えてください。
