タスクが使用するデータベース接続情報を定義します。複数の接続を登録できます。

| 列名 | 必須 | 説明 |
| --- | --- | --- |
| DB接続ID | ○ | 接続を識別する ID。housekeep DB設定 シートから参照される |
| driver名 | ○ | JDBC ドライバの完全修飾クラス名（下表参照） |
| 接続url : protocol | ○ | 接続 URL のプロトコル部分（下表参照） |
| 接続url : サーバ | ○ | データベースサーバのホスト名または IP アドレス |
| 接続url : port | ○ | ポート番号 |
| 接続url : database | ○ | データベース名 |
| 接続url : schema | — | スキーマ名（省略可、**PostgreSQL のみ** — MySQL / MariaDB にはスキーマの概念がなく、この列は無視される） |
| ユーザ名 | ○ | 接続ユーザ名 |
| password | ○ | 接続パスワード。文字数制限なし。値を直接書く代わりに `${VAR_NAME}` 参照（下記参照）を使うことを推奨 |

パスワードを Excel ファイルに直接記載すると、その Excel ファイル自体が機密情報（リポジトリにコミットしたり不用意に共有してはいけないもの）になってしまいます。これを避けるため、`${VAR_NAME}` 形式の変数参照を使うことができます。変数は OS 環境変数・JVM システムプロパティ・`application.properties` のいずれかで定義してください（詳細は[設定ファイル](page?id=housekeep-db/config&lang=ja)を参照）。

```properties
password 列: ${DB_PASSWORD}
```

```bash
export DB_PASSWORD=実際のパスワード
```

**対応データベース**

| データベース | driver名 | 接続url : protocol |
| --- | --- | --- |
| PostgreSQL | `org.postgresql.Driver` | `postgresql` |
| MySQL / MariaDB | `org.mariadb.jdbc.Driver` | `mysql` |

## 設定例

具体的な設定例は[クイックスタート](page?id=housekeep-db/quickstart&lang=ja)を参照してください。
