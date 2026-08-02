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
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `true`（未設定時に適用されるデフォルト）: `api/public/executeScript` へのアクセスは拒否（403）されます。APIを呼び出すには `api/key/executeScript`（`X-Api-Key` ヘッダによる認証が必須）を使用してください。`false`: `api/public/executeScript` が有効になります。信頼できる内部ネットワークでのみ使用してください。（どちらのエンドポイントでも、スクリプトごとに許可するHTTPメソッドはHTTPメソッドのプレフィックス（後述）で制御されます。） |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | `api/key/executeScript` の `X-Api-Key` ヘッダの値と照合する共有シークレットが書かれたファイルのパス。スクリプトパス（後述）と同様に `${ENV_VAR}` 展開に対応しています。省略可能 — 未設定時のデフォルトは下記の[ecuacion-tool-command-api-key.txt](#ecuacion-tool-command-api-key.txt)を参照。 |

例:

```properties
jp.ecuacion.tool.command-api.api-key-required=true
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

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

## ecuacion-tool-command-api-key.txt

`api/key/executeScript` の `X-Api-Key` ヘッダの値と照合する共有シークレットが書かれたファイル（[アクセス制御](#アクセス制御)参照）は、以下の優先順位で解決されます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `jp.ecuacion.tool.command-api.api-key-file-path` で指定したパス |
| 2 | カレントディレクトリの `config/ecuacion-tool-command-api-key.txt` |
| 3（低） | カレントディレクトリ直下の `ecuacion-tool-command-api-key.txt` |

優先度2・3は、`api-key-file-path` を一切設定しなくても使える、手軽な検証・ローカル用途向けのゼロコンフィグなデフォルトです。

> **Note:** `application.properties` / `ecuacion-tool-command-api.properties` と異なり、優先度2・3は JVM の作業ディレクトリ（`user.dir`）を基準にした単純なファイル存在チェックであり、Spring Boot の `spring.config.location` / `classpath:` 検索は経由しません。単独起動（`java -jar`）の場合は説明通りに動作しますが、既存の Tomcat 等にデプロイする場合（後述）は `user.dir` が Tomcat 自身の作業ディレクトリになり、WAR や `app-conf` オーバーレイディレクトリとは無関係なため、このデフォルトは確実には機能しません。その場合は `api-key-file-path` を明示的に設定してください。

ファイルの内容はリクエストのたびに読み込まれ（前後の空白・改行はトリムされます）、ファイルの内容を差し替えるだけでアプリを再起動せずにキーをローテーションできます。

### カスタム ecuacion-tool-command-api-key.txt を使う場合

**方法 1 — `config/` サブディレクトリに配置:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    └── ecuacion-tool-command-api-key.txt
```

**方法 2 — WAR と同じディレクトリに直接配置:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── ecuacion-tool-command-api-key.txt
```

**方法 3 — パスを明示（本番環境ではこちらを推奨）:**

```properties
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

本番環境では、キーがアプリのデプロイ物と一緒にバンドル・バックアップ・上書きされないよう、デプロイディレクトリの外（シークレット用ボリュームや、より厳格なパーミッションの場所など）を指定するこの方法を推奨します。

### 比較方式: 平文 vs. bcrypt

デフォルトでは、ファイル内の全行が平文として比較されます。生のキーをアプリが読み取れる場所に一切保持したくない場合は、以下のプロパティを設定するとbcryptハッシュとして扱われます。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | `PLAIN`（デフォルト）: 全行を平文キーとして直接比較します。`BCRYPT`: 全行をbcryptハッシュとして扱い、`BCryptPasswordEncoder.matches`で比較します。認識できない値が設定された場合は起動時に警告ログを出力し、`PLAIN`として扱います。 |

```properties
jp.ecuacion.tool.command-api.api-key-comparison-mode=BCRYPT
```

> **Note:** このモードは**ファイル全体**に適用されます（全行が平文、または全行がbcryptハッシュのいずれかである必要があります）。同一ファイル内で平文キーとbcryptハッシュを混在させることはサポートしていません。

キーのbcryptハッシュを生成する例:

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

### 設定例

このファイルの中身は共有シークレットの値そのものだけです。キー名も `properties` 形式も不要です。1行につき1つのキーを書けます。複数行書いた場合、どの行のキーが提示されても受理されます。呼び出し元ごとに1つ発行しておけば、該当行を削除するだけで他のキーに影響を与えずに失効できます。

空行、および `#` で始まる行（前後の空白を除いた上で判定）はコメント行として無視されます。どのキーが誰向けかを書き添えておけば、失効させたいキーを探しやすくなります。

```
# key for client A
04f1befd704277c4b76afd01d655e6f1e8e36af9f74abe3a010d539ed3ac88cf

# key for client B
dcef325238aed9023681c8971d6df53080c536d0643692f9cad5a465118d5e79
```

キーの値自体は十分に長いランダムな値を推奨します。`PLAIN`（デフォルト）の場合は値をそのまま1行追記し、`BCRYPT` の場合は先にbcryptハッシュ化してから追記します（ハッシュ化コマンドの例は上記「比較方式: 平文 vs. bcrypt」を参照）。`X-Api-Key` ヘッダに設定すべき値は、ハッシュ化前の元の値です。

いずれのモードでも、ファイルの各行の値（前後の空白・改行はトリムされます）のいずれかと一致する `X-Api-Key` ヘッダを持つリクエストのみが `api/key/executeScript` を呼び出せます。

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

**方法 1b — カレントディレクトリ直下に直接配置:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
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
