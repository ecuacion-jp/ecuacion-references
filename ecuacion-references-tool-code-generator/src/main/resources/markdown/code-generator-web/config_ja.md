このページでは、`ecuacion-tool-code-generator-web` が使用する2つの設定ファイル — `application.properties` ・ `logback-spring.xml` — とその配置方法について説明します。

## application.properties

本ファイルの読み取りはcode-generator-web独自の仕組みではなく、Spring Bootの機能です。配置場所については下記の[ファイルの配置](#ファイルの配置)を参照してください。

### 設定できる項目

追加の設定は `application.properties` に記述してください。作成するファイルには、変更したい設定項目だけを記述すれば十分です。記述しなかった項目は、以下に記載のデフォルト値のまま動作します。

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

この2つのファイルをどこに置くかは、`ecuacion-tool-code-generator-web` の起動方法によって異なります。

### 単独で起動する場合

`application.properties` の配置ルールは、code-generator-web独自ではなく素のSpring Bootの外部設定機能です。`logback-spring.xml`も*ほぼ*同じですが、こちらはSpring Boot自体には同等の探索機能が無いため、`ecuacion-splib-core`が拡張として提供しています。いずれも配置ルールの考え方は共通で、システムプロパティによるパス指定・`config`サブディレクトリ・デフォルトの配置場所の3段階です（後者2つはいずれもアプリの起動元のカレントディレクトリ基準で解決されます）。

<table>
<thead>
<tr><th>ファイル</th><th>優先度1（高）</th><th>優先度2</th><th>優先度3（低）</th></tr>
</thead>
<tbody>
<tr><td><code>application.properties</code></td><td><code>-Dspring.config.location=...</code> で指定したパス</td><td rowspan="2"><code>config</code> サブディレクトリ</td><td rowspan="2">同じディレクトリ</td></tr>
<tr><td><code>logback-spring.xml</code></td><td><code>-Dlogging.config=...</code> で指定したパス</td></tr>
</tbody>
</table>

例として `application.properties` の場合（`logback-spring.xml` も同じ考え方で、ファイル名を読み替えるだけです）:

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
├── application.properties   ← 優先度3
└── config/
    └── application.properties   ← 優先度2（こちらが優先）
```

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-web-x.x.x.war

java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

> **Note:** 優先度2・3の「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。WAR と同じディレクトリに `cd` してから起動する運用であれば、実質的に「WAR と同じディレクトリ」基準になります。別のディレクトリから起動する場合は、そちらのディレクトリ基準で探索される点に注意してください。

### 既存の Tomcat 等にデプロイする場合

WAR と同じディレクトリという概念がないため、代わりに Spring Boot の `classpath:` 探索に乗せる形で外部ディレクトリを認識させます。方法は2つあります。

#### 方法1 — `jp.ecuacion.tool.code-generator.app-conf-dir` によるアプリ個別のディレクトリ指定

`jp.ecuacion.tool.code-generator.app-conf-dir` システムプロパティで指定したディレクトリがclasspathとして設定されます。ディレクトリが存在しない場合は自動的に作成されるため、事前にディレクトリを用意しておく必要はありません。未設定の場合は何もマウントされず、WAR に埋め込まれた設定がそのまま使われます。

個別アプリごとにclasspathのディレクトリを設定できるため、複数のアプリを同じ Tomcat に同居させても設定ファイルが混ざりません。

`${CATALINA_HOME}/bin/setenv.sh` で `CATALINA_OPTS` として指定するのが一般的です。

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.code-generator.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

このディレクトリに `application.properties` / `logback-spring.xml` を置くと、いずれも自動的に認識されます（`logback-spring.xml` についても、この方式では `-Dlogging.config` の指定は不要です）。

#### 方法2 — `setenv.sh` で `CLASSPATH` を指定

Tomcat の場合は `${CATALINA_HOME}/bin/setenv.sh` を作成（または編集）します。

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

このディレクトリに置いた `application.properties` は、Spring Boot の `classpath:` 探索により自動的にマージされます。`logback-spring.xml` を差し替えたい場合は、この場合も引き続き `-Dlogging.config` でパスを明示してください。

> **Note:** `CLASSPATH` は Tomcat プロセス全体で共有されます。同じ Tomcat に複数の ecuacion 製アプリ（例: `ecuacion-tool-code-generator` と `ecuacion-tool-command-api`）を同居させる場合、設定ファイルを置くディレクトリが共用されてしまい扱いにくくなります。アプリごとに設定を分けたい場合は方法1を使ってください。
