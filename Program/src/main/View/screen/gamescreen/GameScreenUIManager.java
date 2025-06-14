package main.View.screen.gamescreen;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.View.GameUI;

import java.io.InputStream;
import java.util.Objects;

public class GameScreenUIManager {
    private final Controller controller;
    private final GameScreenTheme theme;
    private GameScreenResponsiveDimensions responsiveDims;
    private final GameScreenRenderer renderer;
    private final GameScreenMinimapRenderer minimapRenderer;
    private final GameScreenPortraitRenderer portraitRenderer;


    // UI Components
    private Label playerHealthLabel;
    private Label playerManaLabel;
    private Label playerAttackLabel;
    private Label playerGoldLabel;
    private Label playerPillarsLabel;
    private VBox messagesArea;
    private ScrollPane messageScrollPane;
    private VBox messageContainer;
    private Pane damageFlashOverlay;
    private Canvas minimapCanvas;
    private Canvas portraitCanvas;

    public GameScreenUIManager(Controller controller, GameScreenRenderer renderer) {
        this.controller = controller;
        this.renderer = renderer;
        this.theme = new GameScreenTheme();
        this.minimapRenderer = new GameScreenMinimapRenderer(controller);
        this.portraitRenderer = new GameScreenPortraitRenderer(controller);
    }

    public void initialize(Scene scene) {
        this.responsiveDims = new GameScreenResponsiveDimensions(scene);
        createDamageFlashOverlay(scene);
    }

    public void setupThemedBackground(BorderPane root) {
        try (InputStream bgStream = getClass().getResourceAsStream(theme.getBackgroundPath())) {
            if (bgStream != null) {
                root.setStyle(theme.getBackgroundStyle());
            } else {
                root.setStyle(theme.getFallbackBackgroundStyle());
            }
        } catch (Exception e) {
            root.setStyle(theme.getFallbackBackgroundStyle());
        }
    }

    public HBox createMainGameArea(GameUI gameUI) {
        HBox mainGameArea = new HBox();
        mainGameArea.setStyle(theme.getMainGameAreaStyle());
        mainGameArea.setFillHeight(true);
        mainGameArea.setMinWidth(600);

        VBox leftPanel = createLeftPanel();
        VBox centerArea = createCenterArea();
        VBox rightPanel = createRightPanel(gameUI);

        HBox.setHgrow(leftPanel, Priority.NEVER);
        HBox.setHgrow(centerArea, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.NEVER);

        mainGameArea.getChildren().addAll(leftPanel, centerArea, rightPanel);
        return mainGameArea;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = createThemedPanel();

        VBox statsBox = createPlayerStatsBox();
        VBox portraitSection = createCharacterPortrait();
        Button inventoryButton = createThemedButton("INV");
        inventoryButton.setOnAction(e -> controller.getGameController().openInventory());

        leftPanel.getChildren().addAll(statsBox, portraitSection, inventoryButton);
        return leftPanel;
    }

    private VBox createPlayerStatsBox() {
        VBox statsBox = new VBox();
        statsBox.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(3));
        statsBox.setStyle(theme.getStatsBoxStyle());

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
        return statsBox;
    }

    private HBox createStatBox(String iconPath, Label valueLabel) {
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
        ImageView iconView = new ImageView(iconImage);

        iconView.fitWidthProperty().bind(responsiveDims.getStatsIconSizeBinding());
        iconView.fitHeightProperty().bind(responsiveDims.getStatsIconSizeBinding());
        iconView.setPreserveRatio(true);

        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(Double.MAX_VALUE);

        HBox statBox = new HBox(6, iconView, valueLabel);
        statBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(valueLabel, Priority.ALWAYS);

        statBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(0, responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0, 0),
                responsiveDims.getPanelPaddingBinding()));

        return statBox;
    }

    private Label createThemedStatLabel() {
        Label label = new Label();
        label.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(), responsiveDims.getStatsFontSizeBinding().getValue().intValue(), "Monospaced"),
                responsiveDims.getStatsFontSizeBinding()));
        label.setTextFill(theme.getTanColor());
        label.setStyle(theme.getShadowStyle());
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    private VBox createCharacterPortrait() {
        VBox portraitSection = new VBox();
        portraitSection.setAlignment(Pos.CENTER);

        portraitCanvas = new Canvas();
        portraitCanvas.widthProperty().bind(responsiveDims.getPortraitSizeBinding());
        portraitCanvas.heightProperty().bind(responsiveDims.getPortraitSizeBinding());

        portraitRenderer.initialize(portraitCanvas);

        StackPane portraitContainer = new StackPane(portraitCanvas);
        portraitContainer.setStyle(
                "-fx-border-color: " + theme.getPanelBorderColor() + "; " +
                        "-fx-border-width: 2px; " +
                        "-fx-background-color: black;"
        );

        portraitSection.getChildren().add(portraitContainer);
        updateCharacterPortrait();
        return portraitSection;
    }

    private VBox createCenterArea() {
        VBox centerArea = new VBox();
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setStyle(theme.getCenterAreaStyle());

        VBox titleBox = createThemedTitleBox();
        StackPane canvasContainer = createCanvasContainer();

        centerArea.getChildren().addAll(titleBox, canvasContainer);
        VBox.setVgrow(canvasContainer, Priority.ALWAYS);
        return centerArea;
    }

    private StackPane createCanvasContainer() {
        Canvas roomCanvas = renderer.getRoomCanvas();
        StackPane canvasContainer = new StackPane(roomCanvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setStyle(
                "-fx-background-color: rgba(0,0,0,0.7); " +
                        "-fx-border-color: #DAA520; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;" +
                        theme.getShadowStyle()
        );
        canvasContainer.setPadding(new Insets(10));
        return canvasContainer;
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
                        loadFont(theme.getFontPath(), responsiveDims.getTitleFontSizeBinding().getValue().intValue(), "Impact"),
                responsiveDims.getTitleFontSizeBinding()));
        gameTitle.setFill(theme.getOrangeColor());
        gameTitle.setStyle(theme.getShadowStyle());

        Text subtitle = new Text("DIVE");
        subtitle.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(), (int)(responsiveDims.getTitleFontSizeBinding().getValue().doubleValue() * 0.6), "Impact"),
                responsiveDims.getTitleFontSizeBinding()));
        subtitle.setFill(theme.getOrangeColor());
        subtitle.setStyle(theme.getShadowStyle());

        titleBox.getChildren().addAll(gameTitle, subtitle);
        return titleBox;
    }

    private VBox createRightPanel(GameUI gameUI) {
        VBox rightPanel = createThemedPanel();

        minimapCanvas = new Canvas();
        minimapCanvas.widthProperty().bind(responsiveDims.getMinimapSizeBinding());
        minimapCanvas.heightProperty().bind(responsiveDims.getMinimapSizeBinding());
        minimapRenderer.initialize(minimapCanvas);

        Button helpButton = createThemedButton("HELP");
        Button pauseButton = createThemedButton("PAUSE");

        helpButton.setOnAction(event -> controller.helpMenu(event, gameUI));
        pauseButton.setOnAction(event -> controller.pauseGame(event, gameUI));

        rightPanel.getChildren().addAll(minimapCanvas, helpButton, pauseButton);
        return rightPanel;
    }

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
        panel.setStyle(theme.getPanelStyle());
        return panel;
    }

    private Button createThemedButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.WHITE);
        button.setTextOverrun(OverrunStyle.CLIP);

        button.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(),
                                responsiveDims.getButtonFontSizeBinding().getValue().intValue(),
                                "Courier New"),
                responsiveDims.getButtonFontSizeBinding()));

        button.prefWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(0.95));
        button.prefHeightProperty().bind(responsiveDims.getButtonHeightBinding());
        button.setWrapText(true);
        button.setAlignment(Pos.CENTER);

        String baseStyle = theme.getButtonBaseStyle();
        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(theme.getButtonHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    public void createMessageArea() {
        messageContainer = createThemedMessageContainer();
        messagesArea = createMessagesArea();
        messageScrollPane = createMessageScrollPane();
        messageContainer.getChildren().add(messageScrollPane);
    }

    private VBox createThemedMessageContainer() {
        VBox container = new VBox();
        container.setStyle(theme.getMessageContainerStyle());
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
        scrollPane.setStyle(theme.getMessageScrollPaneStyle());

        scrollPane.prefHeightProperty().bind(
                Bindings.max(50.0,
                        Bindings.min(100.0,
                                responsiveDims.getScene().heightProperty().multiply(0.08)))
        );
        return scrollPane;
    }

    private void createDamageFlashOverlay(Scene scene) {
        damageFlashOverlay = new Pane();
        damageFlashOverlay.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4);");
        damageFlashOverlay.setVisible(false);
        damageFlashOverlay.setMouseTransparent(true);

        damageFlashOverlay.prefWidthProperty().bind(scene.widthProperty());
        damageFlashOverlay.prefHeightProperty().bind(scene.heightProperty());
    }

    public void setupResponsiveBindings() {
    }

    public void addGameMessage(String message) {
        if (messagesArea == null || messageScrollPane == null) return;

        Label messageLabel = createThemedMessageLabel(message);
        messagesArea.getChildren().add(messageLabel);

        if (messagesArea.getChildren().size() > 20) { // MAX_MESSAGES
            messagesArea.getChildren().remove(0);
        }
        javafx.application.Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }

    private Label createThemedMessageLabel(String message) {
        Label label = new Label("• " + message);
        label.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(), responsiveDims.getMessageFontSizeBinding().getValue().intValue(), "Monospaced"),
                responsiveDims.getMessageFontSizeBinding()));
        label.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0,
                                responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0),
                responsiveDims.getPanelPaddingBinding()));
        label.setTextFill(theme.getTanColor());
        label.setWrapText(true);
        label.setStyle(theme.getShadowStyle());
        return label;
    }

    public void updatePlayerStats() {
        Hero player = controller.getPlayer();
        if (player != null) {
            javafx.application.Platform.runLater(() -> {
                playerHealthLabel.setText("HP: " + player.getHealth() + "/" + player.getMaxHealth());
                playerManaLabel.setText("MP: " + player.getSpecialMana() + "/4");
                playerAttackLabel.setText("ATK: " + player.getType().getBaseAttack());
                playerGoldLabel.setText("Gold: " + player.getGold());
                playerPillarsLabel.setText("Pillars: " + player.getPillarsActivated() + "/4");
            });
        }
    }

    public void updateMinimap() {
        if (minimapRenderer != null) {
            minimapRenderer.updateMinimap();
        }
    }

    public void updateCharacterPortrait() {
        if (portraitRenderer != null) {
            portraitRenderer.updatePortraitCanvas();
        }
    }

    public void flashDamageEffect() {
        if (damageFlashOverlay == null) {
            System.err.println("Warning: damageFlashOverlay is null, cannot show damage flash");
            return;
        }

        damageFlashOverlay.setVisible(true);
        damageFlashOverlay.setOpacity(0.0);

        javafx.animation.Timeline flashTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.0)),
                new javafx.animation.KeyFrame(Duration.millis(50),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.8)),
                new javafx.animation.KeyFrame(Duration.millis(150),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.2)),
                new javafx.animation.KeyFrame(Duration.millis(250),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.6)),
                new javafx.animation.KeyFrame(Duration.millis(350),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.1)),
                new javafx.animation.KeyFrame(Duration.millis(450),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.4)),
                new javafx.animation.KeyFrame(Duration.millis(600),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.2)),
                new javafx.animation.KeyFrame(Duration.millis(1200),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.0))
        );

        flashTimeline.setOnFinished(event -> damageFlashOverlay.setVisible(false));
        flashTimeline.play();
    }

    public void onRoomChanged() {
        updateMinimap();
        updatePlayerStats();
        if (controller.getGameController() != null) {
            addGameMessage(controller.getGameController().getCurrentRoomDescription());
        }
    }

    private Font loadFont(String path, int size, String fallback) {
        try (InputStream fontStream = getClass().getResourceAsStream(path)) {
            if (fontStream != null) {
                Font customFont = Font.loadFont(fontStream, size);
                if (customFont != null) return customFont;
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom font " + path + ". Using fallback. Error: " + e.getMessage());
        }
        return Font.font(fallback, size);
    }

    public VBox getMessageContainer() { return messageContainer; }
    public Pane getDamageFlashOverlay() { return damageFlashOverlay; }
}