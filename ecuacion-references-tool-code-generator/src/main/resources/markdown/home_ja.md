こちらは `ecuacion-tool-code-generator` のリファレンスページです。
DB項目定義書（Excel）から Spring Boot + JPA アプリケーションの base モジュールコードを自動生成するツールの使い方をまとめています。

## このツールは何をするか

Excel で定義したテーブル・カラム情報をもとに、JPA Entity や Repository、
ビジネスロジッククラスなどのボイラープレートコードを自動生成します。

手書きで書くとミスやバラつきが出やすいコードを統一されたルールで生成することで、
新テーブル追加時の手間を大幅に削減できます。

## 生成されるコード

DB項目定義書に記述したテーブル・カラム定義から、以下のファイルが自動生成されます。

| カテゴリ | パッケージ | 内容 |
| --- | --- | --- |
| Entity | `*.base.entity` | JPA Entity クラス（`@Entity`, `@Table`） |
| Record | `*.base.record` | 入出力 DTO（Entity ⇔ Record 変換） |
| Repository | `*.base.repository` | Spring Data JPA インターフェース |
| BL | `*.base.bl` | CRUD・検証・重複チェック等のビジネスロジック |
| Enum | `*.base.enums` | 列挙型 |
| Converter | `*.base.converter` | Enum ⇔ DB 変換（JPA `@Converter`） |
| DataTypeValidator | `*.base.datatype` | フィールド固有バリデーション |

## 主な生成機能

生成されるコードにはフレームワーク共通の以下の機能が組み込まれます。
いずれの機能もカラム名は固定ではなく、DB項目定義書側で任意の名前を指定できます（以下は設定例です）。

- **ソフトデリート**: 論理削除フラグカラム（例: `DEL_FLG`）による論理削除。Hibernate フィルタで透過的に適用
- **グループフィルタ**: グループIDカラム（例: `ACC_GROUP_ID`）によるマルチテナント対応
- **楽観的ロック**: バージョンカラム（例: `VERSION`）
- **監査情報**: 作成者・作成日時・更新者・更新日時カラム（例: `CREATE_ACC_ID`, `CREATE_TIME`, `LST_UPD_ACC_ID`, `LST_UPD_TIME`）

## 実行方法の選択

ツールには 2 種類の実行方法があります。

| | code-generator-cli | code-generator-web |
| --- | --- | --- |
| 実行方法 | コマンドライン（`java -jar`） | ブラウザからファイルアップロード |
| 使い方 | Excel をローカルのディレクトリに配置して実行 | Excel をアップロードして ZIP をダウンロード |
| 向いている場面 | 開発者が手元で実行する場面 | チームで共有して非開発者も使う場面 |

どちらの方法でも同じ DB項目定義書（Excel）を使います。

## 構成

| メニュー | 内容 |
| --- | --- |
| code-generator-cli | コマンドライン実行モジュールの概要・セットアップ・使い方 |
| code-generator-web | Web UI モジュールの概要・セットアップ・使い方 |
| DB項目定義書（Excel） | 入力 Excel のシート構成・列の詳細仕様 |
