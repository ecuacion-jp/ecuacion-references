`rowAddableTable` in `bootstrap/components-edit.html` is a table whose rows can be added and
removed on the page. Use it to edit one-to-many child rows bound to a `List` in the root record
(e.g. display names by language) together on one page. The application only writes the header
cells and the cells of one row; splib takes care of the table frame, the "add" / "remove"
buttons, the template of a new row, and renumbering.

## Usage

Write the header cells and the cells of one row as fragments, and pass them as arguments of
`rowAddableTable`. In the cells of a row, use the input components with the `cell` option.

```html
<div layout:fragment="edit-fields-content" th:remove="tag">
	<div th:replace="~{bootstrap/components-edit :: rowAddableTable('nameRows', ~{::nameHeader}, ~{::nameRow}, '')}"></div>
</div>

<!-- Write them outside layout:fragment -->
<th:block th:fragment="nameHeader">
	<th th:replace="~{bootstrap/components-edit :: rowAddableTableHeaderCell('nameRows[].lang', '')}"></th>
	<th th:replace="~{bootstrap/components-edit :: rowAddableTableHeaderCell('nameRows[].name', '')}"></th>
</th:block>

<th:block th:fragment="nameRow">
	<td><div th:replace="~{bootstrap/components-input :: inputSelect(${rowPath + 'lang'}, 'cell')}"></div></td>
	<td><div th:replace="~{bootstrap/components-input :: inputText(${rowPath + 'name'}, 'cell')}"></div></td>
</th:block>
```

```java
public class TargetSystemEditRecord extends TargetSystemBaseRecord implements HtmlItemContainer {

  private List<TargetSystemNameRow> nameRows = new ArrayList<>();

  // Options of inputSelect. The options of nameRows[3].lang come from getNameRows_LangList(),
  // the name without the index.
  public List<String[]> getNameRows_LangList() { ... }
  ...
}
```

Write the fragment definitions (`nameHeader`, `nameRow`) outside `layout:fragment`. Otherwise
they are also rendered where they are written. Since the fragments are passed as arguments, you
can put two or more tables on one page.

## Arguments

| Argument | Description |
| --- | --- |
| `itemPropertyPath` | The itemPropertyPath of the list from the root record (e.g. `nameRows`). |
| `headerContents` | The `<th>`s of the header.<br>Don't write the one for the "remove" button column; splib adds it. |
| `rowContents` | The `<td>`s of one row.<br>Don't write the one for the "remove" button column; splib adds it. |
| `options` | The options below.<br>Pass `''` when you don't need any. |

The following variables are available in `rowContents`.

| Variable | Description |
| --- | --- |
| `rowPath` | The itemPropertyPath of the row with a trailing `.`.<br>It is `nameRows[3].` in an existing row, and `nameRows[].` (no index) in the template of a new row. |
| `row` | The element of the list for the row.<br>It is `null` in the template. |
| `isRowTemplate` | `true` while the template is rendered. |

## Options

| Option | Description |
| --- | --- |
| `readonly` | Doesn't show the "add" and "remove" buttons. |
| `minRows` | Disables the "remove" buttons when the number of rows is this or less. |
| `maxRows` | Disables the "add" button when the number of rows is this or more. |
| `addButtonLabel` | The label of the "add" button (a message key or a string). |
| `removeButtonLabel` | The label of the "remove" buttons (a message key or a string). |

The default labels are `jp.ecuacion.splib.web.common.button.addRow` and
`jp.ecuacion.splib.web.common.button.removeRow`. Writing the same keys (without `.default`) in
the application's `item_names.properties` replaces them in all pages. To change them for one
page, use `addButtonLabel` / `removeButtonLabel`.

## Input components in cells (`cell` option)

Input components (`inputText`, `inputSelect`, etc.) are usually wrapped in a column div
(`col-...`) with a label and a bottom margin. With the `cell` option, they show only the input
and its field error messages, which fits in a table cell. Item names are shown in the header.

`rowAddableTableHeaderCell` is a `<th>` which shows the item name and the required mark (`*`)
in the same way as the labels of the input components. Pass the itemPropertyPath without indexes
(e.g. `nameRows[].name`). The required mark is shown when `notEmpty()` is specified for the
`HtmlItem` with the path without indexes (e.g. `new HtmlItemString("nameRows[].name").notEmpty()`).
The key of the item name is `<class part>.<field name>`. The class part is the value of
`@ItemNameKeyClass` on the list field, or the name of the list field if it is not specified
(e.g. `nameRows.name`).

If you don't want to write the item names in `item_names.properties`, you can write plain HTML
like `<th th:text="#{...}">` instead.

## Adding and removing rows, and renumbering

The template of a new row is rendered once in a `<template>` below the table. Its names and ids
contain `nameRows[]` without an index. The content of a `<template>` is not submitted with the
form, so the template itself is never sent.

When "add" or "remove" is pressed, `static/js/splib-web.js` renumbers `nameRows[...]` in the
`name`, `id` and label `for` attributes of the rows from 0. So even if a row in the middle is
removed, the server receives the list without gaps. Only the indexes of the table's own list
are renumbered; those of other tables or of nested paths in the rows are left as they are.

The ids of the input components contain the path of the row by default (e.g.
`xxxEditForm.xxx.nameRows[3].name`), so they don't collide between rows. If you write ids in
plain HTML in the cells, include the path of the row in the same way. Otherwise the ids collide
with those of added rows.

## Field errors of each row

Field errors are shown with paths with indexes. When you pass a path with an index, seen from the
form, to `BusinessViolation`, the message is shown below the input of the row and the input
turns red (when messages are shown at each item; see
[Exception Handling](page?id=web/exception-handling&lang=en)). The index is that of the list
sent from the page (don't recount it, e.g. by skipping empty rows).

```java
for (int i = 0; i < rows.size(); i++) {
  TargetSystemNameRow row = rows.get(i);
  if (StringUtils.isEmpty(row.getName())) {
    violations.add(new BusinessViolation(
        new String[] {"targetSystem.nameRows[" + i + "].name"}, "MSG_NAME_REQUIRED"));
  }
}
violations.throwIfAny();
```

## Notes

- When all rows are removed and the form is sent, no parameter of the list is sent. The list
  then stays as the initial value of the received record. In applications which don't create
  the record for every request (e.g. keep it in the session), the previous list may remain.
- Ignore rows that are not entered (e.g. both the language and the display name are empty) in
  the application (a service, etc.).
- Scripts in `rowContents` (e.g. the ones added by the `event` option) don't work in added rows.
