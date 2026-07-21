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

現時点で本サイトがカバーしているのは **`ecuacion-splib-rest`**（上部メニューの **REST**）と
**`ecuacion-splib-batch`**（上部メニューの **BATCH**）です。他モジュールの記事は順次追加予定です。

`ecuacion-splib-web` については、Markdown 記事ではなく、フレームワーク上に実装された実際の画面を
操作しながら学べる専用のチュートリアルアプリ（別サイト）が用意されています。

---

## セットアップ

`ecuacion-splib-parent` を親 POM として指定する（または BOM としてインポートする）ことで
`ecuacion-splib-xxx` 各モジュールのバージョンを一元管理できます。その上で必要なモジュールを追加します。

```xml
<parent>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-parent</artifactId>
    <version>（バージョン）</version>
</parent>
```

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

モジュールごとの詳細は、上部メニューの **REST** にある
[セットアップ](/public/showMarkdown/page?id=rest/setup&lang=ja)、または **BATCH** にある
[セットアップ](/public/showMarkdown/page?id=batch/setup&lang=ja) を参照してください。
