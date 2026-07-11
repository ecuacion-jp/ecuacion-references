# Item and ItemNameKey

This describes the mechanism for item name keys used in validation error messages.

---

## itemNameKey

A key used to look up the display name of an item from `item_names.properties`.
The format is `"classPart.fieldPart"` (e.g., `"user.name"`), which corresponds to keys in `item_names.properties`.

```properties
# item_names.properties
user.name=Full Name
user.email=Email Address
```

`itemNameKey` is normally resolved automatically, but can also be specified explicitly on an `Item`.
For details on the automatic resolution rules, see [itemNameKey Resolution Rules](?id=item/item-name-key).

---

## @ItemNameKeyClass

An annotation that specifies the default value of the class part of `itemNameKey` in bulk.

For details, see [@ItemNameKeyClass](?id=messaging/item-name-key-class).

---

## itemNamePath

A feature that appends information about which nesting level a validation error occurred at
to the item name when an error occurs in a nested object.

```text
null is not allowed for 'zipCode' of 'Address'
```

For details, see [itemNamePath](?id=messaging/item-name-path).
