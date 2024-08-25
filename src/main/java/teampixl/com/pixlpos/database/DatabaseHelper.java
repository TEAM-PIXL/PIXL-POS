package teampixl.com.pixlpos.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DB_URL;

    static {
        // Specify the path to the SQLite database file within the resources directory
        DB_URL = "jdbc:sqlite:" + getDatabaseFilePath();
    }

    private static String getDatabaseFilePath() {
        // Get the absolute path to the database file in the resources directory
        File resourceDir = new File("src/main/resources/teampixl/com/pixlpos/database");
        File dbFile = new File(resourceDir, "pixlpos.db");

        return dbFile.getAbsolutePath();
    }

    // Connect to SQLite database
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Initialize the database and create the tables
    public static void initializeDatabase() {
        String sqlCreateUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                password TEXT NOT NULL,
                email TEXT,
                role TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
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
                total REAL NOT NULL,
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

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreateUsersTable);
            stmt.execute(sqlCreateMenuItemsTable);
            stmt.execute(sqlCreateOrdersTable);
            stmt.execute(sqlCreateOrderItemsTable);
            System.out.println("Database initialized and tables created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


