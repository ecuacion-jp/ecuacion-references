## Overview

`Violations.MessageParameters` is a parameter class that controls how error messages are generated.
It can be configured using `withMessageParameters()` in method chain format.

```java
violations.withMessageParameters(p -> p
    .isMessageWithItemName(true)
    .messagePrefix("upload.row.prefix"));
```

The primary use case is when you want to build informative error messages outside of screen display
(e.g., validating an uploaded Excel file).

---

## isMessageWithItemName — Switching Between Messages With/Without Item Names

Controls whether validation error messages are retrieved from `messages.properties` or
`messages_with_item_names.properties`.

| Value | Behavior |
| ------ | ---- |
| `null` (default) | Follows the value of `isMessagesWithItemNamesAsDefault` in `ExceptionUtil.getMessageList()` |
| `true` | Uses messages with item names |
| `false` | Uses messages without item names |

When `null`, the fallback is the `isMessagesWithItemNamesAsDefault` parameter of `ExceptionUtil.getMessageList()`.
Frameworks such as splib set this value as the system default.
When `isMessageWithItemName` is explicitly set to `true` / `false`, it takes priority over `isMessagesWithItemNamesAsDefault`.

For details, see **util > ExceptionUtil**.

```java
violations.withMessageParameters(p -> p.isMessageWithItemName(true));
```

**Example with item names:**

```properties
# messages_with_item_names.properties
error.required = {item_name} is required
```

When `isMessageWithItemName(true)`, the item name of the field is embedded at `{item_name}`.
`Item` / `ItemUtil` are used to resolve the item name (see util > Item for details).

---

## showsItemNamePath — Displaying the Item Name Path

Controls whether to include position information (path) in the message for validation errors
on elements within a collection (list, etc.).

| Value | Display Example |
| ------ | ------ |
| `false` (default) | `name is required` |
| `true` | `name of the 1st element of list is required` |

```java
violations.withMessageParameters(p -> p.showsItemNamePath(true));
```

---

## messagePrefix / messagePostfix — Adding Text Before/After Messages

Appends fixed text before or after each error message.
Used for displays like "In the uploaded file, ○○ is required" in Excel upload validation.

### String Version — Specify as Literal or Key

The string passed to `messagePrefix(String)` is first resolved as a key in `messages.properties`.
If found, its value is used; if not, the passed string is used as-is.

```properties
# messages.properties
excel.upload.prefix = In the uploaded Excel file,
```

```java
// Key specification (retrieves value from messages.properties)
violations.withMessageParameters(p -> p.messagePrefix("excel.upload.prefix"));
// → "In the uploaded Excel file, 'Full Name' is required"

// Literal string (key not found, used as-is)
violations.withMessageParameters(p -> p.messagePrefix("Row 2: "));
// → "Row 2: 'Full Name' is required"
```

### Arg Version — Dynamic Values Including Placeholders

When the text contains placeholders (`{0}`, etc.) or embeds runtime values, use `Arg.message()`.

```properties
# messages.properties
upload.error.row.prefix = Row {0}:
```

```java
violations.withMessageParameters(p -> p
    .messagePrefix(Arg.message("upload.error.row.prefix", rowNumber)));
// → "Row 3: 'Full Name' is required"
```

`messagePostfix` works the same way.

---

## Combined Example

An example of validating an Excel file with the row number as prefix and including item names in messages.

```java
for (int i = 0; i < rows.size(); i++) {
  int rowNum = i + 2; // Row number excluding the header row
  new Violations()
      .validate(rows.get(i))
      .withMessageParameters(p -> p
          .isMessageWithItemName(true)
          .showsItemNamePath(true)
          .messagePrefix(rowNum + ": "))
      .throwIfAny();
}
```
