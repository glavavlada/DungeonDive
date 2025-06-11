package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.Model.element.Item;
import main.View.GameUI;

import java.util.List;

public class InventoryScreen extends Screen {
    private ListView<String> myInventoryList;
    private Label mySelectedItemLabel;
    private Label myItemDescriptionLabel;
    private Label myInstructionsLabel;
    private Button myUseButton;
    private Button myCloseButton;
    private int mySelectedIndex = 0;

    public InventoryScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
        setupInventoryScreen();
    }

    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = createInventoryLayout();
        Scene scene = new Scene(root, 800, 600);

        //update inventory when showing
        updateInventory();

        MY_PRIMARY_STAGE.setScene(scene);
        MY_PRIMARY_STAGE.setTitle("DungeonDive - Inventory");
        MY_PRIMARY_STAGE.show();

        // Request focus for key events
        root.requestFocus();
        System.out.println("Inventory screen displayed");
    }

    private BorderPane createInventoryLayout() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #1a1a1a;");

        // Title
        Label titleLabel = new Label("INVENTORY");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: gold;");

        // Instructions
        myInstructionsLabel = new Label("Use W/S or ↑/↓ to navigate • E/Enter to use item • I/Esc to close");
        myInstructionsLabel.setFont(Font.font("Arial", 12));
        myInstructionsLabel.setStyle("-fx-text-fill: #cccccc;");

        // Inventory list
        myInventoryList = new ListView<>();
        myInventoryList.setPrefHeight(300);
        myInventoryList.setPrefWidth(400);
        myInventoryList.setStyle(
            "-fx-background-color: #2d2d2d; " +
            "-fx-border-color: #555555; " +
            "-fx-border-width: 2px; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px;"
        );

        // Selected item info panel
        VBox itemInfoPanel = new VBox(10);
        itemInfoPanel.setPadding(new Insets(15));
        itemInfoPanel.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #555555; -fx-border-width: 2px;");
        itemInfoPanel.setPrefWidth(400);

        mySelectedItemLabel = new Label("No item selected");
        mySelectedItemLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        mySelectedItemLabel.setStyle("-fx-text-fill: gold;");

        myItemDescriptionLabel = new Label("Select an item to see its description");
        myItemDescriptionLabel.setFont(Font.font("Arial", 14));
        myItemDescriptionLabel.setStyle("-fx-text-fill: #cccccc;");
        myItemDescriptionLabel.setWrapText(true);
        myItemDescriptionLabel.setPrefHeight(60);

        itemInfoPanel.getChildren().addAll(mySelectedItemLabel, myItemDescriptionLabel);

        // Buttons
        myUseButton = new Button("Use Item (E)");
        myUseButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        myUseButton.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 5px;"
        );
        myUseButton.setOnAction(e -> useSelectedItem());

        myCloseButton = new Button("Close (I)");
        myCloseButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        myCloseButton.setStyle(
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 5px;"
        );
        myCloseButton.setOnAction(e -> closeInventory());

        // Layout assembly
        VBox topSection = new VBox(10);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20));
        topSection.getChildren().addAll(titleLabel, myInstructionsLabel);

        VBox centerSection = new VBox(15);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(0, 20, 0, 20));
        centerSection.getChildren().addAll(myInventoryList, itemInfoPanel);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));
        buttonBox.getChildren().addAll(myUseButton, myCloseButton);

        mainLayout.setTop(topSection);
        mainLayout.setCenter(centerSection);
        mainLayout.setBottom(buttonBox);

        // Add selection listener for mouse clicks
        myInventoryList.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null && newValue.intValue() >= 0) {
                    mySelectedIndex = newValue.intValue();
                    updateSelectedItemInfo();
                }
            }
        );

        return mainLayout;
    }

    private void setupInventoryScreen() {
        // This method can be used for any additional setup if needed
        // The main setup is now in createInventoryLayout()
    }

    public void updateInventory() {
        if (MY_CONTROLLER.getGameController() == null || MY_CONTROLLER.getPlayer() == null) {
            return;
        }

        List<Item> inventory = MY_CONTROLLER.getPlayer().getInventory();
        if (myInventoryList != null) {
            myInventoryList.getItems().clear();

            if (inventory.isEmpty()) {
                myInventoryList.getItems().add("Your inventory is empty");
                mySelectedItemLabel.setText("No items available");
                myItemDescriptionLabel.setText("Find items in the dungeon to fill your inventory!");
                myUseButton.setDisable(true);
                mySelectedIndex = -1;
            } else {
                // Ensure selected index is valid
                if (mySelectedIndex >= inventory.size()) {
                    mySelectedIndex = 0;
                }
                if (mySelectedIndex < 0) {
                    mySelectedIndex = 0;
                }

                for (int i = 0; i < inventory.size(); i++) {
                    Item item = inventory.get(i);
                    String prefix = (i == mySelectedIndex) ? "→ " : "   ";
                    String itemText = prefix + item.getName();
                    myInventoryList.getItems().add(itemText);
                }

                myUseButton.setDisable(false);
                updateSelectedItemInfo();

                // Update ListView selection to match our internal selection
                myInventoryList.getSelectionModel().select(mySelectedIndex);
            }
        }
    }

    private void updateSelectedItemInfo() {
        if (MY_CONTROLLER.getPlayer() == null) {
            return;
        }

        List<Item> inventory = MY_CONTROLLER.getPlayer().getInventory();
        if (!inventory.isEmpty() && mySelectedIndex >= 0 && mySelectedIndex < inventory.size()) {
            Item selectedItem = inventory.get(mySelectedIndex);
            mySelectedItemLabel.setText("Selected: " + selectedItem.getName());
            myItemDescriptionLabel.setText(selectedItem.getDescription());
        } else {
            mySelectedItemLabel.setText("No item selected");
            myItemDescriptionLabel.setText("");
        }
    }

    public void updateSelection(int newIndex) {
        if (MY_CONTROLLER.getPlayer() == null) {
            return;
        }

        List<Item> inventory = MY_CONTROLLER.getPlayer().getInventory();
        if (!inventory.isEmpty() && newIndex >= 0 && newIndex < inventory.size()) {
            mySelectedIndex = newIndex;
            updateInventory(); //refresh display with new selection
        }
    }

    private void useSelectedItem() {
        if (MY_CONTROLLER.getGameController() != null) {
            MY_CONTROLLER.getGameController().useSelectedItem();
        }
    }

    private void closeInventory() {
        if (MY_CONTROLLER.getGameController() != null) {
            MY_CONTROLLER.getGameController().closeInventory();
        }
    }

    //method to be called when screen becomes active
    public void onScreenActivated() {
        updateInventory();
    }
}
