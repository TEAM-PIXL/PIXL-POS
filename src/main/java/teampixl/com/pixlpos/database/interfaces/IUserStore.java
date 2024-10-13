package teampixl.com.pixlpos.database.interfaces;

import teampixl.com.pixlpos.models.Users;
import javafx.collections.ObservableList;

/**
 * The IUserStore interface is used to define the methods that will be used to interact with the Users table in the database.
 */
public interface IUserStore {

    /**
     * The readUsers method is used to read all the users from the Users table in the database.
     * @return ObservableList<Users> - The list of users in the Users table.
     */
    ObservableList<Users> readUsers();

    /**
     * The createUser method is used to create a new user in the Users table in the database.
     * @param user - The user to be created.
     */
    void createUser(Users user);

    /**
     * The deleteUser method is used to delete a user from the Users table in the database.
     * @param user - The user to be deleted.
     */
    void deleteUser(Users user);

    /**
     * The updateUser method is used to update a user in the Users table in the database.
     * @param user - The user to be updated.
     */
    void updateUser(Users user);
}


