Endpoints mapped under `/api/public/**` are always reachable — `permitAll`, no authentication
required. This filter chain runs at `@Order(8)`, the first of the three chains
`SplibRestSecurityConfig` registers.

## CSRF is disabled here

CSRF protection only matters when a forged cross-site request could change state on the victim's
behalf. A request that only reads data has nothing for CSRF to protect, so CSRF is disabled for this
prefix.

**This argument depends entirely on every endpoint under `/api/public/**` being side-effect-free.**
Nothing in `ecuacion-splib-rest` enforces that — it is a convention the framework assumes but cannot
itself verify. A `@PostMapping` that writes data could still be added under this prefix; if it were,
a malicious site could trigger that write through a logged-in user's browser with no CSRF token
required, since `/api/public/**` is `permitAll` rather than exempt from a logged-in session.

**Keep `/api/public/**` read-only (GET/HEAD only).** If an endpoint needs to write data, put it under
`/api/key/**` (see
[API Key Authentication](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=en)) or
behind your own security configuration (see
[Custom Endpoint Security](/public/showMarkdown/page?id=rest/security/custom-endpoints&lang=en)).

## Example

The built-in
[Config Endpoint](/public/showMarkdown/page?id=rest/config-endpoint&lang=en)
(`GET /api/public/ecuacion/config`) is an example of a read-only endpoint under this prefix.
