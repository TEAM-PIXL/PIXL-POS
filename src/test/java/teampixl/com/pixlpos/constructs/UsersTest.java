package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.MetadataWrapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users("john_doe", "password123", "john@example.com", Users.Role.ADMIN);
    }

    @Test
    void testInitialMetadata() {
        MetadataWrapper metadata = user.getMetadata();
        assertNotNull(metadata.metadata().get("id"));
        assertNotNull(metadata.metadata().get("creationTime"));
        assertNotNull(metadata.metadata().get("creationDate"));
        assertEquals(Users.Role.ADMIN, metadata.metadata().get("role"));
        assertFalse((Boolean) metadata.metadata().get("isActive"));
        assertEquals("john_doe", metadata.metadata().get("username"));
    }

    @Test
    void testInitialData() {
        Map<String, Object> data = user.getData();
        assertEquals("john_doe", data.get("username"));
        assertEquals("password123", data.get("password"));
        assertEquals("john@example.com", data.get("email"));
    }

    @Test
    void testUpdateMetadata() {
        user.updateMetadata("role", Users.Role.WAITER);
        user.updateMetadata("isActive", true);

        MetadataWrapper metadata = user.getMetadata();
        assertEquals(Users.Role.WAITER, metadata.metadata().get("role"));
        assertTrue((Boolean) metadata.metadata().get("isActive"));
    }

    @Test
    void testSetDataValue() {
        user.setDataValue("password", "newpassword");
        user.setDataValue("email", "new_email@example.com");

        assertEquals("newpassword", user.getData().get("password"));
        assertEquals("new_email@example.com", user.getData().get("email"));
    }

    @Test
    void testToString() {
        String expected = "Users{Metadata: " + user.getMetadata().toString() +
                ", Data: {email=john@example.com, password=password123, username=john_doe}}";
        assertEquals(expected, user.toString());
    }
}
