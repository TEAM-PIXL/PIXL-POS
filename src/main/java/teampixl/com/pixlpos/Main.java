package teampixl.com.pixlpos;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.models.Stock;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.DatabaseHelper;
import teampixl.com.pixlpos.authentication.PasswordUtils;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

public class Main {

    /*===================================================================================================================================================================================================================================
    Code Description:
    This is the functionality testing class for the application. It tests the functionality of the application by creating test data and testing the functionality of the application.
    The aim is to ensure that the application is functioning as expected and that the data is being stored and retrieved correctly. All the classes and methods are tested in this class.
    ===================================================================================================================================================================================================================================*/

    public static void main(String[] args) {

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section handles the initialization of the database and the creation of tables. It also clears the data from the database.

        Tested Methods:
        - initializeDatabase()
        - clearData()
        - getInstance() {Constructor}
        ===================================================================================================================================================================================================================================*/

        DatabaseHelper.initializeDatabase();
        System.out.println("Database initialized and tables created.");

        DataStore dataStore = DataStore.getInstance();
        dataStore.clearData();

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the Users class. It tests the creation of users, retrieval of users, updating of users, and removal of users. It also tests the hashing and verification of passwords.
        All the methods of the Users class are tested in this section. As well as some of the methods of the AuthenticationManager class.

        Tested Methods:

            Authentication Methods:
                - register()
                - verifyPassword()
                - hashPassword()

            User Methods:
                - getData()
                - getMetadata()
                - getUsers()
                - getUser()
                - setDataValue()
                - updateMetadata()
                - updateUser()
                - updateUserPassword()
                - removeUser()
        ===================================================================================================================================================================================================================================*/

        boolean registerAdmin = AuthenticationManager.register("admin", "admin", "admin", "admin", "admin@example.com", Users.UserRole.ADMIN);
        boolean registerWaiter =  AuthenticationManager.register("waiter", "waiter","waiter", "waiter", "waiter@example.com", Users.UserRole.WAITER);
        boolean registerCook =  AuthenticationManager.register("cook", "cook","cook", "cook", "cook@example.com", Users.UserRole.COOK);

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the MenuItem class. It tests the creation of menu items, retrieval of menu items, updating of menu items, and removal of menu items. It also tests the addition of ingredients to menu items.
        All the methods of the MenuItem class are tested in this section. As well as the methods of the DataStore class that interact with the MenuItem class.

        Tested Methods:
            - MenuItem()
            - getMenuItems()
            - getMenuItem()
            - addMenuItem()
            - setDataValue()
            - updateMetadata()
            - updateMenuItem()
            - removeMenuItem()
            - addMenuItemIngredient()
            - updateMenuItemIngredient()
            - removeMenuItemIngredient()
            - getMenuItemIngredients()
        ===================================================================================================================================================================================================================================*/

        MenuItem item1 = new MenuItem("Pizza", 18.99, MenuItem.ItemType.MAIN, true, "Cheesy pizza with toppings", null);
        MenuItem item2 = new MenuItem("Vegan Salad", 12.99, MenuItem.ItemType.ENTREE, true, "Healthy vegan salad", MenuItem.DietaryRequirement.VEGAN);
        MenuItem item3 = new MenuItem("Fish & Chips", 22.99, MenuItem.ItemType.MAIN, true, "Fresh fish fillet with chips", null);
        MenuItem item4 = new MenuItem("Basil Pasta", 15.99, MenuItem.ItemType.MAIN, true, "Pasta with fresh basil", MenuItem.DietaryRequirement.VEGETARIAN);

        /* Add Hardcoded items to menu items */

        MenuItem item5 = new MenuItem("Classic Cheeseburger", 17.95, MenuItem.ItemType.MAIN, true, "A timeless favorite with juicy beef, melted cheese, and classic toppings.", MenuItem.DietaryRequirement.NONE);
        MenuItem item6 = new MenuItem("BBQ Bacon Cheeseburger", 17.95, MenuItem.ItemType.MAIN, true, "A smoky BBQ twist on the classic cheeseburger with crispy bacon and tangy BBQ sauce.", MenuItem.DietaryRequirement.NONE);
        MenuItem item7 = new MenuItem("Mushroom Swiss Burger", 17.95, MenuItem.ItemType.MAIN, true, "A savory burger with sautéed mushrooms, Swiss cheese, and a touch of garlic aioli.", MenuItem.DietaryRequirement.VEGETARIAN);
        MenuItem item8 = new MenuItem("Spicy Jalapeño Burger", 18.95, MenuItem.ItemType.MAIN, true, "A fiery burger with jalapeños, pepper jack cheese, and a spicy chipotle mayo.", MenuItem.DietaryRequirement.NONE);
        MenuItem item9 = new MenuItem("Hawaiian Pineapple Burger", 18.95, MenuItem.ItemType.MAIN, true, "A tropical burger with grilled pineapple, ham, and a sweet teriyaki glaze.", MenuItem.DietaryRequirement.NONE);
        MenuItem item10 = new MenuItem("Veggie Bean Burger", 20.95, MenuItem.ItemType.MAIN, true, "A hearty vegetarian burger with a blend of beans, grains, and spices.", MenuItem.DietaryRequirement.VEGAN);
        MenuItem item11 = new MenuItem("Beyond Burger", 20.95, MenuItem.ItemType.MAIN, true, "A plant-based burger that looks, cooks, and tastes like beef.", MenuItem.DietaryRequirement.VEGAN);
        MenuItem item12 = new MenuItem("Mediterranean Falafel Burger", 20.95, MenuItem.ItemType.MAIN, true, "A flavorful burger with crispy falafel, hummus, and a tangy tzatziki sauce.", MenuItem.DietaryRequirement.NONE);
        MenuItem item13 = new MenuItem("Teriyaki Salmon Burger", 19.95, MenuItem.ItemType.MAIN, true, "A fresh burger with grilled salmon, avocado, and a sweet teriyaki glaze.", MenuItem.DietaryRequirement.NONE);
        MenuItem item14 = new MenuItem("Breakfast Burger", 19.95, MenuItem.ItemType.MAIN, true, "A hearty burger with a fried egg, crispy bacon, and melted cheese on a toasted bun.", MenuItem.DietaryRequirement.NONE);

        MenuItem item15 = new MenuItem("Coke", 3, MenuItem.ItemType.DRINK, true, "Classic Coca-Cola", MenuItem.DietaryRequirement.NONE);
        MenuItem item16 = new MenuItem("Fanta", 3, MenuItem.ItemType.DRINK, true, "Refreshing Fanta Orange", MenuItem.DietaryRequirement.NONE);
        MenuItem item17 = new MenuItem("Sprite", 3, MenuItem.ItemType.DRINK, true, "Crisp Sprite Lemon-Lime", MenuItem.DietaryRequirement.NONE);
        MenuItem item18 = new MenuItem("Iced Tea", 4.5, MenuItem.ItemType.DRINK, true, "Chilled Iced Tea", MenuItem.DietaryRequirement.NONE);
        MenuItem item19 = new MenuItem("Iced Coffee", 4.5, MenuItem.ItemType.DRINK, true, "Iced Coffee with Cream", MenuItem.DietaryRequirement.NONE);

        /* Finish Hardcoded items to menu items */

        dataStore.createMenuItem(item1);
        dataStore.createMenuItem(item2);
        dataStore.createMenuItem(item3);
        dataStore.createMenuItem(item4);
        dataStore.createMenuItem(item5);
        dataStore.createMenuItem(item6);
        dataStore.createMenuItem(item7);
        dataStore.createMenuItem(item8);
        dataStore.createMenuItem(item9);
        dataStore.createMenuItem(item10);
        dataStore.createMenuItem(item11);
        dataStore.createMenuItem(item12);
        dataStore.createMenuItem(item13);
        dataStore.createMenuItem(item14);
        dataStore.createMenuItem(item15);
        dataStore.createMenuItem(item16);
        dataStore.createMenuItem(item17);
        dataStore.createMenuItem(item18);
        dataStore.createMenuItem(item19);

        System.out.println("Menu items added to the database:");

    }
}







