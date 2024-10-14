package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.*;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Stock;
import teampixl.com.pixlpos.models.Users;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONTokener;

public class AdminScreenHomeController {
    private final UserStack userStack = UserStack.getInstance();
    private final Users currentUser = userStack.getCurrentUser();
    private final String firstName = currentUser.getMetadata().metadata().get("first_name").toString();
    private final StockAPI stockAPI = StockAPI.getInstance();
    private final IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
    private final DataStore dataStore = DataStore.getInstance();
    private final OrderAPI orderAPI = OrderAPI.getInstance();

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
    @FXML
    private ImageView weatherIcon;
    @FXML
    private Label temperatureLabel;

    private final AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    };

    @FXML
    public void initialize() {
        initializeDependencies();
        datetime.start();
        greeting.setText("Hello, " + firstName);
        addTooltips();
        initializeWidgets();
        loadWeatherData();
    }

    private void initializeDependencies() {
        while (DataStore.getInstance() == null || StockAPI.getInstance() == null || IngredientsAPI.getInstance() == null || OrderAPI.getInstance() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addTooltips() {
        Tooltip hometooltip = new Tooltip("home");
        hometooltip.setShowDelay(Duration.millis(250));
        Tooltip.install(homebutton, hometooltip);

        Tooltip userstooltip = new Tooltip("users");
        userstooltip.setShowDelay(Duration.millis(250));
        Tooltip.install(usersbutton, userstooltip);

        Tooltip menutooltip = new Tooltip("menu");
        menutooltip.setShowDelay(Duration.millis(250));
        Tooltip.install(menubutton, menutooltip);

        Tooltip stocktooltip = new Tooltip("stock");
        stocktooltip.setShowDelay(Duration.millis(250));
        Tooltip.install(stockbutton, stocktooltip);

        Tooltip analyticstooltip = new Tooltip("analytics");
        analyticstooltip.setShowDelay(Duration.millis(250));
        Tooltip.install(analyticsbutton, analyticstooltip);

        Tooltip logouttooltip = new Tooltip("logout");
        logouttooltip.setShowDelay(Duration.millis(250));
        Tooltip.install(logoutbutton, logouttooltip);
    }

    private void initializeWidgets() {
        noteslistview.getItems().clear();
        addNoteToListView("1", "John Doe", "This is a note");
        addNoteToListView("2", "Jane Doe", "This is a note");
        addNoteToListView("3", "Michael Doe", "This is a note");

        stockalertlistview.getItems().clear();
        calculateStockWidget();

        userloglistview.getItems().clear();
        addUserLogToListView("1", "13/12/2020", "John Smith", "10:02am", "0hrs 6mins");
        addUserLogToListView("2", "12/12/2020", "Jane Doe", "9:02am", "5hrs 6mins");
        addUserLogToListView("3", "14/12/2020", "Michael Smith", "11:02am", "4hrs 6mins");

        topsellerslistview.getItems().clear();
        findTop5Sellers();

        PaymentPieChartController paymentPieChartController = new PaymentPieChartController();
        paymentPieChartController.initialize();
    }

    public void loadWeatherData() {
        new Thread(() -> {
            try {
                WeatherData weatherData = fetchWeatherData();
                if (weatherData != null) {
                    Platform.runLater(() -> {
                        String iconFilename = getWeatherIcon(weatherData.condition());
                        if (iconFilename != null) {
                            Image image = new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/weathericons/" + iconFilename));
                            weatherIcon.setImage(image);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



    private WeatherData fetchWeatherData() {
        String apiKey = "4e4e5534d5ac03b5ec33c8b2f450ba8d";
        String location = "Brisbane,AU";
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&units=metric&appid=" + apiKey;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream responseStream = connection.getInputStream();
            JSONTokener tokener = new JSONTokener(responseStream);
            JSONObject jsonObject = new JSONObject(tokener);

            double temperature = jsonObject.getJSONObject("main").getDouble("temp");
            String condition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            System.out.println("Temperature: " + temperature + "°C, Condition: " + condition);
            return new WeatherData(temperature, condition);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getWeatherIcon(String condition) {
        Map<String, String> conditionIconMap = new HashMap<>();
        conditionIconMap.put("Clear", "sunny_icon.png");
        conditionIconMap.put("Clouds", "cloudy_icon.png");
        conditionIconMap.put("Rain", "cloudy_rain_icon.png");
        conditionIconMap.put("Drizzle", "cloud_drizzel_rain_icon.png");
        conditionIconMap.put("Thunderstorm", "cloud_storm_icon.png");
        conditionIconMap.put("Snow", "clouds_snow_icon.png");
        conditionIconMap.put("Fog", "cloudy_foggy_icon.png");
        conditionIconMap.put("Mist", "cloudy_foggy_icon.png");
        conditionIconMap.put("Haze", "cloudy_foggy_icon.png");

        return conditionIconMap.getOrDefault(condition, "sunny_icon.png");
    }

    @FXML
    protected void onUsersButtonClick() {
        Stage stage = (Stage) usersbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
    }

    @FXML
    protected void onMenuButtonClick() {
        Stage stage = (Stage) menubutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_MENU_FXML, GuiCommon.ADMIN_SCREEN_MENU_TITLE, stage);
    }

    @FXML
    protected void onHomeButtonClick() {
        Stage stage = (Stage) homebutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, stage);
    }

    @FXML
    protected void onStockButtonClick() {
        Stage stage = (Stage) stockbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_STOCK_FXML, GuiCommon.ADMIN_SCREEN_STOCK_TITLE, stage);
    }

    @FXML
    protected void onAnalyticsButtonClick() {
        Stage stage = (Stage) analyticsbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_ANALYTICS_FXML, GuiCommon.ADMIN_SCREEN_ANALYTICS_TITLE, stage);
    }

    @FXML
    protected void onLogoutButtonClick() {
        GuiCommon.logout(logoutbutton);
    }

    @FXML
    protected void onSettingsButtonClick() {

    }

    @FXML
    protected void onAddNoteButtonClick() {
        addNoteToListView("1", "John Doe", "This is a note");
    }

    private void calculateStockWidget() {
        List<Stock> stockItems = dataStore.readStock();
        stockItems.sort(Comparator.comparing(stock -> (Double) stock.getData().get("numeral")));
        List<Stock> leastInStockItems = stockItems.stream().limit(3).toList();

        stockalertlistview.getItems().clear();
        for (Stock stock : leastInStockItems) {
            addStockAlertToListView(
                    stock.getMetadataValue("stock_id").toString(),
                    ingredientsAPI.reverseKeySearch(stock.getMetadataValue("ingredient_id").toString()),
                    (Stock.StockStatus) stock.getMetadataValue("stockStatus"),
                    (Boolean) stock.getMetadataValue("onOrder")
            );
        }
    }

    private void findTop5Sellers() {
        List<MenuItem> menuItems = dataStore.readMenuItems();
        menuItems.sort(Comparator.comparing(menuItem -> {
            Integer amountOrdered = (Integer) menuItem.getDataValue("amount_ordered");
            return amountOrdered != null ? amountOrdered : 0;
        }));
        List<MenuItem> top5Sellers = menuItems.stream().limit(5).toList();

        topsellerslistview.getItems().clear();
        int rank = 1;
        for (MenuItem menuItem : top5Sellers) {
            String rankString = "#" + rank;
            addTopSellerToListView(
                    menuItem.getMetadataValue("id").toString(),
                    rankString,
                    menuItem.getMetadataValue("itemName").toString()
            );
            rank++;
        }
    }

    public void addNoteToListView(String id, String user, String note) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/notesdynamic.fxml"));
            HBox hbox = loader.load();
            hbox.setId(id);

            Label datefield = (Label) hbox.lookup("#datelabel");
            Label userfield = (Label) hbox.lookup("#userlabel");
            Label notefield = (Label) hbox.lookup("#notelabel");

            String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datefield.setText(formattedDate);
            userfield.setText(user);
            notefield.setText(note);

            Button removebutton = (Button) hbox.lookup("#removenotebutton");
            removebutton.setOnAction(event -> onRemoveNoteButtonClick(event, id));
            Tooltip tooltip = new Tooltip("Remove Item");
            removebutton.setTooltip(tooltip);

            noteslistview.getItems().add(hbox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onRemoveNoteButtonClick(ActionEvent event, String id) {
        ObservableList<HBox> items = noteslistview.getItems();
        items.removeIf(hbox -> id.equals(hbox.getId()));
    }

    public void addStockAlertToListView(String id, String name, Stock.StockStatus status, boolean onOrder) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/stockalertdynamic.fxml"));
            HBox hbox = loader.load();
            hbox.setId(id);

            Label namefield = (Label) hbox.lookup("#stocknamelabel");
            Label levelfield = (Label) hbox.lookup("#stocklevellabel");
            Label statusfield = (Label) hbox.lookup("#stockstatuslabel");

            namefield.setText(name);

            switch (status) {
                case INSTOCK -> {
                    levelfield.setText("In Stock");
                    levelfield.getStyleClass().add("stockalert-level-in");
                }
                case LOWSTOCK -> {
                    levelfield.setText("Low Stock");
                    levelfield.getStyleClass().add("stockalert-level-low");
                }
                case NOSTOCK -> {
                    levelfield.setText("No Stock");
                    levelfield.getStyleClass().add("stockalert-level-no");
                }
            }

            if (onOrder) {
                statusfield.setText("ON-ORDER");
                statusfield.getStyleClass().add("stockalert-status-on");
            } else {
                statusfield.setText("NOT-ORDERED");
                statusfield.getStyleClass().add("stockalert-status-not");
            }

            stockalertlistview.getItems().add(hbox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUserLogToListView(String id, String date, String user, String logged, String active) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/userlogdynamic.fxml"));
            HBox hbox = loader.load();
            hbox.setId(id);

            Label datefield = (Label) hbox.lookup("#datelabel");
            Label userfield = (Label) hbox.lookup("#userlabel");
            Label loggedfield = (Label) hbox.lookup("#loggedlabel");
            Label activefield = (Label) hbox.lookup("#activelabel");

            datefield.setText(date);
            userfield.setText(user);
            loggedfield.setText(logged);
            activefield.setText(active);

            userloglistview.getItems().add(hbox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTopSellerToListView(String id, String rank, String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/homedynamics/topsellersdynamic.fxml"));
            HBox hbox = loader.load();
            hbox.setId(id);

            Label rankfield = (Label) hbox.lookup("#ranklabel");
            Label namefield = (Label) hbox.lookup("#namelabel");

            rankfield.setText(rank);
            namefield.setText(name);

            topsellerslistview.getItems().add(hbox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class PaymentPieChartController {
        @FXML
        private void initialize() {
            double totalAmount = fillPieChartWithData();
            piechart.setLegendVisible(false);
            piechartlabel.setText(String.format("Total Sales: $%.2f", totalAmount));
            piechartlabel.getStyleClass().add("notes-labeltext");
        }

        private double fillPieChartWithData() {
            orderAPI.reloadOrders();
            List<Order> orders = orderAPI.getOrders();

            double totalAmount = 0.0;
            double cashAmount = 0.0;
            double cardAmount = 0.0;
            double mobileAmount = 0.0;

            for (Order order : orders) {
                if ("NOT_PAID".equals(order.getDataValue("payment_method").toString())) {
                    continue;
                }

                double orderTotal = (double) order.getDataValue("total");
                totalAmount += orderTotal;

                switch (order.getDataValue("payment_method").toString()) {
                    case "CASH" -> cashAmount += orderTotal;
                    case "CARD" -> cardAmount += orderTotal;
                    case "MOBILE" -> mobileAmount += orderTotal;
                }
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Cash", cashAmount),
                    new PieChart.Data("Card", cardAmount),
                    new PieChart.Data("Mobile", mobileAmount)
            );

            piechart.setData(pieChartData);
            addDataLabels(pieChartData, totalAmount);
            return totalAmount;
        }

        private void addDataLabels(ObservableList<PieChart.Data> pieChartData, double total) {
            for (PieChart.Data data : pieChartData) {
                String percentage = String.format("%.1f%%", (data.getPieValue() / total) * 100);
                data.nameProperty().set(data.getName() + " " + percentage);
            }
        }
    }

    private record WeatherData(double temperature, String condition) {
    }
}

