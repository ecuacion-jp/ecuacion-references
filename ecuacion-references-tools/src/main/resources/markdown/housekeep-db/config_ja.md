このページでは、`ecuacion-tool-housekeep-db` が使用する2つの設定ファイル — `application.properties` ・ `logback-spring.xml` — とその配置方法について説明します。

## application.properties

本ファイルの読み取りはhousekeep-db独自の仕組みではなく、Spring Bootの機能です。`application.yml` / `application.yaml` でも全く同じように動作します。網羅的な説明は[Spring Boot公式リファレンス](https://docs.spring.io/spring-boot/reference/features/external-config.html)を参照してください。配置場所については下記の[ファイルの配置](#ファイルの配置)を参照してください。

### 設定できる項目

| プロパティ | 必須 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `jp.ecuacion.tool.housekeep-db.excel-path` | ○ | — | 実行するExcel設定ファイルのパス |
| `jp.ecuacion.tool.housekeep-db.max-select-lines` | — | `1000` | メインのSELECTが1ループあたりに取得・コミットする行数 |
| `jp.ecuacion.tool.housekeep-db.target-system-name` | — | —<br>（未設定時は非表示） | ジョブ開始ログ、異常データ存在チェックの警告メールの件名に表示するシステム名。<br>未設定の場合はその部分が省略されます |

```properties
jp.ecuacion.tool.housekeep-db.excel-path=/path/to/your-settings.xlsx
jp.ecuacion.tool.housekeep-db.max-select-lines=1000
jp.ecuacion.tool.housekeep-db.target-system-name=my-system
```

### メール送信の設定

エラー発生時のエラーメール、および[異常データ存在チェック](page?id=housekeep-db/excel-settings/housekeep-db-settings&lang=ja)でレコードが見つかった場合の警告メールの送信に使用します。警告メールの宛先はエラーメールと同じです。

| プロパティ | 必須 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `spring.mail.host` | △ | — | SMTPサーバのホスト名 |
| `spring.mail.port` | — | `587` | SMTPサーバのポート |
| `spring.mail.username` | △ | — | SMTP認証のユーザ名（送信元アドレスとしても使用） |
| `spring.mail.password` | △ | — | SMTP認証のパスワード |
| `jp.ecuacion.splib.mail.address-csv-on-system-error` | △ | — | エラーメール・警告メールの宛先（カンマ区切りで複数指定可） |
| `jp.ecuacion.splib.mail.title-prefix` | — | （なし） | メール件名の先頭に付与する文字列 |

△：メールを送信する場合は必須です。未設定の場合はメールを送信せず、その旨を `WARN` レベルでログ出力して処理を継続します。

警告メールの件名は `<title-prefix>[WARN] HousekeepDb:<target-system-name>` の形式です。

> **Note:** Spring Boot は `application.properties` を ISO-8859-1 として読み込むため、`title-prefix` に日本語などの非ASCII文字を含める場合は `application.yml`（UTF-8 として読み込まれる）に記載してください。

---

## logback-spring.xml

Logback の設定ファイルです。もちろん自由に設定できますが、splib が appenders / loggers 用の
built-in な include リソースを用意しているため、それを使うと以下のように端的に書けます。

### 設定例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="logback-spring-app-common.xml" />

    <property name="log-dir" value="/path/to/logs/directory" />
    <include resource="logback-spring-appenders-batch.xml" />

    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-batch-to-console-and-files.xml" />
</configuration>
```

---

## ファイルの配置

`application.properties` の配置ルールは、housekeep-db独自ではなく素のSpring Bootの外部設定機能です。`logback-spring.xml` も*ほぼ*同じですが、こちらはSpring Boot自体には同等の探索機能が無いため、`ecuacion-splib-core` が拡張として提供しています。いずれも配置ルールの考え方は共通で、システムプロパティによるパス指定・`config`サブディレクトリ・カレントディレクトリの3段階です（後者2つはいずれもアプリの起動元のカレントディレクトリ基準で解決されます）。

| ファイル | 優先度1（高） | 優先度2 | 優先度3（低） |
| --- | --- | --- | --- |
| `application.properties` | `-Dspring.config.location=...` で指定したパス | `config` サブディレクトリ | 同じディレクトリ |
| `logback-spring.xml` | `-Dlogging.config=...` で指定したパス | `config` サブディレクトリ | 同じディレクトリ |

例として `application.properties` の場合（`logback-spring.xml` も同じ考え方で、ファイル名を読み替えるだけです）:

```
/your-work-dir/
├── ecuacion-tool-housekeep-db-x.x.x.jar
├── application.properties   ← 優先度3
└── config/
    └── application.properties   ← 優先度2（こちらが優先）
```

`java -jar` は `/your-work-dir` から実行する必要があります。「同じディレクトリ」「`config` サブディレクトリ」はいずれも、JARファイルが置かれている場所ではなく、アプリを起動したカレントディレクトリを基準に解決されます。

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-housekeep-db-x.x.x.jar
```

> **Note:** `-Dspring.config.location` は**ディレクトリ**を指定するものです。単一ファイルを指定するとそのファイルだけが読み込まれます。`logback-spring.xml` は専用のシステムプロパティ（上の表の `-Dlogging.config`）を使い、`-Dspring.config.location` の影響は受けません。
