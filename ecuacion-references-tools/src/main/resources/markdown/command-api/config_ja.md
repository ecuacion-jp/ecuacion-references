## application.properties

Spring Boot の外部設定ファイルは以下の優先順位で読み込まれます（上位が下位を上書き）。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | WAR と同じディレクトリの `config/application.properties` |
| 3（低） | WAR と同じディレクトリの `application.properties` |

> **Note:** 外部ファイルは埋め込み設定を**置き換えるのではなく、マージ**されます。外部ファイルで明示的に定義したキーのみが上書きされ、それ以外の埋め込み設定はそのまま有効です。

スクリプト登録用の `ecuacion-tool-command-api.properties`（後述）も、`application.properties` と全く同じ優先順位・配置ルールで読み込まれます。

### カスタム application.properties / ecuacion-tool-command-api.properties を使う場合

WAR と同じディレクトリ、または `config/` サブディレクトリに配置します。

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── application.properties               ← 埋め込み設定を上書き
├── ecuacion-tool-command-api.properties  ← スクリプト登録
└── config/
    ├── application.properties            ← こちらでも可（優先度高）
    └── ecuacion-tool-command-api.properties
```

特定のパスを明示したい場合はシステムプロパティで指定します。`ecuacion-tool-command-api.properties` も同時に外部化したい場合は、単一ファイルではなく**ディレクトリ**を指定してください（単一ファイルを指定すると、そのファイルだけが読み込まれます）。

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-command-api-x.x.x.war
```

### 設定できる項目

#### スクリプトの登録

`ecuacion-tool-command-api.properties` に以下の形式でスクリプトを登録します。

```properties
script.<スクリプトID>=<スクリプトの絶対パス>
```

例:

```properties
script.say-hello=/opt/scripts/sayHello.sh
script.daily-batch=/opt/scripts/dailyBatch.sh
```

#### 環境変数の使用

スクリプトのパスに環境変数を使用できます（`${ENV_VAR}` 形式）。

```properties
script.say-hello=${SCRIPT_DIR}/sayHello.sh
```

---

## logback-spring.xml

デフォルトではコンソールに INFO レベル以上のログが出力されます。出力先やログレベルを変更したい場合は、`-Dlogging.config` でカスタムファイルのパスを明示します。

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-command-api-x.x.x.war
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

---

## 既存の Tomcat 等にデプロイする場合

WAR と同じディレクトリという概念がないため、代わりに `CLASSPATH` 環境変数で指定したディレクトリを使います。Tomcat の場合は `${CATALINA_HOME}/bin/setenv.sh` を作成（または編集）します。

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

このディレクトリに置いた `application.properties` / `ecuacion-tool-command-api.properties` は、Spring Boot の `classpath:` 探索により自動的にマージされます。`logback-spring.xml` を差し替えたい場合は、この場合も引き続き `-Dlogging.config` でパスを明示してください。
