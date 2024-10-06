package teampixl.com.pixlpos.controllers.waiterconsole;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.*;

import teampixl.com.pixlpos.models.Order;
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
import teampixl.com.pixlpos.models.tools.DataManager;

import javafx.scene.layout.HBox;

import javax.lang.model.type.NullType;



public class WaiterScreen2Controller
{

    /*
    Textfields
     */
    @FXML
    private TextField searchbar;

    private String searchText;

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
    private ComboBox<String> customernumber;
    @FXML
    private ComboBox<String> tablenumber;
    @FXML
    private ComboBox<String> paymentstatus;
    @FXML
    private ComboBox<String> ordertype;
    @FXML
    private Label totalprice;
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
    private Tab searchtab;
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
    private DynamicLabelManager labelManager;
    private DynamicButtonManager searchbuttonManager;
    private DynamicButtonManager entreebuttonManager;
    private DynamicButtonManager mainbuttonManager;
    private DynamicButtonManager drinksbuttonManager;
    private DynamicButtonManager dessertbuttonManager;

    private final MenuAPI menuAPI = MenuAPI.getInstance();
    private final DataStore dataStore = DataStore.getInstance();
    private final OrderAPI orderAPI = OrderAPI.getInstance();
    private final UserStack userStack = UserStack.getInstance();

    private int id = 1;

    private List<MenuItem> menuItems;
    private List<MenuItem> queryMenuItems;
    private Tooltip priceTooltip;

    private final Map<String, Integer> orderItems = new HashMap<>();
    private Label selectedItem = null;
    private final Stack<Runnable> actionStack = new Stack<>();
    private Order currentOrder;
    private String orderID;
    private Integer orderNumber = 0;
    private Double orderTotal = 0.00;


    //ComboBoxes for inputs
    public void comboinitialize() {
        // Define options for each ComboBox
        String[] customerAmounts = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] orderTypes = {"DINEIN", "TAKEAWAY", "DELIVERY"};
        String[] tableNumbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        String[] paymentStatuses = {"PAID", "PENDING", "NOT PAID"};

        // Initialize ComboBoxes with options
        customernumber.getItems().setAll(customerAmounts);
        ordertype.getItems().setAll(orderTypes);
        tablenumber.getItems().setAll(tableNumbers);
        paymentstatus.getItems().setAll(paymentStatuses);

        // Set default selections (optional)
        customernumber.setValue("00");
        ordertype.setValue("Dine In");
        tablenumber.setValue("1");
        paymentstatus.setValue("Not PAID");

        // Event Handlers using Lambdas
        customernumber.setOnAction(event -> handleCustomerNumberSelection());
        ordertype.setOnAction(event -> handleOrderTypeSelection());
        tablenumber.setOnAction(event -> handleTableNumberSelection());
        paymentstatus.setOnAction(event -> handlePaymentStatusSelection());
    }

    // Event handler methods
    private void handleCustomerNumberSelection() {
        String selectedCustomerNumber = customernumber.getValue();
        // Add logic to update the database or perform other actions
        System.out.println("Customer Number selected: " + selectedCustomerNumber);
    }

    private void handleOrderTypeSelection() {
        String selectedOrderType = ordertype.getValue();
        // Add logic here
        System.out.println("Order Type selected: " + selectedOrderType);
    }

    private void handleTableNumberSelection() {
        String selectedTableNumber = tablenumber.getValue();
        // Add logic here
        System.out.println("Table Number selected: " + selectedTableNumber);
    }

    private void handlePaymentStatusSelection() {
        String selectedPaymentStatus = paymentstatus.getValue();
        // Add logic here
        System.out.println("Payment Status selected: " + selectedPaymentStatus);
    }



    private enum TabType {
        SEARCH("search"),
        ENTREE("entree"),
        MAIN("main"),
        DRINKS("drinks"),
        DESSERT("dessert");

        private final String id;

        TabType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static TabType fromId(String id) {
            for (TabType tabType : values()) {
                if (tabType.getId().equals(id)) {
                    return tabType;
                }
            }
            return null;
        }
    }

    /**
     * Animation timer to update the date and time
     */
    AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    };

    @FXML
    private void initialize() {
        initialiseOrder();
        datetime.start();
        tabManager = new DynamicTabManager(itemtab);
        labelManager = new DynamicLabelManager(orderitemslistview);
        /*these managers allow for adding and removing buttons*/
        searchbuttonManager = new DynamicButtonManager(searchpane,labelManager);
        entreebuttonManager = new DynamicButtonManager(entreepane,labelManager);
        mainbuttonManager = new DynamicButtonManager(mainpane,labelManager);
        drinksbuttonManager = new DynamicButtonManager(drinkspane,labelManager);
        dessertbuttonManager = new DynamicButtonManager(dessertpane,labelManager);
        initialiseSlider();
        comboinitialize();

        searchbar.setOnAction(event -> handleSearchBarEnter());
        priceslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleSliderChange(newValue.doubleValue());
            priceTooltip.setText(String.format("$%.2f", newValue.doubleValue()));
        });

        priceslider.setOnMouseMoved(event -> {
            priceTooltip.show(priceslider, event.getScreenX(), event.getScreenY() + 10);
        });

        priceslider.setOnMouseDragged(event -> {
            priceTooltip.show(priceslider, event.getScreenX(), event.getScreenY() + 10);
        });

        // Hide the tooltip when the mouse is released
        priceslider.setOnMouseReleased(event -> {
            priceTooltip.hide();
        });

        // Hide the tooltip when the mouse exits the slider
        priceslider.setOnMouseExited(event -> {
            priceTooltip.hide();
        });

        initsearch();

        menuItems = dataStore.readMenuItems();
        queryMenuItems = menuItems;

        /*this line allows for the tabs to scale dynamically as there is no feature for this ...*/
        itemtab.widthProperty().addListener((obs, oldVal, newVal) -> tabManager.adjustTabWidths());
        /*this allows you to update the buttons when a tab is clicked, does not work on init search tab so that has to be loaded above*/
        itemtab.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {

            if (newTab != null) {
                TabType tabType = TabType.fromId(newTab.getId());
                switch (tabType) {
                    case SEARCH:
                        searchbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            searchbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                            id++;
                        }
                        break;
                    case ENTREE:
                        entreebuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.ENTREE)) {
                                entreebuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                                id++;
                            }
                        }
                        break;
                    case MAIN:
                        mainbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.MAIN)) {
                                mainbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                                id++;
                            }
                        }
                        break;
                    case DRINKS:
                        drinksbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.DRINK)) {
                                drinksbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                                id++;
                            }
                        }
                        break;
                    case DESSERT:
                        dessertbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.DESSERT)) {
                                dessertbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                                id++;
                            }
                        }
                        break;
                }
            }
        });
    }

    private void handleSearchBarEnter() {
        itemtab.getSelectionModel().select(searchtab);
        searchText = searchbar.getText();
        searchbuttonManager.clearAllButtons();
        queryMenuItems = menuAPI.searchMenuItem(searchText);
        if (queryMenuItems.isEmpty()) {
            showErrorDialog(searchText);
            for (MenuItem menuItem : menuItems) {
                searchbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                id++;
            }
        } else {
            for (MenuItem menuItem : queryMenuItems) {
                searchbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                id++;
            }
        }
    }

    private void initialiseOrder() {
        String userId = userStack.getCurrentUserId();
        currentOrder = orderAPI.initializeOrder();
        if (currentOrder == null) {
            System.out.println("Failed to initialize order.");
            return;
        }
        orderNumber = currentOrder.getOrderNumber();
        // change order number

        System.out.println("Order initialized: " + currentOrder);
        ordernumber.setText(orderNumber.toString());
        orderID = currentOrder.getMetadataValue("order_id").toString();
    }

    private void initialiseSlider() {
        priceslider.setMin(0);
        priceslider.setMax(50);
        priceslider.setValue(50);
        priceslider.setShowTickLabels(true);
        priceslider.setShowTickMarks(true);
        priceslider.setMinorTickCount(5);
        priceslider.setBlockIncrement(10);

        priceTooltip = new Tooltip(String.format("$%.2f", priceslider.getValue()));
        Tooltip.install(priceslider, priceTooltip);
    }

    private void handleSliderChange(double maxPrice) {
        filterMenuItemsByPrice(maxPrice);
    }

    private void filterMenuItemsByPrice(double maxPrice) {
        searchbuttonManager.clearAllButtons();
        List<MenuItem> menuItems = queryMenuItems;
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getMetadataValue("price") instanceof Double) {
                if ((Double) menuItem.getMetadataValue("price") <= maxPrice) {
                    searchbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
                    id++;
                }
            }
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No items found");
        alert.setHeaderText(null);
        alert.setContentText("No items found for search term: " + message);
        alert.showAndWait();
    }

    @FXML
    protected void onSendOrderButtonClick() {
        // TODO: When orderAPI finished
    }

    @FXML
    protected void onCustomizeButtonClick() {
        itemtab.getSelectionModel().select(searchtab); /** this brings you to the tab and runs selection code*/
    }
    @FXML
    protected void onItemCorrectButtonClick() {

    }
    @FXML
    protected void onVoidItemButtonClick() {
        labelManager.clearAllLabels(); /*SAMPLEUSE*/
    }
    @FXML
    protected void onRestartButtonClick() {
        searchbuttonManager.clearAllButtons();/*SAMPLEUSE*/
    }
    @FXML
    protected void onFilterButtonClick() {
        // NOT NEEDED
    }
    @FXML
    protected void onLogoutButtonClick() {
        GuiCommon.loadRoot(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, logoutbutton);
    }

    protected void initsearch() {
        int id = 1;
        ObservableList<MenuItem> menuItems = dataStore.readMenuItems();
        for (MenuItem menuItem : menuItems){
            searchbuttonManager.addButton(String.valueOf(id), String.valueOf(menuItem.getMetadataValue("itemName")), "$" + menuItem.getMetadataValue("price"));
            id++;
        }
    }
    /**
     * This class is used to manage dynamic buttons
     */
    private class DynamicButtonManager {
        private int buttonCount = 0; // Keep track of the number of buttons
        private final FlowPane buttonPane; // FlowPane to hold buttons
        private final Map<String, Button> buttons; // Map to associate IDs with buttons
        private final DynamicLabelManager labelManager;
        private DynamicButtonManager(FlowPane buttonPane,DynamicLabelManager labelManager) {
            this.buttonPane = buttonPane;
            this.labelManager = labelManager;
            this.buttons = new HashMap<>();
        }

        // Method to add a new button
        private void addButton(String id,String itemname,String price) {
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
        private void removeButtonById(String id) {
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
           String menuItemId = menuAPI.keySearch(itemname);
          if (orderItems.containsKey(menuItemId)) {
            orderItems.put(menuItemId, orderItems.get(menuItemId) + 1);
          } else {
            orderItems.put(menuItemId, 1);
          }

          actionStack.push(() -> {
              if (orderItems.get(menuItemId) == 1) {
                  orderItems.remove(menuItemId);
              } else {
                  orderItems.put(menuItemId, orderItems.get(menuItemId) - 1);
              }
          });

          updateOrderSummary();
        }

        private void updateOrderSummary() {
            labelManager.clearAllLabels();
            for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
                String menuItemId = entry.getKey();
                String itemName = menuAPI.reverseKeySearch(menuItemId);
                int quantity = entry.getValue();
                MenuItem menuItem = menuAPI.getMenuItem(itemName);
                if (menuItem != null) {
                    Double price = (Double) menuItem.getMetadataValue("price");
                    Double total = price * quantity;
                    orderTotal += total;
                    totalprice.setText("$" + String.format("%.2f", orderTotal));
                    labelManager.addLabel(quantity, itemName);
                }
            }
        }

        // Method to clear all buttons
        private void clearAllButtons() {
            buttonPane.getChildren().clear(); // Clear the FlowPane
            buttons.clear(); // Clear the map
            System.out.println("All buttons cleared.");
        }
    }

    /**
     * This class is used to manage dynamic tabs
     */
    private static class DynamicTabManager {
        private final TabPane tabPane; // TabPane to hold the tabs
        private DynamicTabManager(TabPane tabPane) {
            this.tabPane = tabPane;
        }

        // Method to adjust the widths of the tabs
        private void adjustTabWidths() {
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

    /**
     * This class is used to manage dynamic labels
     */
    private static class DynamicLabelManager {
        private final ListView<Label> labelListView;

        private DynamicLabelManager(ListView<Label> labelListView) {
            this.labelListView = labelListView;
        }

        private void addLabel(int amount, String name) {
            Label newLabel = new Label(amount + "x " + name);
            // Apply a dummy style class
            newLabel.getStyleClass().add("docket-label");
            labelListView.getItems().add(newLabel);
            System.out.println("Added Label with text: " + name);
        }

        private void removeLabelByIndex(int index) {
            if (index >= 0 && index < labelListView.getItems().size()) {
                Label removedLabel = labelListView.getItems().remove(index);
                System.out.println("Removed Label: " + removedLabel.getText());
            } else {
                System.out.println("Invalid index: " + index);
            }
        }

        private void removeLabelByText(String text) {
            for (Label label : labelListView.getItems()) {
                if (label.getText().equals(text)) {
                    labelListView.getItems().remove(label);
                    System.out.println("Removed Label: " + text);
                    return;
                }
            }
            System.out.println("Label with text '" + text + "' not found.");
        }

        private void clearAllLabels() {
            labelListView.getItems().clear();
            System.out.println("All labels cleared.");
        }

        private Label getLabelByIndex(int index) {
            if (index >= 0 && index < labelListView.getItems().size()) {
                return labelListView.getItems().get(index);
            }
            return null;
        }

        private int getLabelCount() {
            return labelListView.getItems().size();
        }
    }
}
