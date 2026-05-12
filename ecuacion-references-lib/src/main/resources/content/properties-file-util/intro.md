# PropertiesFileUtil 入門

## 概要

`PropertiesFileUtil` は、`.properties` ファイルを読み取るためのユーティリティクラスです。
Java 標準の `ResourceBundle` をラップしており、複数ファイル種別への対応・ロケール切り替え・引数埋め込みなどの機能を追加しています。

この記事では、最初に使うことが多い `messages.properties` の読み取りに絞って説明します。
他のファイル種別（`constants.properties`, `item_names.properties` など）については
**util &gt; PropertiesFileUtil &gt; 基本的な使い方** を参照してください。

---

## messages.properties とは

`messages.properties` は、アプリケーションが表示するメッセージ文字列を管理するファイルです。
エラーメッセージ・通知メッセージ・ラベル文字列など、ユーザーに見せるテキストをここに集約します。

```properties
# messages.properties
error.required=必須項目です。
error.range={0} は {1} 以上 {2} 以下で入力してください。
greeting=こんにちは、{0} さん。
```

ロケール別のファイル（`messages_ja.properties`, `messages_en.properties` など）を用意すると、
`Locale` に応じて自動的に切り替わります。

---

## キーを指定して読み取る

```java
String msg = PropertiesFileUtil.getMessage("error.required");
// => "必須項目です。"
```

`getMessage(key)` はLocale.ROOTでメッセージを返します。

---

## プレースホルダーに値を埋め込む

メッセージ内の `{0}`, `{1}` ... には、`Object...` 引数として渡した値が順に埋め込まれます。

```java
String msg = PropertiesFileUtil.getMessage(
    "error.range", "年齢", "0", "150");
// => "年齢 は 0 以上 150 以下で入力してください。"
```

内部では `java.text.MessageFormat` が使われているため、数値・日付の型awarフォーマットも利用できます。

```properties
amount.label=合計: {0,number,#,###} 円
```

```java
String msg = PropertiesFileUtil.getMessage("amount.label", 1234567);
// => "合計: 1,234,567 円"
```

---

## ロケールを指定する

ロケールを明示する場合は `getMessage(Locale, key, args...)` を使います。

```java
String msg = PropertiesFileUtil.getMessage(
    Locale.JAPANESE, "greeting", "田中太郎");
// => "こんにちは、田中太郎 さん。"
```

`Locale` に `null` を渡すとLocale.ROOTとして扱われます（引数なしの `getMessage(key)` と同じ動作）。

---

## キーが存在しない場合

`messages.properties` にキーが存在しない場合、例外は投げられず **キー文字列そのものが返ります**。

```java
// "no.such.key" というキーが存在しない場合
String msg = PropertiesFileUtil.getMessage("no.such.key");
// => "no.such.key"
```

この挙動は `application.properties`（存在しないキーは例外）とは異なります。
開発中に画面でキー文字列がそのまま表示されていたら、キーのタイポやファイル未登録を疑ってください。

---

---

## 詳細リファレンス

`Arg` クラスを使った高度な引数渡し、他のファイル種別、クロスファイル参照などの詳細は
**util &gt; PropertiesFileUtil** 以下の各記事を参照してください。
