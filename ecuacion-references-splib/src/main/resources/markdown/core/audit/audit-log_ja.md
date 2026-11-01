監査ログは、「いつ・誰が・どこから・どの操作を・どのデータに対して行い・結果はどうだったか」を 1 件ずつ記録する仕組みです。`ecuacion-splib-web` と `ecuacion-splib-rest` で共通に使え、アプリ側は設定を 2 か所足すだけで有効になります。アプリが使うクラスのうち、`AuditLogUtil`・`@AuditLogTarget` 等は `ecuacion-splib-core` の `jp.ecuacion.splib.core.audit` パッケージに、記録する処理は `ecuacion-splib-webmvc` の `jp.ecuacion.splib.webmvc.audit` パッケージにあります。`ecuacion-splib-webmvc` は `ecuacion-splib-web` と `ecuacion-splib-rest` が依存しているので、アプリの pom に足す必要はありません。

既定では無効です。有効にしないアプリの動きは何も変わりません。

## 有効にする

### application.properties

```properties
jp.ecuacion.splib.core.audit-log.enabled=true
```

### logback-spring.xml

`logback-spring-appenders.xml` の後に `logback-spring-audit-log.xml` を include します。

```xml
<include resource="logback-spring-appenders.xml" />
<include resource="logback-spring-audit-log.xml" />
```

記録は `${log-dir}/${project-name}-audit.log` に、1 件 1 行の JSON で出力されます（行の先頭に日時やログレベルは付きません）。ほかのログには出力されません。古いファイルは、ほかのログファイルと同じく `ecuacion-tool-housekeep-files` で削除してください。

`logback-spring-audit-log.xml` を include しない場合、記録はルートロガーの出力先（ローカル環境ならコンソール等）に出力されます。ルートロガーのレベルが `WARN` 以上の場合は出力されないため、その状態で起動すると警告がログに出力されます。

### リバースプロキシの背後で動かす場合

接続元の IP アドレスには `HttpServletRequest#getRemoteAddr()` を記録します。リバースプロキシの背後で動かすアプリで `server.forward-headers-strategy` を設定していないと、プロキシの IP アドレスが記録されます。`X-Forwarded-For` ヘッダーは偽装できるため、ライブラリ側では解析しません。

## 記録されるもの

| 種類（`eventType`） | 記録されるタイミング |
| --- | --- |
| `OPERATION` | コントローラのメソッドへのリクエスト。<br>既定では、何も変更しないリクエスト以外が対象です。<br>何も変更しないリクエストは、<br>`GET`・`HEAD`・`OPTIONS`・`TRACE` と、`@ReadOnlyOperation` を付けたメソッドへのリクエストです（後述の「記録の対象を変える」を参照）。 |
| `LOGIN` | ログインの成功（`result` が `SUCCESS`）と失敗（`FAILURE`）。<br>フォームログイン・OAuth2 ログイン・remember-me が対象です。<br>`ecuacion-splib-rest` では、`X-Api-Key` による認証の失敗も記録されます（後述の「REST の場合」を参照）。 |
| `LOGOUT` | ログアウト。 |
| `SWITCH_USER_START` | 代理ログイン（`SplibWebSecurityConfigForSwitchUser`）の開始。 |
| `SWITCH_USER_EXIT` | 代理ログインの終了。 |

## 記録の項目

```json
{"timestamp":"2026-10-29T12:34:56.789+09:00","operationId":"6f1c2a4e-8b3d-4f7a-9c0e-2d5b7a1e3f90","eventType":"OPERATION","result":"BUSINESS_ERROR","actor":"user1","originalActor":"admin1","clientIp":"192.0.2.1","sessionIdSuffix":"IJKLMNOP","httpMethod":"POST","uri":"/account/inquiry/action","handler":"InquiryEditController#edit","loginState":"account","targets":[{"type":"Inquiry","id":"123"}],"error":"jp.ecuacion.lib.core.exception.ViolationException [MSG_ID_1]","attributes":{"systemCode":"S1"}}
```

| 項目 | 内容 |
| --- | --- |
| `timestamp` | 記録した日時（サーバーのタイムゾーン）。 |
| `operationId` | 操作ごとの ID（UUID）。<br>[データの変更履歴](page?id=core/audit/data-change-history&lang=ja)の `revinfo` 表の `operation_id` と同じ値なので、<br>この操作でどのデータがどう変わったかをたどれます。<br>`OPERATION` 以外では `null` です。 |
| `eventType` | 種類（上の表を参照）。 |
| `result` | 結果。<br>`SUCCESS`（成功）、`FAILURE`（ログイン・認証の失敗）、`BUSINESS_ERROR`（業務エラー）、`SYSTEM_ERROR`（システムエラー）のいずれかです。<br>判定方法は後述の「結果の判定」を参照してください。 |
| `actor` | 操作した人。<br>`Authentication#getName()`（ログインしたユーザー名）で、ログインしていない場合は `null` です。<br>ログインの失敗では、入力されたユーザー名がそのまま記録されます。 |
| `originalActor` | 代理ログイン中のみ、元の管理者。<br>代理ログイン中は、`actor` がなりすまし先のユーザー、`originalActor` が管理者になります（代理ログインの開始・終了の記録も同じ形です）。 |
| `clientIp` | 接続元の IP アドレス。 |
| `sessionIdSuffix` | セッション ID の末尾 8 文字（同じセッションの記録を関連付けるため）。<br>セッションが無いリクエストでは `null` です（記録のためにセッションを作ることはありません）。 |
| `httpMethod`・`uri` | リクエストの HTTP メソッドと URI。 |
| `handler` | 処理したコントローラのメソッド（`クラス名#メソッド名`）。<br>`OPERATION` 以外では `null` です。 |
| `loginState` | ログインの種類（`public`・`account`・`admin` など、URL の先頭のパス）。<br>`ecuacion-splib-web` の画面への操作（`OPERATION`）の記録にだけ含まれます。ログイン・ログアウト等の記録や、`ecuacion-splib-rest` の記録には含まれません。 |
| `targets` | 操作の対象のデータ（後述の「対象のデータを足す」を参照）。 |
| `error` | 例外のクラス名。<br>`ViolationException`・`ConstraintViolationException` の場合は、続けてメッセージ ID の一覧が付きます。<br>ログインの失敗では、失敗の理由です。<br>例外が無い場合は `null` です。 |
| `attributes` | アプリが足した項目（後述の「アプリ固有の項目を足す」を参照）。 |

### 記録しないもの

パスワード等の秘密の値や、利用者が入力した個人情報が記録に残らないよう、次のものは記録しません。

- リクエストのパラメータ（すべて）。
- 例外のメッセージ本文（入力値を含むことがあるため。クラス名とメッセージ ID だけを記録します）。
- `X-Api-Key` ヘッダーの値。

利用者が入力した値のうち、そのまま記録されるのはログインに失敗したときのユーザー名だけです。総当たり攻撃などを検知するために必要なためです。ユーザー名の欄に誤ってパスワードを入力した場合はそれが記録されるため、監査ログのファイルは、閲覧できる人を絞り、早めに別のサーバーへ転送・保管するといった運用で守ってください。

## 記録の対象を変える

何も変更しないリクエストは記録しません。既定では `GET`・`HEAD`・`OPTIONS`・`TRACE` が何も変更しないリクエストです。それ以外の HTTP メソッドでも何も変更しない場合は、コントローラのメソッドまたはクラスに `@ReadOnlyOperation`（`ecuacion-splib-core` の `jp.ecuacion.splib.core.annotation`）を付けます。逆に、何かを変更する `GET` には `@ReadOnlyOperation(false)` を付けます。`@ReadOnlyOperation` は、代理ログインを「見るだけ」にする設定（[フォームログイン](page?id=web/security/form-login&lang=ja) の `SplibWebSecurityConfigForSwitchUser` を参照）でも使われ、その設定が有効なときは、代理ログイン中に何も変更しないリクエストだけが許可されます。

記録するかどうかだけを変える場合は、`@AuditLogTarget` を付けます。`@AuditLogTarget` は `@ReadOnlyOperation` より優先されます。いずれも、メソッドに付けたもの（オーバーライド元のメソッドに付いたものを含む）が、クラスに付けたものより優先されます。

```java
// 何も変更しない POST（前の画面に戻る等）。記録せず、代理ログインを「見るだけ」にしていても許可される
@ReadOnlyOperation
@PostMapping(value = "action", params = "action=back")
public String back(...) { ... }

// 記録すべき GET（個人情報のダウンロード等）を記録する
@AuditLogTarget
@GetMapping("download")
public ResponseEntity<byte[]> download(...) { ... }
```

`@AuditLogTarget(false)` は、何かを変更するが記録するほどではない操作に使います。何も変更しない `POST` に `@AuditLogTarget(false)` を付けると、記録はされませんが、代理ログインを「見るだけ」にしている場合は拒否されます。何も変更しない `POST` には `@ReadOnlyOperation` を付けてください。

`ecuacion-splib` 自身のコントローラのうち、何も変更しない `POST` を受けるメソッド（`SplibEditController#back`、`SplibSearchListController#showInsertForm`・`showUpdateForm`、`SplibGeneral1FormController`・`SplibGeneral2FormsController` の `page`・`submitOnChangeToRefresh` 等）には、あらかじめ `@ReadOnlyOperation` が付いています。アプリでこれらのメソッドをオーバーライドしても、引き継がれます。

## 結果の判定

| 状況 | `result` |
| --- | --- |
| 例外ハンドラが処理しなかった例外が発生した | `SYSTEM_ERROR` |
| `ViolationException`（`ViolationWarningException` を含む）・`ConstraintViolationException` が発生し、例外ハンドラが処理した | `BUSINESS_ERROR` |
| それ以外の例外が発生し、例外ハンドラが処理した | 応答のステータスが 5xx なら `SYSTEM_ERROR`、それ以外は `BUSINESS_ERROR`。<br>`ecuacion-splib-web` の例外ハンドラでは、楽観ロックの失敗やアップロードの上限超過はメッセージ付きのリダイレクトになるため `BUSINESS_ERROR`、それ以外の例外はエラー画面（500）になるため `SYSTEM_ERROR` です。 |
| 例外が発生しなかった | 応答のステータスが 5xx なら `SYSTEM_ERROR`、4xx なら `BUSINESS_ERROR`、それ以外は `SUCCESS`。 |

アプリ独自の `@ExceptionHandler` で処理した例外も、同じように判定されます。

## 対象のデータを足す

`ecuacion-splib-web` の画面の操作では、対象のデータが自動で足されます。

- 画面の form が持つ record（form 直下の record）のうち、ID を持つものを足します。編集画面で表示している行や、一覧画面でボタンを押した行（削除など）がこれにあたります。データの種類は、record の `@ItemNameKeyClass` の値の先頭を大文字にしたもの（`ecuacion-tool-code-generator` で生成した record ならエンティティ名）、ID は record 自身の主キーです。関連先の行の主キーは足しません。
- コントローラの `prepare` で足すので、入力チェック等で失敗した操作にも対象のデータが残ります。
- 新規登録は、保存するまで ID が無いため、`ecuacion-splib-web-jpa` の `SplibEditJpaService#insertOrUpdate` が保存した後に足します（エンティティのクラス名と ID）。更新では両方から同じデータが足されますが、記録には 1 つだけ入ります。

それ以外のデータ（アプリ独自のサービスで変更した行、一括削除のクエリで削除した行等）は、処理の中で `AuditLogUtil.addTarget` を呼んで足します。コントローラ・サービス・BL のどこから呼んでもかまいません。同じデータを複数回足しても、記録には 1 つだけ入ります。

```java
AuditLogUtil.addTarget("Inquiry", inquiry.getId());
```

`AuditLogUtil` のメソッドは、記録の対象のリクエストを処理していないとき（監査ログが無効、`GET` のリクエスト、バッチ等）には何もしないので、条件を付けずに呼んでかまいません。ただし、情報はスレッドごとに保持されるため、`@Async` 等で別のスレッドに処理を渡す場合は、渡す前にリクエストのスレッドで呼んでください。

## アプリ固有の項目を足す

記録の `attributes` に項目を足すには、処理の中で `AuditLogUtil.putAttribute` を呼びます。

```java
AuditLogUtil.putAttribute("systemCode", inquiry.getSystemCode());
```

同じキーで複数回呼んだ場合は、後の値が残ります。`AuditLogUtil.addTarget` と同じく、記録される操作の処理中だけ有効です（ログイン・ログアウトの記録には付きません）。秘密の値や、監査ログに残すべきでない個人情報は渡さないでください。

## ログへの書き込みで例外が発生した場合

記録の組み立てやログへの書き込みで例外が発生しても、記録の対象の操作やログインは止まりません。例外はエラーとしてログに出力されます。記録の欠落を許さない場合は、ログの転送・監視等の運用で担保してください。

## REST の場合

`ecuacion-splib-rest` でも、同じ設定で有効になります。

- `X-Api-Key` で認証したリクエストでは、`actor` は `X-Api-Key-Id` ヘッダーの値（無い場合は `api-key-client`、組み込み Key エンドポイントでは `builtin-api-key-client`）です。
- `X-Api-Key` による認証の失敗（ヘッダーが無い・キーが違う・失敗が続いてロックアウト中等）は、`LOGIN`・`FAILURE` として記録されます。`actor` は、正しい形式の `X-Api-Key-Id` ヘッダーがある場合のみその値です。キーの値は記録しません。
- 認証の成功は記録しません（リクエストごとに認証するため。リクエスト自体が `OPERATION` として記録されます）。
- `ecuacion-splib-web` と一緒に使う場合も、記録は 1 回だけです。
