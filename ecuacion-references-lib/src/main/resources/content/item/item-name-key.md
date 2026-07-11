# itemNameKey Resolution Rules

## Overview

`itemNameKey` is a key used to look up the display name of an item from `item_names.properties` and similar files.
The format is `"classPart.fieldPart"` (e.g., `"user.name"`).

Unless explicitly specified on an `Item`, it is automatically determined based on the `itemPropertyPath` and class information.

---

## Field Part Resolution Rules

The field part is determined by the following priority order.

| Priority | Condition | Field Part Value |
| :---: | --- | --- |
| 1 | Field part explicitly specified with `itemNameKey()` | Specified value |
| 2 | Otherwise | Rightmost node of `itemPropertyPath` (excluding collection part) |

Examples of rightmost node:

| itemPropertyPath | Rightmost Node → Field Part |
| --- | --- |
| `"name"` | `"name"` |
| `"dept.name"` | `"name"` |
| `"item.property.path"` | `"path"` |
| `"strList[0].<list element>"` | `"strList"` |

---

## Class Part Resolution Rules

The class part is determined by the following priority order.

| Priority | Condition | Class Part Value |
| :---: | --- | --- |
| 1 | Class part explicitly specified with `itemNameKey("cls.field")` | Specified value |
| 2 | The class the field belongs to has a `@ItemNameKeyClass` annotation | The annotation's value (first letter lowercased) |
| 3 | Otherwise | Class name set by `ItemContainer#getItem()` (first letter lowercased) |

---

## Concrete Example

Using the following class structure as an example:

```java
public class UserRecord implements ItemContainer {
    private String name;
    private String email;
    // ...
}
```

| Configuration | itemNameKey |
| --- | --- |
| No configuration (`"name"` as itemPropertyPath) | `"userRecord.name"` |
| `.itemNameKey("fullName")` | `"userRecord.fullName"` |
| `.itemNameKey("person.fullName")` | `"person.fullName"` |

---

## `@ItemNameKeyClass` Annotation

Used when you want to specify a class part that differs from the class name in bulk.
For details, see [@ItemNameKeyClass](?id=messaging/item-name-key-class).

---

## Correspondence with item_names.properties

The resolved `itemNameKey` is used to look up the item name from `item_names.properties`.

```properties
# item_names.properties
userRecord.name=Full Name
userRecord.email=Email Address
user.name=Full Name
```
