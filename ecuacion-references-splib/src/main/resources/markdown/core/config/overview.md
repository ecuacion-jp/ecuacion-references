`PropertiesFileUtil` (`jp.ecuacion.lib.core.util.PropertiesFileUtil`) is ecuacion's own
`*.properties` reading utility, used across `application.properties`, `messages.properties`,
and `ValidationMessages.properties`. Because `ecuacion-lib` itself has no dependency on
Spring, each of these three integrates with Spring Boot differently — how much of "Spring's
own way" vs "PropertiesFileUtil's own way" applies depends on which file kind you're looking
at. This page summarizes the three; the following pages cover each in more detail.

## The three file kinds, side by side

| File kind | Who owns resolution | Calling `PropertiesFileUtil` directly (`getApplication`/`getMessage`/etc.) | Going through Spring (`@Value`, `Environment`, `MessageSource`, Bean Validation) |
| --- | --- | --- | --- |
| `application.properties` | Spring, always | Delegates to Spring's `Environment` — identical result | This *is* the source |
| `messages.properties` | `PropertiesFileUtil` by default; Spring, opt-in | Always `PropertiesFileUtil`'s own resolution, regardless of the setting | Configurable — see [messages.properties](page?id=core/config/messages&lang=en) |
| `ValidationMessages.properties` | `PropertiesFileUtil`, always | Always `PropertiesFileUtil`'s own resolution | No Spring-facing swap point exists |

## Why they differ

It looks inconsistent at first glance that `application.properties` fully hands resolution
over to Spring — even when calling `PropertiesFileUtil.getApplication()` directly — while
`messages.properties`'s Spring-native toggle affects only Spring's own `MessageSource` bean,
leaving `PropertiesFileUtil.getMessage()` unaffected either way. The difference comes down to
what each side would gain or lose by delegating.

- **`application.properties`**: ecuacion modules are not designed to ship their own
  `application.properties` in the first place — this file kind sits outside the
  `.default`/`.base` cross-module override scheme that `messages.properties` uses. Spring's
  `Environment` is a strict superset: it covers everything the classpath file would, plus
  externalized files, environment variables, profiles, and `-D` system properties. Handing
  this one over entirely costs little (see [application.properties](page?id=core/config/application-properties&lang=en)
  for the one caveat, `#{...}` cross-reference) and gains a lot, so it applies
  unconditionally — there's no setting for it.
- **`messages.properties`**: `PropertiesFileUtil`'s `.default`/`.base` cross-module override
  and `#{fileKind:key}` cross-reference are real, actively used features — including by
  ecuacion's own built-in messages. Spring's standard `ResourceBundleMessageSource` has no
  equivalent for either. Handing this over would be a genuine loss of functionality, so it's
  opt-in, and scoped narrowly to the Spring-facing `MessageSource` bean — the one integration
  surface where "plain Spring Boot behavior" actually matters (Thymeleaf message expressions,
  Spring MVC's own message resolution, etc.). Direct `PropertiesFileUtil.getMessage()` calls
  are unaffected either way.
- **`ValidationMessages.properties`**: `PropertiesFileUtil`'s own resolution is already
  upper-compatible with what Jakarta Bean Validation's own message interpolation offers — see
  [ValidationMessages.properties](page?id=core/config/validation-messages&lang=en) — so
  there's simply no need for a toggle here.

## Further reading

- [application.properties](page?id=core/config/application-properties&lang=en)
- [messages.properties](page?id=core/config/messages&lang=en)
- [ValidationMessages.properties](page?id=core/config/validation-messages&lang=en)
