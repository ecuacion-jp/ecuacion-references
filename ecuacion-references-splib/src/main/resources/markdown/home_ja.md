こちらは ecuacion-splib（モジュール名：`ecuacion-splib-xxx`）のリファレンスページです。
各モジュールが提供する Spring Boot 連携機能を網羅的にまとめています。

## ecuacion-splib とは

ecuacion-splib は ecuacion-lib の上に構築された Spring Boot 向けベースライブラリです。
設定・例外処理・セキュリティ・JPA/Batch 連携・Web アプリケーションフレームワーク一式といった
共通基盤を提供し、各アプリケーションがそれらをゼロから作り込む必要をなくします。

## モジュール構成

ecuacion-splib は以下のモジュールで構成されています。

| モジュール名 | 役割 |
| --- | --- |
| `ecuacion-splib-core` | 他の `ecuacion-splib-xxx` モジュール全体が共有する基盤機能（設定・例外処理の契約） |
| `ecuacion-splib-jpa` | Web 層に依存しない JPA 連携（エンティティ・リポジトリ） |
| `ecuacion-splib-batch` | Spring Batch 連携 |
| `ecuacion-splib-web` | Spring MVC を用いた Web アプリケーションフレームワーク一式（コントローラー・フォーム・Thymeleaf/Bootstrap テンプレート） |
| `ecuacion-splib-web-jpa` | `ecuacion-splib-web` と `ecuacion-splib-jpa` を繋ぐ連携機能 |
| `ecuacion-splib-web-markdown` | Markdown ファイルを Web ページとして表示する機能。本リファレンスサイト自体もこれを利用して構築されている |
| `ecuacion-splib-rest` | REST API 構築用フレームワーク（例外処理、API キー / Public / 拒否エンドポイントのセキュリティ） |

現時点で本サイトがカバーしているのは **`ecuacion-splib-rest`**（上部メニューの **rest**）と
**`ecuacion-splib-batch`**（上部メニューの **batch**）です。他モジュールの記事は順次追加予定です。

`ecuacion-splib-web` については、Markdown 記事ではなく、フレームワーク上に実装された実際の画面を
操作しながら学べる専用のチュートリアルアプリ（別サイト）が用意されています。

---

## セットアップ

`ecuacion-splib-xxx` 各モジュールのバージョンは、`ecuacion-splib-parent` を BOM としてインポートするか、
親 POM として指定することで一元管理できます。Spring Boot 側のバージョンをどう扱うかによって、
以下の3パターンがあります。

### パターン 1: ecuacion-splib・Spring Boot ともに BOM としてインポートする（推奨）

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.splib</groupId>
            <artifactId>ecuacion-splib-parent</artifactId>
            <version>（バージョン）</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

`ecuacion-splib-parent` は自身の `dependencyManagement` で `spring-boot-dependencies` を BOM
インポートしており、その内容はこの import にも推移的に含まれます。そのため Spring Boot 関連の
依存関係（`spring-boot-starter-tomcat` など）も、Spring Boot のバージョンを別途指定することなく
バージョン管理下に置けます。

一方、プラグインのバージョン・設定（`pluginManagement`）は BOM import では継承されません。
実行可能 jar を作る場合の `spring-boot-maven-plugin` など、こうしたプラグインが必要な場合は
プロジェクト側で個別に追加してください。Spring Boot 公式の
[Using Spring Boot without the Parent POM](https://docs.spring.io/spring-boot/maven-plugin/using.html#using.import)
が参考になります。

このパターンでは、プロジェクトが元々使用している親 POM（社内共通の親 POM など）をそのまま維持できます。

### パターン 2: ecuacion-splib は BOM インポート、Spring Boot は親 POM として指定する

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>（Spring Boot のバージョン）</version>
</parent>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.splib</groupId>
            <artifactId>ecuacion-splib-parent</artifactId>
            <version>（バージョン）</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

このパターンでは Spring Boot のバージョンは ecuacion-splib 側のバージョンと連動しません。
`<parent>` に指定するバージョンはプロパティ参照ができない（Maven の仕様上、親 POM のバージョンは
プロパティ解決より前に決定されるため）ので、明示的に指定したうえで、ecuacion-splib が使用している
Spring Boot のバージョンと手動で揃える必要があります。

### パターン 3: ecuacion-splib を親 POM として指定する

```xml
<parent>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-parent</artifactId>
    <version>（バージョン）</version>
</parent>
```

パターン1と同様、Spring Boot のバージョンは ecuacion-splib 側と連動するため明示的な指定は不要です。
ただし親 POM にすると、バージョン管理だけでなく `ecuacion-splib-parent` が内部的に使用している
以下のビルド設定も一緒に継承される点に注意してください。

- `spring-boot-devtools` / `spring-boot-starter-test` / `allure-jupiter` が、バージョン管理だけでなく
  実際の依存関係として自動的に追加される
- コンパイル時に NullAway / Error Prone による静的解析が強制される
- `maven-surefire-plugin` の `useModulePath=false` など、テスト実行の設定が上書きされる

`ecuacion-tool-code-generator` など一部の ecuacion 系プロジェクトではこのパターンを採用していますが、
上記の副作用を許容できる場合を除き、一般のアプリケーションでは通常パターン1を推奨します。

### 必要なモジュールを追加する

```xml
<!-- REST API を構築する場合 -->
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-rest</artifactId>
</dependency>

<!-- Spring Batch のジョブを構築する場合 -->
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-batch</artifactId>
</dependency>
```

モジュールごとの詳細は、上部メニューの **rest** にある
[セットアップ](page?id=rest/setup&lang=ja)、または **batch** にある
[セットアップ](page?id=batch/setup&lang=ja) を参照してください。
