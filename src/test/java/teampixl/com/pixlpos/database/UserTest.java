package teampixl.com.pixlpos.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.authentication.PasswordUtils;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")
class UserTest {

    private DataStore dataStore;
    private PasswordUtils passwordUtils;

    @BeforeEach
    public void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.clearData();
        passwordUtils = new PasswordUtils();
    }
}