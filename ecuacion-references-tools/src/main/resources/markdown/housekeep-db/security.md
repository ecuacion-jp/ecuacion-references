This page covers the security considerations behind how `ecuacion-tool-housekeep-db` builds its SQL statements.

## Table / Column Name Validation

Table and column names specified in the excel config (`Table Name`, `ID Column Name`, `Related Table Name`, etc.) are all validated against the regular expression `^[A-Za-z_][A-Za-z0-9_]*$` (letters, digits, and underscores only, and must not start with a digit).

These values are embedded directly (unquoted) into the generated SQL, but this restriction excludes any character that could not be interpreted as a plain identifier (quotes, semicolons, whitespace, etc.), so SQL injection via identifiers is not possible.

## Parameter Binding

The following values are bound to `PreparedStatement` `?` placeholders rather than embedded as SQL literal text:

- The id of each housekeep target record and the foreign-key value of each related-table row (both read back from the DB)
- The soft-delete flag (`true` / `false`)
- The update timestamp ("now")

Because of this, the content of data already stored in the target table cannot break or inject into the generated SQL. It also means the tool does not need to account for differences in string-literal escaping rules between PostgreSQL and MySQL / MariaDB (the latter treats backslash as an escape character by default) - binding sidesteps that dialect difference entirely.

## Excel Config Values

Values typed directly into the excel config - the Search Condition Settings sheet's `Condition Value`, and the "updated by" user id value - are not covered by the above. These are trusted, administrator-authored configuration, so they continue to be embedded as SQL literal text with single quotes escaped, as before.
