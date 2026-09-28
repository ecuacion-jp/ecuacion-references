## Overview

`itemNameKey` is a key used to look up the display name of an item from `item_names.properties` and similar files.
The format is `"classPart.fieldPart"` (e.g., `"user.name"`).

Unless explicitly specified on an `Item`, it is automatically determined based on the `itemPropertyPath` and class information.

---

## How to Specify It

`itemNameKey` is normally resolved automatically, but it can also be specified explicitly using `Item`'s `itemNameKey()`.

```java
// Specifying both class part + field part
new Item("mobilePhoneNumber.value").itemNameKey("mobilePhone.number")

// Specifying only the field part (class part is resolved automatically)
new Item("name").itemNameKey("fullName")
```

If it contains `"."`, it is interpreted as `"classPart.fieldPart"`; otherwise it is interpreted as field part only.

---

## itemNameKey Resolution Rules

`itemNameKey` is resolved in the following priority order (if 1 is unavailable, 2 is used; if 2 is unavailable, 3 is used).

1. The value specified via `itemNameKey()`
2. The `itemPropertyPath` (for the class part, if `@ItemNameKeyClass` is placed on the corresponding field, its value)
3. (Class part only) The `ItemContainer` class's class name (if `@ItemNameKeyClass` is placed on the class, its value)

Since `itemNameKey` and `itemPropertyPath` each have patterns with and without a class part specified, the class part and field part are determined independently of each other.

The concrete patterns are as follows.
Assume the object implementing ItemContainer is UserDto, and UserDto holds a DeptDto as a field (field name `dept`).

| itemNameKey() specification | itemPropertyPath | Resulting itemNameKey |
| --- | --- | --- |
| `"user.name"`<br>(class part + field part) | Any | `"user.name"`<br>(used as-is regardless of the itemPropertyPath) |
| `"fullName"`<br>(field part only) | `"dept.name"` | `"dept.fullName"`<br>(class part is the `dept` node's name, used as-is) |
| `"fullName"`<br>(field part only) | `"name"` | `"userDto.fullName"`<br>(class part resolved from the `ItemContainer`'s class name) |
| Not specified | `"name"` | `"userDto.name"`<br>(field part is the rightmost node, class part is the `ItemContainer`'s class name) |
| Not specified | `"dept.name"` | `"dept.name"`<br>(class part is the `dept` node's name, used as-is; field part is the rightmost node) |

When the itemPropertyPath has multiple levels, such as `dept.manager.name`, the field part is the rightmost node (`name`), and the class part is the node immediately to its left (`manager`).

---

## `@ItemNameKeyClass` Annotation

The previous section showed that when no class part is specified via `itemNameKey()` or `itemPropertyPath`, `userDto` is used by default as the class part of the itemNameKey.
In practice, though, `item_names.properties` is usually defined with a more generic form such as `user.name` rather than `userDto.name`.
This default class part `userDto` can be changed to `user` using `@ItemNameKeyClass` (`jp.ecuacion.lib.core.annotation.ItemNameKeyClass`).

`@ItemNameKeyClass` is an annotation that specifies the class part of `itemNameKey` in bulk for a class.
Without it, the class part defaults to the class name with the first letter lowercased, as described above.

```java
@ItemNameKeyClass("user")
public class UserDto implements ItemContainer {
    private String name;    // itemNameKey: "user.name"
    private String email;   // itemNameKey: "user.email"
}
```

### Placing It on a Field

`@ItemNameKeyClass` can also be placed on a field.
When the itemPropertyPath is nested, the field name is used as the class part as-is,
but for a field such as `List<DeptDto> deptList`, the class part becomes `deptList`, which forces you to define `deptList.name` in `item_names.properties`.
In such cases, placing `@ItemNameKeyClass` on the field changes the class part of all items under that field at once.

```java
public class UserDto implements ItemContainer {
    @ItemNameKeyClass("dept")
    private List<DeptDto> deptList;   // itemNameKey of deptList[0].name: "dept.name"

    private DeptDto belongingDept;    // itemNameKey of belongingDept.name: "belongingDept.name"
}
```

Placing it on a field takes effect only for the field corresponding to the class part of the itemPropertyPath (the second node from the right).
Also, if the class part is explicitly specified via `itemNameKey()`, that takes priority.

---

## Correspondence with item_names.properties

The resolved `itemNameKey` is used to look up the item name from `item_names.properties`.

```properties
# item_names.properties
userDto.name=Full Name
userDto.email=Email Address
user.name=Full Name
```
