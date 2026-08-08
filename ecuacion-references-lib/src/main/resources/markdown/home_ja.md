こちらは ecuacion library（モジュール名：`ecuacion-lib-xxx`）のリファレンスページです。
各ライブラリのユーティリティクラスや機能を網羅的にまとめています。

## ecuacion library とは

ecuacion library は、アプリケーション開発で共通的に必要となる各種機能を提供するライブラリです。
本ライブラリ単体でも使用可能ですが、基本的には ecuacion-splib などのベースライブラリと組み合わせて使用します。

## モジュール構成

ecuacion library は以下の 5 つのモジュールで構成されています。

| モジュール名 | 役割 |
| --- | --- |
| `ecuacion-lib-parent` | 親 POM（ecuacion-lib 各モジュールのバージョン管理） |
| `ecuacion-lib-dependencies` | BOM（ecuacion-lib が依存する外部ライブラリのバージョン管理） |
| `ecuacion-lib-core` | コア機能（Item・PropertiesFileUtil・Violation など） |
| `ecuacion-lib-validation` | Jakarta Validation 独自アノテーション群 |
| `ecuacion-lib-validation-business-messages` | ビジネス向けバリデーションメッセージ |

`ecuacion-lib-core` と `ecuacion-lib-validation` の詳細はナビゲーションの各ページを参照してください。
以降では、このページで説明を完結させる残り 3 モジュールについて説明します。

---

## セットアップ

### 1. BOM をインポートする

`ecuacion-lib-parent` を BOM としてインポートすることで、各モジュールのバージョンを個別に指定せずに済みます。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.lib</groupId>
            <artifactId>ecuacion-lib-parent</artifactId>
            <version>（バージョン）</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 必要なモジュールを追加する

```xml
<!-- コア機能（Item・PropertiesFileUtil・Violation など） -->
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-core</artifactId>
</dependency>

<!-- Jakarta Validation 独自アノテーション群 -->
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-validation</artifactId>
</dependency>

<!-- ビジネス向けバリデーションメッセージ（任意） -->
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-validation-business-messages</artifactId>
</dependency>
```

`ecuacion-lib-validation` は `ecuacion-lib-core` に依存しているため、
`ecuacion-lib-validation` を追加すれば `ecuacion-lib-core` も自動的に含まれます。

---

## ecuacion-lib-parent

ecuacion-lib 全モジュールの親 POM です。dependencyManagement に ecuacion-lib 各モジュール
（`ecuacion-lib-core`・`ecuacion-lib-validation`・`ecuacion-lib-validation-business-messages`）の
バージョンが定義されており、BOM としてインポートすることでバージョンを個別に記述しなくて済みます。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.lib</groupId>
            <artifactId>ecuacion-lib-parent</artifactId>
            <version>（バージョン）</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## ecuacion-lib-dependencies

ecuacion-lib が依存する外部ライブラリ（`jakarta.validation-api`・`hibernate-validator`・`junit` 等）の
バージョンを一元管理する Maven BOM です。dependencyManagement にインポートすることで、
これらのライブラリを ecuacion-lib と互換性のあるバージョンで利用できます。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.lib</groupId>
            <artifactId>ecuacion-lib-dependencies</artifactId>
            <version>（バージョン）</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### なぜ ecuacion-lib-parent と分けているのか

`ecuacion-splib-parent` は `ecuacion-lib-parent` を親 POM として指定しています。
そのため `ecuacion-lib-parent` に含めた設定は、`ecuacion-splib-parent` を親 POM として
使う一般アプリケーション（[ecuacion-references-splib](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=home&lang=ja)
のセットアップ パターン3）にもすべて継承されます。

`ecuacion-lib-dependencies` は、`ecuacion-lib` 自身の開発でのみ必要な設定・依存関係をまとめたモジュールです。

- 外部ライブラリのバージョン管理（`jakarta.validation-api`・`hibernate-validator` 等）。
  これを `ecuacion-lib-parent` に含めると、`ecuacion-splib-parent` を親 POM にした一般アプリケーションにまで
  Spring Boot の dependencyManagement と競合しうるバージョンが伝播してしまいます。
- checkstyle・spotbugs・ライセンスヘッダー自動付与・NullAway/Error Prone による静的解析など、
  ecuacion ライブラリ自身の品質担保のためのビルド設定。
- ソース jar・javadoc jar の生成、ecuacion 運用サーバへのアップロード（wagon）、テストカバレッジ計測
  （jacoco）、必要な Maven バージョンの強制（enforcer）など、ecuacion-lib 自身の公開・ビルド運用のための設定。
- `jspecify`（NullAway が利用する null 安全アノテーション）・`allure-jupiter`（テストレポート生成）の実依存関係。

これらを `ecuacion-lib-parent` ではなく `ecuacion-lib-dependencies` に切り出すことで、
`ecuacion-lib-parent`（ひいては `ecuacion-splib-parent`）を一般アプリケーションが安心して
親 POM に指定できる状態に保っています。`ecuacion-lib-dependencies` は `ecuacion-lib` 自身のモジュールと、
同様の役割を持つ `ecuacion-splib-dependencies`（`ecuacion-splib` 自身のモジュールが使う親 POM）から利用されます。

## ecuacion-lib-validation-business-messages

Jakarta Validation のデフォルトメッセージ（Hibernate Validator が提供する `"must not be null"` など）は
技術者向けの表現です。このモジュールはそれをエンドユーザー向けの自然な表現に置き換える
**ValidationMessages プロパティファイルのみ**を含みます。Java コードは一切含まれておらず、
dependency に追加するだけで有効になります。

### 使い方

`pom.xml` に以下を追加します（バージョンは `ecuacion-lib-dependencies` BOM で管理されます）。

```xml
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-validation-business-messages</artifactId>
</dependency>
```

### 提供するメッセージ

Jakarta Validation 標準アノテーションと ecuacion-lib-validation 独自アノテーションの両方を対象に、
英語（デフォルト）と日本語（`_ja` ロケール）のメッセージを提供します。

#### 置き換えの例（英語）

| アノテーション | デフォルト（Hibernate Validator） | このモジュール適用後 |
| --- | --- | --- |
| `@NotNull` | must not be null | is required |
| `@NotEmpty` | must not be empty | is required |
| `@Pattern` | must match "..." | must be in the correct format |
| `@Size` | size must be between {min} and {max} | must be between {min} and {max} characters |
| `@Email` | must be a well-formed email address | must be a valid email address |

#### 置き換えの例（日本語）

| アノテーション | デフォルト（Hibernate Validator） | このモジュール適用後 |
| --- | --- | --- |
| `@NotNull` | null は許可されていません | 入力必須です |
| `@NotEmpty` | 空要素は許可されていません | 入力必須です |
| `@Pattern` | 正規表現 "{regexp}" にマッチさせてください | 正しい形式で入力してください |
| `@Size` | {min} から {max} の間のサイズにしてください | {min}文字以上{max}文字以内で入力してください |
| `@Email` | 電子メールアドレスとして正しい形式にしてください | メールアドレスの形式で入力してください |

### アプリ側でのメッセージ上書き

このモジュールのメッセージキーには `.default` サフィックスが付いています
（例：`jakarta.validation.constraints.NotNull.message.default`）。
ecuacion-lib は、通常のキー（例：`jakarta.validation.constraints.NotNull.message`）が
アプリの `ValidationMessages.properties` に見つからない場合に `.default` キーへフォールバックする仕組みを持っています。
これにより、アプリ固有のカスタマイズを妨げることなくデフォルトメッセージを差し替えられます。
