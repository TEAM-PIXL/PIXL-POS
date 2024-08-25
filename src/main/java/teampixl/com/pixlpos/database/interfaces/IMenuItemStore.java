package teampixl.com.pixlpos.database.interfaces;


import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.MenuItem;

public interface IMenuItemStore {
    ObservableList<MenuItem> getMenuItems();

    void addMenuItem(MenuItem item);

    void removeMenuItem(MenuItem item);

    MenuItem getMenuItem(String itemName);

    void updateMenuItem(MenuItem item);
}


