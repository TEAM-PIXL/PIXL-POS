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
        DB_URL = "jdbc:sqlite:C:/Users/parke/OneDrive/Documents/SoftwareEngineering/Semester2-2024/CAB302/PIXL-POS/src/main/resources/teampixl/com/pixlpos/database/pixlpos.db";
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
        String sqlCreateMenuTable = """
            CREATE TABLE IF NOT EXISTS menu_items (
                id TEXT PRIMARY KEY,
                item_name TEXT NOT NULL,
                price REAL NOT NULL,
                item_type TEXT NOT NULL,
                active_item INTEGER NOT NULL,
                dietary_requirement TEXT,
                description TEXT NOT NULL,
                notes TEXT,
                amount_ordered INTEGER NOT NULL DEFAULT 0
            );
            """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Create the menu_items table
            stmt.execute(sqlCreateMenuTable);
            System.out.println("MenuItem table created or already exists.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

