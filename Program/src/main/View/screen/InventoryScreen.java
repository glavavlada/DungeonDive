package main.View.screen;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Model.element.Item;
import main.View.GameUI;

public class InventoryScreen {

    public InventoryScreen() {

    }

    public void showScreen(GameUI theUI) {
        Stage inventoryStage = new Stage();
        VBox items = new VBox(10);
        Scene inventoryScene = new Scene(items, 200, 300);

//        // This will be updated later to erase items from view after use.
//        for (Item item : theInventory) {
//            HBox currHBox = new HBox(15);
//            Button b = new Button("Use Item");
//            b.setOnAction(event -> getController().getPlayer().useItem(item));
//            currHBox.getChildren().addAll(new Label(item.toString()),
//                    new Button("Use Item"));
//            items.getChildren().add(currHBox);
//        }

        // Test items here until program running and can actually store items in inventory.
        for (int i = 0; i < 3; i++) {
            Button b = new Button("Use Item");
            HBox currHBox = new HBox(15);
            currHBox.getChildren().addAll(new Label("Test Item"),
                    b);
            items.getChildren().add(currHBox);
        }


        inventoryStage.setTitle("Inventory");
        inventoryStage.setScene(inventoryScene);
        inventoryStage.show();
    }
}
