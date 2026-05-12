# Violation

## 概要

ecuacion-lib では、バリデーション違反を `Violations` クラスに集約してから
`throwIfAny()` でまとめてスローするパターンを採用しています。

`Violations` は2種類の違反を受け付けます。

| 種別 | クラス | 用途 |
| ------ | -------- | ------ |
| ビジネスルール違反 | `BusinessViolation` | 業務ロジック上の違反を手動で作成 |
| 制約違反 | `ConstraintViolation` | Jakarta Validation の検証結果 |

---

## 基本的な使い方

```java
Violations violations = new Violations();

// ビジネスルール違反を追加
violations.add(new BusinessViolation("error.some-message-id"));

// 1件でも違反があれば ViolationException をスロー
violations.throwIfAny();
```

複数チェックを先に行い**まとめてスロー**することで、1リクエストで全エラーをユーザーに提示できます。

```java
Violations violations = new Violations();

if (conditionA) {
  violations.add(new BusinessViolation("error.condition-a"));
}
if (conditionB) {
  violations.add(new BusinessViolation("error.condition-b"));
}

violations.throwIfAny();
```

---

## BusinessViolation の詳細

### プレースホルダー引数を渡す

```java
// {0} に "年齢"、{1} に "0"、{2} に "150" が埋め込まれる
violations.add(new BusinessViolation("error.range", "年齢", "0", "150"));
```

### 関連フィールドを指定する

どのフィールドに関する違反かを `itemPropertyPath` で関連付けられます。

```java
violations.add(new BusinessViolation(new String[] {"birthDate"}, "error.future-date"));

// 複数フィールドにまたがる違反
violations.add(new BusinessViolation(
    new String[] {"startDate", "endDate"}, "error.date-range"));
```

### 項目名をメッセージに埋め込む（itemNameKeys）

`messages_with_item_names.properties` に `{item_name}` プレースホルダーを含むメッセージを定義し、
`itemNameKeys` を指定すると、メッセージ生成時に `item_names.properties` から項目名が解決されます。

```properties
# messages_with_item_names.properties
error.already-registered={item_name}はすでに登録されています
```

```properties
# item_names.properties
customer.email=メールアドレス
```

```java
// 第1引数：itemNameKeys（item_names.properties のキー）
// 第2引数：itemPropertyPaths（UI のフィールドハイライト用）
violations.add(new BusinessViolation(
    new String[] {"customer.email"},
    new String[] {"email"},
    "error.already-registered"));
// → 「メールアドレスはすでに登録されています」
```

`itemNameKeys` が不要（`{item_name}` を使わない）場合は、従来通り `itemPropertyPaths` のみ指定します。

---

## throwIfAny() — まとめてスロー

`throwIfAny()` は違反が1件でも存在すれば `ViolationException` をスローします。
違反がなければ何もしません。

---

## ViolationException

`ViolationException` は `RuntimeException` を継承した非検査例外です。
`getViolations()` で `Violations` を取得できます。

通常のアプリケーション開発では直接キャッチするケースは少なく、
フレームワーク層（ecuacion-splib など）が一括して処理します。

---

## throwWarningIfAny() — 警告として処理する

`throwIfAny()` の代わりに `throwWarningIfAny()` を使うと、`ViolationException` ではなく
`ViolationWarningException` がスローされます。

```java
violations.throwWarningIfAny();
```

`ViolationWarningException` は `ViolationException` のサブクラスであるため、
`catch (ViolationException e)` でまとめて捕捉することもできます。

### 主な用途

**ecuacion-splib-web との組み合わせ**（主な用途）:
UI 画面上で「〇〇しますがよろしいですか？」という確認ダイアログを出す仕組みに使います。
ユーザーが確認すると、その警告を無視して処理を続行するフローになります。
詳しくは ecuacion-splib-web のドキュメントを参照してください。

**ecuacion-lib 単体での活用例**:
バッチ処理や API ハンドラで「エラー（処理停止）」と「警告（続行可）」を型で区別したい場合に使えます。

```java
// 呼び出し側
try {
    someService.process(data);
} catch (ViolationWarningException e) {
    // 警告 → ログ記録して続行
    logger.warn(e.getViolations().toString());
} catch (ViolationException e) {
    // エラー → 処理停止
    throw e;
}
```

---

## 応用

### violations.add() ショートハンド

`BusinessViolation` を明示せず、`Violations` に直接メッセージキーなどを指定できます。

```java
// BusinessViolation を明示した書き方（標準）
violations.add(new BusinessViolation("error.some-message-id"));

// 同等のショートハンド
violations.add("error.some-message-id");
```

`itemPropertyPath` 付きや引数付きのオーバーロードも同様です。

```java
violations.add(new String[] {"fieldName"}, "error.message");
violations.add("error.range", "年齢", "0", "150");
```

### violations.validate() — Jakarta Validation との連携

Jakarta Validation の検証結果を `Violations` に追加します。

```java
violations.validate(someObject);                        // グループなし
violations.validate(someObject, GroupA.class);          // バリデーショングループ指定
```

詳しくは **Jakarta Validation** を参照してください。

---

## MessageParameters — メッセージ生成のカスタマイズ

`Violations.MessageParameters` は、エラーメッセージの生成方法を制御するオプションのパラメータクラスです。
`withMessageParameters()` でフルエント設定できます。

```java
violations.withMessageParameters(p -> p
    .isMessageWithItemName(true)
    .messagePrefix("upload.row.prefix"));
```

主なユースケースは、画面表示以外（アップロードされた Excel ファイルのバリデーション等）で
わかりやすいエラーメッセージを組み立てたい場合です。

### isMessageWithItemName — 項目名あり/なしのメッセージ切り替え

バリデーションエラーのメッセージを `messages.properties` と
`messages_with_item_names.properties` のどちらから取得するかを制御します。

| 設定値 | 動作 |
| ------ | ---- |
| `null`（デフォルト） | フレームワーク層の既定値に従う |
| `true` | 項目名あり版のメッセージを使用 |
| `false` | 項目名なし版のメッセージを使用 |

```java
violations.withMessageParameters(p -> p.isMessageWithItemName(true));
```

`isMessageWithItemName(true)` の場合、`messages_with_item_names.properties` の
`{item_name}` プレースホルダーにフィールドの項目名が埋め込まれます。

```properties
# messages_with_item_names.properties
error.required = {item_name}は入力必須です
```

### showsItemNamePath — 項目名パスの表示

コレクション内の要素に対するバリデーションエラーで、要素の位置情報をメッセージに含めるかを制御します。

| 設定値 | 表示例 |
| ------ | ------ |
| `false`（デフォルト） | `名前は入力必須です` |
| `true` | `リストの[1]番目の名前は入力必須です` |

```java
violations.withMessageParameters(p -> p.showsItemNamePath(true));
```

### messagePrefix / messagePostfix — メッセージの前後に追加テキスト

各エラーメッセージの前後に固定テキストを付加します。
Excel アップロードのバリデーションで「2行目: 〇〇は必須です」のような表示に使います。

```java
// 文字列を直接指定（messages.properties のキーとして解決を試み、なければそのまま使用）
violations.withMessageParameters(p -> p.messagePrefix("2行目: "));

// Arg 版: Arg.message() で動的に組み立てる
violations.withMessageParameters(p -> p
    .messagePrefix(Arg.message("upload.error.row.prefix", rowNumber)));
```

```properties
# messages.properties
upload.error.row.prefix = {0}行目:
```

### 組み合わせ例

Excel ファイルの検証で、行番号を prefix に付けつつ項目名を含めたメッセージを出す例です。

```java
for (int i = 0; i < rows.size(); i++) {
    int rowNum = i + 2; // ヘッダー行を除いた行番号
    new Violations()
        .validate(rows.get(i))
        .withMessageParameters(p -> p
            .isMessageWithItemName(true)
            .showsItemNamePath(true)
            .messagePrefix(rowNum + "行目: "))
        .throwIfAny();
}
