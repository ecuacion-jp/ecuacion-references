ハウスキーピングのタスク（論理廃止・削除、または異常データ存在チェック）を 1 行 1 タスクで定義します。タスクは上から順番に実行されます。

## 基本設定列

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| 処理ID | ○ | タスクを識別する ID |
| DB接続ID | ○ | DB接続設定 シートの ID と一致させる |
| 処理種別 | ○ | 表示用の処理種別（「論理廃止」「削除」または「異常データ存在チェック」） |
| 処理種別（内部値） | ○ | ツールが参照する内部値。`SOFT_DELETE`、`HARD_DELETE` または `ABNORMAL_DATA_CHECK` を記載 |
| テーブル名 | ○ | 処理対象のテーブル名 |
| IDカラム名 | ○ | 主キーまたはユニークインデックス列の名前（ID 列） |
| IDカラム型リテラル記号 | ○ | ID 列の値に SQL クォートが必要かどうか。`(none)` または `quotes(')` |

**IDカラム型リテラル記号について**

| データ型 | 指定値 |
| --- | --- |
| integer, boolean など数値・論理型 | `(none)` |
| varchar など文字列型 | `quotes(')` |

## 経過日数による絞り込み（任意）

タイムスタンプ列の値が指定日数以上前のレコードのみを対象にする場合に設定します。
3 列はすべて同時に設定するか、すべて空白にする必要があります。
すべて空白にして論理廃止／削除を行う場合、レコード作成・更新からの期間経過を加味せず削除されます。

| 列名 | 説明 |
| --- | --- |
| 期間経過チェック用：timestampカラム名 | タイムスタンプ列の名前（例: `last_updated`） |
| 期間経過チェック用：timestampカラム型 | 列のデータ型。`LocalDateTime`（timestamp without time zone）または `OffsetDateTime`（timestamp with time zone） |
| 期間経過チェック用：timestamp要経過日数 | 何日前以前のレコードを対象にするか（整数） |

## soft delete（論理廃止）用列（論理廃止時のみ）

`処理種別` が「論理廃止」の場合のみ使用します（「削除フラグカラム名」は、後述の「削除」「異常データ存在チェック」の場合にも任意で指定できます）。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| 削除フラグカラム名 | ○（論理廃止時） | 削除フラグ列の名前（`bool` 型の列）。`true` に更新される |
| 論理廃止：更新用timestampカラム名 | — | 論理廃止時に更新するタイムスタンプ列の名前。値は自動で現在時刻が設定される |
| 論理廃止：更新用ユーザIDカラム名 | — | 論理廃止時に更新するユーザ ID 列の名前 |
| 論理廃止：更新用ユーザIDカラム型リテラル記号 | △ | ユーザ ID 列の値にクォートが必要かどうか。`(none)` または `quotes(')` |
| 論理廃止：更新用ユーザIDカラム値 | △ | ユーザ ID 列にセットする値 |

「論理廃止：更新用ユーザIDカラム名」「論理廃止：更新用ユーザIDカラム型リテラル記号」「論理廃止：更新用ユーザIDカラム値」の 3 列は、
すべて同時に設定するか、すべて空白にする必要があります。

## 物理削除時に削除フラグカラム名を指定した場合の挙動

`処理種別` が「削除」（物理削除）の場合でも、`削除フラグカラム名` を指定できます。この場合、削除フラグカラムの値が `true` のレコードのみが物理削除の対象になります（`false` のレコードは対象外）。論理廃止で `true` に更新済みのレコードだけをまとめて物理削除する、といった運用に使えます。

## 異常データ存在チェック

`処理種別` を「異常データ存在チェック」にすると、条件に合致するレコードを削除する代わりに、**そのようなレコードが存在すること自体を異常とみなして警告**します。たとえば「処理済みフラグが立たないまま 90 日経過したレコード」のように、本来存在してはいけないレコードの検知に使います。

- 条件の指定方法は論理廃止・削除と同じです。[データ検索条件設定](page?id=housekeep-db/excel-settings/search-condition-settings&lang=ja) シートの条件と、経過日数による絞り込み（任意）に合致するレコードが対象になります
- `削除フラグカラム名` を指定した場合、論理廃止済み（削除フラグ = `true`）のレコードはチェック対象から除外されます
- 「論理廃止：更新用〜」の各列は使用しないため、空欄にする必要があります
- [関連テーブル処理設定](page?id=housekeep-db/excel-settings/related-table-settings&lang=ja) シートは使用できません（異常データ存在チェックのタスクに紐づく行があるとエラーになります）
- レコードは一切更新・削除されません

合致するレコードが見つかった場合、タスクごとに件数と ID を `WARN` レベルでログ出力し、全タスクの実行後に、見つかった内容を **1 通の警告メール**にまとめて送信します（タスクごとに件数と、ID 順で先頭 10 件までの ID を記載。10 件を超える場合はその旨を記載）。メールの宛先・件名などの設定は[設定ファイル](page?id=housekeep-db/config&lang=ja)を参照してください。

> **Note:** レコードが見つかった場合でも、ツールは正常終了（終了コード `0`）します。検知結果は警告メール、または `WARN` レベルのログで確認してください。

### 削除タスクとの組み合わせ

異常データ存在チェックの期間・条件は、削除タスクとは独立に設定します。たとえば「処理済みになったら早めに論理廃止したいが、処理済みになるまでの期間はまちまち」という場合、次のように 2 つのタスクを並べます。

| 処理ID | 処理種別 | 期間経過チェック用：timestamp要経過日数 | データ検索条件設定 シートの条件 |
| --- | --- | --- | --- |
| task-1 | 論理廃止 | 7 | `processed = true` |
| task-2 | 異常データ存在チェック | 90 | `processed = false` |

## 設定例

### 論理廃止の場合

#### 1. テスト用テーブルとデータの準備

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    is_deleted boolean DEFAULT false,
    updated_at timestamp,
    updated_by varchar,
    PRIMARY KEY (num1)
);

INSERT INTO test_table (num1, char1) VALUES (123, 'abc');
```

#### 2. Excel 設定ファイルの編集

| 処理ID | DB接続ID | 処理種別 | テーブル名 | IDカラム名 | IDカラム型リテラル記号 |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | 論理廃止 | test_table | num1 | (none) |

| 削除フラグカラム名 | 論理廃止：更新用timestampカラム名 | 論理廃止：更新用ユーザIDカラム名 | 論理廃止：更新用ユーザIDカラム型リテラル記号 | 論理廃止：更新用ユーザIDカラム値 |
| --- | --- | --- | --- | --- |
| is_deleted | updated_at | updated_by | quotes(') | batch-user |

実行すると、`is_deleted` が `true` に、`updated_at` が実行時刻に、`updated_by` が `batch-user` に更新されます（レコード自体は削除されません）。

### 経過日数による絞り込みの場合

#### 1. テスト用テーブルとデータの準備

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    last_updated timestamp,
    PRIMARY KEY (num1)
);

-- 5日前に更新されたことになっているレコード（削除対象）
INSERT INTO test_table (num1, char1, last_updated) VALUES (123, 'abc', now() - interval '5 days');
-- 1日前に更新されたことになっているレコード（削除対象外）
INSERT INTO test_table (num1, char1, last_updated) VALUES (456, 'def', now() - interval '1 days');
```

#### 2. Excel 設定ファイルの編集

| 処理ID | DB接続ID | 処理種別 | テーブル名 | IDカラム名 | IDカラム型リテラル記号 |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | 削除 | test_table | num1 | (none) |

| 期間経過チェック用：timestampカラム名 | 期間経過チェック用：timestampカラム型 | 期間経過チェック用：timestamp要経過日数 |
| --- | --- | --- |
| last_updated | LocalDateTime | 3 |

実行すると、`last_updated` が3日以上前の `num1=123` のレコードのみ削除され、`num1=456` のレコードは残ります。

> **要経過日数の判定基準について:** ここでいう「3日」は**日付ベースではなく時間ベース**です。内部では現在時刻と `last_updated` の差分を計算し、`72時間`（3日分）を超えているかどうかで判定しています（日付が3回変わったかどうかではありません）。そのため、日をまたぐタイミングによっては「日付上は3日前」でも対象外になったり、逆に「日付上は2日前」でも72時間を超えていれば対象になったりする点に注意してください。

### 異常データ存在チェックの場合

#### 1. テスト用テーブルとデータの準備

```sql
CREATE TABLE test_table (
    num1 integer,
    processed boolean,
    last_updated timestamp,
    PRIMARY KEY (num1)
);

-- 100日前から未処理のままのレコード（検知対象）
INSERT INTO test_table (num1, processed, last_updated) VALUES (123, false, now() - interval '100 days');
-- 100日前に処理済みになったレコード（検知対象外）
INSERT INTO test_table (num1, processed, last_updated) VALUES (456, true, now() - interval '100 days');
-- 1日前に登録された未処理のレコード（検知対象外）
INSERT INTO test_table (num1, processed, last_updated) VALUES (789, false, now() - interval '1 days');
```

#### 2. Excel 設定ファイルの編集

##### housekeep DB設定 シート

| 処理ID | DB接続ID | 処理種別 | テーブル名 | IDカラム名 | IDカラム型リテラル記号 |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | 異常データ存在チェック | test_table | num1 | (none) |

| 期間経過チェック用：timestampカラム名 | 期間経過チェック用：timestampカラム型 | 期間経過チェック用：timestamp要経過日数 |
| --- | --- | --- |
| last_updated | LocalDateTime | 90 |

##### データ検索条件設定 シート

| 処理ID | 条件カラム名 | 条件演算子 | 条件カラム型リテラル記号 | 条件カラム値 |
| --- | --- | --- | --- | --- |
| task-1 | processed | equal | (none) | false |

実行すると、`num1=123` のレコードのみが検知され、以下のような警告メールが送信されます（レコードは更新・削除されません）。

```
hostname: your-server

Record(s) that should not exist were found by the abnormal data check task(s) below:

- Task ID: task-1
  Table  : test_table
  Count  : 1
  num1: 123
```
