## Fallback

Some file types fall back to another file type when a key is not found.
File types with a fallback configured work even without creating the corresponding file,
by falling back to the fallback target.
While splitting files into separate ones can be easier to manage, it is also common practice to write
everything in `messages.properties`, so fallback is configured to support that usage as well.

| File Name | Fallback Target |
| --- | --- |
| `application[_xxx].properties` | None |
| `constants[_xxx].properties` | None |
| `messages[_xxx].properties` | None |
| `messages_with_item_names[_xxx].properties` | `messages` |
| `item_names[_xxx].properties` | `messages` |
| `enum_names[_xxx].properties` | `messages` |
| `ValidationMessages[_xxx].properties` | None |
| `ValidationMessagesWithItemNames[_xxx].properties` | `ValidationMessages` |
| `ValidationMessagesPatternDescriptions[_xxx].properties` | None |

For example, even if you only prepare `messages.properties` without creating `item_names.properties`,
`getItemName(...)` will search for the key in `messages.properties` and return the result.

---

## Loading Files from Multiple Modules at Once

ecuacion assumes that applications are split into multiple modules (`base`, `core`, `web`, `batch`, etc.).
For example, if the application name is `sample-app`:

```text
sample-app-base  → messages_base.properties
sample-app-core  → messages_core.properties
sample-app-web   → messages.properties
```

`PropertiesFileUtil.getMessage(...)` searches all of the above at once.
If the same key is defined in multiple files, an **exception is thrown** (duplicate detection).

The application-specific suffixes such as `base`, `core`, and `web` above
are automatically detected and registered from the `spring.messages.basename` configuration
when using ecuacion-splib.
When not using ecuacion-splib or when building a custom module/framework,
call them manually.

```java
PropertiesFileUtil.addResourceBundlePostfix("mymodule");
// → messages_mymodule.properties, application_mymodule.properties, etc. are also added to the search targets
```

---

## Behavior When a Key Does Not Exist

| File Type | Behavior When Key Is Not Found |
| --- | --- |
| `application.properties` | Throws an exception |
| Others | Returns the key string as-is (no exception) |

Only `application.properties` throws an exception because you want to reliably detect missing configuration
values at application startup.
On the other hand, for `messages.properties` and similar files, it is more convenient during development
for undefined keys to be displayed on screen, so no exception is thrown.

---

## Overriding Default Values with `.default` Suffix

Keys provided by ecuacion modules have a `.default` suffix.
To override them in the application, define a key with the same name without `.default` in the application's file.

```properties
# File within an ecuacion module
some.key.default=ecuacion default value

# Application's file (override)
some.key=Application-specific value
```

`getApplication("some.key")` returns the application's value with priority.
