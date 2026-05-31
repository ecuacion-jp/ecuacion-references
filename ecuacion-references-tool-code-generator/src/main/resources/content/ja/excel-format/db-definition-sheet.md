# DB項目定義シート

「DB項目定義」シートでは、データベースのテーブルとカラムを定義します。
このシートの内容が Entity・Repository・BL 等の生成コードの基礎となります。

## 列構成

テーブル名: `テーブル7`、範囲: `A5:AB{最終行}`

| 列 | 項目 | 説明 |
| --- | --- | --- |
| A | テーブル名 | DB テーブル名（スネークケース大文字。同テーブルの行は同じ値を繰り返す） |
| B | 表示名（デフォルト言語） | 英語表示名 |
| C | カラム名 | DB カラム名（スネークケース大文字） |
| D | dataType | `DT_XXXX` 形式の DataType 名 |
| E | dataType 存在確認 | VLOOKUP 式による自動チェック（コピーで対応） |
| G | PK・UK | `S`=サロゲートキー（PK）/ `U`=ユニークキー |
| H | nullable | `○`=NULL 許可（空白=NOT NULL） |
| I | 自動採番 | `○`=DB シーケンスによる採番 |
| O | 関連：種類 | `@ManyToOne` / `@OneToOne` / `@OneToMany` |
| P | 関連：direction | `unidirectional` / `bidirectional` |
| Q | 関連：参照元変数名 | Java フィールド名（camelCase） |
| R | 関連：参照先テーブル | 参照先テーブル名 |
| S | 関連：参照先カラム | 参照先カラム名（通常 `ID`） |
| T | 関連：参照先変数名 | bidirectional 時の逆参照フィールド名 |
| Y | 備考 | コメント（生成に影響しない） |
| Z | 表示名（追加言語 1） | 日本語表示名 |

---

## テーブルの記述パターン

### 基本構造

1 テーブル 1 グループとして行を記述します。テーブルの先頭行は必ずサロゲートキー（`S`）から始めます。

```
テーブル名   | 表示名 | カラム名 | DataType  | (E) |   | PK/UK | null | 採番 | ...
------------|------|--------|---------|-----|---|-------|------|------|
MY_TABLE    | name | ID     | DT_SERIAL | ○   |   | S     |      | ○    |
MY_TABLE    |      | CODE   | DT_CODE   | ○   |   | U     |      |      |
MY_TABLE    |      | NAME   | DT_ACC_NAME | ○ |   |       | ○    |      |
```

### サロゲートキー（全テーブル必須）

すべてのテーブルの先頭行はサロゲートキーである必要があります。

| テーブル名 | 表示名 | カラム名 | dataType | G列（PK/UK） | I列（採番） |
| --- | --- | --- | --- | --- | --- |
| MY_TABLE | name | ID | DT_SERIAL | S | ○ |

- `G列=S`: サロゲートキー（PRIMARY KEY）として扱われる
- `I列=○`: DB 側のシーケンスによる自動採番

### ユニークキー（Natural Key）

自然キーとなるカラムを定義します。

| G列（PK/UK） |
| --- |
| U |

ユニークキーが複数ある場合は、各行に `U` を指定します。
複合ユニークキーは現在未対応です。

### NULL 許可カラム

`H列（nullable）= ○` のカラムは NULL 許可となり、生成コードで `@Nullable` アノテーションが付与されます。

---

## リレーションシップの定義

テーブル間のリレーションシップは O〜T 列で定義します。

### @ManyToOne（最も一般的な外部キー）

```
テーブル名 | 表示名     | カラム名  | dataType  | ... | O列           | P列             | Q列      | R列          | S列 | T列 |
---------|---------|--------|---------|-----|--------------|----------------|---------|-------------|-----|-----|
MY_TABLE  | group ID | GROUP_ID | DT_SERIAL | ... | @ManyToOne   | unidirectional | groupVar | GROUP_TABLE | ID  |     |
```

- `@ManyToOne`: 多対一（外部キーがあるテーブル側に記述）
- `unidirectional`: 一方向のみ（参照元→参照先）。ほとんどのケースでこれを使用

生成される Entity コード（抜粋）:

```java
@ManyToOne
@JoinColumn(name = "GROUP_ID")
private GroupEntity groupVar;
```

### @ManyToOne（bidirectional）

逆方向からも参照する場合は `bidirectional` を使用し、`T列` に逆参照フィールド名を指定します。

```
... | @ManyToOne | bidirectional | parentVar | PARENT_TABLE | ID | childListVar | ...
```

参照先（PARENT_TABLE）側の Entity に以下が生成されます:

```java
@OneToMany(mappedBy = "parentVar")
private List<ChildEntity> childListVar;
```

### @OneToOne

1 対 1 のリレーションシップ。指定方法は `@ManyToOne` と同様です。

---

## dataType 存在確認列（E列）の VLOOKUP 式

E 列は dataType 定義シートに DataType が存在するかを自動チェックする列です。
値が `×` の場合は DataType が未定義なのでエラーになります。

テーブル内での式（コピーで自動設定）:

```
=IF(NOT(ISNA(VLOOKUP(テーブル7[[#This Row],[dataType]], dataType定義!A:A, 1,FALSE))), "○", "×")
```

テーブル範囲外の行に追加した場合は、セル参照式に変更します:

```
=IF(NOT(ISNA(VLOOKUP(D{行番号}, dataType定義!A:A, 1,FALSE))), "○", "×")
```

---

## 既存テーブル（認証系）について

サンプル Excel には `ecuacion-splib` の認証機能で使用する以下のテーブルが定義されています。
これらはそのまま残してください。新しいテーブルは既存行の直後から追記します。

- `ACC`: アカウント
- `ACC_ADMIN`: 管理者アカウント
- その他 splib 認証テーブル
