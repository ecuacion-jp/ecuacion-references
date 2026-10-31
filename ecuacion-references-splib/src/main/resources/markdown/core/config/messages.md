`messages.properties` resolution has two modes, controlled by one setting:

```properties
jp.ecuacion.splib.core.messages.use-spring-native=false
```

| Setting | `MessageSource` bean | What it means |
| --- | --- | --- |
| `false` (default) | `PropertiesFileUtilMessageSource`, backed by `PropertiesFileUtil` | `.default`/`.base` module-override hierarchy, `#{fileKind:key}` cross-references, guaranteed UTF-8 reading — ecuacion's own resolution |
| `true` | Spring Boot's standard `ResourceBundleMessageSource` | Plain, unmodified Spring Boot behavior; every `spring.messages.*` setting works as Spring Boot itself documents it |

This setting only changes what Spring's own `MessageSource` bean does — the object consulted
by Thymeleaf's `#{...}` message expressions, Spring MVC's own message resolution, and any code
that injects `MessageSource` and calls `getMessage(...)` on it.

## What is *not* affected by this setting

Calling `PropertiesFileUtil.getMessage(...)` (or `getItemName`/`getConstant`/`getEnumName`)
directly always uses `PropertiesFileUtil`'s own resolution, regardless of this setting. This
includes messages built into ecuacion modules themselves (e.g. the item-name formatting
messages `ecuacion-lib-core` ships under a `.default` suffix), which rely on the `.default`
override mechanism Spring's own `ResourceBundleMessageSource` does not understand.

In other words: this setting decides what Spring sees, not what `PropertiesFileUtil` does.

## Switching to `true`

Spring Boot's `ResourceBundleMessageSource` only activates if it can find a basename on the
classpath (`spring.messages.basename`, default `"messages"`). Because ecuacion modules ship
their `messages.properties` under module-specific names (e.g. `messages_splib_core.properties`,
never a bare `messages.properties`), you must set `spring.messages.basename` explicitly —
otherwise Spring falls back to an empty no-op `MessageSource` (`DelegatingMessageSource`), and
every message resolves to nothing.

```properties
jp.ecuacion.splib.core.messages.use-spring-native=true
spring.messages.basename=messages_myapp
```

Switching to `true` also trades away `PropertiesFileUtil`'s own features on the
`MessageSource` side: the `.default`/`.base` override hierarchy and `#{fileKind:key}`
cross-references no longer apply to messages resolved through `MessageSource`.

## Unsupported settings (default mode only)

When `use-spring-native` is `false` (the default), the following `spring.messages.*` settings
have no `PropertiesFileUtil` equivalent and cause a startup failure with an explanatory
message, rather than being silently ignored:

- `spring.messages.cache-duration`
- `spring.messages.fallback-to-system-locale`
- `spring.messages.encoding`
- `spring.messages.always-use-message-format`
- `spring.messages.use-code-as-default-message` (only `=false` is rejected; `=true` matches
  the existing fixed behavior)
- `spring.messages.common-messages`

Switch to `use-spring-native=true` if you need any of these.

## Message language (splib-web / splib-rest)

Messages and item names produced by splib-web and splib-rest (validation errors, business errors,
delete confirmations, REST error responses, etc.) are resolved in the locale Spring MVC determines
for the request through its `LocaleResolver` (`LocaleContextHolder.getLocale()`). This is the same
locale Thymeleaf's `#{...}` and `#locale` use, so a page never mixes two languages.

By default Spring Boot's `AcceptHeaderLocaleResolver` is used, so the language follows the
browser's `Accept-Language` header. To change it — for example, to the language a user chose on a
profile page — define your own `LocaleResolver` bean. It must be named `localeResolver`, the name
`DispatcherServlet` looks it up by:

```java
@Bean
LocaleResolver localeResolver() {
  // e.g. keeps the language in the session; set it with
  // localeResolver.setLocale(request, response, locale) when the user changes it
  return new SessionLocaleResolver();
}
```

`ecuacion-splib-web-markdown` registers a `SessionLocaleResolver` for its language switch, but only
when the application does not define its own `localeResolver` bean.
