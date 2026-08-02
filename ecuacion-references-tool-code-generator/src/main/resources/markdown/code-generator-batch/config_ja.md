## application.properties

Spring Boot の外部設定ファイルは以下の優先順位で読み込まれます（上位が下位を上書き）。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | JAR と同じディレクトリの `config/application.properties` |
| 3（低） | JAR と同じディレクトリの `application.properties` |

> **Note:** 作成する `application.properties` には、変更したい設定項目だけを記述すれば十分です。記述しなかった項目は、以下に記載のデフォルト値のまま動作します。

### カスタム application.properties を使う場合

JAR と同じディレクトリ、または `config/` サブディレクトリに配置します。

```
/your-work-dir/
├── ecuacion-tool-code-generator-batch-x.x.x.jar
├── application.properties          ← 埋め込み設定を上書き
└── config/
    └── application.properties      ← こちらでも可（優先度高）
```

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-batch-x.x.x.jar
```

### 設定できる項目

追加の設定は `application.properties` に記述してください。

#### 入出力ディレクトリ

| プロパティ | 説明 | デフォルト |
| --- | --- | --- |
| `input-dir` | Excel ファイルを置くディレクトリ。カンマ区切りで複数指定可能（例: `./dir1,./dir2`） | `./excel-format` |
| `output-dir` | 生成ソースの出力先 | `./products/` |

#### メール通知（バッチ失敗時）

バッチ失敗時に `SplibMailUtil` で管理者へメール通知します。`spring.mail.*` /
`jp.ecuacion.splib.mail.*` の各プロパティ・デフォルト値・設定例（Gmail含む）は
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=ja)
を参照してください。

---

## logback-spring.xml

Logback の設定ファイルは以下の優先順位で読み込まれます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dlogging.config=...` で指定したパス |
| 2 | カレントディレクトリの `config/logback-spring.xml` |
| 3（低） | カレントディレクトリ直下の `logback-spring.xml` |

> **Note:** 優先度2・3の「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。JAR と同じディレクトリに `cd` してから起動する運用（下記）であれば、実質的に「JAR と同じディレクトリ」基準になります。別のディレクトリから起動する場合は、そちらのディレクトリ基準で探索される点に注意してください。
>
> 優先度2・3はSpring Boot自体の機能ではなく、`ecuacion-splib-core`（`SplibEnvironmentPostProcessor`）が提供する ecuacion 独自の拡張です。`application.properties`と挙動を揃えるために、`config/`とカレントディレクトリ直下の両方を自動的に見るようにしています。

### カスタム logback-spring.xml を使う場合

**方法 1 — `config/` サブディレクトリに配置（推奨）:**

```
/your-work-dir/
├── ecuacion-tool-code-generator-batch-x.x.x.jar
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-code-generator-batch-x.x.x.jar
```

**方法 1b — カレントディレクトリ直下に直接配置:**

```
/your-work-dir/
├── ecuacion-tool-code-generator-batch-x.x.x.jar
└── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-code-generator-batch-x.x.x.jar
```

**方法 2 — パスを明示:**

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-code-generator-batch-x.x.x.jar
```

### 設定例

コンソールとファイルに出力する最小構成の例です。

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
