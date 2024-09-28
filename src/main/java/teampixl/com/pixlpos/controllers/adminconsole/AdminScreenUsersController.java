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














    /**
     * Adds a user item to the specified ListView. The user is represented as an HBox containing four AnchorPanes
     *
     * @param listView the ListView to which the new menu item will be added. Each user item will be displayed as an HBox.
     * @param name the name of the user item (e.g., the michael smith, displayed in the first column of the HBox.
     * @param email the email of the user, displayed in the second column of the HBox.
     * @param username the username of the user, displayed in the third column of the HBox.
     * @param userSince time the account was made, displayed in the fourth column of the HBox.
     * @param role the role of the user
     */
    public static void addUserToListView(ListView<HBox> listView, String name, String email, String username, String userSince, String role) {

        // Create the main HBox
        HBox hBox = new HBox();
        hBox.setPrefHeight(50.0);
        hBox.setPrefWidth(200.0);

        // VBox to hold Name and Email (Email will be below Name)
        VBox nameVBox = new VBox();
        nameVBox.setAlignment(Pos.CENTER); // Center the text inside the VBox
        nameVBox.setSpacing(5); // Space between name and email

        // Name label
        Label nameLabel = new Label(name);
        nameLabel.setAlignment(Pos.CENTER); // Center text within the label

        // Email label (below name)
        Label emailLabel = new Label(email);
        emailLabel.setTextFill(javafx.scene.paint.Color.valueOf("#918e8e"));
        emailLabel.setAlignment(Pos.CENTER); // Center text within the label

        // Add name and email labels to the VBox
        nameVBox.getChildren().addAll(nameLabel, emailLabel);

        // Wrap VBox in an AnchorPane (to match your FXML layout)
        AnchorPane nameAnchorPane = new AnchorPane();
        nameAnchorPane.setPrefHeight(50.0);
        nameAnchorPane.setPrefWidth(212.0);
        nameAnchorPane.getChildren().add(nameVBox);

        // Position the VBox in the center of the AnchorPane
        AnchorPane.setTopAnchor(nameVBox, 0.0);
        AnchorPane.setBottomAnchor(nameVBox, 0.0);
        AnchorPane.setLeftAnchor(nameVBox, 0.0);
        AnchorPane.setRightAnchor(nameVBox, 0.0);

        // AnchorPane for Username
        AnchorPane usernameAnchorPane = new AnchorPane();
        usernameAnchorPane.setPrefHeight(200.0);
        usernameAnchorPane.setPrefWidth(200.0);

        Label usernameLabel = new Label(username);
        usernameLabel.setAlignment(Pos.CENTER); // Center text within the label
        usernameLabel.setLayoutX(45.0);
        usernameLabel.setLayoutY(16.0);

        usernameAnchorPane.getChildren().add(usernameLabel);

        // AnchorPane for User Since (Date)
        AnchorPane userSinceAnchorPane = new AnchorPane();
        userSinceAnchorPane.setPrefHeight(200.0);
        userSinceAnchorPane.setPrefWidth(200.0);

        Label userSinceLabel = new Label(userSince);
        userSinceLabel.setAlignment(Pos.CENTER); // Center text within the label
        userSinceLabel.setLayoutX(35.0); //57.0
        userSinceLabel.setLayoutY(16.0);

        userSinceAnchorPane.getChildren().add(userSinceLabel);

        // AnchorPane for Role
        AnchorPane roleAnchorPane = new AnchorPane();
        roleAnchorPane.setPrefHeight(200.0);
        roleAnchorPane.setPrefWidth(200.0);

        Label roleLabel = new Label(role);
        roleLabel.setAlignment(Pos.CENTER); // Center text within the label
        roleLabel.setLayoutX(46.0);
        roleLabel.setLayoutY(10.0);
        roleLabel.setPrefHeight(30.0);
        roleLabel.setPrefWidth(60.0);
        roleLabel.setStyle("-fx-background-color: #0095FF; -fx-background-radius: 10;");
        roleLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        roleAnchorPane.getChildren().add(roleLabel);

        // Add all anchor panes to the HBox
        hBox.getChildren().addAll(nameAnchorPane, usernameAnchorPane, userSinceAnchorPane, roleAnchorPane);

        // Add the HBox to the ListView
        listView.getItems().add(hBox);
    }






















    @FXML
    protected void onSubmitButtonClick(){

    }
    @FXML
    protected void onAddUserButtonClick(){
        addUserToListView(userslist,"josh","josh.he@gmail.com","joshe","20/20/2033","waiter");
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
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
    }
    @FXML
    protected void onMenuButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) menubutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_MENU_FXML, GuiCommon.ADMIN_SCREEN_MENU_TITLE, stage);
    }
    @FXML
    protected void onHomeButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) homebutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, stage);
    }
    @FXML
    protected void onStockButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) stockbutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_STOCK_FXML, GuiCommon.ADMIN_SCREEN_STOCK_TITLE, stage);
    }
    @FXML
    protected void onAnalyticsButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) analyticsbutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_ANALYTICS_FXML, GuiCommon.ADMIN_SCREEN_ANALYTICS_TITLE, stage);
    }
    @FXML
    protected void onLogoutButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, stage);
    }
}
