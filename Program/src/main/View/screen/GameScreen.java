package main.View.screen;

import javafx.event.ActionEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import main.Controller.Controller;
import main.Model.element.Item;
import main.Model.util.Point;
import main.View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Class for the GameScreen.
 * This is where the main gameplay (dungeon view, player stats, etc.) is displayed.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class GameScreen extends Screen {

    private Label myPlayerHealthLabel;
    private Label myPlayerAttackLabel;
    private Label myPlayerGoldLabel;
    private Label myPillarsLabel;
    private GridPane myDungeonViewPane; // For displaying the dungeon map
    private VBox myMessagesArea;      // For displaying game messages

    /**
     * Constructor for GameScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public GameScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController); // Pass both to the superclass (Screen)
    }

    /**
     * showScreen for the GameScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 10;");
        Scene gameScene = new Scene(root, 800, 600); // Potentially larger screen for game
        // Apply a stylesheet (option in the future)
        //gameScene.getStylesheets().add(getClass().getResource("/main/View/css/style.css").toExternalForm());

        // Top: Game Info (e.g., Pause Button, Dungeon Level)
        Button pauseButton = new Button("Pause");
        setButtonSize(pauseButton);
        pauseButton.setOnAction(event -> getController().pauseGame(event, theUI));
        BorderPane.setAlignment(pauseButton, Pos.TOP_RIGHT);
        root.setTop(pauseButton);

        // Left: Player Stats
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        myPlayerHealthLabel = new Label("Health: ");
        myPlayerAttackLabel = new Label("Attack: "); // Or other relevant stats
        myPlayerGoldLabel = new Label("Gold: ");
        myPillarsLabel = new Label("Pillars: ");
        statsBox.getChildren().addAll(
                new Label("--- Player Stats ---"),
                myPlayerHealthLabel,
                myPlayerAttackLabel,
                myPlayerGoldLabel,
                myPillarsLabel
        );
        root.setLeft(statsBox);

        // Center: Dungeon View
        myDungeonViewPane = new GridPane();
        myDungeonViewPane.setAlignment(Pos.CENTER);
        myDungeonViewPane.setHgap(5);
        myDungeonViewPane.setVgap(5);
        myDungeonViewPane.setStyle("-fx-border-color: gray; -fx-padding: 10;");
        // The GameController will be responsible for populating/updating this pane
        root.setCenter(myDungeonViewPane);

        // Right: Inventory / Actions (Optional, or could be part of bottom)
        VBox actionsBox = new VBox(10);
        actionsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");

        // TODO: Add buttons for Attack, Use Item, Inventory display

        Button InventoryButton = new Button("Inventory");

        setButtonSize(InventoryButton);

        InventoryButton.setOnAction(event -> getController().getGameController().openInventory());


        Label movementLabel = new Label("--- Movement ---");
        Button northButton = new Button("North");
        Button westButton = new Button("West");
        Button eastButton = new Button("East");
        Button southButton = new Button("South");
        setButtonSize(northButton);
        setButtonSize(westButton);
        setButtonSize(eastButton);
        setButtonSize(southButton);

        // REMINDER CHANGE NORTH BUTTON BACK TO NORMAL, just testing a screen for now
        northButton.setOnAction(event -> theUI.showCombatTestDeleteMe());

        westButton.setOnAction(event -> {
            getController().getGameController().movePlayerWest();
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        });

        eastButton.setOnAction(event -> {
            getController().getGameController().movePlayerEast();
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        });

        southButton.setOnAction(event -> {
          getController().getGameController().movePlayerSouth();
          addGameMessage(getController().getGameController().getCurrentRoomDescription());
        });


        actionsBox.getChildren().addAll(new Label("--- Actions/Inventory ---"),
                InventoryButton, movementLabel, northButton, westButton, eastButton, southButton);

        actionsBox.setAlignment(Pos.TOP_CENTER);
        root.setRight(actionsBox);

        // Bottom: Message Log
        myMessagesArea = new VBox(5);
        myMessagesArea.setStyle("-fx-padding: 10; -fx-border-color: silver; -fx-border-width: 1; -fx-background-color: #f0f0f0;");
        myMessagesArea.setPrefHeight(100);
        ScrollPane messageScrollPane = new ScrollPane(myMessagesArea);
        messageScrollPane.setFitToWidth(true);
        root.setBottom(messageScrollPane);

        // Initial update of UI elements
        updatePlayerStats();
        updateDungeonView(); // This would be called by GameController
        addGameMessage("Welcome to the dungeon!");
        addGameMessage(getController().getGameController().getCurrentRoomDescription());


        getStage().setScene(gameScene);
        getStage().setTitle("Dungeon Dive - In Progress");
        getStage().show();

        // Notify GameController that the game screen is ready if needed
        // e.g., getController().getGameController().onGameScreenReady(this);
    }

    /**
     * Updates room description in the message area.
     */
    public void displayRoomDescription() {
        if (getController() != null && getController().getGameController() != null &&
                getController().getGameController().getCurrentRoomDescription() != null) {
            //myMessagesArea.getChildren()
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }

    /**
     * Updates the player stats displayed on the screen.
     * This should be called by the GameController when stats change.
     */
    public void updatePlayerStats() {
        if (getController() != null && getController().getPlayer() != null) {
            main.Model.character.Hero player = getController().getPlayer();
            myPlayerHealthLabel.setText("Health: " + player.getHealth() + " / " + player.getMaxHealth());
            // Assuming Hero has getAttackPower() or similar after stats refactor
            myPlayerAttackLabel.setText("Attack: " + player.getType().getBaseAttack()); // Example
            myPlayerGoldLabel.setText("Gold: " + player.getGold());
            if (getController().getDungeon() != null) {
                myPillarsLabel.setText("Pillars: " + player.getPillarsActivated() + " / " + getController().getDungeon().getTotalPillars());
            } else {
                myPillarsLabel.setText("Pillars: " + player.getPillarsActivated() + " / ?");
            }
        }
    }

    /**
     * Clears and redraws the dungeon view.
     * This would be called by the GameController.
     */
    public void updateDungeonView() {
        if (getController() == null || getController().getDungeon() == null || myDungeonViewPane == null) {
            return;
        }

        myDungeonViewPane.getChildren().clear(); // Clear old view

        // TODO: Implement logic to draw the dungeon map in myDungeonViewPane
        // This will involve iterating through getController().getDungeon().getRooms()
        // and creating Labels or other Nodes for each room.
        // Example: myDungeonViewPane.add(new Label("[R]"), x, y);
        // The GameController should ideally handle the specifics of this.

        String str = getController().getDungeon().getMapString(getController().getPlayer().getPosition());
        int strIndex = 0;
        for (int x = 0; x < getController().getDungeon().getHeight(); x++) {
            for (int y = 0; y < getController().getDungeon().getWidth(); y++) {

                Label currRoom = new Label(getController().getDungeon().getRoom(x, y).getRoomType().getDisplayName());
                myDungeonViewPane.add(currRoom, x, y);
            }
        }


//        Label placeholder = new Label("Dungeon map will appear here.\n(GameController should draw this)");
//        myDungeonViewPane.add(placeholder, 0,0);
    }

    /**
     * Adds a message to the game's message log area.
     * @param message The message string to add.
     */
    public void addGameMessage(String message) {
        if (myMessagesArea != null) {
            Label messageLabel = new Label(message);
            myMessagesArea.getChildren().add(messageLabel);
            // Auto-scroll to bottom
             if (myMessagesArea.getParent() instanceof ScrollPane) {
                 ((ScrollPane)myMessagesArea.getParent()).setVvalue(1.0);
             }
        }
    }
   // You were going to work on figuring out save game stuff, and look at other gameController stuff for wiring.
    // Also fix message area issue where full message not displayed after multiple displays. 

}
