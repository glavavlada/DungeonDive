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

public class CombatScreen extends Screen {

    // UI elements
    private VBox topBattlefield;
    private VBox bottomInterface;
    private ProgressBar heroHealthBar;
    private ProgressBar monsterHealthBar;
    private Label heroHealthNumbers;
    private Text heroNameDisplay;
    private Label monsterHealthNumbers;
    private Text monsterNameDisplay;
    private VBox combatMessages;

    // Sprite containers (could be ImageView or Rectangle)
    private ImageView heroSprite;
    private ImageView monsterSprite;
    private javafx.scene.Group heroPlatform;
    private javafx.scene.Group monsterPlatform;

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
        battleArea.setSpacing(210);

        VBox heroSide = createHeroSide();
        VBox monsterSide = createMonsterSide();

        heroSide.setTranslateY(-10);
        heroSide.setTranslateX(-30);
        monsterSide.setTranslateY(-40); // <<< Nudge the hero 10 pixels UP
        monsterSide.setTranslateX(-40);

        battleArea.getChildren().addAll(heroSide, monsterSide);
        topBattlefield.getChildren().add(battleArea);
    }

    private VBox createHeroSide() {
        VBox heroSide = new VBox(10);
        heroSide.setAlignment(Pos.CENTER);

        heroSprite = createHeroSprite();

        HBox heroInfo = new HBox(10);
        heroInfo.getStyleClass().add("combat-info-panel");
        heroInfo.setAlignment(Pos.CENTER_RIGHT);

        VBox nameSection = new VBox(3);
        heroNameDisplay = new Text(currentHero != null ? currentHero.getName() : "Hero");
        heroNameDisplay.getStyleClass().add("combat-name");
        Text classText = new Text(currentHero != null ? currentHero.getType().getDisplayName() : "Unknown");
        classText.getStyleClass().add("combat-subtitle");
        nameSection.getChildren().addAll(heroNameDisplay, classText);

        VBox healthSection = createHealthSection(true);

        heroInfo.getChildren().addAll(nameSection, healthSection);

        heroSide.getChildren().addAll(heroInfo, heroSprite);

        return heroSide;
    }

    private VBox createMonsterSide() {
        VBox monsterSide = new VBox(10);
        monsterSide.setAlignment(Pos.CENTER_RIGHT);

        monsterSprite = createMonsterSprite();

        // Monster info panel (top)
        HBox monsterInfo = new HBox();
        monsterInfo.getStyleClass().add("combat-info-panel");
        monsterInfo.setAlignment(Pos.CENTER);

        // Name Section (Left side of HBox)
        VBox nameSection = new VBox(3);
        nameSection.setAlignment(Pos.CENTER_LEFT);
        monsterNameDisplay = new Text(currentMonster != null ? currentMonster.getName() : "Unknown Monster");
        monsterNameDisplay.getStyleClass().add("combat-name");
        Text levelText = new Text("Lv. " + (currentMonster != null && currentMonster.isElite() ? "Elite" : "Normal"));
        levelText.getStyleClass().add("combat-subtitle");
        nameSection.getChildren().addAll(monsterNameDisplay, levelText);

        // Health Section (Right side of HBox)
        VBox healthSection = createHealthSection(false);
        monsterInfo.getChildren().addAll(nameSection, healthSection);

        // Add HBox and Sprite to the main VBox
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
        healthSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        Label hpLabel = new Label("HP");
        hpLabel.getStyleClass().add("combat-subtitle");

        // Create the ProgressBar
        ProgressBar healthBar = new ProgressBar();
        healthBar.getStyleClass().add("progress-bar");

        // *** FORCE SIZES (for debugging) ***
        healthBar.setPrefWidth(120);
        healthBar.setMinWidth(120); // Force minimum width
        healthBar.setPrefHeight(10);
        healthBar.setMinHeight(10); // Force minimum height

        if (isHero) {
            heroHealthBar = healthBar;
            heroHealthNumbers = new Label();
            heroHealthNumbers.getStyleClass().add("combat-subtitle");

            if (currentHero != null) {
                double ratio = currentHero.getHealthPercentage();
                heroHealthNumbers.setText(currentHero.getHealthDisplay());
                updateHealthBar(heroHealthBar, ratio);
            } else {
                heroHealthNumbers.setText("0 / 0");
                heroHealthBar.setProgress(0.0); // Set to 0 if no hero
            }
            healthSection.getChildren().addAll(hpLabel, heroHealthBar, heroHealthNumbers);

        } else { // Monster
            monsterHealthBar = healthBar;
            monsterHealthNumbers = new Label();
            monsterHealthNumbers.getStyleClass().add("combat-subtitle");

            if (currentMonster != null) {
                double ratio = currentMonster.getHealthPercentage();
                monsterHealthNumbers.setText(currentMonster.getHealthDisplay());
                updateHealthBar(monsterHealthBar, ratio);
            } else {
                monsterHealthNumbers.setText("0 / 0");
                monsterHealthBar.setProgress(0.0); // Set to 0 if no monster
            }
            healthSection.getChildren().addAll(hpLabel, monsterHealthBar, monsterHealthNumbers);
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

    // COMBAT LOGIC

    private void performPlayerAttack() {
        if (!playerTurn || !combatActive || currentMonster == null) return;
        final Monster attackedMonster = currentMonster;

        playerTurn = false;
        updateTurnIndicator();
        addCombatMessage(currentHero.getName() + " uses Attack!");

        playAttackAnimation(() -> {
            getController().getGameController().playerAttack();
            updateCombatDisplay();

            // Re-fetch monster in case it died
            Room currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
            List<Monster> monsters = currentRoom.getMonsters();
            currentMonster = monsters.isEmpty() ? null : monsters.get(0);

            if (currentMonster != null && currentMonster.isAlive()) {
                playerTurn = false;
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> performMonsterTurn()));
                delay.play();
            } else {
                endCombat(true, attackedMonster);
            }
        });
    }

    private void performSpecialAttack() {
        if (!playerTurn || !combatActive || currentMonster == null) return;

        if (currentHero.canUseSpecialAttack()) {
            final Monster attackedMonster = currentMonster;
            playerTurn = false;
            updateTurnIndicator();
            addCombatMessage(currentHero.getName() + " uses " + currentHero.getType().getSpecialAttackName() + "!");

            playSpecialAttackAnimation(() -> {
                getController().getGameController().playerSpecialAttack();
                updateCombatDisplay();

                Room currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
                List<Monster> monsters = currentRoom.getMonsters();
                currentMonster = monsters.isEmpty() ? null : monsters.get(0);

                if (currentMonster != null && currentMonster.isAlive()) {
                    playerTurn = false;
                    Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> performMonsterTurn()));
                    delay.play();
                } else {
                    endCombat(true, attackedMonster);
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
            getController().getGameController().monsterAttacks();
            boolean isGameOver = getController().getGameController().getStateController().isInState(GameState.GAME_OVER);

            if (isGameOver) {
                // Controller handles showing GameOverScreen. We just show defeat animation.
                endCombat(false);
            } else if (combatActive) {
                // If combat is still active, it's our turn.
                playerTurn = true;
                updateTurnIndicator();
            }
        });
    }

    // ANIMATIONS

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
            flash.setCycleCount(4);
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
            flash.setCycleCount(4);
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

    //  UI UPDATES

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
            if (monsterHealthNumbers != null && currentMonster.getMaxHealth() > 0) {
                monsterHealthNumbers.setText(currentMonster.getHealth() + " / " + currentMonster.getMaxHealth());
            } else if (monsterHealthNumbers != null) {
                monsterHealthNumbers.setText(String.valueOf(currentMonster.getHealth()));
            }
        }
    }

    private void updateHealthBars() {
        if (currentHero != null) {
            double heroHealthRatio = currentHero.getHealthPercentage();
            heroHealthBar.setProgress(heroHealthRatio);

            Timeline heroHealthAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.8),
                            new KeyValue(heroHealthBar.progressProperty(), heroHealthRatio))
            );
            heroHealthAnimation.play();
            updateHealthBar(heroHealthBar, heroHealthRatio);
            if (heroHealthNumbers != null && currentHero.getMaxHealth() > 0) { // Check if max HP exists
                heroHealthNumbers.setText(currentHero.getHealth() + " / " + currentHero.getMaxHealth());
            } else if (heroHealthNumbers != null) {
                heroHealthNumbers.setText(String.valueOf(currentHero.getHealth()));
            }
        }

        if (currentMonster != null) {
            double monsterHealthRatio = currentMonster.getHealthPercentage();
            monsterHealthBar.setProgress(monsterHealthRatio);

            Timeline monsterHealthAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.8),
                            new KeyValue(monsterHealthBar.progressProperty(), monsterHealthRatio))
            );
            monsterHealthAnimation.play();
            updateHealthBar(monsterHealthBar, monsterHealthRatio);

            if (monsterHealthNumbers != null && currentMonster.getMaxHealth() > 0) { // Check if max HP exists
                monsterHealthNumbers.setText(currentMonster.getHealth() + " / " + currentMonster.getMaxHealth());
            } else if (monsterHealthNumbers != null) {
                monsterHealthNumbers.setText(String.valueOf(currentMonster.getHealth()));
            }
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

    // COMBAT END
    /**
     * Overloaded endCombat to handle calls that don't need a specific defeated monster.
     * Calls the main endCombat method, passing the current monster (or null if none).
     */
    private void endCombat(boolean victory) {
        // For the defeat case (victory=false), we don't need a specific monster name,
        // so passing currentMonster (which might be null or the last one) is fine.
        endCombat(victory, currentMonster);
    }

    private void endCombat(boolean victory, Monster monsterForMessage) {
        combatActive = false;
        playerTurn = false;

        if (victory) {
            String monsterName = (monsterForMessage != null) ? monsterForMessage.getName() : "The monster";
            addCombatMessage(monsterName + " was defeated!");
            addCombatMessage(currentHero.getName() + " wins the battle!");

            // Victory animation
            RotateTransition victoryRotation = new RotateTransition(Duration.seconds(1), heroSprite);
            victoryRotation.setByAngle(360);
            victoryRotation.play();

            Timeline endDelay = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                getController().resumeCurrentGame(getController().getGameController().getGameUI());
            }));
            endDelay.play();

        } else {
            addCombatMessage(currentHero.getName() + " was defeated!");
            addCombatMessage("Game Over!");

            // Defeat animation
            FadeTransition defeat = new FadeTransition(Duration.seconds(1), heroSprite);
            defeat.setToValue(0.3);
            defeat.play();
        }
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

    // ENHANCED VISUAL EFFECTS

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

    // DEBUGGING METHODS (Remove later)

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

    // ARENA BACKGROUND WITH CUSTOM PLATFORMS

    /**
     * Apply arena-style background with gray gradients and round cobblestone platforms
     */
    private void applyArenaBackground(BorderPane root) {
        // Battlefield area
        topBattlefield.setStyle("-fx-background-color: transparent;");

        // Create the cobblestone platforms
        createBattlePlatforms();
    }

    /**
     * Creates a Pokémon-style battle platform (flatter, elliptical).
     * @param centerX X position of platform center
     * @param centerY Y position of platform center
     * @param radiusX Radius of the ellipse on the X-axis
     * @param radiusY Radius of the ellipse on the Y-axis
     * @return Group containing the platform graphics
     */
    private javafx.scene.Group createPokemonStylePlatform(double centerX, double centerY, double radiusX, double radiusY) {
        javafx.scene.Group platformGroup = new javafx.scene.Group();

        javafx.scene.shape.Ellipse baseEllipse = new javafx.scene.shape.Ellipse(centerX, centerY, radiusX, radiusY);

        // A semi-transparent, slightly glowing pad
        baseEllipse.setFill(javafx.scene.paint.Color.rgb(150, 150, 220, 0.3)); // Light blue/purple, semi-transparent
        baseEllipse.setStroke(javafx.scene.paint.Color.rgb(200, 200, 255, 0.7)); // Lighter stroke
        baseEllipse.setStrokeWidth(2);

        javafx.scene.effect.Bloom bloomEffect = new javafx.scene.effect.Bloom();
        bloomEffect.setThreshold(0.6);
        baseEllipse.setEffect(bloomEffect);

        platformGroup.getChildren().add(baseEllipse);

        // subtle inner pattern or texture if desired,
        javafx.scene.shape.Ellipse innerAccent = new javafx.scene.shape.Ellipse(centerX, centerY, radiusX * 0.7, radiusY * 0.7);
        innerAccent.setFill(javafx.scene.paint.Color.TRANSPARENT);
        innerAccent.setStroke(javafx.scene.paint.Color.rgb(200, 200, 255, 0.4));
        innerAccent.setStrokeWidth(1.5);
        innerAccent.getStrokeDashArray().addAll(5d, 5d); // Dashed line
        platformGroup.getChildren().add(innerAccent);

        return platformGroup;
    }

    /**
     * Create battle platforms for hero and monster using the new style.
     */
    private void createBattlePlatforms() {
        // Remove any existing platform layers first
        topBattlefield.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("platform-layer"));

        javafx.scene.layout.Pane platformLayer = new javafx.scene.layout.Pane();
        platformLayer.setUserData("platform-layer");
        platformLayer.setMouseTransparent(true);

        double heroPlatformCenterX = 160;
        double heroPlatformCenterY = 288;
        double heroPlatformRadiusX = 90;
        double heroPlatformRadiusY = 45;

        heroPlatform = createPokemonStylePlatform(
                heroPlatformCenterX, heroPlatformCenterY, heroPlatformRadiusX, heroPlatformRadiusY);


        // Monster platform (top-rightish, or centered opposite hero)
        double monsterPlatformCenterX = 604;
        double monsterPlatformCenterY = 256;
        double monsterPlatformRadiusX = 100;
        double monsterPlatformRadiusY = 50;

        monsterPlatform = createPokemonStylePlatform(
                monsterPlatformCenterX, monsterPlatformCenterY, monsterPlatformRadiusX, monsterPlatformRadiusY);

        platformLayer.getChildren().addAll(heroPlatform, monsterPlatform);
        // Ensure platforms are drawn but don't force VBox height
        platformLayer.setPrefHeight(350); // Match battlefield height
        topBattlefield.getChildren().add(0, platformLayer);    }
}