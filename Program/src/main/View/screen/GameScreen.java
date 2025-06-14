package main.View.screen;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;
import main.View.screen.gamescreen.*;

public class GameScreen extends Screen {

    private final GameScreenRenderer renderer;
    private final GameScreenUIManager uiManager;
    private final GameScreenGameLoop gameLoop;
    private final GameScreenInputHandler inputHandler;
    private final GameScreenCanvasDimensions canvasDimensions;

    private Scene scene;
    private StackPane scalableRoot;

    public GameScreen(final Stage primaryStage, final Controller controller) {
        super(primaryStage, controller);
        this.canvasDimensions = new GameScreenCanvasDimensions();
        this.renderer = new GameScreenRenderer(controller, canvasDimensions);
        this.uiManager = new GameScreenUIManager(controller, renderer);
        this.gameLoop = new GameScreenGameLoop(controller, renderer, uiManager, canvasDimensions);
        this.inputHandler = new GameScreenInputHandler(controller, gameLoop);
    }

    @Override
    public void showScreen(final GameUI gameUI) {
        initializeScreen(gameUI);
        setupScene(gameUI);
        setupEventHandlers(gameUI);
        finalizeScreen();

        Platform.runLater(() -> {
            if (isModelInitialized()) {
                startGameSession();
            } else {
                retryInitialization();
            }
        });
    }

    private void initializeScreen(GameUI gameUI) {
        scalableRoot = new StackPane();

        double currentWidth = getStage().getScene() != null ?
                getStage().getScene().getWidth() : BASE_WIDTH;
        double currentHeight = getStage().getScene() != null ?
                getStage().getScene().getHeight() : BASE_HEIGHT;

        scene = new Scene(scalableRoot, currentWidth, currentHeight);

        renderer.initialize(scene);
        uiManager.initialize(scene);
    }

    private void setupScene(GameUI gameUI) {
        BorderPane mainContent = createMainContent(gameUI);
        scalableRoot.getChildren().addAll(mainContent, uiManager.getDamageFlashOverlay());

        uiManager.setupResponsiveBindings();
        renderer.setupCanvas();
    }

    private BorderPane createMainContent(GameUI gameUI) {
        BorderPane root = new BorderPane();
        uiManager.setupThemedBackground(root);

        HBox mainGameArea = uiManager.createMainGameArea(gameUI);
        uiManager.createMessageArea();

        root.setCenter(mainGameArea);
        root.setBottom(uiManager.getMessageContainer());

        return root;
    }

    private void setupEventHandlers(GameUI gameUI) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                getController().pauseGame(null, gameUI);
            }
        });
    }

    private void finalizeScreen() {
        boolean wasFullScreen = getStage().isFullScreen();

        getStage().setScene(scene);
        getStage().setTitle("Dungeon Dive - Pixel Adventure");

        if (wasFullScreen) {
            getStage().setFullScreen(true);
        }

        if (!getStage().isShowing()) {
            getStage().show();
        }
    }

    private boolean isModelInitialized() {
        return getController() != null &&
                getController().getPlayer() != null &&
                getController().getDungeon() != null;
    }

    private void startGameSession() {
        renderer.loadSprites();
        renderer.initializeHeroPosition();
        renderer.scaleCanvas();
        renderer.renderRoom();

        uiManager.updatePlayerStats();
        uiManager.updateMinimap();
        gameLoop.start();

        uiManager.addGameMessage("Welcome to the dungeon!");
        if (getController().getGameController() != null) {
            uiManager.addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }

    private void retryInitialization() {
        System.err.println("GameScreen: Model not fully initialized yet, retrying...");
        Platform.runLater(() -> {
            if (isModelInitialized()) {
                startGameSession();
            } else {
                System.err.println("GameScreen: Model still not initialized, starting partial session");
                gameLoop.start();
                uiManager.updatePlayerStats();
            }
        });
    }

    // Public interface methods
    public void addGameMessage(String message) {
        uiManager.addGameMessage(message);
    }

    public void updatePlayerStats() {
        uiManager.updatePlayerStats();
    }

    public void updateMinimap() {
        uiManager.updateMinimap();
    }

    public void flashDamageEffect() {
        uiManager.flashDamageEffect();
    }

    public void onRoomChanged() {
        renderer.onRoomChanged();
        uiManager.onRoomChanged();
    }

    public void stopGameLoop() {
        gameLoop.stop();
    }

    public GameScreenCanvasDimensions getCanvasDimensions() {
        return canvasDimensions;
    }
}