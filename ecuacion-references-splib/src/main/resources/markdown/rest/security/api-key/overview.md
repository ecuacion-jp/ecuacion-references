Endpoints mapped under `/api/key/**` require a valid `X-Api-Key` header. See
[Quickstart](page?id=rest/security/api-key/quickstart&lang=en) for the shortest path to an actual
working call.

## Request headers

| Header | Required | Meaning |
| --- | --- | --- |
| `X-Api-Key` | Yes | The API key itself. |
| `X-Api-Key-Id` | No | An optional key identifier, passed through as-is to your provider — analogous to an AWS access key ID or an HTTP Basic username, used to look up *which* record's expected value to check. A single-shared-key setup can ignore it. |

For how the lookup logic is implemented, and what happens on a successful or failed match, see
[Authentication Handling](page?id=rest/security/api-key/authentication&lang=en).
