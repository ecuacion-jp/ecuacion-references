`bootstrap/components-edit.html` の `rowAddableTable` は、画面で行を足したり消したりできる表です。ルートレコードの `List` に結び付く 1 対多の子の行（例：言語ごとの表示名）を、1 つの画面でまとめて編集するときに使います。アプリはヘッダのセルと 1 行分のセルを書くだけで、表の外枠・「追加」「削除」ボタン・行を足すときの雛形・番号の振り直しは splib が引き受けます。

## 使い方

ヘッダのセルと 1 行分のセルをフラグメントで書き、`rowAddableTable` の引数で渡します。1 行分のセルの中では、入力部品に `cell` オプションを付けて使います。

```html
<div layout:fragment="edit-fields-content" th:remove="tag">
	<div th:replace="~{bootstrap/components-edit :: rowAddableTable('nameRows', ~{::nameHeader}, ~{::nameRow}, '')}"></div>
</div>

<!-- layout:fragment の外に書く -->
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

  // inputSelect の選択肢。nameRows[3].lang の選択肢は、番号を除いた getNameRows_LangList() になる
  public List<String[]> getNameRows_LangList() { ... }
  ...
}
```

フラグメントの定義（`nameHeader`・`nameRow`）は、`layout:fragment` の外に書いてください。中に書くと、その場所でも描画されてしまいます。フラグメントを引数で渡す形なので、1 つの画面に表を 2 つ以上置くこともできます。

## 引数

| 引数 | 説明 |
| --- | --- |
| `itemPropertyPath` | ルートレコードから見たリストの itemPropertyPath です（例：`nameRows`）。 |
| `headerContents` | ヘッダの `<th>` です。<br>「削除」ボタンの列の `<th>` は splib が足すので、書きません。 |
| `rowContents` | 1 行分の `<td>` です。<br>「削除」ボタンの列の `<td>` は splib が足すので、書きません。 |
| `options` | 下記のオプションです。<br>不要な場合は `''` を渡します。 |

`rowContents` の中では、次の変数を使えます。

| 変数 | 説明 |
| --- | --- |
| `rowPath` | その行の itemPropertyPath の頭（末尾の `.` 付き）です。<br>今ある行では `nameRows[3].`、行を足すときの雛形では番号なしの `nameRows[].` になります。 |
| `row` | その行に当たるリストの要素です。<br>雛形では `null` になります。 |
| `isRowTemplate` | 雛形を描いている間は `true` になります。 |

## オプション

| オプション | 説明 |
| --- | --- |
| `readonly` | 「追加」「削除」ボタンを出しません。 |
| `minRows` | 行の数がこの数以下のとき、「削除」ボタンを押せなくします。 |
| `maxRows` | 行の数がこの数以上のとき、「追加」ボタンを押せなくします。 |
| `addButtonLabel` | 「追加」ボタンの文言（メッセージのキー、または文字列）です。 |
| `removeButtonLabel` | 「削除」ボタンの文言（メッセージのキー、または文字列）です。 |

ボタンの文言の既定値は、`jp.ecuacion.splib.web.common.button.addRow`・`jp.ecuacion.splib.web.common.button.removeRow` です。アプリの `item_names.properties` に同じキー（`.default` なし）を書くと、画面全体で置き換えられます。画面ごとに変えたいときは、`addButtonLabel`・`removeButtonLabel` を使います。

## セルの中の入力部品（`cell` オプション）

入力部品（`inputText`・`inputSelect` 等）は、通常は列の div（`col-...`）・ラベル・下の余白で包まれます。`cell` オプションを付けると、それらを出さず、入力欄と項目エラーのメッセージだけを出すので、表のセルに収まります。項目名はヘッダに出します。

`rowAddableTableHeaderCell` は、入力部品のラベルと同じ仕組みで、項目名と必須の印（`*`）を出す `<th>` です。引数には番号なしの itemPropertyPath（例：`nameRows[].name`）を渡します。必須の印は、`HtmlItem` に番号なしのパスで `notEmpty()` を指定すると出ます（例：`new HtmlItemString("nameRows[].name").notEmpty()`）。項目名のキーは `<クラス部>.<フィールド名>` で、クラス部はリストのフィールドに付けた `@ItemNameKeyClass` の値、付けていなければリストのフィールド名（例：`nameRows.name`）になります。

ヘッダの項目名を `item_names.properties` に書かずに済ませたいときは、`<th th:text="#{...}">` のように素の HTML で書いてもかまいません。

## 行の追加・削除と番号の振り直し

行を足すときの雛形は、表の下の `<template>` の中に 1 回だけ描かれます。雛形の name・id は、番号なしの `nameRows[]` になっています。`<template>` の中身はフォームの送信の対象にならないので、雛形がそのまま送られることはありません。

「追加」「削除」を押すと、`static/js/splib-web.js` が、表の行の name・id・label の for にある `nameRows[...]` の番号を 0 から振り直します。そのため、行を消して間が空いても、サーバでは番号の詰まったリストとして受け取れます。振り直すのはその表のリストの番号だけで、ほかの表や行の中のネストしたパスの番号には触れません。

入力部品の id は、既定でその行のパス（例：`xxxEditForm.xxx.nameRows[3].name`）を含むので、行の間で重なりません。セルの中に素の HTML で id を書く場合は、同じようにその行のパスを含めてください。含めないと、足した行と id が重なります。

## 行ごとの項目エラー

項目エラーは、番号付きのパスで出します。`BusinessViolation` に、フォームから見た番号付きのパスを渡すと、その行の入力欄の下にメッセージが出て、入力欄が赤くなります（メッセージを項目ごとに出す設定の場合。[例外処理](page?id=web/exception-handling&lang=ja)を参照）。番号は、画面から送られたリストの番号です（空の行を飛ばす等で数え直さないでください）。

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

## 注意

- 行を全部消して送ると、リストのパラメータが 1 つも送られません。その場合、リストは受け取ったレコードの初期値のままになります。レコードをリクエストのたびに作り直さないアプリ（セッションに置く等）では、前のリストが残ることがあります。
- 言語も表示名も空の行のような、入力されていない行を無視する処理は、アプリ側（Service 等）で行ってください。
- `rowContents` の中のスクリプト（`event` オプションで足されるもの等）は、足した行では動きません。
