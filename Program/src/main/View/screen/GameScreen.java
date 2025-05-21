package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.Model.character.Monster;
import main.Model.dungeon.Room;
import main.Model.element.Item;
import main.Model.util.Point;
import main.Model.util.RoomType;
import main.View.GameUI;


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
    private BorderPane myRoomViewPane;      // For displaying the current room
    private GridPane myMinimapPane;         // For displaying the minimap
    private VBox myMessagesArea;            // For displaying game messages
    // Room view styling constants
    private static final int ROOM_VIEW_SIZE = 400;
    private static final int MINIMAP_SIZE = 80;
    private static final int MINIMAP_CELL_SIZE = 10;


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

        // Top: Game title and pause button
        BorderPane topPane = new BorderPane();

        // Add title to the top pane
        Label gameTitle = new Label("Dungeon Dive");
        gameTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topPane.setLeft(gameTitle);

        // Add pause button to top-right
        Button pauseButton = new Button("Pause");
        setButtonSize(pauseButton);
        pauseButton.setOnAction(event -> getController().pauseGame(event, theUI));
        topPane.setRight(pauseButton);

        // Add the topPane to the root
        root.setTop(topPane);

        // Create minimap
        myMinimapPane = new GridPane();
        myMinimapPane.setAlignment(Pos.CENTER);
        myMinimapPane.setHgap(1);
        myMinimapPane.setVgap(1);
        myMinimapPane.setPrefSize(MINIMAP_SIZE, MINIMAP_SIZE);
        myMinimapPane.setMaxSize(MINIMAP_SIZE, MINIMAP_SIZE);
        myMinimapPane.setStyle("-fx-background-color: black;");

        // Add minimap to top-right
        StackPane minimapContainer = new StackPane(myMinimapPane);
        minimapContainer.setMaxSize(MINIMAP_SIZE, MINIMAP_SIZE);
        minimapContainer.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        HBox topRightBox = new HBox(10);
        topRightBox.getChildren().addAll(minimapContainer, pauseButton);
        topRightBox.setAlignment(Pos.CENTER_RIGHT);
        topPane.setRight(topRightBox);

        // Create room view pane
        myRoomViewPane = new BorderPane();
        myRoomViewPane.setPrefSize(ROOM_VIEW_SIZE, ROOM_VIEW_SIZE);
        myRoomViewPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray; -fx-border-width: 2;");
        root.setCenter(myRoomViewPane);

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

        // Right: Inventory / Actions
        VBox actionsBox = new VBox(10);
        actionsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");

        Button interactButton = new Button("Interact");
        setButtonSize(interactButton);
        interactButton.setOnAction(event -> {
            MY_CONTROLLER.getGameController().interact();
            updatePlayerStats();
            updateRoomView();
            updateMinimapView();
        });

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

        northButton.setOnAction(event -> {
            getController().getGameController().movePlayerNorth();
            displayRoomDescription();
            updatePlayerStats();
            updateRoomView();
        });

        westButton.setOnAction(event -> {
            getController().getGameController().movePlayerWest();
            displayRoomDescription();
            updatePlayerStats();
            updateRoomView();
        });

        eastButton.setOnAction(event -> {
            getController().getGameController().movePlayerEast();
            displayRoomDescription();
            updatePlayerStats();
            updateRoomView();
        });

        southButton.setOnAction(event -> {
            getController().getGameController().movePlayerSouth();
            displayRoomDescription();
            updatePlayerStats();
            updateRoomView();
        });

        actionsBox.getChildren().addAll(new Label("--- Actions/Inventory ---"),
                interactButton, InventoryButton, movementLabel, northButton, westButton, eastButton, southButton);

        actionsBox.setAlignment(Pos.TOP_CENTER);
        root.setRight(actionsBox);

        // Bottom: Message Log
        myMessagesArea = new VBox(20);
        myMessagesArea.setStyle("-fx-padding: 10; -fx-border-color: silver; -fx-border-width: 1; -fx-background-color: #f0f0f0;");
        ScrollPane messageScrollPane = new ScrollPane(myMessagesArea);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.setPrefHeight(100);
        root.setBottom(messageScrollPane);

        // Initial update of UI elements
        updatePlayerStats();
        updateRoomView();
        updateMinimapView();

        addGameMessage("Welcome to the dungeon!");
        addGameMessage(getController().getGameController().getCurrentRoomDescription());

        getStage().setScene(gameScene);
        getStage().setTitle("Dungeon Dive - In Progress");
        getStage().show();
    }

    /**
     * Creates the room view pane that shows the current room in detail
     */
    private BorderPane createRoomViewPane() {
        BorderPane roomView = new BorderPane();
        roomView.setPrefSize(ROOM_VIEW_SIZE, ROOM_VIEW_SIZE);
        roomView.setStyle("-fx-background-color: #d3d3d3;"); // Light gray background for the room

        return roomView;
    }

    /**
     * Creates the minimap pane
     */
    private GridPane createMinimapPane() {
        GridPane minimap = new GridPane();
        minimap.setAlignment(Pos.CENTER);
        minimap.setHgap(1);
        minimap.setVgap(1);
        minimap.setPrefSize(MINIMAP_SIZE, MINIMAP_SIZE);
        minimap.setMaxSize(MINIMAP_SIZE, MINIMAP_SIZE);
        minimap.setStyle("-fx-background-color: black;");

        return minimap;
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
     * Updates the room view to display the current room.
     */
    public void updateRoomView() {
        if (getController() == null || getController().getDungeon() == null ||
                getController().getPlayer() == null || myRoomViewPane == null) {
            return;
        }

        // Clear the current room view
        myRoomViewPane.getChildren().clear();

        // Get current room
        Point playerPos = getController().getPlayer().getPosition();
        Room currentRoom = getController().getDungeon().getRoom(playerPos);

        if (currentRoom == null) {
            return;
        }

        // We need to use an AnchorPane for precise positioning
        AnchorPane roomPane = new AnchorPane();

        // Create room display
        BorderPane roomLayout = new BorderPane();
        roomLayout.setPrefSize(ROOM_VIEW_SIZE, ROOM_VIEW_SIZE);

        // Room title at the top
        Label roomTitle = new Label(currentRoom.getRoomType().getDisplayName() + " Room");
        roomTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        roomTitle.setPadding(new Insets(5));
        roomLayout.setTop(roomTitle);
        BorderPane.setAlignment(roomTitle, Pos.CENTER);

        // Room features in center
        VBox roomFeatures = new VBox(10);
        roomFeatures.setAlignment(Pos.CENTER);

        // Room symbol
        Label roomSymbol = new Label(getRoomTypeSymbol(currentRoom.getRoomType()));
        roomSymbol.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        roomFeatures.getChildren().add(roomSymbol);

        // Player indicator
        Label playerIndicator = new Label("Player: " + getController().getPlayer().getName());
        playerIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        roomFeatures.getChildren().add(playerIndicator);

        // Add room specific information (monsters, etc.) - your existing code

        // Create exit indicators
        HBox exitInfo = new HBox(5);
        exitInfo.setAlignment(Pos.CENTER);
        Label exitsLabel = new Label("Exits: ");
        exitInfo.getChildren().add(exitsLabel);

        if (currentRoom.hasNorthDoor()) exitInfo.getChildren().add(new Label("North"));
        if (currentRoom.hasEastDoor()) exitInfo.getChildren().add(new Label("East"));
        if (currentRoom.hasSouthDoor()) exitInfo.getChildren().add(new Label("South"));
        if (currentRoom.hasWestDoor()) exitInfo.getChildren().add(new Label("West"));

        roomFeatures.getChildren().add(exitInfo);

        roomLayout.setCenter(roomFeatures);

        // Create a visual floor (background)
        Rectangle floor = new Rectangle(300, 300);
        floor.setFill(getRoomColor(currentRoom.getRoomType()));
        floor.setStroke(Color.BLACK);
        floor.setOpacity(0.2);

        StackPane roomWithFloor = new StackPane();
        roomWithFloor.getChildren().addAll(floor, roomLayout);

        // Add the room to the AnchorPane with constraints to fill the entire space
        roomPane.getChildren().add(roomWithFloor);
        AnchorPane.setTopAnchor(roomWithFloor, 0.0);
        AnchorPane.setLeftAnchor(roomWithFloor, 0.0);
        AnchorPane.setRightAnchor(roomWithFloor, 0.0);
        AnchorPane.setBottomAnchor(roomWithFloor, 0.0);

        // Update minimap
        updateMinimapView();

        // Create minimap container with background
        StackPane minimapContainer = new StackPane();
        Rectangle minimapBg = new Rectangle(MINIMAP_SIZE + 5, MINIMAP_SIZE + 5);
        minimapBg.setFill(Color.BLACK);
        minimapBg.setOpacity(0.7);
        minimapBg.setStroke(Color.WHITE);
        minimapBg.setStrokeWidth(1);
        minimapContainer.getChildren().addAll(minimapBg, myMinimapPane);

        // Add minimap to the AnchorPane with constraints for the top-right corner
        roomPane.getChildren().add(minimapContainer);
        AnchorPane.setTopAnchor(minimapContainer, 10.0);
        AnchorPane.setRightAnchor(minimapContainer, 10.0);

        // Set the AnchorPane as the center of the room view
        myRoomViewPane.setCenter(roomPane);
    }

    /**
     * Updates the minimap view.
     */
    public void updateMinimapView() {
        if (getController() == null || getController().getDungeon() == null ||
                getController().getPlayer() == null) {
            return;
        }

        // Create the minimap pane if it doesn't exist
        if (myMinimapPane == null) {
            myMinimapPane = new GridPane();
        }

        // Configure the minimap pane
        myMinimapPane.getChildren().clear();
        myMinimapPane.setAlignment(Pos.CENTER);
        myMinimapPane.setHgap(1);
        myMinimapPane.setVgap(1);
        myMinimapPane.setPrefSize(MINIMAP_SIZE, MINIMAP_SIZE);
        myMinimapPane.setMaxSize(MINIMAP_SIZE, MINIMAP_SIZE);

        // Get the dungeon and player position
        Point playerPos = getController().getPlayer().getPosition();
        int dungeonWidth = getController().getDungeon().getWidth();
        int dungeonHeight = getController().getDungeon().getHeight();

        // Determine how many rooms to show around the player position
        int viewRadius = 2;
        int minX = Math.max(0, playerPos.getX() - viewRadius);
        int maxX = Math.min(dungeonWidth - 1, playerPos.getX() + viewRadius);
        int minY = Math.max(0, playerPos.getY() - viewRadius);
        int maxY = Math.min(dungeonHeight - 1, playerPos.getY() + viewRadius);

        // Draw minimap cells
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                Room room = getController().getDungeon().getRoom(x, y);
                if (room != null) {
                    StackPane cell = new StackPane();
                    cell.setPrefSize(MINIMAP_CELL_SIZE, MINIMAP_CELL_SIZE);

                    // Create the room rectangle
                    Rectangle roomRect = new Rectangle(MINIMAP_CELL_SIZE - 2, MINIMAP_CELL_SIZE - 2);

                    // Determine color and style
                    if (x == playerPos.getX() && y == playerPos.getY()) {
                        // Current player position
                        roomRect.setFill(Color.RED);
                    } else if (room.isVisited()) {
                        // Visited rooms
                        roomRect.setFill(getRoomColor(room.getRoomType()));
                    } else {
                        // Unvisited rooms
                        roomRect.setFill(Color.DARKGRAY);
                    }

                    roomRect.setStroke(Color.BLACK);
                    roomRect.setStrokeWidth(0.5);

                    // Add room type indicator if room is visited
                    if (room.isVisited() && room.getRoomType() != RoomType.EMPTY) {
                        Label roomLabel = new Label(getRoomTypeIndicator(room.getRoomType()));
                        roomLabel.setFont(Font.font("Arial", 8));
                        roomLabel.setTextFill(Color.BLACK);
                        cell.getChildren().addAll(roomRect, roomLabel);
                    } else {
                        cell.getChildren().add(roomRect);
                    }

                    GridPane.setRowIndex(cell, y - minY);
                    GridPane.setColumnIndex(cell, x - minX);
                    myMinimapPane.getChildren().add(cell);
                }
            }
        }
        // Add a "MINIMAP" label at the bottom
        Label minimapLabel = new Label("MINIMAP");
        minimapLabel.setFont(Font.font("Arial", 8));
        minimapLabel.setTextFill(Color.WHITE);
        StackPane labelContainer = new StackPane(minimapLabel);
        GridPane.setRowIndex(labelContainer, maxY - minY + 1);
        GridPane.setColumnSpan(labelContainer, maxX - minX + 1);
        myMinimapPane.getChildren().add(labelContainer);
    }

    /**
     * Helper method to get a color for a room type.
     * @param roomType The room type
     * @return The color for that room type
     */
    private Color getRoomColor(RoomType roomType) {
        switch (roomType) {
            case ENTRANCE: return Color.LIGHTGREEN;
            case EXIT: return Color.LIGHTBLUE;
            case MONSTER: return Color.SALMON;
            case TREASURE: return Color.GOLD;
            case PILLAR: return Color.MEDIUMPURPLE;
            case TRAP: return Color.ORANGERED;
            case BOSS: return Color.DARKRED;
            default: return Color.LIGHTGRAY;
        }
    }

    /**
     * Helper method to get a symbol for a room type.
     * @param roomType The room type
     * @return A symbol representing that room type
     */
    private String getRoomTypeSymbol(RoomType roomType) {
        switch (roomType) {
            case ENTRANCE: return "E";
            case EXIT: return "X";
            case MONSTER: return "M";
            case TREASURE: return "$";
            case PILLAR: return "P";
            case TRAP: return "!";
            case BOSS: return "B";
            default: return "?";
        }
    }

    /**
     * Returns a single character indicator for room type on the minimap.
     */
    private String getRoomTypeIndicator(RoomType roomType) {
        return getRoomTypeSymbol(roomType); // Use the same symbols for consistency
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
