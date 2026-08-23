Adds additional WHERE conditions to a task. Leave the sheet empty if not needed.

For example, to delete only records where `exit_code` is `COMPLETED`:

| Task ID | Search Condition Column Name | Search Condition Column Literal Symbol | Search Condition Column Value |
| --- | --- | --- | --- |
| task-1 | exit_code | quotes(') | COMPLETED |

Multiple rows with the same Task ID are combined with AND.
