# Local database setup (MySQL Workbench)

These steps create a local database for Sourcerer: Crusade. They do not host the
game online or synchronize saves between computers. The SQL scripts have not
been run against your database.

## 1. Start XAMPP and check the database server

1. Open the XAMPP Control Panel and start **MySQL**. Keep the control panel open
   while using the game.
2. If startup fails, open the XAMPP MySQL log and resolve the port conflict
   before continuing. Do not change ports blindly.
3. Identify which server XAMPP installed (MySQL or MariaDB) and its configured
   port. Common local installations use port `3306`, but verify yours in the
   XAMPP configuration/logs. The JDBC URL and Workbench connection must use the
   same port.
4. In MySQL Workbench, create or open a connection to `127.0.0.1`, using that
   verified port and the credentials configured for your local server. A
   typical XAMPP account is `root` with a blank password, but installations can
   differ. Verify your actual credentials; do not assume the defaults.
5. Click **Test Connection**. Resolve any connection or authentication error
   before running scripts.

The scripts use `utf8mb4`, InnoDB, foreign keys, and CHECK constraints. Use a
current MySQL 8.0 server (8.0.16 or later for enforced CHECK constraints) or a
recent MariaDB version that supports CHECK constraints. Older versions may
parse but not enforce some checks. Verify your actual server/version first.

## 2. Create tables and seed the stage list

The setup scripts are ordered. They create the `sourcerer_crusade` schema only
if it does not exist. Table creation does not erase existing rows, and the
stage seed inserts missing definitions without replacing existing rows.

1. In Workbench choose **File → Open SQL Script…** and open
   `sql/01_create_database.sql` from the project folder.
2. Click the lightning bolt (**Execute**) to run the whole script. The Action
   Output panel should show successful statements. If you select only part of
   a script, Workbench executes only that selection.
3. In the **Schemas** panel, click the refresh icon. Expand `sourcerer_crusade`
   and confirm it appears.
4. Open and run these scripts in order:
   - `sql/02_create_tables.sql`
   - `sql/03_constraints_and_indexes.sql`
   - `sql/04_seed_game_data.sql`

   The third script reports constraints; the keys, foreign keys, indexes, and
   checks are declared with the tables. The seed script adds stages 1–100 and
   marks every tenth stage as a boss. Stage rows are definitions only; challenge
   content remains in the Java game.
5. Refresh the Schemas panel again. Expand **Tables** and confirm `players`,
   `stages`, `player_progress`, `inventory`, `boss_defeats`, and
   `operation_history` exist. Expand each table's **Columns**, **Indexes**, and
   **Foreign Keys** nodes to inspect its structure and relationships.

If Workbench shows an error, select the failed statement in the Action Output,
read the server message, and check server version, active schema, and syntax
compatibility. Correct the cause before continuing. Do not repeatedly execute
the remaining scripts after a failure.

## 3. Verify the schema

1. Open and execute `sql/05_verification_queries.sql`.
2. The stage summary should report 90 standard stages and 10 boss stages. The
   player, inventory, progress, boss, and history result sets can be empty
   before a player account is created.
3. For an individual query, place the caret in its statement or highlight that
   statement, then click the lightning bolt. Workbench displays results in a
   result grid below the editor.

## 4. Configure and launch the Java application

`sourcerer.util.DatabaseConnection` reads these environment variables:

- `SOURCERER_DB_URL` (default: `jdbc:mysql://localhost:3306/sourcerer_crusade`)
- `SOURCERER_DB_USER` (default: `root`)
- `SOURCERER_DB_PASSWORD` (default: blank)

The game uses JDBC persistence when `SOURCERER_DB_URL` is set. If it is unset,
the existing local properties save remains active. Once database mode is
selected, connection or schema errors stop the load with an actionable error;
the game does not silently start over with defaults. The current game uses one
profile named `Sourcerer`. The project includes MySQL Connector/J through Maven.

Set the variables in a temporary PowerShell session using the host, port,
username, and password you verified above, then build and launch from the same
window:

```powershell
$env:SOURCERER_DB_URL = 'jdbc:mysql://localhost:3306/sourcerer_crusade'
$env:SOURCERER_DB_USER = 'root'
$env:SOURCERER_DB_PASSWORD = ''
mvn clean package
mvn exec:java
```

Do not put passwords in source code or commit them. Run the SQL setup scripts
before starting with database mode enabled.

## 5. Check persistence

1. Start the application with database mode enabled. It creates the `Sourcerer`
   profile on first launch or restores its existing record.
2. Complete a stage that has not received its first-clear reward, then buy a
   potion with enough coins.
3. Defeat a boss stage and run `sql/05_verification_queries.sql` again. Check
   XP, coins, inventory, stage progress, and individual boss records.
4. Close and restart the game, then confirm the same state is restored.

## Test data cleanup

`sql/06_reset_test_data.sql` is destructive for one named account: it deletes
`Workbench Test Player` and linked rows. Use it only if you created that
disposable account and are certain it contains nothing valuable. It is never
run automatically. Do not change the name to a real player's name.
