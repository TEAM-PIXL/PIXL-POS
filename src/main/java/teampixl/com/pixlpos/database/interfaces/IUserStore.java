package teampixl.com.pixlpos.database.interfaces;

import teampixl.com.pixlpos.constructs.Users;
import javafx.collections.ObservableList;

public interface IUserStore {
    ObservableList<Users> getUsers();
    void addUser(Users user);
    void removeUser(Users user);
    Users getUser(String username);
    void updateUser(Users user);
    void updateUserPassword(Users user, String newPassword);
}


