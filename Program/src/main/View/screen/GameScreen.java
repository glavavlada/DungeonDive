package main.View.screen;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Controller.StateController;
import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.element.Item;
import main.Model.util.Point;
import main.View.GameUI;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameScreen extends Screen {

    // ====== THEME CONSTANTS (matching other screens) ======
    private static final Color TAN_COLOR = Color.rgb(222, 184, 135);
    private static final Color ORANGE_COLOR = Color.ORANGE;
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";
    // Trap constants
    private static final Duration DAMAGE_FLASH_DURATION = Duration.millis(1500); // Longer duration (was 1000)
    private static final Color DAMAGE_FLASH_COLOR = Color.DARKRED; // Darker, more intense red
    private static final double DAMAGE_FLASH_OPACITY = 0.7; // Higher opacity (was 0.4)
    // ====== UI COMPONENTS ======
    private Scene scene;
    private StackPane scalableRoot; // For consistent scaling
    private Canvas roomCanvas;
    private GraphicsContext graphicsContext;
    private GraphicsContext minimapGraphics;
    private GraphicsContext portraitGraphics;
    private Canvas minimapCanvas;
    private ResponsiveDimensions responsiveDims;

    // Player Stats
    private Label playerHealthLabel;
    private Label playerManaLabel;
    private Label playerAttackLabel;
    private Label playerGoldLabel;
    private Label playerPillarsLabel;

    // Message Area
    private VBox messagesArea;
    private ScrollPane messageScrollPane;
    private VBox messageContainer;

    // Game Loop
    private AnimationTimer gameLoop;
    private CanvasDimensions currentDimensions;

    // Pillar sprite
    private Image pillarSprite;
    private double pillarGlowAnimation = 0.0;
    private static final double PILLAR_GLOW_SPEED = 0.05;
    private static final double PILLAR_COLLECTION_DISTANCE = 30.0; // Pixels

    // Chest sprite
    private Image chestSprite;
    private double chestGlowAnimation = 0.0;
    private static final double CHEST_GLOW_SPEED = 0.04;
    private static final double CHEST_COLLECTION_DISTANCE = 40.0;
    private boolean hasTriedChestCollection = false;
    private boolean hasShownGoldWarning = false;
    private javafx.scene.layout.Pane damageFlashOverlay;


    // ====== CONSTRUCTOR ======
    public GameScreen(final Stage primaryStage, final Controller controller) {
        super(primaryStage, controller);
        this.currentDimensions = new CanvasDimensions(Config.DEFAULT_CANVAS_SIZE);
    }

    // ====== MAIN SCREEN SETUP ======
    @Override
    public void showScreen(final GameUI gameUI) {
        // Create scalable root container (like other screens)
        scalableRoot = new StackPane();

        // Get current scene dimensions for smooth transition
        double currentWidth = BASE_WIDTH;
        double currentHeight = BASE_HEIGHT;

        if (getStage().getScene() != null) {
            currentWidth = getStage().getScene().getWidth();
            currentHeight = getStage().getScene().getHeight();
        }

        scene = new Scene(scalableRoot, currentWidth, currentHeight);

        // Initialize canvas early (before UI creation)
        roomCanvas = new Canvas(Config.DEFAULT_CANVAS_SIZE, Config.DEFAULT_CANVAS_SIZE);
        graphicsContext = roomCanvas.getGraphicsContext2D();

        responsiveDims = new ResponsiveDimensions(scene);

        // Create the main content
        BorderPane mainContent = createMainContent(gameUI);

        //flash overlay
        damageFlashOverlay = createDamageFlashOverlay();
        scalableRoot.getChildren().addAll(mainContent, damageFlashOverlay);

        // Apply consistent scaling and theming
        setupScalingAndTheming();
        setupInitialState();

        // Preserve fullscreen state for smooth transition
        boolean wasFullScreen = getStage().isFullScreen();

        getStage().setScene(scene);
        getStage().setTitle("Dungeon Dive - Pixel Adventure");

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // This ensures the game loop is stopped correctly
                getController().pauseGame(null, gameUI);
            }
        });

        if (wasFullScreen) {
            getStage().setFullScreen(true);
        }

        if (!getStage().isShowing()) {
            getStage().show();
        }
    }

    private BorderPane createMainContent(GameUI gameUI) {
        BorderPane root = new BorderPane();

        // Apply themed background
        setupThemedBackground(root);

        HBox mainGameArea = createMainGameArea(gameUI);
        createMessageArea();

        root.setCenter(mainGameArea);
        root.setBottom(messageContainer);

        return root;
    }

    private void setupThemedBackground(BorderPane root) {
        try (InputStream bgStream = getClass().getResourceAsStream(BACKGROUND_PATH)) {
            if (bgStream != null) {
                root.setStyle(
                        "-fx-background-image: url('" + BACKGROUND_PATH + "'); " +
                                "-fx-background-repeat: no-repeat; " +
                                "-fx-background-size: cover; " +
                                "-fx-background-position: center center;"
                );
            } else {
                root.setStyle("-fx-background-color: #202020;");
            }
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #202020;");
        }
    }

    private void setupScalingAndTheming() {
        // Apply consistent scaling like other screens
        //applyScaling(scalableRoot, scene);
        setupResponsiveBindings();
    }

    private void setupInitialState() {
        loadPillarSprite();
        loadChestSprite();
        initializeHeroPosition();
        scaleCanvas();

        Platform.runLater(() -> {
            if (getController() != null && getController().getPlayer() != null && getController().getDungeon() != null) {
                initializeHeroMovementSpeed();
                renderRoom();
                updatePlayerStats();
                updateMinimap();
                startGameLoop();
                displayWelcomeMessages();
            } else {
                System.err.println("GameScreen: Model not fully initialized yet, retrying...");
                // Retry after a short delay
                Platform.runLater(() -> {
                    if (getController() != null && getController().getPlayer() != null && getController().getDungeon() != null) {
                        initializeHeroMovementSpeed();
                        renderRoom();
                        updatePlayerStats();
                        updateMinimap();
                        startGameLoop();
                        displayWelcomeMessages();
                    } else {
                        System.err.println("GameScreen: Model still not initialized, skipping welcome messages");
                        // Initialize what we can without accessing the dungeon
                        initializeHeroMovementSpeed();
                        updatePlayerStats();
                        startGameLoop();
                    }
                });
            }
        });
    }

    private void displayWelcomeMessages() {
        addGameMessage("Welcome to the dungeon!");
        if (getController().getGameController() != null) {
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }

    // ====== MAIN GAME AREA CREATION ======
    private HBox createMainGameArea(GameUI gameUI) {
        HBox mainGameArea = new HBox();
        mainGameArea.setStyle("-fx-background-color: rgba(0,0,0,0.3);"); // Semi-transparent overlay
        mainGameArea.setFillHeight(true);
        mainGameArea.setMinWidth(600); // Force minimum sizes to prevent disappearing panels

        VBox leftPanel = createLeftPanel();
        VBox centerArea = createCenterArea();
        VBox rightPanel = createRightPanel(gameUI);

        HBox.setHgrow(leftPanel, Priority.NEVER);
        HBox.setHgrow(centerArea, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.NEVER);

        mainGameArea.getChildren().addAll(leftPanel, centerArea, rightPanel);
        return mainGameArea;
    }

    // ====== LEFT PANEL CREATION (with theming) ======
    private VBox createLeftPanel() {
        VBox leftPanel = createThemedPanel();

        VBox statsBox = createPlayerStatsBox();
        VBox portraitSection = createCharacterPortrait();
        Button inventoryButton = createThemedButton("INV");
        inventoryButton.setOnAction(e -> getController().getGameController().openInventory());

        leftPanel.getChildren().addAll(statsBox, portraitSection, inventoryButton);
        return leftPanel;
    }

    private VBox createPlayerStatsBox() {
        VBox statsBox = new VBox();
        statsBox.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(3));
        statsBox.setStyle(
                "-fx-background-color: rgba(139, 69, 19, 0.9); " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 2px; -fx-padding: 8; " +
                        "-fx-border-radius: 5px; -fx-background-radius: 5px;" +
                        SHADOW_STYLE
        );
        playerHealthLabel = createThemedStatLabel();
        HBox healthBox = createStatBox("/sprites/icons/heart.png", playerHealthLabel);

        playerAttackLabel = createThemedStatLabel();
        HBox attackBox = createStatBox("/sprites/icons/sword.png", playerAttackLabel);

        playerManaLabel = createThemedStatLabel();
        HBox manaBox = createStatBox("/sprites/icons/mana.png", playerManaLabel);

        playerGoldLabel = createThemedStatLabel();
        HBox goldBox = createStatBox("/sprites/icons/coin.png", playerGoldLabel);

        playerPillarsLabel = createThemedStatLabel();
        HBox pillarsBox = createStatBox("/sprites/icons/pillar.png", playerPillarsLabel);

        statsBox.getChildren().addAll(healthBox, manaBox, attackBox, goldBox, pillarsBox);
        return statsBox;    }

    // FIX: Modified createStatBox to bind icon size and add padding for text fitting
    private HBox createStatBox(String iconPath, Label valueLabel) {
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
        ImageView iconView = new ImageView(iconImage);

        // Bind icon size to a responsive dimension, related to font size for consistency
        iconView.fitWidthProperty().bind(responsiveDims.getStatsIconSizeBinding());
        iconView.fitHeightProperty().bind(responsiveDims.getStatsIconSizeBinding());
        iconView.setPreserveRatio(true);

        valueLabel.setWrapText(true); // Ensure text wrapping is enabled
        valueLabel.setMaxWidth(Double.MAX_VALUE); // Allow label to expand to fill available space

        HBox statBox = new HBox(6, iconView, valueLabel);
        statBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(valueLabel, Priority.ALWAYS); // Allow the label to grow horizontally

        // Add padding to the HBox itself to prevent text from hitting the edge
        statBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(0, responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0, 0),
                responsiveDims.getPanelPaddingBinding()));

        return statBox;
    }

    // FIX: Refined createThemedStatLabel for better text wrapping and alignment
    private Label createThemedStatLabel() {
        Label label = new Label();
        label.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(FONT_PATH, responsiveDims.getStatsFontSizeBinding().getValue().intValue(), "Monospaced"),
                responsiveDims.getStatsFontSizeBinding()));
        label.setTextFill(TAN_COLOR);
        label.setStyle(SHADOW_STYLE);
        label.setMaxWidth(Double.MAX_VALUE); // Allow it to expand as much as the parent HBox allows
        label.setWrapText(true); // Crucial for multi-line text if needed
        label.setAlignment(Pos.CENTER_LEFT); // Align text within the label
        return label;
    }

    private VBox createCharacterPortrait() {
        VBox portraitSection = createPortraitContainer();
        StackPane portraitFrame = createPortraitFrame();

        Canvas portraitCanvas = createPortraitCanvas(portraitFrame);
        portraitFrame.getChildren().add(portraitCanvas);
        portraitSection.getChildren().add(portraitFrame);

        return portraitSection;
    }

    private VBox createPortraitContainer() {
        VBox portraitSection = new VBox();
        portraitSection.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(3));
        portraitSection.setAlignment(Pos.CENTER);
        portraitSection.setStyle(
                "-fx-background-color: rgba(139, 69, 19, 0.9); " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 2px; -fx-padding: 6; " +
                        "-fx-border-radius: 5px; -fx-background-radius: 5px;" +
                        SHADOW_STYLE
        );
        return portraitSection;
    }

    private StackPane createPortraitFrame() {
        StackPane portraitFrame = new StackPane();
        portraitFrame.prefWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(0.5));
        portraitFrame.prefHeightProperty().bind(responsiveDims.getPanelWidthBinding().multiply(0.5));
        portraitFrame.setStyle(
                "-fx-background-color: #2a2a2a; " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 2px;" +
                        SHADOW_STYLE
        );
        return portraitFrame;
    }

    private Canvas createPortraitCanvas(StackPane portraitFrame) {
        Canvas portraitCanvas = new Canvas();
        portraitCanvas.widthProperty().bind(portraitFrame.prefWidthProperty().subtract(10));
        portraitCanvas.heightProperty().bind(portraitFrame.prefHeightProperty().subtract(10));
        portraitGraphics = portraitCanvas.getGraphicsContext2D();

        portraitCanvas.widthProperty().addListener((obs, oldVal, newVal) -> updatePortraitBackground());
        portraitCanvas.heightProperty().addListener((obs, oldVal, newVal) -> updatePortraitBackground());

        return portraitCanvas;
    }

    private void updatePortraitBackground() {
        if (portraitGraphics != null) {
            Canvas canvas = portraitGraphics.getCanvas();
            portraitGraphics.setFill(Color.rgb(40, 40, 45));
            portraitGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    // ====== CENTER AREA CREATION (with theming) ======
    private VBox createCenterArea() {
        VBox centerArea = new VBox();
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setStyle("-fx-background-color: rgba(0,0,0,0.2);"); // Semi-transparent

        VBox titleBox = createThemedTitleBox();
        StackPane canvasContainer = createCanvasContainer();

        centerArea.getChildren().addAll(titleBox, canvasContainer);
        VBox.setVgrow(canvasContainer, Priority.ALWAYS);
        return centerArea;
    }

    private VBox createThemedTitleBox() {
        VBox titleBox = new VBox(10);
        titleBox.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(2));
        titleBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        titleBox.setAlignment(Pos.CENTER);

        Text gameTitle = new Text("DUNGEON");
        gameTitle.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(FONT_PATH, responsiveDims.getTitleFontSizeBinding().getValue().intValue(), "Impact"),
                responsiveDims.getTitleFontSizeBinding()));
        gameTitle.setFill(ORANGE_COLOR);
        gameTitle.setStyle(SHADOW_STYLE);

        Text subtitle = new Text("DIVE");
        subtitle.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(FONT_PATH, (int)(responsiveDims.getTitleFontSizeBinding().getValue().doubleValue() * 0.6), "Impact"),
                responsiveDims.getTitleFontSizeBinding()));
        subtitle.setFill(ORANGE_COLOR);
        subtitle.setStyle(SHADOW_STYLE);

        titleBox.getChildren().addAll(gameTitle, subtitle);
        return titleBox;
    }

    private StackPane createCanvasContainer() {
        StackPane canvasContainer = new StackPane(roomCanvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setStyle(
                "-fx-background-color: rgba(0,0,0,0.7); " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;" +
                        SHADOW_STYLE
        );
        canvasContainer.setPadding(new Insets(10));
        return canvasContainer;
    }

    // ====== RIGHT PANEL CREATION (with theming) ======
    private VBox createRightPanel(GameUI gameUI) {
        VBox rightPanel = createThemedPanel();

        VBox minimapContainer = createMinimapContainer();
        Button helpButton = createThemedButton("HELP");
        Button pauseButton = createThemedButton("PAUSE");

        helpButton.setOnAction(event -> getController().helpMenu(event, gameUI));
        pauseButton.setOnAction(event -> getController().pauseGame(event, gameUI));

        rightPanel.getChildren().addAll(minimapContainer, helpButton, pauseButton);
        return rightPanel;
    }

    private VBox createMinimapContainer() {
        VBox minimapContainer = createMinimapContainerBase();
        Label minimapTitle = createMinimapTitle();
        minimapCanvas = createMinimapCanvas();

        minimapContainer.getChildren().addAll(minimapTitle, minimapCanvas);
        return minimapContainer;
    }

    private VBox createMinimapContainerBase() {
        VBox minimapContainer = new VBox();
        minimapContainer.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(2));
        minimapContainer.setAlignment(Pos.CENTER);
        minimapContainer.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.9); " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 3px; -fx-padding: 6; " +
                        "-fx-border-radius: 5px; -fx-background-radius: 5px;" +
                        SHADOW_STYLE
        );
        return minimapContainer;
    }

    private Label createMinimapTitle() {
        Label minimapTitle = new Label("MAP");
        minimapTitle.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(FONT_PATH, responsiveDims.getStatsFontSizeBinding().getValue().intValue(), "Impact"),
                responsiveDims.getStatsFontSizeBinding()));
        minimapTitle.setTextFill(TAN_COLOR);
        minimapTitle.setAlignment(Pos.CENTER);
        minimapTitle.setStyle(SHADOW_STYLE);
        return minimapTitle;
    }

    private Canvas createMinimapCanvas() {
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(responsiveDims.getMinimapSizeBinding());
        canvas.heightProperty().bind(responsiveDims.getMinimapSizeBinding());

        minimapGraphics = canvas.getGraphicsContext2D();
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> updateMinimap());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> updateMinimap());

        return canvas;
    }

    // ====== THEMED UI UTILITY METHODS ======
    private VBox createThemedPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.TOP_CENTER);
        panel.spacingProperty().bind(responsiveDims.getPanelPaddingBinding());
        panel.prefWidthProperty().bind(responsiveDims.getPanelWidthBinding());
        panel.maxWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(1.2));
        panel.minWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(0.8));
        panel.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        panel.setStyle(
                "-fx-background-color: rgba(139, 69, 19, 0.9); " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;" +
                        SHADOW_STYLE
        );
        return panel;
    }

    private Button createThemedButton(String text) {
        Font buttonFont = loadFont(FONT_PATH, 12, "Courier New");
        Button button = new Button(); // Changed from createStyledButton since it's not defined
        button.setText(text);
        button.setTextFill(Color.WHITE);

        button.setTextOverrun(OverrunStyle.CLIP);

        // Responsive font binding
        button.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(FONT_PATH,
                                responsiveDims.getButtonFontSizeBinding().getValue().intValue(),
                                "Courier New"),
                responsiveDims.getButtonFontSizeBinding()));

        // Size bindings
        button.prefWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(Config.BUTTON_WIDTH_RATIO));
        button.prefHeightProperty().bind(responsiveDims.getButtonHeightBinding());

        button.minWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(0.8));
        button.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
                        Math.max(Config.BUTTON_MIN_HEIGHT,
                                responsiveDims.getButtonHeightBinding().getValue().doubleValue()),
                responsiveDims.getButtonHeightBinding()));

        button.setWrapText(true);
        button.setAlignment(Pos.CENTER);
        button.setContentDisplay(ContentDisplay.CENTER);

        // Set default (base) style
        String baseStyle = "-fx-background-color: #444444;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
        button.setStyle(baseStyle);

        // Set hover effect manually (since we're not using CSS)
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #666666;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);"
        ));

        // Reset style on exit
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    // ====== MESSAGE AREA (with theming) ======
    private void createMessageArea() {
        messageContainer = createThemedMessageContainer();
        messagesArea = createMessagesArea();
        messageScrollPane = createMessageScrollPane();
        messageContainer.getChildren().add(messageScrollPane);
    }

    private VBox createThemedMessageContainer() {
        VBox container = new VBox();
        container.setStyle(
                "-fx-background-color: rgba(42, 42, 42, 0.9); " +
                        "-fx-border-color: " + Config.PANEL_BORDER_COLOR + "; " +
                        "-fx-border-width: 3 0 0 0;" +
                        SHADOW_STYLE
        );
        container.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        return container;
    }

    private VBox createMessagesArea() {
        VBox area = new VBox();
        area.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(2));
        area.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        area.setAlignment(Pos.TOP_LEFT);
        return area;
    }

    private ScrollPane createMessageScrollPane() {
        ScrollPane scrollPane = new ScrollPane(messagesArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
                "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;"
        );
        scrollPane.prefHeightProperty().bind(
                Bindings.max(Config.MESSAGE_AREA_MIN_HEIGHT,
                        Bindings.min(Config.MESSAGE_AREA_MAX_HEIGHT,
                                scene.heightProperty().multiply(Config.MESSAGE_AREA_HEIGHT_RATIO)))
        );
        return scrollPane;
    }

    /**
     * Creates the damage flash overlay that covers the entire game screen
     */
    private javafx.scene.layout.Pane createDamageFlashOverlay() {
        javafx.scene.layout.Pane flashOverlay = new javafx.scene.layout.Pane();
        flashOverlay.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4);"); // Semi-transparent red
        flashOverlay.setVisible(false); // Initially hidden
        flashOverlay.setMouseTransparent(true); // Don't block mouse events

        // Make it cover the entire screen
        flashOverlay.prefWidthProperty().bind(scene.widthProperty());
        flashOverlay.prefHeightProperty().bind(scene.heightProperty());

        return flashOverlay;
    }


    // ====== RESPONSIVE BINDINGS ======
    private void setupResponsiveBindings() {
        // The width available for the canvas is the scene width minus the panels and some padding.
        // The previous magic number '100' was too large. 40 provides a safer buffer.
        NumberBinding availableWidth = scene.widthProperty()
                .subtract(responsiveDims.getPanelWidthBinding().multiply(2))
                .subtract(40);

        // The previous magic number '200' for height was not scalable.
        // Using a ratio of the scene height is more robust for handling the title and message areas.
        NumberBinding availableHeight = scene.heightProperty().multiply(0.70);

        // Bind the canvas to the smaller of the two dimensions to maintain its square shape.
        NumberBinding canvasSizeBinding = Bindings.max(
                Config.CANVAS_MIN_SIZE,
                Bindings.min(availableWidth, availableHeight)
        );

        roomCanvas.widthProperty().bind(canvasSizeBinding);
        roomCanvas.heightProperty().bind(canvasSizeBinding);

        // Add listener to re-render when size changes
        roomCanvas.widthProperty().addListener((obs, oldVal, newVal) -> scaleCanvas());
    }

    // ====== CANVAS SCALING ======
    private void scaleCanvas() {
        currentDimensions = new CanvasDimensions(roomCanvas.getWidth());

        Hero player = getController().getPlayer();
        if (player != null) {
            player.setMovementSpeedForCanvasSize(currentDimensions.getSize());
        }

        Platform.runLater(this::renderRoom);
    }

    // ====== MESSAGE HANDLING ======
    public void addGameMessage(final String message) {
        if (messagesArea == null || messageScrollPane == null) return;

        Label messageLabel = createThemedMessageLabel(message);
        messagesArea.getChildren().add(messageLabel);

        if (messagesArea.getChildren().size() > Config.MAX_MESSAGES) {
            messagesArea.getChildren().remove(0);
        }
        Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }

    private Label createThemedMessageLabel(String message) {
        Label label = new Label("• " + message);
        label.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(FONT_PATH, responsiveDims.getMessageFontSizeBinding().getValue().intValue(), "Monospaced"),
                responsiveDims.getMessageFontSizeBinding()));
        label.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0,
                                responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0),
                responsiveDims.getPanelPaddingBinding()));
        //label.maxWidthProperty().bind(scene.widthProperty().subtract(responsiveDims.getPanelWidthBinding().multiply(2)).subtract(100));
        label.setTextFill(TAN_COLOR);
        label.setWrapText(true);
        label.setStyle(SHADOW_STYLE);
        return label;
    }

    private void loadPillarSprite() {
        try {
            pillarSprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/icons/pillar.png")));
        } catch (Exception e) {
            System.err.println("Could not load pillar sprite: " + e.getMessage());
            pillarSprite = null;
        }
    }

    private void loadChestSprite() {
        try {
            chestSprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/icons/chest.png")));
        } catch (Exception e) {
            System.err.println("Could not load chest sprite: " + e.getMessage());
            chestSprite = null;
        }
    }

    private void checkPillarCollection() {
        Room currentRoom = getCurrentRoom();
        Hero player = getController().getPlayer();

        if (currentRoom == null || player == null || !currentRoom.hasPillar()) {
            return;
        }

        // Check if pillar is already activated
        if (currentRoom.getPillar().isActivated()) {
            return;
        }

        // Calculate pillar center position
        double pillarCenterX = currentDimensions.getSize() / 2;
        double pillarCenterY = currentDimensions.getSize() / 2;

        // Calculate player center position
        double playerCenterX = player.getPixelX() + currentDimensions.getHeroSize() / 2;
        double playerCenterY = player.getPixelY() + currentDimensions.getHeroSize() / 2;

        // Calculate distance between player and pillar
        double distance = Math.sqrt(
                Math.pow(playerCenterX - pillarCenterX, 2) +
                        Math.pow(playerCenterY - pillarCenterY, 2)
        );

        // If player is close enough, collect the pillar
        if (distance <= PILLAR_COLLECTION_DISTANCE) {
            collectPillar(currentRoom);
        }
    }

    private void checkChestCollection() {
        Room currentRoom = getCurrentRoom();
        Hero player = getController().getPlayer();

        if (currentRoom == null || player == null || !currentRoom.hasChest()) {
            hasTriedChestCollection = false;
            return;
        }

        // Check if chest is already opened
        if (currentRoom.isChestOpened() || hasTriedChestCollection) {
            return;
        }

        if(hasTriedChestCollection) {
            return;
        }

        // Smart positioning: if there's a pillar, place chest in a different location
        double chestCenterX, chestCenterY;
        if (currentRoom.hasPillar() && !currentRoom.getPillar().isActivated()) {
            // Place chest in top-right corner if pillar is present
            chestCenterX = currentDimensions.getSize() / 2 + currentDimensions.getTileSize() * 3;
            chestCenterY = currentDimensions.getSize() / 2 - currentDimensions.getTileSize() * 2;
        } else {
            // Place chest in center if no pillar
            chestCenterX = currentDimensions.getSize() / 2;
            chestCenterY = currentDimensions.getSize() / 2;
        }

        // Calculate player center position
        double playerCenterX = player.getPixelX() + currentDimensions.getHeroSize() / 2;
        double playerCenterY = player.getPixelY() + currentDimensions.getHeroSize() / 2;

        // Calculate distance between player and chest
        double distance = Math.sqrt(
                Math.pow(playerCenterX - chestCenterX, 2) +
                        Math.pow(playerCenterY - chestCenterY, 2)
        );

        // If player is close enough, collect the chest
        if (distance <= CHEST_COLLECTION_DISTANCE) {
            collectChest(currentRoom);
        } else {
            hasShownGoldWarning = false;
        }
    }

    private void collectPillar(Room room) {
        try {
            Hero player = getController().getPlayer();

            // Activate the pillar
            room.getPillar().activate(player);

            // Update player's pillar count
            player.setPillarsActivated(player.getPillarsActivated() + 1);

            // Add collection message
            addGameMessage("You collected the " + room.getPillar().getType() + " Pillar!");

            // Update stats display
            updatePlayerStats();

            // Update minimap to reflect the change
            updateMinimap();

            // Check if all pillars are collected
            if (player.getPillarsActivated() >= 4) {
                addGameMessage("All pillars collected! You can now challenge the final boss!");
            }

        } catch (Exception e) {
            System.err.println("Error collecting pillar: " + e.getMessage());
        }
    }

    private void collectChest(Room room) {
        try {
            Hero player = getController().getPlayer();

            // Check if player has enough gold (5 gold required to open chest)
            if (player.getGold() < 5) {
                if (!hasShownGoldWarning) {
                    addGameMessage("You need 5 gold to open this chest. Current gold: " + player.getGold());
                    hasShownGoldWarning = true;
                }
                return;
            }

            int originalGold = player.getGold();

            int originalInventorySize = player.getInventory().size();

            // Open the chest (this handles the gold cost and item collection)
            room.openChest(player);

            boolean chestWasOpened = player.getGold() < originalGold;

            if (chestWasOpened) {
                hasTriedChestCollection = true;

                // Check what was actually collected by comparing inventory
                List<Item> currentInventory = player.getInventory();
                List<Item> newlyCollectedItems = new ArrayList<>();

                // Get the newly added items (from the end of the inventory)
                for (int i = originalInventorySize; i < currentInventory.size(); i++) {
                    newlyCollectedItems.add(currentInventory.get(i));
                }

                // Add collection message
                if (!newlyCollectedItems.isEmpty()) {
                    addGameMessage("Chest opened! Found:");
                    for (Item item : newlyCollectedItems) {
                        addGameMessage("• " + item.getName() + " - " + item.getDescription());
                    }
                } else {
                    addGameMessage("Chest opened but was empty!");
                }
            }

            // Update stats display
            updatePlayerStats();

            // Update minimap to reflect the change
            updateMinimap();

        } catch (Exception e) {
            System.err.println("Error collecting chest: " + e.getMessage());
        }
    }

    // ====== HERO INITIALIZATION AND GAME LOOP ======
    private void initializeHeroPosition() {
        Hero player = getController().getPlayer();
        if (player != null && player.getPixelX() == 0 && player.getPixelY() == 0) {
            double centerPos = currentDimensions.getSize() / 2 - currentDimensions.getHeroSize() / 2;
            player.setPixelPosition(centerPos, centerPos);
        }
    }

    private void initializeHeroMovementSpeed() {
        Hero player = getController().getPlayer();
        if (player != null) {
            player.setMovementSpeedForCanvasSize(currentDimensions.getSize());
        }
    }

    private void startGameLoop() {
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (shouldUpdateGame()) {
                        updateGame();
                    }
                    renderRoom(); // Always render for smooth visuals
                }
            };
        }
        gameLoop.start();
    }

    /**
     * Intense damage flash effect with multiple pulses and screen shake feel
     */
    public void flashDamageEffect() {
        if (damageFlashOverlay == null) {
            System.err.println("Warning: damageFlashOverlay is null, cannot show damage flash");
            return;
        }

        // Show the flash overlay
        damageFlashOverlay.setVisible(true);
        damageFlashOverlay.setOpacity(0.0);

        // Create intense pulsing effect with multiple flashes
        javafx.animation.Timeline flashTimeline = new javafx.animation.Timeline(
                // Initial flash - quick and bright
                new javafx.animation.KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.0)),
                new javafx.animation.KeyFrame(Duration.millis(50),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.8)), // Very bright flash

                // First pulse
                new javafx.animation.KeyFrame(Duration.millis(150),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.2)),
                new javafx.animation.KeyFrame(Duration.millis(250),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.6)), // Second pulse

                // Second pulse
                new javafx.animation.KeyFrame(Duration.millis(350),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.1)),
                new javafx.animation.KeyFrame(Duration.millis(450),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.4)), // Third pulse

                // Final fade out
                new javafx.animation.KeyFrame(Duration.millis(600),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.2)),
                new javafx.animation.KeyFrame(Duration.millis(1200),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.0))
        );

        // Hide the overlay when animation completes
        flashTimeline.setOnFinished(event -> damageFlashOverlay.setVisible(false));

        // Play the animation
        flashTimeline.play();

        System.out.println("Intense damage flash effect triggered");
    }


    private boolean shouldUpdateGame() {
        Controller c = getController();
        return c != null && c.getGameController() != null && c.getPlayer() != null &&
                c.getGameController().getStateController().isInState(StateController.GameState.EXPLORING);
    }

    private void updateGame() {
        Hero player = getController().getPlayer();
        if (player != null) {
            player.updatePixelPosition();
            player.updateAnimation(System.nanoTime());

            checkPillarCollection();
            checkChestCollection();

            if (checkRoomTransition()) {
                onRoomChanged();
            }

            pillarGlowAnimation += PILLAR_GLOW_SPEED;
            if (pillarGlowAnimation > Math.PI * 2) {
                pillarGlowAnimation = 0.0;
            }

            chestGlowAnimation += CHEST_GLOW_SPEED;
            if (chestGlowAnimation > Math.PI * 2) {
                chestGlowAnimation = 0.0;
            }

            updatePortraitCanvas();
            updatePlayerStats();
        }
    }

    public void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    // ====== MOVEMENT AND ROOM TRANSITION LOGIC ======
    private boolean checkRoomTransition() {
        Hero player = getController().getPlayer();
        Room currentRoom = getCurrentRoom();
        if (player == null || currentRoom == null) return false;

        double px = player.getPixelX();
        double py = player.getPixelY();
        double heroSize = currentDimensions.getHeroSize();
        boolean transitioned = false;

        // North
        if (py <= currentDimensions.getBoundaryTop() && currentRoom.hasNorthDoor()) {
            if (isPlayerInDoorRange(px, heroSize)) {
                getController().getGameController().movePlayerNorth();
                player.setPixelPosition(px, currentDimensions.getBoundaryBottom() - 1);
                transitioned = true;
            }
        }
        // South
        else if (py >= currentDimensions.getBoundaryBottom() && currentRoom.hasSouthDoor()) {
            if (isPlayerInDoorRange(px, heroSize)) {
                getController().getGameController().movePlayerSouth();
                player.setPixelPosition(px, currentDimensions.getBoundaryTop() + 1);
                transitioned = true;
            }
        }
        // West
        else if (px <= currentDimensions.getBoundaryLeft() && currentRoom.hasWestDoor()) {
            if (isPlayerInDoorRange(py, heroSize)) {
                getController().getGameController().movePlayerWest();
                player.setPixelPosition(currentDimensions.getBoundaryRight() - 1, py);
                transitioned = true;
            }
        }
        // East
        else if (px >= currentDimensions.getBoundaryRight() && currentRoom.hasEastDoor()) {
            if (isPlayerInDoorRange(py, heroSize)) {
                getController().getGameController().movePlayerEast();
                player.setPixelPosition(currentDimensions.getBoundaryLeft() + 1, py);
                transitioned = true;
            }
        }

        if (!transitioned) {
            double constrainedX = Math.max(currentDimensions.getBoundaryLeft(), Math.min(currentDimensions.getBoundaryRight(), px));
            double constrainedY = Math.max(currentDimensions.getBoundaryTop(), Math.min(currentDimensions.getBoundaryBottom(), py));
            player.setPixelPosition(constrainedX, constrainedY);
        }

        return transitioned;
    }

    private boolean isPlayerInDoorRange(double playerPos, double heroSize) {
        double playerCenter = playerPos + heroSize / 2;
        double doorWidth = currentDimensions.getDoorWidth();
        double canvasSize = currentDimensions.getSize();
        double doorStart = (canvasSize - doorWidth) / 2;
        double doorEnd = doorStart + doorWidth;

        return playerCenter >= doorStart && playerCenter <= doorEnd;
    }

    // ====== ADVANCED RENDERING LOGIC ======
    private void renderRoom() {
        if (graphicsContext == null) return;

        clearCanvas();
        drawFloor();
        drawWalls();
        drawPillar();
        drawChest();
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
                graphicsContext.setFill(((x + y) % 2 == 0) ? darkStone : lightStone);
                graphicsContext.fillRect(x * currentDimensions.getTileSize(), y * currentDimensions.getTileSize(),
                        currentDimensions.getTileSize(), currentDimensions.getTileSize());
            }
        }
    }

    private void drawWalls() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) return;

        Color wallColor = Color.rgb(80, 85, 100);
        Color doorColor = Color.rgb(25, 25, 30); // The "empty" space of the door

        double size = currentDimensions.getSize();
        double thickness = currentDimensions.getWallThickness();
        double doorWidth = currentDimensions.getDoorWidth();

        // Draw walls with proper coordinates
        // North wall (top)
        drawWall(0, 0, size, thickness, currentRoom.hasNorthDoor(), true, doorWidth, wallColor, doorColor);

        // South wall (bottom)
        drawWall(0, size - thickness, size, thickness, currentRoom.hasSouthDoor(), true, doorWidth, wallColor, doorColor);

        // West wall (left)
        drawWall(0, 0, thickness, size, currentRoom.hasWestDoor(), false, doorWidth, wallColor, doorColor);

        // East wall (right)
        drawWall(size - thickness, 0, thickness, size, currentRoom.hasEastDoor(), false, doorWidth, wallColor, doorColor);
    }

    private void drawWall(double x, double y, double width, double height, boolean hasDoor, boolean isHorizontal,
                          double doorSize, Color wallColor, Color doorColor) {

        // Always draw the base wall first
        graphicsContext.setFill(wallColor);
        graphicsContext.fillRect(x, y, width, height);

        // If there's a door, draw the opening over the wall
        if (hasDoor) {
            graphicsContext.setFill(doorColor);

            if (isHorizontal) {
                // For horizontal walls (north/south), door is centered horizontally
                double doorStart = x + (width - doorSize) / 2;
                graphicsContext.fillRect(doorStart, y, doorSize, height);
            } else {
                // For vertical walls (east/west), door is centered vertically
                double doorStart = y + (height - doorSize) / 2;
                graphicsContext.fillRect(x, doorStart, width, doorSize);
            }
        }
    }

    private void drawPillar() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null || !currentRoom.hasPillar() || currentRoom.getPillar().isActivated()) {
            return;
        }

        double pillarSize = currentDimensions.getTileSize() * 4; // Make pillar 2 tiles big
        double pillarX = (currentDimensions.getSize() - pillarSize) / 2;
        double pillarY = (currentDimensions.getSize() - pillarSize) / 2;

        if (pillarSprite != null) {
            // Calculate glow effect
            double glowIntensity = (Math.sin(pillarGlowAnimation) + 1.0) / 2.0; // 0.0 to 1.0
            double alpha = 0.7 + (glowIntensity * 0.3); // 0.7 to 1.0 alpha

            // Save the current global alpha
            double originalAlpha = graphicsContext.getGlobalAlpha();

            // Apply glow effect
            graphicsContext.setGlobalAlpha(alpha);

            // Draw the pillar sprite
            graphicsContext.drawImage(pillarSprite, pillarX, pillarY, pillarSize, pillarSize);

            // Draw a subtle glow around the pillar
            graphicsContext.setGlobalAlpha(glowIntensity * 0.3);
            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.fillOval(pillarX - 5, pillarY - 5, pillarSize + 10, pillarSize + 10);

            // Restore original alpha
            graphicsContext.setGlobalAlpha(originalAlpha);
        } else {
            // Fallback rendering if sprite is missing
            double glowIntensity = (Math.sin(pillarGlowAnimation) + 1.0) / 2.0;

            // Draw glow effect
            graphicsContext.setFill(Color.rgb(255, 255, 0, glowIntensity * 0.3));
            graphicsContext.fillOval(pillarX - 10, pillarY - 10, pillarSize + 20, pillarSize + 20);

            // Draw pillar base
            graphicsContext.setFill(Color.rgb(200, 200, 200));
            graphicsContext.fillRect(pillarX, pillarY, pillarSize, pillarSize);

            // Draw pillar top with glow
            Color pillarColor = Color.rgb(
                    (int)(255 * (0.7 + glowIntensity * 0.3)),
                    (int)(255 * (0.7 + glowIntensity * 0.3)),
                    0
            );
            graphicsContext.setFill(pillarColor);
            graphicsContext.fillRect(pillarX + 5, pillarY + 5, pillarSize - 10, pillarSize - 10);
        }
    }

    private void drawChest() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null || !currentRoom.hasChest()) {
            return; // No chest in room
        }

        // Check if chest is opened OR if we've already tried to collect it
        if (currentRoom.isChestOpened()) {
            return; // Don't draw if chest is opened
        }

        double chestSize = currentDimensions.getTileSize() * 3; // Make chest 3 tiles big

        // Smart positioning: if there's a pillar, place chest in a different location
        double chestX, chestY;
        if (currentRoom.hasPillar() && !currentRoom.getPillar().isActivated()) {
            // Place chest in top-right area if pillar is present
            chestX = (currentDimensions.getSize() / 2 + currentDimensions.getTileSize() * 3) - (chestSize / 2);
            chestY = (currentDimensions.getSize() / 2 - currentDimensions.getTileSize() * 2) - (chestSize / 2);
        } else {
            // Place chest in center if no pillar
            chestX = (currentDimensions.getSize() - chestSize) / 2;
            chestY = (currentDimensions.getSize() - chestSize) / 2;
        }

        // Rest of the drawing code remains the same...
        if (chestSprite != null) {
            // Calculate glow effect
            double glowIntensity = (Math.sin(chestGlowAnimation) + 1.0) / 2.0; // 0.0 to 1.0
            double alpha = 0.7 + (glowIntensity * 0.3); // 0.7 to 1.0 alpha

            // Save the current global alpha
            double originalAlpha = graphicsContext.getGlobalAlpha();

            // Apply glow effect
            graphicsContext.setGlobalAlpha(alpha);

            // Draw the chest sprite
            graphicsContext.drawImage(chestSprite, chestX, chestY, chestSize, chestSize);

            // Draw a subtle glow around the chest (golden glow)
            graphicsContext.setGlobalAlpha(glowIntensity * 0.3);
            graphicsContext.setFill(Color.GOLD);
            graphicsContext.fillOval(chestX - 5, chestY - 5, chestSize + 10, chestSize + 10);

            // Restore original alpha
            graphicsContext.setGlobalAlpha(originalAlpha);
        } else {
            // Fallback rendering remains the same...
            double glowIntensity = (Math.sin(chestGlowAnimation) + 1.0) / 2.0;

            // Draw glow effect (golden)
            graphicsContext.setFill(Color.rgb(255, 215, 0, glowIntensity * 0.3));
            graphicsContext.fillOval(chestX - 10, chestY - 10, chestSize + 20, chestSize + 20);

            // Draw chest base (brown)
            graphicsContext.setFill(Color.rgb(139, 69, 19));
            graphicsContext.fillRect(chestX, chestY, chestSize, chestSize);

            // Draw chest details with glow
            Color chestColor = Color.rgb(
                    (int)(255 * (0.7 + glowIntensity * 0.3)),
                    (int)(215 * (0.7 + glowIntensity * 0.3)),
                    0
            );
            graphicsContext.setFill(chestColor);
            graphicsContext.fillRect(chestX + 5, chestY + 5, chestSize - 10, chestSize - 10);

            // Draw chest lock
            graphicsContext.setFill(Color.DARKGRAY);
            graphicsContext.fillOval(chestX + chestSize/2 - 5, chestY + chestSize/2 - 5, 10, 10);
        }
    }

    private void drawPlayer() {
        Hero player = getController().getPlayer();
        if (player == null) return;

        double heroSize = currentDimensions.getHeroSize();
        Image spriteSheet = player.getSpriteSheet();

        if (spriteSheet != null) {
            graphicsContext.drawImage(spriteSheet,
                    player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH,
                    player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    player.getPixelX(), player.getPixelY(), heroSize, heroSize);
        } else {
            // Fallback rendering if sprite is missing
            graphicsContext.setFill(Color.BLUE);
            graphicsContext.fillRect(player.getPixelX(), player.getPixelY(), heroSize, heroSize);
        }
    }

    private void updatePortraitCanvas() {
        if (portraitGraphics == null) return;

        Hero player = getController().getPlayer();
        if (player == null) return;

        Canvas canvas = portraitGraphics.getCanvas();
        double w = canvas.getWidth(), h = canvas.getHeight();

        portraitGraphics.setFill(Color.rgb(40, 40, 45));
        portraitGraphics.fillRect(0, 0, w, h);

        double size = Math.min(w, h) * 0.8;
        double x = (w - size) / 2;
        double y = (h - size) / 2;

        Image spriteSheet = player.getSpriteSheet();
        if (spriteSheet != null) {
            portraitGraphics.drawImage(spriteSheet,
                    player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH,
                    player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    x, y, size, size);
        }
    }

    // ====== ADVANCED MINIMAP LOGIC ======
    public void updateMinimap() {
        if (getController() == null || getController().getDungeon() == null || minimapGraphics == null) {
            return;
        }
        clearMinimap();
        drawMinimapBorder();
        drawMinimapRooms();
    }

    private void clearMinimap() {
        double size = minimapCanvas.getWidth();
        minimapGraphics.clearRect(0, 0, size, size);
        minimapGraphics.setFill(Color.BLACK);
        minimapGraphics.fillRect(0, 0, size, size);
    }

    private void drawMinimapBorder() {
        double size = minimapCanvas.getWidth();
        minimapGraphics.setStroke(Color.rgb(139, 69, 19)); // Same as panel border color
        minimapGraphics.setLineWidth(2);
        minimapGraphics.strokeRect(1, 1, size - 2, size - 2);
    }

    private void drawMinimapRooms() {
        Dungeon dungeon = getController().getDungeon();
        Hero player = getController().getPlayer();
        if (player == null) return;

        Point playerPos = player.getPosition();
        int gridSize = (int) Config.MINIMAP_GRID_SIZE;

        double canvasSize = minimapCanvas.getWidth();
        double roomDisplaySize = canvasSize / (gridSize + 2); // Dynamic room size
        double totalGridSize = roomDisplaySize * gridSize;
        double startDrawX = (canvasSize - totalGridSize) / 2;
        double startDrawY = (canvasSize - totalGridSize) / 2;

        int startX = playerPos.getX() - gridSize / 2;
        int startY = playerPos.getY() - gridSize / 2;

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int mapX = startX + x;
                int mapY = startY + y;
                Room room = dungeon.getRoom(mapX, mapY);

                if (room != null && room.isVisited()) {
                    boolean isPlayerHere = (mapX == playerPos.getX() && mapY == playerPos.getY());
                    double roomDrawX = startDrawX + x * roomDisplaySize;
                    double roomDrawY = startDrawY + y * roomDisplaySize;
                    drawMinimapRoom(room, roomDrawX, roomDrawY, roomDisplaySize, isPlayerHere);
                }
            }
        }
    }

    private void drawMinimapRoom(Room room, double x, double y, double size, boolean isPlayerPosition) {
        Color roomColor = getMinimapRoomColor(room, isPlayerPosition);
        minimapGraphics.setFill(roomColor);
        minimapGraphics.fillRect(x, y, size, size);

        if (isPlayerPosition) {
            minimapGraphics.setStroke(Color.WHITE);
            minimapGraphics.setLineWidth(1.5);
            minimapGraphics.strokeRect(x, y, size, size);
        }
    }

    private Color getMinimapRoomColor(Room room, boolean isPlayerPosition) {
        if (isPlayerPosition) return Color.LIME;
        if (!room.isVisited()) return Color.TRANSPARENT; // Don't draw if not visited

        switch (room.getRoomType()) {
            case ENTRANCE: return Color.LIGHTGREEN;
            case EXIT: return Color.ORANGE;
            case BOSS: return Color.DARKRED;
            case PILLAR: return room.hasPillar() && room.getPillar().isActivated() ? Color.PURPLE : Color.MEDIUMPURPLE;
            case MONSTER: return room.getMonsters().isEmpty() ? Color.LIGHTGRAY : Color.RED;
            case TREASURE: return room.isChestOpened() ? Color.LIGHTGRAY : Color.GOLD;
            case TRAP: return room.hasTrap() && room.getTrap().isSprung() ? Color.LIGHTGRAY : Color.ORANGERED;
            case EMPTY:
            default: return Color.LIGHTGRAY;
        }
    }

    // ====== UTILITY AND UPDATE METHODS ======
    public void updatePlayerStats() {
        Hero player = getController().getPlayer();
        if (player != null) {
            Platform.runLater(() -> {
                playerHealthLabel.setText("HP: " + player.getHealth() + "/" + player.getMaxHealth());
                playerManaLabel.setText("MP: " + player.getSpecialMana() + "/4");
                playerAttackLabel.setText("ATK: " + player.getType().getBaseAttack());
                playerGoldLabel.setText("Gold: " + player.getGold());
                playerPillarsLabel.setText("Pillars: " + player.getPillarsActivated() + "/4" );
            });
        }
    }

    public void onRoomChanged() {
        hasTriedChestCollection = false;
        updateMinimap();
        updatePlayerStats();
        if (getController().getGameController() != null) {
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }

    private Room getCurrentRoom() {
        Controller c = getController();
        if (c == null || c.getPlayer() == null || c.getDungeon() == null) {
            return null;
        }
        return c.getDungeon().getRoom(c.getPlayer().getPosition());
    }

    // ====== CONFIGURATION AND DIMENSION CLASSES ======
    private static final class Config {
        // Canvas
        static final double DEFAULT_CANVAS_SIZE = 480.0;
        static final double CANVAS_MIN_SIZE = 300.0;
        static final double CANVAS_MAX_SIZE = 800.0;
        // Game Elements
        static final double TILES_PER_CANVAS = 30.0;
        static final double HERO_SIZE_MULTIPLIER = 2.0;
        static final double DOOR_WIDTH_MULTIPLIER = 2.0;
        // UI Panels
        static final double PANEL_PREF_WIDTH_RATIO = 0.14;
        static final double PANEL_MIN_WIDTH_ABSOLUTE = 160.0;
        static final double PANEL_MAX_WIDTH_ABSOLUTE = 220.0;
        static final double PANEL_PADDING_RATIO = 0.008;
        // UI Buttons
        static final double BUTTON_HEIGHT_RATIO = 0.045;
        static final double BUTTON_MIN_HEIGHT = 30.0;
        static final double BUTTON_MAX_HEIGHT = 45.0;
        static final double BUTTON_WIDTH_RATIO = 0.95;
        // Minimap
        static final double MINIMAP_SIZE_RATIO = 0.08;
        static final double MINIMAP_MIN_SIZE = 70.0;
        static final double MINIMAP_MAX_SIZE = 120.0; // Increased max size slightly
        static final double MINIMAP_GRID_SIZE = 7.0; // For detailed minimap
        // Message Area
        static final double MESSAGE_AREA_HEIGHT_RATIO = 0.08;
        static final double MESSAGE_AREA_MIN_HEIGHT = 50.0;
        static final double MESSAGE_AREA_MAX_HEIGHT = 100.0;
        static final int MAX_MESSAGES = 20;
        // Fonts
        static final double STATS_FONT_SIZE_RATIO = 0.012;
        static final double STATS_FONT_MIN_SIZE = 10.0; // Added min font size
        static final double STATS_FONT_MAX_SIZE = 20.0; // Added max font size
        static final double STATS_ICON_SIZE_RATIO = 0.009; // New: for icon scaling
        static final double STATS_ICON_MIN_SIZE = 12.0; // Min icon size
        static final double STATS_ICON_MAX_SIZE = 20.0; // Max icon size
        static final double TITLE_FONT_SIZE_RATIO = 0.020;
        static final double MESSAGE_FONT_SIZE_RATIO = 0.007;
        static final double BUTTON_FONT_SIZE_RATIO = 0.018;
        static final double BUTTON_FONT_MIN_SIZE = 8.0;
        static final double BUTTON_FONT_MAX_SIZE = 16.0;

        // Traps


        // Colors
        static final String PANEL_BORDER_COLOR = "#DAA520";
    }

    private static class CanvasDimensions {
        private final double size, tileSize, heroSize, wallThickness, doorWidth;

        public CanvasDimensions(double canvasSize) {
            this.size = Math.max(Config.CANVAS_MIN_SIZE, Math.min(Config.CANVAS_MAX_SIZE, canvasSize));
            this.tileSize = this.size / Config.TILES_PER_CANVAS;
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

    private static class ResponsiveDimensions {
        private final NumberBinding panelWidthBinding, panelPaddingBinding, buttonHeightBinding, minimapSizeBinding;
        private final NumberBinding statsFontSizeBinding, titleFontSizeBinding, messageFontSizeBinding, buttonFontSizeBinding, statsIconSizeBinding;

        public ResponsiveDimensions(Scene scene) {
            panelWidthBinding = Bindings.createDoubleBinding(() -> {
                double sceneWidth = scene.getWidth();
                double calculated = sceneWidth * Config.PANEL_PREF_WIDTH_RATIO;
                return Math.max(Config.PANEL_MIN_WIDTH_ABSOLUTE, Math.min(Config.PANEL_MAX_WIDTH_ABSOLUTE, calculated));
            }, scene.widthProperty());

            panelPaddingBinding = Bindings.createDoubleBinding(() -> {
                double basePadding = scene.getWidth() * Config.PANEL_PADDING_RATIO;
                return Math.max(5.0, Math.min(15.0, basePadding));
            }, scene.widthProperty());

            buttonHeightBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * Config.BUTTON_HEIGHT_RATIO;
                return Math.max(Config.BUTTON_MIN_HEIGHT, Math.min(Config.BUTTON_MAX_HEIGHT, calculated));
            }, scene.heightProperty());

            minimapSizeBinding = Bindings.createDoubleBinding(() -> {
                double smallerDimension = Math.min(scene.getWidth(), scene.getHeight());
                double calculated = smallerDimension * Config.MINIMAP_SIZE_RATIO;
                return Math.max(Config.MINIMAP_MIN_SIZE, Math.min(Config.MINIMAP_MAX_SIZE, calculated));
            }, scene.widthProperty(), scene.heightProperty());

            // FIX: Added min/max bounds for statsFontSizeBinding
            statsFontSizeBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * Config.STATS_FONT_SIZE_RATIO;
                return Math.max(Config.STATS_FONT_MIN_SIZE, Math.min(Config.STATS_FONT_MAX_SIZE, calculated));
            }, scene.heightProperty());

            // FIX: Added statsIconSizeBinding
            statsIconSizeBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * Config.STATS_ICON_SIZE_RATIO;
                return Math.max(Config.STATS_ICON_MIN_SIZE, Math.min(Config.STATS_ICON_MAX_SIZE, calculated));
            }, scene.heightProperty());


            titleFontSizeBinding = Bindings.createDoubleBinding(() -> Math.max(20.0, scene.getHeight() * Config.TITLE_FONT_SIZE_RATIO), scene.heightProperty());
            messageFontSizeBinding = Bindings.createDoubleBinding(() -> Math.max(9.0, scene.getHeight() * Config.MESSAGE_FONT_SIZE_RATIO), scene.heightProperty());
            buttonFontSizeBinding = Bindings.createDoubleBinding(() -> {
                double sceneHeight = scene.getHeight();
                double sceneWidth = scene.getWidth();

                // Base font size on both dimensions for better scaling
                double heightBasedSize = sceneHeight * Config.BUTTON_FONT_SIZE_RATIO;
                double widthBasedSize = sceneWidth * (Config.BUTTON_FONT_SIZE_RATIO * 0.5);

                // Use the smaller of the two to ensure text fits
                double calculated = Math.min(heightBasedSize, widthBasedSize);

                return Math.max(Config.BUTTON_FONT_MIN_SIZE,
                        Math.min(Config.BUTTON_FONT_MAX_SIZE, calculated));
            }, scene.heightProperty(), scene.widthProperty());
        }

        public NumberBinding getPanelWidthBinding() { return panelWidthBinding; }
        public NumberBinding getPanelPaddingBinding() { return panelPaddingBinding; }
        public NumberBinding getButtonHeightBinding() { return buttonHeightBinding; }
        public NumberBinding getMinimapSizeBinding() { return minimapSizeBinding; }
        public NumberBinding getStatsFontSizeBinding() { return statsFontSizeBinding; }
        public NumberBinding getStatsIconSizeBinding() { return statsIconSizeBinding; } // New getter
        public NumberBinding getTitleFontSizeBinding() { return titleFontSizeBinding; }
        public NumberBinding getMessageFontSizeBinding() { return messageFontSizeBinding; }
        public NumberBinding getButtonFontSizeBinding() { return buttonFontSizeBinding; }
    }
}