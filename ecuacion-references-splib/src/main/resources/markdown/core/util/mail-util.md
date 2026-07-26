`SplibMailUtil` (`jp.ecuacion.splib.core.util.SplibMailUtil`) sends mail using Spring Boot's
standard `spring.mail.*` settings. It is registered as a `@Component`, so applications receive it
via dependency injection rather than calling static methods.

## Usage

The only public method today is `sendErrorMail(Throwable)`, specialized for notifying
administrators when a system error occurs.

```java
@Component
public class AppExceptionHandlerAction implements SplibRestExceptionHandlerAction {

  private final SplibMailUtil mailUtil;

  public AppExceptionHandlerAction(SplibMailUtil mailUtil) {
    this.mailUtil = mailUtil;
  }

  @Override
  public void execute(@Nullable Throwable th) {
    mailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
```

This is the implementation shown as a `SplibRestExceptionHandlerAction` example in
`ecuacion-splib-rest`'s [Exception Handling](page?id=rest/exception-handling&lang=en). The
`ecuacion-splib-batch` equivalent shown in `ecuacion-splib-batch`'s
[Exception Handling](page?id=batch/exception-handling&lang=en) looks identical apart from the
`implements` clause, using the same `SplibExceptionHandlerAction` that `ecuacion-splib-web` uses —
REST is the only frontend with its own dedicated interface.

## Behavior when unconfigured

If any of `spring.mail.host`, `spring.mail.username`, `spring.mail.password`, or
`jp.ecuacion.splib.mail.address-csv-on-system-error` is unset, the call is silently skipped (logged
at `INFO`) rather than throwing. Whatever calls this method — such as exception handling — keeps
working fine even without mail settings configured, e.g. in a development environment.

## `application.properties` settings

### SMTP connection (`spring.mail.*`)

| Property | Description | Default |
| --- | --- | --- |
| `spring.mail.host` | SMTP server hostname | None (unset skips sending) |
| `spring.mail.port` | SMTP port | `587` |
| `spring.mail.username` | Sender address (SMTP login username) | None (unset skips sending) |
| `spring.mail.password` | SMTP password (an app password for Gmail) | None (unset skips sending) |
| `spring.mail.properties.mail.smtp.auth` | Whether SMTP auth is required | `true` |
| `spring.mail.properties.mail.smtp.ssl.enable` | `true` for SSL (port 465); `false` for STARTTLS (port 587) — STARTTLS itself is then enabled automatically | `false` |

### Application settings (`jp.ecuacion.splib.mail.*`)

| Property | Description | Default |
| --- | --- | --- |
| `jp.ecuacion.splib.mail.address-csv-on-system-error` | Recipients for `sendErrorMail` (comma-separated, multiple allowed) | None (unset skips sending) |
| `jp.ecuacion.splib.mail.title-prefix` | String prepended to the mail subject (e.g. an environment name) | `""` |
| `jp.ecuacion.splib.mail.smtp.starttls-required` | For STARTTLS (port 587), whether to fail the connection outright rather than fall back to plaintext when the server doesn't support it ¹ | `true` |
| `jp.ecuacion.splib.mail.smtp.bounce-address` | Address that receives bounce mail | Unset |
| `jp.ecuacion.splib.mail.debug` | Enables JavaMail debug logging | `false` |

¹ Setting this to `false` is a security risk: if the server lacks STARTTLS support, SMTP
authentication (including the password) is sent in plaintext instead. Only set it to `false` for a
server known not to support STARTTLS (e.g. a local test relay), never in production.

## Example config (port 587 / STARTTLS, Gmail)

> To use Gmail's SMTP, enable 2-step verification on the Google account and issue an
> [app password](https://myaccount.google.com/apppasswords).

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-account@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=false

jp.ecuacion.splib.mail.title-prefix=[MyApp: staging]
jp.ecuacion.splib.mail.address-csv-on-system-error=admin@example.com
```
