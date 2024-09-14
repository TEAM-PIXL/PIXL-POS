package teampixl.com.pixlpos.controllers.cookconsole;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.DataStore;

 // https://www.educba.com/javafx-listview/ this website to learn about listview
public class CookScreenController extends GuiCommon {
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the cook screen of the application. It handles displaying the orders and converting orders from database Infomation to visual dockets
    ====================================================================================================================================================================================*/


    @FXML
    private Button refresh;
    @FXML
    private Button complete;
    @FXML
    private Button logout;
    @FXML
    private ListView<VBox> orderview;



    // this is the code i have for the order visual layout where items go in the gridpane and order details are in text objs
     //this may change with emilys code
     //consider switching to list views for our lists instead of gridpane? may be worth looking into

    /*
     <VBox alignment="TOP_CENTER" prefHeight="200.0" prefWidth="100.0" BorderPane.alignment="CENTER">
         <children>
            <Label alignment="CENTER" contentDisplay="RIGHT" text="Order#">
               <graphic>
                  <Text fx:id="ordernum" strokeType="OUTSIDE" strokeWidth="0.0" text="null">
                     <font>
                        <Font size="25.0" />
                     </font>
                  </Text>
               </graphic>
               <font>
                  <Font size="30.0" />
               </font>
            </Label>
            <HBox alignment="CENTER" prefHeight="48.0" prefWidth="524.0" spacing="30.0" style="-fx-border-color: black;">
               <children>
                  <Label contentDisplay="BOTTOM" style="-fx-border-color: black;" text="Time Ordered">
                     <graphic>
                        <Text fx:id="timeordered" strokeType="OUTSIDE" strokeWidth="0.0" text="null" />
                     </graphic>
                  </Label>
                  <Label contentDisplay="BOTTOM" style="-fx-border-color: black;" text="Table/Takeaway">
                     <graphic>
                        <Text fx:id="tablenum" strokeType="OUTSIDE" strokeWidth="0.0" text="null" />
                     </graphic>
                  </Label>
               </children>
            </HBox>
            <GridPane>
              <columnConstraints>
                <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
              </columnConstraints>
              <rowConstraints>
                <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
              </rowConstraints>
            </GridPane>
            <HBox alignment="CENTER" prefHeight="45.0" prefWidth="524.0" spacing="30.0" style="-fx-border-color: black;">
               <children>
                  <Label contentDisplay="BOTTOM" text="Time due">
                     <graphic>
                        <Text fx:id="timedue" strokeType="OUTSIDE" strokeWidth="0.0" text="null" />
                     </graphic>
                  </Label>
               </children>
            </HBox>
         </children>
      </VBox>
     */
}
