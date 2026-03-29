# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Java コーディングルール

### スタイル標準
- **Google Java Style Guide** 準拠（CheckstyleによりCIで強制）
- インデント: **スペース2つ**（タブ禁止）
- 最大行長: **100文字**（package/import文・URL除く）— **コメントにも適用**
- エンコーディング: **UTF-8**

### インポート
- ワイルドカードインポート（`.*`）は**禁止**
- static importを先に書き、1行空けてサードパーティパッケージのimport（アルファベット順）

### Javadoc
- **`public` および `protected` なクラス・メソッドにはJavadocが必須**
- `@Override`・`@Test` アノテーションが付いたメソッドは免除
- 編集したメソッドのJavadocも合わせて更新する

### 名前付け規則（Checkstyleで強制）
- メンバー変数・メソッド名: `^[a-z][a-z0-9][a-zA-Z0-9]*$`（最低2文字）
- パラメータ名・ローカル変数名: `^[a-z]([a-z0-9][a-zA-Z0-9]*)?$`（1文字も可）

## ビルド検証

**Javaファイルを編集したら必ず以下を実行し、違反を修正してから完了とする:**

```bash
mvn checkstyle:check spotbugs:check
mvn javadoc:javadoc
```

よくある違反:
- Checkstyle: 100文字超の行（コメント・Javadocも含む）
- Checkstyle: `public`/`protected` メンバーへのJavadoc不足
- Checkstyle: ワイルドカードインポート

## ビルド・実行コマンド

```bash
# 全モジュールビルド
mvn clean install

# 特定モジュールのみビルド
mvn clean install -pl ecuacion-references-splib-web-tutorial

# Webアプリ起動（開発時）
cd ecuacion-references-splib-web-tutorial && mvn spring-boot:run
```

## アーキテクチャ概要

### モジュール構成

```
util → splib-web-tutorial
     → splib-web-project-template
     → lib-tutorial
```

- **util**: 共有ユーティリティ（`RecordWithId` 基底クラス、`MemoryDataStoreDao`）
- **splib-web-tutorial**: `ecuacion-splib` Web フレームワークの包括的なチュートリアルアプリ（WAR）
- **splib-web-project-template**: 新規プロジェクト開発用テンプレート（WAR）。パッケージ名 `your.company.app` を実際のものに変更して使う
- **lib-tutorial**: `ecuacion-splib` コア機能の説明アプリ（WAR）

### 実装パターン（splib-web-tutorial）

Controller → Service → Form/Record → Thymeleaf テンプレートの4層構成:

- **Controller**: `SplibGeneral1FormController` を継承。単一フォーム・複数フォーム・複数フォーム+複数コントロールの3パターン
- **Record/Form**: `SplibRecord` / `SplibGeneralForm` を継承し、Jakarta Validation（`@NotEmpty`, `@Pattern`, `@Size` 等）でバリデーション定義
- **テンプレート**: `base-page-wide` / `base-page-narrow` の2レイアウト。`th:replace` で `inputText`, `inputSelect` 等の再利用コンポーネントを埋め込む
- **エラーハンドリング**: `@ControllerAdvice` のグローバルハンドラ（`AppExceptionHandler`, `AppExceptionHandlerJpa`）

### 環境設定

```
src/envs/local/resources/   ← デフォルト（ローカル環境）
src/envs/prod/resources/    ← 本番環境
```

Mavenプロファイルでどちらをパッケージするかを制御（`maven-profiles.txt` 参照）。

### 親フレームワーク

`ecuacion-splib`（別リポジトリ: `../ecuacion-splib`）を親pomとして使用。`relativePath` で参照しているため、ローカルに `ecuacion-splib` リポジトリが必要。
