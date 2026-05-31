# code-generator-batch 概要

`ecuacion-tool-code-generator-batch` は、DB項目定義書（Excel）を入力として
Spring Boot + JPA アプリケーションの **base モジュール** の Java ソースコードを自動生成する
バッチ実行モジュールです。

## 生成されるコード

DB項目定義書に記述したテーブル・カラム定義から、以下のファイルが自動生成されます。

| カテゴリ | パッケージ | 内容 |
| --- | --- | --- |
| Entity | `*.base.entity` | JPA Entity クラス（`@Entity`, `@Table`） |
| Record | `*.base.record` | 入出力 DTO（Entity ⇔ Record 変換） |
| Repository | `*.base.repository` | Spring Data JPA インターフェース |
| RepositoryImpl | `*.base.repositoryimpl` | カスタムクエリ実装 |
| BL | `*.base.bl` | CRUD・検証・重複チェック等のビジネスロジック |
| Enum | `*.base.enums` | 列挙型 |
| Converter | `*.base.converter` | Enum ⇔ DB 変換（JPA `@Converter`） |
| DataTypeValidator | `*.base.datatype` | フィールド固有バリデーション |

## 主な生成機能

生成されるコードにはフレームワーク共通の以下の機能が組み込まれます。

- **ソフトデリート**: `DEL_FLG` カラムによる論理削除。Hibernate フィルタで透過的に適用
- **グループフィルタ**: `ACC_GROUP_ID` によるマルチテナント対応
- **楽観的ロック**: `VERSION` カラム
- **監査情報**: `CREATE_ACC_ID`, `CREATE_TIME`, `LST_UPD_ACC_ID`, `LST_UPD_TIME`

## 動作の仕組み

1. `ecuacion-tool-code-generator-excel-format/` ディレクトリに配置した DB項目定義書（xlsx）を読み込む
2. `mvn spring-boot:run` でバッチを実行する
3. `products/<SYSTEM_NAME>/` ディレクトリに Java ソースが出力される

## ドキュメント

- [セットアップ](/public/ja/article?id=code-generator-batch/setup)
- [クイックスタート](/public/ja/article?id=code-generator-batch/quickstart)
- [DB項目定義書（Excel）の仕様](/public/ja/article?id=excel-format/overview)
