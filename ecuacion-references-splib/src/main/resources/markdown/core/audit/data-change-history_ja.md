データの変更履歴は、「どの行が・いつ・誰によって・どう変わったか」を、行の追加・更新・削除ごとに残す仕組みです。[Hibernate Envers](https://hibernate.org/orm/envers/) を使い、変更後の値を行ごとに履歴の表に残します（変更前の値は、1 つ前の履歴で分かります）。`ecuacion-splib-jpa` に含まれていて、アプリ側は Hibernate Envers の依存を足すと有効になります。

Hibernate Envers の依存を足さないアプリの動きは何も変わりません。

操作そのものの記録（いつ・誰が・どの操作をしたか）は[監査ログ](page?id=core/audit/audit-log&lang=ja)です。両方を有効にすると、監査ログの `operationId` から、その操作でどの行がどう変わったかをたどれます。どちらか一方だけを有効にすることもできます。

## 有効にする

### pom.xml

Hibernate Envers の依存を足します。`ecuacion-splib-jpa` は Hibernate Envers に optional で依存しているので、使わないアプリには入りません。バージョンは Spring Boot が管理しているので、書く必要はありません。

```xml
<dependency>
  <groupId>org.hibernate.orm</groupId>
  <artifactId>hibernate-envers</artifactId>
</dependency>
```

### 止める

Hibernate Envers の依存はあるが止めたい場合（テスト等）は、Hibernate の設定で止めます。止めると `@Audited` は無視され、履歴の表も作られません。

```properties
spring.jpa.properties.hibernate.integration.envers.enabled=false
```

### エンティティ

履歴を取るエンティティに `@Audited`（`org.hibernate.envers.Audited`）を付けます。エンティティが共通の項目を親クラス（`@MappedSuperclass`）から受け継いでいる場合は、親クラスにも付けます。履歴に残さない列には `@NotAudited` を付けます。`ecuacion-tool-code-generator` で生成するエンティティでは、DB 項目定義書（Excel）で履歴を取る表・外す列を指定すると、これらのアノテーションが付きます。

```java
@Entity
@Audited
public class Inquiry extends SystemCommon {

  // 履歴に残さない列
  @NotAudited
  private String memo;
  ...
}
```

`@Audited` を付けたエンティティから、付けていないエンティティへの関連がある場合は、その関連に `@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)` を付けます。付けないと起動時にエラーになります。手でエンティティを書く場合は、これらも自分で付けてください。

### エンティティの scan

アプリが `@EntityScan` で自分のエンティティのパッケージを指定していることが前提です（`ecuacion-tool-code-generator` で生成した `BaseConfig` には付いています）。有効にすると、履歴の情報を持つ `DataChangeRevision` がその scan の対象に足されます。

## 記録されるもの

### 履歴の表（`<表名>_aud`）

`@Audited` を付けたエンティティごとに、元の表の名前に `_aud` を付けた表ができます。行を追加・更新・削除するたびに、その行の変更後の値が 1 行ずつ入ります。

| 列 | 内容 |
| --- | --- |
| 元の表の主キー | 変更された行の主キー。 |
| `rev` | 変更をまとめた単位（リビジョン）の番号。<br>`revinfo` 表の `rev` と結び付きます。 |
| `revtype` | 変更の種類。<br>`0` が追加、`1` が更新、`2` が削除です。 |
| 元の表の列 | 変更後の値。<br>削除の場合は `null` です。 |

### リビジョンの表（`revinfo`）

1 つのトランザクションで行った変更が、1 つのリビジョンになります。

| 列 | 内容 |
| --- | --- |
| `rev` | リビジョンの番号。 |
| `revtstmp` | 変更した日時（サーバーのタイムゾーン）。<br>Envers の標準ではミリ秒の数値ですが、<br>`ecuacion-tool-housekeep-db` で古い履歴を消せるように日時の列にしています。 |
| `operation_id` | 変更した操作の ID。<br>同じ操作の監査ログの `operationId` と同じ値です。 |
| `actor` | 変更した人。<br>監査ログの `actor` と同じく、`Authentication#getName()` です。 |
| `original_actor` | 代理ログイン中のみ、元の管理者。<br>監査ログの `originalActor` と同じです。 |

`operation_id`・`actor`・`original_actor` は、コントローラのメソッドへのリクエストの処理中に取得します（監査ログの記録の対象でないリクエストも含みます）。それ以外での変更（バッチ、ログインの処理の中、`@Async` 等で別のスレッドに渡した処理）では、履歴は残りますがこれらの列は `null` になります。

## 表の作成

`spring.jpa.hibernate.ddl-auto=update` 等で表を自動で作る場合は、履歴の表と `revinfo` 表も自動で作られます。DDL を手で書く場合は、これらの表の DDL も用意してください（`ddl-auto=update` で作られた表を参考にできます）。

## 記録されない変更

JPA のエンティティを通した変更だけが記録されます。次の変更は記録されません。

- JPQL・SQL の一括の `update`・`delete`（`@Modifying` を付けたクエリ等）
- JDBC 等で JPA を通さずに行った変更

## 古い履歴を消す

`ecuacion-tool-housekeep-db` で、`revinfo` 表を対象（ID 列は `rev`、日時の条件は `revtstmp`）にし、各履歴の表を関連テーブル（`rev` で結び付く）に指定すると、古いリビジョンと、その履歴の行をまとめて消せます。履歴の表は主キーが 2 つの列（元の表の主キーと `rev`）なので、履歴の表だけを対象にすることはできません。

## 個人情報の扱い

履歴の表には、変更後の値がそのまま残ります。元の行から個人情報を消しても、履歴には消す前の値が残ります。

- 履歴に残す必要の無い列は、`@NotAudited` で履歴から外します（上記の「エンティティ」を参照）。
- 履歴からも消す必要がある場合は、アプリ側で、該当する行の履歴を消すか書き換えてください。`ecuacion-tool-housekeep-db` の「更新」は単一の ID 列を持つ表が対象のため、履歴の表には使えません。
