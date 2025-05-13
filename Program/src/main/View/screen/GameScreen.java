package main.View.screen;

import javafx.scene.control.ScrollPane;
import main.Controller.Controller;
import main.View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        actionsBox.getChildren().add(new Label("--- Actions/Inventory ---"));
        // TODO: Add buttons for Attack, Use Item, Inventory display
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
        addGameMessage("Welcome to the Dungeon!");


        getStage().setScene(gameScene);
        getStage().setTitle("Dungeon Dive - In Progress");
        getStage().show();

        // Notify GameController that the game screen is ready if needed
        // e.g., getController().getGameController().onGameScreenReady(this);
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
        // TODO: Implement logic to draw the dungeon map in myDungeonViewPane
        // This will involve iterating through getController().getDungeon().getRooms()
        // and creating Labels or other Nodes for each room.
        // Example: myDungeonViewPane.add(new Label("[R]"), x, y);
        // The GameController should ideally handle the specifics of this.
        myDungeonViewPane.getChildren().clear(); // Clear old view
        Label placeholder = new Label("Dungeon map will appear here.\n(GameController should draw this)");
        myDungeonViewPane.add(placeholder, 0,0);
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
}
