package teampixl.com.pixlpos;

import teampixl.com.pixlpos.database.api.IngredientsAPI;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.StockAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Stock;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.DatabaseHelper;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int MAX_RETRIES = 3;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        DatabaseHelper.initializeDatabase();
        System.out.println("Database initialized and tables created.");

        DataStore dataStore = DataStore.getInstance();
        dataStore.clearData();

        AuthenticationManager.register("Jon", "Doe", "admin", "admin", "admin@example.com", Users.UserRole.ADMIN);
        AuthenticationManager.register("waiter", "waiter","waiter", "waiter", "waiter@example.com", Users.UserRole.WAITER);
        AuthenticationManager.register("cook", "cook","cook", "cook", "cook@example.com", Users.UserRole.COOK);

        MenuAPI menuAPI = MenuAPI.getInstance();
        IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
        StockAPI stockAPI = StockAPI.getInstance();

        scheduler.schedule(() -> batchProcessMenuItems(menuAPI), 0, TimeUnit.SECONDS);
        scheduler.schedule(() -> batchProcessIngredients(ingredientsAPI), 5, TimeUnit.SECONDS);
        scheduler.schedule(() -> batchProcessStock(stockAPI, ingredientsAPI), 10, TimeUnit.SECONDS);

        scheduler.shutdown();
    }

    private static void batchProcessMenuItems(MenuAPI menuAPI) {
        List<StatusCode> menuItemCodes = new ArrayList<>();
        String[][] menuItems = {
                {"Pizza", "18.99", "Cheesy pizza with toppings", "MAIN", "NONE"},
                {"Vegan Salad", "12.99", "Healthy vegan salad", "ENTREE", "VEGAN"},
                {"Fish and Chips", "22.99", "Fresh fish fillet with chips", "MAIN", "NONE"},
                {"Basil Pasta", "15.99", "Pasta with fresh basil", "MAIN", "VEGETARIAN"},
                {"Classic Cheeseburger", "17.95", "A timeless favorite with juicy beef, melted cheese, and classic toppings.", "MAIN", "NONE"},
                {"BBQ Bacon Cheeseburger", "17.95", "A smoky BBQ twist on the classic cheeseburger with crispy bacon and tangy BBQ sauce.", "MAIN", "NONE"},
                {"Mushroom Swiss Burger", "17.95", "A savory burger with sautéed mushrooms, Swiss cheese, and a touch of garlic aioli.", "MAIN", "VEGETARIAN"},
                {"Spicy Jalapeño Burger", "18.95", "A fiery burger with jalapeños, pepper jack cheese, and a spicy chipotle mayo.", "MAIN", "NONE"},
                {"Hawaiian Pineapple Burger", "18.95", "A tropical burger with grilled pineapple, ham, and a sweet teriyaki glaze.", "MAIN", "NONE"},
                {"Veggie Bean Burger", "20.95", "A hearty vegetarian burger with a blend of beans, grains, and spices.", "MAIN", "VEGAN"},
                {"Beyond Burger", "20.95", "A plant-based burger that looks, cooks, and tastes like beef.", "MAIN", "VEGAN"},
                {"Mediterranean Falafel Burger", "20.95", "A flavorful burger with crispy falafel, hummus, and a tangy tzatziki sauce.", "MAIN", "NONE"},
                {"Teriyaki Salmon Burger", "19.95", "A fresh burger with grilled salmon, avocado, and a sweet teriyaki glaze.", "MAIN", "NONE"},
                {"Breakfast Burger", "19.95", "A hearty burger with a fried egg, crispy bacon, and melted cheese on a toasted bun.", "MAIN", "NONE"},
                {"Coke", "3.0", "Classic Coca-Cola", "DRINK", "NONE"},
                {"Fanta", "3.0", "Refreshing Fanta Orange", "DRINK", "NONE"},
                {"Sprite", "3.0", "Crisp Sprite Lemon-Lime", "DRINK", "NONE"},
                {"Iced Tea", "4.5", "Chilled Iced Tea", "DRINK", "NONE"},
                {"Iced Coffee", "4.5", "Iced Coffee with Cream", "DRINK", "NONE"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            double price = Double.parseDouble(item[1]);
            String description = item[2];
            MenuItem.ItemType itemType = MenuItem.ItemType.valueOf(item[3]);
            MenuItem.DietaryRequirement dietaryRequirement = MenuItem.DietaryRequirement.valueOf(item[4]);

            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                List<StatusCode> result = menuAPI.postMenuItem(name, price, true, itemType, description, "", dietaryRequirement);
                if (Exceptions.isSuccessful(result)) {
                    menuItemCodes.addAll(result);
                    break;
                } else {
                    retryCount++;
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (!Exceptions.isSuccessful(menuItemCodes)) {
            System.out.println(Exceptions.returnStatus("Menu items could not be created with the following statuses:", menuItemCodes));
        } else {
            System.out.println("Menu items created successfully.");
        }
    }

    private static void batchProcessIngredients(IngredientsAPI ingredientsAPI) {
        List<StatusCode> ingredientCodes = new ArrayList<>();
        String[][] ingredients = {
                {"Flour", "Flour for pizza dough"},
                {"Tomato Sauce", "Tomato sauce for pizza"},
                {"Beef", "Beef for burgers"},
                {"Cheese", "Cheese for burgers"}
        };

        for (String[] ingredient : ingredients) {
            String name = ingredient[0];
            String description = ingredient[1];

            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                List<StatusCode> result = ingredientsAPI.postIngredient(name, description);
                if (Exceptions.isSuccessful(result)) {
                    ingredientCodes.addAll(result);
                    break;
                } else {
                    retryCount++;
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (!Exceptions.isSuccessful(ingredientCodes)) {
            System.out.println(Exceptions.returnStatus("Ingredients could not be created with the following statuses:", ingredientCodes));
        } else {
            System.out.println("Ingredients created successfully.");
        }
    }

    private static void batchProcessStock(StockAPI stockAPI, IngredientsAPI ingredientsAPI) {
        List<StatusCode> stockCodes = new ArrayList<>();
        Object[][] stockItems = {
                {"Flour", Stock.StockStatus.INSTOCK, Stock.UnitType.KG, 150.0, false},
                {"Tomato Sauce", Stock.StockStatus.LOWSTOCK, Stock.UnitType.L, 2.0, false},
                {"Beef", Stock.StockStatus.INSTOCK, Stock.UnitType.KG, 130.0, false},
                {"Cheese", Stock.StockStatus.NOSTOCK, Stock.UnitType.KG, 0.0, true}
        };

        for (Object[] stockItem : stockItems) {
            String ingredientName = (String) stockItem[0];
            Stock.StockStatus stockStatus = (Stock.StockStatus) stockItem[1];
            Stock.UnitType unitType = (Stock.UnitType) stockItem[2];
            double quantity = (double) stockItem[3];
            boolean needsRestocking = (boolean) stockItem[4];

            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                List<StatusCode> result = stockAPI.postStock(ingredientsAPI.keySearch(ingredientName), stockStatus, unitType, quantity, needsRestocking);
                if (Exceptions.isSuccessful(result)) {
                    stockCodes.addAll(result);
                    break;
                } else {
                    retryCount++;
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (!Exceptions.isSuccessful(stockCodes)) {
            System.out.println(Exceptions.returnStatus("Stock could not be created with the following statuses:", stockCodes));
        } else {
            System.out.println("Stock created successfully.");
        }
    }
}







