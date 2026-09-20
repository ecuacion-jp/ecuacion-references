このページでは、`ecuacion-tool-command-api` が使用する3つの設定ファイル — `application.properties` ・ `ecuacion-tool-command-api-scripts.properties` ・ `logback-spring.xml` — とその配置方法について説明します。

## application.properties

本ファイルの読み取りはcommand-api独自の仕組みではなく、Spring Bootの機能です。`application.yml` / `application.yaml` でも全く同じように動作します。網羅的な説明は[Spring Boot公式リファレンス](https://docs.spring.io/spring-boot/reference/features/external-config.html)を参照してください。配置場所については下記の[ファイルの配置](#ファイルの配置)を参照してください。

### 設定できる項目

追加の設定は `application.properties` に記述してください。

#### アクセス制御

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `api/public/execute` を無効化し、代わりに `api/key/execute` と有効な `X-Api-Key` ヘッダを必須にするかどうか。デフォルト: `true`。 |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | `X-Api-Key` と照合する共有シークレットが書かれたファイルのパス。 |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | api-key ファイルの各行を平文（`PLAIN`）またはbcryptハッシュ（`BCRYPT`）のどちらとして比較するか。デフォルト: `BCRYPT`。 |

各プロパティの詳細、およびapi-keyファイル自体の管理方法については[アクセス制御](page?id=command-api/access-control&lang=ja)を参照してください。

#### スクリプト実行

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.script-timeout-seconds` | long | スクリプトの実行を待つ最大秒数。超過すると強制終了され、`504 Gateway Timeout` を返します。デフォルト: `60`。 |
| `jp.ecuacion.tool.command-api.script-max-output-bytes` | long | レスポンスに含める標準出力・標準エラー出力の上限バイト数（stdout・stderrそれぞれ独立に適用）。超過した時点以降の行は破棄され、レスポンスの `stdoutTruncated` / `stderrTruncated` が `true` になります。スクリプト自体は最後まで実行されます。デフォルト: `1048576`（1MiB）。 |

ハングするスクリプト（またはハングさせるパラメータ）でワーカースレッドが専有され続けるのを防ぐための設定です。また、大量の出力（大きなファイルの `cat` や無限出力ループ等）でJVMヒープを圧迫するのも防ぎます。

#### メール通知（エラー発生時）

システムエラー発生時に `SplibMailUtil` で管理者へメール通知します。`spring.mail.*` / `jp.ecuacion.splib.mail.*` の各プロパティ・デフォルト値・設定例（Gmail含む）は
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=ja)を参照してください。

---

## ecuacion-tool-command-api-scripts.properties

スクリプト登録用の設定ファイルです。配置ルールは `application.properties` と同じです（下記の[ファイルの配置](#ファイルの配置)を参照）。

### 設定できる項目

#### スクリプトの登録

`ecuacion-tool-command-api-scripts.properties` に以下の形式でスクリプトを登録します。

```properties
<スクリプトID>=<スクリプトの絶対パス>
```

例:

```properties
script.say-hello=/opt/scripts/sayHello.sh
script.daily-batch=/opt/scripts/dailyBatch.sh
```

#### 許可するHTTPメソッドの指定

スクリプト定義の値の先頭に `GET:` / `POST:` / `ALL:`（大文字小文字を区別しない）を付けると、`api/public/execute`（有効化されている場合）・`api/key/execute` の両方で、そのスクリプトを呼び出せるHTTPメソッドを制限できます。プレフィックスを省略した場合は `POST` のみ許可されます。

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

> **Note:** このプレフィックスは `api/public/execute` と `api/key/execute` の両方に同じルールで適用されます。両エンドポイントの違いはこのメソッド制限ではなく、`X-Api-Key` ヘッダによる認証が必須かどうかだけです。

#### 変数参照の使用

スクリプトのパスに `${VAR_NAME}` 形式の変数参照を使用できます。値は `application.properties`・
OS 環境変数・JVM システムプロパティなど、Spring Boot の `Environment` が解決できるあらゆる設定源
から取得されます。

```properties
script.say-hello=${SCRIPT_DIR}/sayHello.sh
```

---

## logback-spring.xml

Logbackの設定ファイルです。配置方法は下記の[ファイルの配置](#ファイルの配置)を参照してください。

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

## ファイルの配置

この3つのファイルをどこに置くかは、`ecuacion-tool-command-api` の起動方法によって異なります — 起動方法自体については[起動方法](page?id=command-api/launch-patterns&lang=ja)を参照してください。

> **Note:** `application.properties` の変更は、`ecuacion-splib-rest` の `clearPropertiesCache` エンドポイント（詳細は[運用系エンドポイント](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=rest/operational-endpoints&lang=ja)を参照）を呼ぶことでアプリを再起動せずに反映できます。一方 `ecuacion-tool-command-api-scripts.properties` の変更はこのエンドポイントでは反映**されません**（`ContextRefresher` は `spring.config.name` の1番目＝プライマリしか確実にはリロードせず、このファイルは2番目以降の設定名として登録されているため）— スクリプト登録の変更を反映するにはアプリの再起動が必要です。

### 単独で起動する場合

2つの`.properties`ファイルの配置ルールは、command-api独自ではなく素のSpring Bootの外部設定機能です。`logback-spring.xml`も*ほぼ*同じですが、こちらはSpring Boot自体には同等の探索機能が無いため、`ecuacion-splib-core`が拡張として提供しています。いずれも配置ルールの考え方は共通で、システムプロパティによるパス指定・`config`サブディレクトリ・デフォルトの配置場所の3段階です（後者2つはいずれもアプリの起動元のカレントディレクトリ基準で解決されます）。

<table>
<thead>
<tr><th>ファイル</th><th>優先度1（高）</th><th>優先度2</th><th>優先度3（低）</th></tr>
</thead>
<tbody>
<tr><td><code>application.properties</code></td><td rowspan="2"><code>-Dspring.config.location=...</code> で指定したパス</td><td rowspan="3"><code>config</code> サブディレクトリ</td><td rowspan="3">同じディレクトリ</td></tr>
<tr><td><code>ecuacion-tool-command-api-scripts.properties</code></td></tr>
<tr><td><code>logback-spring.xml</code></td><td><code>-Dlogging.config=...</code> で指定したパス</td></tr>
</tbody>
</table>

例として `application.properties` の場合（他の2つも同じ考え方で、ファイル名を読み替えるだけです）:

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── application.properties   ← 優先度3
└── config/
    └── application.properties   ← 優先度2（こちらが優先）
```

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-command-api-x.x.x.war
```

> **Note:** `-Dspring.config.location` は2つの `.properties` ファイル両方に効きます。単一ファイルを指定するとそのファイルだけが読み込まれるので、`application.properties` と `ecuacion-tool-command-api-scripts.properties` を両方外部化したい場合は**ディレクトリ**を指定してください。`logback-spring.xml` は専用のシステムプロパティ（上の表の `-Dlogging.config`）を使い、`-Dspring.config.location` の影響は受けません。

### 既存の Tomcat 等にデプロイする場合

WAR と同じディレクトリという概念がないため、代わりに Spring Boot の `classpath:` 探索に乗せる形で外部ディレクトリを認識させます。方法は2つあります。

#### 方法1 — `jp.ecuacion.tool.command-api.app-conf-dir` によるアプリ個別のディレクトリ指定

`jp.ecuacion.tool.command-api.app-conf-dir` システムプロパティで指定したディレクトリがclasspathとして設定されます。未指定の場合は `${catalina.base}/app-conf/ecuacion-tool-command-api` が使われます。
個別アプリごとにclasspathのディレクトリを設定できるため、複数のアプリを同じ Tomcat に同居させても設定ファイルが混ざりません。

`${CATALINA_HOME}/bin/setenv.sh` で `CATALINA_OPTS` として指定するのが一般的です。

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.command-api.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

#### 方法2 — `setenv.sh` で `CLASSPATH` を指定

Tomcat の場合は `${CATALINA_HOME}/bin/setenv.sh` を作成（または編集）します。

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

このディレクトリに置いた `application.properties` / `ecuacion-tool-command-api-scripts.properties` は、Spring Boot の `classpath:` 探索により自動的にマージされます。`logback-spring.xml` を差し替えたい場合は、この場合も引き続き `-Dlogging.config` でパスを明示してください。

> **Note:** `CLASSPATH` は Tomcat プロセス全体で共有されます。同じ Tomcat に複数の ecuacion 製アプリ（例: `ecuacion-tool-command-api` と `ecuacion-tool-code-generator`）を同居させる場合、設定ファイルを置くディレクトリが共用されてしまい扱いにくくなります。アプリごとに設定を分けたい場合は方法1を使ってください。
