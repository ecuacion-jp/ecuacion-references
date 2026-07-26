## application.properties

Spring Boot の外部設定ファイルは以下の優先順位で読み込まれます（上位が下位を上書き）。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | WAR と同じディレクトリの `config/application.properties` |
| 3（低） | WAR と同じディレクトリの `application.properties` |

> **Note:** 外部ファイルは埋め込み設定を**置き換えるのではなく、マージ**されます。外部ファイルで明示的に定義したキーのみが上書きされ、それ以外の埋め込み設定はそのまま有効です。

### アクセス制御

`executeScript` へのアクセスを制御するプロパティが2つあります（実際のHTTP挙動については[APIスペック](page?id=command-api/api-spec&lang=ja)を参照）。どちらも埋め込みの`application.properties`には意図的に設定されていません。未設定のままだと、デフォルトに静かにフォールバックするのではなく、起動時に警告ログが出力されます。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.allow-insecure-access` | boolean | `true`: `GET` が許可され、`POST` の `apiKey` 検証も省略されます。信頼できる内部ネットワークでのみ使用してください。`false`（未設定時に適用されるデフォルト）: `GET` は拒否（403）され、`POST` には有効な `apiKey` が必須です。 |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | `apiKey` POSTパラメータと照合する共有シークレットが書かれたファイルのパス。スクリプトパス（後述）と同様に `${ENV_VAR}` 展開に対応しています。 |

例:

```properties
jp.ecuacion.tool.command-api.allow-insecure-access=false
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

api-key ファイルの内容はリクエストのたびに読み込まれ（前後の空白・改行はトリムされます）、ファイルの内容を差し替えるだけでアプリを再起動せずにキーをローテーションできます。

### カスタム application.properties を使う場合

WAR と同じディレクトリ、または `config/` サブディレクトリに配置します。

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── application.properties   ← 埋め込み設定を上書き
└── config/
    └── application.properties   ← こちらでも可（優先度高）
```

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-command-api-x.x.x.war
```

> **Note:** 単一ファイルを指定すると、そのファイルだけが読み込まれます。`ecuacion-tool-command-api.properties`（後述）も同時に外部化したい場合は、単一ファイルではなく**ディレクトリ**を指定してください。

---

## ecuacion-tool-command-api.properties

スクリプト登録用の設定ファイルです。`application.properties` と全く同じ優先順位・配置ルールで読み込まれます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | WAR と同じディレクトリの `config/ecuacion-tool-command-api.properties` |
| 3（低） | WAR と同じディレクトリの `ecuacion-tool-command-api.properties` |

### カスタム ecuacion-tool-command-api.properties を使う場合

WAR と同じディレクトリ、または `config/` サブディレクトリに配置します。

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── ecuacion-tool-command-api.properties   ← スクリプト登録
└── config/
    └── ecuacion-tool-command-api.properties   ← こちらでも可（優先度高）
```

特定のパスを明示したい場合はシステムプロパティで指定します。単一ファイルを指定すると、そのファイルだけが読み込まれます。`application.properties` も同時に外部化したい場合は、単一ファイルではなく**ディレクトリ**を指定してください。

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

Logback の設定ファイルは以下の優先順位で読み込まれます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dlogging.config=...` で指定したパス |
| 2（低） | カレントディレクトリの `config/logback-spring.xml` |

> **Note:** 優先度2の「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。WAR と同じディレクトリに `cd` してから起動する運用（下記）であれば、実質的に「WAR と同じディレクトリの `config/`」と同じ意味になります。別のディレクトリから起動する場合は、そちらのディレクトリ基準で探索される点に注意してください。

### カスタム logback-spring.xml を使う場合

**方法 1 — `config/` サブディレクトリに配置（推奨）:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-command-api-x.x.x.war
```

**方法 2 — パスを明示:**

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
