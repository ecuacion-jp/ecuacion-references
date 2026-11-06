`ecuacion-splib-core` provides an HTTP client as a bean for calling external services. Its proxy and timeouts are set in `application.properties`, and the settings apply only to this client, not to the whole JVM.

The beans are registered whichever of `ecuacion-splib-web`, `ecuacion-splib-rest`, `ecuacion-splib-batch` and `ecuacion-splib-cli` you use. The clients aren't created until they're used, so apps that don't call external services pay no cost.

## Usage

### `RestClient` / `RestTemplate`

Receive the `ClientHttpRequestFactory` bean and pass it to the builder.

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

With modules other than `ecuacion-splib-web` and `ecuacion-splib-rest`, add `spring-web` to your app's dependencies. It's `optional` in `ecuacion-splib-core`, so it isn't added automatically.

### The JDK's `HttpClient`

Receive the `HttpClient` bean. The read timeout is a setting of each request, not of `HttpClient`, so pass the value of `SplibHttpClientSettings`.

```java
HttpRequest request = HttpRequest.newBuilder(uri).timeout(settings.getReadTimeout()).build();
HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
```

Receive the `HttpClient` bean with `@Qualifier(SplibHttpClientConfig.BEAN_NAME_HTTP_CLIENT)`.

## application.properties settings

All the keys are prefixed with `jp.ecuacion.splib.core.http-client.`.

| Property | Default | Description |
| --- | --- | --- |
| `proxy.host` / `proxy.port` | None | The proxy.<br>Specify both. |
| `proxy.username` / `proxy.password` | None | The proxy's credentials.<br>Specify both. |
| `connect-timeout` | `10s` | The connect timeout. |
| `read-timeout` | `30s` | The read timeout. |

An invalid combination fails at startup. When `proxy.host` is omitted, the JVM's system properties (`https.proxyHost` etc.) apply.

## Using a proxy with credentials

The preparation and behavior depend on what you use.

| What you use | Preparation | Behavior |
| --- | --- | --- |
| `ClientHttpRequestFactory` | Add `org.apache.httpcomponents.client5:httpclient5` to your app's dependencies.<br>Startup fails without it. | Apache HttpClient is used inside.<br>A 401 from the destination results in `HttpClientErrorException.Unauthorized` with its body as usual.<br>When the proxy refuses CONNECT, the result is a response with the proxy's status, not an exception. |
| `HttpClient` | Add the startup option `-Djdk.http.auth.tunneling.disabledSchemes=""`.<br>It's a JVM-wide setting, so splib doesn't change it.<br>A warning is logged when the client is created without it. | A 401 from the destination results in `IOException` (`No credentials provided` etc.) instead of a response.<br>Without the startup option, HTTPS requests fail with a 407 from the proxy. |

The `IOException` message looks like a failure of the proxy's authentication, but it also appears when the destination returns a 401. If the destination may return a 401, use `ClientHttpRequestFactory`.

In both cases, the proxy's username and password are sent only to the proxy, never to the destination server.

## Requests not covered

The settings don't apply to requests that don't use this client. For example, configure libraries with their own HTTP client, such as the AWS SDK, in the way each library provides.

## OAuth2 login

With `SplibOauth2WebSecurityConfig`, the requests during login use this client. See the social login section of [security settings](page?id=web/security/form-login&lang=en).
