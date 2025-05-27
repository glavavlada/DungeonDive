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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.dungeon.Room;
import main.View.GameUI;

import java.util.List;

public class CombatScreen extends Screen {

    // UI elements
    private VBox topBattlefield;
    private VBox bottomInterface;
    private ProgressBar heroHealthBar;
    private ProgressBar monsterHealthBar;
    private Label heroHealthNumbers;
    private Text heroNameDisplay;
    private Text monsterNameDisplay;
    private VBox combatMessages;

    // Sprite containers (could be ImageView or Rectangle)
    private ImageView heroSprite;
    private ImageView monsterSprite;

    // Combat state
    private Hero currentHero;
    private Monster currentMonster;
    private boolean playerTurn = true;
    private boolean combatActive = true;

    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(GameUI theUI) {
        showScreen(theUI, null);
    }

    public void showScreen(GameUI theUI, List<Monster> monsters) {
        testResourceLoading(); // test resources if loading properly
        initializeCombatData();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("combat-root");

        createTopBattlefield();
        createBottomInterface();

        root.setTop(topBattlefield);
        root.setBottom(bottomInterface);

        Scene combatScene = new Scene(root, 800, 600);

        getStage().setScene(combatScene);
        getStage().setTitle("Combat - " + (currentHero != null ? currentHero.getName() : "Hero") +
                " vs " + (currentMonster != null ? currentMonster.getName() : "Monster"));

        // Apply arena background with cobblestone platforms
        applyArenaBackground(root);

        // Load the CSS file
        try {
            combatScene.getStylesheets().add(
                    getClass().getResource("/main/View/css/combat.css").toExternalForm()
            );
        } catch (Exception e) {
            System.err.println("Could not load combat.css: " + e.getMessage());
        }

        getStage().setScene(combatScene);
        getStage().setTitle("Combat - " + (currentHero != null ? currentHero.getName() : "Hero") +
                " vs " + (currentMonster != null ? currentMonster.getName() : "Monster"));

        getStage().show();
        playEntranceAnimation();
    }

    private void initializeCombatData() {
        currentHero = getController().getPlayer();
        if (currentHero != null) {
            Room currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
            List<Monster> monsters = currentRoom.getMonsters();
            currentMonster = monsters.isEmpty() ? null : monsters.get(0);
        }
    }

    private void createTopBattlefield() {
        topBattlefield = new VBox();
        topBattlefield.setPrefHeight(350);
        topBattlefield.getStyleClass().add("battlefield");

        HBox battleArea = new HBox();
        battleArea.setAlignment(Pos.CENTER);
        battleArea.setPadding(new Insets(20));
        battleArea.setSpacing(120);

        VBox heroSide = createHeroSide();
        VBox monsterSide = createMonsterSide();

        battleArea.getChildren().addAll(heroSide, monsterSide);
        topBattlefield.getChildren().add(battleArea);
    }

    private VBox createHeroSide() {
        VBox heroSide = new VBox(15);
        heroSide.setAlignment(Pos.CENTER);

        // Use ImageView for actual sprites or Rectangle as placeholder
        heroSprite = createHeroSprite();

        HBox heroInfo = new HBox(10);
        heroInfo.getStyleClass().add("combat-info-panel");
        heroInfo.setAlignment(Pos.CENTER_LEFT);

        VBox nameSection = new VBox(3);
        heroNameDisplay = new Text(currentHero != null ? currentHero.getName() : "Hero");
        heroNameDisplay.getStyleClass().add("combat-name");

        Text classText = new Text(currentHero != null ? currentHero.getType().getDisplayName() : "Unknown");
        classText.getStyleClass().add("combat-subtitle");

        nameSection.getChildren().addAll(heroNameDisplay, classText);

        VBox healthSection = createHealthSection(true);

        heroInfo.getChildren().addAll(nameSection, healthSection);
        heroSide.getChildren().addAll(heroSprite, heroInfo);

        return heroSide;
    }

    private VBox createMonsterSide() {
        VBox monsterSide = new VBox(15);
        monsterSide.setAlignment(Pos.CENTER);

        // Monster info panel (top)
        HBox monsterInfo = new HBox(10);
        monsterInfo.getStyleClass().add("combat-info-panel");
        monsterInfo.setAlignment(Pos.CENTER_LEFT);

        VBox nameSection = new VBox(3);
        monsterNameDisplay = new Text(currentMonster != null ? currentMonster.getName() : "Unknown Monster");
        monsterNameDisplay.getStyleClass().add("combat-name");

        Text levelText = new Text("Lv. " + (currentMonster != null && currentMonster.isElite() ? "Elite" : "Normal"));
        levelText.getStyleClass().add("combat-subtitle");

        nameSection.getChildren().addAll(monsterNameDisplay, levelText);

        VBox healthSection = createHealthSection(false);

        monsterInfo.getChildren().addAll(nameSection, healthSection);

        // Monster sprite
        monsterSprite = createMonsterSprite();

        monsterSide.getChildren().addAll(monsterInfo, monsterSprite);
        return monsterSide;
    }

    private ImageView createHeroSprite() {
        ImageView sprite = new ImageView();
        sprite.setFitWidth(120);
        sprite.setFitHeight(120);
        sprite.getStyleClass().add("hero-sprite");

        // Try to load actual sprite image
        try {
            String spritePath = "/sprites/heroes/";
            if (currentHero != null) {
                switch (currentHero.getType()) {
                    case WARRIOR:
                        spritePath += "warrior.png";
                        break;
                    case PRIESTESS:
                        spritePath += "priestess.png";
                        break;
                    case THIEF:
                        spritePath += "thief.png";
                        break;
                    default:
                        spritePath += "default.png";
                        break;
                }
            }
            Image heroImage = new Image(getClass().getResourceAsStream(spritePath));
            sprite.setImage(heroImage);
        } catch (Exception e) {
            System.err.println("Could not load hero sprite, using fallback");
            // Create a fallback colored rectangle effect using CSS
            sprite.setStyle("-fx-background-color: steelblue; -fx-background-radius: 10px;");
        }

        return sprite;
    }

    private ImageView createMonsterSprite() {
        ImageView sprite = new ImageView();
        sprite.setFitWidth(140);
        sprite.setFitHeight(140);
        sprite.getStyleClass().add("monster-sprite");

        // Try to load actual sprite image
        try {
            String spritePath = "/sprites/monsters/";
            if (currentMonster != null) {
                spritePath += currentMonster.getType().getName().toLowerCase().replace(" ", "_") + ".png";
            } else {
                spritePath += "default.png";
            }
            Image monsterImage = new Image(getClass().getResourceAsStream(spritePath));
            sprite.setImage(monsterImage);
        } catch (Exception e) {
            System.err.println("Could not load monster sprite, using fallback");
            // Create a fallback colored rectangle effect using CSS
            sprite.setStyle("-fx-background-color: darkred; -fx-background-radius: 10px;");
        }

        return sprite;
    }

    private VBox createHealthSection(boolean isHero) {
        VBox healthSection = new VBox(3);
        healthSection.setAlignment(Pos.CENTER_RIGHT);

        Label hpLabel = new Label("HP");
        hpLabel.getStyleClass().add("combat-subtitle");

        ProgressBar healthBar = new ProgressBar();
        healthBar.setPrefWidth(120);
        healthBar.setPrefHeight(10);

        if (isHero) {
            heroHealthBar = healthBar;
            heroHealthNumbers = new Label();
            heroHealthNumbers.getStyleClass().add("combat-subtitle");

            if (currentHero != null) {
                updateHealthBar(heroHealthBar, currentHero.getHealthPercentage());
                heroHealthNumbers.setText(currentHero.getHealthDisplay());
            }

            healthSection.getChildren().addAll(hpLabel, heroHealthBar, heroHealthNumbers);
        } else {
            monsterHealthBar = healthBar;
            if (currentMonster != null) {
                updateHealthBar(monsterHealthBar, currentMonster.getHealthPercentage());
            } else {
                monsterHealthBar.setProgress(1.0);
                updateHealthBar(monsterHealthBar, 1.0);
            }
            healthSection.getChildren().addAll(hpLabel, monsterHealthBar);
        }

        return healthSection;
    }

    private void createBottomInterface() {
        bottomInterface = new VBox();
        bottomInterface.setPrefHeight(250);
        bottomInterface.getStyleClass().add("bottom-interface");
        bottomInterface.setPadding(new Insets(15));

        // Combat messages
        combatMessages = new VBox(5);
        combatMessages.setPrefHeight(120);
        combatMessages.getStyleClass().add("combat-message-area");
        combatMessages.setAlignment(Pos.BOTTOM_LEFT);

        ScrollPane messageScroll = new ScrollPane(combatMessages);
        messageScroll.setFitToWidth(true);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setStyle("-fx-background-color: transparent;");

        HBox pokemonStyleButtons = createPokemonStyleButtons();

        bottomInterface.getChildren().addAll(messageScroll, pokemonStyleButtons);
    }

    private HBox createPokemonStyleButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button attackBtn = new Button("ATTACK");
        attackBtn.getStyleClass().addAll("combat-button", "attack-button");

        Button specialBtn = new Button(
                currentHero != null ? currentHero.getType().getSpecialAttackName().toUpperCase() : "SPECIAL"
        );
        specialBtn.getStyleClass().addAll("combat-button", "special-button");

        Button itemBtn = new Button("ITEM");
        itemBtn.getStyleClass().addAll("combat-button", "item-button");

        Button runBtn = new Button("RUN");
        runBtn.getStyleClass().addAll("combat-button", "run-button");

        // Set actions
        attackBtn.setOnAction(event -> {
            if (playerTurn && combatActive) {
                performPlayerAttack();
            }
        });

        specialBtn.setOnAction(event -> {
            if (playerTurn && combatActive) {
                performSpecialAttack();
            }
        });

        itemBtn.setOnAction(event -> {
            addCombatMessage("Item menu - opening inventory...");
            getController().getGameController().openInventory();
        });

        runBtn.setOnAction(event -> {
            addCombatMessage(currentHero.getName() + " tries to run away!");
            getController().getGameController().playerRun();
        });

        buttonBox.getChildren().addAll(attackBtn, specialBtn, itemBtn, runBtn);
        return buttonBox;
    }

    // ============================================================================
    // MISSING METHODS - COMBAT LOGIC
    // ============================================================================

    private void performPlayerAttack() {
        if (!playerTurn || !combatActive || currentMonster == null) return;

        addCombatMessage(currentHero.getName() + " uses Attack!");

        playAttackAnimation(() -> {
            getController().getGameController().playerAttack();
            updateCombatDisplay();

            if (currentMonster != null && currentMonster.isAlive()) {
                playerTurn = false;
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> performMonsterTurn()));
                delay.play();
            } else {
                endCombat(true);
            }
        });
    }

    private void performSpecialAttack() {
        if (!playerTurn || !combatActive || currentMonster == null) return;

        if (currentHero.canUseSpecialAttack()) {
            addCombatMessage(currentHero.getName() + " uses " + currentHero.getType().getSpecialAttackName() + "!");

            playSpecialAttackAnimation(() -> {
                getController().getGameController().playerSpecialAttack();
                updateCombatDisplay();

                if (currentMonster != null && currentMonster.isAlive()) {
                    playerTurn = false;
                    Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> performMonsterTurn()));
                    delay.play();
                } else {
                    endCombat(true);
                }
            });
        } else {
            addCombatMessage("Cannot use special attack right now!");
        }
    }

    private void performMonsterTurn() {
        if (!combatActive || currentMonster == null) return;

        addCombatMessage(currentMonster.getName() + " attacks!");

        playMonsterAttackAnimation(() -> {
            int damage = currentMonster.attack(currentHero);
            currentHero.takeDamage(damage);
            updateCombatDisplay();

            if (!currentHero.isAlive()) {
                endCombat(false);
            } else {
                playerTurn = true;
            }
        });
    }

    // ============================================================================
    // MISSING METHODS - ANIMATIONS
    // ============================================================================

    private void playEntranceAnimation() {
        // Hero slides in from left
        TranslateTransition heroEntrance = new TranslateTransition(Duration.seconds(1), heroSprite);
        heroEntrance.setFromX(-400);
        heroEntrance.setToX(0);

        // Monster slides in from right
        TranslateTransition monsterEntrance = new TranslateTransition(Duration.seconds(1), monsterSprite);
        monsterEntrance.setFromX(400);
        monsterEntrance.setToX(0);

        ParallelTransition entrance = new ParallelTransition(heroEntrance, monsterEntrance);
        entrance.setOnFinished(e -> {
            addCombatMessage("A wild " + (currentMonster != null ? currentMonster.getName() : "Monster") + " appeared!");
            addCombatMessage("What will " + (currentHero != null ? currentHero.getName() : "Hero") + " do?");
        });
        entrance.play();
    }

    private void playAttackAnimation(Runnable onComplete) {
        // Hero moves right towards monster
        Timeline attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(heroSprite.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(200), new KeyValue(heroSprite.translateXProperty(), 20)),
                new KeyFrame(Duration.millis(250), new KeyValue(heroSprite.translateXProperty(), 15)),
                new KeyFrame(Duration.millis(300), new KeyValue(heroSprite.translateXProperty(), 25)),
                new KeyFrame(Duration.millis(350), new KeyValue(heroSprite.translateXProperty(), 15)),
                new KeyFrame(Duration.millis(500), new KeyValue(heroSprite.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> {
            // Flash monster when hit
            FadeTransition flash = new FadeTransition(Duration.millis(100), monsterSprite);
            flash.setFromValue(1.0);
            flash.setToValue(0.3);
            flash.setCycleCount(3);
            flash.setAutoReverse(true);
            flash.setOnFinished(event -> onComplete.run());
            flash.play();
        });

        attack.play();
    }

    private void playSpecialAttackAnimation(Runnable onComplete) {
        // More dramatic animation for special attacks
        RotateTransition spin = new RotateTransition(Duration.millis(600), heroSprite);
        spin.setByAngle(360);

        ScaleTransition grow = new ScaleTransition(Duration.millis(600), heroSprite);
        grow.setToX(1.3);
        grow.setToY(1.3);
        grow.setAutoReverse(true);
        grow.setCycleCount(2);

        ParallelTransition special = new ParallelTransition(spin, grow);
        special.setOnFinished(e -> {
            // Intense flash on monster
            FadeTransition flash = new FadeTransition(Duration.millis(80), monsterSprite);
            flash.setFromValue(1.0);
            flash.setToValue(0.1);
            flash.setCycleCount(5);
            flash.setAutoReverse(true);
            flash.setOnFinished(event -> onComplete.run());
            flash.play();
        });

        special.play();
    }

    private void playMonsterAttackAnimation(Runnable onComplete) {
        // Monster moves left towards hero
        Timeline attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(monsterSprite.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(monsterSprite.translateXProperty(), -40)),
                new KeyFrame(Duration.millis(600), new KeyValue(monsterSprite.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> {
            // Flash hero when hit
            FadeTransition flash = new FadeTransition(Duration.millis(150), heroSprite);
            flash.setFromValue(1.0);
            flash.setToValue(0.4);
            flash.setCycleCount(2);
            flash.setAutoReverse(true);
            flash.setOnFinished(event -> onComplete.run());
            flash.play();
        });

        attack.play();
    }

    // ============================================================================
    // MISSING METHODS - UI UPDATES
    // ============================================================================

    public void updateCombatDisplay() {
        updateCombatStats();
        updateHealthBars();
    }

    public void updateDisplay() {
        updateCombatDisplay();
    }

    public void updateCombatStats() {
        if (currentHero != null) {
            heroNameDisplay.setText(currentHero.getName());
            heroHealthNumbers.setText(currentHero.getHealthDisplay());
        }

        if (currentMonster != null) {
            monsterNameDisplay.setText(currentMonster.getName());
        }
    }

    private void updateHealthBars() {
        if (currentHero != null) {
            double heroHealthRatio = currentHero.getHealthPercentage();

            Timeline heroHealthAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.8),
                            new KeyValue(heroHealthBar.progressProperty(), heroHealthRatio))
            );
            heroHealthAnimation.play();
            updateHealthBar(heroHealthBar, heroHealthRatio);
            heroHealthNumbers.setText(currentHero.getHealthDisplay());
        }

        if (currentMonster != null) {
            double monsterHealthRatio = currentMonster.getHealthPercentage();

            Timeline monsterHealthAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.8),
                            new KeyValue(monsterHealthBar.progressProperty(), monsterHealthRatio))
            );
            monsterHealthAnimation.play();
            updateHealthBar(monsterHealthBar, monsterHealthRatio);
        }
    }

    private void updateHealthBar(ProgressBar healthBar, double ratio) {
        // Remove all health bar style classes
        healthBar.getStyleClass().removeAll("health-bar-green", "health-bar-orange", "health-bar-red");

        // Add appropriate style class based on health ratio
        if (ratio > 0.6) {
            healthBar.getStyleClass().add("health-bar-green");
        } else if (ratio > 0.3) {
            healthBar.getStyleClass().add("health-bar-orange");
        } else {
            healthBar.getStyleClass().add("health-bar-red");
        }
    }

    // ============================================================================
    // MISSING METHODS - COMBAT END
    // ============================================================================

    private void endCombat(boolean victory) {
        combatActive = false;

        if (victory) {
            addCombatMessage(currentMonster.getName() + " was defeated!");
            addCombatMessage(currentHero.getName() + " wins the battle!");

            // Victory animation
            RotateTransition victoryRotation = new RotateTransition(Duration.seconds(1), heroSprite);
            victoryRotation.setByAngle(360);
            victoryRotation.play();

        } else {
            addCombatMessage(currentHero.getName() + " was defeated!");
            addCombatMessage("Game Over!");

            // Defeat animation
            FadeTransition defeat = new FadeTransition(Duration.seconds(1), heroSprite);
            defeat.setToValue(0.3);
            defeat.play();
        }

        // Return to game after delay
        Timeline endDelay = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            if (victory) {
                getController().resumeCurrentGame(getController().getGameController().getGameUI());
            } else {
                getController().getGameController().checkPlayerStatus();
            }
        }));
        endDelay.play();
    }

    // ============================================================================
    // MESSAGE SYSTEM
    // ============================================================================

    public void addGameMessage(String message) {
        addCombatMessage(message);
    }

    private void addCombatMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("combat-message");
        messageLabel.setMaxWidth(700);

        combatMessages.getChildren().add(messageLabel);

        // Keep only last 6 messages
        if (combatMessages.getChildren().size() > 6) {
            combatMessages.getChildren().remove(0);
        }

        // Typing effect animation
        animateTyping(messageLabel, message);
    }

    private void animateTyping(Label label, String fullText) {
        Timeline typing = new Timeline();
        label.setText("");

        for (int i = 0; i <= fullText.length(); i++) {
            final int index = i;
            typing.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 25),
                            e -> label.setText(fullText.substring(0, index)))
            );
        }
        typing.play();
    }

    // ============================================================================
    // COMPATIBILITY METHODS (for GameController integration)
    // ============================================================================

    /**
     * Method expected by GameController for updating combat screen
     * @param monsters List of monsters currently in combat
     */
    public void updateCombatScreen(List<Monster> monsters) {
        // Update current monster reference if needed
        if (monsters != null && !monsters.isEmpty()) {
            currentMonster = monsters.get(0);
            updateCombatDisplay();
        }
        System.out.println("Combat screen updated with " + (monsters != null ? monsters.size() : 0) + " monsters");
    }

    /**
     * Hides the combat screen (called when combat ends)
     */
    public void hideCombatScreen() {
        System.out.println("Combat screen hidden");
        // The actual hiding is handled by screen transitions in GameController
    }

    /**
     * Shows monster attack effect (visual feedback for monster attacks)
     * @param monster The monster that attacked
     * @param damage The amount of damage dealt
     */
    public void showMonsterAttackEffect(Monster monster, int damage) {
        addCombatMessage(monster.getName() + " dealt " + damage + " damage!");

        // Add visual effect - screen shake for powerful attacks
        if (damage > 15) {
            addScreenShake();
        }

        // Show floating damage number (optional visual enhancement)
        showFloatingDamageNumber(damage, false);
    }

    /**
     * Shows player attack effect (visual feedback for player attacks)
     * @param damage The amount of damage dealt
     */
    public void showPlayerAttackEffect(int damage) {
        addCombatMessage("Dealt " + damage + " damage!");

        // Show floating damage number
        showFloatingDamageNumber(damage, true);
    }

    // ============================================================================
    // ENHANCED VISUAL EFFECTS (Optional but cool!)
    // ============================================================================

    /**
     * Adds a screen shake effect for powerful attacks
     */
    private void addScreenShake() {
        Timeline shake = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(topBattlefield.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(50), new KeyValue(topBattlefield.translateXProperty(), -5)),
                new KeyFrame(Duration.millis(100), new KeyValue(topBattlefield.translateXProperty(), 5)),
                new KeyFrame(Duration.millis(150), new KeyValue(topBattlefield.translateXProperty(), -3)),
                new KeyFrame(Duration.millis(200), new KeyValue(topBattlefield.translateXProperty(), 3)),
                new KeyFrame(Duration.millis(250), new KeyValue(topBattlefield.translateXProperty(), 0))
        );
        shake.play();
    }

    /**
     * Shows floating damage numbers above sprites
     * @param damage Amount of damage to display
     * @param isPlayerAttack True if player dealt the damage, false if monster did
     */
    private void showFloatingDamageNumber(int damage, boolean isPlayerAttack) {
        Label damageLabel = new Label("-" + damage);
        damageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-text-fill: " + (isPlayerAttack ? "red" : "blue") + ";");

        // Position above the target
        double startX = isPlayerAttack ?
                monsterSprite.getLayoutX() + monsterSprite.getBoundsInLocal().getWidth() / 2 :
                heroSprite.getLayoutX() + heroSprite.getBoundsInLocal().getWidth() / 2;
        double startY = isPlayerAttack ?
                monsterSprite.getLayoutY() :
                heroSprite.getLayoutY();

        damageLabel.setLayoutX(startX);
        damageLabel.setLayoutY(startY);

        // Add to battlefield temporarily
        topBattlefield.getChildren().add(damageLabel);

        // Animate upward and fade out
        Timeline floatAnimation = new Timeline(
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(damageLabel.layoutYProperty(), startY - 50),
                        new KeyValue(damageLabel.opacityProperty(), 0)
                )
        );

        floatAnimation.setOnFinished(e -> topBattlefield.getChildren().remove(damageLabel));
        floatAnimation.play();
    }

    /**
     * Shows special status effects (like critical hits, buffs, debuffs)
     * @param effectName Name of the effect
     * @param effectColor Color of the effect text
     * @param isForHero True if effect is on hero, false if on monster
     */
    public void showStatusEffect(String effectName, Color effectColor, boolean isForHero) {
        Label statusLabel = new Label(effectName);
        statusLabel.setTextFill(effectColor);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Position above character
        ImageView targetSprite = isForHero ? heroSprite : monsterSprite;
        statusLabel.setLayoutX(targetSprite.getLayoutX() + targetSprite.getBoundsInLocal().getWidth() / 2);
        statusLabel.setLayoutY(targetSprite.getLayoutY() - 20);

        // Add to scene temporarily
        topBattlefield.getChildren().add(statusLabel);

        // Fade out and remove
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), statusLabel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> topBattlefield.getChildren().remove(statusLabel));
        fadeOut.play();
    }

    /**
     * Enhanced critical hit animation
     */
    public void playCriticalHitAnimation() {
        addCombatMessage("Critical Hit!");

        // Golden flash effect
        FadeTransition critFlash = new FadeTransition(Duration.millis(200), monsterSprite);
        critFlash.setFromValue(1.0);
        critFlash.setToValue(0.3);
        critFlash.setCycleCount(4);
        critFlash.setAutoReverse(true);

        // Scale animation
        ScaleTransition critScale = new ScaleTransition(Duration.millis(400), heroSprite);
        critScale.setToX(1.4);
        critScale.setToY(1.4);
        critScale.setAutoReverse(true);
        critScale.setCycleCount(2);

        ParallelTransition criticalHit = new ParallelTransition(critFlash, critScale);
        criticalHit.play();

        // Show status effect
        showStatusEffect("CRITICAL!", Color.GOLD, true);

        // Screen shake
        addScreenShake();
    }

    // ============================================================================
    // UTILITY METHODS
    // ============================================================================

    /**
     * Gets the current combat state for external queries
     * @return True if combat is currently active
     */
    public boolean isCombatActive() {
        return combatActive;
    }

    /**
     * Gets the current player turn state
     * @return True if it's currently the player's turn
     */
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    /**
     * Forces the combat to end (for emergency exits or debugging)
     * @param victory Whether to treat it as a victory or defeat
     */
    public void forceCombatEnd(boolean victory) {
        combatActive = false;
        endCombat(victory);
    }

    /**
     * Updates the turn indicator (if you want to add visual turn indicators)
     */
    private void updateTurnIndicator() {
        // You can add visual indicators here to show whose turn it is
        if (playerTurn) {
            heroSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.CYAN));
            monsterSprite.setEffect(null);
        } else {
            monsterSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.RED));
            heroSprite.setEffect(null);
        }
    }

    /**
     * Test method to verify all resources are loading correctly
     */
    private void testResourceLoading() {
        System.out.println("=== TESTING COMBAT SCREEN RESOURCES ===");

        // Test CSS loading
        try {
            String cssPath = "/main/View/css/combat.css";
            var cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                System.out.println("✅ CSS found: " + cssPath);
            } else {
                System.out.println("❌ CSS NOT found: " + cssPath);
            }
        } catch (Exception e) {
            System.out.println("❌ CSS loading failed: " + e.getMessage());
        }

        // Test sprite loading
        String[] testSprites = {
                "/sprites/heroes/warrior.png",
                "/sprites/heroes/priestess.png",
                "/sprites/heroes/thief.png",
                "/sprites/monsters/goblin.png"
        };

        for (String spritePath : testSprites) {
            try {
                var spriteUrl = getClass().getResource(spritePath);
                if (spriteUrl != null) {
                    System.out.println("✅ Sprite found: " + spritePath);
                } else {
                    System.out.println("❌ Sprite NOT found: " + spritePath);
                }
            } catch (Exception e) {
                System.out.println("❌ Sprite loading failed: " + spritePath + " - " + e.getMessage());
            }
        }

        System.out.println("=== END RESOURCE TEST ===");
    }

    // ============================================================================
    // DEBUGGING METHODS (Remove these in production)
    // ============================================================================

    /**
     * Debug method to simulate combat scenarios
     * Call this temporarily to test different combat situations
     */
    public void debugCombat() {
        System.out.println("=== COMBAT DEBUG INFO ===");
        System.out.println("Combat Active: " + combatActive);
        System.out.println("Player Turn: " + playerTurn);
        System.out.println("Hero: " + (currentHero != null ? currentHero.getName() : "null"));
        System.out.println("Monster: " + (currentMonster != null ? currentMonster.getName() : "null"));
        if (currentHero != null) {
            System.out.println("Hero Health: " + currentHero.getHealth() + "/" + currentHero.getMaxHealth());
        }
        if (currentMonster != null) {
            System.out.println("Monster Health: " + currentMonster.getHealth());
        }
        System.out.println("=== END DEBUG INFO ===");
    }

    // ============================================================================
// ARENA BACKGROUND WITH COBBLESTONE PLATFORMS
// ============================================================================

    /**
     * Apply arena-style background with gray gradients and round cobblestone platforms
     */
    private void applyArenaBackground(BorderPane root) {
        // Main gray gradient background
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #D3D3D3, #A9A9A9, #696969);");

        // Battlefield area with darker gray
        topBattlefield.setStyle("-fx-background-color: linear-gradient(to bottom, #B0B0B0, #808080); " +
                "-fx-border-color: #2F2F2F; -fx-border-width: 0 0 4 0;");

        // Create the cobblestone platforms
        createCoblestonePlatforms();
    }

    /**
     * Create round cobblestone platforms for hero and monster
     */
    private void createCoblestonePlatforms() {
        // Remove any existing platform layers first
        topBattlefield.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("platform-layer"));

        // Create a pane for the platforms (behind sprites but above background)
        javafx.scene.layout.Pane platformLayer = new javafx.scene.layout.Pane();
        platformLayer.setUserData("platform-layer"); // Mark for removal if needed
        platformLayer.setMouseTransparent(true); // Don't interfere with clicks

        // Create hero platform (left side)
        javafx.scene.Group heroPlatform = createCoblestonePlatform(180, 200, 100);

        // Create monster platform (right side)
        javafx.scene.Group monsterPlatform = createCoblestonePlatform(520, 200, 110);

        platformLayer.getChildren().addAll(heroPlatform, monsterPlatform);

        // Add platform layer to battlefield (index 0 = behind other content)
        topBattlefield.getChildren().add(0, platformLayer);
    }

    /**
     * Create a single cobblestone platform
     * @param centerX X position of platform center
     * @param centerY Y position of platform center
     * @param radius Radius of the platform
     * @return Group containing the platform graphics
     */
    private javafx.scene.Group createCoblestonePlatform(double centerX, double centerY, double radius) {
        javafx.scene.Group platform = new javafx.scene.Group();

        // Base circle (darker background)
        javafx.scene.shape.Circle base = new javafx.scene.shape.Circle(centerX, centerY, radius);
        base.setFill(javafx.scene.paint.Color.web("#4A4A4A"));
        base.setStroke(javafx.scene.paint.Color.web("#2F2F2F"));
        base.setStrokeWidth(3);

        // Add the base
        platform.getChildren().add(base);

        // Create cobblestone texture with multiple small circles
        java.util.Random random = new java.util.Random();

        // Generate cobblestone pieces
        for (int i = 0; i < 50; i++) {
            // Random position within the platform circle
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * (radius - 15); // Stay within bounds

            double stoneX = centerX + Math.cos(angle) * distance;
            double stoneY = centerY + Math.sin(angle) * distance;

            // Random stone size
            double stoneRadius = 4 + random.nextDouble() * 8;

            // Create cobblestone piece
            javafx.scene.shape.Circle cobblestone = new javafx.scene.shape.Circle(stoneX, stoneY, stoneRadius);

            // Vary the gray colors for realistic cobblestone look
            javafx.scene.paint.Color[] stoneColors = {
                    javafx.scene.paint.Color.web("#8B8B8B"),
                    javafx.scene.paint.Color.web("#A9A9A9"),
                    javafx.scene.paint.Color.web("#696969"),
                    javafx.scene.paint.Color.web("#778899"),
                    javafx.scene.paint.Color.web("#708090")
            };

            cobblestone.setFill(stoneColors[random.nextInt(stoneColors.length)]);
            cobblestone.setStroke(javafx.scene.paint.Color.web("#2F2F2F"));
            cobblestone.setStrokeWidth(0.5);

            // Add 3D effect with small shadow
            javafx.scene.effect.DropShadow stoneShadow = new javafx.scene.effect.DropShadow();
            stoneShadow.setRadius(2);
            stoneShadow.setOffsetX(1);
            stoneShadow.setOffsetY(1);
            stoneShadow.setColor(javafx.scene.paint.Color.web("#2F2F2F"));
            cobblestone.setEffect(stoneShadow);

            platform.getChildren().add(cobblestone);
        }

        // Add platform shadow/depth effect
        javafx.scene.effect.DropShadow platformShadow = new javafx.scene.effect.DropShadow();
        platformShadow.setRadius(10);
        platformShadow.setOffsetX(3);
        platformShadow.setOffsetY(3);
        platformShadow.setColor(javafx.scene.paint.Color.web("#1F1F1F"));
        platform.setEffect(platformShadow);

        return platform;
    }

    /**
     * Enhanced version with animated cobblestone platforms
     */
    private void createAnimatedCoblestonePlatforms() {
        // Create platforms normally first
        createCoblestonePlatforms();

        // Add subtle pulsing animation when combat starts
        javafx.scene.layout.Pane platformLayer = (javafx.scene.layout.Pane) topBattlefield.getChildren()
                .filtered(node -> node.getUserData() != null && node.getUserData().equals("platform-layer"))
                .get(0);

        if (platformLayer != null) {
            // Subtle glow animation
            Timeline glowAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(platformLayer.opacityProperty(), 0.8)),
                    new KeyFrame(Duration.seconds(2),
                            new KeyValue(platformLayer.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(4),
                            new KeyValue(platformLayer.opacityProperty(), 0.8))
            );

            glowAnimation.setCycleCount(Timeline.INDEFINITE);
            glowAnimation.play();
        }
    }

    /**
     * Alternative: Hexagonal cobblestone platforms
     */
    private javafx.scene.Group createHexagonalPlatform(double centerX, double centerY, double radius) {
        javafx.scene.Group platform = new javafx.scene.Group();

        // Create hexagon points
        double[] hexX = new double[6];
        double[] hexY = new double[6];

        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3; // 60 degrees each
            hexX[i] = centerX + radius * Math.cos(angle);
            hexY[i] = centerY + radius * Math.sin(angle);
        }

        // Create hexagonal base
        javafx.scene.shape.Polygon hexBase = new javafx.scene.shape.Polygon();
        for (int i = 0; i < 6; i++) {
            hexBase.getPoints().addAll(hexX[i], hexY[i]);
        }

        hexBase.setFill(javafx.scene.paint.Color.web("#4A4A4A"));
        hexBase.setStroke(javafx.scene.paint.Color.web("#2F2F2F"));
        hexBase.setStrokeWidth(3);

        platform.getChildren().add(hexBase);

        // Add cobblestone texture within hexagon
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < 40; i++) {
            // Generate point within hexagon
            double stoneX, stoneY;
            do {
                stoneX = centerX + (random.nextDouble() - 0.5) * radius * 1.5;
                stoneY = centerY + (random.nextDouble() - 0.5) * radius * 1.5;
            } while (!isPointInHexagon(stoneX, stoneY, centerX, centerY, radius));

            double stoneRadius = 3 + random.nextDouble() * 6;

            javafx.scene.shape.Circle cobblestone = new javafx.scene.shape.Circle(stoneX, stoneY, stoneRadius);

            javafx.scene.paint.Color[] stoneColors = {
                    javafx.scene.paint.Color.web("#8B8B8B"),
                    javafx.scene.paint.Color.web("#A9A9A9"),
                    javafx.scene.paint.Color.web("#696969")
            };

            cobblestone.setFill(stoneColors[random.nextInt(stoneColors.length)]);
            cobblestone.setStroke(javafx.scene.paint.Color.web("#2F2F2F"));
            cobblestone.setStrokeWidth(0.5);

            platform.getChildren().add(cobblestone);
        }

        return platform;
    }

    /**
     * Helper method to check if point is within hexagon
     */
    private boolean isPointInHexagon(double px, double py, double centerX, double centerY, double radius) {
        double dx = Math.abs(px - centerX);
        double dy = Math.abs(py - centerY);

        if (dx > radius * 0.866) return false; // 0.866 = sqrt(3)/2
        if (dy > radius) return false;
        if (dx + dy * 0.577 > radius) return false; // 0.577 = 1/sqrt(3)

        return true;
    }

    /**
     * Platform that reacts to combat events
     */
    private void createReactivePlatforms() {
        createCoblestonePlatforms();

        // Store reference to platform layer for effects
        javafx.scene.layout.Pane platformLayer = (javafx.scene.layout.Pane) topBattlefield.getChildren()
                .filtered(node -> node.getUserData() != null && node.getUserData().equals("platform-layer"))
                .get(0);

        if (platformLayer != null && platformLayer.getChildren().size() >= 2) {
            javafx.scene.Group heroPlatform = (javafx.scene.Group) platformLayer.getChildren().get(0);
            javafx.scene.Group monsterPlatform = (javafx.scene.Group) platformLayer.getChildren().get(1);

            // Store references for combat effects
            heroPlatform.setUserData("hero-platform");
            monsterPlatform.setUserData("monster-platform");
        }
    }

    /**
     * Flash platform when character attacks or takes damage
     */
    public void flashPlatform(boolean isHero, javafx.scene.paint.Color flashColor) {
        javafx.scene.layout.Pane platformLayer = (javafx.scene.layout.Pane) topBattlefield.getChildren()
                .filtered(node -> node.getUserData() != null && node.getUserData().equals("platform-layer"))
                .get(0);

        if (platformLayer != null) {
            javafx.scene.Group foundPlatform = null;

            for (javafx.scene.Node node : platformLayer.getChildren()) {
                if (node instanceof javafx.scene.Group) {
                    javafx.scene.Group platform = (javafx.scene.Group) node;
                    String userData = (String) platform.getUserData();
                    if ((isHero && "hero-platform".equals(userData)) ||
                            (!isHero && "monster-platform".equals(userData))) {
                        foundPlatform = platform;
                        break;
                    }
                }
            }

            final javafx.scene.Group targetPlatformForEffect = foundPlatform;

            if (targetPlatformForEffect != null) {
                // Flash effect
                javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
                colorAdjust.setHue(flashColor.getHue());
                colorAdjust.setSaturation(0.5);
                colorAdjust.setBrightness(0.3);

                targetPlatformForEffect.setEffect(colorAdjust);

                // Remove effect after flash
                Timeline flashTimeline = new Timeline(
                        new KeyFrame(Duration.millis(200), e -> targetPlatformForEffect.setEffect(null))
                );
                flashTimeline.play();
            }
        }
    }
}