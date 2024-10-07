package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.models.Users;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import javafx.scene.layout.VBox;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.UsersAPI;
import javafx.scene.control.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.Users;
import javafx.scene.layout.HBox;
import teampixl.com.pixlpos.models.tools.DataManager;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javafx.scene.input.KeyCode;

public class AdminScreenUsersController
{
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the home admin screen of the application.
    ====================================================================================================================================================================================*/
    private final UserStack userStack = UserStack.getInstance();
    Users currentuser = userStack.getCurrentUser();
    String firstName = currentuser.getMetadata().metadata().get("first_name").toString();
    private DataStore dataStore;
    private UsersAPI userAPI;
    private DataManager dataManager;
    private Users loadedUser;
    /*
    Shared Components
     */

    @FXML
    private Text greeting;
    @FXML
    private TextField searchbar;
    @FXML
    private Label date;
    @FXML
    private Label time;

    @FXML
    private Button homebutton;
    @FXML
    private Button usersbutton;
    @FXML
    private Button menubutton;
    @FXML
    private Button stockbutton;
    @FXML
    private Button analyticsbutton;
    @FXML
    private Button logoutbutton;

    /*
    User components
     */
    @FXML
    private TextField firstnamefield;
    @FXML
    private TextField lastnamefield;
    @FXML
    private TextField emailfield;
    @FXML
    private PasswordField passwordfield;
    @FXML
    private ChoiceBox<Users.UserRole> roleselect;
    @FXML
    private TextField usernamefield;

    @FXML
    private Button submitbutton;
    @FXML
    private Button adduserbutton;
    @FXML
    private Button cancelbutton;
    @FXML
    private Button editbutton;
    @FXML
    private Button removebutton;

    @FXML
    private ListView<HBox> userslist;

    int adding_counter = 0;

    AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    };


    @FXML
    public void initialize() {
        datetime.start();
        adding_counter = 0;
        userslist.getItems().clear();
        greeting.setText("Hello, " + firstName);
        dataStore = DataStore.getInstance();
        userAPI = UsersAPI.getInstance();

        roleselect.getItems().clear();
        roleselect.getItems().addAll(new HashSet<>(Arrays.asList(Users.UserRole.values())));
        populateUserGrid();


        searchbar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchUser();
            }
        });

        searchbar.setOnAction(event -> searchUser());

    }

    private String toReadableDate(String dateString){

        long createAt = Long.parseLong(dateString);

        LocalDateTime createdAtDateTime = Instant.ofEpochMilli(createAt).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return createdAtDateTime.format(formatter);
    }

    private void populateUserGrid() {
        int id_counter = 0;

        ObservableList<Users> listOfUsers = dataStore.readUsers();

        for (Users user : listOfUsers) {
            String username = user.getMetadataValue("username").toString();

            addUserToListView(
                    userslist,
                    user.getMetadataValue("id").toString(),
                    userAPI.getUsersFirstNameByUsername(username),
                    (userAPI.getUsersFirstNameByUsername(username) + " " + userAPI.getUsersLastNameByUsername(username)),
                    username,
                    toReadableDate(user.getMetadataValue("created_at").toString()),
                    userAPI.getUsersRoleByUsername(username).toString());

            id_counter++;
        }
    }

    @FXML
    protected void onSubmitButtonClick(){
        // Handle submit changes button click
        try{
            String username = usernamefield.getText();
            String firstName = firstnamefield.getText();
            String lastName = lastnamefield.getText();
            String password = passwordfield.getText();
            String email = emailfield.getText();
            Users.UserRole role = roleselect.getSelectionModel().getSelectedItem();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() ||
                    lastName.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "All fields are required");
            }else {


                try {
                    loadedUser.updateMetadata("username", username);
                    loadedUser.updateMetadata("first_name", firstName);
                    loadedUser.updateMetadata("last_name", lastName);
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
        onCancelButtonClick();
    }
    @FXML
    protected void onAddUserButtonClick(){

        try {
            String firstName = firstnamefield.getText();
            String lastName = lastnamefield.getText();
            String password = passwordfield.getText();
            String username = usernamefield.getText();
            String email = emailfield.getText();
            Users.UserRole role = roleselect.getSelectionModel().getSelectedItem();

            if (username.isEmpty()|| firstName.isEmpty() || lastName.isEmpty()  || password.isEmpty() || email.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "All fields are required");
            } else {
                if (userAPI.getUser(username) == null) {
                    boolean registerUser = AuthenticationManager.register(firstName, lastName, username, password, email, role);
                    try {
                        userAPI.postUsers(firstName, lastName, username, password, email, role);
                        initialize();
                        showAlert(Alert.AlertType.CONFIRMATION, "New User", "New User has been created");
                    }catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "New User", "Registration Failed");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "New User", "User already exists");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "New User", "Unexpected error occured: " + e.getMessage());
        }
        onCancelButtonClick();

    }
    @FXML
    protected void onCancelButtonClick(){
        // Handle clear button click
        firstnamefield.clear();
        lastnamefield.clear();
        usernamefield.clear();
        passwordfield.clear();
        emailfield.clear();
        roleselect.setValue(null);
        loadedUser = null;
    }


    @FXML
    protected void onUsersButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) usersbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
    }
    @FXML
    protected void onMenuButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) menubutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_MENU_FXML, GuiCommon.ADMIN_SCREEN_MENU_TITLE, stage);
    }
    @FXML
    protected void onHomeButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) homebutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, stage);
    }
    @FXML
    protected void onStockButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) stockbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_STOCK_FXML, GuiCommon.ADMIN_SCREEN_STOCK_TITLE, stage);
    }
    @FXML
    protected void onAnalyticsButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) analyticsbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_ANALYTICS_FXML, GuiCommon.ADMIN_SCREEN_ANALYTICS_TITLE, stage);
    }
    @FXML
    protected void onLogoutButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, stage);
    }


    // Placeholder methods for button actions
    private void onEditButtonClick(ActionEvent event, String id) {
        // Implement edit menu item logic here
        try{
            loadedUser = userAPI.keyTransform(id);
            if (loadedUser == null) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Please select a user from the table");
            } else{
                populateUserParam(loadedUser);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected error occured: " + e.getMessage());
        }
    }

    private void onRemoveButtonClick(ActionEvent event, String id) {
        // Implement remove menu item logic here

        ObservableList<HBox> items = userslist.getItems(); // Get the items of the ListView

        // Loop through the list to find the HBox with the matching ID
        for (int i = 0; i < items.size(); i++) {
            HBox hbox = items.get(i);

            if (id.equals(hbox.getId())) {  // Compare the ID of the HBox
                try{
                    items.remove(i);  // Remove the HBox at the found index
                    // Handle delete user button click
                    dataStore.deleteUser(userAPI.getUser(userAPI.reverseKeySearch(id)));
                    initialize();
                    showAlert(Alert.AlertType.CONFIRMATION, "Deleted User", "User has been deleted");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Deleted User", "Unexpected error occured: " + e.getMessage());
                }
                break;            // Exit the loop once the HBox is removed
            }
        }
    }



    /**
     * Adds a user item to the specified ListView. The user is represented as an HBox containing four AnchorPanes
     *
     * @param id the way to identify the entry along with the listview index, recommend matching them up
     * @param listView the ListView to which the new menu item will be added. Each user item will be displayed as an HBox.
     * @param name the name of the user item (e.g., the michael smith, displayed in the first column of the HBox.
     * @param email the email of the user, displayed in the second column of the HBox.
     * @param username the username of the user, displayed in the third column of the HBox.
     * @param userSince time the account was made, displayed in the fourth column of the HBox.
     * @param role the role of the user
     */
    public void addUserToListView(ListView<HBox> listView, String id, String name, String email, String username, String userSince, String role) {
        HBox hbox = new HBox();
        hbox.setId(id);
        hbox.setPrefHeight(50.0);

        // Name and Email
        AnchorPane nameEmailPane = new AnchorPane();
        Label nameLabel = new Label(name);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setContentDisplay(ContentDisplay.BOTTOM);
        nameLabel.setPrefSize(83.2, 50.4);
        Label emailLabel = new Label(email);
        emailLabel.setTextFill(Color.web("#918e8e"));
        nameLabel.setGraphic(emailLabel);
        AnchorPane.setTopAnchor(nameLabel, 0.0);
        AnchorPane.setRightAnchor(nameLabel, 0.0);
        AnchorPane.setBottomAnchor(nameLabel, 0.0);
        AnchorPane.setLeftAnchor(nameLabel, 0.0);
        nameEmailPane.getChildren().add(nameLabel);
        HBox.setHgrow(nameEmailPane, Priority.ALWAYS);

        // Username
        AnchorPane usernamePane = new AnchorPane();
        Label usernameLabel = new Label(username);
        usernameLabel.setAlignment(Pos.CENTER);
        usernameLabel.setContentDisplay(ContentDisplay.CENTER);
        usernameLabel.setPrefSize(111.2, 50.4);
        AnchorPane.setTopAnchor(usernameLabel, 0.0);
        AnchorPane.setRightAnchor(usernameLabel, 0.0);
        AnchorPane.setBottomAnchor(usernameLabel, 0.0);
        AnchorPane.setLeftAnchor(usernameLabel, 0.0);
        usernamePane.getChildren().add(usernameLabel);
        HBox.setHgrow(usernamePane, Priority.ALWAYS);

        // User Since Date
        AnchorPane datePane = new AnchorPane();
        Label dateLabel = new Label(userSince);
        dateLabel.setAlignment(Pos.CENTER);
        dateLabel.setPrefSize(89.6, 50.4);
        AnchorPane.setTopAnchor(dateLabel, 0.0);
        AnchorPane.setRightAnchor(dateLabel, 0.0);
        AnchorPane.setBottomAnchor(dateLabel, 0.0);
        AnchorPane.setLeftAnchor(dateLabel, 0.0);
        datePane.getChildren().add(dateLabel);
        HBox.setHgrow(datePane, Priority.ALWAYS);

        // Role
        AnchorPane rolePane = new AnchorPane();
        Label roleLabel = new Label(role);
        roleLabel.setAlignment(Pos.CENTER);
        roleLabel.setPrefSize(83.2, 50.4);
        roleLabel.setStyle("-fx-background-color: #0095FF; -fx-background-radius: 10;");
        roleLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(roleLabel, 0.0);
        AnchorPane.setRightAnchor(roleLabel, 0.0);
        AnchorPane.setBottomAnchor(roleLabel, 0.0);
        AnchorPane.setLeftAnchor(roleLabel, 0.0);
        rolePane.getChildren().add(roleLabel);
        rolePane.setPadding(new Insets(10, 0, 10, 0));
        HBox.setHgrow(rolePane, Priority.ALWAYS);

        // Edit Button
        AnchorPane editButtonPane = new AnchorPane();
        editButtonPane.setMaxWidth(100.0);
        editButtonPane.setMinWidth(100.0);
        Button editButton = new Button("Edit");
        editButton.setId("editbutton");
        editButton.setLayoutX(35.0);
        editButton.setLayoutY(12.0);
        editButton.setMinHeight(26.4);
        editButton.setMaxHeight(26.4);
        editButton.setPrefHeight(26.4);
        editButton.setMinWidth(50.0);
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(event -> onEditButtonClick(event,id));
        editButtonPane.getChildren().add(editButton);
        HBox.setHgrow(editButtonPane, Priority.ALWAYS);

        // Remove Button
        AnchorPane removeButtonPane = new AnchorPane();
        removeButtonPane.setMaxWidth(100.0);
        removeButtonPane.setMinWidth(100.0);
        Button removeButton = new Button("Remove");
        removeButton.setId("removebutton");
        removeButton.setLayoutX(11.0);
        removeButton.setLayoutY(12.0);
        removeButton.setMinHeight(26.4);
        removeButton.setPrefHeight(26.4);
        removeButton.setMinWidth(50.0);
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(event -> onRemoveButtonClick(event,id));
        removeButtonPane.getChildren().add(removeButton);
        HBox.setHgrow(removeButtonPane, Priority.ALWAYS);

        // Add all components to the HBox
        hbox.getChildren().addAll(nameEmailPane, usernamePane, datePane, rolePane, editButtonPane, removeButtonPane);

        // Add the HBox to the ListView
        listView.getItems().add(hbox);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void populateUserParam(Users User) {
        Object username = User.getMetadataValue("username");
        Object password = User.getData().get("password");
        Object email = User.getData().get("email");
        Object role = User.getMetadata().metadata().get("role");
        Object fistName = User.getMetadata().metadata().get("first_name");
        Object lastName = User.getMetadata().metadata().get("last_name");

        usernamefield.setText(username.toString());
        firstnamefield.setText(fistName.toString());
        lastnamefield.setText(lastName.toString());
        passwordfield.setText(password.toString());
        emailfield.setText(email.toString());
        roleselect.setValue(Users.UserRole.valueOf(role.toString()));
        loadedUser = User;
    }

    private void searchUser(){
        // Handle search button click
        String searchInput = searchbar.getText();
        if (searchInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please specify a User");
        }else {
            try {
                List<Users> usersList = userAPI.searchUsers(searchInput);
                if (usersList.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "User not found");
                }else {
                    if (usersList.size() > 1) {
                        showAlert(Alert.AlertType.ERROR, "Failed", "Multiple users found. Please refine your search");
                    } else {
                        loadedUser = usersList.getFirst();
                        populateUserParam(loadedUser);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected Error: " + e.getMessage());
            }
        }
    }

}