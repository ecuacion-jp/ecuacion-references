# ecuacion-references - Claude Code ガイドライン

## 内部ドキュメント

作業前に `ecuacion-internal-docs` リポジトリの `CLAUDE.md` を読むこと。
共通ガイドライン・プロジェクト SPEC.md・ローカルセットアップ確認事項をカバーしている。

## プロジェクト概要

`ecuacion-lib` / `ecuacion-splib` の利用者向けチュートリアル・リファレンスアプリ群。
Maven マルチモジュールプロジェクト。

- **言語**: Java 21
- **ビルドツール**: Maven
- **主要モジュール**: `ecuacion-references-lib-tutorial`, `ecuacion-references-splib-web-tutorial`,
  `ecuacion-references-splib-web-project-template`, `ecuacion-references-util`

## 記事メンテナンス時の確認ルール

メニュー名・用語・表記を変更したときは、変更した記事だけでなく **全記事** を対象に
以下を確認して違和感があれば合わせて修正すること。

- 変更前の文言が他記事の本文・見出し・リンクテキストに残っていないか
- 見出し語句がサイト全体の表記と統一されているか（例：「概念」→「概要」など）
- メニューパスを参照しているテキスト（例：`**violation > Violation**` 形式）が
  ナビゲーション構造の変更と一致しているか
