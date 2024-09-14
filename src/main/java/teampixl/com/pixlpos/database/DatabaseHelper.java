package teampixl.com.pixlpos.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class provides methods to establish a connection to the SQLite database and initialize the database.
 */
public class DatabaseHelper {

    /*============================================================================================================================================================
    Code Description:
    - DB_URL: This is the URL to the SQLite database file in globally.
    - getDatabaseFilePath(): This method returns the path to the SQLite database file.
    - connect(): This method establishes a connection to the SQLite database.
    ============================================================================================================================================================*/

    private static final String DB_URL;

    static {
        DB_URL = "jdbc:sqlite:" + getDatabaseFilePath();
    }

    private static String getDatabaseFilePath() {
        File resourceDir = new File("src/main/resources/teampixl/com/pixlpos/database");
        File dbFile = new File(resourceDir, "pixlpos.db");

        return dbFile.getAbsolutePath();
    }

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return Connection object
     */
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

    /*============================================================================================================================================================
    Code Description:
    - initializeDatabase(): This method initializes the SQLite database and creates the tables if they do not exist.

    Tables:
    - users: This table stores the user information.
    - menu_items: This table stores the menu items.
    - orders: This table stores the order information.
    - ingredients: This table stores the ingredient information.
    - menu_item_ingredients: This table stores the relationship between menu items and ingredients.
    ============================================================================================================================================================*/

    /**
     * Initializes the SQLite database and creates the tables if they do not exist.
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
            is_active INTEGER NOT NULL DEFAULT 1
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
            stmt.execute(sqlCreateUsersTable);
            stmt.execute(sqlCreateMenuItemsTable);
            stmt.execute(sqlCreateOrdersTable);
            stmt.execute(sqlCreateOrderItemsTable);
            stmt.execute(sqlCreateIngredientsTable);
            stmt.execute(sqlCreateStockTable);
            stmt.execute(sqlCreateMenuItemIngredientsTable);
            System.out.println("Database initialized and tables created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}



