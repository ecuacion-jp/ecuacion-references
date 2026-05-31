# ecuacion-tool-code-generator reference

こちらは `ecuacion-tool-code-generator` のリファレンスページです。
DB項目定義書（Excel）から Spring Boot + JPA アプリケーションの base モジュールコードを自動生成するツールの
使い方をまとめています。

## このツールは何をするか

Excel で定義したテーブル・カラム情報をもとに、JPA Entity や Repository、
ビジネスロジッククラスなどのボイラープレートコードを自動生成します。

手書きで書くとミスやバラつきが出やすいコードを統一されたルールで生成することで、
新テーブル追加時の手間を大幅に削減できます。

## 実行方法の選択

ツールには 2 種類の実行方法があります。

| 実行方法 | 説明 |
| --- | --- |
| `code-generator-batch` | コマンドライン（`mvn spring-boot:run`）で実行。Excel をローカルに配置して使う |
| `code-generator-web` | ブラウザから Excel をアップロード → ZIP でダウンロード |

どちらの方法でも同じ DB項目定義書（Excel）を使います。

## 構成

| メニュー | 内容 |
| --- | --- |
| code-generator-batch | バッチ実行モジュールの概要・セットアップ・使い方 |
| code-generator-web | Web UI モジュールの概要・セットアップ・使い方 |
| DB項目定義書（Excel） | 入力 Excel のシート構成・列の詳細仕様 |
