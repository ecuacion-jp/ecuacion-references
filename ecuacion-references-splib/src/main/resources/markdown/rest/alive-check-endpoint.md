`ecuacion-splib-rest` provides one built-in controller, `AliveCheckController`, mapped under
`/api/ecuacion-splib/public/aliveCheck`:

```
GET  /api/ecuacion-splib/public/aliveCheck
POST /api/ecuacion-splib/public/aliveCheck
```

It sits under the `/api/ecuacion-splib/public/**` prefix — reserved for `ecuacion-splib`'s own
built-in endpoints, kept separate from the application's `/api/public/**` (see
[Public Endpoints](page?id=rest/security/public-endpoints&lang=en)) — so it is
reachable without authentication, same as `/api/public/**`. Both `GET` and `POST` are accepted
(and Spring MVC serves `HEAD` automatically alongside `GET`) so that no monitoring or uptime tool
is ever blocked from reaching it by a method restriction.

No request parameters are defined. It returns an HTTP `200` with a small JSON body:

```json
{"status": "OK"}
```

It is useful as a lightweight, always-allowed endpoint to confirm the application is up,
independent of any application-specific endpoint.
