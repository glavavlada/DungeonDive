package main.View.screen;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    // Constants
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;
    private static final int BATTLEFIELD_HEIGHT = 350;
    private static final int INTERFACE_HEIGHT = 250;
    private static final int MESSAGE_HEIGHT = 120;
    private static final int MAX_MESSAGES = 6;
    private static final int HERO_SPRITE_SIZE = 120;
    private static final int MONSTER_SPRITE_SIZE = 140;
    private static final int HEALTH_BAR_WIDTH = 120;
    private static final int HEALTH_BAR_HEIGHT = 10;

    // Animation durations
    private static final Duration ENTRANCE_DURATION = Duration.seconds(1);
    private static final Duration ATTACK_DURATION = Duration.millis(500);
    private static final Duration SPECIAL_DURATION = Duration.millis(600);
    private static final Duration FLASH_DURATION = Duration.millis(100);
    private static final Duration TURN_DELAY = Duration.seconds(1.5);
    private static final Duration END_DELAY = Duration.seconds(3);

    // UI Components
    private final CombatUIComponents myUIComponents;
    private final CombatState myCombatState;
    private final AnimationManager myAnimationManager;

    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
        this.myUIComponents = new CombatUIComponents();
        this.myCombatState = new CombatState();
        this.myAnimationManager = new AnimationManager();
    }

    @Override
    public void showScreen(GameUI theUI) {
        showScreen(theUI, null);
    }

    public void showScreen(GameUI theUI, List<Monster> monsters) {
        initializeCombatData();

        BorderPane root = createRootLayout();
        Scene combatScene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        setupCSS(combatScene);
        setupStage(combatScene);

        myAnimationManager.playEntranceAnimation(
                myUIComponents.heroSprite,
                myUIComponents.monsterSprite,
                this::addWelcomeMessages
        );
    }

    private void initializeCombatData() {
        myCombatState.currentHero = getController().getPlayer();
        if (myCombatState.currentHero != null) {
            Room currentRoom = getController().getDungeon().getRoom(myCombatState.currentHero.getPosition());
            List<Monster> monsters = currentRoom.getMonsters();
            myCombatState.currentMonster = monsters.isEmpty() ? null : monsters.getFirst();
        }
        myCombatState.resetCombat();
    }

    private BorderPane createRootLayout() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("combat-root");

        myUIComponents.battlefield = createBattlefield();
        myUIComponents.bottomInterface = createBottomInterface();

        root.setTop(myUIComponents.battlefield);
        root.setBottom(myUIComponents.bottomInterface);

        createBattlePlatforms();

        return root;
    }

    private VBox createBattlefield() {
        VBox battlefield = new VBox();
        battlefield.setPrefHeight(BATTLEFIELD_HEIGHT);
        battlefield.getStyleClass().add("battlefield");

        HBox battleArea = new HBox();
        battleArea.setAlignment(Pos.CENTER);
        battleArea.setPadding(new Insets(20));
        battleArea.setSpacing(210);

        VBox heroSide = createHeroSide();
        VBox monsterSide = createMonsterSide();

        // Position adjustments
        heroSide.setTranslateY(-10);
        heroSide.setTranslateX(-30);
        monsterSide.setTranslateY(-40);
        monsterSide.setTranslateX(-40);

        battleArea.getChildren().addAll(heroSide, monsterSide);
        battlefield.getChildren().add(battleArea);

        return battlefield;
    }

    private VBox createHeroSide() {
        VBox heroSide = new VBox(10);
        heroSide.setAlignment(Pos.CENTER);

        myUIComponents.heroSprite = SpriteFactory.createHeroSprite(myCombatState.currentHero);
        HBox heroInfo = createHeroInfo();

        heroSide.getChildren().addAll(heroInfo, myUIComponents.heroSprite);
        return heroSide;
    }

    private HBox createHeroInfo() {
        HBox heroInfo = new HBox(10);
        heroInfo.getStyleClass().add("combat-info-panel");
        heroInfo.setAlignment(Pos.CENTER_RIGHT);

        VBox nameSection = createNameSection(true);
        VBox healthSection = createHealthSection(true);

        heroInfo.getChildren().addAll(nameSection, healthSection);
        return heroInfo;
    }

    private VBox createMonsterSide() {
        VBox monsterSide = new VBox(10);
        monsterSide.setAlignment(Pos.CENTER_RIGHT);

        myUIComponents.monsterSprite = SpriteFactory.createMonsterSprite(myCombatState.currentMonster);
        HBox monsterInfo = createMonsterInfo();

        monsterSide.getChildren().addAll(monsterInfo, myUIComponents.monsterSprite);
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
        VBox nameSection = new VBox(3);
        nameSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        if (isHero) {
            myUIComponents.heroNameDisplay = new Text(getHeroName());
            myUIComponents.heroNameDisplay.getStyleClass().add("combat-name");

            Text classText = new Text(getHeroClass());
            classText.getStyleClass().add("combat-subtitle");

            nameSection.getChildren().addAll(myUIComponents.heroNameDisplay, classText);
        } else {
            myUIComponents.monsterNameDisplay = new Text(getMonsterName());
            myUIComponents.monsterNameDisplay.getStyleClass().add("combat-name");

            Text levelText = new Text(getMonsterLevel());
            levelText.getStyleClass().add("combat-subtitle");

            nameSection.getChildren().addAll(myUIComponents.monsterNameDisplay, levelText);
        }

        return nameSection;
    }

    private VBox createHealthSection(boolean isHero) {
        VBox healthSection = new VBox(3);
        healthSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        Label hpLabel = new Label("HP");
        hpLabel.getStyleClass().add("combat-subtitle");

        ProgressBar healthBar = createHealthBar();
        Label healthNumbers = new Label();
        healthNumbers.getStyleClass().add("combat-subtitle");

        if (isHero) {
            myUIComponents.heroHealthBar = healthBar;
            myUIComponents.heroHealthNumbers = healthNumbers;
            updateHeroHealthDisplay();
        } else {
            myUIComponents.monsterHealthBar = healthBar;
            myUIComponents.monsterHealthNumbers = healthNumbers;
            updateMonsterHealthDisplay();
        }

        healthSection.getChildren().addAll(hpLabel, healthBar, healthNumbers);
        return healthSection;
    }

    private ProgressBar createHealthBar() {
        ProgressBar healthBar = new ProgressBar();
        healthBar.getStyleClass().add("progress-bar");
        healthBar.setPrefWidth(HEALTH_BAR_WIDTH);
        healthBar.setMinWidth(HEALTH_BAR_WIDTH);
        healthBar.setPrefHeight(HEALTH_BAR_HEIGHT);
        healthBar.setMinHeight(HEALTH_BAR_HEIGHT);
        return healthBar;
    }

    private VBox createBottomInterface() {
        VBox bottomInterface = new VBox();
        bottomInterface.setPrefHeight(INTERFACE_HEIGHT);
        bottomInterface.getStyleClass().add("bottom-interface");
        bottomInterface.setPadding(new Insets(15));

        myUIComponents.combatMessages = createMessageArea();
        ScrollPane messageScroll = createMessageScrollPane();
        HBox buttonArea = createButtonArea();

        bottomInterface.getChildren().addAll(messageScroll, buttonArea);
        return bottomInterface;
    }

    private VBox createMessageArea() {
        VBox messageArea = new VBox(5);
        messageArea.setPrefHeight(MESSAGE_HEIGHT);
        messageArea.getStyleClass().add("combat-message-area");
        messageArea.setAlignment(Pos.BOTTOM_LEFT);
        return messageArea;
    }

    private ScrollPane createMessageScrollPane() {
        ScrollPane messageScroll = new ScrollPane(myUIComponents.combatMessages);
        messageScroll.setFitToWidth(true);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setStyle("-fx-background-color: transparent;");
        return messageScroll;
    }

    private HBox createButtonArea() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

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
        button.setOnAction(_ -> {
            if (myCombatState.playerTurn && myCombatState.combatActive) {
                action.run();
            }
        });
        return button;
    }

    // Combat Actions
    private void performPlayerAttack() {
        if (canPlayerAct()) return;

        final Monster attackedMonster = myCombatState.currentMonster;
        myCombatState.startPlayerTurn();
        updateTurnIndicator();
        addCombatMessage(myCombatState.currentHero.getName() + " uses Attack!");

        myAnimationManager.playAttackAnimation(myUIComponents.heroSprite, myUIComponents.monsterSprite, () -> {
            getController().getGameController().playerAttack();
            updateCombatDisplay();
            handlePostAttack(attackedMonster);
        });
    }

    private void performSpecialAttack() {
        if (canPlayerAct()) return;

        if (!myCombatState.currentHero.canUseSpecialAttack()) {
            addCombatMessage("Cannot use special attack right now!");
            return;
        }

        final Monster attackedMonster = myCombatState.currentMonster;
        myCombatState.startPlayerTurn();
        updateTurnIndicator();
        addCombatMessage(myCombatState.currentHero.getName() + " uses " +
                myCombatState.currentHero.getType().getSpecialAttackName() + "!");

        myAnimationManager.playSpecialAttackAnimation(myUIComponents.heroSprite, myUIComponents.monsterSprite, () -> {
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
        addCombatMessage(myCombatState.currentHero.getName() + " tries to run away!");
        getController().getGameController().playerRun();
    }

    private void handlePostAttack(Monster attackedMonster) {
        refreshMonsterState();

        if (myCombatState.currentMonster != null && myCombatState.currentMonster.isAlive()) {
            Timeline delay = new Timeline(new KeyFrame(TURN_DELAY, _ -> performMonsterTurn()));
            delay.play();
        } else {
            endCombat(true, attackedMonster);
        }
    }

    private void performMonsterTurn() {
        if (!myCombatState.combatActive || myCombatState.currentMonster == null) return;

        addCombatMessage(myCombatState.currentMonster.getName() + " attacks!");

        myAnimationManager.playMonsterAttackAnimation(myUIComponents.monsterSprite, myUIComponents.heroSprite, () -> {
            getController().getGameController().monsterAttacks();
            boolean isGameOver = getController().getGameController().getStateController().isInState(GameState.GAME_OVER);

            if (isGameOver) {
                endCombat(false);
            } else if (myCombatState.combatActive) {
                myCombatState.startMonsterTurn();
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
        if (myCombatState.currentHero != null) {
            myUIComponents.heroNameDisplay.setText(myCombatState.currentHero.getName());
            updateHeroHealthDisplay();
        }

        if (myCombatState.currentMonster != null) {
            myUIComponents.monsterNameDisplay.setText(myCombatState.currentMonster.getName());
            updateMonsterHealthDisplay();
        }
    }

    private void updateHealthBars() {
        if (myCombatState.currentHero != null) {
            double heroHealthRatio = myCombatState.currentHero.getHealthPercentage();
            animateHealthBar(myUIComponents.heroHealthBar, heroHealthRatio);
            updateHealthBarColor(myUIComponents.heroHealthBar, heroHealthRatio);
        }

        if (myCombatState.currentMonster != null) {
            double monsterHealthRatio = myCombatState.currentMonster.getHealthPercentage();
            animateHealthBar(myUIComponents.monsterHealthBar, monsterHealthRatio);
            updateHealthBarColor(myUIComponents.monsterHealthBar, monsterHealthRatio);
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
        if (myCombatState.currentHero != null && myUIComponents.heroHealthNumbers != null) {
            myUIComponents.heroHealthNumbers.setText(myCombatState.currentHero.getHealthDisplay());
            updateHealthBarColor(myUIComponents.heroHealthBar, myCombatState.currentHero.getHealthPercentage());
        }
    }

    private void updateMonsterHealthDisplay() {
        if (myCombatState.currentMonster != null && myUIComponents.monsterHealthNumbers != null) {
            myUIComponents.monsterHealthNumbers.setText(myCombatState.currentMonster.getHealthDisplay());
            updateHealthBarColor(myUIComponents.monsterHealthBar, myCombatState.currentMonster.getHealthPercentage());
        }
    }

    private void updateTurnIndicator() {
        if (myCombatState.playerTurn) {
            myUIComponents.heroSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.CYAN));
            myUIComponents.monsterSprite.setEffect(null);
        } else {
            myUIComponents.monsterSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.RED));
            myUIComponents.heroSprite.setEffect(null);
        }
    }

    // Combat End
    private void endCombat(boolean victory) {
        endCombat(victory, myCombatState.currentMonster);
    }

    private void endCombat(boolean victory, Monster monsterForMessage) {
        myCombatState.endCombat();

        if (victory) {
            handleVictory(monsterForMessage);
        } else {
            handleDefeat();
        }
    }

    private void handleVictory(Monster monsterForMessage) {
        String monsterName = (monsterForMessage != null) ? monsterForMessage.getName() : "The monster";
        addCombatMessage(monsterName + " was defeated!");
        addCombatMessage(myCombatState.currentHero.getName() + " wins the battle!");

        myAnimationManager.playVictoryAnimation(myUIComponents.heroSprite);

        Timeline endDelay = new Timeline(new KeyFrame(END_DELAY, _ ->
                getController().resumeCurrentGame(getController().getGameController().getGameUI())
        ));
        endDelay.play();
    }

    private void handleDefeat() {
        addCombatMessage(myCombatState.currentHero.getName() + " was defeated!");
        addCombatMessage("Game Over!");
        myAnimationManager.playDefeatAnimation(myUIComponents.heroSprite);
    }

    // Message System
    private void addCombatMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("combat-message");
        messageLabel.setMaxWidth(700);

        myUIComponents.combatMessages.getChildren().add(messageLabel);

        // Keep only last messages
        if (myUIComponents.combatMessages.getChildren().size() > MAX_MESSAGES) {
            myUIComponents.combatMessages.getChildren().removeFirst();
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

    // Platform Creation
    private void createBattlePlatforms() {
        myUIComponents.battlefield.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("platform-layer"));

        Pane platformLayer = new Pane();
        platformLayer.setUserData("platform-layer");
        platformLayer.setMouseTransparent(true);

        javafx.scene.Group heroPlatform = PlatformFactory.createPlatform(160, 288, 90, 45);
        javafx.scene.Group monsterPlatform = PlatformFactory.createPlatform(604, 256, 100, 50);

        platformLayer.getChildren().addAll(heroPlatform, monsterPlatform);
        platformLayer.setPrefHeight(BATTLEFIELD_HEIGHT);
        myUIComponents.battlefield.getChildren().addFirst(platformLayer);
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
        getStage().setScene(scene);
        getStage().setTitle("Combat - " + getHeroName() + " vs " + getMonsterName());
        getStage().show();
    }

    private void addWelcomeMessages() {
        addCombatMessage("A wild " + getMonsterName() + " appeared!");
        addCombatMessage("What will " + getHeroName() + " do?");
    }

    // Utility Methods
    private boolean canPlayerAct() {
        return !myCombatState.playerTurn || !myCombatState.combatActive || myCombatState.currentMonster == null;
    }

    private void refreshMonsterState() {
        Room currentRoom = getController().getDungeon().getRoom(myCombatState.currentHero.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();
        myCombatState.currentMonster = monsters.isEmpty() ? null : monsters.getFirst();
    }

    private String getHeroName() {
        return myCombatState.currentHero != null ? myCombatState.currentHero.getName() : "Hero";
    }

    private String getHeroClass() {
        return myCombatState.currentHero != null ? myCombatState.currentHero.getType().getDisplayName() : "Unknown";
    }

    private String getMonsterName() {
        return myCombatState.currentMonster != null ? myCombatState.currentMonster.getName() : "Unknown Monster";
    }

    private String getMonsterLevel() {
        return "Lv. " + (myCombatState.currentMonster != null && myCombatState.currentMonster.isElite() ? "Elite" : "Normal");
    }

    private String getSpecialAttackName() {
        return myCombatState.currentHero != null ?
                myCombatState.currentHero.getType().getSpecialAttackName().toUpperCase() : "SPECIAL";
    }

    public boolean isCombatActive() {
        return myCombatState.combatActive;
    }

    public boolean isPlayerTurn() {
        return myCombatState.playerTurn;
    }

    // Inner Classes
    private static class CombatUIComponents {
        VBox battlefield;
        VBox bottomInterface;
        ImageView heroSprite;
        ImageView monsterSprite;
        ProgressBar heroHealthBar;
        ProgressBar monsterHealthBar;
        Label heroHealthNumbers;
        Label monsterHealthNumbers;
        Text heroNameDisplay;
        Text monsterNameDisplay;
        VBox combatMessages;
    }

    private static class CombatState {
        Hero currentHero;
        Monster currentMonster;
        boolean playerTurn = true;
        boolean combatActive = true;

        void resetCombat() {
            playerTurn = true;
            combatActive = true;
        }

        void startPlayerTurn() {
            playerTurn = false;
        }

        void startMonsterTurn() {
            playerTurn = true;
        }

        void endCombat() {
            combatActive = false;
            playerTurn = false;
        }
    }

    private static class AnimationManager {
        void playEntranceAnimation(ImageView heroSprite, ImageView monsterSprite, Runnable onComplete) {
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

        void playAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
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

        void playSpecialAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
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

        void playMonsterAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
            Timeline attack = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(attacker.translateXProperty(), 0)),
                    new KeyFrame(Duration.millis(300), new KeyValue(attacker.translateXProperty(), -40)),
                    new KeyFrame(Duration.millis(600), new KeyValue(attacker.translateXProperty(), 0))
            );

            attack.setOnFinished(_ -> playFlashEffect(target, onComplete));
            attack.play();
        }

        void playVictoryAnimation(ImageView sprite) {
            RotateTransition victory = new RotateTransition(Duration.seconds(1), sprite);
            victory.setByAngle(360);
            victory.play();
        }

        void playDefeatAnimation(ImageView sprite) {
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
    }

    private static class SpriteFactory {
        static ImageView createHeroSprite(Hero hero) {
            ImageView sprite = new ImageView();
            sprite.setFitWidth(HERO_SPRITE_SIZE);
            sprite.setFitHeight(HERO_SPRITE_SIZE);
            sprite.getStyleClass().add("hero-sprite");

            try {
                String spritePath = getHeroSpritePath(hero);
                Image heroImage = new Image(Objects.requireNonNull(SpriteFactory.class.getResourceAsStream(spritePath)));
                sprite.setImage(heroImage);
            } catch (Exception e) {
                System.err.println("Could not load hero sprite, using fallback");
                sprite.setStyle("-fx-background-color: steelblue; -fx-background-radius: 10px;");
            }

            return sprite;
        }

        static ImageView createMonsterSprite(Monster monster) {
            ImageView sprite = new ImageView();
            sprite.setFitWidth(MONSTER_SPRITE_SIZE);
            sprite.setFitHeight(MONSTER_SPRITE_SIZE);
            sprite.getStyleClass().add("monster-sprite");

            try {
                String spritePath = getMonsterSpritePath(monster);
                Image monsterImage = new Image(Objects.requireNonNull(SpriteFactory.class.getResourceAsStream(spritePath)));
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
    }

    private static class PlatformFactory {
        static javafx.scene.Group createPlatform(double centerX, double centerY, double radiusX, double radiusY) {
            javafx.scene.Group platformGroup = new javafx.scene.Group();

            javafx.scene.shape.Ellipse baseEllipse = new javafx.scene.shape.Ellipse(centerX, centerY, radiusX, radiusY);
            baseEllipse.setFill(javafx.scene.paint.Color.rgb(150, 150, 220, 0.3));
            baseEllipse.setStroke(javafx.scene.paint.Color.rgb(200, 200, 255, 0.7));
            baseEllipse.setStrokeWidth(2);

            javafx.scene.effect.Bloom bloomEffect = new javafx.scene.effect.Bloom();
            bloomEffect.setThreshold(0.6);
            baseEllipse.setEffect(bloomEffect);

            javafx.scene.shape.Ellipse innerAccent = new javafx.scene.shape.Ellipse(centerX, centerY, radiusX * 0.7, radiusY * 0.7);
            innerAccent.setFill(javafx.scene.paint.Color.TRANSPARENT);
            innerAccent.setStroke(javafx.scene.paint.Color.rgb(200, 200, 255, 0.4));
            innerAccent.setStrokeWidth(1.5);
            innerAccent.getStrokeDashArray().addAll(5d, 5d);

            platformGroup.getChildren().addAll(baseEllipse, innerAccent);
            return platformGroup;
        }
    }
}