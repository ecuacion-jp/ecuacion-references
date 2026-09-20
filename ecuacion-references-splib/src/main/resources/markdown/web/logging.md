`SplibWebConfig` registers `LoggingInterceptor` automatically for every request (once
`jp.ecuacion.splib.web.config` is component-scanned — see
[Quickstart](page?id=web/quickstart&lang=en)), applying two safeguards so that turning on DEBUG
logging doesn't itself become a credential leak.

## Request parameter masking

Every request is logged at DEBUG with its parameters (URI and parameter list), which is useful for
tracing what a request actually carried — but request parameters are entirely client-supplied,
and a login or password-change submission would otherwise place the raw password straight into the
log file. Any parameter whose name contains `password` (case-insensitive) anywhere — e.g.
`login.password`, `adminLogin.password`, `builtinAdminLogin.password` — has its value replaced
with `***` instead of being logged. This works by naming convention: every password field in this
library, and by convention in applications built on it, names itself with "password" somewhere in
the key.

## Session ID truncation

The log prefix on every line identifies the session with only the **last 8 characters** of the
session ID, never the full value. A session ID is a bearer credential — anyone who obtains it can
hijack the session without needing a password — and this prefix is written at DEBUG on every
request, so logging the full ID would turn the log file itself into a place session hijacking
material leaks to. The 8-character suffix is still enough to correlate the lines belonging to one
session within a single log file.

## Session ID kept out of the URL

Separately from logging, `SplibWebConfig` also sets `SessionTrackingMode.COOKIE` at the servlet
container level, so the session ID is only ever carried in a cookie, never appended to URLs as
`;jsessionid=...`. This isn't primarily a logging concern, but the same class of risk applies: a
session ID that leaks into a URL ends up in browser history, access logs, and `Referer` headers
sent to other sites — all places a cookie-only session ID never reaches. A `;jsessionid=...`
suffix could also otherwise reach Spring Security's URL firewall and be rejected as a
"potentially malicious" character, which is a second, independent reason this is turned off by
default here.
