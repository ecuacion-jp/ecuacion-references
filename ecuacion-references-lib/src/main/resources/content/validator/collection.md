# コレクション・アサーション系

## コレクション系 — 複数フィールドの null / empty 一括検証

クラスレベルで `propertyPath[]` に複数フィールドを指定し、それらの null / empty 状態をまとめて検証します。

### Any系

| アノテーション | 検証内容 |
| ------------- | -------- |
| `@AnyNull` | 少なくとも1フィールドが `null` であること |
| `@AnyNotNull` | 少なくとも1フィールドが `null` でないこと |
| `@AnyEmpty` | 少なくとも1フィールドが空（null または空文字）であること |
| `@AnyNotEmpty` | 少なくとも1フィールドが空でないこと |

### All系

| アノテーション | 検証内容 |
| ------------- | -------- |
| `@AllNullOrAllNotNull` | 全フィールドが `null`、または全フィールドが `null` でないこと |
| `@AllEmptyOrAllNotEmpty` | 全フィールドが空、または全フィールドが空でないこと |

### 使用例

```java
// startDate と endDate はどちらも入力するか、どちらも入力しないかのどちらか
@AllNullOrAllNotNull(propertyPath = {"startDate", "endDate"})
public class SearchForm { ... }
```

```java
// email / phone / address のうち少なくとも1つは入力必須
@AnyNotEmpty(propertyPath = {"email", "phone", "address"})
public class ContactForm { ... }
```

---

## AssertTrueWithPropertyPath — フィールド紐付き AssertTrue

標準の `@AssertTrue` と同様に `true` であることを検証しますが、`propertyPath` でエラーを特定のフィールドに紐付けられます。

```java
@AssertTrueWithPropertyPath(propertyPath = {"startDate", "endDate"})
public boolean isDateRangeValid() {
    if (startDate == null || endDate == null) return true;
    return startDate.isBefore(endDate);
}
```

クラスメソッドを使った複雑なバリデーションに適しています。

---

## ReturnTrue — メソッドバリデーター

クラス内の指定メソッドが `true` を返すことを検証するメソッドレベルのアノテーションです。

```java
public class EventForm {

    @ReturnTrue(
        methodName = "isDateRangeValid",
        propertyPath = {"startDate", "endDate"},
        message = "開始日は終了日より前に設定してください"
    )
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) return true;
        return startDate.isBefore(endDate);
    }
}
```

| 属性 | 説明 |
| ---- | ---- |
| `methodName` | 実行するメソッド名（引数なし、`boolean` 戻り値） |
| `propertyPath` | エラーを紐付けるフィールド |
| `message` | エラーメッセージ（メッセージキーまたはリテラル文字列） |

`AssertTrueWithPropertyPath` との違いは、`@ReturnTrue` がメソッド自体に付与するのに対し、`@AssertTrueWithPropertyPath` はクラスに付与してメソッドを呼び出す点です。
