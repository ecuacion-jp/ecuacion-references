# housekeep-files Task Patterns

This page describes the values for the "Task Pattern" column in the Task Settings sheet, and the details of each pattern.

Task patterns are divided into two categories: **Local operations** and **SFTP operations**.

---

## Local Operations

File and directory operations on the local filesystem.

### CREATE\_DIR (101) — Create Directory

Creates a directory at the specified path.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Prohibited (leave empty) |
| Destination path fields (4) | Required |
| Is Dest Dir | `true` |

---

### CREATE\_FILE (102) — Create File

Creates an empty file at the specified path.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Prohibited (leave empty) |
| Destination path fields (4) | Required |
| Is Dest Dir | `false` |

---

### MOVE (111) — Move

Moves a file or directory to another location.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Required |
| Destination path fields (4) | Required |

---

### COPY (121) — Copy

Copies a file or directory to another location. The original is kept.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Required |
| Destination path fields (4) | Required |

---

### DELETE (131) — Delete

Deletes a file or directory.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Required |
| Destination path fields (4) | Prohibited (leave empty) |

---

### ZIP\_DELETE\_ORIG (141) — ZIP and Delete Original

Compresses a file into a ZIP archive and deletes the original.
If the destination path is omitted, the ZIP is created in the same location as the source with a `.zip` extension.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Required |
| Destination path fields (4) | Optional |

---

### ZIP\_REMAIN\_ORIG (142) — ZIP and Keep Original

Compresses a file into a ZIP archive and keeps the original.
If the destination path is omitted, the ZIP is created in the same location as the source with a `.zip` extension.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Required |
| Destination path fields (4) | Optional |

---

### UNZIP\_DELETE\_ORIG (151) — Unzip and Delete Original

Extracts a ZIP file and deletes the original ZIP.
If the destination path is omitted, the files are extracted to the same directory as the ZIP file.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Required |
| Destination path fields (4) | Optional |

---

### UNZIP\_REMAIN\_ORIG (152) — Unzip and Keep Original

Extracts a ZIP file and keeps the original ZIP.
If the destination path is omitted, the files are extracted to the same directory as the ZIP file.

| Field | Input |
| --- | --- |
| Remote Server | Prohibited (leave empty) |
| Source path fields (5) | Required |
| Destination path fields (4) | Optional |

---

## SFTP Operations

File transfer operations with remote servers using SFTP.
All SFTP tasks require the "Remote Server" field.

### SFTP\_CREATE\_DIR (201) — Create Directory on SFTP Server

Creates a directory at the specified path on the remote server.

| Field | Input |
| --- | --- |
| Remote Server | Required |
| Source path fields (5) | Prohibited (leave empty) |
| Destination path fields (4) | Required (path on remote server) |

---

### SFTP\_CREATE\_FILE (202) — Create File on SFTP Server

Creates an empty file at the specified path on the remote server.

| Field | Input |
| --- | --- |
| Remote Server | Required |
| Source path fields (5) | Prohibited (leave empty) |
| Destination path fields (4) | Required (path on remote server) |

---

### SFTP\_MOVE\_FROM\_SERVER (211) — Move from SFTP Server to Local

Moves a file from the remote server to local. The file on the remote server is deleted.

| Field | Input |
| --- | --- |
| Remote Server | Required |
| Source path fields (5) | Required (path on remote server) |
| Destination path fields (4) | Required (local path) |

---

### SFTP\_MOVE\_TO\_SERVER (212) — Move from Local to SFTP Server

Moves a local file to the remote server. The local file is deleted.

| Field | Input |
| --- | --- |
| Remote Server | Required |
| Source path fields (5) | Required (local path) |
| Destination path fields (4) | Required (path on remote server) |

---

### SFTP\_COPY\_FROM\_SERVER (221) — Copy from SFTP Server to Local

Copies a file from the remote server to local. The file on the remote server remains.

| Field | Input |
| --- | --- |
| Remote Server | Required |
| Source path fields (5) | Required (path on remote server) |
| Destination path fields (4) | Required (local path) |

---

### SFTP\_COPY\_TO\_SERVER (222) — Copy from Local to SFTP Server

Copies a local file to the remote server. The local file remains.

| Field | Input |
| --- | --- |
| Remote Server | Required |
| Source path fields (5) | Required (local path) |
| Destination path fields (4) | Required (path on remote server) |

---

### SFTP\_DELETE\_FROM\_SERVER (231) — Delete File from SFTP Server

Deletes a file from the remote server.

| Field | Input |
| --- | --- |
| Remote Server | Required |
| Source path fields (5) | Required (path on remote server) |
| Destination path fields (4) | Prohibited (leave empty) |

---

## Summary Table

| Task Pattern | Value | Remote Server | Source Path | Dest Path |
| --- | --- | --- | --- | --- |
| CREATE\_DIR | 101 | Prohibited | Prohibited | Required |
| CREATE\_FILE | 102 | Prohibited | Prohibited | Required |
| MOVE | 111 | Prohibited | Required | Required |
| COPY | 121 | Prohibited | Required | Required |
| DELETE | 131 | Prohibited | Required | Prohibited |
| ZIP\_DELETE\_ORIG | 141 | Prohibited | Required | Optional |
| ZIP\_REMAIN\_ORIG | 142 | Prohibited | Required | Optional |
| UNZIP\_DELETE\_ORIG | 151 | Prohibited | Required | Optional |
| UNZIP\_REMAIN\_ORIG | 152 | Prohibited | Required | Optional |
| SFTP\_CREATE\_DIR | 201 | Required | Prohibited | Required (remote) |
| SFTP\_CREATE\_FILE | 202 | Required | Prohibited | Required (remote) |
| SFTP\_MOVE\_FROM\_SERVER | 211 | Required | Required (remote) | Required (local) |
| SFTP\_MOVE\_TO\_SERVER | 212 | Required | Required (local) | Required (remote) |
| SFTP\_COPY\_FROM\_SERVER | 221 | Required | Required (remote) | Required (local) |
| SFTP\_COPY\_TO\_SERVER | 222 | Required | Required (local) | Required (remote) |
| SFTP\_DELETE\_FROM\_SERVER | 231 | Required | Required (remote) | Prohibited |
