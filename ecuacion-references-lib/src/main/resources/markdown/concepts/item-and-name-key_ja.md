バリデーションエラーメッセージで使われる項目名キーの仕組みです。

---

## itemNameKey

項目の表示名を `item_names.properties` から引くためのキーです。
形式は `"クラス部.フィールド部"`（例：`"user.name"`）で、`item_names.properties` のキーと対応します。

```properties
# item_names.properties
user.name=氏名
user.email=メールアドレス
```

`itemNameKey` は通常は自動で解決されますが、`Item` に明示指定することもできます。
自動解決のルール詳細は [itemNameKey の解決ルール](?id=item/item-name-key) を参照してください。

---

## @ItemNameKeyClass

`itemNameKey` のクラス部のデフォルト値を一括指定するアノテーションです。

詳細は [@ItemNameKeyClass](?id=messaging/item-name-key-class) を参照してください。

---

## itemNamePath

ネストしたオブジェクトでバリデーションエラーが発生した場合に、
どのネスト階層で起きたかを項目名に付加する機能です。

```text
「住所」の「郵便番号」にnullは許可されていません
```

詳細は [itemNamePath](?id=messaging/item-name-path) を参照してください。
