# enum定義シート

「enum定義」シートでは、`ENUM` 型の DataType が持つ列挙値を定義します。

## いつ使うか

「dataType定義」シートで型が `ENUM` の DataType を定義した場合に、
そのEnumが取りうる値をこのシートで定義します。

## シートの基本構造

シートには Enum クラスごとのテーブルが並んでいます。
各テーブルの先頭行がクラス名、それ以降の行がその Enum の値定義となります。

### 例

DataType `DT_STATUS` の型を `ENUM` とした場合、
enum 定義シートに以下のようなテーブルを作成します。

| Enum クラス名 | DB 格納値 | 表示名（英語） | 表示名（日本語） |
| --- | --- | --- | --- |
| StatusEnum | | | |
| ACTIVE | 1 | Active | 有効 |
| INACTIVE | 2 | Inactive | 無効 |
| DELETED | 3 | Deleted | 削除済み |

### 生成されるコード例

```java
public enum StatusEnum {
    ACTIVE("1"),
    INACTIVE("2"),
    DELETED("3");

    private final String code;

    StatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
```

また、DB 格納値との変換を行う `Converter` クラスも自動生成されます。

```java
@Converter(autoApply = true)
public class StatusEnumConverter implements AttributeConverter<StatusEnum, String> {
    // DB値 ⇔ Enum変換
}
```

## DB 格納値について

DB カラムには Enum の名前ではなく、定義した格納値（上の例では `"1"`, `"2"`, `"3"`）が保存されます。
格納値の型は現在 `String` のみ対応しています。

## Enum を DataType・カラムに紐づける流れ

1. **dataType定義シート**: 型を `ENUM` とした DataType を定義（例: `DT_STATUS`）
2. **enum定義シート**: そのEnumの値一覧を定義（例: `StatusEnum` の各値）
3. **DB項目定義シート**: カラムの DataType に `DT_STATUS` を指定

ツールが各シートの内容を組み合わせて、Enum クラス・Converter・DataType バリデーターを生成します。
