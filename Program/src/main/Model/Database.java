package main.Model;

import main.Model.util.HeroType;
import main.Model.util.MonsterType;
import main.Model.util.RoomType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database handler for DungeonDive game.
 * Manages database connection, table creation, and initial data population.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class Database {
    private Connection myConnection;
    private static final String DB_NAME = "dungeondive.db"; // In the root of the project

    /**
     * Constructor initializes database connection.
     */
    public Database() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Create connection to database (will create the file if it doesn't exist)
            myConnection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            System.out.println("Database connection established to " + DB_NAME);
            initializeTables();
            populateInitialData();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
            e.printStackTrace(); // For more detailed error info
        } catch (SQLException e) {
            System.err.println("Database error during initialization: " + e.getMessage());
            e.printStackTrace(); // For more detailed error info
        }
    }

    /**
     * Creates necessary tables if they do not already exist.
     */
    private void initializeTables() throws SQLException {
        try (Statement statement = myConnection.createStatement()) {
            System.out.println("Attempting to create table: character_types");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS character_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "base_health INTEGER NOT NULL," +
                            "base_attack INTEGER NOT NULL," +
                            "special_attack_name TEXT," +
                            "special_attack_damage INTEGER," +
                            "crit_chance REAL DEFAULT 0.0," +
                            "crit_multiplier REAL DEFAULT 1.5," +
                            "description TEXT" +
                            ");"
            );
            System.out.println("Table created/verified: character_types");

            System.out.println("Attempting to create table: player");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS player (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "character_type_id INTEGER," +
                            "health INTEGER," +
                            "max_health INTEGER," +
                            "attack INTEGER," +
                            "crit_chance REAL," +
                            "crit_multiplier REAL," +
                            "gold INTEGER DEFAULT 0," +
                            "pillars_activated INTEGER DEFAULT 0," +
                            "x_position INTEGER DEFAULT 0," +
                            "y_position INTEGER DEFAULT 0," +
                            "current_dungeon_id INTEGER," +
                            "FOREIGN KEY (character_type_id) REFERENCES character_types(id)" +
                            // "FOREIGN KEY (current_dungeon_id) REFERENCES dungeon(id)" + // Keep commented if dungeon table might not exist yet or for simplicity
                            ");"
            );
            System.out.println("Table created/verified: player");

            System.out.println("Attempting to create table: monster_types");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS monster_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "health INTEGER NOT NULL," +
                            "attack INTEGER NOT NULL," +
                            "special_attack_name TEXT," +
                            "crit_chance REAL DEFAULT 0.0," +
                            "crit_multiplier REAL DEFAULT 1.5," +
                            "gold_reward INTEGER DEFAULT 0," +
                            "is_elite BOOLEAN DEFAULT 0," +
                            "is_boss BOOLEAN DEFAULT 0," +
                            "description TEXT" +
                            ");"
            );
            System.out.println("Table created/verified: monster_types");

            System.out.println("Attempting to create table: room_types"); // Create this before tables that reference it
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS room_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "description TEXT" +
                            ");"
            );
            System.out.println("Table created/verified: room_types");

            System.out.println("Attempting to create table: dungeon"); // Create this before tables that reference it
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS dungeon (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT UNIQUE," +
                            "difficulty TEXT," +
                            "width INTEGER," +
                            "height INTEGER," +
                            "pillars_total INTEGER DEFAULT 0," + // Default to 0, actual count set by Dungeon generation
                            "pillars_activated INTEGER DEFAULT 0," +
                            "boss_spawned BOOLEAN DEFAULT 0" +
                            ");"
            );
            System.out.println("Table created/verified: dungeon");

            System.out.println("Attempting to create table: rooms");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS rooms (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "room_type_id INTEGER," +
                            "x_position INTEGER NOT NULL," +
                            "y_position INTEGER NOT NULL," +
                            "dungeon_id INTEGER NOT NULL," +
                            "is_visited BOOLEAN DEFAULT 0," +
                            "has_north_door BOOLEAN DEFAULT 0," +
                            "has_east_door BOOLEAN DEFAULT 0," +
                            "has_south_door BOOLEAN DEFAULT 0," +
                            "has_west_door BOOLEAN DEFAULT 0," +
                            "contains_pillar BOOLEAN DEFAULT 0," +
                            "pillar_activated BOOLEAN DEFAULT 0," +
                            "FOREIGN KEY (room_type_id) REFERENCES room_types(id)," +
                            "FOREIGN KEY (dungeon_id) REFERENCES dungeon(id)," +
                            "UNIQUE (dungeon_id, x_position, y_position)" +
                            ");"
            );
            System.out.println("Table created/verified: rooms");

            System.out.println("Attempting to create table: monsters");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS monsters (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "monster_type_id INTEGER," +
                            "current_health INTEGER," +
                            "x_position INTEGER," +
                            "y_position INTEGER," +
                            "dungeon_id INTEGER," +
                            "room_id INTEGER," +
                            "FOREIGN KEY (monster_type_id) REFERENCES monster_types(id)," +
                            "FOREIGN KEY (room_id) REFERENCES rooms(id)," +
                            "FOREIGN KEY (dungeon_id) REFERENCES dungeon(id)" +
                            ");"
            );
            System.out.println("Table created/verified: monsters");

            System.out.println("Attempting to create table: item_types");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS item_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "effect TEXT," +
                            "value INTEGER," +
                            "description TEXT" +
                            ");"
            );
            System.out.println("Table created/verified: item_types");

            System.out.println("Attempting to create table: items");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "item_type_id INTEGER," +
                            "player_id INTEGER NULL," +
                            "room_id INTEGER NULL," + // Item can be in a room
                            // "dungeon_id INTEGER NULL," + // room_id implies dungeon_id via rooms table
                            "FOREIGN KEY (item_type_id) REFERENCES item_types(id)," +
                            "FOREIGN KEY (player_id) REFERENCES player(id)," +
                            "FOREIGN KEY (room_id) REFERENCES rooms(id)" +
                            ");"
            );
            System.out.println("Table created/verified: items");

            System.out.println("Attempting to create table: pillars");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS pillars (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "pillar_type_name TEXT NOT NULL," + // Store enum name e.g. "ABSTRACTION"
                            "room_id INTEGER," +
                            "is_activated BOOLEAN DEFAULT 0," +
                            "FOREIGN KEY (room_id) REFERENCES rooms(id)" +
                            ");"
            );
            System.out.println("Table created/verified: pillars");

            System.out.println("All database tables created/verified successfully.");
        }
    }

    /**
     * Populates tables with initial game data from Enums if they are empty.
     */
    private void populateInitialData() throws SQLException {
        if (isTableEmpty("character_types")) {
            System.out.println("Populating character_types...");
            String sql = "INSERT INTO character_types (name, base_health, base_attack, special_attack_name, special_attack_damage, crit_chance, crit_multiplier, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
                for (HeroType type : HeroType.values()) {
                    pstmt.setString(1, type.getDisplayName());
                    pstmt.setInt(2, type.getBaseHealth());
                    pstmt.setInt(3, type.getBaseAttack());
                    pstmt.setString(4, type.getSpecialAttackName());
                    pstmt.setInt(5, type.getSpecialAttackDamage());
                    pstmt.setDouble(6, type.getCritChance());
                    pstmt.setDouble(7, type.getCritMultiplier());
                    pstmt.setString(8, type.getDescription());
                    pstmt.executeUpdate();
                }
                System.out.println("Populated character_types from HeroType enum.");
            }
        }

        if (isTableEmpty("monster_types")) {
            System.out.println("Populating monster_types...");
            String sql = "INSERT INTO monster_types (name, health, attack, special_attack_name, crit_chance, crit_multiplier, gold_reward, is_elite, is_boss, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
                for (MonsterType type : MonsterType.values()) {
                    pstmt.setString(1, type.getName());
                    pstmt.setInt(2, type.getBaseHealth());
                    pstmt.setInt(3, type.getBaseAttack());
                    pstmt.setString(4, type.getSpecialAttackName());
                    pstmt.setDouble(5, type.getCritChance());
                    pstmt.setDouble(6, type.getCritMultiplier());
                    pstmt.setInt(7, type.getGoldReward());
                    pstmt.setBoolean(8, type.isElite());
                    pstmt.setBoolean(9, type.isBoss());
                    pstmt.setString(10, type.getDescription());
                    pstmt.executeUpdate();
                }
                System.out.println("Populated monster_types from MonsterType enum.");
            }
        }

        if (isTableEmpty("item_types")) {
            System.out.println("Populating item_types...");
            executeUpdate("INSERT INTO item_types (name, effect, value, description) VALUES " +
                    "('Health Potion', 'heal', 25, 'Restores 25 health points')," +
                    "('Vision Potion', 'vision', 0, 'Reveals surrounding rooms')," + // Value 0 as it's an effect, not a quantity
                    "('Bomb', 'damage', 20, 'Deals 20 damage to enemies in the room')");
            System.out.println("Populated item_types with default items.");
        }

        if (isTableEmpty("room_types")) {
            System.out.println("Populating room_types...");
            String sql = "INSERT INTO room_types (name, description) VALUES (?, ?)";
            try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
                for (RoomType type : RoomType.values()) {
                    pstmt.setString(1, type.getDisplayName());
                    pstmt.setString(2, type.getDescription());
                    pstmt.executeUpdate();
                }
                System.out.println("Populated room_types from RoomType enum.");
            }
        }
        System.out.println("Initial game data population check complete.");
    }

    private boolean isTableEmpty(final String theTableName) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM " + theTableName;
        try (Statement stmt = myConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        }
        return true;
    }

    public void executeUpdate(final String theSql) {
        try (Statement stmt = myConnection.createStatement()) {
            stmt.executeUpdate(theSql);
        } catch (SQLException e) {
            System.err.println("Error executing update: " + e.getMessage() + " (SQL: " + theSql + ")");
        }
    }

    public ResultSet executeQuery(final String theSql) {
        try {
            Statement stmt = myConnection.createStatement();
            return stmt.executeQuery(theSql);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage() + " (SQL: " + theSql + ")");
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (myConnection != null && !myConnection.isClosed()) {
                myConnection.close();
                System.out.println("Database connection closed successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
