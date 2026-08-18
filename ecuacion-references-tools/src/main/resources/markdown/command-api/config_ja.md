## application.properties

Spring Boot の外部設定ファイルは以下の優先順位で読み込まれます（上位が下位を上書き）。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | WAR と同じディレクトリの `config/application.properties` |
| 3（低） | WAR と同じディレクトリの `application.properties` |

> **Note:** 外部ファイルは埋め込み設定を**置き換えるのではなく、マージ**されます。外部ファイルで明示的に定義したキーのみが上書きされ、それ以外の埋め込み設定はそのまま有効です。

> **Note:** このファイルの変更は、`ecuacion-splib-rest` の `clearPropertiesCache` エンドポイント（`POST /api/ecuacion-splib/key/clearPropertiesCache`）を呼ぶことでアプリを再起動せずに反映できます。仕組みと制限事項については[運用系エンドポイント](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=rest/operational-endpoints&lang=ja)を参照してください。このエンドポイント自体の認証は、以下で説明する `api-key-file-path` のキーとは別の、組み込み専用のAPIキーで行われます。

### 配置（任意）

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

### 設定できる項目

追加の設定は `application.properties` に記述してください。

#### アクセス制御

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `api/public/executeScript` を無効化し、代わりに `api/key/executeScript` と有効な `X-Api-Key` ヘッダを必須にするかどうか。デフォルト: `true`。 |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | `X-Api-Key` と照合する共有シークレットが書かれたファイルのパス。 |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | api-key ファイルの各行を平文（`PLAIN`）またはbcryptハッシュ（`BCRYPT`）のどちらとして比較するか。デフォルト: `PLAIN`。 |

各プロパティの詳細、およびapi-keyファイル自体の管理方法については[アクセス制御](page?id=command-api/access-control&lang=ja)を参照してください。

#### メール通知（エラー発生時）

システムエラー発生時に `SplibMailUtil` で管理者へメール通知します。`spring.mail.*` / `jp.ecuacion.splib.mail.*` の各プロパティ・デフォルト値・設定例（Gmail含む）は
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=ja)を参照してください。

---

## ecuacion-tool-command-api.properties

スクリプト登録用の設定ファイルです。`application.properties` と全く同じ優先順位・配置ルールで読み込まれます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | WAR と同じディレクトリの `config/ecuacion-tool-command-api.properties` |
| 3（低） | WAR と同じディレクトリの `ecuacion-tool-command-api.properties` |

> **Note:** `application.properties` とは異なり、このファイルの変更は `clearPropertiesCache` エンドポイントでは反映**されません**。理由については[運用系エンドポイント](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=rest/operational-endpoints&lang=ja)を参照してください（`ContextRefresher` は `spring.config.name` の1番目＝プライマリしか確実にはリロードせず、このファイルは2番目以降の設定名として登録されているため）。スクリプト登録の変更を反映するにはアプリの再起動が必要です。

### 配置

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

#### 許可するHTTPメソッドの指定

スクリプト定義の値の先頭に `GET:` / `POST:` / `ALL:`（大文字小文字を区別しない）を付けると、`api/public/executeScript`（有効化されている場合）・`api/key/executeScript` の両方で、そのスクリプトを呼び出せるHTTPメソッドを制限できます。プレフィックスを省略した場合は `POST` のみ許可されます。

```properties
script.say-hello=GET:/opt/scripts/sayHello.sh
script.daily-batch=POST:/opt/scripts/dailyBatch.sh
script.status-check=ALL:/opt/scripts/statusCheck.sh
script.legacy-job=/opt/scripts/legacyJob.sh
```

| スクリプトID | 許可されるメソッド |
| --- | --- |
| `script.say-hello` | `GET` のみ |
| `script.daily-batch` | `POST` のみ |
| `script.status-check` | `GET` ・ `POST` いずれも |
| `script.legacy-job` | `POST` のみ（プレフィックス省略時のデフォルト） |

> **Note:** このプレフィックスは `api/public/executeScript` と `api/key/executeScript` の両方に同じルールで適用されます。両エンドポイントの違いはこのメソッド制限ではなく、`X-Api-Key` ヘッダによる認証が必須かどうかだけです。

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
| 2 | カレントディレクトリの `config/logback-spring.xml` |
| 3（低） | カレントディレクトリ直下の `logback-spring.xml` |

> **Note:** 優先度2・3の「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。WAR と同じディレクトリに `cd` してから起動する運用（下記）であれば、実質的に「WAR と同じディレクトリ」基準になります。別のディレクトリから起動する場合は、そちらのディレクトリ基準で探索される点に注意してください。
>
> 優先度2・3はSpring Boot自体の機能ではなく、`ecuacion-splib-core`（`SplibEnvironmentPostProcessor`）が提供する ecuacion 独自の拡張です。`application.properties`と挙動を揃えるために、`config/`とカレントディレクトリ直下の両方を自動的に見るようにしています。

### 配置

`config/` サブディレクトリ、またはアプリの起動元のカレントディレクトリ直下に配置します（上記のNote参照）。

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── logback-spring.xml   ← こちらでも可
└── config/
    └── logback-spring.xml   ← こちらが優先度高
```

特定のパスを明示したい場合はシステムプロパティで指定します。

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

---

## 既存の Tomcat 等にデプロイする場合

WAR と同じディレクトリという概念がないため、代わりに Spring Boot の `classpath:` 探索に乗せる形で外部ディレクトリを認識させます。方法は2つあります。

### 方法1 — `setenv.sh` で `CLASSPATH` を指定

Tomcat の場合は `${CATALINA_HOME}/bin/setenv.sh` を作成（または編集）します。

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

このディレクトリに置いた `application.properties` / `ecuacion-tool-command-api.properties` は、Spring Boot の `classpath:` 探索により自動的にマージされます。`logback-spring.xml` を差し替えたい場合は、この場合も引き続き `-Dlogging.config` でパスを明示してください。

> **Note:** `CLASSPATH` は Tomcat プロセス全体で共有されます。同じ Tomcat に複数の ecuacion 製アプリ（例: `ecuacion-tool-command-api` と `ecuacion-tool-code-generator`）を同居させる場合、設定ファイルを置くディレクトリが共用されてしまい扱いにくくなります。アプリごとに設定を分けたい場合は方法2を使ってください。

### 方法2 — `META-INF/context.xml` でアプリ個別のディレクトリを指定（推奨）

`setenv.sh` を編集する必要がなく、複数の ecuacion 製アプリを同じ Tomcat に同居させても設定ファイルが混ざりません。`ecuacion-tool-command-api` の WAR には、あらかじめ以下の内容の `META-INF/context.xml` が同梱されています。

```xml
<Context>
	<Resources>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf/ecuacion-tool-command-api"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
	</Resources>
</Context>
```

アプリ個別のディレクトリ（`app-conf/ecuacion-tool-command-api`）に加えて、共用の `app-conf` 直下も併せてマウントされています。両方に同名のファイルがあった場合はアプリ個別側が優先され、片方にしかないファイルもそのまま認識されます（ディレクトリ単位のオーバーレイ）。

- **この Tomcat に `ecuacion-tool-command-api` の WAR しかデプロイしない場合**: `app-conf` 直下に直接設定ファイルを置けば十分です（`app-conf/ecuacion-tool-command-api/` の深い階層を作らなくて済みます）。
- **複数の ecuacion 製アプリを同居させる場合**: アプリごとに設定を分けたいファイルは `app-conf/ecuacion-tool-command-api/` に置いてください（`app-conf` 直下より優先されます）。

> **PREREQUISITE:** デプロイ前に、サーバー上に以下のディレクトリを作成しておく必要があります。存在しない状態でデプロイすると、Tomcat が `IllegalArgumentException` で起動に失敗します。
>
> ```
> ${catalina.base}/app-conf/ecuacion-tool-command-api/
>   (例: /usr/local/tomcat/app-conf/ecuacion-tool-command-api/)
> ```
>
> `mkdir -p` でこのディレクトリを作成すれば、親の `${catalina.base}/app-conf/` も同時に作られるため、実質コマンド1回で両方の前提条件を満たせます。

このディレクトリに `application.properties` / `ecuacion-tool-command-api.properties` / `logback-spring.xml` を置くと、いずれも自動的に認識されます（`logback-spring.xml` についても、この方式では `-Dlogging.config` の指定は不要です）。
