「各種設定」シートにはプロジェクト全体に関わる設定を記載します。
新規プロジェクト作成時に必ず修正が必要な項目と、通常は変更不要な項目があります。

## 必須修正項目

| 行 | キー | 説明 |
| --- | --- | --- |
| 8 | `SYSTEM_NAME` | プロジェクト識別名。生成ソースの親フォルダ名になる。英数字・ハイフン推奨（例: `my-project`） |
| 9 | `BASE_PACKAGE` | 生成コードの Java パッケージ共通部分（例: `jp.example.myapp`） |
| 31 | `TABLE_NAMES_WITHOUT_GROUPING` | グループフィルタ（`ACC_GROUP_ID` 列）を持たないテーブル名を CSV で列挙（例: `ACC,ACC_ADMIN`） |

### SYSTEM_NAME

コード生成の出力先フォルダ名と、生成される `Constants.java` に埋め込まれる値です。

```java
// 例: SYSTEM_NAME = "my-project" の場合
public class Constants {
    public static final String SYSTEM_NAME = "my-project";
}
```

### BASE_PACKAGE

生成されるすべての Java ソースのパッケージの共通部分です。

```
jp.example.myapp.base.entity.AccEntity
jp.example.myapp.base.bl.AccBl
...
```

### TABLE_NAMES_WITHOUT_GROUPING

`ecuacion-splib` フレームワークでは、マルチテナント対応のため多くのテーブルが
`ACC_GROUP_ID` カラムを持ちます。このカラムを持たないテーブルをここに列挙します。

認証系テーブル（`ACC`, `ACC_ADMIN` など）は通常グループに属さないため、ここに含めます。

---

## 通常は変更不要な項目

これらの項目はフレームワークの規約に基づいており、特別な理由がない限り変更しません。

| キー | デフォルト値 | 説明 |
| --- | --- | --- |
| `LOGICAL_DELETE.COLUMN_NAME` | `DEL_FLG` | ソフトデリートのフラグ列名 |
| `GROUPING.COLUMN_NAME` | `ACC_GROUP_ID` | マルチテナント用グループ列名 |
| `OPTIMISTIC_LOCKING.COLUMN_NAME` | `VERSION` | 楽観的ロック用バージョン列名 |
