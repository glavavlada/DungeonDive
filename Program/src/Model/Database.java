package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database handler for DungeonDive game.
 * basic outline for DataBase that will store character types, monster types,
 * item types, room types and dungeon layout with instances of each type.
 * This will also be used for loading and saving data with multiple save files
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 4/26/2025
 */
public class Database {
    private Connection connection;
    private static final String DB_NAME = "dungeondive.db";

    /**
     * Constructor initializes database connection.
     */
    public Database() {
        try {
            //load SQLite JDBC driver(3.49.1.0)
            Class.forName("org.sqlite.JDBC");
            //create connection to database
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            System.out.println("Database connection established");
            initializeTables();
            // Populate tables with initial data
            populateInitialData();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    /**
     * Creates necessary tables
     */
    private void initializeTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create character_types table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS character_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "base_health INTEGER," +
                            "base_attack INTEGER," +
                            "special_attack_name TEXT," +
                            "special_attack_damage INTEGER," +
                            "crit_chance REAL DEFAULT 0.10," +
                            "crit_multiplier REAL DEFAULT 2.0," +
                            "description TEXT" +
                            ");"
            );

            // Create player table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS player (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "character_type_id INTEGER," +
                            "health INTEGER," +
                            "max_health INTEGER," +
                            "attack INTEGER," +
                            "special_attack INTEGER," +
                            "crit_chance REAL," +
                            "crit_multiplier REAL DEFAULT 2.0," +
                            "gold INTEGER DEFAULT 0," +
                            "pillars_activated INTEGER DEFAULT 0," +
                            "x_position INTEGER DEFAULT 0," +
                            "y_position INTEGER DEFAULT 0," +
                            "current_dungeon_id INTEGER," +
                            "FOREIGN KEY (character_type_id) REFERENCES character_types(id)" +
                            ");"
            );

            // Create monster_types table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS monster_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "health INTEGER," +
                            "attack INTEGER," +
                            "special_attack TEXT," +
                            "crit_chance REAL DEFAULT 0.05," +
                            "crit_multiplier REAL DEFAULT 2.0," +
                            "is_elite BOOLEAN DEFAULT 0," +
                            "is_boss BOOLEAN DEFAULT 0," +
                            "description TEXT" +
                            ");"
            );

            // Create monsters table
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
                            "FOREIGN KEY (room_id) REFERENCES rooms(id)" +
                            ");"
            );

            // Create item_types table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS item_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "effect TEXT," +
                            "value INTEGER," +
                            "description TEXT" +
                            ");"
            );

            // Create items table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "item_type_id INTEGER," +
                            "player_id INTEGER," +
                            "room_id INTEGER," +
                            "is_used BOOLEAN DEFAULT 0," +
                            "FOREIGN KEY (item_type_id) REFERENCES item_types(id)," +
                            "FOREIGN KEY (player_id) REFERENCES player(id)," +
                            "FOREIGN KEY (room_id) REFERENCES rooms(id)" +
                            ");"
            );

            // Create dungeon table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS dungeon (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "difficulty TEXT," +
                            "width INTEGER," +
                            "height INTEGER," +
                            "pillars_total INTEGER DEFAULT 4," +
                            "pillars_activated INTEGER DEFAULT 0," +
                            "boss_spawned BOOLEAN DEFAULT 0" +
                            ");"
            );

            // Create room_types table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS room_types (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "description TEXT" +
                            ");"
            );

            // Create rooms table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS rooms (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "room_type_id INTEGER," +
                            "x_position INTEGER," +
                            "y_position INTEGER," +
                            "dungeon_id INTEGER," +
                            "is_visited BOOLEAN DEFAULT 0," +
                            "has_north_door BOOLEAN DEFAULT 0," +
                            "has_east_door BOOLEAN DEFAULT 0," +
                            "has_south_door BOOLEAN DEFAULT 0," +
                            "has_west_door BOOLEAN DEFAULT 0," +
                            "contains_pillar BOOLEAN DEFAULT 0," +
                            "pillar_activated BOOLEAN DEFAULT 0," +
                            "FOREIGN KEY (room_type_id) REFERENCES room_types(id)," +
                            "FOREIGN KEY (dungeon_id) REFERENCES dungeon(id)" +
                            ");"
            );

            // Create pillars table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS pillars (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "room_id INTEGER," +
                            "is_activated BOOLEAN DEFAULT 0," +
                            "buff_type TEXT," +
                            "buff_value INTEGER," +
                            "FOREIGN KEY (room_id) REFERENCES rooms(id)" +
                            ");"
            );

            System.out.println("Database tables created successfully");
        }
    }

    /**
     * Populates tables with initial game data.
     */
    private void populateInitialData() throws SQLException {
        if (isTablePopulated("character_types")) {
            // Populate character types (Warrior, Rogue, Mage) with different crit chances
            executeUpdate("INSERT INTO character_types (name, base_health, base_attack, special_attack_name, special_attack_damage, crit_chance, description) VALUES " +
                    "('Warrior', 125, 15, 'Mighty Swing', 25, 0.05, 'A strong fighter with high health and physical attack')");
            executeUpdate("INSERT INTO character_types (name, base_health, base_attack, special_attack_name, special_attack_damage, crit_chance, description) VALUES " +
                    "('Rogue', 90, 20, 'Backstab', 35, 0.20, 'A nimble fighter with high damage and critical hit chance')");
            executeUpdate("INSERT INTO character_types (name, base_health, base_attack, special_attack_name, special_attack_damage, crit_chance, description) VALUES " +
                    "('Mage', 75, 10, 'Fireball', 40, 0.05, 'A spellcaster with powerful special attacks but low critical chance')");
        }

        if (isTablePopulated("monster_types")) {
            // Populate regular monster types (Goblin, Skeleton, Slime) with crit chances
            executeUpdate("INSERT INTO monster_types (name, health, attack, special_attack, crit_chance, gold_reward, is_elite, is_boss, description) VALUES " +
                    "('Goblin', 40, 8, 'Stab', 0.04, 0, 0, 'A small green creature with a knife')");
            executeUpdate("INSERT INTO monster_types (name, health, attack, special_attack, crit_chance, gold_reward, is_elite, is_boss, description) VALUES " +
                    "('Skeleton', 35, 10, 'Bone Throw', 0.03, 0, 0, 'An animated body of bones with a sword')");
            executeUpdate("INSERT INTO monster_types (name, health, attack, special_attack, crit_chance, gold_reward, is_elite, is_boss, description) VALUES " +
                    "('Slime', 30, 5, 'Acid Splash', 0.02, 0, 0, 'A green blob on the floor')");

            // Populate elite monster types (Orc, Big Slime, Wizard) with higher crit chances
            executeUpdate("INSERT INTO monster_types (name, health, attack, special_attack, crit_chance, gold_reward, is_elite, is_boss, description) VALUES " +
                    "('Orc', 80, 15, 'Cleave', 0.06, 1, 0, 'A large brutish goblin-alike with green skin')");
            executeUpdate("INSERT INTO monster_types (name, health, attack, special_attack, crit_chance, gold_reward, is_elite, is_boss, description) VALUES " +
                    "('Big Slime', 70, 12, 'Engulf', 0.05, 1, 0, 'A massive slime that can split into smaller slime')");
            executeUpdate("INSERT INTO monster_types (name, health, attack, special_attack, crit_chance, gold_reward, is_elite, is_boss, description) VALUES " +
                    "('Wizard', 60, 20, 'Lightning Bolt', 0.08, 1, 0, 'A powerful wizard with magic')");

            // Populate boss monster (Giant) with significant crit chance
            executeUpdate("INSERT INTO monster_types (name, health, attack, special_attack, crit_chance, gold_reward, is_elite, is_boss, description) VALUES " +
                    "('Giant', 200, 25, 'Ground Slam', 0.10, 0, 1, 'A massive humanoid that towers over anyone')");
        }

        if (isTablePopulated("item_types")) {
            // Populate item types (Potion, Vision Potion, Bomb)
            executeUpdate("INSERT INTO item_types (name, effect, value, description) VALUES " +
                    "('Potion', 'heal', 30, 'Restores 30 health points')");
            executeUpdate("INSERT INTO item_types (name, effect, value, description) VALUES " +
                    "('Vision Potion', 'vision', 0, 'Reveals surrounding rooms')");
            executeUpdate("INSERT INTO item_types (name, effect, value, description) VALUES " +
                    "('Bomb', 'damage', 20, 'Deals 20 damage to all enemies in the room')");
        }

        if (isTablePopulated("room_types")) {
            // Populate room types (Empty, Trap, Chest, Monster, Pillar, Escape)
            executeUpdate("INSERT INTO room_types (name, description) VALUES " +
                    "('Empty', 'An empty room with nothing of interest')");
            executeUpdate("INSERT INTO room_types (name, description) VALUES " +
                    "('Trap', 'A room with a dangerous trap that damages the player')");
            executeUpdate("INSERT INTO room_types (name, description) VALUES " +
                    "('Chest', 'A room containing a treasure chest with items or gold')");
            executeUpdate("INSERT INTO room_types (name, description) VALUES " +
                    "('Monster', 'A room with monsters to defeat')");
            executeUpdate("INSERT INTO room_types (name, description) VALUES " +
                    "('Pillar', 'A room containing a magical pillar guarded by an elite monster')");
            executeUpdate("INSERT INTO room_types (name, description) VALUES " +
                    "('Escape', 'The final room where the boss awaits')");
        }

        System.out.println("Initial game data populated successfully");
    }

    /**
     * Checks if a table already has data.
     *
     * @param tableName The name of the table to check
     * @return true if the table has at least one row, false otherwise
     */
    private boolean isTablePopulated(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count") <= 0;
            }
        }
        return true;
    }

    /**
     * Executes a custom SQL update.
     *
     * @param sql The SQL update to execute
     */
    public void executeUpdate(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error executing update: " + e.getMessage());
        }
    }

    /**
     * Executes a custom SQL query.
     *
     * @param sql The SQL query to execute
     * @return ResultSet containing the query results
     */
    public ResultSet executeQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }

        return null;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
