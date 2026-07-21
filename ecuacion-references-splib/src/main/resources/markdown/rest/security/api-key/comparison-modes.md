`SplibApiKeyComparisonMode` selects how the value your
[`SplibApiKeyExpectedValueProvider`](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=en)
returns is compared against the client-presented `X-Api-Key` header. It is set application-wide via:

```properties
jp.ecuacion.splib.rest.api-key.mode=PLAIN
```

A single application is assumed to use one mode consistently — it is not configurable per endpoint or
per key. The default is `PLAIN`.

## `PLAIN`

The provider returns the key itself. It is compared directly (in constant time) against the
presented value.

## `HASH`

The provider returns the lowercase-hex SHA-256 digest of the key, rather than the key itself, so the
raw key is never at rest anywhere the application can read it back. The presented header value is
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
