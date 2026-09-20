`SplibExceptionHandler` は、アプリケーション側で継承する抽象 `@ControllerAdvice` です。`SplibExceptionHandler` のBeanが一つも登録されていない場合、[概要](page?id=web/overview&lang=ja)で説明した `SplibWebExceptionHandlerAutoConfiguration` が `ViolationException` のみを処理する最小限のフォールバックを提供します。以下の一式が必要になった時点で `SplibExceptionHandler` を継承してください。

```java
@ControllerAdvice
public class AppExceptionHandler extends SplibExceptionHandler {

  public AppExceptionHandler(HttpServletRequest request,
      @Nullable SplibExceptionHandlerAction actionOnThrowable, SplibLoginStateUtil loginStateUtil) {
    super(request, actionOnThrowable, loginStateUtil);
  }
}
```

## 処理する例外

| 例外 | 扱い |
| --- | --- |
| `ViolationWarningException` | 確認ダイアログ的な警告（例：「既存データを上書きします。続けますか？」）。送信は完了していないため、リダイレクトはせず、警告メッセージとどのボタンが押されたかを示す情報を添えて同じページを再描画します。 |
| `ViolationException` | 下記「2つのリダイレクトパス」を参照。 |
| `ConstraintViolationException` | `ViolationException` にラップされ、同じように処理されます。 |
| `NoResourceFoundException` | リクエストされたURLに一致する `@RequestMapping` がない場合。「見つかりません」メッセージとともにホームページへリダイレクトされます。 |
| `RedirectException`（および `RedirectToHomePageException` などのサブクラス） | アプリケーション側から発行する、特定のパスへメッセージ付きでリダイレクトするためのシグナルです。例：「指定されたレコードは既に存在しません」。 |
| `OverlappingFileLockException` | 楽観的ロックの競合に相当する扱いです。編集ページ上ではメッセージとともにそのレコードの表示ページへリダイレクトし、それ以外の場所では `ViolationException` として扱われます。 |
| `MaxUploadSizeExceededException` | アップロードされたファイルが `spring.servlet.multipart.max-file-size`/`max-request-size` を超えた場合。これはコントローラーの `prepare()` が実行される前に発生する（モデル・フォームがまだ存在しない）ため、コントローラーなしの `ViolationException` と同じ方法で、フラッシュメッセージを添えて呼び出し元のページへリダイレクトします。 |
| その他すべての `Throwable` | 本当に想定外の例外（報告済みの不具合ではなくバグ）です。`LogUtil.logSystemError` でログに記録され、登録済みの `SplibExceptionHandlerAction` Bean（下記参照）があればそれに渡された後、HTTPステータス `500` の汎用 `error` ビューとしてレンダリングされます。 |

## `ViolationException` の2つのリダイレクトパス

モデルに `SplibGeneralController`（とそのフォーム群）が存在するかどうかで、どちらの経路になるかが決まります。

- **コントローラーがある場合**（一般的なケース。`SplibGeneral1FormController` などを土台にした実際のページ）：違反メッセージは該当フィールドの `BindingResult` に紐づけられ（または後述のプロパティ設定次第でページ上部に表示され）、ブラウザは同じページへリダイレクトされ、それらのエラー付きで再描画されます。
- **コントローラーがない場合**（フォームを持たない、素の `@Controller`/`SplibBaseController`）：何かを紐づける `BindingResult` が存在しないため、メッセージはフラッシュ属性経由で渡され、ブラウザは代わりに呼び出し元のページへリダイレクトされます（リダイレクト先は `Referer` ヘッダーから取得するため、[オープンリダイレクト対策](page?id=web/security/open-redirect-protection&lang=ja)を経由します）。

## メッセージ表示：フィールド単位か、ページ上部か

`ViolationException` のメッセージをどう表示するかは、`application.properties` の2つのキーで制御します。少なくとも一方は `true` である必要があります。

```properties
jp.ecuacion.splib.web.process-result-message.shown-at-each-item=true
jp.ecuacion.splib.web.process-result-message.shown-at-the-top=false
```

`shown-at-each-item` は該当フィールドの隣にメッセージを表示し、`shown-at-the-top` はページ上部のサマリーに表示します。紐づける特定のフィールドがない違反（クラスレベルの制約や、プロパティパスを持たないメッセージ）は、他に表示する場所がないため、これらの設定に関わらず常にページ上部に表示されます。フィールド単位のメッセージが1つでも表示される場合、下にスクロールするよう促すサマリー行が自動的にページ上部に追加されます。

## 未捕捉の例外に独自ロジックを実行する

`jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction` を実装したBeanを登録すると、未捕捉の `Throwable` がハンドラに到達するたびに独自ロジックを実行できます。`execute(Throwable th)` の中に必要な処理を書いてください。これは `ecuacion-splib-rest` や `ecuacion-splib-batch` と共有している拡張ポイントと同じものです。実装例（`SplibMailUtil` を使ってスタックトレースをアラートメールとして送信する例）は、**rest** メニュー配下の[例外処理](page?id=rest/exception-handling&lang=ja)を参照してください。
