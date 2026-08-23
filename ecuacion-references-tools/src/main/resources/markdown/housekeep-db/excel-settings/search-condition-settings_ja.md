housekeep DB設定 シートで指定したテーブルに対する検索に、条件を追加する機能です。必要ない場合はシートを空のままにしてください。

たとえば、`exit_code` 列が `COMPLETED` のレコードのみを削除したい場合に使用します。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| 処理ID | ○ | 条件を適用するタスクの ID |
| 条件カラム名 | ○ | WHERE 条件に使用する列名 |
| 条件カラム型リテラル記号 | ○ | 列の値にクォートが必要かどうか。`(none)` または `quotes(')` |
| 条件カラム値 | ○ | WHERE 条件の値 |

1 つのタスクに複数の条件を設定する場合は、同じ 処理ID で複数行記載します（AND 条件で結合されます）。

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

housekeep DB設定 シートで `task-1` が `test_table` を対象に定義済みであるとして、データ検索条件設定 シートに以下を追加します。

| 処理ID | 条件カラム名 | 条件カラム型リテラル記号 | 条件カラム値 |
| --- | --- | --- | --- |
| task-1 | exit_code | quotes(') | COMPLETED |

実行すると、`exit_code` が `COMPLETED` の `num1=123` のみが対象になり、`FAILED` の `num1=456` は対象外になります。
