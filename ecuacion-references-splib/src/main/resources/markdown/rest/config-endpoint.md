`ecuacion-splib-rest` provides one built-in controller, `ConfigController`, mapped under
`/api/public/ecuacion/config`:

```
GET /api/public/ecuacion/config
```

It sits under the `/api/public/**` prefix (see
[Public Endpoints](/public/showMarkdown/page?id=rest/security/public-endpoints&lang=en)), so it is
reachable without authentication. In the current version it returns an HTTP `200` with an empty
body — no request parameters or response payload are defined. It is useful as a lightweight,
always-allowed endpoint to confirm the application is up and its `/api/public/**` chain is wired
correctly, independent of any application-specific endpoint.
