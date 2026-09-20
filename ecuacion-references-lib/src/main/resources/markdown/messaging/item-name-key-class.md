## Overview

`@ItemNameKeyClass` (`jp.ecuacion.lib.core.annotation.ItemNameKeyClass`) is an annotation that
specifies the class part of `itemNameKey` in bulk for a class.

Without this annotation, the class part defaults to the class name with the first letter lowercased.

```java
public class UserRecord implements ItemContainer { ... }
// Class part: "userRecord" (UserRecord with first letter lowercased)
```

By adding `@ItemNameKeyClass`, you can specify any string.

```java
@ItemNameKeyClass("user")
public class UserRecord implements ItemContainer { ... }
// Class part: "user"
```

---

## Usage

```java
@ItemNameKeyClass("user")
public class UserRecord implements ItemContainer {

    private String name;    // itemNameKey: "user.name"
    private String email;   // itemNameKey: "user.email"
    private String zipCode; // itemNameKey: "user.zipCode"

    @Override
    public Item[] customizedItems() {
        return new Item[] {};
    }
}
```

`item_names.properties`:

```properties
user.name=Full Name
user.email=Email Address
user.zipCode=Zip Code
```

---

## Comparison with and without @ItemNameKeyClass

| Setting | Class Part | Example item_names Key |
| --- | --- | --- |
| None | `userRecord` | `userRecord.name` |
| `@ItemNameKeyClass("user")` | `user` | `user.name` |

`@ItemNameKeyClass` is not needed when you want to use the class name as-is for the class part of the key.
Use it when you want a shorter name that differs from the class name or when matching an existing naming convention.

---

## Combination with ItemContainer

The `@ItemNameKeyClass` information is read inside `ItemContainer#getItem()` and
automatically reflected in the returned `Item`.
Even when `Item` instances are individually defined in `customizedItems()`,
`@ItemNameKeyClass` takes priority for specifying the class part.

However, if the class part is explicitly specified for an `Item` using `itemNameKey("cls.field")`,
that value takes the highest priority.

---

## Detailed Reference

- Overall itemNameKey automatic resolution rules → **[itemNameKey Resolution Rules](?id=item/item-name-key)**
- Integration with ItemContainer → **[ItemContainer](?id=item/item-container)**
