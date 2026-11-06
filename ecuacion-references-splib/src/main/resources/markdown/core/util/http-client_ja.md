`ecuacion-splib-core` は、外部のサービスと通信するための HTTP クライアントを Bean として提供します。プロキシとタイムアウトを `application.properties` で設定でき、設定はこのクライアントだけに効きます（JVM 全体には影響しません）。

Bean は `ecuacion-splib-web`・`ecuacion-splib-rest`・`ecuacion-splib-batch`・`ecuacion-splib-cli` のいずれを使っていても登録されます。実際に使うまでクライアントは作られないので、外部と通信しないアプリの負担にはなりません。

## 使い方

### `RestClient` / `RestTemplate`

`ClientHttpRequestFactory` の Bean を受け取って渡します。

```java
@Component
public class ExternalApiClient {

  private final RestClient restClient;

  public ExternalApiClient(@Qualifier(SplibHttpClientConfig.BEAN_NAME_REQUEST_FACTORY)
      ClientHttpRequestFactory requestFactory) {
    restClient = RestClient.builder().requestFactory(requestFactory).build();
  }
}
```

`ecuacion-splib-web`・`ecuacion-splib-rest` 以外のモジュールでは、`spring-web` をアプリの依存に追加してください。`ecuacion-splib-core` では `optional` なので、自動では入りません。

### JDK の `HttpClient`

`HttpClient` の Bean を受け取ります。読み取りのタイムアウトは `HttpClient` の設定ではなくリクエストごとの設定なので、`SplibHttpClientSettings` の値を渡します。

```java
HttpRequest request = HttpRequest.newBuilder(uri).timeout(settings.getReadTimeout()).build();
HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
```

`HttpClient` の Bean は `@Qualifier(SplibHttpClientConfig.BEAN_NAME_HTTP_CLIENT)` を付けて受け取ります。

## application.properties の設定

接頭辞はすべて `jp.ecuacion.splib.core.http-client.` です。

| プロパティ | 既定値 | 内容 |
| --- | --- | --- |
| `proxy.host` / `proxy.port` | なし | プロキシ。<br>2 つとも指定します。 |
| `proxy.username` / `proxy.password` | なし | プロキシの認証。<br>2 つとも指定します。 |
| `connect-timeout` | `10s` | 接続のタイムアウト。 |
| `read-timeout` | `30s` | 読み取りのタイムアウト。 |

組み合わせが正しくない場合は、起動時にエラーになります。`proxy.host` を指定しない場合は、JVM のシステムプロパティ（`https.proxyHost` など）に従います。

## 認証付きのプロキシを使う場合

使うものによって、必要な準備と動きが変わります。

| 使うもの | 必要な準備 | 動き |
| --- | --- | --- |
| `ClientHttpRequestFactory` | `org.apache.httpcomponents.client5:httpclient5` をアプリの依存に追加します。<br>追加がないと起動時にエラーになります。 | 中身が Apache HttpClient になります。<br>通信先の 401 は、通常どおり本文付きで `HttpClientErrorException.Unauthorized` になります。<br>プロキシが CONNECT を拒否した場合は、例外ではなく、プロキシのステータスの応答になります。 |
| `HttpClient` | 起動オプション `-Djdk.http.auth.tunneling.disabledSchemes=""` を追加します。<br>JVM 全体に効く設定なので、splib では変更しません。<br>指定がないと、クライアントの作成時に警告のログを出します。 | 通信先の 401 は、応答ではなく `IOException`（`No credentials provided` など）になります。<br>起動オプションがないと、HTTPS の通信はプロキシの 407 で失敗します。 |

`IOException` の文言はプロキシの認証の失敗のように見えますが、通信先が 401 を返した場合にも出ます。通信先が 401 を返す可能性があるなら、`ClientHttpRequestFactory` を使ってください。

プロキシの ID・パスワードは、どちらの場合もプロキシにだけ送ります。通信先のサーバには送りません。

## 対象外の通信

このクライアントを使わない通信には、設定は効きません。たとえば、AWS SDK のように独自の HTTP クライアントを持つライブラリは、それぞれのライブラリの方法で設定してください。

## OAuth2 ログイン

`SplibOauth2WebSecurityConfig` を使う場合、ログイン中の通信はこのクライアントを使います。[セキュリティの設定](page?id=web/security/form-login&lang=ja) のソーシャルログインの節を参照してください。
