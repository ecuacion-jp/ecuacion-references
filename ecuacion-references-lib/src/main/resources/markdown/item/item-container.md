## Overview

`ItemContainer` (`jp.ecuacion.lib.core.item.ItemContainer`) is an interface that holds `Item` instances
with customized display attributes for fields.

By implementing it on classes such as Records or Forms, you can centrally manage the item name key (`itemNameKey`)
and value visibility for the fields belonging to that class.

---

## How to Implement

To implement `ItemContainer`, define `customizedItems()`.

```java
public class UserRecord implements ItemContainer {

    private String name;
    private String password;

    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("password").hideValue(),
        };
    }
}
```

Fields that do not require customization do not need to be included in `customizedItems()`.
If `getItem()` cannot find a customized `Item`, it returns an auto-generated `Item`.

If no customization is needed at all, return an empty array.

```java
@Override
public Item[] customizedItems() {
    return new Item[] {};
}
```

---

## getItem()

`getItem(itemPropertyPath)` returns the `Item` corresponding to the specified path.

```java
Item item = userRecord.getItem("password");
```

Internal behavior:

1. Converts `itemPropertyPath` to a form with collection indices removed (`toIndexlessPath`)
2. Traverses up the class hierarchy, collecting and merging `Item` instances whose `propertyPath` matches from each level's `customizedItems()` (details below)
3. If not found, generates and returns `new Item(propertyPath)`
4. Reads `@ItemNameKeyClass` annotation information from the class the field belongs to, and sets it on the `Item`

As described in [What is itemPropertyPath](?id=item/item-property-path),
the `itemPropertyPath` passed to `getItem()` is a **relative path from the ItemContainer itself**.

---

## Inheriting Settings from Parent Classes

When a class implementing `ItemContainer` overrides `customizedItems()`,
`getItem()` traverses up the class hierarchy and merges `Item` instances with the same `propertyPath`.
Properties explicitly set in the child class take priority, and unset properties inherit the parent class's settings.

```java
// Parent class: sets itemNameKey and hideValue
public class UserRecord implements ItemContainer {
    private String name;
    private String password;

    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("name").itemNameKey("user.name"),
            new Item("password").itemNameKey("user.password").hideValue(),
        };
    }
}

// Child class: overrides itemNameKey for name depending on the screen
public class EditUserRecord extends UserRecord {
    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("name").itemNameKey("editUser.name"),
        };
    }
}
```

When calling `getItem("name")` on an instance of `EditUserRecord`:

- `itemNameKey` → `"editUser.name"` (child class setting takes priority)
- `showsValue` → `true` (not set in the parent either, so the default value is used)

When calling `getItem("password")`:

- `itemNameKey` → `"user.password"` (inherited from parent class)
- `showsValue` → `false` (inherits `hideValue()` from parent class)

If the child class does not override `customizedItems()` at all,
the parent class settings are simply used as-is.

> **Module Constraint**: This feature uses Java's `MethodHandles` to call each class hierarchy's
> `customizedItems()` individually.
> In unnamed module environments such as Spring Boot fat JARs, it works automatically,
> but in named module environments, you need to add `opens` to `module-info.java` as follows:
>
> ```java
> module com.example.myapp {
>     requires jp.ecuacion.lib.core;
>     opens com.example.myapp.record to jp.ecuacion.lib.core;
> }
> ```

---

## mergeItems()

A utility method that combines a common `Item[]` with a Record-specific `Item[]`.

```java
public class UserRecord implements ItemContainer {

    private static final Item[] COMMON_ITEMS = new Item[] {
        new Item("createdAt").itemNameKey("common.createdAt"),
        new Item("updatedAt").itemNameKey("common.updatedAt"),
    };

    @Override
    public Item[] customizedItems() {
        return mergeItems(COMMON_ITEMS, new Item[] {
            new Item("password").hideValue(),
        });
    }
}
```

If both arrays contain an `Item` with the same `propertyPath`, a `RuntimeException` is thrown.

---

## ItemContainer Search Scope

`ItemUtil.resolveItem()` is a method that finds the ItemContainer based on the rootBean and propertyPath
and resolves the `Item`. It is called internally by the framework when generating validation error messages
(for details, see [ItemUtil](?id=item/item-util)).

The search scope is **up to 1 level from the rootBean**.
