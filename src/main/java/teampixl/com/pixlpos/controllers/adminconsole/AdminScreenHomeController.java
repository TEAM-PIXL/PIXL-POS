package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.api.*;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;

import teampixl.com.pixlpos.models.Stock;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


import javafx.scene.layout.HBox;

public class AdminScreenHomeController {
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the home admin screen of the application.
    ====================================================================================================================================================================================*/
    private final UserStack userStack = UserStack.getInstance();
    Users currentuser = userStack.getCurrentUser();
    String firstName = currentuser.getMetadata().metadata().get("first_name").toString();
    private final StockAPI stockAPI = StockAPI.getInstance();
    private final IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
    private final DataStore dataStore = DataStore.getInstance();
    /*
    Shared Components
     */

    @FXML
    private Label greeting;
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

    @FXML
    private ListView<HBox> noteslistview;
    @FXML
    private ListView<HBox> stockalertlistview;
    @FXML
    private ListView<HBox> userloglistview;
    @FXML
    private ListView<HBox> topsellerslistview;

    @FXML
    private PieChart piechart;

    @FXML
    private Label piechartlabel;

    AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    };

    protected void addtooltips() {
        Tooltip hometooltip = new Tooltip("home");
        hometooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(homebutton, hometooltip);

        Tooltip userstooltip = new Tooltip("users");
        userstooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(usersbutton, userstooltip);

        Tooltip menutooltip = new Tooltip("menu");
        menutooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(menubutton, menutooltip);

        Tooltip stocktooltip = new Tooltip("stock");
        stocktooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(stockbutton, stocktooltip);

        Tooltip analyticstooltip = new Tooltip("analytics");
        analyticstooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(analyticsbutton, analyticstooltip);

        Tooltip logouttooltip = new Tooltip("logout");
        logouttooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(logoutbutton, logouttooltip);
    }

    @FXML
    public void initialize() {
        datetime.start();
        greeting.setText("Hello, " + firstName);
        addtooltips();

        /* WIDGET CODE */
        noteslistview.getItems().clear();
        addNoteToListView(noteslistview, "1", "John Doe", "This is a note");
        addNoteToListView(noteslistview, "2", "Jane Doe", "This is a note");
        addNoteToListView(noteslistview, "3", "Michael Doe", "This is a note");
        stockalertlistview.getItems().clear();
        calculateStockWidget();
        userloglistview.getItems().clear();
        adduserlogToListView(userloglistview, "1", "13/12/2020", "John Smith", "10:02am", "0hrs 6mins");
        adduserlogToListView(userloglistview, "2", "12/12/2020", "Jane Doe", "9:02am", "5hrs 6mins");
        adduserlogToListView(userloglistview, "3", "14/12/2020", "Michael Smith", "11:02am", "4hrs 6mins");
        topsellerslistview.getItems().clear();
        addTopSellerToListView(topsellerslistview, "1", "#1", "Teriyaki Salmon Burger");
        addTopSellerToListView(topsellerslistview, "2", "#2", "Chicken Caesar Salad");
        addTopSellerToListView(topsellerslistview, "3", "#3", "Pulled Pork Sandwich");
        PaymentPieChartController paymentPieChartController = new PaymentPieChartController();
        paymentPieChartController.initialize();
    }


    @FXML
    protected void onUsersButtonClick() {
        // Handle exit button click

        Stage stage = (Stage) usersbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
//        double width = stage.getScene().getWindow().getWidth();
//        double height = stage.getScene().getWindow().getHeight();
//        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage,width,height);
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
    protected void onSettingsButtonClick() {
        // Handle settings button click
    }

    @FXML
    protected void onAddNoteButtonClick() {
        // Handle exit button click
        addNoteToListView(noteslistview, "1", "John Doe", "This is a note");
    }


    @FXML
    protected void onLogoutButtonClick() {
        GuiCommon.logout(logoutbutton);
    }

    private void calculateStockWidget() {
        List<Stock> stockItems = dataStore.readStock();
        stockItems.sort(Comparator.comparing(stock -> (Double) stock.getData().get("numeral")));
        List<Stock> leastInStockItems = stockItems.stream().limit(3).collect(Collectors.toList());

        stockalertlistview.getItems().clear();
        for (Stock stock : leastInStockItems) {
            addStockAlertListView(stockalertlistview, stock.getMetadataValue("stock_id").toString(),
                    ingredientsAPI.reverseKeySearch(stock.getMetadataValue("ingredient_id").toString()),
                    (Stock.StockStatus) stock.getMetadataValue("stockStatus"),
                    (Boolean) stock.getMetadataValue("onOrder"));
        }
    }


    /* =========================================================Start of dynamic Widget code========================================================= */

    public void addNoteToListView(ListView<HBox> listView, String id, String user, String note) {
        try {
            // Load HBox from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/notesdynamic.fxml"));
            HBox hbox = loader.load();

            // Set the ID of the HBox
            hbox.setId(id);

            // Get the controller associated with the FXML (if you have one)
            // Optionally, set values via the controller, or directly access the fields
            // Example: If you have IDs set in FXML for the labels, you can access them like this:

            Label datefield = (Label) hbox.lookup("#datelabel");
            Label userfield = (Label) hbox.lookup("#userlabel");
            Label notefield = (Label) hbox.lookup("#notelabel");

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = now.format(formatter);

            // Set values dynamically
            datefield.setText(formattedDate);
            userfield.setText(user);
            notefield.setText(note);
            // Set action handlers for buttons (if they exist in your FXML)

            Button removebutton = (Button) hbox.lookup("#removenotebutton");
            removebutton.setOnAction(event -> onRemoveNoteButtonClick(event, id));

            Tooltip tooltip2 = new Tooltip("Remove Item");
            removebutton.setTooltip(tooltip2);

            // Add the HBox to the ListView
            listView.getItems().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void onRemoveNoteButtonClick(javafx.event.ActionEvent event, String id) {
        // Implement remove menu item logic here
        ObservableList<HBox> items = noteslistview.getItems(); // Get the items of the ListView

        // Loop through the list to find the HBox with the matching ID
        for (int i = 0; i < items.size(); i++) {
            HBox hbox = items.get(i);

            if (id.equals(hbox.getId())) {  // Compare the ID of the HBox
                try {
                    items.remove(i);  // Remove the HBox at the found index
                    // Handle delete user button click

                } catch (Exception e) {

                }
                break;            // Exit the loop once the HBox is removed
            }
        }
    }


    public void addStockAlertListView(ListView<HBox> listView, String id, String name, Stock.StockStatus status, boolean onOrder) {
        try {
            // Load HBox from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/stockalertdynamic.fxml"));
            HBox hbox = loader.load();

            // Set the ID of the HBox
            hbox.setId(id);

            // Get the controller associated with the FXML (if you have one)
            Label namefield = (Label) hbox.lookup("#stocknamelabel");
            Label levelfield = (Label) hbox.lookup("#stocklevellabel");
            Label statusfield = (Label) hbox.lookup("#stockstatuslabel");


            //get Level and Status from database based off name

            // Set values dynamically
            namefield.setText(name);

            //set depending on database

            if(status == Stock.StockStatus.INSTOCK) {
                levelfield.setText("In Stock");
                levelfield.getStyleClass().add("stockalert-level-in");
            } else if(status == Stock.StockStatus.LOWSTOCK) {
                levelfield.setText("Low Stock");
                levelfield.getStyleClass().add("stockalert-level-low");
            } else if(status == Stock.StockStatus.NOSTOCK) {
                levelfield.setText("No Stock");
                levelfield.getStyleClass().add("stockalert-level-no");

            }

            if(onOrder) {
                statusfield.setText("ON-ORDER");
                statusfield.getStyleClass().add("stockalert-status-on");
            } else {
                statusfield.setText("NOT-ORDERED");
                statusfield.getStyleClass().add("stockalert-status-not");
            }

            // Add the HBox to the ListView
            listView.getItems().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void adduserlogToListView(ListView<HBox> listView, String id, String date,String user ,String logged, String active) {
        try {
            // Load HBox from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/userlogdynamic.fxml"));
            HBox hbox = loader.load();

            // Set the ID of the HBox
            hbox.setId(id);

            // Get the controller associated with the FXML (if you have one)
            // Optionally, set values via the controller, or directly access the fields
            // Example: If you have IDs set in FXML for the labels, you can access them like this:

            Label datefield = (Label) hbox.lookup("#datelabel");
            Label userfield = (Label) hbox.lookup("#userlabel");
            Label loggedfield = (Label) hbox.lookup("#loggedlabel");
            Label activefield = (Label) hbox.lookup("#activelabel");


            // Set values dynamically
            datefield.setText(date);
            userfield.setText(user);
            loggedfield.setText(logged);
            activefield.setText(active);
            // Set action handlers for buttons (if they exist in your FXML)

            // Add the HBox to the ListView
            listView.getItems().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTopSellerToListView(ListView<HBox> listView, String id, String rank,String name)  {
        try {
            // Load HBox from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/topsellersdynamic.fxml"));
            HBox hbox = loader.load();

            // Set the ID of the HBox
            hbox.setId(id);

            // Get the controller associated with the FXML (if you have one)
            // Optionally, set values via the controller, or directly access the fields
            // Example: If you have IDs set in FXML for the labels, you can access them like this:

            Label rankfield = (Label) hbox.lookup("#ranklabel");
            Label namefield = (Label) hbox.lookup("#namelabel");



            // Set values dynamically
            rankfield.setText(rank);
            namefield.setText(name);

            // Set action handlers for buttons (if they exist in your FXML)

            // Add the HBox to the ListView
            listView.getItems().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class PaymentPieChartController {

        @FXML
        public void initialize() {
            fillPieChartWithRandomData();
            piechart.setLegendVisible(false);
            piechartlabel.setText("Total Sales: $430.45");
            piechartlabel.getStyleClass().add("notes-labeltext");
        }

        public void fillPieChartWithRandomData() {
            Random random = new Random();
            int cash = random.nextInt(100);
            int card = random.nextInt(100);
            int mobile = random.nextInt(100);
            int total = cash + card + mobile;

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Cash", cash),
                    new PieChart.Data("Card", card),
                    new PieChart.Data("Mobile", mobile)
            );

            piechart.setData(pieChartData);
            addDataLabels(pieChartData, total);
        }

        public void setPieChartData(Map<String, Integer> data) {
            int cash = data.getOrDefault("Cash", 0);
            int card = data.getOrDefault("Card", 0);
            int mobile = data.getOrDefault("Mobile", 0);
            int total = cash + card + mobile;

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Cash", cash),
                    new PieChart.Data("Card", card),
                    new PieChart.Data("Mobile", mobile)
            );

            piechart.setData(pieChartData);
            addDataLabels(pieChartData, total);
        }

        private void addDataLabels(ObservableList<PieChart.Data> pieChartData, int total) {
            for (PieChart.Data data : pieChartData) {
                String percentage = String.format("%.1f%%", (data.getPieValue() / total) * 100);
                data.nameProperty().set(data.getName() + " " + percentage);
            }
        }
    }
}
