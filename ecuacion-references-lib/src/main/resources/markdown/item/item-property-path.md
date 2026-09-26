## item

An `item` is a concept defined by this library, representing a single "item" on screen, such as an input field or a display field.
Its value itself is held as a field on an object, but displaying it also requires attributes beyond the value, such as the label text or whether it is required.
An `Item` is an object that holds these attribute values for a single field (see [Item](?id=item/item) for details).

In this library (ecuacion-lib), `Item` and its related classes are used solely to generate error messages for Jakarta Validation
and `BusinessViolation` (for details, see [Overview of Violations and Exceptions](?id=concepts/violations-overview)).
In other libraries that build on this library, such as ecuacion-splib, they are also used for on-screen display, such as display labels.

---

## itemPropertyPath

`itemPropertyPath` is a string path that represents the location of an item.
It plays the same role as propertyPath, but adds features for working with items, such as a simplified notation and use with itemNameKey (described later).

It uses the same dot notation as propertyPath.
If you don't use `ItemContainer` (described later), it is exactly the same as propertyPath.

| itemPropertyPath | Meaning |
| --- | --- |
| `"name"` | The `name` field directly under the root |
| `"dept.name"` | The `name` field of the `dept` object held by the root |
| `"bookList[1].title"` | The `title` field of the second element of `bookList` |

---

## ItemContainer

`ItemContainer` (`jp.ecuacion.lib.core.item.ItemContainer`) is an interface implemented by an object that holds items.
It is implemented on classes such as a Form or the DTO directly beneath it (for details, see [ItemContainer](?id=item/item-container)).

To customize an item, the object holding that item must implement `ItemContainer`.
Here's an example of setting an attribute on an item using `ItemContainer`.

Jakarta Validation has a feature that displays the value that caused the error by including the placeholder
`{invalidValue}` in a message. For sensitive items, specifying `Item`'s `hideValue()` lets you suppress the value
from appearing in that `{invalidValue}` part. Here, we implement `ItemContainer`'s abstract method
`customizedItems()` and specify `hideValue()` on the `password` field.

```java
public class UserDto implements ItemContainer {

    @Size(min = 8, max = 20)
    private String password;

    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("password").hideValue()
        };
    }
}
```

---

## Base Object of itemPropertyPath

For example, if the rootBean is `UserForm` and it has a direct child `UserDto` (with `name` and `address` fields), the standard
Jakarta Validation propertyPath (hereafter called fullPropertyPath) would be `"userDto.name"`.

In other words, `customizedItems()` would need to be defined like this.

```java
// Written with fullPropertyPath (from UserForm's perspective, verbose)
new Item("userDto.name"), new Item("userDto.address"), ...
```

Since that's verbose, the `itemPropertyPath` you specify on `Item` is written as a path based on `ItemContainer`.

```java
// Written with itemPropertyPath (from UserDto's perspective)
new Item("name"), new Item("address"), ...
```

The base object for `itemPropertyPath` is determined by the following rules depending on whether an `ItemContainer` is present.

| Situation | Base of itemPropertyPath |
| --- | --- |
| No `ItemContainer` | rootBean |
| rootBean itself is an `ItemContainer` | rootBean (= ItemContainer) |
| A direct child of rootBean is an `ItemContainer` | That child ItemContainer |

The search for `ItemContainer` is **up to 1 level deep**.
An `ItemContainer` nested more than 2 levels deep, such as `rootBean.dept.record`, is not discovered.

In the example above, if `UserDto` is an `ItemContainer` (i.e., `UserDto` implements `ItemContainer`),
then for a fullPropertyPath of `"userDto.name"`, `"name"` becomes the itemPropertyPath.

By sharing the same base between the template side (Thymeleaf, etc.) and the backend, both the amount of code and readability improve.

For how to write `itemPropertyPath` (including shorthand forms) for collection fields such as List, Set, and Map,
see [propertyPath in Collections](?id=item/collection-property-path).

---

## Note: Why Sibling ItemContainers Don't Cause Ambiguity

For example, if `UserForm` has two direct children, `UserDto` and `DeptDto`, both `ItemContainer`s and both with a `name` field,
you might wonder whether writing `itemPropertyPath` as simply `"name"` would leave it unclear which `ItemContainer` is meant.
In practice, this ambiguity does not arise, for two reasons corresponding to the two ways `itemPropertyPath` is used:

1. **When displaying validation messages** (starting from the field where the violation occurred): Jakarta Validation itself has already
   identified the field where the violation occurred by walking from the rootBean. The search for an `ItemContainer` starts from that
   already-identified fullPropertyPath (e.g., `"userDto.name"`), so it is never reduced to an ambiguous bare `"name"`.
2. **When displaying item names via Thymeleaf in splib** (walking from the rootBean toward the target item in order): even though the
   `itemPropertyPath` written on each component may be in shorthand form (e.g., `"name"`), the part that was shortened (i.e., which
   `ItemContainer`'s scope it belongs to) is specified separately elsewhere, so the component as a whole can still uniquely identify its target.
