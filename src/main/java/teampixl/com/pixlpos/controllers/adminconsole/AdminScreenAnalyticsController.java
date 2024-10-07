package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.UsersAPI;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Button;

import java.util.Random;
import java.util.Random;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.Users;
import javafx.scene.layout.HBox;

public class AdminScreenAnalyticsController
{
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the home admin screen of the application.
    ====================================================================================================================================================================================*/
    private final UserStack userStack = UserStack.getInstance();
    Users currentuser = userStack.getCurrentUser();
    String firstName = currentuser.getMetadata().metadata().get("first_name").toString();
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

    @FXML
    private ScatterChart<Integer,Double> chart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button updatebutton;
    @FXML
    private TextField dailyordernumber;
    @FXML
    private TextField monthlyordernumber;

    @FXML
    private ListView<HBox> completedorders;

    @FXML
    private TextField totaldailysales;
    @FXML
    private TextField totalmonthlysales;





    AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    };




    public class ScatterChartController {

        private boolean showingDailyData = true; // Track current data type

        public void initialize() {
            // Set default labels for the axes
            xAxis.setLabel("Hour of the Day");
            yAxis.setLabel("Total Profit ($)");

            // Load the daily data initially
            fillWithDailyData();
        }

        // Method to toggle between daily and monthly data
        @FXML
        private void toggleData() {
            chart.getData().clear(); // Clear existing data
            if (showingDailyData) {
                // If currently showing daily data, switch to monthly data
                xAxis.setLabel("Day of the Month");
                fillWithMonthlyData();
            } else {
                // If currently showing monthly data, switch to daily data
                xAxis.setLabel("Hour of the Day");
                fillWithDailyData();
            }
            showingDailyData = !showingDailyData; // Toggle the flag
        }

        // Method to fill the chart with daily data
        private void fillWithDailyData() {
            XYChart.Series<Integer, Double> series = new XYChart.Series<>();
            series.setName("Total Profit - Daily");

            Random random = new Random();

            // Generate profit for 24 hours (from hour 0 to 23)
            for (int hour = 0; hour < 24; hour++) {
                double profit = 400 + random.nextGaussian() * 50; // Average $400, standard deviation $50
                series.getData().add(new XYChart.Data<>(hour, profit));
            }

            chart.getData().add(series); // Add the series to the chart
        }

        // Method to fill the chart with monthly data
        private void fillWithMonthlyData() {
            XYChart.Series<Integer, Double> series = new XYChart.Series<>();
            series.setName("Total Profit - Monthly");

            Random random = new Random();

            // Generate profit for 30 days (from day 1 to 30)
            for (int day = 1; day <= 30; day++) {
                double profit;
                if (day <= 15) {
                    profit = 400 + random.nextGaussian() * 50; // First half: Average $400
                } else {
                    profit = 100 + random.nextGaussian() * 30; // Second half: Average $100
                }

                series.getData().add(new XYChart.Data<>(day, profit));
            }

            chart.getData().add(series); // Add the series to the chart
        }
    }
    ScatterChartController scatterChartController = new ScatterChartController();


















    @FXML
    public void initialize() {
        datetime.start();
        scatterChartController.initialize();
        greeting.setText("Hello, " + firstName);
    }





    @FXML
    protected void onUpdateDataButtonClick() {
        scatterChartController.toggleData();

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
        GuiCommon.logout(logoutbutton);
    }
}
