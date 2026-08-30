`ecuacion-splib-web` layers two independent mechanisms on top of each other: Spring Security's
standard, session-based CSRF protection (left at its default — nothing in
`SplibWebSecurityConfig` disables it, unlike `ecuacion-splib-rest`'s `/api/**` endpoints, which
disable it because none of them rely on an ambient credential), and a separate one-time
**transaction token** that catches double form submissions.

## Why a separate transaction token, if CSRF is already on

CSRF protection stops a *different site* from forging a request on the user's behalf. It does
nothing about the user's *own* browser submitting the *same* form twice — e.g. double-clicking
Submit, or pressing the browser's back button and resubmitting a stale page. The transaction token
exists specifically for that case: it is a one-time value, so a second submission using the same
token is rejected even though it carries a perfectly valid CSRF token.

## How it works

1. `SplibControllerAdvice#modelAttribute` issues a new token
   (`TransactionTokenUtil#issueNewToken`) on every non-`@RestController` request and adds it to
   the model, so every rendered form carries a fresh, random 40-character token as a hidden field.
   (`@RestController` endpoints are skipped entirely — this mechanism applies only to
   Thymeleaf-rendered forms.)
2. Issued tokens are held in a `Set` in the session (one set per session, not per form), so more
   than one page can be open at once without invalidating each other's tokens.
3. On submission, `SplibControllerPrepareHelper#transactionTokenCheck` compares the token
   submitted with the form against the session's set. A token not found in the set (i.e. already
   consumed, or never issued) raises a `BusinessViolation` — handled the same way as any other
   validation failure, see [Exception Handling](page?id=web/exception-handling&lang=en). A
   matching token is removed from the set immediately, so it cannot be reused for a second
   submission.
4. The check is skipped entirely when the request is a server-side forward (`forward=true`) —
   forwarding replays all of the original request's parameters, which would otherwise trip this
   check a second time on the same request.

## Bounded per session

Because a token is only removed once its form is actually submitted, a session that keeps
requesting fresh pages without ever submitting them (e.g. repeated `GET`s of a `permitAll` page)
would otherwise accumulate tokens in that session-scoped set without bound. To prevent that,
`TransactionTokenUtil` caps the set at 10 tokens per session
(`TransactionTokenUtil.MAX_TOKENS_PER_SESSION`); once the cap is reached, the oldest token is
evicted first (a `LinkedHashSet` preserves insertion order for this purpose). This leaves headroom
for a handful of concurrently open tabs on the same session, while keeping the memory cost of an
unbounded-token attack constant per session.
