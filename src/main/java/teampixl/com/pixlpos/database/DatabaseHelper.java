package teampixl.com.pixlpos.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName());

    private static final String URL = "jdbc:sqlserver://pixlpos.database.windows.net:1433;database=pixlpos";
    private static final String USER = "pixldb_maindb";
    private static final String PASSWORD = "1j<9iS2q*tpc5B%4mK*]";

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return Connection object
     */
    public static Connection connect() {
        LOGGER.info("Attempting to establish a database connection...");
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error connecting to database: " + e.getMessage(), e);
            return null;
        }
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
     * Initializes the Azure SQL database and creates the tables if they do not exist.
     */
    public static void initializeDatabase() {
        String sqlCreateUsersTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
                CREATE TABLE users (
                    id VARCHAR(255) PRIMARY KEY,
                    first_name VARCHAR(255) NOT NULL,
                    last_name VARCHAR(255) NOT NULL,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255),
                    role VARCHAR(255) NOT NULL,
                    created_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    updated_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    is_active INT NOT NULL,
                    additional_info TEXT
                );
                """;

        String sqlCreateLoginTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_logs')
                CREATE TABLE user_logs (
                    id VARCHAR(255) PRIMARY KEY,
                    user_id VARCHAR(255) NOT NULL,
                    log_type VARCHAR(255) NOT NULL,
                    created_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        String sqlCreateLogsTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'global_logs')
                CREATE TABLE global_logs (
                    log_id VARCHAR(255) PRIMARY KEY,
                    user_id VARCHAR(255) NOT NULL,
                    log_timestamp BIGINT NOT NULL,
                    log_action VARCHAR(255) NOT NULL,
                    log_status VARCHAR(255) NOT NULL,
                    log_type VARCHAR(255) NOT NULL,
                    log_category VARCHAR(255) NOT NULL,
                    log_priority VARCHAR(255) NOT NULL,
                    log_description VARCHAR(255) NOT NULL,
                    log_location VARCHAR(255),
                    log_device VARCHAR(255),
                    log_ip VARCHAR(255),
                    log_mac VARCHAR(255),
                    log_os VARCHAR(255),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        String sqlCreateSettingsTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_settings')
                CREATE TABLE user_settings (
                    user_id VARCHAR(255) PRIMARY KEY,
                    theme VARCHAR(255),
                    resolution VARCHAR(255),
                    currency VARCHAR(255),
                    timezone VARCHAR(255),
                    language VARCHAR(255),
                    access_level VARCHAR(255),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        String sqlCreateGlobalNotesTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'global_notes')
                CREATE TABLE global_notes (
                    note_id VARCHAR(255) PRIMARY KEY,
                    user_id VARCHAR(255) NOT NULL,
                    note_title VARCHAR(255) NOT NULL,
                    note_content TEXT NOT NULL,
                    timestamp BIGINT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        String sqlCreateMenuItemsTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'menu_items')
                CREATE TABLE menu_items (
                    id VARCHAR(255) PRIMARY KEY,
                    item_name VARCHAR(255) NOT NULL,
                    price REAL NOT NULL,
                    item_type VARCHAR(255) NOT NULL,
                    active_item INT NOT NULL,
                    dietary_requirement VARCHAR(255),
                    description TEXT NOT NULL,
                    notes TEXT,
                    amount_ordered INT NOT NULL DEFAULT 0,
                    created_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    updated_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE()))
                );
                """;

        String sqlCreateOrdersTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'orders')
                CREATE TABLE orders (
                    order_id VARCHAR(255) PRIMARY KEY,
                    order_number INT NOT NULL,
                    user_id VARCHAR(255) NOT NULL,
                    order_status VARCHAR(255) NOT NULL,
                    is_completed INT NOT NULL DEFAULT 0,
                    order_type VARCHAR(255) NOT NULL,
                    table_number INT,
                    customers INT,
                    total REAL NOT NULL,
                    special_requests TEXT,
                    payment_method VARCHAR(255) NOT NULL,
                    created_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    updated_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        String sqlCreateOrderItemsTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'order_items')
                CREATE TABLE order_items (
                    order_id VARCHAR(255) NOT NULL,
                    menu_item_id VARCHAR(255) NOT NULL,
                    quantity INT NOT NULL,
                    PRIMARY KEY (order_id, menu_item_id),
                    FOREIGN KEY (order_id) REFERENCES orders(order_id),
                    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
                );
                """;

        String sqlCreateIngredientsTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ingredients')
                CREATE TABLE ingredients (
                    ingredient_id VARCHAR(255) PRIMARY KEY,
                    item_name VARCHAR(255) NOT NULL,
                    notes TEXT
                );
                """;

        String sqlCreateStockTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'stock')
                CREATE TABLE stock (
                    stock_id VARCHAR(255) PRIMARY KEY,
                    ingredient_id VARCHAR(255) NOT NULL,
                    stock_status VARCHAR(255) NOT NULL,
                    on_order INT NOT NULL,
                    created_at BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    last_updated BIGINT DEFAULT (DATEDIFF_BIG(SECOND, '19700101', GETUTCDATE())),
                    unit_type VARCHAR(255) NOT NULL,
                    numeral REAL NOT NULL,
                    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
                );
                """;

        String sqlCreateMenuItemIngredientsTable = """
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'menu_item_ingredients')
                CREATE TABLE menu_item_ingredients (
                    menu_item_id VARCHAR(255) NOT NULL,
                    ingredient_id VARCHAR(255) NOT NULL,
                    numeral REAL NOT NULL,
                    PRIMARY KEY (menu_item_id, ingredient_id),
                    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
                    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
                );
                """;
    }

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreateUsersTable);
            stmt.execute(sqlCreateLoginTable);
            stmt.execute(sqlCreateLogsTable);
            stmt.execute(sqlCreateSettingsTable);
            stmt.execute(sqlCreateGlobalNotesTable);
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



