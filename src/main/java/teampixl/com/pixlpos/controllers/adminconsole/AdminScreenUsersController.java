package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.models.Users;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;

import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import javafx.scene.layout.VBox;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.UsersAPI;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javafx.scene.layout.HBox;

public class AdminScreenUsersController
{
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the home admin screen of the application.
    ====================================================================================================================================================================================*/

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



































    @FXML
    protected void onSubmitButtonClick(){

    }
    @FXML
        protected void onAddUserButtonClick(){

        if(adding_counter == 0){
            addUserToListView(userslist,String.valueOf(adding_counter),"steve steven","steve@gmail.com","Graycat45","20/20/2033","Waiter");
        }
        else if(adding_counter == 1){
            addUserToListView(userslist,String.valueOf(adding_counter),"paul Allen","Paul@gmail.com","Blackcat45","20/30/2033","Cook");
        }
        else if(adding_counter == 2){
            addUserToListView(userslist,String.valueOf(adding_counter),"Rachael Black","rachael@gmail.com","bird45","70/30/2033","Admin");
        }
        else{
            addUserToListView(userslist,String.valueOf(adding_counter),"you get it","meow@gmail.com","meow","meow","meow");
        }
        adding_counter++;

    }
    @FXML
    protected void onCancelButtonClick(){

    }

    @FXML
    protected void onEditButtonClick(){

    }

    @FXML
    protected void onRemoveButtonClick(){

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
    private void onEditButtonClick(javafx.event.ActionEvent event,String id) {
        // Implement edit menu item logic here
    }

    private void onRemoveButtonClick(javafx.event.ActionEvent event,String id) {
        // Implement remove menu item logic here

        ObservableList<HBox> items = userslist.getItems(); // Get the items of the ListView

        // Loop through the list to find the HBox with the matching ID
        for (int i = 0; i < items.size(); i++) {
            HBox hbox = items.get(i);

            if (id.equals(hbox.getId())) {  // Compare the ID of the HBox
                items.remove(i);  // Remove the HBox at the found index
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
        nameLabel.setAlignment(javafx.geometry.Pos.CENTER);
        nameLabel.setContentDisplay(ContentDisplay.BOTTOM);
        nameLabel.setPrefSize(83.2, 50.4);
        Label emailLabel = new Label(email);
        emailLabel.setTextFill(javafx.scene.paint.Color.web("#918e8e"));
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
        usernameLabel.setAlignment(javafx.geometry.Pos.CENTER);
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
        dateLabel.setAlignment(javafx.geometry.Pos.CENTER);
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
        roleLabel.setAlignment(javafx.geometry.Pos.CENTER);
        roleLabel.setPrefSize(83.2, 50.4);
        roleLabel.setStyle("-fx-background-color: #0095FF; -fx-background-radius: 10;");
        roleLabel.setTextFill(javafx.scene.paint.Color.WHITE);
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

}
