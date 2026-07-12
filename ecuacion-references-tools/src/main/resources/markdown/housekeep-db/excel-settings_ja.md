# housekeep-db Excel設定ファイル

housekeep-db の動作は Excel ファイル 1 枚で制御します。
Excel ファイルには以下の 4 つのシートがあります。

| シート名 | 役割 |
| --- | --- |
| DB Connection Settings | データベース接続情報 |
| Housekeep DB Settings | ハウスキーピングの実行タスク |
| Related Table Settings | 関連テーブルの連動処理設定 |
| Search Condition Settings | 追加の検索条件 |

---

## DB Connection Settings シート

タスクが使用するデータベース接続情報を定義します。複数の接続を登録できます。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| DB Connection ID | ○ | 接続を識別する ID。Housekeep DB Settings シートから参照される |
| Driver Name | ○ | JDBC ドライバ名（例: `postgresql`） |
| Connection URL: Protocol | ○ | 接続 URL のプロトコル部分（例: `postgresql`） |
| Connection URL: Server | ○ | データベースサーバのホスト名または IP アドレス |
| Connection URL: Port | ○ | ポート番号 |
| Connection URL: Database | ○ | データベース名 |
| Connection URL: Schema | — | スキーマ名（省略可） |
| Username | ○ | 接続ユーザ名 |
| Password | ○ | 接続パスワード |

---

## Housekeep DB Settings シート

削除処理のタスクを 1 行 1 タスクで定義します。タスクは上から順番に実行されます。

### 基本設定列

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| Task ID | ○ | タスクを識別する ID |
| DB Connection ID | ○ | DB Connection Settings シートの ID と一致させる |
| Soft / Hard Delete | ○ | 表示用の削除種別（「Soft Delete」または「Hard Delete」） |
| Soft / Hard Delete (internal value) | ○ | ツールが参照する内部値。`SOFT_DELETE` または `HARD_DELETE` を記載 |
| Table Name | ○ | 削除対象のテーブル名 |
| ID Column Name | ○ | 主キーまたはユニークインデックス列の名前（ID 列） |
| ID Column Literal Symbol | ○ | ID 列の値に SQL クォートが必要かどうか。`(none)` または `quotes(')` |

**ID Column Literal Symbol について**

| データ型 | 指定値 |
| --- | --- |
| integer, boolean など数値・論理型 | `(none)` |
| varchar など文字列型 | `quotes(')` |

### 経過日数による絞り込み（任意）

タイムスタンプ列の値が指定日数以上前のレコードのみを対象にする場合に設定します。
3 列はすべて同時に設定するか、すべて空白にする必要があります。

| 列名 | 説明 |
| --- | --- |
| Expiration Check: Timestamp Column Name | タイムスタンプ列の名前（例: `last_updated`） |
| Expiration Check: Timestamp Column Data Type | 列のデータ型。`LocalDateTime`（timestamp without time zone）または `OffsetDateTime`（timestamp with time zone） |
| Expiration Check: Validity Days | 何日前以前のレコードを対象にするか（整数） |

### ソフトデリート用列（ソフトデリート時のみ）

`Soft / Hard Delete (internal value)` が `SOFT_DELETE` の場合のみ使用します。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| Soft Delete Column Name | ○（ソフトデリート時） | 削除フラグ列の名前（`bool` 型の列）。`true` に更新される |
| Soft Delete: Update Timestamp Column Name | — | ソフトデリート時に更新するタイムスタンプ列の名前 |
| Soft Delete: Update User ID Column Name | — | ソフトデリート時に更新するユーザ ID 列の名前 |
| Soft Delete: Update User ID Column Literal Symbol | △ | ユーザ ID 列の値にクォートが必要かどうか。`(none)` または `quotes(')` |
| Soft Delete: Update User ID Column Value | △ | ユーザ ID 列にセットする値 |

「Update User ID Column Name」「Update User ID Column Literal Symbol」「Update User ID Column Value」の 3 列は、
すべて同時に設定するか、すべて空白にする必要があります。

---

## Related Table Settings シート

関連テーブルの処理を設定します。必要ない場合はシートを空のままにしてください。

このシートでは 2 種類の処理が設定できます。

| 処理パターン | 内部値 | 動作 |
| --- | --- | --- |
| Delete | `DELETE` | ターゲットテーブルの ID に紐づく関連テーブルのレコードを削除してから、ターゲットテーブルを削除 |
| Check and Skip Delete | `CHECK_AND_SKIP_DELETE` | 関連テーブルにレコードが存在する場合、ターゲットテーブルの削除をスキップ |

### 列一覧

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| Task ID | ○ | Housekeep DB Settings シートのタスク ID と一致させる |
| Soft / Hard Delete (internal value) | ○ | `SOFT_DELETE` または `HARD_DELETE`。対応するタスクの設定と一致させる |
| Related Table Process Pattern | ○ | 表示用パターン名（`Delete` または `Check and Skip Delete`） |
| Related Table Process Pattern (internal value) | ○ | `DELETE` または `CHECK_AND_SKIP_DELETE` |
| Target Table Column Name | ○ | ターゲットテーブル（削除対象テーブル）の列名。関連テーブルと結合するキー |
| Related Table Name | ○ | 関連テーブルの名前 |
| Related Table ID Column Name | ○ | 関連テーブルの ID 列（主キーまたはユニークインデックス）の名前 |
| Related Table ID Column Literal Symbol | ○ | 関連テーブルの ID 列にクォートが必要かどうか。`(none)` または `quotes(')` |

**DELETE パターンのソフトデリート設定（任意）**

関連テーブルのレコードをソフトデリートする場合は以下の列も設定します。

| 列名 | 説明 |
| --- | --- |
| Soft Delete Column Name | 関連テーブルの削除フラグ列 |
| Soft Delete: Update Timestamp Column Name | ソフトデリート時に更新するタイムスタンプ列 |
| Soft Delete: Update User ID Column Name | ソフトデリート時に更新するユーザ ID 列 |
| Soft Delete: Update User ID Column Literal Symbol | ユーザ ID 列のクォート要否 |
| Soft Delete: Update User ID Column Value | ユーザ ID 列にセットする値 |

---

## Search Condition Settings シート

タスクに追加の WHERE 条件を設定します。必要ない場合はシートを空のままにしてください。

たとえば、`exit_code` 列が `COMPLETED` のレコードのみを削除したい場合に使用します。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| Task ID | ○ | 条件を適用するタスクの ID |
| Search Condition Column Name | ○ | WHERE 条件に使用する列名 |
| Search Condition Column Literal Symbol | ○ | 列の値にクォートが必要かどうか。`(none)` または `quotes(')` |
| Search Condition Column Value | ○ | WHERE 条件の値 |

1 つのタスクに複数の条件を設定する場合は、同じ Task ID で複数行記載します（AND 条件で結合されます）。

---

## トランザクション仕様

- Housekeep DB Settings シートの各タスク完了時にコミットされます
- 1 タスクで 1,000 件以上のレコードを削除する場合は、1,000 件ごとにコミットされます
