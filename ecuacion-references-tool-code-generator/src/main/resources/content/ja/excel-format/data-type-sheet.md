# dataType定義シート

「dataType定義」シートでは、各カラムに割り当てるデータ型（DataType）を定義します。
DataType は `DT_XXXX` という命名規則のキーで、Java の型・バリデーションルール・文字種制約を表します。

## 列構成

テーブル名: `テーブル2`、範囲: `A8:S{最終行}`

| 列 | 項目 | 説明 |
| --- | --- | --- |
| A | DataType名 | `DT_` で始まる識別子。大文字・数字・アンダースコアのみ |
| B | 型 | `BOOLEAN` / `LONG` / `INTEGER` / `DATE_TIME` / `STRING` / `ENUM` |
| C | 長さ最小 | STRING 型のみ有効。文字列の最小長 |
| D | 長さ最大 | STRING 型のみ有効。文字列の最大長 |
| E | データパターン | STRING 型のみ有効。使用可能文字の制約 |

## 型の説明

| 型 | Java 型 | 説明 |
| --- | --- | --- |
| `BOOLEAN` | `Boolean` | 真偽値 |
| `LONG` | `Long` | 長整数（主にサロゲートキー・バージョン番号） |
| `INTEGER` | `Integer` | 整数 |
| `DATE_TIME` | `LocalDateTime` | 日時（タイムゾーンなし） |
| `STRING` | `String` | 文字列（長さ・文字種の制約を別途指定） |
| `ENUM` | （Enum クラス） | 列挙型。対応する値は「enum定義」シートで定義 |

## データパターン（STRING 型）

| 値 | 説明 |
| --- | --- |
| `全半角（制限なし）` | 文字種の制限なし |
| `半角` | ASCII 印字可能文字（スペース〜チルダ）のみ |

## 標準組み込み DataType

すべてのプロジェクトに共通で含まれる DataType です。

| DataType | 型 | 内容 |
| --- | --- | --- |
| `DT_BOOL` | BOOLEAN | 真偽値 |
| `DT_DB_UPD_VER` | LONG | 楽観的ロック用バージョン番号 |
| `DT_SERIAL` | LONG | 自動採番サロゲートキー・外部キー |
| `DT_TIMESTAMP` | DATE_TIME | 日時（監査カラム等） |
| `DT_CODE` | STRING (1-100, 半角) | コード・認証コード |
| `DT_MAIL_ADDRESS` | STRING (1-256) | メールアドレス |
| `DT_ACC_NAME` | STRING (1-30) | アカウント名 |
| `DT_HASHED_PASSWORD` | STRING (60-60) | BCrypt ハッシュ済みパスワード |

## プロジェクト固有 DataType の追加方法

標準組み込み DataType に追加する形で、プロジェクト固有の DataType を定義します。

追加例（Qiita データ連携プロジェクトの場合）:

| DataType名 | 型 | 最小 | 最大 | データパターン |
| --- | --- | --- | --- | --- |
| `DT_QIITA_ITEM_ID` | STRING | 20 | 20 | 半角 |
| `DT_QIITA_TITLE` | STRING | 1 | 255 | 全半角（制限なし） |
| `DT_COUNT` | INTEGER | | | |

DataType を追加した場合は、テーブル範囲（`テーブル2` の `ref` 属性）を拡張する必要があります。
詳細については Excel の名前付き範囲の操作方法を参照してください。

## DataType とバリデーション

DataType から自動生成される `DataTypeValidator` クラスで、フィールドのバリデーション制約が定義されます。
たとえば `DT_CODE` の場合:

```java
@NotEmpty
@Size(min = 1, max = 100)
@Pattern(regexp = "^[a-zA-Z0-9 -/:-@\\[-`{-~]*$")
private String code;
```
