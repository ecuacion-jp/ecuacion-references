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
| `work-dir` | 一時作業ファイルのベースディレクトリ | `./app-work` |

#### メール通知（エラー発生時）

システムエラー発生時に `SplibMailUtil` で管理者へメール通知します。`spring.mail.*` /
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

> **Note:** 優先度2・3の「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。WAR と同じディレクトリに `cd` してから起動する運用（下記）であれば、実質的に「WAR と同じディレクトリ」基準になります。別のディレクトリから起動する場合は、そちらのディレクトリ基準で探索される点に注意してください。
>
> 優先度2・3はSpring Boot自体の機能ではなく、`ecuacion-splib-core`（`SplibEnvironmentPostProcessor`）が提供する ecuacion 独自の拡張です。`application.properties`と挙動を揃えるために、`config/`とカレントディレクトリ直下の両方を自動的に見るようにしています。

### カスタム logback-spring.xml を使う場合

**方法 1 — `config/` サブディレクトリに配置（推奨）:**

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

**方法 1b — カレントディレクトリ直下に直接配置:**

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
└── logback-spring.xml
```

```bash
cd /your-work-dir
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

---

## 既存の Tomcat 等にデプロイする場合

WAR と同じディレクトリという概念がないため、代わりに Spring Boot の `classpath:` 探索に乗せる形で外部ディレクトリを認識させます。方法は2つあります。

### 方法1 — `setenv.sh` で `CLASSPATH` を指定

Tomcat の場合は `${CATALINA_HOME}/bin/setenv.sh` を作成（または編集）します。

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

このディレクトリに置いた `application.properties` は、Spring Boot の `classpath:` 探索により自動的にマージされます。`logback-spring.xml` を差し替えたい場合は、この場合も引き続き `-Dlogging.config` でパスを明示してください。

> **Note:** `CLASSPATH` は Tomcat プロセス全体で共有されます。同じ Tomcat に複数の ecuacion 製アプリ（例: `ecuacion-tool-code-generator` と `ecuacion-tool-command-api`）を同居させる場合、設定ファイルを置くディレクトリが共用されてしまい扱いにくくなります。アプリごとに設定を分けたい場合は方法2を使ってください。

### 方法2 — `META-INF/context.xml` でアプリ個別のディレクトリを指定（推奨）

`setenv.sh` を編集する必要がなく、複数の ecuacion 製アプリを同じ Tomcat に同居させても設定ファイルが混ざりません。`ecuacion-tool-code-generator-web` の WAR には、あらかじめ以下の内容の `META-INF/context.xml` が同梱されています。

```xml
<Context>
	<Resources>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf/ecuacion-tool-code-generator"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
	</Resources>
</Context>
```

アプリ個別のディレクトリ（`app-conf/ecuacion-tool-code-generator`）に加えて、共用の `app-conf` 直下も併せてマウントされています。両方に同名のファイルがあった場合はアプリ個別側が優先され、片方にしかないファイルもそのまま認識されます（ディレクトリ単位のオーバーレイ）。

- **この Tomcat に `ecuacion-tool-code-generator-web` の WAR しかデプロイしない場合**: `app-conf` 直下に直接設定ファイルを置けば十分です（`app-conf/ecuacion-tool-code-generator/` の深い階層を作らなくて済みます）。
- **複数の ecuacion 製アプリを同居させる場合**: アプリごとに設定を分けたいファイルは `app-conf/ecuacion-tool-code-generator/` に置いてください（`app-conf` 直下より優先されます）。

> **PREREQUISITE:** デプロイ前に、サーバー上に以下のディレクトリを作成しておく必要があります。存在しない状態でデプロイすると、Tomcat が `IllegalArgumentException` で起動に失敗します。
>
> ```
> ${catalina.base}/app-conf/ecuacion-tool-code-generator/
>   (例: /usr/local/tomcat/app-conf/ecuacion-tool-code-generator/)
> ```
>
> `mkdir -p` でこのディレクトリを作成すれば、親の `${catalina.base}/app-conf/` も同時に作られるため、実質コマンド1回で両方の前提条件を満たせます。

このディレクトリに `application.properties` / `logback-spring.xml` を置くと、いずれも自動的に認識されます（`logback-spring.xml` についても、この方式では `-Dlogging.config` の指定は不要です）。
