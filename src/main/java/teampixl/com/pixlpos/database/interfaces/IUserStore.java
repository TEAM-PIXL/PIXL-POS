package teampixl.com.pixlpos.database.interfaces;

import teampixl.com.pixlpos.models.Users;
import javafx.collections.ObservableList;

public interface IUserStore {
    ObservableList<Users> readUsers();
    void createUser(Users user);
    void deleteUser(Users user);
    Users getUser(String username);
    void updateUser(Users user);
    void updateUserPassword(Users user, String newPassword);
}


