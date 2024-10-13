package teampixl.com.pixlpos.database;

import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods to establish a connection to the SQLite database and initialize the database.
 */
public class DatabaseHelper {

    private static final Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName());

    private static final String DB_URL;

    static {
        DB_URL = "jdbc:sqlite:" + getDatabaseFilePath();
    }

    private static String getDatabaseFilePath() {
        File resourceDir = new File("src/main/resources/teampixl/com/pixlpos/database");
        File dbFile = new File(resourceDir, "pixlpos.db");

        return dbFile.getAbsolutePath();
    }

    private static final HikariDataSource dataSource = new HikariDataSource();

    private static void logSql(String sql) {
        LOGGER.log(Level.INFO, "Executing SQL: {0}", sql);
    }

    static {
        dataSource.setJdbcUrl(DB_URL);
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection connect() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Initializes the SQLite database and creates the tables if they do not exist.
     * <p>
     * Tables:
     * - users
     * - user_logs
     * - global_logs
     * - user_settings
     * - global_notes
     * - menu_items
     * - orders
     * - order_items
     * - ingredients
     * - stock
     * - menu_item_ingredients
     * <p>
     * The method logs the SQL statements executed to create the tables.
     */
    public static void initializeDatabase() {
        String sqlCreateUsersTable = """
        CREATE TABLE IF NOT EXISTS users (
            id TEXT PRIMARY KEY,
            first_name TEXT NOT NULL,
            last_name TEXT NOT NULL,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            email TEXT,
            role TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            is_active INT NOT NULL,
            additional_info TEXT
        );
    """;

        String sqlCreateLoginTable = """
        CREATE TABLE IF NOT EXISTS user_logs (
            id TEXT PRIMARY KEY,
            user_id TEXT NOT NULL,
            log_type TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );
    """;

        String sqlCreateLogsTable = """
        CREATE TABLE IF NOT EXISTS global_logs (
            log_id TEXT PRIMARY KEY,
            user_id TEXT NOT NULL,
            log_timestamp INTEGER NOT NULL,
            log_action TEXT NOT NULL,
            log_status TEXT NOT NULL,
            log_type TEXT NOT NULL,
            log_category TEXT NOT NULL,
            log_priority TEXT NOT NULL,
            log_description TEXT NOT NULL,
            log_location TEXT,
            log_device TEXT,
            log_ip TEXT,
            log_mac TEXT,
            log_os TEXT,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );
        """;

        String sqlCreateSettingsTable = """
        CREATE TABLE IF NOT EXISTS user_settings (
            user_id TEXT PRIMARY KEY,
            theme TEXT,
            resolution TEXT,
            currency TEXT,
            timezone TEXT,
            language TEXT,
            access_level TEXT,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );
    """;

        String sqlCreateGlobalNotesTable = """
        CREATE TABLE IF NOT EXISTS global_notes (
            note_id TEXT PRIMARY KEY,
            user_id TEXT NOT NULL,
            note_title TEXT NOT NULL,
            note_content TEXT NOT NULL,
            timestamp INTEGER NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );
    """;

        String sqlCreateMenuItemsTable = """
        CREATE TABLE IF NOT EXISTS menu_items (
            id TEXT PRIMARY KEY,
            item_name TEXT NOT NULL,
            price REAL NOT NULL,
            item_type TEXT NOT NULL,
            active_item INTEGER NOT NULL,
            dietary_requirement TEXT,
            description TEXT NOT NULL,
            notes TEXT,
            amount_ordered INTEGER NOT NULL DEFAULT 0,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    """;

        String sqlCreateOrdersTable = """
        CREATE TABLE IF NOT EXISTS orders (
            order_id TEXT PRIMARY KEY,
            order_number INTEGER NOT NULL,
            user_id TEXT NOT NULL,
            order_status TEXT NOT NULL,
            is_completed INTEGER NOT NULL DEFAULT 0,
            order_type TEXT NOT NULL,
            table_number INTEGER,
            customers INTEGER,
            total REAL NOT NULL,
            special_requests TEXT,
            payment_method TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );
    """;

        String sqlCreateOrderItemsTable = """
        CREATE TABLE IF NOT EXISTS order_items (
            order_id TEXT NOT NULL,
            menu_item_id TEXT NOT NULL,
            quantity INTEGER NOT NULL,
            PRIMARY KEY (order_id, menu_item_id),
            FOREIGN KEY (order_id) REFERENCES orders(order_id),
            FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
        );
    """;

        String sqlCreateIngredientsTable = """
    CREATE TABLE IF NOT EXISTS ingredients (
        ingredient_id TEXT PRIMARY KEY,
        item_name TEXT NOT NULL,
        notes TEXT
    );
    """;

        String sqlCreateStockTable = """
    CREATE TABLE IF NOT EXISTS stock (
        stock_id TEXT PRIMARY KEY,
        ingredient_id TEXT NOT NULL,
        stock_status TEXT NOT NULL,
        on_order INTEGER NOT NULL,
        created_at TEXT NOT NULL,
        last_updated TEXT NOT NULL,
        unit_type TEXT NOT NULL,
        numeral REAL NOT NULL,
        desired_quantity REAL NOT NULL,
        price_per_unit REAL NOT NULL,
        low_stock_threshold REAL NOT NULL,
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
    );
    """;

        String sqlCreateMenuItemIngredientsTable = """
    CREATE TABLE IF NOT EXISTS menu_item_ingredients (
        menu_item_id TEXT NOT NULL,
        ingredient_id TEXT NOT NULL,
        numeral REAL NOT NULL,
        PRIMARY KEY (menu_item_id, ingredient_id),
        FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
    );
    """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            logSql(sqlCreateUsersTable);
            stmt.execute(sqlCreateUsersTable);

            logSql(sqlCreateLoginTable);
            stmt.execute(sqlCreateLoginTable);

            logSql(sqlCreateLogsTable);
            stmt.execute(sqlCreateLogsTable);

            logSql(sqlCreateSettingsTable);
            stmt.execute(sqlCreateSettingsTable);

            logSql(sqlCreateGlobalNotesTable);
            stmt.execute(sqlCreateGlobalNotesTable);

            logSql(sqlCreateMenuItemsTable);
            stmt.execute(sqlCreateMenuItemsTable);

            logSql(sqlCreateOrdersTable);
            stmt.execute(sqlCreateOrdersTable);

            logSql(sqlCreateOrderItemsTable);
            stmt.execute(sqlCreateOrderItemsTable);

            logSql(sqlCreateIngredientsTable);
            stmt.execute(sqlCreateIngredientsTable);

            logSql(sqlCreateStockTable);
            stmt.execute(sqlCreateStockTable);

            logSql(sqlCreateMenuItemIngredientsTable);
            stmt.execute(sqlCreateMenuItemIngredientsTable);

            System.out.println("Database initialized and tables created.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database initialization error: {0}", e.getMessage());
        }
    }
}