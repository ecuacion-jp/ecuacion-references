# itemPropertyPath

## Overview

`itemPropertyPath` is a string path that represents the location of a field within an object.
It is used to identify "which item caused the error" in validation error messages and similar contexts.

It uses the same dot notation as propertyPath.

| itemPropertyPath | Meaning |
| --- | --- |
| `"name"` | The `name` field directly under the root |
| `"dept.name"` | The `name` field of the `dept` object held by the root |
| `"bookList[1].title"` | The `title` field of the second element of `bookList` |
| `"strList[0].<list element>"` | The element itself of `strList` (`List<String>`) |

---

## Base Object

`ItemContainer` (`jp.ecuacion.lib.core.item.ItemContainer`) is an interface that holds `Item` instances
with customized display attributes for fields. It is implemented on classes such as Records or Forms
(for details, see [ItemContainer](?id=item/item-container)).

The base object for `itemPropertyPath` is determined by the following rules depending on whether an `ItemContainer` is present.

| Situation | Base of itemPropertyPath |
| --- | --- |
| No `ItemContainer` | rootBean |
| rootBean itself is an `ItemContainer` | rootBean (= ItemContainer) |
| A direct child of rootBean is an `ItemContainer` | That child ItemContainer |

The search for `ItemContainer` is **up to 1 level deep**.
An `ItemContainer` nested more than 2 levels deep, such as `rootBean.dept.record`, is not automatically discovered.

For example, if the rootBean is `SomeForm` and it has a direct child `UserRecord` (ItemContainer),
for a fullPropertyPath of `"userRecord.name"`,
`"name"` is passed as the `itemPropertyPath` to `UserRecord#getItem()`.

---

## Reason for Existence

In web UIs, field-by-field attributes (item name key, value display control, etc.) are defined in `ItemContainer.customizedItems()`.
In a configuration where a form holds a DTO (`UserForm` holds `UserDto`, and `UserDto` holds `name` and `address`),
writing paths from the `UserForm` perspective becomes verbose.

```java
// Written from UserForm perspective (verbose)
new Item("userDto.name"), new Item("userDto.address"), ...
```

Since `itemPropertyPath` uses `ItemContainer` (in this case `UserDto`) as the base, it can be written concisely.

```java
// Written from UserDto perspective (itemPropertyPath)
new Item("name"), new Item("address"), ...
```

By sharing the same base between the template side (Thymeleaf, etc.) and the backend, both the amount of code and readability improve.

---

## Writing itemPropertyPath and Shorthand Forms

The `itemPropertyPath` passed to `new Item()` can be specified either in the format matching Jakarta Validation's `propertyPath` (with indices)
or in the shorthand form with indices omitted.
Both are normalized internally, so either form produces the same result.

```java
// Both have the same meaning
new Item("bookList[1].title")   // propertyPath-compliant form (with index)
new Item("bookList[].title")    // shorthand form
```

The table below summarizes the propertyPath-compliant and shorthand forms by collection type.
All field names are for illustration purposes. `User` is a class with a `name` field.

### List

| Field Type | Target | propertyPath-compliant Form | Shorthand Form |
| --- | --- | --- | --- |
| `List<String> strList` | Element itself | `strList[0].<list element>` | `strList[]` |
| `List<User> userList` | `User.name` | `userList[0].name` | `userList[].name` |
| `List<List<String>> nestedList` | Inner String element | `nestedList[0].<list element>[1].<list element>` | `nestedList[][]` |
| `List<List<User>> nestedList` | Inner `User.name` | `nestedList[0].<list element>[1].name` | `nestedList[][].name` |

### Set

Since sets do not have ordering, indices do not appear in runtime paths.
There are many cases where the form does not change between before and after normalization.

| Field Type | Target | propertyPath-compliant Form | Shorthand Form |
| --- | --- | --- | --- |
| `Set<String> strSet` | Element itself | `strSet[].<iterable element>` | `strSet[]` |
| `Set<User> userSet` | `User.name` | `userSet[].name` | `userSet[].name` (same) |

### Map

The propertyPath for map key constraints contains a qualifier indicating key access,
which is not removed after normalization. Therefore, the `itemPropertyPath` for keys and values are distinguished.

| Field Type | Target | propertyPath-compliant Form | Shorthand Form |
| --- | --- | --- | --- |
| `Map<String, ?> strMap` | Key itself | `strMap<K>[].<map key>` | `strMap<K>[]` |
| `Map<?, String> strMap` | Value itself | `strMap[key1].<map value>` | `strMap[]` |
| `Map<?, User> strMap` | Value's `User.name` | `strMap[key1].name` | `strMap[].name` |

### Duplicate Registration

Paths with different indices become the same key after normalization, so registering duplicates
within the same `customizedItems()` will cause a runtime exception.

```java
// NG: Both become "userList[].name" after normalization, causing duplicates
new Item("userList[1].name"),
new Item("userList[2].name")
```

---

## Collection Element Keywords

These are the keywords that appear when referring to the element of a collection itself.

| Type | Keyword |
| --- | --- |
| `List<T>` | `<list element>` |
| `Set<T>` | `<iterable element>` |
| Key of `Map<K, V>` | `<map key>` |
| Value of `Map<K, V>` | `<map value>` |
