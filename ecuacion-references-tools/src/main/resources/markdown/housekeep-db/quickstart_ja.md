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

#### DB接続設定 シート

利用するデータベースに応じて、いずれか一方を設定してください。

**PostgreSQL の場合**

| DB接続ID | driver名 | 接続url : protocol | 接続url : サーバ | 接続url : port | 接続url : database | 接続url : schema | ユーザ名 | password |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| test-conn | org.postgresql.Driver | postgresql | localhost | 5432 | mydb | public | myuser | mypassword |

**MySQL / MariaDB の場合**

| DB接続ID | driver名 | 接続url : protocol | 接続url : サーバ | 接続url : port | 接続url : database | 接続url : schema | ユーザ名 | password |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| test-conn | org.mariadb.jdbc.Driver | mysql | localhost | 3306 | mydb | | myuser | mypassword |

#### housekeep DB設定 シート

| 処理ID | DB接続ID | 論理廃止 / 削除 | テーブル名 | IDカラム名 | IDカラム型リテラル記号 | （以降は空白） |
| --- | --- | --- | --- | --- | --- | --- |
| task-1 | test-conn | 削除 | test_table | num1 | (none) | |

**ポイント:**
- `論理廃止 / 削除`: ドロップダウンから「削除」を選択
- `IDカラム型リテラル記号`: `num1` は integer 型なのでクォートは不要 → `(none)` を指定
  - varchar 型の場合は `quotes(')` を指定します

### 3. application.properties の設定

JAR と同じ場所に `application.properties` を作成（または編集）し、先ほど設定した Excel ファイルのパスを指定します。

```properties
jp.ecuacion.tool.housekeep-db.excel-path=/path/to/your-settings.xlsx
```

### 4. ツールを実行する

```bash
java -jar ecuacion-tool-housekeep-db-x.x.x.jar
```

`test_table` から INSERT したレコードが削除されれば成功です。
