# General Settings Sheet

The General Settings sheet contains project-wide configuration.
Some entries must be updated for each new project; others are fixed framework defaults.

## Required Updates

| Row | Key | Description |
| --- | --- | --- |
| 8 | `SYSTEM_NAME` | Project identifier. Becomes the output folder name under `products/`. Use alphanumerics and hyphens (e.g., `my-project`) |
| 9 | `BASE_PACKAGE` | Common Java package prefix for all generated code (e.g., `jp.example.myapp`) |
| 31 | `TABLE_NAMES_WITHOUT_GROUPING` | Comma-separated list of table names that do NOT have the group filter column (`ACC_GROUP_ID`) |

### SYSTEM_NAME

Determines the output directory name and is embedded in the generated `Constants.java`:

```java
// Example: SYSTEM_NAME = "my-project"
public class Constants {
    public static final String SYSTEM_NAME = "my-project";
}
```

### BASE_PACKAGE

All generated Java sources use this as their package root:

```
jp.example.myapp.base.entity.AccEntity
jp.example.myapp.base.bl.AccBl
...
```

### TABLE_NAMES_WITHOUT_GROUPING

The `ecuacion-splib` framework adds `ACC_GROUP_ID` to most tables for multi-tenant support.
List the tables that do NOT have this column here (e.g., authentication tables like `ACC`, `ACC_ADMIN`).

---

## Framework Defaults (Normally Unchanged)

These reflect framework conventions and should not be changed unless there is a specific reason.

| Key | Default | Description |
| --- | --- | --- |
| `LOGICAL_DELETE.COLUMN_NAME` | `DEL_FLG` | Soft-delete flag column name |
| `GROUPING.COLUMN_NAME` | `ACC_GROUP_ID` | Multi-tenant group column name |
| `OPTIMISTIC_LOCKING.COLUMN_NAME` | `VERSION` | Optimistic locking version column name |
