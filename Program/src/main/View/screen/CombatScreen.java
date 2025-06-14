package main.View.screen;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.View.GameUI;
import main.Controller.StateController.GameState;

import java.util.List;
import java.util.Objects;

/**
 * Combat screen handling all combat-related UI interactions and animations.
 * Provides responsive scaling and immersive visual effects for turn-based combat.
 */
public class CombatScreen extends Screen {

    // ====== RESPONSIVE CONFIGURATION ======
    private static final class ResponsiveConfig {
        static final double BASE_WIDTH = 800.0;
        static final double BASE_HEIGHT = 600.0;
        static final double MIN_SCALE = 0.6;
        static final double MAX_SCALE = 2.5;

        // Layout ratios
        static final double BATTLEFIELD_HEIGHT_RATIO = 0.58;
        static final double INTERFACE_HEIGHT_RATIO = 0.42;
        static final double MESSAGE_HEIGHT_RATIO = 0.20;

        // Sprite size ratios
        static final double HERO_SPRITE_SIZE_RATIO = 0.15;
        static final double MONSTER_SPRITE_SIZE_RATIO = 0.175;
        static final double HERO_SPRITE_MIN_SIZE = 80.0;
        static final double HERO_SPRITE_MAX_SIZE = 160.0;
        static final double MONSTER_SPRITE_MIN_SIZE = 90.0;
        static final double MONSTER_SPRITE_MAX_SIZE = 180.0;

        // UI element ratios
        static final double HEALTH_BAR_WIDTH_RATIO = 0.15;
        static final double HEALTH_BAR_HEIGHT_RATIO = 0.016;
        static final double HEALTH_BAR_MIN_WIDTH = 80.0;
        static final double HEALTH_BAR_MAX_WIDTH = 180.0;
        static final double HEALTH_BAR_MIN_HEIGHT = 8.0;
        static final double HEALTH_BAR_MAX_HEIGHT = 20.0;

        // Font size ratios
        static final double NAME_FONT_SIZE_RATIO = 0.032;
        static final double SUBTITLE_FONT_SIZE_RATIO = 0.022;
        static final double BUTTON_FONT_SIZE_RATIO = 0.028;
        static final double MESSAGE_FONT_SIZE_RATIO = 0.02;

        // Spacing and dimensions
        static final double PADDING_RATIO = 0.025;
        static final double SPACING_RATIO = 0.035;
        static final double HERO_SPACING_RATIO = 0.26;
        static final double BUTTON_WIDTH_RATIO = 0.16;
        static final double BUTTON_HEIGHT_RATIO = 0.08;
        static final double BUTTON_MIN_WIDTH = 80.0;
        static final double BUTTON_MAX_WIDTH = 140.0;
        static final double BUTTON_MIN_HEIGHT = 40.0;
        static final double BUTTON_MAX_HEIGHT = 80.0;

        // Platform dimensions
        static final double PLATFORM_RADIUS_X_RATIO = 0.11;
        static final double PLATFORM_RADIUS_Y_RATIO = 0.075;
        static final double PLATFORM_MIN_RADIUS_X = 60.0;
        static final double PLATFORM_MAX_RADIUS_X = 120.0;
        static final double PLATFORM_MIN_RADIUS_Y = 35.0;
        static final double PLATFORM_MAX_RADIUS_Y = 70.0;

        // Constants
        static final int MAX_MESSAGES = 6;
    }

    // ====== ANIMATION CONSTANTS ======
    private static final class AnimationConfig {
        static final Duration ENTRANCE_DURATION = Duration.seconds(1);
        static final Duration ATTACK_DURATION = Duration.millis(500);
        static final Duration SPECIAL_DURATION = Duration.millis(600);
        static final Duration FLASH_DURATION = Duration.millis(100);
        static final Duration TURN_DELAY = Duration.seconds(1.5);
        static final Duration END_DELAY = Duration.seconds(3);
    }

    // ====== UI COMPONENTS ======
    private VBox battlefield;
    private VBox bottomInterface;
    private ImageView heroSprite;
    private ImageView monsterSprite;
    private ProgressBar heroHealthBar;
    private ProgressBar monsterHealthBar;
    private Label heroHealthNumbers;
    private Label heroManaNumbers;
    private Label monsterHealthNumbers;
    private Label heroNameDisplay;
    private Label monsterNameDisplay;
    private VBox combatMessages;

    // ====== COMBAT STATE ======
    private Hero currentHero;
    private Monster currentMonster;
    private boolean playerTurn = true;
    private boolean combatActive = true;

    // ====== RESPONSIVE BINDINGS ======
    private Scene scene;
    private final ResponsiveBindingManager bindingManager;

    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
        this.bindingManager = new ResponsiveBindingManager();
    }

    @Override
    public void showScreen(GameUI theUI) {
        showScreen(theUI, null);
    }

    public void showScreen(GameUI theUI, List<Monster> monsters) {
        initializeCombatData();
        createScene();
        setupUI();
        finalizeScreen();
        playEntranceAnimation(heroSprite, monsterSprite, this::addWelcomeMessages);
    }

    private void initializeCombatData() {
        currentHero = getController().getPlayer();
        if (currentHero != null) {
            var currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
            var monsters = currentRoom.getMonsters();
            currentMonster = monsters.isEmpty() ? null : monsters.getFirst();
        }
        resetCombat();
    }

    private void createScene() {
        var root = new BorderPane();
        root.getStyleClass().add("combat-root");

        var stage = getStage();
        double width = stage.getScene() != null ? stage.getScene().getWidth() : ResponsiveConfig.BASE_WIDTH;
        double height = stage.getScene() != null ? stage.getScene().getHeight() : ResponsiveConfig.BASE_HEIGHT;

        scene = new Scene(root, width, height);
        bindingManager.initializeBindings(scene);
    }

    private void setupUI() {
        battlefield = createBattlefield();
        bottomInterface = createBottomInterface();

        var root = (BorderPane) scene.getRoot();
        root.setTop(battlefield);
        root.setBottom(bottomInterface);

        setupCSS(scene);
        setupResponsiveBindings();
    }

    private void finalizeScreen() {
        var stage = getStage();
        boolean wasFullScreen = stage.isFullScreen();

        stage.setScene(scene);
        stage.setTitle("Combat - " + getHeroName() + " vs " + getMonsterName());

        if (wasFullScreen) {
            stage.setFullScreen(true);
        }

        if (!stage.isShowing()) {
            stage.show();
        }
    }

    // ====== UI CREATION METHODS ======
    private VBox createBattlefield() {
        var vBox = new VBox();
        vBox.getStyleClass().add("battlefield");

        var battleArea = new HBox();
        battleArea.setAlignment(Pos.CENTER);

        bindingManager.bindBattleAreaProperties(battleArea);

        var heroSide = createHeroSide();
        var monsterSide = createMonsterSide();

        bindingManager.bindSidePositions(heroSide, monsterSide);

        battleArea.getChildren().addAll(heroSide, monsterSide);
        vBox.getChildren().add(battleArea);

        return vBox;
    }

    private VBox createHeroSide() {
        var heroSide = new VBox();
        heroSide.setAlignment(Pos.CENTER);
        heroSide.spacingProperty().bind(bindingManager.getSpacingBinding().divide(2));

        heroSprite = createHeroSprite(currentHero);
        var heroInfo = createHeroInfo();

        heroSide.getChildren().addAll(heroInfo, heroSprite);
        return heroSide;
    }

    private HBox createHeroInfo() {
        var heroInfo = new HBox();
        heroInfo.getStyleClass().add("combat-info-panel");
        heroInfo.setAlignment(Pos.CENTER_RIGHT);
        heroInfo.spacingProperty().bind(bindingManager.getSpacingBinding().divide(2));

        var nameSection = createNameSection(true);
        var healthSection = createHealthSection(true);
        var manaSection = createManaSection();

        heroInfo.getChildren().addAll(nameSection, healthSection, manaSection);
        return heroInfo;
    }

    private VBox createMonsterSide() {
        var monsterSide = new VBox();
        monsterSide.setAlignment(Pos.CENTER_RIGHT);
        monsterSide.spacingProperty().bind(bindingManager.getSpacingBinding().divide(2));

        monsterSprite = createMonsterSprite(currentMonster);
        var monsterInfo = createMonsterInfo();

        monsterSide.getChildren().addAll(monsterInfo, monsterSprite);
        return monsterSide;
    }

    private HBox createMonsterInfo() {
        var monsterInfo = new HBox();
        monsterInfo.getStyleClass().add("combat-info-panel");
        monsterInfo.setAlignment(Pos.CENTER);

        var nameSection = createNameSection(false);
        var healthSection = createHealthSection(false);

        monsterInfo.getChildren().addAll(nameSection, healthSection);
        return monsterInfo;
    }

    private VBox createNameSection(boolean isHero) {
        var nameSection = new VBox();
        nameSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        nameSection.spacingProperty().bind(bindingManager.getSpacingBinding().divide(6));

        if (isHero) {
            heroNameDisplay = createNameLabel(getHeroName());
            var classText = createSubtitleLabel(getHeroClass());
            nameSection.getChildren().addAll(heroNameDisplay, classText);
        } else {
            monsterNameDisplay = createNameLabel(getMonsterName());
            var levelText = createSubtitleLabel(getMonsterLevel());
            nameSection.getChildren().addAll(monsterNameDisplay, levelText);
        }

        return nameSection;
    }

    private Label createNameLabel(String text) {
        var label = new Label(text);
        label.getStyleClass().add("combat-name");
        label.fontProperty().bind(bindingManager.createNameFontBinding());
        return label;
    }

    private Label createSubtitleLabel(String text) {
        var label = new Label(text);
        label.getStyleClass().add("combat-subtitle");
        label.fontProperty().bind(bindingManager.createSubtitleFontBinding());
        return label;
    }

    private VBox createHealthSection(boolean isHero) {
        var healthSection = new VBox();
        healthSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        healthSection.spacingProperty().bind(bindingManager.getSpacingBinding().divide(6));

        var hpLabel = createSubtitleLabel("HP");
        var healthBar = createHealthBar();
        var healthNumbers = createSubtitleLabel("");

        if (isHero) {
            heroHealthBar = healthBar;
            heroHealthNumbers = healthNumbers;
            updateHeroHealthDisplay();
        } else {
            monsterHealthBar = healthBar;
            monsterHealthNumbers = healthNumbers;
            updateMonsterHealthDisplay();
        }

        healthSection.getChildren().addAll(hpLabel, healthBar, healthNumbers);
        return healthSection;
    }

    private VBox createManaSection() {
        var manaSection = new VBox();
        manaSection.setAlignment(Pos.CENTER_LEFT);
        manaSection.spacingProperty().bind(bindingManager.getSpacingBinding().divide(6));

        var manaLabel = createSubtitleLabel("MANA");
        heroManaNumbers = createSubtitleLabel("");
        updateHeroManaDisplay();

        manaSection.getChildren().addAll(manaLabel, heroManaNumbers);
        return manaSection;
    }

    private ProgressBar createHealthBar() {
        var healthBar = new ProgressBar();
        healthBar.getStyleClass().add("progress-bar");
        bindingManager.bindHealthBarProperties(healthBar);
        return healthBar;
    }

    private VBox createBottomInterface() {
        var bottomInterface = new VBox();
        bottomInterface.getStyleClass().add("bottom-interface");
        bindingManager.bindBottomInterfaceProperties(bottomInterface);

        combatMessages = createMessageArea();
        var messageScroll = createMessageScrollPane();
        var buttonArea = createButtonArea();

        bottomInterface.getChildren().addAll(messageScroll, buttonArea);
        return bottomInterface;
    }

    private VBox createMessageArea() {
        var messageArea = new VBox();
        messageArea.getStyleClass().add("combat-message-area");
        messageArea.setAlignment(Pos.BOTTOM_LEFT);
        messageArea.spacingProperty().bind(bindingManager.getSpacingBinding().divide(4));
        bindingManager.bindMessageAreaProperties(messageArea);
        return messageArea;
    }

    private ScrollPane createMessageScrollPane() {
        var messageScroll = new ScrollPane(combatMessages);
        messageScroll.setFitToWidth(true);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setStyle("-fx-background-color: transparent;");
        return messageScroll;
    }

    private HBox createButtonArea() {
        var buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        bindingManager.bindButtonAreaProperties(buttonBox);

        var attackBtn = createCombatButton("ATTACK", "attack-button", this::performPlayerAttack);
        var specialBtn = createCombatButton(getSpecialAttackName(), "special-button", this::performSpecialAttack);
        var itemBtn = createCombatButton("ITEM", "item-button", this::openInventory);
        var runBtn = createCombatButton("RUN", "run-button", this::attemptRun);

        buttonBox.getChildren().addAll(attackBtn, specialBtn, itemBtn, runBtn);
        return buttonBox;
    }

    private Button createCombatButton(String text, String styleClass, Runnable action) {
        var button = new Button(text);
        button.getStyleClass().addAll("combat-button", styleClass);

        bindingManager.bindButtonProperties(button);

        button.setOnAction(_ -> {
            if (playerTurn && combatActive) {
                action.run();
            }
        });

        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    // ====== COMBAT ACTIONS ======
    private void performPlayerAttack() {
        if (!canPlayerAct()) return;

        var attackedMonster = currentMonster;
        startPlayerTurn();
        updateTurnIndicator();
        addCombatMessage(currentHero.getName() + " uses Attack!");

        playAttackAnimation(heroSprite, monsterSprite, () -> {
            getController().getGameController().playerAttack();
            updateCombatDisplay();
            handlePostAttack(attackedMonster);
        });
    }

    private void performSpecialAttack() {
        if (!canPlayerAct()) return;

        if (!currentHero.canUseSpecialAttack()) {
            addCombatMessage("Cannot use special attack right now!");
            return;
        }

        var attackedMonster = currentMonster;
        startPlayerTurn();
        updateTurnIndicator();
        addCombatMessage(currentHero.getName() + " uses " +
                currentHero.getType().getSpecialAttackName() + "!");

        playSpecialAttackAnimation(heroSprite, monsterSprite, () -> {
            getController().getGameController().playerSpecialAttack();
            updateCombatDisplay();
            handlePostAttack(attackedMonster);
        });
    }

    private void openInventory() {
        addCombatMessage("Item menu - opening inventory...");
        getController().getGameController().openInventory();
    }

    private void attemptRun() {
        addCombatMessage(currentHero.getName() + " tries to run away!");
        getController().getGameController().playerRun();
    }

    private void handlePostAttack(Monster attackedMonster) {
        refreshMonsterState();

        if (currentMonster != null && currentMonster.isAlive()) {
            var delay = new Timeline(new KeyFrame(AnimationConfig.TURN_DELAY, _ -> performMonsterTurn()));
            delay.play();
        } else {
            endCombat(true, attackedMonster);
        }
    }

    private void performMonsterTurn() {
        if (!combatActive || currentMonster == null) return;

        addCombatMessage(currentMonster.getName() + " attacks!");

        playMonsterAttackAnimation(monsterSprite, heroSprite, () -> {
            getController().getGameController().monsterAttacks();
            boolean isGameOver = getController().getGameController().getStateController().isInState(GameState.GAME_OVER);
            updateCombatDisplay();

            if (isGameOver) {
                endCombat(false);
            } else if (combatActive) {
                startMonsterTurn();
                updateTurnIndicator();
            }
        });
    }

    // ====== UI UPDATES ======
    public void updateCombatDisplay() {
        updateCombatStats();
        updateHealthBars();
    }

    private void updateCombatStats() {
        if (currentHero != null) {
            heroNameDisplay.setText(currentHero.getName());
            updateHeroHealthDisplay();
            updateHeroManaDisplay();
        }

        if (currentMonster != null) {
            monsterNameDisplay.setText(currentMonster.getName());
            updateMonsterHealthDisplay();
        }
    }

    private void updateHealthBars() {
        if (currentHero != null) {
            double heroHealthRatio = currentHero.getHealthPercentage();
            animateHealthBar(heroHealthBar, heroHealthRatio);
            updateHealthBarColor(heroHealthBar, heroHealthRatio);
        }

        if (currentMonster != null) {
            double monsterHealthRatio = currentMonster.getHealthPercentage();
            animateHealthBar(monsterHealthBar, monsterHealthRatio);
            updateHealthBarColor(monsterHealthBar, monsterHealthRatio);
        }
    }

    private void animateHealthBar(ProgressBar healthBar, double targetRatio) {
        var healthAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.8), new KeyValue(healthBar.progressProperty(), targetRatio))
        );
        healthAnimation.play();
    }

    private void updateHealthBarColor(ProgressBar healthBar, double ratio) {
        healthBar.getStyleClass().removeAll("health-bar-green", "health-bar-orange", "health-bar-red");

        String colorClass = ratio > 0.6 ? "health-bar-green" :
                ratio > 0.3 ? "health-bar-orange" : "health-bar-red";
        healthBar.getStyleClass().add(colorClass);
    }

    private void updateHeroHealthDisplay() {
        if (currentHero != null && heroHealthNumbers != null) {
            heroHealthNumbers.setText(currentHero.getHealthDisplay());
            updateHealthBarColor(heroHealthBar, currentHero.getHealthPercentage());
        }
    }

    private void updateHeroManaDisplay() {
        if (currentHero != null && heroManaNumbers != null) {
            heroManaNumbers.setText(currentHero.getSpecialMana() + "/4");
        }
    }

    private void updateMonsterHealthDisplay() {
        if (currentMonster != null && monsterHealthNumbers != null) {
            monsterHealthNumbers.setText(currentMonster.getHealthDisplay());
            updateHealthBarColor(monsterHealthBar, currentMonster.getHealthPercentage());
        }
    }

    private void updateTurnIndicator() {
        if (playerTurn) {
            heroSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.CYAN));
            monsterSprite.setEffect(null);
        } else {
            monsterSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.RED));
            heroSprite.setEffect(null);
        }
    }

    // ====== COMBAT END ======
    private void endCombat(boolean victory) {
        endCombat(victory, currentMonster);
    }

    private void endCombat(boolean victory, Monster monsterForMessage) {
        endCombat();

        if (victory) {
            handleVictory(monsterForMessage);
        } else {
            handleDefeat();
        }
    }

    private void handleVictory(Monster monsterForMessage) {
        String monsterName = (monsterForMessage != null) ? monsterForMessage.getName() : "The monster";
        addCombatMessage(monsterName + " was defeated!");
        addCombatMessage(currentHero.getName() + " wins the battle!");

        playVictoryAnimation(heroSprite);

        var endDelay = new Timeline(new KeyFrame(AnimationConfig.END_DELAY, _ ->
                getController().resumeCurrentGame(getController().getGameController().getGameUI())
        ));
        endDelay.play();
    }

    private void handleDefeat() {
        addCombatMessage(currentHero.getName() + " was defeated!");
        addCombatMessage("Game Over!");
        playDefeatAnimation(heroSprite);
    }

    // ====== MESSAGE SYSTEM ======
    private void addCombatMessage(String message) {
        var messageLabel = createMessageLabel(message);
        combatMessages.getChildren().add(messageLabel);

        if (combatMessages.getChildren().size() > ResponsiveConfig.MAX_MESSAGES) {
            combatMessages.getChildren().removeFirst();
        }

        animateTyping(messageLabel, message);
    }

    private Label createMessageLabel(String message) {
        var messageLabel = new Label(message);
        messageLabel.getStyleClass().add("combat-message");
        messageLabel.setWrapText(true);
        bindingManager.bindMessageLabelProperties(messageLabel);
        return messageLabel;
    }

    private void animateTyping(Label label, String fullText) {
        var typing = new Timeline();
        label.setText("");

        for (int i = 0; i <= fullText.length(); i++) {
            final int index = i;
            typing.getKeyFrames().add(
                    new KeyFrame(Duration.millis(index * 25), _ -> label.setText(fullText.substring(0, index)))
            );
        }
        typing.play();
    }

    // ====== RESPONSIVE BINDINGS ======
    private void setupResponsiveBindings() {
        battlefield.prefHeightProperty().bind(bindingManager.getBattlefieldHeightBinding());
        bottomInterface.prefHeightProperty().bind(bindingManager.getInterfaceHeightBinding());

        heroSprite.fitWidthProperty().bind(bindingManager.getHeroSpriteBinding());
        heroSprite.fitHeightProperty().bind(bindingManager.getHeroSpriteBinding());
        monsterSprite.fitWidthProperty().bind(bindingManager.getMonsterSpriteBinding());
        monsterSprite.fitHeightProperty().bind(bindingManager.getMonsterSpriteBinding());

        // Add listeners for platform updates
        setupPlatformUpdateListeners();
        createBattlePlatforms();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
    }

    private void setupPlatformUpdateListeners() {
        heroSprite.layoutXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.layoutYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.translateXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.translateYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.layoutXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.layoutYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.translateXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.translateYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
    }

    private void updatePlatformPositions() {
        if (battlefield != null && heroSprite != null && monsterSprite != null) {
            javafx.application.Platform.runLater(this::createBattlePlatforms);
        }
    }

    private void createBattlePlatforms() {
        if (battlefield == null || heroSprite == null || monsterSprite == null) return;

        battlefield.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("platform-layer"));

        var platformLayer = new Pane();
        platformLayer.setUserData("platform-layer");
        platformLayer.setMouseTransparent(true);

        var heroBounds = battlefield.sceneToLocal(heroSprite.localToScene(heroSprite.getBoundsInLocal()));
        var monsterBounds = battlefield.sceneToLocal(monsterSprite.localToScene(monsterSprite.getBoundsInLocal()));

        double heroPlatformX = heroBounds.getCenterX();
        double heroPlatformY = heroBounds.getCenterY() + (heroBounds.getHeight() / 4);

        double monsterPlatformX = monsterBounds.getCenterX();
        double monsterPlatformY = monsterBounds.getCenterY() + (monsterBounds.getHeight() / 4);

        double radiusX = bindingManager.getPlatformRadiusXBinding().getValue().doubleValue();
        double radiusY = bindingManager.getPlatformRadiusYBinding().getValue().doubleValue();

        var heroPlatform = createPlatform(heroPlatformX, heroPlatformY, radiusX * 0.9, radiusY);
        var monsterPlatform = createPlatform(monsterPlatformX, monsterPlatformY, radiusX, radiusY * 1.1);

        platformLayer.getChildren().addAll(heroPlatform, monsterPlatform);
        platformLayer.prefHeightProperty().bind(bindingManager.getBattlefieldHeightBinding());
        battlefield.getChildren().add(0, platformLayer);
    }

    // ====== SETUP METHODS ======
    private void setupCSS(Scene scene) {
        try {
            String cssPath = "/main/View/css/combat.css";
            var cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Could not find combat.css at: " + cssPath);
            }
        } catch (Exception e) {
            System.err.println("Could not load combat.css: " + e.getMessage());
        }
    }

    private void addWelcomeMessages() {
        addCombatMessage("A wild " + getMonsterName() + " appeared!");
        addCombatMessage("What will " + getHeroName() + " do?");
    }

    // ====== UTILITY METHODS ======
    private boolean canPlayerAct() {
        return !playerTurn || !combatActive || currentMonster == null;
    }

    private void refreshMonsterState() {
        var currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
        var monsters = currentRoom.getMonsters();
        currentMonster = monsters.isEmpty() ? null : monsters.get(0);
    }

    private String getHeroName() {
        return currentHero != null ? currentHero.getName() : "Hero";
    }

    private String getHeroClass() {
        return currentHero != null ? currentHero.getType().getDisplayName() : "Unknown";
    }

    private String getMonsterName() {
        return currentMonster != null ? currentMonster.getName() : "Unknown Monster";
    }

    private String getMonsterLevel() {
        return "Lv. " + (currentMonster != null && currentMonster.isElite() ? "Elite" : "Normal");
    }

    private String getSpecialAttackName() {
        return currentHero != null ?
                currentHero.getType().getSpecialAttackName().toUpperCase() : "SPECIAL";
    }

    public boolean isCombatActive() {
        return combatActive;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    // ====== COMBAT STATE MANAGEMENT ======
    private void resetCombat() {
        playerTurn = true;
        combatActive = true;
    }

    private void startPlayerTurn() {
        playerTurn = false;
    }

    private void startMonsterTurn() {
        playerTurn = true;
    }

    private void endCombat() {
        combatActive = false;
        playerTurn = false;
    }

    // ====== ANIMATION METHODS ======
    private void playEntranceAnimation(ImageView heroSprite, ImageView monsterSprite, Runnable onComplete) {
        var heroEntrance = new TranslateTransition(AnimationConfig.ENTRANCE_DURATION, heroSprite);
        heroEntrance.setFromX(-400);
        heroEntrance.setToX(0);

        var monsterEntrance = new TranslateTransition(AnimationConfig.ENTRANCE_DURATION, monsterSprite);
        monsterEntrance.setFromX(400);
        monsterEntrance.setToX(0);

        var entrance = new ParallelTransition(heroEntrance, monsterEntrance);
        entrance.setOnFinished(e -> onComplete.run());
        entrance.play();
    }

    private void playAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        var attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(attacker.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(200), new KeyValue(attacker.translateXProperty(), 20)),
                new KeyFrame(Duration.millis(250), new KeyValue(attacker.translateXProperty(), 15)),
                new KeyFrame(Duration.millis(300), new KeyValue(attacker.translateXProperty(), 25)),
                new KeyFrame(Duration.millis(350), new KeyValue(attacker.translateXProperty(), 15)),
                new KeyFrame(AnimationConfig.ATTACK_DURATION, new KeyValue(attacker.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> playFlashEffect(target, onComplete));
        attack.play();
    }

    private void playSpecialAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        var spin = new RotateTransition(AnimationConfig.SPECIAL_DURATION, attacker);
        spin.setByAngle(360);

        var grow = new ScaleTransition(AnimationConfig.SPECIAL_DURATION, attacker);
        grow.setToX(1.3);
        grow.setToY(1.3);
        grow.setAutoReverse(true);
        grow.setCycleCount(2);

        var special = new ParallelTransition(spin, grow);
        special.setOnFinished(e -> playIntenseFlashEffect(target, onComplete));
        special.play();
    }

    private void playMonsterAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        var attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(attacker.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(attacker.translateXProperty(), -40)),
                new KeyFrame(Duration.millis(600), new KeyValue(attacker.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> playFlashEffect(target, onComplete));
        attack.play();
    }

    private void playVictoryAnimation(ImageView sprite) {
        var victory = new RotateTransition(Duration.seconds(1), sprite);
        victory.setByAngle(360);
        victory.play();
    }

    private void playDefeatAnimation(ImageView sprite) {
        var defeat = new FadeTransition(Duration.seconds(1), sprite);
        defeat.setToValue(0.3);
        defeat.play();
    }

    private void playFlashEffect(ImageView target, Runnable onComplete) {
        var flash = new FadeTransition(AnimationConfig.FLASH_DURATION, target);
        flash.setFromValue(1.0);
        flash.setToValue(0.3);
        flash.setCycleCount(4);
        flash.setAutoReverse(true);
        flash.setOnFinished(e -> onComplete.run());
        flash.play();
    }

    private void playIntenseFlashEffect(ImageView target, Runnable onComplete) {
        var flash = new FadeTransition(Duration.millis(80), target);
        flash.setFromValue(1.0);
        flash.setToValue(0.1);
        flash.setCycleCount(4);
        flash.setAutoReverse(true);
        flash.setOnFinished(e -> onComplete.run());
        flash.play();
    }

    // ====== SPRITE FACTORY METHODS ======
    private static ImageView createHeroSprite(Hero hero) {
        var sprite = new ImageView();
        sprite.getStyleClass().add("hero-sprite");

        try {
            String spritePath = getHeroSpritePath(hero);
            var heroImage = new Image(Objects.requireNonNull(CombatScreen.class.getResourceAsStream(spritePath)));
            sprite.setImage(heroImage);
        } catch (Exception e) {
            System.err.println("Could not load hero sprite, using fallback");
            sprite.setStyle("-fx-background-color: steelblue; -fx-background-radius: 10px;");
        }

        return sprite;
    }

    private static ImageView createMonsterSprite(Monster monster) {
        var sprite = new ImageView();
        sprite.getStyleClass().add("monster-sprite");

        try {
            String spritePath = getMonsterSpritePath(monster);
            var monsterImage = new Image(Objects.requireNonNull(CombatScreen.class.getResourceAsStream(spritePath)));
            sprite.setImage(monsterImage);
        } catch (Exception e) {
            System.err.println("Could not load monster sprite, using fallback");
            sprite.setStyle("-fx-background-color: darkred; -fx-background-radius: 10px;");
        }

        return sprite;
    }

    private static String getHeroSpritePath(Hero hero) {
        if (hero == null) return "/sprites/heroes/default.png";

        return switch (hero.getType()) {
            case WARRIOR -> "/sprites/heroes/warrior.png";
            case PRIESTESS -> "/sprites/heroes/priestess.png";
            case THIEF -> "/sprites/heroes/thief.png";
        };
    }

    private static String getMonsterSpritePath(Monster monster) {
        if (monster == null) return "/sprites/monsters/default.png";

        return "/sprites/monsters/" +
                monster.getType().getName().toLowerCase().replace(" ", "_") + ".png";
    }

    private static Group createPlatform(double centerX, double centerY, double radiusX, double radiusY) {
        var platformGroup = new Group();

        var baseEllipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        baseEllipse.setFill(Color.rgb(150, 150, 220, 0.3));
        baseEllipse.setStroke(Color.rgb(200, 200, 255, 0.7));
        baseEllipse.setStrokeWidth(2);

        var bloomEffect = new Bloom();
        bloomEffect.setThreshold(0.6);
        baseEllipse.setEffect(bloomEffect);

        var innerAccent = new Ellipse(centerX, centerY, radiusX * 0.7, radiusY * 0.7);
        innerAccent.setFill(Color.TRANSPARENT);
        innerAccent.setStroke(Color.rgb(200, 200, 255, 0.4));
        innerAccent.setStrokeWidth(1.5);
        innerAccent.getStrokeDashArray().addAll(5d, 5d);

        platformGroup.getChildren().addAll(baseEllipse, innerAccent);
        return platformGroup;
    }

    // ====== RESPONSIVE BINDING MANAGER ======
    private static class ResponsiveBindingManager {
        private NumberBinding scaleBinding;
        private NumberBinding paddingBinding;
        private NumberBinding spacingBinding;
        private NumberBinding heroSpriteBinding;
        private NumberBinding monsterSpriteBinding;
        private NumberBinding healthBarWidthBinding;
        private NumberBinding healthBarHeightBinding;
        private NumberBinding nameFontSizeBinding;
        private NumberBinding subtitleFontSizeBinding;
        private NumberBinding buttonFontSizeBinding;
        private NumberBinding messageFontSizeBinding;
        private NumberBinding buttonWidthBinding;
        private NumberBinding buttonHeightBinding;
        private NumberBinding platformRadiusXBinding;
        private NumberBinding platformRadiusYBinding;
        private NumberBinding battlefieldHeightBinding;
        private NumberBinding interfaceHeightBinding;
        private NumberBinding messageHeightBinding;
        private NumberBinding heroSpacingBinding;

        void initializeBindings(Scene scene) {
            scaleBinding = Bindings.createDoubleBinding(() -> {
                double widthScale = scene.getWidth() / ResponsiveConfig.BASE_WIDTH;
                double heightScale = scene.getHeight() / ResponsiveConfig.BASE_HEIGHT;
                double scale = Math.min(widthScale, heightScale);
                return Math.max(ResponsiveConfig.MIN_SCALE, Math.min(ResponsiveConfig.MAX_SCALE, scale));
            }, scene.widthProperty(), scene.heightProperty());

            paddingBinding = Bindings.createDoubleBinding(() ->
                            Math.max(10.0, Math.min(scene.getWidth(), scene.getHeight()) * ResponsiveConfig.PADDING_RATIO),
                    scene.widthProperty(), scene.heightProperty());

            spacingBinding = Bindings.createDoubleBinding(() ->
                            Math.max(15.0, Math.min(scene.getWidth(), scene.getHeight()) * ResponsiveConfig.SPACING_RATIO),
                    scene.widthProperty(), scene.heightProperty());

            heroSpacingBinding = Bindings.createDoubleBinding(() ->
                            Math.max(150.0, scene.getWidth() * ResponsiveConfig.HERO_SPACING_RATIO),
                    scene.widthProperty());

            battlefieldHeightBinding = Bindings.createDoubleBinding(() ->
                            scene.getHeight() * ResponsiveConfig.BATTLEFIELD_HEIGHT_RATIO,
                    scene.heightProperty());

            interfaceHeightBinding = Bindings.createDoubleBinding(() ->
                            scene.getHeight() * ResponsiveConfig.INTERFACE_HEIGHT_RATIO,
                    scene.heightProperty());

            messageHeightBinding = Bindings.createDoubleBinding(() ->
                            scene.getHeight() * ResponsiveConfig.MESSAGE_HEIGHT_RATIO,
                    scene.heightProperty());

            heroSpriteBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.HERO_SPRITE_SIZE_RATIO;
                return Math.max(ResponsiveConfig.HERO_SPRITE_MIN_SIZE,
                        Math.min(ResponsiveConfig.HERO_SPRITE_MAX_SIZE, calculated));
            }, scene.widthProperty());

            monsterSpriteBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.MONSTER_SPRITE_SIZE_RATIO;
                return Math.max(ResponsiveConfig.MONSTER_SPRITE_MIN_SIZE,
                        Math.min(ResponsiveConfig.MONSTER_SPRITE_MAX_SIZE, calculated));
            }, scene.widthProperty());

            healthBarWidthBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.HEALTH_BAR_WIDTH_RATIO;
                return Math.max(ResponsiveConfig.HEALTH_BAR_MIN_WIDTH,
                        Math.min(ResponsiveConfig.HEALTH_BAR_MAX_WIDTH, calculated));
            }, scene.widthProperty());

            healthBarHeightBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * ResponsiveConfig.HEALTH_BAR_HEIGHT_RATIO;
                return Math.max(ResponsiveConfig.HEALTH_BAR_MIN_HEIGHT,
                        Math.min(ResponsiveConfig.HEALTH_BAR_MAX_HEIGHT, calculated));
            }, scene.heightProperty());

            nameFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(14.0, scene.getHeight() * ResponsiveConfig.NAME_FONT_SIZE_RATIO),
                    scene.heightProperty());

            subtitleFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(10.0, scene.getHeight() * ResponsiveConfig.SUBTITLE_FONT_SIZE_RATIO),
                    scene.heightProperty());

            buttonFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(12.0, scene.getHeight() * ResponsiveConfig.BUTTON_FONT_SIZE_RATIO),
                    scene.heightProperty());

            messageFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(11.0, scene.getHeight() * ResponsiveConfig.MESSAGE_FONT_SIZE_RATIO),
                    scene.heightProperty());

            buttonWidthBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.BUTTON_WIDTH_RATIO;
                return Math.max(ResponsiveConfig.BUTTON_MIN_WIDTH,
                        Math.min(ResponsiveConfig.BUTTON_MAX_WIDTH, calculated));
            }, scene.widthProperty());

            buttonHeightBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * ResponsiveConfig.BUTTON_HEIGHT_RATIO;
                return Math.max(ResponsiveConfig.BUTTON_MIN_HEIGHT,
                        Math.min(ResponsiveConfig.BUTTON_MAX_HEIGHT, calculated));
            }, scene.heightProperty());

            platformRadiusXBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.PLATFORM_RADIUS_X_RATIO;
                return Math.max(ResponsiveConfig.PLATFORM_MIN_RADIUS_X,
                        Math.min(ResponsiveConfig.PLATFORM_MAX_RADIUS_X, calculated));
            }, scene.widthProperty());

            platformRadiusYBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * ResponsiveConfig.PLATFORM_RADIUS_Y_RATIO;
                return Math.max(ResponsiveConfig.PLATFORM_MIN_RADIUS_Y,
                        Math.min(ResponsiveConfig.PLATFORM_MAX_RADIUS_Y, calculated));
            }, scene.heightProperty());
        }

        // Font binding methods
        ObservableValue<? extends Font> createNameFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", FontWeight.BOLD, nameFontSizeBinding.getValue().doubleValue()),
                    nameFontSizeBinding);
        }

        ObservableValue<? extends Font> createSubtitleFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                    subtitleFontSizeBinding);
        }

        ObjectBinding<Font> createButtonFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", FontWeight.BOLD, buttonFontSizeBinding.getValue().doubleValue()),
                    buttonFontSizeBinding);
        }

        ObjectBinding<Font> createMessageFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", messageFontSizeBinding.getValue().doubleValue()),
                    messageFontSizeBinding);
        }

        // Binding application methods
        void bindBattleAreaProperties(HBox battleArea) {
            battleArea.paddingProperty().bind(Bindings.createObjectBinding(() ->
                            new Insets(paddingBinding.getValue().doubleValue()),
                    paddingBinding));
            battleArea.spacingProperty().bind(heroSpacingBinding);
        }

        void bindSidePositions(VBox heroSide, VBox monsterSide) {
            heroSide.translateYProperty().bind(paddingBinding.multiply(-0.5));
            heroSide.translateXProperty().bind(paddingBinding.multiply(-1.5));
            monsterSide.translateYProperty().bind(paddingBinding.multiply(-2));
            monsterSide.translateXProperty().bind(paddingBinding.multiply(-2));
        }

        void bindHealthBarProperties(ProgressBar healthBar) {
            healthBar.prefWidthProperty().bind(healthBarWidthBinding);
            healthBar.minWidthProperty().bind(healthBarWidthBinding);
            healthBar.prefHeightProperty().bind(healthBarHeightBinding);
            healthBar.minHeightProperty().bind(healthBarHeightBinding);
        }

        void bindBottomInterfaceProperties(VBox bottomInterface) {
            bottomInterface.paddingProperty().bind(Bindings.createObjectBinding(() ->
                            new Insets(paddingBinding.getValue().doubleValue()),
                    paddingBinding));
        }

        void bindMessageAreaProperties(VBox messageArea) {
            messageArea.prefHeightProperty().bind(messageHeightBinding);
        }

        void bindButtonAreaProperties(HBox buttonBox) {
            buttonBox.spacingProperty().bind(spacingBinding);
            buttonBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                            new Insets(paddingBinding.getValue().doubleValue(), 0, 0, 0),
                    paddingBinding));
        }

        void bindButtonProperties(Button button) {
            button.fontProperty().bind(createButtonFontBinding());
            button.minWidthProperty().bind(buttonWidthBinding);
            button.setMaxWidth(Double.MAX_VALUE);
            button.prefHeightProperty().bind(buttonHeightBinding);
        }

        void bindMessageLabelProperties(Label messageLabel) {
            messageLabel.fontProperty().bind(createMessageFontBinding());
            messageLabel.maxWidthProperty().bind(scaleBinding.multiply(800)); // Scale with scene
        }

        // Getter methods
        NumberBinding getSpacingBinding() { return spacingBinding; }
        NumberBinding getBattlefieldHeightBinding() { return battlefieldHeightBinding; }
        NumberBinding getInterfaceHeightBinding() { return interfaceHeightBinding; }
        NumberBinding getHeroSpriteBinding() { return heroSpriteBinding; }
        NumberBinding getMonsterSpriteBinding() { return monsterSpriteBinding; }
        NumberBinding getPlatformRadiusXBinding() { return platformRadiusXBinding; }
        NumberBinding getPlatformRadiusYBinding() { return platformRadiusYBinding; }
    }
}