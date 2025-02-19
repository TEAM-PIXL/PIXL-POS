package teampixl.com.pixlpos.controllers.waiterconsole;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import teampixl.com.pixlpos.common.GuiCommon;

import java.util.*;

import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.common.OrderUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;

public class WaiterScreen2Controller {

    @FXML
    private TextField searchbar;

    @FXML
    private Label date;
    @FXML
    private Label time;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

    @FXML
    private ListView<OrderItem> orderitemslistview;

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
    private Button settingsbutton;
    @FXML
    private Button filterbutton;
    @FXML
    private Slider priceslider;

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

    private final Map<MenuItem, Integer> orderItems = new HashMap<>();
    private final Map<MenuItem, List<String>> itemNotes = new HashMap<>();
    private OrderItem selectedItem = null;
    private final Stack<Runnable> actionStack = new Stack<>();
    private Order currentOrder;
    private String orderID;
    private Double orderTotal = 0.00;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    protected void addtooltips() {
        Tooltip hometooltip = new Tooltip("Settings");
        hometooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(settingsbutton, hometooltip);

        Tooltip userstooltip = new Tooltip("Logout");
        userstooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(logoutbutton, userstooltip);
    }

    public void comboinitialize() {
        String[] customerAmounts = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] orderTypes = Arrays.stream(Order.OrderType.values()).map(Enum::name).toArray(String[]::new);
        String[] tableNumbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] paymentStatuses = Arrays.stream(Order.PaymentMethod.values()).map(Enum::name).toArray(String[]::new);

        customernumber.getItems().setAll(customerAmounts);
        ordertype.getItems().setAll(orderTypes);
        tablenumber.getItems().setAll(tableNumbers);
        paymentstatus.getItems().setAll(paymentStatuses);

        customernumber.setValue("1");
        ordertype.setValue(Order.OrderType.DINE_IN.name());
        tablenumber.setValue("1");
        paymentstatus.setValue(Order.PaymentMethod.NOT_PAID.name());

        customernumber.setOnAction(event -> handleCustomerNumberSelection());
        ordertype.setOnAction(event -> handleOrderTypeSelection());
        tablenumber.setOnAction(event -> handleTableNumberSelection());
        paymentstatus.setOnAction(event -> handlePaymentStatusSelection());
    }

    private void handleCustomerNumberSelection() {
        String selectedCustomerNumber = customernumber.getValue();
        System.out.println("Customer Number selected: " + selectedCustomerNumber);
    }

    private void handleOrderTypeSelection() {
        String selectedOrderType = ordertype.getValue();
        System.out.println("Order Type selected: " + selectedOrderType);
    }

    private void handleTableNumberSelection() {
        String selectedTableNumber = tablenumber.getValue();
        System.out.println("Table Number selected: " + selectedTableNumber);
    }

    private void handlePaymentStatusSelection() {
        String selectedPaymentStatus = paymentstatus.getValue();
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

    @FXML
    private void initialize() {
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String formattedTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            Platform.runLater(() -> {
                date.setText(formattedDate);
                time.setText(formattedTime);
            });
        }, 0, 1, TimeUnit.SECONDS);

        tabManager = new DynamicTabManager(itemtab);
        labelManager = new DynamicLabelManager(orderitemslistview);
        searchbuttonManager = new DynamicButtonManager(searchpane, labelManager);
        entreebuttonManager = new DynamicButtonManager(entreepane, labelManager);
        mainbuttonManager = new DynamicButtonManager(mainpane, labelManager);
        drinksbuttonManager = new DynamicButtonManager(drinkspane, labelManager);
        dessertbuttonManager = new DynamicButtonManager(dessertpane, labelManager);

        initialiseSlider();
        comboinitialize();
        addtooltips();
        initialiseOrder();

        searchbar.setOnAction(event -> handleSearchBarEnter());
        priceslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleSliderChange(newValue.doubleValue());
            priceTooltip.setText(String.format("$%.2f", newValue.doubleValue()));
        });

        priceslider.setOnMouseMoved(event -> priceTooltip.show(priceslider, event.getScreenX(), event.getScreenY() + 10));
        priceslider.setOnMouseDragged(event -> priceTooltip.show(priceslider, event.getScreenX(), event.getScreenY() + 10));
        priceslider.setOnMouseReleased(event -> priceTooltip.hide());
        priceslider.setOnMouseExited(event -> priceTooltip.hide());

        initsearch();

        itemtab.widthProperty().addListener((obs, oldVal, newVal) -> tabManager.adjustTabWidths());
        itemtab.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {

            if (newTab != null) {
                populateButtonsForTab(newTab);
            }
        });

        orderitemslistview.setCellFactory(param -> new OrderItemCell());

        orderitemslistview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedItem = newValue);
    }

    private void populateButtonsForCurrentTab() {
        Tab currentTab = itemtab.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            populateButtonsForTab(currentTab);
        }
    }

    private void populateButtonsForTab(Tab tab) {
        TabType tabType = TabType.fromId(tab.getId());
        if (tabType == null) return;

        Task<Void> loadButtonsTask = new Task<>() {
            @Override
            protected Void call() {
                switch (tabType) {
                    case SEARCH:
                        searchbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            String itemName = (String) menuItem.getMetadataValue("itemName");
                            String price = menuItem.getMetadataValue("price").toString();
                            searchbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                            id++;
                        }
                        break;
                    case ENTREE:
                        entreebuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.ENTREE)) {
                                String itemName = (String) menuItem.getMetadataValue("itemName");
                                String price = menuItem.getMetadataValue("price").toString();
                                entreebuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                                id++;
                            }
                        }
                        break;
                    case MAIN:
                        mainbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.MAIN)) {
                                String itemName = (String) menuItem.getMetadataValue("itemName");
                                String price = menuItem.getMetadataValue("price").toString();
                                mainbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                                id++;
                            }
                        }
                        break;
                    case DRINKS:
                        drinksbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.DRINK)) {
                                String itemName = (String) menuItem.getMetadataValue("itemName");
                                String price = menuItem.getMetadataValue("price").toString();
                                drinksbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                                id++;
                            }
                        }
                        break;
                    case DESSERT:
                        dessertbuttonManager.clearAllButtons();
                        for (MenuItem menuItem : menuItems) {
                            if (Objects.equals(menuItem.getMetadataValue("itemType"), MenuItem.ItemType.DESSERT)) {
                                String itemName = (String) menuItem.getMetadataValue("itemName");
                                String price = menuItem.getMetadataValue("price").toString();
                                dessertbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                                id++;
                            }
                        }
                        break;
                }
                return null;
            }
        };

        loadButtonsTask.setOnSucceeded(event -> {
        });

        executorService.submit(loadButtonsTask);
    }

    private void handleSearchBarEnter() {
        itemtab.getSelectionModel().select(searchtab);
        String searchText = searchbar.getText();

        Task<Void> searchTask = new Task<>() {
            @Override
            protected Void call() {
                queryMenuItems = menuAPI.searchMenuItem(searchText);
                Platform.runLater(() -> {
                    searchbuttonManager.clearAllButtons();
                    if (queryMenuItems.isEmpty()) {
                        showErrorDialog("No items were found with the name: " + searchText);
                        for (MenuItem menuItem : menuItems) {
                            String itemName = (String) menuItem.getMetadataValue("itemName");
                            String price = menuItem.getMetadataValue("price").toString();
                            searchbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                            id++;
                        }
                    } else {
                        for (MenuItem menuItem : queryMenuItems) {
                            String itemName = (String) menuItem.getMetadataValue("itemName");
                            String price = menuItem.getMetadataValue("price").toString();
                            searchbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                            id++;
                        }
                    }
                });
                return null;
            }
        };

        executorService.submit(searchTask);
    }

    private void initialiseOrder() {
        orderTotal = 0.00;
        currentOrder = orderAPI.initializeOrder();
        if (currentOrder == null) {
            System.out.println("Failed to initialize order.");
            return;
        }
        int orderNumber = currentOrder.getOrderNumber();

        System.out.println("Order initialized: " + currentOrder);
        ordernumber.setText(Integer.toString(orderNumber));
        orderID = (String) currentOrder.getMetadataValue("order_id");

        Map<MenuItem, Integer> existingItems = orderAPI.getOrderItemsById(orderID);
        if (existingItems != null) {
            orderItems.putAll(existingItems);
        }

        String specialRequests = (String) currentOrder.getDataValue("special_requests");
        if (specialRequests != null) {
            itemNotes.putAll(OrderUtil.deserializeItemNotes(specialRequests, menuAPI));
        }

        updateOrderSummary();
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
        Task<Void> filterTask = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    searchbuttonManager.clearAllButtons();
                    for (MenuItem menuItem : queryMenuItems) {
                        if (menuItem.getMetadataValue("price") instanceof Double) {
                            if ((Double) menuItem.getMetadataValue("price") <= maxPrice) {
                                String itemName = (String) menuItem.getMetadataValue("itemName");
                                String price = menuItem.getMetadataValue("price").toString();
                                searchbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                                id++;
                            }
                        }
                    }
                });
                return null;
            }
        };

        executorService.submit(filterTask);
    }

    private void showErrorDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    protected void onSendOrderButtonClick() {
        if (orderItems.isEmpty()) {
            showErrorDialog("No items in the order.");
            return;
        }

        Task<Void> sendOrderTask = new Task<>() {
            @Override
            protected Void call() {
                List<StatusCode> statusCodes = new ArrayList<>();

                statusCodes.addAll(orderAPI.putOrderItems(orderID, orderItems));
                statusCodes.addAll(orderAPI.putOrderCustomers(orderID, Integer.parseInt(customernumber.getValue())));
                statusCodes.addAll(orderAPI.putOrderTableNumber(orderID, Integer.parseInt(tablenumber.getValue())));
                statusCodes.addAll(orderAPI.putOrderType(orderID, Order.OrderType.valueOf(ordertype.getValue())));
                statusCodes.addAll(orderAPI.putOrderPaymentMethod(orderID, Order.PaymentMethod.valueOf(paymentstatus.getValue())));

                String specialRequests = OrderUtil.serializeItemNotes(itemNotes);
                System.out.println("Special Requests: " + specialRequests);
                statusCodes.addAll(orderAPI.putOrderSpecialRequests(orderID, specialRequests));

                statusCodes.addAll(orderAPI.putOrderStatus(orderID, Order.OrderStatus.SENT));

                if (!Exceptions.isSuccessful(statusCodes)) {
                    Platform.runLater(() -> showErrorDialog(Exceptions.returnStatus("Failed to apply order details:", statusCodes)));
                    return null;
                }

                List<StatusCode> postStatus = orderAPI.postOrder(currentOrder);
                if (Exceptions.isSuccessful(postStatus)) {
                    System.out.println("Order placed successfully.");
                    Platform.runLater(() -> {
                        initialiseOrder();
                        onRestartButtonClick();
                    });
                } else {
                    Platform.runLater(() -> showErrorDialog(Exceptions.returnStatus("Order could not be placed:", postStatus)));
                }
                return null;
            }
        };

        executorService.submit(sendOrderTask);
    }

    @FXML
    protected void onCustomizeButtonClick() {
        if (selectedItem == null) {
            showErrorDialog("No item selected to customize.");
            return;
        }

        selectedItem.setAddNoteRequested(true);

        Platform.runLater(() -> orderitemslistview.refresh());
    }

    @FXML
    protected void onItemCorrectButtonClick() {
        if (!actionStack.isEmpty()) {
            actionStack.pop().run();
            updateOrderSummary();
        }
    }

    @FXML
    protected void onVoidItemButtonClick() {
        if (selectedItem != null) {
            MenuItem menuItem = selectedItem.getMenuItem();
            int quantity = orderItems.get(menuItem);
            orderItems.remove(menuItem);
            itemNotes.remove(menuItem);

            actionStack.push(() -> {
                orderItems.put(menuItem, quantity);
                updateOrderSummary();
            });

            orderAPI.deleteOrderItem(orderID, (String) menuItem.getMetadataValue("itemId"), quantity);

            updateOrderSummary();
        }
    }

    private void updateOrderSummary() {
        Platform.runLater(() -> {
            labelManager.clearAllItems();
            orderTotal = 0.00;

            for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
                MenuItem menuItem = entry.getKey();
                int quantity = entry.getValue();
                Double price = (Double) menuItem.getMetadataValue("price");
                double total = price * quantity;
                orderTotal += total;

                List<String> notes = itemNotes.getOrDefault(menuItem, new ArrayList<>());
                OrderItem orderItem = new OrderItem(menuItem, quantity, notes);
                labelManager.addItem(orderItem);
            }
            totalprice.setText("$" + String.format("%.2f", orderTotal));
        });
    }

    @FXML
    protected void onRestartButtonClick() {
        Map<MenuItem, Integer> currentOrderItems = new HashMap<>(orderItems);
        Map<MenuItem, List<String>> currentItemNotes = new HashMap<>(itemNotes);

        actionStack.push(() -> {
            orderItems.putAll(currentOrderItems);
            itemNotes.putAll(currentItemNotes);
            updateOrderSummary();
        });

        orderItems.clear();
        itemNotes.clear();

        orderAPI.deleteOrder(orderID);
        initialiseOrder();
        updateOrderSummary();
    }

    @FXML
    protected void onFilterButtonClick() {
        populateButtonsForCurrentTab();
    }

    @FXML
    protected void onSettingsButtonClick() {
        // Handle settings button click
    }

    @FXML
    protected void onLogoutButtonClick() {
        GuiCommon.logout(logoutbutton);
    }

    protected void initsearch() {
        Task<Void> initSearchTask = new Task<>() {
            @Override
            protected Void call() {
                menuItems = dataStore.readMenuItems();
                queryMenuItems = new ArrayList<>(menuItems);
                Platform.runLater(() -> {
                    searchbuttonManager.clearAllButtons();
                    for (MenuItem menuItem : menuItems) {
                        String itemName = (String) menuItem.getMetadataValue("itemName");
                        String price = menuItem.getMetadataValue("price").toString();
                        searchbuttonManager.addButton(String.valueOf(id), itemName, "$" + price, menuItem);
                        id++;
                    }
                });
                return null;
            }
        };

        executorService.submit(initSearchTask);
    }

    private class DynamicButtonManager {
        private int buttonCount = 0;
        private final FlowPane buttonPane;
        private final Map<String, Button> buttons;

        private DynamicButtonManager(FlowPane buttonPane, DynamicLabelManager labelManager) {
            this.buttonPane = buttonPane;
            this.buttons = new HashMap<>();
        }

        private void addButton(String id, String itemName, String price, MenuItem menuItem) {
            Platform.runLater(() -> {
                buttonCount++;
                Button newButton = new Button(itemName);
                newButton.setId(id);
                newButton.setPrefSize(225, 120);
                newButton.setContentDisplay(ContentDisplay.BOTTOM);
                newButton.setMnemonicParsing(false);
                newButton.getStyleClass().add("item-button");

                Label priceLabel = new Label(price);
                priceLabel.getStyleClass().add("buttonprice-label");

                newButton.setGraphic(priceLabel);

                buttonPane.getChildren().add(newButton);
                buttons.put(itemName, newButton);

                newButton.setUserData(menuItem);
                newButton.setOnAction(e -> addtoorder(menuItem));
            });
        }

        private void addtoorder(MenuItem menuItem) {
            if (menuItem == null) {
                showErrorDialog("Menu item not found.");
                return;
            }

            orderItems.put(menuItem, orderItems.getOrDefault(menuItem, 0) + 1);

            actionStack.push(() -> {
                if (orderItems.get(menuItem) == 1) {
                    orderItems.remove(menuItem);
                    itemNotes.remove(menuItem);
                } else {
                    orderItems.put(menuItem, orderItems.get(menuItem) - 1);
                }
                updateOrderSummary();
            });

            updateOrderSummary();
        }

        private void clearAllButtons() {
            Platform.runLater(() -> {
                buttonPane.getChildren().clear();
                buttons.clear();
            });
        }
    }

    private record DynamicTabManager(TabPane tabPane) {

        private void adjustTabWidths() {
            double totalWidth = tabPane.getWidth();
            int numberOfTabs = tabPane.getTabs().size();

            if (numberOfTabs > 0) {
                double tabWidth = totalWidth / numberOfTabs;
                tabWidth = tabWidth - 20.5;
                tabPane.setTabMinWidth(tabWidth);
                tabPane.setTabMaxWidth(tabWidth);
            }
        }
    }

    private record DynamicLabelManager(ListView<OrderItem> listView) {

        private void addItem(OrderItem orderItem) {
            listView.getItems().add(orderItem);
        }

        private void clearAllItems() {
            listView.getItems().clear();
        }
    }

    public static class OrderItem {
        private final MenuItem menuItem;
        private final int quantity;
        private final List<String> specialRequests;
        private transient boolean addNoteRequested = false;

        public OrderItem(MenuItem menuItem, int quantity, List<String> specialRequests) {
            this.menuItem = menuItem;
            this.quantity = quantity;
            this.specialRequests = new ArrayList<>(specialRequests);
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getQuantity() {
            return quantity;
        }

        public List<String> getSpecialRequests() {
            return specialRequests;
        }

        public boolean isAddNoteRequested() {
            return addNoteRequested;
        }

        public void setAddNoteRequested(boolean addNoteRequested) {
            this.addNoteRequested = addNoteRequested;
        }
    }

    private class OrderItemCell extends ListCell<OrderItem> {

        private final VBox vbox;
        private final Label addNoteLabel;
        private TextField editingTextField;

        public OrderItemCell() {
            vbox = new VBox();
            vbox.getStyleClass().add("order-item-cell");
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            addNoteLabel = new Label("    + Add Note");
            addNoteLabel.setVisible(false);
            addNoteLabel.setManaged(false);

            addNoteLabel.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    addNoteInline(getItem(), vbox, addNoteLabel);
                }
            });

            vbox.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                addNoteLabel.setVisible(isNowHovered);
                addNoteLabel.setManaged(isNowHovered);
            });
        }

        @Override
        protected void updateItem(OrderItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                vbox.getChildren().clear();
                Label itemLabel = new Label(item.getQuantity() + " x " + item.getMenuItem().getMetadataValue("itemName"));
                vbox.getChildren().add(itemLabel);

                List<String> notes = item.getSpecialRequests();
                if (notes != null && !notes.isEmpty()) {
                    for (int i = 0; i < notes.size(); i++) {
                        String note = notes.get(i);
                        Label noteLabel = new Label("    * " + note);
                        int index = i;

                        noteLabel.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                editNoteInline(item, index, noteLabel);
                            }
                        });

                        vbox.getChildren().add(noteLabel);
                    }
                }

                vbox.getChildren().add(addNoteLabel);

                setText(null);
                setGraphic(vbox);

                if (item.isAddNoteRequested()) {
                    item.setAddNoteRequested(false);

                    Platform.runLater(() -> addNoteInline(item, vbox, addNoteLabel));
                }
            }
        }

        private void editNoteInline(OrderItem item, int noteIndex, Label noteLabel) {
            String currentNote = item.getSpecialRequests().get(noteIndex);
            TextField textField = new TextField(currentNote);
            textField.selectAll();

            textField.setOnAction(event -> finishEditing(item, noteIndex, textField));

            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    finishEditing(item, noteIndex, textField);
                }
            });

            int indexInVBox = vbox.getChildren().indexOf(noteLabel);
            vbox.getChildren().set(indexInVBox, textField);
            textField.requestFocus();
        }

        private void finishAdding(OrderItem item, VBox vbox, Label addNoteLabel, TextField textField) {
            if (Boolean.TRUE.equals(textField.getProperties().get("finished"))) {
                return;
            }
            textField.getProperties().put("finished", true);

            textField.setOnAction(null);
            textField.focusedProperty().removeListener((obs, wasFocused, isNowFocused) -> {
            });

            String newNote = textField.getText();
            if (newNote != null && !newNote.trim().isEmpty()) {
                if (!item.getSpecialRequests().contains(newNote)) {
                    item.getSpecialRequests().add(newNote);
                    itemNotes.put(item.getMenuItem(), new ArrayList<>(item.getSpecialRequests()));
                }

                Label noteLabel = new Label("    * " + newNote);
                int noteIndex = item.getSpecialRequests().size() - 1;

                noteLabel.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        editNoteInline(item, noteIndex, noteLabel);
                    }
                });

                int indexInVBox = vbox.getChildren().indexOf(textField);
                if (indexInVBox >= 0) {
                    vbox.getChildren().set(indexInVBox, noteLabel);
                } else {
                    vbox.getChildren().add(noteLabel);
                }

            } else {
                int indexInVBox = vbox.getChildren().indexOf(textField);
                if (indexInVBox >= 0) {
                    vbox.getChildren().remove(indexInVBox);
                }

            }
            if (!vbox.getChildren().contains(addNoteLabel)) {
                vbox.getChildren().add(addNoteLabel);
            }
        }

        private void finishEditing(OrderItem item, int noteIndex, TextField textField) {
            if (Boolean.TRUE.equals(textField.getProperties().get("finished"))) {
                return;
            }
            textField.getProperties().put("finished", true);

            textField.setOnAction(null);
            textField.focusedProperty().removeListener((obs, wasFocused, isNowFocused) -> {
            });

            String newNote = textField.getText();
            if (newNote == null || newNote.trim().isEmpty()) {
                item.getSpecialRequests().remove(noteIndex);
            } else {
                item.getSpecialRequests().set(noteIndex, newNote);
            }
            itemNotes.put(item.getMenuItem(), new ArrayList<>(item.getSpecialRequests()));

            if (newNote != null && !newNote.trim().isEmpty()) {
                Label updatedNoteLabel = new Label("    * " + newNote);
                updatedNoteLabel.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        editNoteInline(item, noteIndex, updatedNoteLabel);
                    }
                });

                int indexInVBox = vbox.getChildren().indexOf(textField);
                if (indexInVBox >= 0) {
                    vbox.getChildren().set(indexInVBox, updatedNoteLabel);
                } else {
                    vbox.getChildren().add(updatedNoteLabel);
                }
            } else {
                int indexInVBox = vbox.getChildren().indexOf(textField);
                if (indexInVBox >= 0) {
                    vbox.getChildren().remove(indexInVBox);
                }
            }
        }

        private void addNoteInline(OrderItem item, VBox vbox, Label addNoteLabel) {
            TextField textField = new TextField();
            textField.setPromptText("Enter note");

            textField.setOnAction(event -> finishAdding(item, vbox, addNoteLabel, textField));

            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    finishAdding(item, vbox, addNoteLabel, textField);
                }
            });

            int indexInVBox = vbox.getChildren().indexOf(addNoteLabel);
            vbox.getChildren().set(indexInVBox, textField);
            textField.requestFocus();
        }
    }

    @FXML
    public void stop() {
        scheduler.shutdown();
        executorService.shutdown();
    }
}

