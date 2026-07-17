## Overview

`Item` (`jp.ecuacion.lib.core.item.Item`) is a class that holds the attributes of a single field.
It is primarily used to control how item names and field values are displayed in validation error messages.

Normally, `Item` instances are not created directly by application developers;
they are obtained via `ItemUtil.resolveItem()` or `ItemContainer.getItem()`.

You create instances directly only when implementing `customizedItems()` in an [ItemContainer](?id=item/item-container)
to customize the display behavior of fields.

```java
@Override
public Item[] customizedItems() {
    return new Item[] {
        new Item("password").hideValue(),
        new Item("name").itemNameKey("fullName")
    };
}
```

---

## Explicitly Specifying itemNameKey

`itemNameKey` is the key used to look up the item name in error messages from `item_names.properties` and similar files
(for details, see [itemNameKey Resolution Rules](?id=item/item-name-key)).

It is normally resolved automatically, but can also be specified explicitly.

```java
// Specifying both class part + field part
new Item("mobilePhoneNumber.value").itemNameKey("mobilePhone.number")

// Specifying only the field part (class part is resolved automatically)
new Item("name").itemNameKey("fullName")
```

If it contains `"."`, it is interpreted as `"classPart.fieldPart"`; otherwise it is interpreted as field part only.

---

## Hiding Values

Use `hideValue()` for fields such as passwords where you do not want to include the value in error messages.

```java
new Item("password").hideValue()
```

The default is to show the value (`showsValue = true`).

---

## Method Chaining

Each method returns `this`, so they can be written as method chains.

```java
Item item = new Item("password")
    .itemNameKey("account.password")
    .hideValue();
```

---

## Summary

| Method | Description |
| --- | --- |
| `.itemNameKey(key)` | Explicitly specifies the itemNameKey (auto-resolved when omitted) |
| `.hideValue()` | Hides the value in error messages |
