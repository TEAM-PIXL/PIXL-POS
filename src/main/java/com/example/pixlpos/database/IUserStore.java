package com.example.pixlpos.database;

import com.example.pixlpos.constructs.Users;
import javafx.collections.ObservableList;

public interface IUserStore {
    ObservableList<Users> getUsers();
    void addUser(Users user);
    void removeUser(Users user);
    Users getUser(String username);
    void updateUser(Users user);
    void clearUsers();
}