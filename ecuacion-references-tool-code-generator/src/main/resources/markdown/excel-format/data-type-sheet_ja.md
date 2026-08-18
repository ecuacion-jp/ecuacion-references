「dataType定義」シートでは、各カラムに割り当てるデータ型（DataType）を定義します。
DataType は `DT_XXXX` という命名規則のキーで、Java の型・バリデーションルール・文字種制約を表します。

## 列構成

テーブル名: `テーブル2`、範囲: `A8:S{最終行}`

| 列 | 項目 | 説明 |
| --- | --- | --- |
| A | DataType名 | `DT_` で始まる識別子。大文字・数字・アンダースコアのみ |
| B | 型 | 型名（下記「型の説明」参照） |
| C | 長さ最小 | STRING: 文字列の最小長（任意） |
| D | 長さ最大 | STRING: 文字列の最大長（必須） |
| E | データパターン（日本語） | STRING: データパターン名の日本語表記（必須） |
| F | データパターン | STRING: データパターン識別子（必須） |
| G | 禁則文字チェック除外 | STRING: 禁則文字チェックから除外する文字（任意） |
| H | 正規表現 | STRING: 独自正規表現による文字種制約（任意） |
| I | 最小値 | 数値系: 最小値（任意） |
| J | 最大値 | 数値系: 最大値（任意） |
| K | 整数部桁数 | BIG_DECIMAL: 整数部の桁数（必須） |
| L | 小数部桁数 | BIG_DECIMAL: 小数部の桁数（必須） |
| M | コードの長さ | ENUM: コードの文字数（必須） |
| N | timezoneなし | 日時系: `○`=タイムゾーンなし（`LocalDateTime`）（任意） |
| O | 備考 | コメント（生成に影響しない） |
| P | パターン説明（デフォルト言語） | データパターンの説明文（英語） |
| Q〜S | パターン説明（追加言語1〜3） | 言語別のデータパターン説明文 |

---

## 型別の設定項目

型ごとに設定可能な列が異なります。○=必須 / △=任意 / （空白）=設定不可

| 型 | 長さ最小 (C) | 長さ最大 (D) | データパターン (E・F) | 正規表現 (H) | 最小値 (I) | 最大値 (J) | 整数部桁数 (K) | 小数部桁数 (L) | コードの長さ (M) | timezoneなし (N) | 自動採番 (※1) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `STRING` | △ | ○ | ○ | △ | | | | | | | |
| `INTEGER` | | | | | △ | △ | | | | | △ |
| `SHORT` | | | | | △ | △ | | | | | |
| `LONG` | | | | | △ | △ | | | | | △ |
| `FLOAT` | | | | | △ | △ | | | | | |
| `DOUBLE` | | | | | △ | △ | | | | | |
| `BIG_DECIMAL` | | | | | △ | △ | ○ | ○ | | | |
| `TIMESTAMP` | | | | | | | | | | △ | |
| `ENUM` | | | | | | | | | ○ | | |
| `BOOLEAN` | | | | | | | | | | | |

※1 自動採番は DB項目定義シートの H 列で指定します。型依存があるためここに記載しています。

---

## 型の説明

| 型 | Java 型 | PostgreSQL 型 | 説明 |
| --- | --- | --- | --- |
| `STRING` | `String` | `varchar` | 文字列（長さ・文字種の制約を別途指定） |
| `ENUM` | （Enum クラス） | `varchar` | 列挙型。対応する値は「enum定義」シートで定義 |
| `SHORT` | `Short` | `smallint` | 短整数（2バイト符号付き） |
| `INTEGER` | `Integer` | `int` | 整数（4バイト符号付き） |
| `LONG` | `Long` | `bigint` | 長整数（主にサロゲートキー・バージョン番号） |
| `BIG_INTEGER` | `BigInteger` | `numeric` | 高精度整数（小数点なし） |
| `FLOAT` | `Float` | `real` | 単精度浮動小数点数（4バイト） |
| `DOUBLE` | `Double` | `double precision` | 倍精度浮動小数点数（8バイト） |
| `BIG_DECIMAL` | `BigDecimal` | `numeric` | 高精度数値（精度・スケール指定） |
| `YEAR_MONTH` | `YearMonth` | `text` | 年月 |
| `DATE` | `LocalDate` | `date` | 日付 |
| `TIME` | `LocalTime` | `time` | 時刻 |
| `DATE_TIME` | `LocalDateTime` | `timestamp` | 日時（タイムゾーンなし） |
| `TIMESTAMP` | `LocalDateTime` | `timestamp` | タイムスタンプ（タイムゾーンなし） |
| `BOOLEAN` | `Boolean` | `bool` | 真偽値 |

## データパターン（STRING 型）

E 列はドロップダウンに表示される日本語名、F 列は（非表示の）「dataType・データパターン一覧」シートを
参照する VLOOKUP で自動入力される enumValue です。定義済みの全 14 パターン:

| データパターン（日本語・E列） | enumValue（F列） | 内容 |
| --- | --- | --- |
| `全半角（制限なし）` | `REG_EX_ALL` | 文字種の制限なし |
| `半角` | `REG_EX_HAN` | ASCII 印字可能文字（スペース〜チルダ）のみ |
| `半角数字` | `REG_EX_HAN_NUM` | 数字（`0-9`）のみ |
| `英大文字` | `REG_EX_HAN_UC` | 英大文字（`A-Z`）のみ |
| `英大文字＋_` | `REG_EX_HAN_UC_US` | 英大文字とアンダースコア |
| `英小文字` | `REG_EX_HAN_LC` | 英小文字（`a-z`）のみ |
| `英小文字＋_` | `REG_EX_HAN_LC_US` | 英小文字とアンダースコア |
| `半角数字＋英大文字` | `REG_EX_HAN_NUM_UC` | 数字と英大文字 |
| `半角数字＋英大文字＋_` | `REG_EX_HAN_NUM_UC_US` | 数字・英大文字・アンダースコア |
| `半角数字＋英小文字` | `REG_EX_HAN_NUM_LC` | 数字と英小文字 |
| `半角数字＋英小文字＋_` | `REG_EX_HAN_NUM_LC_US` | 数字・英小文字・アンダースコア |
| `半角英字` | `REG_EX_HAN_NUM_UC_LC` | 英大文字・英小文字（名称に反し数字は含まない） |
| `半角英字＋_` | `REG_EX_HAN_NUM_UC_LC_US` | 英大文字・英小文字・アンダースコア |
| `全角` | `REG_EX_ZEN` | 全角文字のみ（半角文字・半角カナは除く） |

## 慣例的なDataType

ブランクテンプレートの「dataType定義」シートは空の状態で配布されており、これらのDataTypeも他のDataTypeと同様に
自分で追加する必要があります。ツール側に組み込まれているわけでもありません。ただし以下の名前の組み合わせは
`ecuacion-splib` を使うプロジェクト間で慣習的に共通して使われており（`ecuacion-splib` が前提とする認証系テーブル用
の項目を含む）、多くのプロジェクトでシートの先頭付近にまず定義しています。

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
