ここでは、テーブルの全レコードを hard delete（物理削除）する最もシンプルな例を通じてツールの基本的な使い方を説明します。

## 前提

- JAR ファイルと Excel 設定ファイルのサンプルを[セットアップ](page?id=housekeep-db/setup&lang=ja)に従って準備済みであること
- PostgreSQL または MySQL / MariaDB のいずれかが利用可能であること

## 手順

### 1. テスト用テーブルとデータの準備

```sql
CREATE TABLE test_table (
    num1 integer,
    char1 varchar,
    PRIMARY KEY (num1)
);

INSERT INTO test_table (num1, char1) VALUES (123, 'abc');
```

### 2. Excel 設定ファイルの編集

サンプルの Excel ファイルを開き、以下の 2 つのシートを設定します。

#### DB Connection Settings シート

利用するデータベースに応じて、いずれか一方を設定してください。

**PostgreSQL の場合**

| DB Connection ID | Driver Name | Connection URL: Protocol | Connection URL: Server | Connection URL: Port | Connection URL: Database | Connection URL: Schema | Username | Password |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| test-conn | org.postgresql.Driver | postgresql | localhost | 5432 | mydb | public | myuser | mypassword |

**MySQL / MariaDB の場合**

| DB Connection ID | Driver Name | Connection URL: Protocol | Connection URL: Server | Connection URL: Port | Connection URL: Database | Connection URL: Schema | Username | Password |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| test-conn | org.mariadb.jdbc.Driver | mysql | localhost | 3306 | mydb | | myuser | mypassword |

#### Housekeep DB Settings シート

| Task ID | DB Connection ID | Soft / Hard Delete | Soft / Hard Delete (internal value) | Table Name | ID Column Name | ID Column Literal Symbol | （以降は空白） |
| --- | --- | --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | Hard Delete | HARD_DELETE | test_table | num1 | (none) | |

**ポイント:**
- `Soft / Hard Delete`: ドロップダウンから「Hard Delete」を選択（英語版 Excel の場合）
- `Soft / Hard Delete (internal value)`: `HARD_DELETE`（ツールが読み取る内部値）
- `ID Column Literal Symbol`: `num1` は integer 型なのでクォートは不要 → `(none)` を指定
  - varchar 型の場合は `quotes(')` を指定します

### 3. ツールを実行する

```bash
java -jar ecuacion-tool-housekeep-db-x.x.x.jar excelPath=/path/to/your-settings.xlsx
```

`test_table` から INSERT したレコードが削除されれば成功です。

---

経過日数での[フィルタリング](page?id=housekeep-db/excel-settings&lang=ja#経過日数による絞り込み任意)や、[soft delete](page?id=housekeep-db/excel-settings&lang=ja#soft-delete論理削除用列soft-delete-時のみ)の実行方法など、他の設定項目については[Excel設定ファイル](page?id=housekeep-db/excel-settings&lang=ja)を参照してください。
