package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;

import javafx.scene.layout.HBox;

public class AdminScreenController {
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the admin screen of the application.
    ====================================================================================================================================================================================*/

    @FXML
    private TabPane root;

    @FXML
    private Button exitButton;

    @FXML
    private Button exitMenuItemButton;

    @FXML
    private Button newUserButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button submitChangesButton;

    @FXML
    private Button customiseButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private ChoiceBox<Users.UserRole> roleField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField priceField;

    @FXML
    private ChoiceBox<MenuItem.ItemType> itemTypeField;

    @FXML
    private ChoiceBox<MenuItem.DietaryRequirement> dietaryRequirementsField;

    @FXML
    private TextArea itemDescriptionArea;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private TextField searchField;

    @FXML
    private TextField searchFieldMenuItem;

    private DataStore dataStore;

    private Users loadedUser;

    private MenuItem loadedMenuItem;

    @FXML
    private GridPane userTable;

    @FXML
    private GridPane menuTable;

    private HBox currentlyHighlightedRow;



    @FXML
    public void initialize() {
        // Initialization code here
        dataStore = DataStore.getInstance();
        //User Management Side
        onCancelButtonClick();
        populateUserGrid();
        roleField.getItems().clear();
        roleField.getItems().addAll(new HashSet<>(Arrays.asList(Users.UserRole.values())));
        loadedUser = null;
        //Menu Item Management side
        onMenuItemCancelButtonClick();
        populateMenuGrid();
        itemTypeField.getItems().clear();
        itemTypeField.getItems().addAll(MenuItem.ItemType.values());
        dietaryRequirementsField.getItems().clear();
        dietaryRequirementsField.getItems().addAll(MenuItem.DietaryRequirement.values());
        loadedMenuItem = null;

    }

/*===================================================================================================================================================================================
User Management:
Methods for user management from here.
====================================================================================================================================================================================*/

    @FXML
    protected void onExitButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) exitButton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, exitButton);
    }

    @FXML
    protected void onNewUserButtonClick() {
        // Handle new user button click
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            Users.UserRole role = roleField.getSelectionModel().getSelectedItem();

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "All fields are required");
            } else {
            if (dataStore.getUser(username) == null) {
                boolean registerUser = AuthenticationManager.register(firstName, lastName, username, password, email, role);
                if (registerUser) {
                    initialize();
                    showAlert(Alert.AlertType.CONFIRMATION, "New User", "New User has been created");
                } else {
                    showAlert(Alert.AlertType.ERROR, "New User", "Registration Failed");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "New User", "User already exists");
            }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "New User", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        // Handle delete user button click
        try{
            if (loadedUser != null) {
                dataStore.removeUser(loadedUser);
                initialize();
                showAlert(Alert.AlertType.CONFIRMATION, "Deleted User", "User has been deleted");
            }
            else{
                showAlert(Alert.AlertType.ERROR, "Deleted User", "Select a user to delete");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Deleted User", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onSubmitChangesButtonClick() {
        // Handle submit changes button click
        if (loadedUser == null) {
            showAlert(Alert.AlertType.ERROR, "New User", "Please select a user");
            return;
        }
        try{
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            Users.UserRole role = roleField.getSelectionModel().getSelectedItem();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "All fields are required");
            }else {


                try {
                    loadedUser.updateMetadata("username", username);
                    loadedUser.updateMetadata("role", role);
                    loadedUser.setDataValue("password", password);
                    loadedUser.setDataValue("email", email);
                    loadedUser.updateMetadata("updated_at", System.currentTimeMillis());
                    dataStore.updateUser(loadedUser);
                    showAlert(Alert.AlertType.CONFIRMATION, "Updated User", "User has been updated");
                    initialize();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Updated User", "Unexpected error occured while updating user: " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Empty Field", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onEditButtonClick() {
        // Handle customise button click
        try{
            if (loadedUser == null) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Please select a user from the table");
            } else{
                populateUserParam(loadedUser);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onSearchButtonClick() {
        // Handle search button click
        String searchInput = searchField.getText();
        if (searchInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please specify a User");
        }else {
            try {
                loadedUser = dataStore.getUser(searchInput);
                if (loadedUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "User not found");
                }else {
                    populateUserParam(loadedUser);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected Error: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void onClearButtonClick() {
        // Handle clear button click
        searchField.clear();
    }

    @FXML
    protected void onCancelButtonClick() {
        // Handle clear button click
        firstNameField.clear();
        lastNameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        roleField.setValue(null);
        loadedUser = null;
    }

    private void populateUserGrid() {
        int row = 0;

        ObservableList<Users> listOfUsers = dataStore.getUsers();
        userTable.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null);

        for (Users user : listOfUsers) {
            // Uncomment when implementing first and last name columns:
            // Label firstNameLabel = new Label(user.getMetadata().metadata().get("first_name").toString());
            // Label lastNameLabel = new Label(user.getMetadata().metadata().get("last_name").toString());
            String readableDate = toReadableDate(user.getMetadata().metadata().get("created_at").toString());
            Label usernameLabel = new Label(user.getMetadata().metadata().get("username").toString());
            Label userSinceLabel = new Label(readableDate);
            Label roleLabel = new Label(user.getMetadata().metadata().get("role").toString());
            String fullName = user.getMetadata().metadata().get("first_name").toString() + " " + user.getMetadata().metadata().get("last_name").toString();
            Label fullNameLabel = new Label(fullName);

            HBox rowContainer = new HBox(10);
            rowContainer.setAlignment(Pos.CENTER_LEFT);
            rowContainer.setOnMouseClicked(event -> {loadedUser = user; rowHighlight(rowContainer);});
            userTable.add(fullNameLabel, 0, row);
            userTable.add(usernameLabel,1, row);
            userTable.add(userSinceLabel,2, row);
            userTable.add(roleLabel,3, row);

            userTable.add(rowContainer, 0, row);
            GridPane.setColumnSpan(rowContainer, userTable.getColumnCount());
            row++;

        }
    }

    private String toReadableDate(String dateString){

        long createAt = Long.parseLong(dateString);

        LocalDateTime createdAtDateTime = Instant.ofEpochMilli(createAt).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return createdAtDateTime.format(formatter);
    }

    private void populateUserParam(Users User) {
        Object username = User.getMetadata().metadata().get("username");
        Object password = User.getData().get("password");
        Object email = User.getData().get("email");
        Object role = User.getMetadata().metadata().get("role");
        Object fistName = User.getMetadata().metadata().get("first_name");
        Object lastName = User.getMetadata().metadata().get("last_name");

        firstNameField.setText(fistName.toString());
        lastNameField.setText(lastName.toString());
        usernameField.setText(username.toString());
        passwordField.setText(password.toString());
        emailField.setText(email.toString());
        roleField.setValue(Users.UserRole.valueOf(role.toString()));
        loadedUser = User;
    }


/*===================================================================================================================================================================================
    Menu Management Methods:
    Methods for handling menu management from here
====================================================================================================================================================================================*/

    private void populateMenuGrid() {
        int row = 0;

        ObservableList<MenuItem> listOfMenuItems = dataStore.getMenuItems();
        menuTable.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null);

        for (MenuItem menuItem : listOfMenuItems) {
            Label priceLabel = new Label(menuItem.getMetadata().metadata().get("price").toString());
            Label menuItemNameLabel = new Label(menuItem.getMetadata().metadata().get("itemName").toString());
            Label itemTypeLabel = new Label(menuItem.getMetadata().metadata().get("itemType").toString());
            try {
                Label dietReq = new Label(menuItem.getMetadata().metadata().get("dietaryRequirement").toString());
                menuTable.add(dietReq,3, row);
            } catch (Exception e) {
                System.out.println(("No Dietary Requirement"));
            }

            HBox rowContainerMenu = new HBox(10);
            rowContainerMenu.setAlignment(Pos.CENTER_LEFT);
            rowContainerMenu.setOnMouseClicked(event -> {loadedMenuItem = menuItem; rowHighlight(rowContainerMenu);});

            menuTable.add(menuItemNameLabel, 0, row);
            menuTable.add(priceLabel,1, row);
            menuTable.add(itemTypeLabel,2, row);

            GridPane.setColumnSpan(rowContainerMenu, menuTable.getColumnCount());
            menuTable.add(rowContainerMenu, 0, row);
            row++;

        }
    }

    private void populateMenuItemParam(MenuItem menuItem) {
        Object price = menuItem.getMetadata().metadata().get("price");
        Object itemName = menuItem.getMetadata().metadata().get("itemName");
        Object itemType = menuItem.getMetadata().metadata().get("itemType");
        priceField.setText(Double.toString((double) price));
        itemNameField.setText(itemName.toString());
        itemTypeField.setValue(MenuItem.ItemType.valueOf(itemType.toString()));
        try {
            Object dietaryRequirement = menuItem.getMetadata().metadata().get("dietaryRequirement");
            dietaryRequirementsField.setValue(MenuItem.DietaryRequirement.valueOf(dietaryRequirement.toString()));
        } catch (Exception e) {
            System.out.println(("No Dietary Requirement"));
        }
        try {
            Object description = menuItem.getData().get("description");
            itemDescriptionArea.setText(description.toString());
        } catch (Exception e) {
            System.out.println(("No Description"));
        }
        loadedMenuItem = menuItem;
    }

    @FXML
    protected void onMenuItemSearchButtonClick() {
        // Handle search button click
        String searchInput = searchFieldMenuItem.getText();
        if (searchInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please specify a Menu Item");
        }else {
            try {
                loadedMenuItem = dataStore.getMenuItem(searchInput);
                if (loadedMenuItem == null) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Menu Item not found");
                }else {
                    populateMenuItemParam(loadedMenuItem);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected Error: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void onMenuItemClearButtonClick() {
        // Handle clear button click
        searchFieldMenuItem.clear();
    }

    @FXML
    protected void onMenuItemCancelButtonClick() {
        // Handle clear button click
        priceField.clear();
        itemNameField.clear();
        itemTypeField.setValue(null);
        dietaryRequirementsField.setValue(null);
        itemDescriptionArea.clear();
        loadedMenuItem = null;
    }

    @FXML
    protected void onNewMenuItemButtonClick() {
        // Handle new user button click
        try {
            Double price;
            String itemName = itemNameField.getText();
            MenuItem.ItemType itemType = itemTypeField.getSelectionModel().getSelectedItem();
            MenuItem.DietaryRequirement dietaryRequirement = dietaryRequirementsField.getValue();
            String description = itemDescriptionArea.getText();

            if (itemName.isEmpty() || itemType == null || description.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "Item Name, Item Type, Description and Price are required");
            } else {
                try {
                    price = Double.parseDouble(priceField.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Please enter a valid price");
                    return;
                }
                if (price < 0){
                    showAlert(Alert.AlertType.ERROR, "Failed", "Price cannot be negative");
                    return;
                }
                if (dataStore.getMenuItem(itemName) == null) {
                    MenuItem newMenuItem = new MenuItem(itemName, price, MenuItem.ItemType.MAIN, true, description, null);
                    dataStore.addMenuItem(newMenuItem);
                    if (dataStore.getMenuItem(itemName) != null) {
                        initialize();
                        showAlert(Alert.AlertType.CONFIRMATION, "New Menu Item", "New Menu Item has been created");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "New Menu Item", "Menu Item creation failed");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "New Menu Item", "Menu Item already exists");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "New Menu Item", "Unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void onDeleteMenuItemButtonClick() {
        // Handle delete user button click
        try{
            if (loadedMenuItem != null) {
                dataStore.removeMenuItem(loadedMenuItem);
                initialize();
                showAlert(Alert.AlertType.CONFIRMATION, "Deleted Menu Item", "Menu Item has been deleted");
            }
            else{
                showAlert(Alert.AlertType.ERROR, "Deleted Menu Item", "Select a Menu Item to delete");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Deleted Menu Item", "Unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void onEditMenuItemButtonClick() {
        // Handle customise button click
        try{
            if (loadedMenuItem == null) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Please select a Menu Item from the table");
            } else{
                populateMenuItemParam(loadedMenuItem);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void onSubmitChangesMenuItemButtonClick() {
        // Handle submit changes button click
        if (loadedMenuItem == null) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please select a Menu Item");
            return;
        }
        try{
            String itemName = itemNameField.getText();
            Double price;
            MenuItem.ItemType itemType = itemTypeField.getSelectionModel().getSelectedItem();
            MenuItem.DietaryRequirement dietaryRequirement = dietaryRequirementsField.getSelectionModel().getSelectedItem();
            String description = itemDescriptionArea.getText();
            if (itemName.isEmpty() || itemType == null || description.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "Item Name, Item Type, Description and Price are required");
            }else {
                try{
                    price = Double.parseDouble(priceField.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Please enter a valid price");
                    return;
                }
                if (price < 0){
                    showAlert(Alert.AlertType.ERROR, "Failed", "Price cannot be negative");
                    return;
                }
                try {
                    loadedMenuItem.updateMetadata("itemName", itemName);
                    loadedMenuItem.updateMetadata("price", price);
                    loadedMenuItem.setDataValue("description", description);
                    loadedMenuItem.updateMetadata("itemType", itemType);
                    loadedMenuItem.updateMetadata("dietaryRequirement", dietaryRequirement);
                    dataStore.updateMenuItem(loadedMenuItem);
                    showAlert(Alert.AlertType.CONFIRMATION, "Updated Menu Item", "Menu Item has been updated");
                    initialize();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Updated User", "Unexpected error occured while updating Menu Item: " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Empty Field", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onExitMenuItemButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) exitMenuItemButton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, exitMenuItemButton);
    }
/*===================================================================================================================================================================================
    Methods for both User and Menu Item management:
====================================================================================================================================================================================*/
    private void rowHighlight(HBox row){
        if (currentlyHighlightedRow != null){
            currentlyHighlightedRow.getStyleClass().remove("grid-pane-highlight");
        }
        row.getStyleClass().add("grid-pane-highlight");
        currentlyHighlightedRow = row;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}