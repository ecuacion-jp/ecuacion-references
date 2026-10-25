housekeep DB設定 シートで指定したテーブルに対する検索に、条件を追加する機能です。必要ない場合はシートを空のままにしてください。

たとえば、`exit_code` 列が `COMPLETED` のレコードのみを削除したい場合に使用します。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| 処理ID | ○ | 条件を適用するタスクの ID |
| 条件カラム名 | ○ | WHERE 条件に使用する列名 |
| 条件演算子 | ○ | `equal` または `not equal`（下記参照） |
| 条件カラム型リテラル記号 | ○ | 列の値にクォートが必要かどうか。`(none)` または `quotes(')` |
| 条件カラム値 | ○ | WHERE 条件の値。NULL と比較する場合は `(null)` と記載します |

1 つのタスクに複数の条件を設定する場合は、同じ 処理ID で複数行記載します（AND 条件で結合されます）。

条件演算子と条件カラム値の組み合わせにより、次の条件が生成されます。

| 条件演算子 | 条件カラム値 | 生成される条件 |
| --- | --- | --- |
| equal | 値 | `列 = 値` |
| not equal | 値 | `(列 <> 値 OR 列 IS NULL)` |
| equal | `(null)` | `列 IS NULL` |
| not equal | `(null)` | `列 IS NOT NULL` |

- 値を指定した `not equal` は、列が NULL のレコードも対象にします。SQL では `NULL <> 値` が真にならないため、`列 <> 値` だけでは NULL のレコードが対象から漏れるためです。NULL のレコードを除外したい場合は、同じ列について `not equal` と `(null)` を指定した行を追加してください。
- `(null)` は、条件カラム型リテラル記号の指定にかかわらず NULL として扱われます。

## 設定例

### 1. テスト用テーブルとデータの準備

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    exit_code varchar,
    PRIMARY KEY (num1)
);

INSERT INTO test_table (num1, char1, exit_code) VALUES (123, 'abc', 'COMPLETED');
INSERT INTO test_table (num1, char1, exit_code) VALUES (456, 'def', 'FAILED');
```

### 2. Excel 設定ファイルの編集

#### housekeep DB設定 シート

| 処理ID | DB接続ID | 処理種別 | テーブル名 | IDカラム名 | IDカラム型リテラル記号 |
| --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | 削除 | test_table | num1 | (none) |

#### データ検索条件設定 シート

| 処理ID | 条件カラム名 | 条件演算子 | 条件カラム型リテラル記号 | 条件カラム値 |
| --- | --- | --- | --- | --- |
| task-1 | exit_code | equal | quotes(') | COMPLETED |

実行すると、`exit_code` が `COMPLETED` の `num1=123` のみが対象になり、`FAILED` の `num1=456` は対象外になります。
