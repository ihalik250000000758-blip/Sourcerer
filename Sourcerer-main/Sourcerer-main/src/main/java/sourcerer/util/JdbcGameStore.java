package sourcerer.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sourcerer.model.LevelBank;
import sourcerer.model.Potion;
import sourcerer.model.Progress;

/** JDBC persistence for the single local campaign profile used by the current game. */
public final class JdbcGameStore {
    private static final String PLAYER_NAME = "Sourcerer";
    private static final int MAX_STARS = 3;

    private JdbcGameStore() { }

    /**
     * Loads the profile into Progress. Read-only: it never creates a row.
     *
     * @return false when no profile exists yet (the caller then imports any local save
     *         and calls save(), which creates the profile).
     */
    public static boolean load(Progress progress) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                boolean found;
                try (PreparedStatement select = connection.prepareStatement(
                        "SELECT player_id, xp, character_level, coins FROM players "
                        + "WHERE player_name = ? FOR UPDATE")) {
                    select.setString(1, PLAYER_NAME);
                    try (ResultSet rows = select.executeQuery()) {
                        found = rows.next();
                        if (found) {
                            long playerId = rows.getLong("player_id");
                            int level = rows.getInt("character_level");
                            int xp = rows.getInt("xp");
                            int coins = rows.getInt("coins");
                            int[] inventory = loadInventory(connection, playerId);
                            int[] stars = loadProgress(connection, playerId);
                            progress.restoreDatabaseState(level, xp, 50 + 25 * (level - 1), coins,
                                    inventory[0], inventory[1], stars);
                        }
                    }
                }
                if (found) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
                return found;
            } catch (SQLException | RuntimeException failure) {
                rollbackQuietly(connection, failure);
                throw failure;
            } finally {
                restoreAutoCommitQuietly(connection);
            }
        }
    }

    private static int[] loadInventory(Connection connection, long playerId) throws SQLException {
        int[] quantities = new int[2];
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT potion_code, quantity FROM inventory WHERE player_id = ?")) {
            query.setLong(1, playerId);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    String code = rows.getString(1);
                    if (Potion.HEALING.name().equals(code)) quantities[0] = rows.getInt(2);
                    else if (Potion.MANA.name().equals(code)) quantities[1] = rows.getInt(2);
                }
            }
        }
        return quantities;
    }

    private static int[] loadProgress(Connection connection, long playerId) throws SQLException {
        int[] stars = new int[LevelBank.count() + 2];
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT stage_id, stars FROM player_progress WHERE player_id = ?")) {
            query.setLong(1, playerId);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    int id = rows.getInt(1);
                    if (id > 0 && id <= LevelBank.count()) stars[id] = rows.getInt(2);
                }
            }
        }
        return stars;
    }

    /** Persists player, inventory and the campaign levels in one transaction. */
    public static void save(Progress progress) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement upsert = connection.prepareStatement(
                        "INSERT INTO players (player_name, xp, character_level, coins) VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE xp=VALUES(xp), character_level=VALUES(character_level), "
                        + "coins=VALUES(coins)")) {
                    upsert.setString(1, PLAYER_NAME);
                    upsert.setInt(2, progress.getSavedXp());
                    upsert.setInt(3, progress.getSavedLevel());
                    upsert.setInt(4, progress.getSavedCoins());
                    upsert.executeUpdate();
                }
                long playerId = findPlayerId(connection, "Player record disappeared during save");
                saveInventory(connection, playerId, Potion.HEALING, progress.getSavedHealing());
                saveInventory(connection, playerId, Potion.MANA, progress.getSavedMana());
                saveStageProgress(connection, playerId, progress);
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                rollbackQuietly(connection, failure);
                throw failure;
            } finally {
                restoreAutoCommitQuietly(connection);
            }
        }
    }

    private static long findPlayerId(Connection connection, String missingMessage) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT player_id FROM players WHERE player_name = ?")) {
            query.setString(1, PLAYER_NAME);
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw new SQLException(missingMessage);
                return rows.getLong(1);
            }
        }
    }

    private static void saveInventory(Connection connection, long playerId, Potion potion, int quantity)
            throws SQLException {
        try (PreparedStatement upsert = connection.prepareStatement(
                "INSERT INTO inventory (player_id, potion_code, quantity) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE quantity=VALUES(quantity)")) {
            upsert.setLong(1, playerId);
            upsert.setString(2, potion.name());
            upsert.setInt(3, Math.max(0, quantity));
            upsert.executeUpdate();
        }
    }

    private static void saveStageProgress(Connection connection, long playerId, Progress progress)
            throws SQLException {
        try (PreparedStatement upsert = connection.prepareStatement(
                "INSERT INTO player_progress (player_id, stage_id, unlocked, completed, stars, completed_at) "
                + "VALUES (?, ?, ?, ?, ?, IF(?, CURRENT_TIMESTAMP, NULL)) ON DUPLICATE KEY UPDATE "
                + "unlocked=VALUES(unlocked), completed=VALUES(completed), stars=VALUES(stars), "
                + "completed_at=IF(VALUES(completed), COALESCE(completed_at, CURRENT_TIMESTAMP), NULL)")) {
            for (int id = 1; id <= LevelBank.count(); id++) {
                int stars = clampStars(progress.getStars(id));
                boolean completed = stars > 0;
                // The schema requires (unlocked OR NOT completed); a completed stage is always unlocked.
                boolean unlocked = progress.isUnlocked(id) || completed;
                upsert.setLong(1, playerId);
                upsert.setInt(2, id);
                upsert.setBoolean(3, unlocked);
                upsert.setBoolean(4, completed);
                upsert.setInt(5, stars);
                upsert.setBoolean(6, completed);
                upsert.addBatch();
            }
            upsert.executeBatch();
        }
    }

    /** Reads persisted stars for the tutorial minigames. */
    public static int[] loadTutorialStars(int count) throws SQLException {
        int[] stars = new int[count];
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement query = connection.prepareStatement(
                     "SELECT tp.tutorial_stage_id, tp.stars FROM tutorial_progress tp "
                     + "JOIN players p ON p.player_id = tp.player_id WHERE p.player_name = ?")) {
            query.setString(1, PLAYER_NAME);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    int id = rows.getInt(1) - 1;
                    if (id >= 0 && id < count) stars[id] = clampStars(rows.getInt(2));
                }
            }
        }
        return stars;
    }

    /**
     * Saves tutorial minigame stars. The player profile must already exist (Progress creates it),
     * so a tutorial save can never invent a default profile that would hide a local save import.
     */
    public static void saveTutorialStars(int[] stars) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long playerId = findPlayerId(connection,
                        "Player profile must exist before tutorial progress");
                try (PreparedStatement upsert = connection.prepareStatement(
                        "INSERT INTO tutorial_progress (player_id, tutorial_stage_id, unlocked, completed, stars, completed_at) "
                        + "VALUES (?, ?, ?, ?, ?, IF(?, CURRENT_TIMESTAMP, NULL)) ON DUPLICATE KEY UPDATE "
                        + "unlocked=VALUES(unlocked), completed=VALUES(completed), stars=VALUES(stars), "
                        + "completed_at=IF(VALUES(completed), COALESCE(completed_at, CURRENT_TIMESTAMP), NULL)")) {
                    for (int i = 0; i < stars.length; i++) {
                        int earned = clampStars(stars[i]);
                        boolean completed = earned > 0;
                        // Never write completed=TRUE with unlocked=FALSE (violates chk_tutorial_unlock_order).
                        boolean unlocked = i == 0 || clampStars(stars[i - 1]) > 0 || completed;
                        upsert.setLong(1, playerId);
                        upsert.setInt(2, i + 1);
                        upsert.setBoolean(3, unlocked);
                        upsert.setBoolean(4, completed);
                        upsert.setInt(5, earned);
                        upsert.setBoolean(6, completed);
                        upsert.addBatch();
                    }
                    upsert.executeBatch();
                }
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                rollbackQuietly(connection, failure);
                throw failure;
            } finally {
                restoreAutoCommitQuietly(connection);
            }
        }
    }

    private static int clampStars(int stars) {
        return Math.max(0, Math.min(MAX_STARS, stars));
    }

    /** Rolls back without letting a secondary failure hide the original exception. */
    private static void rollbackQuietly(Connection connection, Throwable cause) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            cause.addSuppressed(rollbackFailure);
        }
    }

    private static void restoreAutoCommitQuietly(Connection connection) {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
            // The connection is closed right after; nothing useful can be done here.
        }
    }
}
