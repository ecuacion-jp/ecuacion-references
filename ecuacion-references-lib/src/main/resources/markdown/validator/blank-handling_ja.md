## 概要

`ecuacion-lib-validation` のバリデータは、ブランク（空文字 `""`）を **valid** として扱います。
これは Jakarta Validation 標準とは異なる設計です。

---

## 標準の @Pattern との違い

標準の `@Pattern` は `null` を valid とする一方、`""` は invalid 扱いです。

| 値 | 標準 `@Pattern` | `@PatternWithDescription` |
| -- | --------------- | ------------------------- |
| `"abc123"` | ✅ valid | ✅ valid |
| `"ABC"` | ❌ invalid（正規表現不一致） | ❌ invalid（正規表現不一致） |
| `null` | ✅ valid | ✅ valid |
| `""` | ❌ **invalid** | ✅ **valid** |

---

## なぜブランクを valid にするのか

Web アプリケーションでは `null` と `""` には意味の違いがあります。

- `""` ＝ 画面に項目があり、ユーザーが未入力のまま送信した
- `null` ＝ そもそも画面に項目がない（送信データに含まれていない）

ユーザーが未入力で送信した場合に `@Pattern` が invalid を返すと、
「形式が不正です」というエラーが表示されます。
しかし本来あるべきエラーは「入力必須です」（`@NotEmpty` によるもの）であって、
書式チェック系のバリデータは未入力（`""`）に反応すべきではありません。

---

## ecuacion-lib-validation の設計

書式チェック系バリデータはブランクを valid として扱い、必須チェックは `@NotEmpty` に任せます。

```java
// 必須 + 書式チェックを明確に役割分担
public record UserProfile(
    @NotEmpty
    @PatternWithDescription(regexp = "^[a-z0-9]+$", description = "半角英数字")
    String username
) {}
```

| 値 | 結果 | 原因 |
| -- | ---- | ---- |
| `"abc123"` | ✅ valid | — |
| `"ABC"` | ❌ invalid | `@PatternWithDescription`（書式不一致） |
| `null` | ❌ invalid | `@NotEmpty` |
| `""` | ❌ invalid | `@NotEmpty` |

`@NotEmpty` と書式チェック系が明確に役割分担できます。

---

## ブランクを valid として扱うバリデータ

`ecuacion-lib-validation` でブランクを invalid とするのは `@NotEmpty` / `@NotBlank` のみです。
それ以外の書式チェック系バリデータはすべてブランクを valid として扱います。

| カテゴリ | 代表的なバリデータ |
| -------- | ----------------- |
| 書式チェック系 | `@PatternWithDescription`, `@IntegerString`, `@BooleanString` など |
| 条件付き系 | `@NotEmptyWhen`, `@TrueWhen` など `@XxxWhen` 系 |
| 大小比較系 | `@LessThan`, `@GreaterThan` など |
