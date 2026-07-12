# code-generator-web 設定ファイル

## application.properties

Spring Boot の外部設定ファイルは以下の優先順位で読み込まれます（上位が下位を上書き）。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | WAR と同じディレクトリの `config/application.properties` |
| 3（低） | WAR と同じディレクトリの `application.properties` |

> **Note:** 作成する `application.properties` には、変更したい設定項目だけを記述すれば十分です。記述しなかった項目は、以下に記載のデフォルト値のまま動作します。

### カスタム application.properties を使う場合

WAR と同じディレクトリ、または `config/` サブディレクトリに配置します。

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
├── application.properties          ← 埋め込み設定を上書き
└── config/
    └── application.properties      ← こちらでも可（優先度高）
```

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

### 設定できる項目

追加の設定は `application.properties` に記述してください。

#### アプリ設定

| プロパティ | 説明 | デフォルト |
| --- | --- | --- |
| `app.work-root-dir` | 一時作業ファイルのベースディレクトリ | `./app-work` |

#### メール通知（エラー発生時）

SMTP 接続には Spring Boot 標準の `spring.mail.*` を使います。

> **Note:** 以下のプロパティにはデフォルト値がありません。`spring.mail.host` / `spring.mail.username` / `spring.mail.password` / `jp.ecuacion.splib.mail.address-csv-on-system-error` のいずれかを設定しない場合、エラー発生時のメール通知は行われません（＝デフォルトは「メール送信なし」です）。

**SMTP 設定（`spring.mail.*`）:**

| プロパティ | 説明 |
| --- | --- |
| `spring.mail.host` | SMTP サーバーのホスト名 |
| `spring.mail.port` | SMTP ポート（STARTTLS: `587`、SSL: `465`） |
| `spring.mail.username` | 送信元アドレス（SMTP ログインユーザー名） |
| `spring.mail.password` | SMTP パスワード（Gmail の場合はアプリパスワード） |
| `spring.mail.properties.mail.smtp.auth` | SMTP 認証が必要な場合は `true` |
| `spring.mail.properties.mail.smtp.ssl.enable` | SSL（ポート 465）の場合は `true`、STARTTLS（587）の場合は `false` |

**アプリ設定（`jp.ecuacion.splib.mail.*`）:**

| プロパティ | 説明 |
| --- | --- |
| `jp.ecuacion.splib.mail.title-prefix` | メール件名に付けるプレフィックス（例: 環境名） |
| `jp.ecuacion.splib.mail.address-csv-on-system-error` | システムエラー時の通知先アドレス（カンマ区切り） |
| `jp.ecuacion.splib.mail.smtp.bounce-address` | バウンスメールの受信アドレス（省略可） |
| `jp.ecuacion.splib.mail.smtp.checks-certificate` | TLS 証明書検証をスキップする場合は `false`（デフォルト: `true`） |
| `jp.ecuacion.splib.mail.debug` | JavaMail デバッグ出力を有効にする場合は `true`（デフォルト: `false`） |

**設定例 — 一般的な SMTP サーバー（ポート 587 / STARTTLS）:**

```properties
app.work-root-dir=./app-work

spring.mail.host=mail.example.com
spring.mail.port=587
spring.mail.username=no-reply@example.com
spring.mail.password=your-smtp-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=false

jp.ecuacion.splib.mail.title-prefix=[code-generator]
jp.ecuacion.splib.mail.address-csv-on-system-error=admin@example.com
```

**設定例 — Gmail（アプリパスワード必須）:**

> Gmail SMTP を使うには、Google アカウントで 2 段階認証を有効にし、[アプリパスワード](https://myaccount.google.com/apppasswords) を発行してください。

```properties
app.work-root-dir=./app-work

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-account@gmail.com
spring.mail.password=xxxx-xxxx-xxxx-xxxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.enable=false

jp.ecuacion.splib.mail.title-prefix=[code-generator]
jp.ecuacion.splib.mail.address-csv-on-system-error=your-account@gmail.com
```

---

## logback-spring.xml

Logback の設定ファイルは以下の優先順位で読み込まれます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dlogging.config=...` で指定したパス |
| 2（低） | WAR と同じディレクトリの `config/logback-spring.xml` |

> **Note:** `application.properties` と異なり、WAR と同じディレクトリ（`config/` なし）に置いた `logback-spring.xml` は Spring Boot に自動認識されません。`config/` サブディレクトリを使うか、パスを明示してください。

### カスタム logback-spring.xml を使う場合

**方法 1 — `config/` サブディレクトリに配置（推奨）:**

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
└── config/
    └── logback-spring.xml
```

```bash
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

**方法 2 — パスを明示:**

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

### 設定例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>./logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>./logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.springframework" level="WARN" />
    <logger name="jp.ecuacion" level="INFO" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>

</configuration>
```

主な調整ポイント:

| 項目 | 変更箇所 | 主な値 |
| --- | --- | --- |
| 全体ログレベル | `<root level="...">` | `DEBUG`, `INFO`, `WARN`, `ERROR` |
| パッケージ別レベル | `<logger name="..." level="...">` | 同上 |
| ログファイルパス | `<file>` / `<fileNamePattern>` | 書き込み可能な任意のパス |
| 保持日数 | `<maxHistory>` | 日数 |
