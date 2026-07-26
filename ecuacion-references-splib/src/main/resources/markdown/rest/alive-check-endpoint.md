`ecuacion-splib-rest` provides one built-in controller, `AliveCheckController`, mapped under
`/api/ecuacion/public/aliveCheck`:

```
GET /api/ecuacion/public/aliveCheck
```

It sits under the `/api/ecuacion/public/**` prefix — reserved for `ecuacion-splib`'s own built-in
endpoints, kept separate from the application's `/api/public/**` (see
[Public Endpoints](page?id=rest/security/public-endpoints&lang=en)) — so it is
reachable without authentication, same as `/api/public/**`. It returns an
HTTP `200` with an empty body — no request parameters or response payload are defined. It is
useful as a lightweight, always-allowed endpoint to confirm the application is up and its
`/api/ecuacion/public/**` chain is wired correctly, independent of any application-specific
endpoint.
