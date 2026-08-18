「enum定義」シートでは、`ENUM` 型の DataType が持つ列挙値を定義します。

## いつ使うか

「dataType定義」シートで型が `ENUM` の DataType を定義した場合に、
そのEnumが取りうる値をこのシートで定義します。

## シートの基本構造

1 行が Enum の値 1 つに対応します。「クラス名の行」という特別な行はなく、「DB項目定義」シートで
テーブル名列を同じテーブルの行で繰り返すのと同様に、同じ Enum に属する行では DataType名列に同じ値を繰り返します。
Enum クラス名自体を直接入力する項目はなく、DataType名から自動生成されます（`DT_` プレフィックスを除去し、
アッパーキャメルケースに変換したうえで `Enum` を付加）。

| 列 | 項目 | 説明 |
| --- | --- | --- |
| DataType名 | DataType名 | この値が属する `DT_XXXX` の DataType。同じグループの全行で同じ値を繰り返す |
| code | code | DB カラムに格納される値（例: `1`, `2`, `3`） |
| varName | varName | 生成される Java の enum 定数名（例: `ACTIVE`） |
| javaのみ | javaのみ | シート上に存在するマーカー列（現時点ではコード生成に使用されていない） |
| 備考 | 備考 | コメント（生成に影響しない） |
| 表示名（デフォルト言語） | 表示名（デフォルト言語） | UI に表示する表示名（デフォルト言語） |
| 表示名（追加言語1〜3） | 表示名（追加言語1〜3） | 各追加言語での表示名 |

### 例

DataType `DT_STATUS` の型を `ENUM` とした場合、
enum 定義シートに以下のような行を作成します。

| DataType名 | code | varName | 表示名（デフォルト言語） | 表示名（追加言語1） |
| --- | --- | --- | --- | --- |
| DT_STATUS | 1 | ACTIVE | Active | 有効 |
| DT_STATUS | 2 | INACTIVE | Inactive | 無効 |
| DT_STATUS | 3 | DELETED | Deleted | 削除済み |

これにより、`DT_STATUS` から `StatusEnum` という Enum クラスが生成されます。

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
