package teampixl.com.pixlpos;

import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.DatabaseHelper;

public class Main {
    public static void main(String[] args) {
        // Initialize the DataStore and database
        DatabaseHelper.initializeDatabase();
        DataStore dataStore = DataStore.getInstance();

        // Add some sample data
        MenuItem item = new MenuItem("Grilled Salmon", 22.50, MenuItem.ItemType.MAIN, true, "Delicious grilled salmon with herbs", MenuItem.DietaryRequirement.GLUTEN_FREE);
        dataStore.addMenuItem(item);

        // Retrieve the item by its ID
        MenuItem retrievedItem = dataStore.getMenuItemById((String) item.getMetadata().metadata().get("id"));
        System.out.println("Retrieved MenuItem: " + retrievedItem);
    }
}
