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

    private JdbcGameStore() { }

    /** Loads the profile into Progress, creating a new local profile when needed. */
    public static boolean load(Progress progress) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                boolean created;
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT IGNORE INTO players (player_name) VALUES (?)")) {
                    insert.setString(1, PLAYER_NAME);
                    created = insert.executeUpdate() == 1;
                }
                if (created) {
                    connection.rollback();
                    return false;
                }
                try (PreparedStatement select = connection.prepareStatement(
                        "SELECT player_id, xp, character_level, coins FROM players WHERE player_name = ? FOR UPDATE")) {
                    select.setString(1, PLAYER_NAME);
                    try (ResultSet rows = select.executeQuery()) {
                        if (!rows.next()) throw new SQLException("Could not load the local player profile");
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
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException failure) {
                connection.rollback();
                throw failure;
            } finally {
                connection.setAutoCommit(true);
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
                    if (Potion.HEALING.name().equals(rows.getString(1))) quantities[0] = rows.getInt(2);
                    if (Potion.MANA.name().equals(rows.getString(1))) quantities[1] = rows.getInt(2);
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

    /** Persists player, inventory and the seven campaign levels in one transaction. */
    public static void save(Progress progress) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement upsert = connection.prepareStatement(
                        "INSERT INTO players (player_name, xp, character_level, coins) VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE xp=VALUES(xp), character_level=VALUES(character_level), coins=VALUES(coins)")) {
                    upsert.setString(1, PLAYER_NAME);
                    upsert.setInt(2, progress.getSavedXp());
                    upsert.setInt(3, progress.getSavedLevel());
                    upsert.setInt(4, progress.getSavedCoins());
                    upsert.executeUpdate();
                }
                long playerId;
                try (PreparedStatement query = connection.prepareStatement(
                        "SELECT player_id FROM players WHERE player_name = ?")) {
                    query.setString(1, PLAYER_NAME);
                    try (ResultSet rows = query.executeQuery()) {
                        if (!rows.next()) throw new SQLException("Player record disappeared during save");
                        playerId = rows.getLong(1);
                    }
                }
                saveInventory(connection, playerId, Potion.HEALING, progress.getSavedHealing());
                saveInventory(connection, playerId, Potion.MANA, progress.getSavedMana());
                saveStageProgress(connection, playerId, progress);
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                connection.rollback();
                throw failure;
            } finally {
                connection.setAutoCommit(true);
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
            upsert.setInt(3, quantity);
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
                boolean completed = progress.getStars(id) > 0;
                upsert.setLong(1, playerId);
                upsert.setInt(2, id);
                upsert.setBoolean(3, progress.isUnlocked(id));
                upsert.setBoolean(4, completed);
                upsert.setInt(5, progress.getStars(id));
                upsert.setBoolean(6, completed);
                upsert.addBatch();
            }
            upsert.executeBatch();
        }
    }

    /** Reads persisted stars for the six tutorial minigames. */
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
                    if (id >= 0 && id < count) stars[id] = rows.getInt(2);
                }
            }
        }
        return stars;
    }

    /** Saves tutorial minigame stars separately from the transient first-run tutorial flag. */
    public static void saveTutorialStars(int[] stars) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long playerId;
                try (PreparedStatement query = connection.prepareStatement(
                        "SELECT player_id FROM players WHERE player_name = ?")) {
                    query.setString(1, PLAYER_NAME);
                    try (ResultSet rows = query.executeQuery()) {
                        if (!rows.next()) throw new SQLException("Player profile must exist before tutorial progress");
                        playerId = rows.getLong(1);
                    }
                }
                try (PreparedStatement upsert = connection.prepareStatement(
                        "INSERT INTO tutorial_progress (player_id, tutorial_stage_id, unlocked, completed, stars, completed_at) "
                        + "VALUES (?, ?, ?, ?, ?, IF(?, CURRENT_TIMESTAMP, NULL)) ON DUPLICATE KEY UPDATE "
                        + "unlocked=VALUES(unlocked), completed=VALUES(completed), stars=VALUES(stars), "
                        + "completed_at=IF(VALUES(completed), COALESCE(completed_at, CURRENT_TIMESTAMP), NULL)")) {
                    for (int i = 0; i < stars.length; i++) {
                        boolean completed = stars[i] > 0;
                        upsert.setLong(1, playerId);
                        upsert.setInt(2, i + 1);
                        upsert.setBoolean(3, i == 0 || stars[i - 1] > 0);
                        upsert.setBoolean(4, completed);
                        upsert.setInt(5, stars[i]);
                        upsert.setBoolean(6, completed);
                        upsert.addBatch();
                    }
                    upsert.executeBatch();
                }
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                connection.rollback();
                throw failure;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
