`SplibApiKeyComparisonMode` selects how the value your
[`SplibApiKeyExpectedValueProvider`](page?id=rest/security/api-key/overview&lang=en)
returns is compared against the client-presented `X-Api-Key` header. It is set application-wide via:

```properties
jp.ecuacion.splib.rest.api-key.mode=PLAIN
```

A single application is assumed to use one mode consistently — it is not configurable per endpoint or
per key. The default is `PLAIN`.

This mode selection is specific to `/api/key/**`. [Built-in Key Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en)
(`/api/ecuacion-splib/key/**`) does not use `SplibApiKeyComparisonMode` at all — its credential is
configured directly via `jp.ecuacion.splib.rest.builtin-api-key.password-plain` or
`...password-bcrypt` instead, with no separate mode property.

## `PLAIN`

The provider returns the keys themselves. Each is compared directly (in constant time) against the
presented value; the request is authenticated if any of them matches.

## `HASH`

The provider returns the lowercase-hex SHA-256 digest of each key, rather than the key itself, so the
raw keys are never at rest anywhere the application can read them back. The presented header value is
hashed the same way before the comparison.

To compute the value to store, hash the raw key on the command line:

```bash
# macOS
echo -n "your-api-key-here" | shasum -a 256

# Linux
echo -n "your-api-key-here" | sha256sum

# Cross-platform (OpenSSL)
echo -n "your-api-key-here" | openssl dgst -sha256
```

`-n` is required in all three: without it, `echo` appends a trailing newline that would be hashed
too, producing a digest that never matches the presented key.
