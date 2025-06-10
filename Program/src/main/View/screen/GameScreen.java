package main.View.screen;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.Controller.StateController;
import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.util.Point;
import main.View.GameUI;

public class GameScreen extends Screen {

    // ====== CONFIGURATION CONSTANTS ======
    private static final class Config {
        // Canvas Configuration
        static final double DEFAULT_CANVAS_SIZE = 480.0;
        static final double CANVAS_MIN_SIZE = 400.0;
        static final double CANVAS_PADDING = 20.0;

        // Tile and Hero Configuration
        static final double TILES_PER_CANVAS = 30.0;
        static final double HERO_SIZE_MULTIPLIER = 2.0;
        static final double DOOR_WIDTH_MULTIPLIER = 2.0;

        // Panel Configuration
        static final double PANEL_MIN_WIDTH = 150.0;
        static final double PANEL_PREF_WIDTH = 160.0;
        static final double PANEL_MAX_WIDTH = 180.0;
        static final double PANEL_PADDING = 10.0;

        // Button Configuration
        static final double BUTTON_HEIGHT = 28.0;
        static final double BUTTON_PADDING = 5.0;
        static final double COMPACT_BUTTON_WIDTH = 130.0;

        // Minimap Configuration
        static final double MINIMAP_SIZE = 110.0;
        static final double MINIMAP_ROOM_SIZE = 10.0;
        static final double MINIMAP_GRID_SIZE = 7.0;

        // Message Area Configuration
        static final double MESSAGE_AREA_HEIGHT_RATIO = 0.12;
        static final double MESSAGE_AREA_MIN_HEIGHT = 80.0;
        static final double MESSAGE_AREA_MAX_HEIGHT = 120.0;
        static final int MAX_MESSAGES = 8;

        // Font Configuration
        static final String FONT_FAMILY = "Monospaced";
        static final double STATS_FONT_SIZE = 12.0;
        static final double TITLE_FONT_SIZE = 28.0;

        // Colors
        static final String BACKGROUND_COLOR = "#1a1a1a";
        static final String PANEL_COLOR = "#8B4513";
        static final String PANEL_BORDER_COLOR = "#654321";
        static final String BUTTON_COLOR = "#654321";
        static final String MESSAGE_AREA_COLOR = "#2a2a2a";
    }

    // ====== DIMENSION CALCULATION CLASS ======
    private static class CanvasDimensions {
        private final double size;
        private final double tileSize;
        private final double heroSize;
        private final double wallThickness;
        private final double doorWidth;

        public CanvasDimensions(double canvasSize) {
            this.size = canvasSize;
            this.tileSize = canvasSize / Config.TILES_PER_CANVAS;
            this.heroSize = tileSize * Config.HERO_SIZE_MULTIPLIER;
            this.wallThickness = tileSize;
            this.doorWidth = wallThickness * Config.DOOR_WIDTH_MULTIPLIER;
        }

        double getSize() { return size; }
        double getTileSize() { return tileSize; }
        double getHeroSize() { return heroSize; }
        double getWallThickness() { return wallThickness; }
        double getDoorWidth() { return doorWidth; }

        double getBoundaryTop() { return wallThickness; }
        double getBoundaryBottom() { return size - wallThickness - heroSize; }
        double getBoundaryLeft() { return wallThickness; }
        double getBoundaryRight() { return size - wallThickness - heroSize; }
    }

    // ====== UI COMPONENTS ======
    private Scene scene;
    private Canvas roomCanvas;
    private GraphicsContext graphicsContext;
    private GraphicsContext minimapGraphics;

    private GraphicsContext portraitGraphics;

    // Player Stats
    private Label playerHealthLabel;
    private Label playerAttackLabel;
    private Label playerGoldLabel;

    // Message Area
    private VBox messagesArea;
    private ScrollPane messageScrollPane;
    private VBox messageContainer;

    // Game Loop
    private AnimationTimer gameLoop;
    private CanvasDimensions currentDimensions;

    // ====== CONSTRUCTOR ======
    public GameScreen(final Stage primaryStage, final Controller controller) {
        super(primaryStage, controller);
        this.currentDimensions = new CanvasDimensions(Config.DEFAULT_CANVAS_SIZE);
    }

    // ====== MAIN SCREEN SETUP ======
    @Override
    public void showScreen(final GameUI gameUI) {
        scene = createScene(gameUI);
        initializeHeroPosition();
        setupCanvasScaling();

        Platform.runLater(() -> {
            initializeHeroMovementSpeed();
            renderRoom();
            updatePlayerStats();
            updateMinimap();
            startGameLoop();
        });

        addGameMessage("Welcome to the dungeon!");
        if (getController().getGameController() != null) {
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }

        getStage().setScene(scene);
        getStage().setTitle("Dungeon Dive - Pixel Adventure");
        getStage().show();
    }

    // ====== SCENE CREATION ======
    private Scene createScene(GameUI gameUI) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + Config.BACKGROUND_COLOR + ";");

        scene = new Scene(root, 1000, 750);

        HBox mainGameArea = createMainGameArea(gameUI);
        createMessageArea();

        root.setCenter(mainGameArea);
        root.setBottom(messageContainer);

        setupResponsiveBindings(mainGameArea);

        return scene;
    }

    private HBox createMainGameArea(GameUI gameUI) {
        HBox mainGameArea = new HBox();
        mainGameArea.setStyle("-fx-background-color: " + Config.BACKGROUND_COLOR + ";");
        mainGameArea.setFillHeight(true);

        VBox leftPanel = createLeftPanel();
        VBox centerArea = createCenterArea();
        VBox rightPanel = createRightPanel(gameUI);

        HBox.setHgrow(leftPanel, Priority.NEVER);
        HBox.setHgrow(centerArea, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.NEVER);

        mainGameArea.getChildren().addAll(leftPanel, centerArea, rightPanel);

        return mainGameArea;
    }

    // ====== LEFT PANEL CREATION ======
    private VBox createLeftPanel() {
        VBox leftPanel = createStyledPanel();

        VBox statsBox = createPlayerStatsBox();
        VBox portraitSection = createCharacterPortrait();
        Button inventoryButton = createStyledButton("INV");
        inventoryButton.setOnAction(_ -> getController().getGameController().openInventory());

        leftPanel.getChildren().addAll(statsBox, portraitSection, inventoryButton);

        return leftPanel;
    }

    private VBox createPlayerStatsBox() {
        VBox statsBox = new VBox(6);
        statsBox.setStyle(
                "-fx-background-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-color: " + Config.PANEL_COLOR + "; " +
                        "-fx-border-width: 2px; " +
                        "-fx-padding: 8; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        Font statsFont = Font.font(Config.FONT_FAMILY, FontWeight.BOLD, Config.STATS_FONT_SIZE);

        // Health with heart icon
        HBox healthBox = createStatBox("❤", Color.RED);
        playerHealthLabel = createStatLabel(statsFont);
        healthBox.getChildren().add(playerHealthLabel);

        // Attack with sword icon
        HBox attackBox = createStatBox("⚔", Color.LIGHTGRAY);
        playerAttackLabel = createStatLabel(statsFont);
        attackBox.getChildren().add(playerAttackLabel);

        // Gold with coin icon
        HBox goldBox = createStatBox("🪙", Color.GOLD);
        playerGoldLabel = createStatLabel(statsFont);
        goldBox.getChildren().add(playerGoldLabel);

        statsBox.getChildren().addAll(healthBox, attackBox, goldBox);

        return statsBox;
    }

    private HBox createStatBox(String icon, Color iconColor) {
        HBox statBox = new HBox(3);
        statBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setTextFill(iconColor);
        iconLabel.setFont(Font.font(14));

        statBox.getChildren().add(iconLabel);
        return statBox;
    }

    private Label createStatLabel(Font font) {
        Label label = new Label();
        label.setFont(font);
        label.setTextFill(Color.WHITE);
        return label;
    }

    private VBox createCharacterPortrait() {
        VBox portraitSection = new VBox(3);
        portraitSection.setAlignment(Pos.CENTER);
        portraitSection.setStyle(
                "-fx-background-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-color: " + Config.PANEL_COLOR + "; " +
                        "-fx-border-width: 2px; " +
                        "-fx-padding: 6; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        StackPane portraitFrame = new StackPane();
        portraitFrame.setPrefSize(80, 80);
        portraitFrame.setStyle(
                "-fx-background-color: #2a2a2a; " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 2px;"
        );

        // Create canvas for animated portrait instead of static image
        // Portrait Canvas for animated character
        Canvas portraitCanvas = new Canvas(70, 70);
        portraitGraphics = portraitCanvas.getGraphicsContext2D();

        // Set up the canvas with a dark background
        portraitGraphics.setFill(Color.rgb(40, 40, 45));
        portraitGraphics.fillRect(0, 0, 70, 70);

        portraitFrame.getChildren().add(portraitCanvas);
        portraitSection.getChildren().add(portraitFrame);

        return portraitSection;
    }

    // ====== CENTER AREA CREATION ======
    private VBox createCenterArea() {
        VBox centerArea = new VBox();
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setStyle("-fx-background-color: " + Config.BACKGROUND_COLOR + ";");

        VBox titleBox = createTitleBox();
        StackPane canvasContainer = createCanvasContainer();

        centerArea.getChildren().addAll(titleBox, canvasContainer);
        VBox.setVgrow(canvasContainer, Priority.ALWAYS);

        return centerArea;
    }

    private VBox createTitleBox() {
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        Label gameTitle = new Label("Dungeon Dive");
        gameTitle.setFont(Font.font("Serif", FontWeight.BOLD, Config.TITLE_FONT_SIZE));
        gameTitle.setTextFill(Color.web("#D4AF37"));
        gameTitle.setAlignment(Pos.CENTER);

        Label subtitle = new Label("Pixel Adventure");
        subtitle.setFont(Font.font("Serif", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#D4AF37"));
        subtitle.setAlignment(Pos.CENTER);

        titleBox.getChildren().addAll(gameTitle, subtitle);
        return titleBox;
    }

    private StackPane createCanvasContainer() {
        StackPane canvasContainer = new StackPane();
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setStyle("-fx-background-color: " + Config.BACKGROUND_COLOR + ";");

        roomCanvas = new Canvas(Config.DEFAULT_CANVAS_SIZE, Config.DEFAULT_CANVAS_SIZE);
        graphicsContext = roomCanvas.getGraphicsContext2D();
        roomCanvas.setVisible(true);
        roomCanvas.setManaged(true);

        canvasContainer.getChildren().add(roomCanvas);

        return canvasContainer;
    }

    // ====== RIGHT PANEL CREATION ======
    private VBox createRightPanel(GameUI gameUI) {
        VBox rightPanel = createStyledPanel();

        VBox minimapContainer = createMinimapContainer();

        Button questButton = createStyledButton("QUEST");
        Button helpButton = createStyledButton("HELP");
        Button pauseButton = createStyledButton("PAUSE");

        questButton.setOnAction(_ -> addGameMessage("Quest system not yet implemented!"));
        helpButton.setOnAction(event -> getController().helpMenu(event, gameUI));
        pauseButton.setOnAction(event -> getController().pauseGame(event, gameUI));

        rightPanel.getChildren().addAll(minimapContainer, questButton, helpButton, pauseButton);

        return rightPanel;
    }

    private VBox createMinimapContainer() {
        VBox minimapContainer = new VBox(5);
        minimapContainer.setAlignment(Pos.CENTER);
        minimapContainer.setStyle(
                "-fx-background-color: #000000; " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: 6; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        Label minimapTitle = new Label("MAP");
        minimapTitle.setFont(Font.font("Serif", FontWeight.BOLD, 12));
        minimapTitle.setTextFill(Color.WHITE);
        minimapTitle.setAlignment(Pos.CENTER);

        Canvas minimapCanvas = new Canvas(Config.MINIMAP_SIZE, Config.MINIMAP_SIZE);
        minimapGraphics = minimapCanvas.getGraphicsContext2D();

        minimapContainer.getChildren().addAll(minimapTitle, minimapCanvas);

        return minimapContainer;
    }

    // ====== UTILITY METHODS FOR UI CREATION ======
    private VBox createStyledPanel() {
        VBox panel = new VBox(8);
        panel.setPrefWidth(Config.PANEL_PREF_WIDTH);
        panel.setMaxWidth(Config.PANEL_MAX_WIDTH);
        panel.setMinWidth(Config.PANEL_MIN_WIDTH);
        panel.setStyle(
                "-fx-background-color: " + Config.PANEL_COLOR + "; " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: " + Config.PANEL_PADDING + ";"
        );
        return panel;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(Config.FONT_FAMILY, FontWeight.BOLD, 11));
        button.setPrefWidth(Config.COMPACT_BUTTON_WIDTH);
        button.setPrefHeight(Config.BUTTON_HEIGHT);
        button.setStyle(
                "-fx-background-color: " + Config.BUTTON_COLOR + "; " +
                        "-fx-text-fill: #E0E0E0; " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 2px; " +
                        "-fx-padding: " + Config.BUTTON_PADDING + "px; " +
                        "-fx-background-radius: 3px; " +
                        "-fx-border-radius: 3px;"
        );

        String originalStyle = button.getStyle();
        button.setOnMouseEntered(_ ->
                button.setStyle(originalStyle + "-fx-background-color: " + Config.PANEL_COLOR + ";"));
        button.setOnMouseExited(_ ->
                button.setStyle(originalStyle));

        return button;
    }

    // ====== MESSAGE AREA ======
    private void createMessageArea() {
        messageContainer = new VBox();
        messageContainer.setStyle(
                "-fx-background-color: " + Config.MESSAGE_AREA_COLOR + "; " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 3 0 0 0; " +
                        "-fx-padding: " + Config.PANEL_PADDING + ";"
        );

        messagesArea = new VBox(6);
        messagesArea.setAlignment(Pos.TOP_LEFT);
        messagesArea.setStyle("-fx-padding: 8;");

        messageScrollPane = new ScrollPane(messagesArea);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScrollPane.setStyle(
                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent;"
        );
        messageScrollPane.setPrefHeight(Config.MESSAGE_AREA_MIN_HEIGHT);
        messageScrollPane.setMaxHeight(Config.MESSAGE_AREA_MAX_HEIGHT);

        messageContainer.getChildren().add(messageScrollPane);
    }

    // ====== RESPONSIVE BINDINGS ======
    private void setupResponsiveBindings(HBox mainGameArea) {
        VBox leftPanel = (VBox) mainGameArea.getChildren().get(0);
        VBox rightPanel = (VBox) mainGameArea.getChildren().get(2);

        leftPanel.setMinWidth(Config.PANEL_MIN_WIDTH);
        leftPanel.setPrefWidth(Config.PANEL_PREF_WIDTH);
        leftPanel.setMaxWidth(Config.PANEL_MAX_WIDTH);

        rightPanel.setMinWidth(Config.PANEL_MIN_WIDTH);
        rightPanel.setPrefWidth(Config.PANEL_PREF_WIDTH);
        rightPanel.setMaxWidth(Config.PANEL_MAX_WIDTH);

        messageContainer.prefHeightProperty().bind(
                Bindings.max(Config.MESSAGE_AREA_MIN_HEIGHT,
                        scene.heightProperty().multiply(Config.MESSAGE_AREA_HEIGHT_RATIO))
        );

        mainGameArea.prefWidthProperty().bind(scene.widthProperty());
        mainGameArea.minWidthProperty().bind(scene.widthProperty());
    }

    // ====== CANVAS SCALING ======
    private void setupCanvasScaling() {
        roomCanvas.widthProperty().addListener((obs, oldVal, newVal) -> scaleCanvas());
        roomCanvas.heightProperty().addListener((obs, oldVal, newVal) -> scaleCanvas());
    }

    private void scaleCanvas() {
        currentDimensions = new CanvasDimensions(roomCanvas.getWidth());

        Hero player = getController().getPlayer();
        if (player != null) {
            player.setMovementSpeedForCanvasSize(currentDimensions.getSize());
        }

        Platform.runLater(() -> renderRoom());
    }

    // ====== HERO INITIALIZATION ======
    private void initializeHeroPosition() {
        Hero player = getController().getPlayer();
        if (player != null && (player.getPixelX() == 0 && player.getPixelY() == 0)) {
            double centerPos = Config.DEFAULT_CANVAS_SIZE / 2 -
                    (Config.DEFAULT_CANVAS_SIZE / Config.TILES_PER_CANVAS *
                            Config.HERO_SIZE_MULTIPLIER) / 2;
            player.setPixelPosition(centerPos, centerPos);
        }
    }

    private void initializeHeroMovementSpeed() {
        Hero player = getController().getPlayer();
        if (player != null) {
            player.setMovementSpeedForCanvasSize(currentDimensions.getSize());
        }
    }

    // ====== GAME LOOP ======
    private void startGameLoop() {
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (shouldUpdateGame()) {
                        updateGame();
                    }
                    renderRoom();
                }
            };
        }
        gameLoop.start();
    }

    private boolean shouldUpdateGame() {
        return getController() != null &&
                getController().getGameController() != null &&
                getController().getPlayer() != null &&
                getController().getGameController().getStateController()
                        .isInState(StateController.GameState.EXPLORING);
    }

    private void updateGame() {
        Hero player = getController().getPlayer();
        if (player != null) {
            player.updatePixelPosition();
            player.updateAnimation(System.nanoTime());

            boolean roomChanged = checkRoomTransition();
            if (roomChanged) {
                onRoomChanged();
            }

            // Update the portrait canvas to match player animation
            updatePortraitCanvas();
        }
    }

    public void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    // ====== MOVEMENT AND ROOM TRANSITIONS ======
    private boolean checkRoomTransition() {
        Hero player = getController().getPlayer();
        Room currentRoom = getCurrentRoom();
        
        
        if (player == null || currentRoom == null) return false;

        double px = player.getPixelX();
        double py = player.getPixelY();
        double heroSize = currentDimensions.getHeroSize();
        double doorWidth = currentDimensions.getDoorWidth();
        double canvasSize = currentDimensions.getSize();

        // Calculate door positions
        double doorStartHorizontal = (canvasSize - doorWidth) / 2;
        double doorEndHorizontal = doorStartHorizontal + doorWidth;
        double doorStartVertical = (canvasSize - doorWidth) / 2;
        double doorEndVertical = doorStartVertical + doorWidth;

        // Check each direction for room transition
        if (checkNorthTransition(currentRoom, px, py, heroSize, doorStartHorizontal, doorEndHorizontal)) {
            return true;
        }
        if (checkSouthTransition(currentRoom, px, py, heroSize, doorStartHorizontal, doorEndHorizontal)) {
            return true;
        }
        if (checkWestTransition(currentRoom, px, py, heroSize, doorStartVertical, doorEndVertical)) {
            return true;
        }
        if (checkEastTransition(currentRoom, px, py, heroSize, doorStartVertical, doorEndVertical)) {
            return true;
        }

        // Keep player within boundaries if no transition
        constrainPlayerToBoundaries(player);
        return false;
    }

    private boolean checkNorthTransition(Room currentRoom, double px, double py, double heroSize,
                                         double doorStart, double doorEnd) {
        if (py <= currentDimensions.getBoundaryTop() && currentRoom.hasNorthDoor() &&
                px + heroSize / 2 >= doorStart && px + heroSize / 2 <= doorEnd) {
            getController().getGameController().movePlayerNorth();
            getController().getPlayer().setPixelPosition(px, currentDimensions.getBoundaryBottom() - 1);
            return true;
        }
        return false;
    }

    private boolean checkSouthTransition(Room currentRoom, double px, double py, double heroSize,
                                         double doorStart, double doorEnd) {
        if (py + heroSize >= currentDimensions.getBoundaryBottom() + heroSize && currentRoom.hasSouthDoor() &&
                px + heroSize / 2 >= doorStart && px + heroSize / 2 <= doorEnd) {
            getController().getGameController().movePlayerSouth();
            getController().getPlayer().setPixelPosition(px, currentDimensions.getBoundaryTop() + 1);
            return true;
        }
        return false;
    }

    private boolean checkWestTransition(Room currentRoom, double px, double py, double heroSize,
                                        double doorStart, double doorEnd) {
        if (px <= currentDimensions.getBoundaryLeft() && currentRoom.hasWestDoor() &&
                py + heroSize / 2 >= doorStart && py + heroSize / 2 <= doorEnd) {
            getController().getGameController().movePlayerWest();
            getController().getPlayer().setPixelPosition(currentDimensions.getBoundaryRight() - 1, py);
            return true;
        }
        return false;
    }

    private boolean checkEastTransition(Room currentRoom, double px, double py, double heroSize,
                                        double doorStart, double doorEnd) {
        if (px + heroSize >= currentDimensions.getBoundaryRight() + heroSize && currentRoom.hasEastDoor() &&
                py + heroSize / 2 >= doorStart && py + heroSize / 2 <= doorEnd) {
            getController().getGameController().movePlayerEast();
            getController().getPlayer().setPixelPosition(currentDimensions.getBoundaryLeft() + 1, py);
            return true;
        }
        return false;
    }

    private void constrainPlayerToBoundaries(Hero player) {
        double px = Math.max(currentDimensions.getBoundaryLeft(),
                Math.min(currentDimensions.getBoundaryRight(), player.getPixelX()));
        double py = Math.max(currentDimensions.getBoundaryTop(),
                Math.min(currentDimensions.getBoundaryBottom(), player.getPixelY()));
        player.setPixelPosition(px, py);
    }

    // ====== RENDERING ======
    private void renderRoom() {
        if (graphicsContext == null) return;

        clearCanvas();
        drawFloor();
        drawWalls();
        drawPlayer();
    }

    private void clearCanvas() {
        graphicsContext.setFill(Color.rgb(20, 20, 25));
        graphicsContext.fillRect(0, 0, currentDimensions.getSize(), currentDimensions.getSize());
    }

    private void drawFloor() {
        Color darkStone = Color.rgb(40, 40, 45);
        Color lightStone = Color.rgb(50, 50, 55);

        int tilesPerSide = (int)(currentDimensions.getSize() / currentDimensions.getTileSize());

        for (int y = 0; y < tilesPerSide; y++) {
            for (int x = 0; x < tilesPerSide; x++) {
                Color tileColor = ((x + y) % 2 == 0) ? darkStone : lightStone;
                graphicsContext.setFill(tileColor);
                graphicsContext.fillRect(
                        x * currentDimensions.getTileSize(),
                        y * currentDimensions.getTileSize(),
                        currentDimensions.getTileSize(),
                        currentDimensions.getTileSize()
                );
            }
        }
    }

    private void drawWalls() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) return;

        Color wallColor = Color.rgb(80, 85, 100);
        Color doorColor = Color.rgb(25, 25, 30);

        double size = currentDimensions.getSize();
        double thickness = currentDimensions.getWallThickness();
        double doorWidth = currentDimensions.getDoorWidth();

        // Draw walls with doors
        drawWall(0, 0, size, thickness, currentRoom.hasNorthDoor(), true, doorWidth, wallColor, doorColor);
        drawWall(0, size - thickness, size, thickness, currentRoom.hasSouthDoor(), true, doorWidth, wallColor, doorColor);
        drawWall(0, 0, thickness, size, currentRoom.hasWestDoor(), false, doorWidth, wallColor, doorColor);
        drawWall(size - thickness, 0, thickness, size, currentRoom.hasEastDoor(), false, doorWidth, wallColor, doorColor);
    }

    private void drawWall(double x, double y, double width, double height,
                          boolean hasDoor, boolean isHorizontal, double doorSize,
                          Color wallColor, Color doorColor) {
        if (hasDoor) {
            drawWallWithDoor(x, y, width, height, isHorizontal, doorSize, wallColor, doorColor);
        } else {
            drawSolidWall(x, y, width, height, wallColor);
        }
    }

    private void drawWallWithDoor(double x, double y, double width, double height,
                                  boolean isHorizontal, double doorSize,
                                  Color wallColor, Color doorColor) {
        double doorStart;

        if (isHorizontal) {
            doorStart = x + (width - doorSize) / 2;

            // Wall before door
            graphicsContext.setFill(wallColor);
            graphicsContext.fillRect(x, y, doorStart - x, height);

            // Door opening
            graphicsContext.setFill(doorColor);
            graphicsContext.fillRect(doorStart, y, doorSize, height);

            // Wall after door
            graphicsContext.setFill(wallColor);
            graphicsContext.fillRect(doorStart + doorSize, y,
                    x + width - (doorStart + doorSize), height);
        } else {
            doorStart = y + (height - doorSize) / 2;

            // Wall before door
            graphicsContext.setFill(wallColor);
            graphicsContext.fillRect(x, y, width, doorStart - y);

            // Door opening
            graphicsContext.setFill(doorColor);
            graphicsContext.fillRect(x, doorStart, width, doorSize);

            // Wall after door
            graphicsContext.setFill(wallColor);
            graphicsContext.fillRect(x, doorStart + doorSize, width,
                    y + height - (doorStart + doorSize));
        }
    }

    private void drawSolidWall(double x, double y, double width, double height, Color wallColor) {
        graphicsContext.setFill(wallColor);
        graphicsContext.fillRect(x, y, width, height);
    }

    private void drawPlayer() {
        Hero player = getController().getPlayer();
        if (player == null) return;

        double px = player.getPixelX();
        double py = player.getPixelY();
        double heroSize = currentDimensions.getHeroSize();

        Image spriteSheet = player.getSpriteSheet();
        if (spriteSheet != null) {
            double sx = player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH;
            double sy = player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT;

            graphicsContext.drawImage(
                    spriteSheet,
                    sx, sy,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    px, py,
                    heroSize, heroSize
            );
        } else {
            drawFallbackPlayer(px, py, heroSize);
        }
    }

    private void drawFallbackPlayer(double px, double py, double heroSize) {
        // Body
        graphicsContext.setFill(Color.rgb(200, 150, 100));
        graphicsContext.fillRect(px + heroSize*0.3, py + heroSize*0.4,
                heroSize*0.4, heroSize*0.5);

        // Head
        graphicsContext.setFill(Color.rgb(200, 150, 100));
        graphicsContext.fillRect(px + heroSize*0.25, py + heroSize*0.1,
                heroSize*0.5, heroSize*0.35);

        // Tunic
        graphicsContext.setFill(Color.rgb(100, 80, 60));
        graphicsContext.fillRect(px + heroSize*0.2, py + heroSize*0.35,
                heroSize*0.6, heroSize*0.4);

        // Legs
        graphicsContext.setFill(Color.rgb(80, 60, 40));
        graphicsContext.fillRect(px + heroSize*0.25, py + heroSize*0.7,
                heroSize*0.2, heroSize*0.25);
        graphicsContext.fillRect(px + heroSize*0.55, py + heroSize*0.7,
                heroSize*0.2, heroSize*0.25);
    }

    // ====== PORTRAIT CANVAS RENDERING ======
    private void updatePortraitCanvas() {
        if (portraitGraphics == null) return;

        Hero player = getController().getPlayer();
        if (player == null) return;

        // Clear the portrait canvas
        portraitGraphics.setFill(Color.rgb(40, 40, 45));
        portraitGraphics.fillRect(0, 0, 70, 70);

        // Draw the player centered in the portrait canvas
        double centerX = 35 - 20; // Center horizontally (35 - half of player size)
        double centerY = 35 - 20; // Center vertically
        double portraitHeroSize = 40; // Size of hero in portrait

        Image spriteSheet = player.getSpriteSheet();
        if (spriteSheet != null) {
            // Use the same sprite frame that's currently being displayed in the main game
            double sx = player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH;
            double sy = player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT;

            portraitGraphics.drawImage(
                    spriteSheet,
                    sx, sy,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    centerX, centerY,
                    portraitHeroSize, portraitHeroSize
            );
        } else {
            // Use the same fallback rendering as the main canvas
            drawFallbackPlayer(centerX, centerY, portraitHeroSize);
        }
    }

    // ====== MINIMAP RENDERING ======
    public void updateMinimap() {
        if (getController() == null || getController().getDungeon() == null || minimapGraphics == null) {
            return;
        }

        clearMinimap();
        drawMinimapBorder();
        drawMinimapRooms();
    }

    private void clearMinimap() {
        minimapGraphics.clearRect(0, 0, Config.MINIMAP_SIZE, Config.MINIMAP_SIZE);
        minimapGraphics.setFill(Color.BLACK);
        minimapGraphics.fillRect(0, 0, Config.MINIMAP_SIZE, Config.MINIMAP_SIZE);
    }

    private void drawMinimapBorder() {
        minimapGraphics.setStroke(Color.rgb(139, 69, 19));
        minimapGraphics.setLineWidth(2);
        minimapGraphics.strokeRect(1, 1, Config.MINIMAP_SIZE - 2, Config.MINIMAP_SIZE - 2);
    }

    private void drawMinimapRooms() {
        Dungeon dungeon = getController().getDungeon();
        Hero player = getController().getPlayer();
        if (player == null) return;

        Point playerPos = player.getPosition();
        int gridSize = (int) Config.MINIMAP_GRID_SIZE;

        int startX = Math.max(0, playerPos.getX() - gridSize / 2);
        int endX = Math.min(dungeon.getWidth() - 1, playerPos.getX() + gridSize / 2);
        int startY = Math.max(0, playerPos.getY() - gridSize / 2);
        int endY = Math.min(dungeon.getHeight() - 1, playerPos.getY() + gridSize / 2);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Room room = dungeon.getRoom(x, y);
                if (room != null) {
                    boolean isPlayerPosition = (x == playerPos.getX() && y == playerPos.getY());
                    drawMinimapRoom(room, x - startX, y - startY, isPlayerPosition);
                }
            }
        }
    }

    private void drawMinimapRoom(Room room, int gridX, int gridY, boolean isPlayerPosition) {
        double roomSize = Config.MINIMAP_ROOM_SIZE;
        double roomX = 8 + gridX * roomSize + gridX * 1;
        double roomY = 8 + gridY * roomSize + gridY * 1;

        Color roomColor = getMinimapRoomColor(room, isPlayerPosition);
        Color borderColor = isPlayerPosition ? Color.WHITE : Color.rgb(100, 100, 100);

        // Draw room
        minimapGraphics.setFill(roomColor);
        minimapGraphics.fillRect(roomX, roomY, roomSize, roomSize);

        // Draw border
        minimapGraphics.setStroke(borderColor);
        minimapGraphics.setLineWidth(1);
        minimapGraphics.strokeRect(roomX, roomY, roomSize, roomSize);

        // Draw doors
        drawMinimapRoomDoors(room, roomX, roomY, roomSize, roomColor);
    }

    private Color getMinimapRoomColor(Room room, boolean isPlayerPosition) {
        if (isPlayerPosition) {
            return Color.LIME;
        }

        if (!room.isVisited()) {
            return Color.DARKGRAY;
        }

        switch (room.getRoomType()) {
            case ENTRANCE: return Color.LIGHTGREEN;
            case EXIT: return Color.ORANGE;
            case BOSS: return Color.DARKRED;
            case PILLAR:
                return (room.hasPillar() && room.getPillar().isActivated())
                        ? Color.PURPLE : Color.MEDIUMPURPLE;
            case MONSTER:
                return room.getMonsters().isEmpty() ? Color.LIGHTGRAY : Color.RED;
            case TREASURE:
                return room.getItems().isEmpty() ? Color.LIGHTGRAY : Color.GOLD;
            case TRAP:
                return (room.hasTrap() && room.getTrap().isSprung())
                        ? Color.LIGHTGRAY : Color.ORANGERED;
            case EMPTY:
            default:
                return Color.LIGHTGRAY;
        }
    }

    private void drawMinimapRoomDoors(Room room, double roomX, double roomY, double roomSize, Color roomColor) {
        minimapGraphics.setStroke(roomColor);
        minimapGraphics.setLineWidth(2);

        if (room.hasNorthDoor()) {
            minimapGraphics.strokeLine(roomX + roomSize/2 - 1, roomY, roomX + roomSize/2 + 1, roomY);
        }
        if (room.hasSouthDoor()) {
            minimapGraphics.strokeLine(roomX + roomSize/2 - 1, roomY + roomSize, roomX + roomSize/2 + 1, roomY + roomSize);
        }
        if (room.hasWestDoor()) {
            minimapGraphics.strokeLine(roomX, roomY + roomSize/2 - 1, roomX, roomY + roomSize/2 + 1);
        }
        if (room.hasEastDoor()) {
            minimapGraphics.strokeLine(roomX + roomSize, roomY + roomSize/2 - 1, roomX + roomSize, roomY + roomSize/2 + 1);
        }
    }

    // ====== UTILITY METHODS ======
    private Room getCurrentRoom() {
        if (getController() == null || getController().getPlayer() == null ||
                getController().getDungeon() == null) {
            return null;
        }
        return getController().getDungeon().getRoom(getController().getPlayer().getPosition());
    }

    // ====== PUBLIC UPDATE METHODS ======
    public void updatePlayerStats() {
        if (getController() != null && getController().getPlayer() != null) {
            Hero player = getController().getPlayer();
            playerHealthLabel.setText("Health: " + player.getHealth() + "/" + player.getMaxHealth());
            playerAttackLabel.setText("Attack: " + player.getType().getBaseAttack());
            playerGoldLabel.setText("Gold: " + player.getGold());
        }
    }

    public void addGameMessage(final String message) {
        if (messagesArea != null && messageScrollPane != null) {
            double scale = calculateFontScale();
            Font messageFont = Font.font(Config.FONT_FAMILY, FontWeight.NORMAL, 11 * scale);

            Label messageLabel = new Label("• " + message);
            messageLabel.setFont(messageFont);
            messageLabel.setTextFill(Color.WHITE);
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(2, 0, 2, 0));
            messageLabel.setMaxWidth(300);

            messagesArea.getChildren().add(messageLabel);

            // Keep only last MAX_MESSAGES
            while (messagesArea.getChildren().size() > Config.MAX_MESSAGES) {
                messagesArea.getChildren().remove(0);
            }

            // Auto-scroll to bottom
            messageScrollPane.applyCss();
            messageScrollPane.layout();
            messageScrollPane.setVvalue(1.0);
        }
    }

    private double calculateFontScale() {
        double scale = Math.min(scene.getWidth() / 1000.0, scene.getHeight() / 750.0);
        return Math.max(0.8, Math.min(scale, 1.2));
    }

    public void onRoomChanged() {
        updateMinimap();
        updatePlayerStats();
        if (getController().getGameController() != null) {
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }

    public void displayRoomDescription() {
        if (getController() != null && getController().getGameController() != null &&
                getController().getGameController().getCurrentRoomDescription() != null) {
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }
}