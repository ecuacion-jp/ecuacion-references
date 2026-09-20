Defines database connections used by tasks. Multiple connections can be registered.

| Column | Required | Description |
| --- | --- | --- |
| DB Connection ID | ○ | Identifier for the connection. Referenced from the Housekeep DB Settings sheet |
| Driver Name | ○ | Fully-qualified JDBC driver class name (see table below) |
| Connection URL: Protocol | ○ | Protocol part of the connection URL (see table below) |
| Connection URL: Server | ○ | Database server hostname or IP address |
| Connection URL: Port | ○ | Port number |
| Connection URL: Database | ○ | Database name |
| Connection URL: Schema | — | Schema name (optional, **PostgreSQL only** — MySQL / MariaDB have no separate schema concept and ignore this column) |
| Username | ○ | Database username |
| Password | ○ | Database password. No length limit - a `${VAR_NAME}` reference (see below) is recommended over writing the secret directly |

Writing the actual database password into the Excel file makes the file itself a secret (it becomes something you must never commit to a repository or share carelessly). To avoid that, write a `${VAR_NAME}` reference instead and define the variable as an OS environment variable, a JVM system property, or an `application.properties` entry (see [Configuration](page?id=housekeep-db/config&lang=en)):

```properties
Password column: ${DB_PASSWORD}
```

```bash
export DB_PASSWORD=the-actual-secret
```

**Supported Databases**

| Database | Driver Name | Connection URL: Protocol |
| --- | --- | --- |
| PostgreSQL | `org.postgresql.Driver` | `postgresql` |
| MySQL / MariaDB | `org.mariadb.jdbc.Driver` | `mysql` |

## Example

See the [Quickstart](page?id=housekeep-db/quickstart&lang=en) for a concrete example.
