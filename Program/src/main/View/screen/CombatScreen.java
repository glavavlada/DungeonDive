package main.View.screen;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.dungeon.Room;
import main.View.GameUI;
import main.Controller.StateController.GameState;

import java.util.List;
import java.util.Objects;

public class CombatScreen extends Screen {

    // ====== RESPONSIVE CONFIGURATION CONSTANTS ======
    private static final double BASE_WIDTH = 800.0;
    private static final double BASE_HEIGHT = 600.0;
    private static final double MIN_SCALE = 0.6;
    private static final double MAX_SCALE = 2.5;
    private static final double BATTLEFIELD_HEIGHT_RATIO = 0.58;
    private static final double INTERFACE_HEIGHT_RATIO = 0.42;
    private static final double MESSAGE_HEIGHT_RATIO = 0.20;
    private static final double HERO_SPRITE_SIZE_RATIO = 0.15;
    private static final double MONSTER_SPRITE_SIZE_RATIO = 0.175;
    private static final double HERO_SPRITE_MIN_SIZE = 80.0;
    private static final double HERO_SPRITE_MAX_SIZE = 160.0;
    private static final double MONSTER_SPRITE_MIN_SIZE = 90.0;
    private static final double MONSTER_SPRITE_MAX_SIZE = 180.0;
    private static final double HEALTH_BAR_WIDTH_RATIO = 0.15;
    private static final double HEALTH_BAR_HEIGHT_RATIO = 0.016;
    private static final double HEALTH_BAR_MIN_WIDTH = 80.0;
    private static final double HEALTH_BAR_MAX_WIDTH = 180.0;
    private static final double HEALTH_BAR_MIN_HEIGHT = 8.0;
    private static final double HEALTH_BAR_MAX_HEIGHT = 20.0;
    private static final double NAME_FONT_SIZE_RATIO = 0.032;
    private static final double SUBTITLE_FONT_SIZE_RATIO = 0.022;
    private static final double BUTTON_FONT_SIZE_RATIO = 0.028;
    private static final double MESSAGE_FONT_SIZE_RATIO = 0.02;
    private static final double PADDING_RATIO = 0.025;
    private static final double SPACING_RATIO = 0.035;
    private static final double HERO_SPACING_RATIO = 0.26;
    private static final double BUTTON_WIDTH_RATIO = 0.16;
    private static final double BUTTON_HEIGHT_RATIO = 0.08;
    private static final double BUTTON_MIN_WIDTH = 80.0;
    private static final double BUTTON_MAX_WIDTH = 140.0;
    private static final double BUTTON_MIN_HEIGHT = 40.0;
    private static final double BUTTON_MAX_HEIGHT = 80.0;
    private static final double PLATFORM_RADIUS_X_RATIO = 0.11;
    private static final double PLATFORM_RADIUS_Y_RATIO = 0.075;
    private static final double PLATFORM_MIN_RADIUS_X = 60.0;
    private static final double PLATFORM_MAX_RADIUS_X = 120.0;
    private static final double PLATFORM_MIN_RADIUS_Y = 35.0;
    private static final double PLATFORM_MAX_RADIUS_Y = 70.0;
    private static final int MAX_MESSAGES = 6;
    private static final Duration ENTRANCE_DURATION = Duration.seconds(1);
    private static final Duration ATTACK_DURATION = Duration.millis(500);
    private static final Duration SPECIAL_DURATION = Duration.millis(600);
    private static final Duration FLASH_DURATION = Duration.millis(100);
    private static final Duration TURN_DELAY = Duration.seconds(1.5);
    private static final Duration END_DELAY = Duration.seconds(3);

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
    private Text heroNameDisplay;
    private Text monsterNameDisplay;
    private VBox combatMessages;

    // ====== COMBAT STATE ======
    private Hero currentHero;
    private Monster currentMonster;
    private boolean playerTurn = true;
    private boolean combatActive = true;

    // ====== RESPONSIVE BINDINGS ======
    private Scene scene;
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


    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(GameUI theUI) {
        showScreen(theUI, null);
    }

    public void showScreen(GameUI theUI, List<Monster> monsters) {
        initializeCombatData();

        // 1. Create root layout shell first.
        BorderPane root = new BorderPane();
        root.getStyleClass().add("combat-root");

        // Get the stage and determine initial dimensions from the previous scene.
        Stage currentStage = getStage();
        double width = currentStage.getScene() != null ?
                currentStage.getScene().getWidth() : BASE_WIDTH;
        double height = currentStage.getScene() != null ?
                currentStage.getScene().getHeight() : BASE_HEIGHT;

        // 2. Create the Scene with the preserved dimensions.
        scene = new Scene(root, width, height);

        // 3. NOW create bindings, since `scene` exists.
        createBindings();

        // 4. NOW create the UI content, since bindings exist.
        battlefield = createBattlefield();
        bottomInterface = createBottomInterface();

        // 5. Add the fully created content to the root.
        root.setTop(battlefield);
        root.setBottom(bottomInterface);

        // Continue with setup
        setupCSS(scene);
        setupStage(scene);
        setupResponsiveBindings();

        playEntranceAnimation(
                heroSprite,
                monsterSprite,
                this::addWelcomeMessages
        );
    }

    private void createBindings() {
        // This method is now safe to call, as `scene` is initialized.
        scaleBinding = Bindings.createDoubleBinding(() -> {
            double widthScale = scene.getWidth() / BASE_WIDTH;
            double heightScale = scene.getHeight() / BASE_HEIGHT;
            double scale = Math.min(widthScale, heightScale);
            return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
        }, scene.widthProperty(), scene.heightProperty());

        paddingBinding = Bindings.createDoubleBinding(() ->
                        Math.max(10.0, Math.min(scene.getWidth(), scene.getHeight()) * PADDING_RATIO),
                scene.widthProperty(), scene.heightProperty());

        spacingBinding = Bindings.createDoubleBinding(() ->
                        Math.max(15.0, Math.min(scene.getWidth(), scene.getHeight()) * SPACING_RATIO),
                scene.widthProperty(), scene.heightProperty());

        heroSpacingBinding = Bindings.createDoubleBinding(() ->
                        Math.max(150.0, scene.getWidth() * HERO_SPACING_RATIO),
                scene.widthProperty());

        battlefieldHeightBinding = Bindings.createDoubleBinding(() ->
                        scene.getHeight() * BATTLEFIELD_HEIGHT_RATIO,
                scene.heightProperty());

        interfaceHeightBinding = Bindings.createDoubleBinding(() ->
                        scene.getHeight() * INTERFACE_HEIGHT_RATIO,
                scene.heightProperty());

        messageHeightBinding = Bindings.createDoubleBinding(() ->
                        scene.getHeight() * MESSAGE_HEIGHT_RATIO,
                scene.heightProperty());

        heroSpriteBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getWidth() * HERO_SPRITE_SIZE_RATIO;
            return Math.max(HERO_SPRITE_MIN_SIZE, Math.min(HERO_SPRITE_MAX_SIZE, calculated));
        }, scene.widthProperty());

        monsterSpriteBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getWidth() * MONSTER_SPRITE_SIZE_RATIO;
            return Math.max(MONSTER_SPRITE_MIN_SIZE, Math.min(MONSTER_SPRITE_MAX_SIZE, calculated));
        }, scene.widthProperty());

        healthBarWidthBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getWidth() * HEALTH_BAR_WIDTH_RATIO;
            return Math.max(HEALTH_BAR_MIN_WIDTH, Math.min(HEALTH_BAR_MAX_WIDTH, calculated));
        }, scene.widthProperty());

        healthBarHeightBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * HEALTH_BAR_HEIGHT_RATIO;
            return Math.max(HEALTH_BAR_MIN_HEIGHT, Math.min(HEALTH_BAR_MAX_HEIGHT, calculated));
        }, scene.heightProperty());

        nameFontSizeBinding = Bindings.createDoubleBinding(() ->
                        Math.max(14.0, scene.getHeight() * NAME_FONT_SIZE_RATIO),
                scene.heightProperty());

        subtitleFontSizeBinding = Bindings.createDoubleBinding(() ->
                        Math.max(10.0, scene.getHeight() * SUBTITLE_FONT_SIZE_RATIO),
                scene.heightProperty());

        buttonFontSizeBinding = Bindings.createDoubleBinding(() ->
                        Math.max(12.0, scene.getHeight() * BUTTON_FONT_SIZE_RATIO),
                scene.heightProperty());

        messageFontSizeBinding = Bindings.createDoubleBinding(() ->
                        Math.max(11.0, scene.getHeight() * MESSAGE_FONT_SIZE_RATIO),
                scene.heightProperty());

        buttonWidthBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getWidth() * BUTTON_WIDTH_RATIO;
            return Math.max(BUTTON_MIN_WIDTH, Math.min(BUTTON_MAX_WIDTH, calculated));
        }, scene.widthProperty());

        buttonHeightBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * BUTTON_HEIGHT_RATIO;
            return Math.max(BUTTON_MIN_HEIGHT, Math.min(BUTTON_MAX_HEIGHT, calculated));
        }, scene.heightProperty());

        platformRadiusXBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getWidth() * PLATFORM_RADIUS_X_RATIO;
            return Math.max(PLATFORM_MIN_RADIUS_X, Math.min(PLATFORM_MAX_RADIUS_X, calculated));
        }, scene.widthProperty());

        platformRadiusYBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * PLATFORM_RADIUS_Y_RATIO;
            return Math.max(PLATFORM_MIN_RADIUS_Y, Math.min(PLATFORM_MAX_RADIUS_Y, calculated));
        }, scene.heightProperty());
    }

    private void setupResponsiveBindings() {
        // Bind layout container sizes
        battlefield.prefHeightProperty().bind(battlefieldHeightBinding);
        bottomInterface.prefHeightProperty().bind(interfaceHeightBinding);

        // Bind sprite sizes now, as they are part of the layout
        heroSprite.fitWidthProperty().bind(heroSpriteBinding);
        heroSprite.fitHeightProperty().bind(heroSpriteBinding);
        monsterSprite.fitWidthProperty().bind(monsterSpriteBinding);
        monsterSprite.fitHeightProperty().bind(monsterSpriteBinding);

        // Update platforms when sprite properties change
        heroSprite.layoutXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.layoutYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.translateXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.translateYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.layoutXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.layoutYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.translateXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.translateYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());

        // Now create platforms since responsive bindings are available
        createBattlePlatforms();

        // Update platform positions when scene size changes
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
    }

    private void updatePlatformPositions() {
        if (battlefield != null && heroSprite != null && monsterSprite != null) {
            // Delay the platform update to ensure layout has been calculated
            javafx.application.Platform.runLater(this::createBattlePlatforms);
        }
    }

    private void initializeCombatData() {
        currentHero = getController().getPlayer();
        if (currentHero != null) {
            Room currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
            List<Monster> monsters = currentRoom.getMonsters();
            currentMonster = monsters.isEmpty() ? null : monsters.getFirst();
        }
        resetCombat();
    }

    private VBox createBattlefield() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("battlefield");

        HBox battleArea = new HBox();
        battleArea.setAlignment(Pos.CENTER);

        // This is safe now because createBindings() was called before this method.
        battleArea.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(paddingBinding.getValue().doubleValue()),
                paddingBinding));
        battleArea.spacingProperty().bind(heroSpacingBinding);

        VBox heroSide = createHeroSide();
        VBox monsterSide = createMonsterSide();

        heroSide.translateYProperty().bind(paddingBinding.multiply(-0.5));
        heroSide.translateXProperty().bind(paddingBinding.multiply(-1.5));
        monsterSide.translateYProperty().bind(paddingBinding.multiply(-2));
        monsterSide.translateXProperty().bind(paddingBinding.multiply(-2));

        battleArea.getChildren().addAll(heroSide, monsterSide);
        vBox.getChildren().add(battleArea);

        return vBox;
    }

    private VBox createHeroSide() {
        VBox heroSide = new VBox();
        heroSide.setAlignment(Pos.CENTER);
        heroSide.spacingProperty().bind(spacingBinding.divide(2));

        heroSprite = createHeroSprite(currentHero);
        HBox heroInfo = createHeroInfo();

        heroSide.getChildren().addAll(heroInfo, heroSprite);
        return heroSide;
    }

    private HBox createHeroInfo() {
        HBox heroInfo = new HBox();
        heroInfo.getStyleClass().add("combat-info-panel");
        heroInfo.setAlignment(Pos.CENTER_RIGHT);
        heroInfo.spacingProperty().bind(spacingBinding.divide(2));

        VBox nameSection = createNameSection(true);
        VBox healthSection = createHealthSection(true);
        VBox manaSection = createManaSection(true);

        heroInfo.getChildren().addAll(nameSection, healthSection, manaSection);
        return heroInfo;
    }

    private VBox createMonsterSide() {
        VBox monsterSide = new VBox();
        monsterSide.setAlignment(Pos.CENTER_RIGHT);
        monsterSide.spacingProperty().bind(spacingBinding.divide(2));


        monsterSprite = createMonsterSprite(currentMonster);
        HBox monsterInfo = createMonsterInfo();

        monsterSide.getChildren().addAll(monsterInfo, monsterSprite);
        return monsterSide;
    }

    private HBox createMonsterInfo() {
        HBox monsterInfo = new HBox();
        monsterInfo.getStyleClass().add("combat-info-panel");
        monsterInfo.setAlignment(Pos.CENTER);

        VBox nameSection = createNameSection(false);
        VBox healthSection = createHealthSection(false);

        monsterInfo.getChildren().addAll(nameSection, healthSection);
        return monsterInfo;
    }

    private VBox createNameSection(boolean isHero) {
        VBox nameSection = new VBox();
        nameSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        nameSection.spacingProperty().bind(spacingBinding.divide(6));

        if (isHero) {
            heroNameDisplay = new Text(getHeroName());
            heroNameDisplay.getStyleClass().add("combat-name");
            // MODIFICATION: Use PixelFont to allow CSS to be overridden by responsive size.
            heroNameDisplay.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", FontWeight.BOLD, nameFontSizeBinding.getValue().doubleValue()),
                    nameFontSizeBinding));

            Text classText = new Text(getHeroClass());
            classText.getStyleClass().add("combat-subtitle");
            // MODIFICATION: Use PixelFont
            classText.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                    subtitleFontSizeBinding));

            nameSection.getChildren().addAll(heroNameDisplay, classText);
        } else {
            monsterNameDisplay = new Text(getMonsterName());
            monsterNameDisplay.getStyleClass().add("combat-name");
            // MODIFICATION: Use PixelFont
            monsterNameDisplay.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", FontWeight.BOLD, nameFontSizeBinding.getValue().doubleValue()),
                    nameFontSizeBinding));

            Text levelText = new Text(getMonsterLevel());
            levelText.getStyleClass().add("combat-subtitle");
            // MODIFICATION: Use PixelFont
            levelText.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                    subtitleFontSizeBinding));

            nameSection.getChildren().addAll(monsterNameDisplay, levelText);
        }

        return nameSection;
    }

    private VBox createHealthSection(boolean isHero) {
        VBox healthSection = new VBox();
        healthSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        healthSection.spacingProperty().bind(spacingBinding.divide(6));

        Label hpLabel = new Label("HP");
        hpLabel.getStyleClass().add("combat-subtitle");
        // MODIFICATION: Use PixelFont
        hpLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                subtitleFontSizeBinding));


        ProgressBar healthBar = createHealthBar();
        Label healthNumbers = new Label();
        healthNumbers.getStyleClass().add("combat-subtitle");
        // MODIFICATION: Use PixelFont
        healthNumbers.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                subtitleFontSizeBinding));

        if (isHero) {
            heroHealthBar = healthBar;
            heroHealthNumbers = healthNumbers;
            updateHeroHealthDisplay();

            // Create and store mana display
            heroManaNumbers = new Label();
            heroManaNumbers.getStyleClass().add("combat-subtitle");
            heroManaNumbers.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                    subtitleFontSizeBinding));
            updateHeroManaDisplay(heroManaNumbers);
        } else {
            monsterHealthBar = healthBar;
            monsterHealthNumbers = healthNumbers;
            updateMonsterHealthDisplay();
        }

        healthSection.getChildren().addAll(hpLabel, healthBar, healthNumbers);
        return healthSection;
    }

    private VBox createManaSection(boolean isHero) {
        if (!isHero) {
            return null; // Only heroes have mana, monsters don't
        }

        VBox manaSection = new VBox();
        manaSection.setAlignment(Pos.CENTER_LEFT);
        manaSection.spacingProperty().bind(spacingBinding.divide(6));

        Label manaLabel = new Label("MANA");
        manaLabel.getStyleClass().add("combat-subtitle");
        manaLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                subtitleFontSizeBinding));

        if (heroManaNumbers == null) {
            heroManaNumbers = new Label();
            heroManaNumbers.getStyleClass().add("combat-subtitle");
            heroManaNumbers.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                    subtitleFontSizeBinding));
            updateHeroManaDisplay(heroManaNumbers);
        }

        manaSection.getChildren().addAll(manaLabel, heroManaNumbers);
        return manaSection;
    }

    private ProgressBar createHealthBar() {
        ProgressBar healthBar = new ProgressBar();
        healthBar.getStyleClass().add("progress-bar");
        healthBar.prefWidthProperty().bind(healthBarWidthBinding);
        healthBar.minWidthProperty().bind(healthBarWidthBinding);
        healthBar.prefHeightProperty().bind(healthBarHeightBinding);
        healthBar.minHeightProperty().bind(healthBarHeightBinding);
        return healthBar;
    }

    private VBox createBottomInterface() {
        VBox bottomInterface = new VBox();
        bottomInterface.getStyleClass().add("bottom-interface");
        bottomInterface.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(paddingBinding.getValue().doubleValue()),
                paddingBinding));

        combatMessages = createMessageArea();
        ScrollPane messageScroll = createMessageScrollPane();
        HBox buttonArea = createButtonArea();

        bottomInterface.getChildren().addAll(messageScroll, buttonArea);
        return bottomInterface;
    }

    private VBox createMessageArea() {
        VBox messageArea = new VBox();
        messageArea.getStyleClass().add("combat-message-area");
        messageArea.setAlignment(Pos.BOTTOM_LEFT);
        messageArea.spacingProperty().bind(spacingBinding.divide(4));
        messageArea.prefHeightProperty().bind(messageHeightBinding);
        return messageArea;
    }

    private ScrollPane createMessageScrollPane() {
        ScrollPane messageScroll = new ScrollPane(combatMessages);
        messageScroll.setFitToWidth(true);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setStyle("-fx-background-color: transparent;");
        return messageScroll;
    }

    private HBox createButtonArea() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.spacingProperty().bind(spacingBinding);
        buttonBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(paddingBinding.getValue().doubleValue(), 0, 0, 0),
                paddingBinding));

        Button attackBtn = createCombatButton("ATTACK", "attack-button", this::performPlayerAttack);
        Button specialBtn = createCombatButton(getSpecialAttackName(), "special-button", this::performSpecialAttack);
        Button itemBtn = createCombatButton("ITEM", "item-button", this::openInventory);
        Button runBtn = createCombatButton("RUN", "run-button", this::attemptRun);

        buttonBox.getChildren().addAll(attackBtn, specialBtn, itemBtn, runBtn);
        return buttonBox;
    }

    private Button createCombatButton(String text, String styleClass, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().addAll("combat-button", styleClass);

        // MODIFICATION: Use PixelFont
        button.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("PixelFont", FontWeight.BOLD, buttonFontSizeBinding.getValue().doubleValue()),
                buttonFontSizeBinding));

        button.minWidthProperty().bind(buttonWidthBinding);
        button.setMaxWidth(Double.MAX_VALUE);

        button.prefHeightProperty().bind(buttonHeightBinding);

        button.setOnAction(_ -> {
            if (playerTurn && combatActive) {
                action.run();
            }
        });

        HBox.setHgrow(button, Priority.ALWAYS);

        return button;
    }

    // Combat Actions
    private void performPlayerAttack() {
        if (canPlayerAct()) return;

        final Monster attackedMonster = currentMonster;
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
        if (canPlayerAct()) return;

        if (!currentHero.canUseSpecialAttack()) {
            addCombatMessage("Cannot use special attack right now!");
            return;
        }

        final Monster attackedMonster = currentMonster;
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
            Timeline delay = new Timeline(new KeyFrame(TURN_DELAY, _ -> performMonsterTurn()));
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

            if (isGameOver) {
                endCombat(false);
            } else if (combatActive) {
                startMonsterTurn();
                updateTurnIndicator();
            }
        });
    }

    // UI Updates
    public void updateCombatDisplay() {
        updateCombatStats();
        updateHealthBars();
    }

    private void updateCombatStats() {
        if (currentHero != null) {
            heroNameDisplay.setText(currentHero.getName());
            updateHeroHealthDisplay();
            updateHeroManaDisplay(heroManaNumbers);
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
        Timeline healthAnimation = new Timeline(
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

    private void updateHeroManaDisplay(Label manaNumbers) {
        if (currentHero != null && manaNumbers != null) {
            manaNumbers.setText(currentHero.getSpecialMana() + "/4");
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

    // Combat End
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

        Timeline endDelay = new Timeline(new KeyFrame(END_DELAY, _ ->
                getController().resumeCurrentGame(getController().getGameController().getGameUI())
        ));
        endDelay.play();
    }

    private void handleDefeat() {
        addCombatMessage(currentHero.getName() + " was defeated!");
        addCombatMessage("Game Over!");
        playDefeatAnimation(heroSprite);
    }

    // Message System
    private void addCombatMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("combat-message");
        messageLabel.setWrapText(true);
        // MODIFICATION: Use PixelFont
        messageLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("PixelFont", messageFontSizeBinding.getValue().doubleValue()),
                messageFontSizeBinding));
        messageLabel.maxWidthProperty().bind(scene.widthProperty().multiply(0.85));

        combatMessages.getChildren().add(messageLabel);

        if (combatMessages.getChildren().size() > MAX_MESSAGES) {
            combatMessages.getChildren().removeFirst();
        }

        animateTyping(messageLabel, message);
    }

    private void animateTyping(Label label, String fullText) {
        Timeline typing = new Timeline();
        label.setText("");

        for (int i = 0; i <= fullText.length(); i++) {
            final int index = i;
            typing.getKeyFrames().add(
                    new KeyFrame(Duration.millis(index * 25), _ -> label.setText(fullText.substring(0, index)))
            );
        }
        typing.play();
    }

    private void createBattlePlatforms() {
        if (battlefield == null || heroSprite == null || monsterSprite == null) return;

        battlefield.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("platform-layer"));

        Pane platformLayer = new Pane();
        platformLayer.setUserData("platform-layer");
        platformLayer.setMouseTransparent(true);

        // Use bounds to get actual positions in the battlefield coordinate space
        var heroBounds = battlefield.sceneToLocal(heroSprite.localToScene(heroSprite.getBoundsInLocal()));
        var monsterBounds = battlefield.sceneToLocal(monsterSprite.localToScene(monsterSprite.getBoundsInLocal()));

        // Calculate platform positions - use the exact center of the sprites
        double heroPlatformX = heroBounds.getCenterX();
        double heroPlatformY = heroBounds.getCenterY() + (heroBounds.getHeight() / 4); // Center + half height + offset

        double monsterPlatformX = monsterBounds.getCenterX();
        double monsterPlatformY = monsterBounds.getCenterY() + (monsterBounds.getHeight() / 4); // Center + half height + offset
        double radiusX = platformRadiusXBinding.getValue().doubleValue();
        double radiusY = platformRadiusYBinding.getValue().doubleValue();

        Group heroPlatform = createPlatform(
                heroPlatformX, heroPlatformY, radiusX * 0.9, radiusY);
        Group monsterPlatform = createPlatform(
                monsterPlatformX, monsterPlatformY, radiusX, radiusY * 1.1);

        platformLayer.getChildren().addAll(heroPlatform, monsterPlatform);
        platformLayer.prefHeightProperty().bind(battlefieldHeightBinding);
        battlefield.getChildren().addFirst(platformLayer);
    }

    // Setup Methods
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

    private void setupStage(Scene scene) {
        // Preserve the full-screen state from the previous screen.
        Stage currentStage = getStage();
        boolean wasFullScreen = currentStage.isFullScreen();

        currentStage.setScene(scene);
        currentStage.setTitle("Combat - " + getHeroName() + " vs " + getMonsterName());

        // Re-apply full-screen if it was active, as setScene() can exit it.
        if (wasFullScreen) {
            currentStage.setFullScreen(true);
        }

        // Show the stage if it isn't already visible.
        if (!currentStage.isShowing()) {
            currentStage.show();
        }
    }

    private void addWelcomeMessages() {
        addCombatMessage("A wild " + getMonsterName() + " appeared!");
        addCombatMessage("What will " + getHeroName() + " do?");
    }

    // Utility Methods
    private boolean canPlayerAct() {
        return !playerTurn || !combatActive || currentMonster == null;
    }

    private void refreshMonsterState() {
        Room currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();
        currentMonster = monsters.isEmpty() ? null : monsters.getFirst();
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

    // Combat State Management
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


    // Animation Methods
    private void playEntranceAnimation(ImageView heroSprite, ImageView monsterSprite, Runnable onComplete) {
        TranslateTransition heroEntrance = new TranslateTransition(ENTRANCE_DURATION, heroSprite);
        heroEntrance.setFromX(-400);
        heroEntrance.setToX(0);

        TranslateTransition monsterEntrance = new TranslateTransition(ENTRANCE_DURATION, monsterSprite);
        monsterEntrance.setFromX(400);
        monsterEntrance.setToX(0);

        ParallelTransition entrance = new ParallelTransition(heroEntrance, monsterEntrance);
        entrance.setOnFinished(_ -> onComplete.run());
        entrance.play();
    }

    private void playAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        Timeline attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(attacker.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(200), new KeyValue(attacker.translateXProperty(), 20)),
                new KeyFrame(Duration.millis(250), new KeyValue(attacker.translateXProperty(), 15)),
                new KeyFrame(Duration.millis(300), new KeyValue(attacker.translateXProperty(), 25)),
                new KeyFrame(Duration.millis(350), new KeyValue(attacker.translateXProperty(), 15)),
                new KeyFrame(ATTACK_DURATION, new KeyValue(attacker.translateXProperty(), 0))
        );

        attack.setOnFinished(_ -> playFlashEffect(target, onComplete));
        attack.play();
    }

    private void playSpecialAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        RotateTransition spin = new RotateTransition(SPECIAL_DURATION, attacker);
        spin.setByAngle(360);

        ScaleTransition grow = new ScaleTransition(SPECIAL_DURATION, attacker);
        grow.setToX(1.3);
        grow.setToY(1.3);
        grow.setAutoReverse(true);
        grow.setCycleCount(2);

        ParallelTransition special = new ParallelTransition(spin, grow);
        special.setOnFinished(_ -> playIntenseFlashEffect(target, onComplete));
        special.play();
    }

    private void playMonsterAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        Timeline attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(attacker.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(attacker.translateXProperty(), -40)),
                new KeyFrame(Duration.millis(600), new KeyValue(attacker.translateXProperty(), 0))
        );

        attack.setOnFinished(_ -> playFlashEffect(target, onComplete));
        attack.play();
    }

    private void playVictoryAnimation(ImageView sprite) {
        RotateTransition victory = new RotateTransition(Duration.seconds(1), sprite);
        victory.setByAngle(360);
        victory.play();
    }

    private void playDefeatAnimation(ImageView sprite) {
        FadeTransition defeat = new FadeTransition(Duration.seconds(1), sprite);
        defeat.setToValue(0.3);
        defeat.play();
    }

    private void playFlashEffect(ImageView target, Runnable onComplete) {
        FadeTransition flash = new FadeTransition(FLASH_DURATION, target);
        flash.setFromValue(1.0);
        flash.setToValue(0.3);
        flash.setCycleCount(4);
        flash.setAutoReverse(true);
        flash.setOnFinished(_ -> onComplete.run());
        flash.play();
    }

    private void playIntenseFlashEffect(ImageView target, Runnable onComplete) {
        FadeTransition flash = new FadeTransition(Duration.millis(80), target);
        flash.setFromValue(1.0);
        flash.setToValue(0.1);
        flash.setCycleCount(4);
        flash.setAutoReverse(true);
        flash.setOnFinished(_ -> onComplete.run());
        flash.play();
    }


    // Sprite and Platform Factory Methods
    private static ImageView createHeroSprite(Hero hero) {
        ImageView sprite = new ImageView();
        sprite.getStyleClass().add("hero-sprite");
        try {
            String spritePath = getHeroSpritePath(hero);
            Image heroImage = new Image(Objects.requireNonNull(CombatScreen.class.getResourceAsStream(spritePath)));
            sprite.setImage(heroImage);
        } catch (Exception e) {
            System.err.println("Could not load hero sprite, using fallback");
            sprite.setStyle("-fx-background-color: steelblue; -fx-background-radius: 10px;");
        }

        return sprite;
    }

    private static ImageView createMonsterSprite(Monster monster) {
        ImageView sprite = new ImageView();
        sprite.getStyleClass().add("monster-sprite");
        try {
            String spritePath = getMonsterSpritePath(monster);
            Image monsterImage = new Image(Objects.requireNonNull(CombatScreen.class.getResourceAsStream(spritePath)));
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
        Group platformGroup = new Group();

        Ellipse baseEllipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        baseEllipse.setFill(Color.rgb(150, 150, 220, 0.3));
        baseEllipse.setStroke(Color.rgb(200, 200, 255, 0.7));
        baseEllipse.setStrokeWidth(2);

        Bloom bloomEffect = new Bloom();
        bloomEffect.setThreshold(0.6);
        baseEllipse.setEffect(bloomEffect);

        Ellipse innerAccent = new Ellipse(centerX, centerY, radiusX * 0.7, radiusY * 0.7);
        innerAccent.setFill(Color.TRANSPARENT);
        innerAccent.setStroke(Color.rgb(200, 200, 255, 0.4));
        innerAccent.setStrokeWidth(1.5);
        innerAccent.getStrokeDashArray().addAll(5d, 5d);

        platformGroup.getChildren().addAll(baseEllipse, innerAccent);
        return platformGroup;
    }
}