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
| Password | ○ | Database password |

**Supported Databases**

| Database | Driver Name | Connection URL: Protocol |
| --- | --- | --- |
| PostgreSQL | `org.postgresql.Driver` | `postgresql` |
| MySQL / MariaDB | `org.mariadb.jdbc.Driver` | `mysql` |
