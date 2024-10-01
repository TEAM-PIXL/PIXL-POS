package teampixl.com.pixlpos.controllers.waiterconsole;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.HashMap;
import java.util.Map;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javafx.scene.layout.HBox;

public class WaiterScreen2Controller
{
    /*
    Textfields
     */
    @FXML
    private TextField searchbar;

    /*
    labels
     */
    @FXML
    private Label date;
    @FXML
    private Label time;
    @FXML
    private Label ordernumber;
    @FXML
    private Label customernumber;
    @FXML
    private Label tabelnumber;

    /*
    listviews
     */
    @FXML
    private ListView<Label> orderitemslistview;

    /*
    buttons
     */
    @FXML
    private Button logoutbutton;
    @FXML
    private Button sendorderbutton;
    @FXML
    private Button customizebutton;
    @FXML
    private Button itemcorrectbutton;
    @FXML
    private Button voiditembutton;
    @FXML
    private Button restartbutton;
    @FXML
    private Button filterbutton;
    @FXML
    private Slider priceslider;

    /*
    containers
     */
    @FXML
    private TabPane itemtab;
    @FXML
    private FlowPane searchpane;
    @FXML
    private FlowPane entreepane;
    @FXML
    private FlowPane mainpane;
    @FXML
    private FlowPane drinkspane;
    @FXML
    private FlowPane dessertpane;



    private DynamicTabManager tabManager;
    public DynamicLabelManager labelManager;
    private DynamicButtonManager searchbuttonManager;
    private DynamicButtonManager entreebuttonManager;
    private DynamicButtonManager mainbuttonManager;
    private DynamicButtonManager drinksbuttonManager;
    private DynamicButtonManager dessertbuttonManager;



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
        tabManager = new DynamicTabManager(itemtab);
        labelManager = new DynamicLabelManager(orderitemslistview);
        /*these managers allow for adding and removing buttons*/
        searchbuttonManager = new DynamicButtonManager(searchpane,labelManager);
        entreebuttonManager = new DynamicButtonManager(entreepane,labelManager);
        mainbuttonManager = new DynamicButtonManager(mainpane,labelManager);
        drinksbuttonManager = new DynamicButtonManager(drinkspane,labelManager);
        dessertbuttonManager = new DynamicButtonManager(dessertpane,labelManager);




        /*this line allows for the tabs to scale dynamically as there is no feature for this ...*/
        itemtab.widthProperty().addListener((obs, oldVal, newVal) -> tabManager.adjustTabWidths());
    }


    @FXML
    protected void onSendOrderButtonClick() {

    }
    @FXML
    protected void onCustomizeButtonClick() {

    }
    @FXML
    protected void onItemCorrectButtonClick() {

    }
    @FXML
    protected void onVoidItemButtonClick() {
        labelManager.clearAllLabels();
    }
    @FXML
    protected void onRestartButtonClick() {
        searchbuttonManager.clearAllButtons();
    }
    @FXML
    protected void onFilterButtonClick() {
        // Handle exit button click
        searchbuttonManager.addButton("1","Beef Cheeseburger","$20");
    }
    @FXML
    protected void onLogoutButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, stage);
    }







    public static class DynamicButtonManager {
        private int buttonCount = 0; // Keep track of the number of buttons
        private final FlowPane buttonPane; // FlowPane to hold buttons
        private final Map<String, Button> buttons; // Map to associate IDs with buttons
        private final DynamicLabelManager labelManager;
        public DynamicButtonManager(FlowPane buttonPane,DynamicLabelManager labelManager) {
            this.buttonPane = buttonPane;
            this.labelManager = labelManager;
            this.buttons = new HashMap<>();
        }

        // Method to add a new button
        public void addButton(String id,String itemname,String price) {
            buttonCount++;
            Button newButton = new Button(itemname);
            newButton.setId(id); // Set the button's ID
            newButton.setPrefSize(225, 120);
            newButton.setContentDisplay(ContentDisplay.BOTTOM);
            newButton.setMnemonicParsing(false);
            newButton.getStyleClass().add("item-button");

            Label priceLabel = new Label(price);
            priceLabel.getStyleClass().add("buttonprice-label");

            newButton.setGraphic(priceLabel);
            // Add the new button to the FlowPane and the map
            buttonPane.getChildren().add(newButton);
            buttons.put(itemname, newButton); // Associate the button ID with the button

            // Add event handler to remove button when clicked
            newButton.setOnAction(e -> addtoorder(itemname));
        }

        // Method to remove a button directly
        private void removeButton(Button button) {
            buttonPane.getChildren().remove(button);
            buttons.remove(button.getId()); // Remove from the map
        }

        // Method to remove a button by ID
        public void removeButtonById(String id) {
            Button buttonToRemove = buttons.get(id);
            if (buttonToRemove != null) {
                removeButton(buttonToRemove);
                System.out.println("Removed Button: " + id);
            } else {
                System.out.println("Button with ID '" + id + "' not found.");
            }
        }

        // Method to remove a button directly
        private void addtoorder(String itemname) {
           /*this code will envoke another function which adds an id dependant item to the order listview*/
           labelManager.addLabel(itemname);
        }

        // Method to clear all buttons
        public void clearAllButtons() {
            buttonPane.getChildren().clear(); // Clear the FlowPane
            buttons.clear(); // Clear the map
            System.out.println("All buttons cleared.");
        }
    }
//    }

    public static class DynamicTabManager {
        private final TabPane tabPane; // TabPane to hold the tabs
        public DynamicTabManager(TabPane tabPane) {
            this.tabPane = tabPane;
        }

        // Method to adjust the widths of the tabs
        public void adjustTabWidths() {
            double totalWidth = tabPane.getWidth(); // Get the width of the TabPane
            int numberOfTabs = tabPane.getTabs().size(); // Get the number of tabs

            if (numberOfTabs > 0) {
                double tabWidth = totalWidth / numberOfTabs; // Calculate the width for each tab
                tabWidth = tabWidth -24.5;
                tabPane.setTabMinWidth(tabWidth); // Set the minimum width for the tabs
                tabPane.setTabMaxWidth(tabWidth); // Set the maximum width for the tabs
            }
        }
    }

    public static class DynamicLabelManager {
        private final ListView<Label> labelListView;

        public DynamicLabelManager(ListView<Label> labelListView) {
            this.labelListView = labelListView;
        }

        public void addLabel(String name) {
            Label newLabel = new Label("1x " + name);

            // Apply a dummy style class
            newLabel.getStyleClass().add("custom-label");

            labelListView.getItems().add(newLabel);
            System.out.println("Added Label with text: " + name);
        }

        public void removeLabelByIndex(int index) {
            if (index >= 0 && index < labelListView.getItems().size()) {
                Label removedLabel = labelListView.getItems().remove(index);
                System.out.println("Removed Label: " + removedLabel.getText());
            } else {
                System.out.println("Invalid index: " + index);
            }
        }

        public void removeLabelByText(String text) {
            for (Label label : labelListView.getItems()) {
                if (label.getText().equals(text)) {
                    labelListView.getItems().remove(label);
                    System.out.println("Removed Label: " + text);
                    return;
                }
            }
            System.out.println("Label with text '" + text + "' not found.");
        }

        public void clearAllLabels() {
            labelListView.getItems().clear();
            System.out.println("All labels cleared.");
        }

        public Label getLabelByIndex(int index) {
            if (index >= 0 && index < labelListView.getItems().size()) {
                return labelListView.getItems().get(index);
            }
            return null;
        }

        public int getLabelCount() {
            return labelListView.getItems().size();
        }
    }
}
