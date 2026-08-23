Configures processing of related tables. Leave the sheet empty if not needed.

Two processing patterns are available:

| Pattern | Behavior |
| --- | --- |
| Delete | Delete related table records first, then delete the target table record |
| Check and Skip Delete | Skip deletion of the target record if a related record exists |

## Column Reference

| Column | Required | Description |
| --- | --- | --- |
| Task ID | ○ | Must match a Task ID in the Housekeep DB Settings sheet |
| Related Table Process Pattern | ○ | Display pattern name (`Delete` or `Check and Skip Delete`) |
| Target Table Column Name | ○ | Column in the target table used to join with the related table |
| Related Table Name | ○ | Name of the related table |
| Related Table ID Column Name | ○ | ID column (primary key or unique index) of the related table |
| Related Table ID Column Literal Symbol | ○ | Whether the related table ID column needs quoting: `(none)` or `quotes(')` |

**Soft Delete Columns for Related Table (Delete Pattern, Optional)**

When soft-deleting related table records, configure these columns:

| Column | Description |
| --- | --- |
| Soft Delete Column Name | Delete flag column of the related table |
| Soft Delete: Update Timestamp Column Name | Timestamp column to update on soft delete |
| Soft Delete: Update User ID Column Name | User ID column to update on soft delete |
| Soft Delete: Update User ID Column Literal Symbol | Whether the user ID column value needs quoting |
| Soft Delete: Update User ID Column Value | Value to set in the user ID column |
