# Local database setup (MySQL Workbench)

These steps create a local database for Sourcerer: Crusade. They do not host the
game online or synchronize saves between computers. The SQL scripts have not
been run against your database.

## 1. Start and check the database server

Start the local MySQL or MariaDB service and note its configured port and
credentials. In Workbench, connect to `127.0.0.1` using those values and confirm
**Test Connection** succeeds. Do not assume that the port or root password uses
the common XAMPP defaults. The schema uses InnoDB, `utf8mb4`, foreign keys and
CHECK constraints; use a current MySQL or MariaDB release that enforces CHECK
constraints.

## 2. Create the schema and seed game definitions

Run these files in order from Workbench, executing the full script each time:

1. `sql/01_create_database.sql`
2. `sql/02_create_tables.sql`
3. `sql/03_constraints_and_indexes.sql`
4. `sql/04_seed_game_data.sql`

The scripts create the schema and tables without deleting existing data. The
seed script inserts exactly seven campaign levels and six tutorial minigames.
The campaign's current source has no boss encounters, so no boss table or boss
rows are defined. Tutorial minigame stars are stored separately from the
one-time introductory tutorial prompt, which is transient and is not saved.

Expand `sourcerer_crusade` in Workbench's Schemas panel, then open **Tables** to
inspect `players`, `stages`, `player_progress`, `tutorial_stages`,
`tutorial_progress`, and `inventory`.

## 3. Inspect data and columns

Run `sql/05_verification_queries.sql`. It displays the overall player view,
level-by-level progress, tutorial stars, potion inventory, a note that the
current campaign has no boss encounters, all table names, and every table's
column metadata. Empty player result sets are expected until a save exists.
The combined progress query is first in the file.

`sql/06_reset_test_data.sql` is guarded: by default it only previews the
`Sourcerer` profile and deletes nothing. To reset a disposable test profile,
change `@confirm_reset` in that script to the exact phrase shown in its comment,
review the preview, then execute the script. The deletion only affects that
named profile and its dependent rows.

## 4. Configure and launch the Java application

`sourcerer.util.DatabaseConnection` reads these environment variables:

- `SOURCERER_DB_URL` (default: `jdbc:mysql://localhost:3306/sourcerer_crusade`)
- `SOURCERER_DB_USER` (default: `root`)
- `SOURCERER_DB_PASSWORD` (default: blank)

Database persistence is enabled when `SOURCERER_DB_URL` is set. Otherwise the
game uses its local properties save. The application uses one profile named
`Sourcerer`; connection or schema errors stop the load instead of replacing
saved state. New saves start with zero potions. An existing inventory quantity
is preserved when loaded and saved.

Set the variables in a temporary PowerShell session using the server address,
port, username, and password you verified above:

```powershell
$env:SOURCERER_DB_URL = 'jdbc:mysql://localhost:3306/sourcerer_crusade'
$env:SOURCERER_DB_USER = 'root'
$env:SOURCERER_DB_PASSWORD = ''
mvn clean package
mvn exec:java
```

Run the SQL setup scripts before starting with database mode enabled. The Java
code saves player XP, character level, coins, potion quantities, seven-level
campaign stars/unlocks, and tutorial minigame stars.
