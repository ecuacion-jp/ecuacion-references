こちらは ecuacion-splib（モジュール名：`ecuacion-splib-xxx`）のリファレンスページです。
各モジュールが提供する Spring Boot 連携機能を網羅的にまとめています。

## ecuacion-splib とは

ecuacion-splib は ecuacion-lib の上に構築された Spring Boot 向けベースライブラリです。
設定・例外処理・セキュリティ・JPA/Batch 連携・Web アプリケーションフレームワーク一式といった共通基盤を提供し、各アプリケーションがそれらをゼロから作り込む必要をなくします。

## モジュール構成

ecuacion-splib は以下のモジュールで構成されています。

| モジュール名 | 役割 |
| --- | --- |
| `ecuacion-splib-core` | 他の `ecuacion-splib-xxx` モジュール全体が共有する基盤機能（設定・例外処理の契約） |
| `ecuacion-splib-jpa` | Web 層に依存しない JPA 連携（エンティティ・リポジトリ） |
| `ecuacion-splib-batch` | Spring Batch 連携。スケジューラーが無人でトリガーするジョブ向け |
| `ecuacion-splib-ui` | `ecuacion-splib-web` と `ecuacion-splib-cli` で共有するUI表示ロジック（必須項目バリデーションエラーによる他エラーのマスキング等）。アプリケーションコードから直接使うものではない |
| `ecuacion-splib-cli` | ユーザーが直接実行し対話的に結果を見るコマンドライン（CUI）アプリケーション向けの軽量な基盤 |
| `ecuacion-splib-web` | Spring MVC を用いた Web アプリケーションフレームワーク一式（コントローラー・フォーム・Thymeleaf/Bootstrap テンプレート） |
| `ecuacion-splib-web-jpa` | `ecuacion-splib-web` と `ecuacion-splib-jpa` を繋ぐ連携機能 |
| `ecuacion-splib-web-markdown` | Markdown ファイルを Web ページとして表示する機能。本リファレンスサイト自体もこれを利用して構築されている |
| `ecuacion-splib-rest` | REST API 構築用フレームワーク（例外処理、API キー / Public / 拒否エンドポイントのセキュリティ） |
| `ecuacion-splib-dependencies` | ecuacion モジュールのビルド用に使用する親 POM（一般アプリケーション開発者向けには非推奨） |

`ecuacion-splib-dependencies` は ecuacion-splib 自身（および他の ecuacion 系プロジェクト）のビルド用モジュールで、
一般アプリケーション開発者は使いません。詳細は下記「セットアップ」を参照してください。

> **`ecuacion-splib-web-markdown` についての注意:** このモジュールは Markdown をサニタイズせず、
> 生の HTML としてそのまま出力します（Thymeleaf の `th:utext`）。レンダー対象がビルド時に
> 開発者が同梱した Markdown のみである限り安全ですが、ユーザーが編集可能な Markdown を
> 流し込むことは絶対に避けてください。間に挟まるサニタイズ層が一段もないため、即座に
> 格納型 XSS になります。

現時点で本サイトがカバーしているのは **`ecuacion-splib-rest`**（上部メニューの **rest**）、**`ecuacion-splib-batch`**（上部メニューの **batch**）、**`ecuacion-splib-cli`**（上部メニューの **cli**）、そして **`ecuacion-splib-web`** の一部（上部メニューの **web**）です。他モジュールの記事は順次追加予定です。

> **`ecuacion-splib-web` に関する2つのドキュメントについて。** 本サイトの **web** メニューがカバーするのは、認証・CSRF・例外処理など、`ecuacion-splib-web` の内部挙動とセキュリティ機構のみです。これは本サイトが `rest`/`batch`/`cli` に対して既に提供しているのと同じ種類のコンテンツです。UI側（コントローラー・フォーム・Thymeleaf/Bootstrapコンポーネント）については、Markdown 記事ではなく、フレームワーク上に実装された実際の画面を操作しながら学べる専用のチュートリアルアプリ（別サイト）が `ecuacion-splib-web` 向けに用意されています。

---

## セットアップ

`ecuacion-splib-xxx` 各モジュールのバージョンは、`ecuacion-splib-parent` を BOM としてインポートするか、
親 POM として指定することで一元管理できます。Spring Boot 側のバージョンをどう扱うかによって、
以下の3パターンがあります。

### パターン 1: ecuacion-splib を親 POM として指定する（推奨）

```xml
<parent>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-parent</artifactId>
    <version>（バージョン）</version>
</parent>
```

Spring Boot のバージョンは ecuacion-splib 側と連動するため明示的な指定は不要です。
`ecuacion-splib-parent` は実際の依存関係や ecuacion 独自のビルド強制設定を持たない薄い親 POM なので、
副作用なく利用できます。唯一、Spring 6+ の `@RequestParam` / `@PathVariable` のパラメータ名解決に必要な `-parameters` コンパイラオプションだけは、これを親 POM にする一般アプリケーション自身のコンパイルにも必要なため、ここに含まれています。

また、実際の Maven `<parent>` を経由するこのパターンでは、プラグインのバージョン・設定（`pluginManagement`）もあわせて継承されます。実行可能 jar を作りたい場合、
`spring-boot-maven-plugin` はバージョンや `repackage` の実行設定を書かずに `<plugin>` 要素を追加するだけで使えます（パターン2 の BOM import ではこの `pluginManagement` は継承されません）。

記述量が最も少なくバージョン指定のミスも起きにくいため、このパターンを推奨します。
社内共通の親 POM など、`ecuacion-splib-parent` 以外を親 POM にしたいためこのパターンが使えない場合は、パターン2を使ってください。

### パターン 2: ecuacion-splib・Spring Boot ともに BOM としてインポートする

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
インポートしており、その内容はこの import にも推移的に含まれます。そのため Spring Boot 関連の依存関係（`spring-boot-starter-tomcat` など）も、Spring Boot のバージョンを別途指定することなくバージョン管理下に置けます。

一方、プラグインのバージョン・設定（`pluginManagement`）は BOM import では継承されません。
実行可能 jar を作る場合の `spring-boot-maven-plugin` など、こうしたプラグインが必要な場合はプロジェクト側で個別に追加してください。Spring Boot 公式の
[Using Spring Boot without the Parent POM](https://docs.spring.io/spring-boot/maven-plugin/using.html#using.import)
が参考になります。

このパターンでは、プロジェクトが元々使用している親 POM（社内共通の親 POM など）をそのまま維持できます。
社内共通の親 POM など、`ecuacion-splib-parent` 以外の親 POM を使いたいためパターン1が使えない場合に選んでください。

### パターン 3: ecuacion-splib は BOM インポート、Spring Boot は親 POM として指定する

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
`<parent>` に指定するバージョンはプロパティ参照ができない（Maven の仕様上、親 POM のバージョンはプロパティ解決より前に決定されるため）ので、明示的に指定したうえで、ecuacion-splib が使用している
Spring Boot のバージョンと手動で揃える必要があります。この手間が生じるぶん、あまり推奨しません。

### 必要なモジュールを追加する

```xml
<!-- サーバーサイドレンダリングのWebアプリケーションを構築する場合 -->
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-web</artifactId>
</dependency>

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

<!-- コマンドライン（CUI）アプリケーションを構築する場合 -->
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-cli</artifactId>
</dependency>
```

モジュールごとの詳細は、上部メニューの **web** にある
[セットアップ](page?id=web/setup&lang=ja)、**rest** にある
[セットアップ](page?id=rest/setup&lang=ja)、**batch** にある
[セットアップ](page?id=batch/setup&lang=ja)、または **cli** にある
[セットアップ](page?id=cli/setup&lang=ja) を参照してください。
