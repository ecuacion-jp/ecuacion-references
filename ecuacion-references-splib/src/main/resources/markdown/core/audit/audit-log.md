The audit log records, one entry at a time, "when, who, from where, which operation, on which data, and with what result". It works the same on `ecuacion-splib-web` and `ecuacion-splib-rest`, and an application enables it by adding two settings. Of the classes an application uses, `AuditLogUtil`, `@AuditLogTarget` and so on are in the `jp.ecuacion.splib.core.audit` package of `ecuacion-splib-core`, and what records them is in the `jp.ecuacion.splib.webmvc.audit` package of `ecuacion-splib-webmvc`. `ecuacion-splib-web` and `ecuacion-splib-rest` depend on `ecuacion-splib-webmvc`, so an application does not need to add it to its pom.

It is disabled by default. Applications that do not enable it behave exactly as before.

## Enabling it

### application.properties

```properties
jp.ecuacion.splib.core.audit-log.enabled=true
```

### logback-spring.xml

Include `logback-spring-audit-log.xml` after `logback-spring-appenders.xml`.

```xml
<include resource="logback-spring-appenders.xml" />
<include resource="logback-spring-audit-log.xml" />
```

Records are written to `${log-dir}/${project-name}-audit.log`, one JSON object per line (with no timestamp or log level prefix). They are not written to any other log. Remove old files with `ecuacion-tool-housekeep-files`, like the other log files.

Without `logback-spring-audit-log.xml`, records go wherever the root logger goes (e.g. the console in a local environment). When the root logger's level is `WARN` or coarser they are not written at all, and a warning is logged at startup.

### Behind a reverse proxy

The client IP address is recorded from `HttpServletRequest#getRemoteAddr()`. For an application behind a reverse proxy, the proxy's address is recorded unless `server.forward-headers-strategy` is set. The library does not parse the `X-Forwarded-For` header itself, since it can be forged.

## What is recorded

| Kind (`eventType`) | When it is recorded |
| --- | --- |
| `OPERATION` | A request to a controller method.<br>By default, requests other than `GET`, `HEAD`, `OPTIONS` and `TRACE` (see "Changing what is recorded" below). |
| `LOGIN` | A successful (`result` is `SUCCESS`) or failed (`FAILURE`) login.<br>Form login, OAuth2 login and remember-me.<br>On `ecuacion-splib-rest`, failed `X-Api-Key` authentications are recorded too (see "REST" below). |
| `LOGOUT` | A logout. |
| `SWITCH_USER_START` | The start of a switch user (`SplibWebSecurityConfigForSwitchUser`). |
| `SWITCH_USER_EXIT` | The end of a switch user. |

## Recorded items

```json
{"timestamp":"2026-10-29T12:34:56.789+09:00","eventType":"OPERATION","result":"BUSINESS_ERROR","actor":"user1","originalActor":"admin1","clientIp":"192.0.2.1","sessionIdSuffix":"IJKLMNOP","httpMethod":"POST","uri":"/account/inquiry/action","handler":"InquiryEditController#edit","loginState":"account","targets":[{"type":"Inquiry","id":"123"}],"error":"jp.ecuacion.lib.core.exception.ViolationException [MSG_ID_1]","attributes":{"systemCode":"S1"}}
```

| Item | Content |
| --- | --- |
| `timestamp` | When it was recorded (in the server's time zone). |
| `eventType` | The kind (see the table above). |
| `result` | The result.<br>One of `SUCCESS`, `FAILURE` (a rejected login or authentication), `BUSINESS_ERROR` and `SYSTEM_ERROR`.<br>See "How the result is decided" below. |
| `actor` | Who did it.<br>`Authentication#getName()` (the logged-in username), and `null` when not logged in.<br>For a failed login, the username as typed. |
| `originalActor` | During a switch user only, the original administrator.<br>During a switch user, `actor` is the switched user and `originalActor` the administrator (records of starting and exiting the switch user take the same form). |
| `clientIp` | The client IP address. |
| `sessionIdSuffix` | The last 8 characters of the session ID, to relate records of the same session.<br>`null` for a request without a session (no session is created for recording). |
| `httpMethod` / `uri` | The HTTP method and URI of the request. |
| `handler` | The controller method that handled it (`ClassName#methodName`).<br>`null` except for `OPERATION`. |
| `loginState` | The login state (the first path segment, such as `public`, `account` or `admin`).<br>Only in records of operations (`OPERATION`) on `ecuacion-splib-web` pages. Not in records of login, logout and so on, nor in those of `ecuacion-splib-rest`. |
| `targets` | The data the operation was about (see "Adding target data" below). |
| `error` | The exception class name.<br>For `ViolationException` / `ConstraintViolationException`, followed by the list of message IDs.<br>For a failed login, the reason.<br>`null` when no exception occurred. |
| `attributes` | Items the application added (see "Adding application-specific items" below). |

### What is not recorded

So that secrets such as passwords, and personal data users type in, do not end up in the records, the following are never recorded:

- Request parameters (none of them).
- Exception messages (they can contain input values; only the class name and message IDs are recorded).
- The value of the `X-Api-Key` header.

The only value typed in by users that is recorded as is, is the username of a failed login, which is needed to detect brute-force attacks. If a user types their password into the username field by mistake, it is recorded, so protect the audit log files by operations: limit who can read them, and forward them to another server for storage early.

## Changing what is recorded

Put `@AuditLogTarget` on a controller method or class. An annotation on the method (including on the method it overrides) takes precedence over one on the class.

```java
// Not recorded: a POST that changes nothing (going back to the previous page, etc.)
@AuditLogTarget(false)
@PostMapping(value = "action", params = "action=back")
public String back(...) { ... }

// Recorded: a GET worth recording (downloading personal data, etc.)
@AuditLogTarget
@GetMapping("download")
public ResponseEntity<byte[]> download(...) { ... }
```

Methods of `ecuacion-splib`'s own controllers that accept a `POST` changing nothing (`SplibEditController#back`, `SplibSearchListController#showInsertForm` / `showUpdateForm`, `page` / `submitOnChangeToRefresh` of `SplibGeneral1FormController` / `SplibGeneral2FormsController`, and so on) already carry `@AuditLogTarget(false)`. They stay unrecorded when the application overrides them.

## How the result is decided

| Situation | `result` |
| --- | --- |
| An exception no exception handler handled | `SYSTEM_ERROR` |
| A `ViolationException` (including `ViolationWarningException`) or `ConstraintViolationException` that an exception handler handled | `BUSINESS_ERROR` |
| Any other exception that an exception handler handled | `SYSTEM_ERROR` if the response status is 5xx, `BUSINESS_ERROR` otherwise.<br>With `ecuacion-splib-web`'s exception handler, an optimistic lock failure or too large an upload becomes a redirect with a message, hence `BUSINESS_ERROR`, while any other exception shows the error page (500), hence `SYSTEM_ERROR`. |
| No exception | `SYSTEM_ERROR` if the response status is 5xx, `BUSINESS_ERROR` if 4xx, `SUCCESS` otherwise. |

Exceptions handled by an application's own `@ExceptionHandler` are decided the same way.

## Adding target data

The data an operation is about (its primary key, etc.) cannot be known generically, so add it with `AuditLogUtil.addTarget` while processing. It can be called from a controller, service or BL.

```java
AuditLogUtil.addTarget("Inquiry", inquiry.getId());
```

`SplibEditJpaService#insertOrUpdate` of `ecuacion-splib-web-jpa` adds the saved entity (its class name and ID) automatically. Add deleted rows, and rows changed by the application's own services, in the application.

The methods of `AuditLogUtil` do nothing when no recorded request is being processed (the audit log is disabled, the request is a `GET`, batch, etc.), so they can be called unconditionally. The information is kept per thread, though: when handing work off to another thread (`@Async`, etc.), call them on the request's thread before that.

## Adding application-specific items

To add items to `attributes` of a record, call `AuditLogUtil.putAttribute` while processing.

```java
AuditLogUtil.putAttribute("systemCode", inquiry.getSystemCode());
```

A later call with the same key overwrites the value. Like `AuditLogUtil.addTarget`, it takes effect only while processing an operation being recorded (records of login and logout do not get them). Do not pass secrets, or personal data the audit log should not keep.

## When writing to the log throws an exception

An exception while building a record or writing it to the log does not stop the operation or login being recorded. It is logged as an error. If records must never be missing, ensure it by operations (log forwarding, monitoring, etc.).

## REST

The same settings enable it on `ecuacion-splib-rest`.

- For a request authenticated by `X-Api-Key`, `actor` is the value of the `X-Api-Key-Id` header (`api-key-client` without one; `builtin-api-key-client` for the built-in key endpoints).
- A failed `X-Api-Key` authentication (no header, a wrong key, locked out after repeated failures, etc.) is recorded as `LOGIN` / `FAILURE`. `actor` is the `X-Api-Key-Id` header only when it has a valid format. The key itself is never recorded.
- Successful authentications are not recorded (every request authenticates; the request itself is recorded as an `OPERATION`).
- Used together with `ecuacion-splib-web`, each request is still recorded once.
