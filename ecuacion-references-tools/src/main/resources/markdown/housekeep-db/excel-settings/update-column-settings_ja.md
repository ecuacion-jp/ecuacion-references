housekeep DB設定 シートの `処理種別` が「更新」のタスクで、書き換える列と値を設定します。1 行に 1 列を記載し、1 つのタスクで複数の列を書き換える場合は、同じ 処理ID で複数行記載します。

「更新」のタスクには 1 行以上の記載が必要です。逆に、「更新」以外のタスクに紐づく行があるとエラーになります。「更新」のタスクを使わない場合は、シートを空のままにしてください。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| 処理ID | ○ | housekeep DB設定 シートのタスク ID と一致させる |
| 更新カラム名 | ○ | 書き換える列の名前 |
| 更新値種別 | ○ | 表示用の値の種類（「固定値」「現在日時」または「NULL」） |
| 更新値種別（内部値） | ○ | ツールが参照する内部値。`FIXED_VALUE`、`CURRENT_TIMESTAMP` または `NULL` を記載 |
| 更新カラム型リテラル記号 | △ | 値にクォートが必要かどうか。`(none)` または `quotes(')` |
| 更新カラム値 | △ | 列に設定する値 |

更新値種別によって、次のように書き換えます。

| 更新値種別 | 書き換える値 | 更新カラム型リテラル記号・更新カラム値 |
| --- | --- | --- |
| 固定値 | 更新カラム値に記載した値 | 両方とも必須 |
| 現在日時 | ツールの実行時の日時（date 型の列の場合は日付のみ） | 両方とも空欄必須 |
| NULL | NULL | 両方とも空欄必須 |

## 書き換え済みのレコードの扱い

「更新」のタスクは、書き換え済みのレコードを**自動では対象から除外しません**。条件に合致する限り、実行するたびに同じレコードが書き換えられます。

同じレコードを 2 回処理したくない場合は、処理済みであることを示す列（例: 匿名化フラグ）を書き換える列に含め、[データ検索条件設定](page?id=housekeep-db/excel-settings/search-condition-settings&lang=ja) シートにその列で除外する条件を記載してください（下記の設定例を参照）。

> **Warning:** 除外する条件を書き忘れると、毎回すべての対象レコードが書き換えられます。「現在日時」を指定した列は実行のたびに上書きされ、関連テーブルの削除も毎回実行されます。

## 設定例

クローズから 90 日たった問い合わせを匿名化する例です。一般ユーザへの参照をシステムアカウントの ID に、電話番号を NULL に書き換え、匿名化フラグと匿名化日時を設定します。あわせて、紐づく添付ファイルの行を物理削除します。

### 1. テスト用テーブルとデータの準備

```sql
CREATE TABLE inquiry (
    id integer,
    status varchar,
    closed_at timestamp,
    user_ref varchar,
    phone varchar,
    anonymized boolean NOT NULL DEFAULT false,
    anonymized_at timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE inquiry_attachment (
    id integer,
    inquiry_id integer,
    PRIMARY KEY (id)
);

INSERT INTO inquiry (id, status, closed_at, user_ref, phone) VALUES (1, 'CLOSED', now() - interval '100 days', 'U001', '090-0000-0000');
INSERT INTO inquiry (id, status, closed_at, user_ref, phone) VALUES (2, 'CLOSED', now() - interval '10 days', 'U002', '080-0000-0000');
INSERT INTO inquiry_attachment (id, inquiry_id) VALUES (11, 1);
INSERT INTO inquiry_attachment (id, inquiry_id) VALUES (12, 2);
```

### 2. Excel 設定ファイルの編集

#### housekeep DB設定 シート

| 処理ID | DB接続ID | 処理種別 | テーブル名 | IDカラム名 | IDカラム型リテラル記号 |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | 更新 | inquiry | id | (none) |

| 期間経過チェック用：日付／日時カラム名 | 期間経過チェック用：要経過日数 |
| --- | --- |
| closed_at | 90 |

#### データ検索条件設定 シート

| 処理ID | 条件カラム名 | 条件演算子 | 条件カラム型リテラル記号 | 条件カラム値 |
| --- | --- | --- | --- | --- |
| task-1 | status | equal | quotes(') | CLOSED |
| task-1 | anonymized | not equal | (none) | true |

2 行目が、匿名化済みのレコードを除外する条件です。

#### 更新項目設定 シート

| 処理ID | 更新カラム名 | 更新値種別 | 更新カラム型リテラル記号 | 更新カラム値 |
| --- | --- | --- | --- | --- |
| task-1 | user_ref | 固定値 | quotes(') | SYS001 |
| task-1 | phone | NULL | | |
| task-1 | anonymized | 固定値 | (none) | true |
| task-1 | anonymized_at | 現在日時 | | |

#### 関連テーブル処理設定 シート

| 処理ID | 関連テーブル処理パターン | 対象テーブルカラム名 | 関連テーブル名 | 関連テーブルIDカラム名 | 関連テーブルIDカラム型リテラル記号 |
| --- | --- | --- | --- | --- | --- |
| task-1 | 論理廃止／削除 | id | inquiry_attachment | inquiry_id | (none) |

関連テーブルIDカラム名には、添付ファイル側の問い合わせへの参照の列（`inquiry_id`）を指定します。1 件の問い合わせに紐づく添付ファイルが複数あっても、すべて削除されます。

実行すると、クローズから 90 日以上たった `id=1` の問い合わせだけが書き換えられ（`user_ref` が `SYS001`、`phone` が NULL、`anonymized` が `true`、`anonymized_at` が実行時の日時）、紐づく添付ファイル `id=11` が物理削除されます。`id=2` の問い合わせと添付ファイル `id=12` はそのまま残ります。もう一度実行しても、`id=1` は `anonymized = true` のため対象外になり、書き換えられません。
