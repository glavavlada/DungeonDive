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
    private VBox myTopBattlefield;
    private VBox myBottomInterface;
    private ProgressBar myHeroHealthBar;
    private ProgressBar myMonsterHealthBar;
    private Label myHeroHealthNumbers;
    private Text myHeroNameDisplay;
    private Label myMonsterHealthNumbers;
    private Text myMonsterNameDisplay;
    private VBox myCombatMessages;

    // Sprite containers (could be ImageView or Rectangle)
    private ImageView myHeroSprite;
    private ImageView myMonsterSprite;
    private VBox myHeroSide;
    private VBox myMonsterSide;
    private javafx.scene.Group myHeroPlatform;
    private javafx.scene.Group myMonsterPlatform;
    private javafx.scene.Node mySelectedNode = null; // To store the currently selected node
    private double myNudgeAmount = 1.0; // How many pixels to move per key press

    // Combat state
    private Hero myCurrentHero;
    private Monster myCurrentMonster;
    private boolean myPlayerTurn = true;
    private boolean myCombatActive = true;

    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(final GameUI theUI) {
        showScreen(theUI, null);
    }

    public void showScreen(final GameUI theUI, final List<Monster> monsters) {
        testResourceLoading(); // test resources if loading properly
        initializeCombatData();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("combat-root");

        createTopBattlefield();
        createBottomInterface();

        root.setTop(myTopBattlefield);
        root.setBottom(myBottomInterface);

        Scene combatScene = new Scene(root, 800, 600);

        getStage().setScene(combatScene);
        getStage().setTitle("Combat - " + (myCurrentHero != null ? myCurrentHero.getName() : "Hero") +
                " vs " + (myCurrentMonster != null ? myCurrentMonster.getName() : "Monster"));

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
        getStage().setTitle("Combat - " + (myCurrentHero != null ? myCurrentHero.getName() : "Hero") +
                " vs " + (myCurrentMonster != null ? myCurrentMonster.getName() : "Monster"));

        getStage().show();
        playEntranceAnimation();
    }

    private void initializeCombatData() {
        myCurrentHero = getController().getPlayer();
        if (myCurrentHero != null) {
            Room currentRoom = getController().getDungeon().getRoom(myCurrentHero.getPosition());
            List<Monster> monsters = currentRoom.getMonsters();
            myCurrentMonster = monsters.isEmpty() ? null : monsters.get(0);
        }
    }

    private void createTopBattlefield() {
        myTopBattlefield = new VBox();
        myTopBattlefield.setPrefHeight(350);
        myTopBattlefield.getStyleClass().add("battlefield");

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
        myTopBattlefield.getChildren().add(battleArea);
    }

    private VBox createHeroSide() {
        VBox heroSide = new VBox(10);
        heroSide.setAlignment(Pos.CENTER);

        myHeroSprite = createHeroSprite();

        HBox heroInfo = new HBox(10);
        heroInfo.getStyleClass().add("combat-info-panel");
        heroInfo.setAlignment(Pos.CENTER_RIGHT);

        VBox nameSection = new VBox(3);
        myHeroNameDisplay = new Text(myCurrentHero != null ? myCurrentHero.getName() : "Hero");
        myHeroNameDisplay.getStyleClass().add("combat-name");
        Text classText = new Text(myCurrentHero != null ? myCurrentHero.getType().getDisplayName() : "Unknown");
        classText.getStyleClass().add("combat-subtitle");
        nameSection.getChildren().addAll(myHeroNameDisplay, classText);

        VBox healthSection = createHealthSection(true);

        heroInfo.getChildren().addAll(nameSection, healthSection);

        heroSide.getChildren().addAll(heroInfo, myHeroSprite);

        return heroSide;
    }

    private VBox createMonsterSide() {
        VBox monsterSide = new VBox(10);
        monsterSide.setAlignment(Pos.CENTER_RIGHT);

        myMonsterSprite = createMonsterSprite();

        // Monster info panel (top)
        HBox monsterInfo = new HBox();
        monsterInfo.getStyleClass().add("combat-info-panel");
        monsterInfo.setAlignment(Pos.CENTER);

        // Name Section (Left side of HBox)
        VBox nameSection = new VBox(3);
        nameSection.setAlignment(Pos.CENTER_LEFT);
        myMonsterNameDisplay = new Text(myCurrentMonster != null ? myCurrentMonster.getName() : "Unknown Monster");
        myMonsterNameDisplay.getStyleClass().add("combat-name");
        Text levelText = new Text("Lv. " + (myCurrentMonster != null && myCurrentMonster.isElite() ? "Elite" : "Normal"));
        levelText.getStyleClass().add("combat-subtitle");
        nameSection.getChildren().addAll(myMonsterNameDisplay, levelText);

        // Health Section (Right side of HBox)
        VBox healthSection = createHealthSection(false);
        monsterInfo.getChildren().addAll(nameSection, healthSection);

        // Add HBox and Sprite to the main VBox
        monsterSide.getChildren().addAll(monsterInfo, myMonsterSprite);

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
            if (myCurrentHero != null) {
                switch (myCurrentHero.getType()) {
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
            if (myCurrentMonster != null) {
                spritePath += myCurrentMonster.getType().getName().toLowerCase().replace(" ", "_") + ".png";
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

    private VBox createHealthSection(final boolean theIsHero) {
        VBox healthSection = new VBox(3);
        healthSection.setAlignment(theIsHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

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

        if (theIsHero) {
            myHeroHealthBar = healthBar;
            myHeroHealthNumbers = new Label();
            myHeroHealthNumbers.getStyleClass().add("combat-subtitle");

            if (myCurrentHero != null) {
                double ratio = myCurrentHero.getHealthPercentage();
                myHeroHealthNumbers.setText(myCurrentHero.getHealthDisplay());
                updateHealthBar(myHeroHealthBar, ratio);
            } else {
                myHeroHealthNumbers.setText("0 / 0");
                myHeroHealthBar.setProgress(0.0); // Set to 0 if no hero
            }
            healthSection.getChildren().addAll(hpLabel, myHeroHealthBar, myHeroHealthNumbers);

        } else { // Monster
            myMonsterHealthBar = healthBar;
            myMonsterHealthNumbers = new Label();
            myMonsterHealthNumbers.getStyleClass().add("combat-subtitle");

            if (myCurrentMonster != null) {
                double ratio = myCurrentMonster.getHealthPercentage();
                myMonsterHealthNumbers.setText(myCurrentMonster.getHealthDisplay());
                updateHealthBar(myMonsterHealthBar, ratio);
            } else {
                myMonsterHealthNumbers.setText("0 / 0");
                myMonsterHealthBar.setProgress(0.0); // Set to 0 if no monster
            }
            healthSection.getChildren().addAll(hpLabel, myMonsterHealthBar, myMonsterHealthNumbers);
        }
        return healthSection;
    }

    private void createBottomInterface() {
        myBottomInterface = new VBox();
        myBottomInterface.setPrefHeight(250);
        myBottomInterface.getStyleClass().add("bottom-interface");
        myBottomInterface.setPadding(new Insets(15));

        // Combat messages
        myCombatMessages = new VBox(5);
        myCombatMessages.setPrefHeight(120);
        myCombatMessages.getStyleClass().add("combat-message-area");
        myCombatMessages.setAlignment(Pos.BOTTOM_LEFT);

        ScrollPane messageScroll = new ScrollPane(myCombatMessages);
        messageScroll.setFitToWidth(true);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setStyle("-fx-background-color: transparent;");

        HBox pokemonStyleButtons = createPokemonStyleButtons();

        myBottomInterface.getChildren().addAll(messageScroll, pokemonStyleButtons);
    }

    private HBox createPokemonStyleButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button attackBtn = new Button("ATTACK");
        attackBtn.getStyleClass().addAll("combat-button", "attack-button");

        Button specialBtn = new Button(
                myCurrentHero != null ? myCurrentHero.getType().getSpecialAttackName().toUpperCase() : "SPECIAL"
        );
        specialBtn.getStyleClass().addAll("combat-button", "special-button");

        Button itemBtn = new Button("ITEM");
        itemBtn.getStyleClass().addAll("combat-button", "item-button");

        Button runBtn = new Button("RUN");
        runBtn.getStyleClass().addAll("combat-button", "run-button");

        // Set actions
        attackBtn.setOnAction(event -> {
            if (myPlayerTurn && myCombatActive) {
                performPlayerAttack();
            }
        });

        specialBtn.setOnAction(event -> {
            if (myPlayerTurn && myCombatActive) {
                performSpecialAttack();
            }
        });

        itemBtn.setOnAction(event -> {
            addCombatMessage("Item menu - opening inventory...");
            getController().getGameController().openInventory();
        });

        runBtn.setOnAction(event -> {
            addCombatMessage(myCurrentHero.getName() + " tries to run away!");
            getController().getGameController().playerRun();
        });

        buttonBox.getChildren().addAll(attackBtn, specialBtn, itemBtn, runBtn);
        return buttonBox;
    }

    // COMBAT LOGIC

    private void performPlayerAttack() {
        if (!myPlayerTurn || !myCombatActive || myCurrentMonster == null) return;
        final Monster attackedMonster = myCurrentMonster;

        myPlayerTurn = false;
        updateTurnIndicator();
        addCombatMessage(myCurrentHero.getName() + " uses Attack!");

        playAttackAnimation(() -> {
            getController().getGameController().playerAttack();
            updateCombatDisplay();

            // Re-fetch monster in case it died
            Room currentRoom = getController().getDungeon().getRoom(myCurrentHero.getPosition());
            List<Monster> monsters = currentRoom.getMonsters();
            myCurrentMonster = monsters.isEmpty() ? null : monsters.get(0);

            if (myCurrentMonster != null && myCurrentMonster.isAlive()) {
                myPlayerTurn = false;
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> performMonsterTurn()));
                delay.play();
            } else {
                endCombat(true, attackedMonster);
            }
        });
    }

    private void performSpecialAttack() {
        if (!myPlayerTurn || !myCombatActive || myCurrentMonster == null) return;

        if (myCurrentHero.canUseSpecialAttack()) {
            final Monster attackedMonster = myCurrentMonster;
            myPlayerTurn = false;
            updateTurnIndicator();
            addCombatMessage(myCurrentHero.getName() + " uses " + myCurrentHero.getType().getSpecialAttackName() + "!");

            playSpecialAttackAnimation(() -> {
                getController().getGameController().playerSpecialAttack();
                updateCombatDisplay();

                Room currentRoom = getController().getDungeon().getRoom(myCurrentHero.getPosition());
                List<Monster> monsters = currentRoom.getMonsters();
                myCurrentMonster = monsters.isEmpty() ? null : monsters.get(0);

                if (myCurrentMonster != null && myCurrentMonster.isAlive()) {
                    myPlayerTurn = false;
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
        if (!myCombatActive || myCurrentMonster == null) return;

        addCombatMessage(myCurrentMonster.getName() + " attacks!");

        playMonsterAttackAnimation(() -> {
            getController().getGameController().monsterAttacks();
            boolean isGameOver = getController().getGameController().getStateController().isInState(GameState.GAME_OVER);

            if (isGameOver) {
                // Controller handles showing GameOverScreen. We just show defeat animation.
                endCombat(false);
            } else if (myCombatActive) {
                // If combat is still active, it's our turn.
                myPlayerTurn = true;
                updateTurnIndicator();
            }
        });
    }

    // ANIMATIONS

    private void playEntranceAnimation() {
        // Hero slides in from left
        TranslateTransition heroEntrance = new TranslateTransition(Duration.seconds(1), myHeroSprite);
        heroEntrance.setFromX(-400);
        heroEntrance.setToX(0);

        // Monster slides in from right
        TranslateTransition monsterEntrance = new TranslateTransition(Duration.seconds(1), myMonsterSprite);
        monsterEntrance.setFromX(400);
        monsterEntrance.setToX(0);

        ParallelTransition entrance = new ParallelTransition(heroEntrance, monsterEntrance);
        entrance.setOnFinished(e -> {
            addCombatMessage("A wild " + (myCurrentMonster != null ? myCurrentMonster.getName() : "Monster") + " appeared!");
            addCombatMessage("What will " + (myCurrentHero != null ? myCurrentHero.getName() : "Hero") + " do?");
        });
        entrance.play();
    }

    private void playAttackAnimation(final Runnable theOnComplete) {
        // Hero moves right towards monster
        Timeline attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(myHeroSprite.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(200), new KeyValue(myHeroSprite.translateXProperty(), 20)),
                new KeyFrame(Duration.millis(250), new KeyValue(myHeroSprite.translateXProperty(), 15)),
                new KeyFrame(Duration.millis(300), new KeyValue(myHeroSprite.translateXProperty(), 25)),
                new KeyFrame(Duration.millis(350), new KeyValue(myHeroSprite.translateXProperty(), 15)),
                new KeyFrame(Duration.millis(500), new KeyValue(myHeroSprite.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> {
            // Flash monster when hit
            FadeTransition flash = new FadeTransition(Duration.millis(100), myMonsterSprite);
            flash.setFromValue(1.0);
            flash.setToValue(0.3);
            flash.setCycleCount(4);
            flash.setAutoReverse(true);
            flash.setOnFinished(event -> theOnComplete.run());
            flash.play();
        });

        attack.play();
    }

    private void playSpecialAttackAnimation(final Runnable theOnComplete) {
        // More dramatic animation for special attacks
        RotateTransition spin = new RotateTransition(Duration.millis(600), myHeroSprite);
        spin.setByAngle(360);

        ScaleTransition grow = new ScaleTransition(Duration.millis(600), myHeroSprite);
        grow.setToX(1.3);
        grow.setToY(1.3);
        grow.setAutoReverse(true);
        grow.setCycleCount(2);

        ParallelTransition special = new ParallelTransition(spin, grow);
        special.setOnFinished(e -> {
            // Intense flash on monster
            FadeTransition flash = new FadeTransition(Duration.millis(80), myMonsterSprite);
            flash.setFromValue(1.0);
            flash.setToValue(0.1);
            flash.setCycleCount(4);
            flash.setAutoReverse(true);
            flash.setOnFinished(event -> theOnComplete.run());
            flash.play();
        });

        special.play();
    }

    private void playMonsterAttackAnimation(final Runnable theOnComplete) {
        // Monster moves left towards hero
        Timeline attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(myMonsterSprite.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(myMonsterSprite.translateXProperty(), -40)),
                new KeyFrame(Duration.millis(600), new KeyValue(myMonsterSprite.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> {
            // Flash hero when hit
            FadeTransition flash = new FadeTransition(Duration.millis(150), myHeroSprite);
            flash.setFromValue(1.0);
            flash.setToValue(0.4);
            flash.setCycleCount(2);
            flash.setAutoReverse(true);
            flash.setOnFinished(event -> theOnComplete.run());
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
        if (myCurrentHero != null) {
            myHeroNameDisplay.setText(myCurrentHero.getName());
            myHeroHealthNumbers.setText(myCurrentHero.getHealthDisplay());
        }

        if (myCurrentMonster != null) {
            myMonsterNameDisplay.setText(myCurrentMonster.getName());
            if (myMonsterHealthNumbers != null && myCurrentMonster.getMaxHealth() > 0) {
                myMonsterHealthNumbers.setText(myCurrentMonster.getHealth() + " / " + myCurrentMonster.getMaxHealth());
            } else if (myMonsterHealthNumbers != null) {
                myMonsterHealthNumbers.setText(String.valueOf(myCurrentMonster.getHealth()));
            }
        }
    }

    private void updateHealthBars() {
        if (myCurrentHero != null) {
            double heroHealthRatio = myCurrentHero.getHealthPercentage();
            myHeroHealthBar.setProgress(heroHealthRatio);

            Timeline heroHealthAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.8),
                            new KeyValue(myHeroHealthBar.progressProperty(), heroHealthRatio))
            );
            heroHealthAnimation.play();
            updateHealthBar(myHeroHealthBar, heroHealthRatio);
            if (myHeroHealthNumbers != null && myCurrentHero.getMaxHealth() > 0) { // Check if max HP exists
                myHeroHealthNumbers.setText(myCurrentHero.getHealth() + " / " + myCurrentHero.getMaxHealth());
            } else if (myHeroHealthNumbers != null) {
                myHeroHealthNumbers.setText(String.valueOf(myCurrentHero.getHealth()));
            }
        }

        if (myCurrentMonster != null) {
            double monsterHealthRatio = myCurrentMonster.getHealthPercentage();
            myMonsterHealthBar.setProgress(monsterHealthRatio);

            Timeline monsterHealthAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.8),
                            new KeyValue(myMonsterHealthBar.progressProperty(), monsterHealthRatio))
            );
            monsterHealthAnimation.play();
            updateHealthBar(myMonsterHealthBar, monsterHealthRatio);

            if (myMonsterHealthNumbers != null && myCurrentMonster.getMaxHealth() > 0) { // Check if max HP exists
                myMonsterHealthNumbers.setText(myCurrentMonster.getHealth() + " / " + myCurrentMonster.getMaxHealth());
            } else if (myMonsterHealthNumbers != null) {
                myMonsterHealthNumbers.setText(String.valueOf(myCurrentMonster.getHealth()));
            }
        }
    }

    private void updateHealthBar(final ProgressBar theHealthBar, final double theRatio) {
        // Remove all health bar style classes
        theHealthBar.getStyleClass().removeAll("health-bar-green", "health-bar-orange", "health-bar-red");

        // Add appropriate style class based on health ratio
        if (theRatio > 0.6) {
            theHealthBar.getStyleClass().add("health-bar-green");
        } else if (theRatio > 0.3) {
            theHealthBar.getStyleClass().add("health-bar-orange");
        } else {
            theHealthBar.getStyleClass().add("health-bar-red");
        }
    }

    // COMBAT END
    /**
     * Overloaded endCombat to handle calls that don't need a specific defeated monster.
     * Calls the main endCombat method, passing the current monster (or null if none).
     */
    private void endCombat(final boolean theVictory) {
        // For the defeat case (victory=false), we don't need a specific monster name,
        // so passing currentMonster (which might be null or the last one) is fine.
        endCombat(theVictory, myCurrentMonster);
    }

    private void endCombat(final boolean theVictory, final Monster theMonsterForMessage) {
        myCombatActive = false;
        myPlayerTurn = false;

        if (theVictory) {
            String monsterName = (theMonsterForMessage != null) ? theMonsterForMessage.getName() : "The monster";
            addCombatMessage(monsterName + " was defeated!");
            addCombatMessage(myCurrentHero.getName() + " wins the battle!");

            // Victory animation
            RotateTransition victoryRotation = new RotateTransition(Duration.seconds(1), myHeroSprite);
            victoryRotation.setByAngle(360);
            victoryRotation.play();

            Timeline endDelay = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                getController().resumeCurrentGame(getController().getGameController().getGameUI());
            }));
            endDelay.play();

        } else {
            addCombatMessage(myCurrentHero.getName() + " was defeated!");
            addCombatMessage("Game Over!");

            // Defeat animation
            FadeTransition defeat = new FadeTransition(Duration.seconds(1), myHeroSprite);
            defeat.setToValue(0.3);
            defeat.play();
        }
    }

    // ============================================================================
    // MESSAGE SYSTEM
    // ============================================================================

    public void addGameMessage(final String theMessage) {
        addCombatMessage(theMessage);
    }

    private void addCombatMessage(final String theMessage) {
        Label messageLabel = new Label(theMessage);
        messageLabel.getStyleClass().add("combat-message");
        messageLabel.setMaxWidth(700);

        myCombatMessages.getChildren().add(messageLabel);

        // Keep only last 6 messages
        if (myCombatMessages.getChildren().size() > 6) {
            myCombatMessages.getChildren().remove(0);
        }

        // Typing effect animation
        animateTyping(messageLabel, theMessage);
    }

    private void animateTyping(final Label theLabel, final String theFullText) {
        Timeline typing = new Timeline();
        theLabel.setText("");

        for (int i = 0; i <= theFullText.length(); i++) {
            final int index = i;
            typing.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 25),
                            e -> theLabel.setText(theFullText.substring(0, index)))
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
                new KeyFrame(Duration.millis(0), new KeyValue(myTopBattlefield.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(50), new KeyValue(myTopBattlefield.translateXProperty(), -5)),
                new KeyFrame(Duration.millis(100), new KeyValue(myTopBattlefield.translateXProperty(), 5)),
                new KeyFrame(Duration.millis(150), new KeyValue(myTopBattlefield.translateXProperty(), -3)),
                new KeyFrame(Duration.millis(200), new KeyValue(myTopBattlefield.translateXProperty(), 3)),
                new KeyFrame(Duration.millis(250), new KeyValue(myTopBattlefield.translateXProperty(), 0))
        );
        shake.play();
    }

    /**
     * Shows floating damage numbers above sprites
     * @param theDamage Amount of damage to display
     * @param theIsPlayerAttack True if player dealt the damage, false if monster did
     */
    private void showFloatingDamageNumber(final int theDamage, final boolean theIsPlayerAttack) {
        Label damageLabel = new Label("-" + theDamage);
        damageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-text-fill: " + (theIsPlayerAttack ? "red" : "blue") + ";");

        // Position above the target
        double startX = theIsPlayerAttack ?
                myMonsterSprite.getLayoutX() + myMonsterSprite.getBoundsInLocal().getWidth() / 2 :
                myHeroSprite.getLayoutX() + myHeroSprite.getBoundsInLocal().getWidth() / 2;
        double startY = theIsPlayerAttack ?
                myMonsterSprite.getLayoutY() :
                myHeroSprite.getLayoutY();

        damageLabel.setLayoutX(startX);
        damageLabel.setLayoutY(startY);

        // Add to battlefield temporarily
        myTopBattlefield.getChildren().add(damageLabel);

        // Animate upward and fade out
        Timeline floatAnimation = new Timeline(
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(damageLabel.layoutYProperty(), startY - 50),
                        new KeyValue(damageLabel.opacityProperty(), 0)
                )
        );

        floatAnimation.setOnFinished(e -> myTopBattlefield.getChildren().remove(damageLabel));
        floatAnimation.play();
    }

    /**
     * Shows special status effects (like critical hits, buffs, debuffs)
     * @param theEffectName Name of the effect
     * @param theEffectColor Color of the effect text
     * @param theIsForHero True if effect is on hero, false if on monster
     */
    public void showStatusEffect(final String theEffectName, final Color theEffectColor,
                                 final boolean theIsForHero) {
        Label statusLabel = new Label(theEffectName);
        statusLabel.setTextFill(theEffectColor);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Position above character
        ImageView targetSprite = theIsForHero ? myHeroSprite : myMonsterSprite;
        statusLabel.setLayoutX(targetSprite.getLayoutX() + targetSprite.getBoundsInLocal().getWidth() / 2);
        statusLabel.setLayoutY(targetSprite.getLayoutY() - 20);

        // Add to scene temporarily
        myTopBattlefield.getChildren().add(statusLabel);

        // Fade out and remove
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), statusLabel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> myTopBattlefield.getChildren().remove(statusLabel));
        fadeOut.play();
    }

    /**
     * Enhanced critical hit animation
     */
    public void playCriticalHitAnimation() {
        addCombatMessage("Critical Hit!");

        // Golden flash effect
        FadeTransition critFlash = new FadeTransition(Duration.millis(200), myMonsterSprite);
        critFlash.setFromValue(1.0);
        critFlash.setToValue(0.3);
        critFlash.setCycleCount(4);
        critFlash.setAutoReverse(true);

        // Scale animation
        ScaleTransition critScale = new ScaleTransition(Duration.millis(400), myHeroSprite);
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
        return myCombatActive;
    }

    /**
     * Gets the current player turn state
     * @return True if it's currently the player's turn
     */
    public boolean isPlayerTurn() {
        return myPlayerTurn;
    }

    /**
     * Updates the turn indicator (if you want to add visual turn indicators)
     */
    private void updateTurnIndicator() {
        // You can add visual indicators here to show whose turn it is
        if (myPlayerTurn) {
            myHeroSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.CYAN));
            myMonsterSprite.setEffect(null);
        } else {
            myMonsterSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.RED));
            myHeroSprite.setEffect(null);
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
        System.out.println("Combat Active: " + myCombatActive);
        System.out.println("Player Turn: " + myPlayerTurn);
        System.out.println("Hero: " + (myCurrentHero != null ? myCurrentHero.getName() : "null"));
        System.out.println("Monster: " + (myCurrentMonster != null ? myCurrentMonster.getName() : "null"));
        if (myCurrentHero != null) {
            System.out.println("Hero Health: " + myCurrentHero.getHealth() + "/" + myCurrentHero.getMaxHealth());
        }
        if (myCurrentMonster != null) {
            System.out.println("Monster Health: " + myCurrentMonster.getHealth());
        }
        System.out.println("=== END DEBUG INFO ===");
    }

    // ARENA BACKGROUND WITH CUSTOM PLATFORMS

    /**
     * Apply arena-style background with gray gradients and round cobblestone platforms
     */
    private void applyArenaBackground(final BorderPane theRoot) {
        // Battlefield area
        myTopBattlefield.setStyle("-fx-background-color: transparent;");

        // Create the cobblestone platforms
        createBattlePlatforms();
    }

    /**
     * Creates a Pokémon-style battle platform (flatter, elliptical).
     * @param theCenterX X position of platform center
     * @param theCenterY Y position of platform center
     * @param theRadiusX Radius of the ellipse on the X-axis
     * @param theRadiusY Radius of the ellipse on the Y-axis
     * @return Group containing the platform graphics
     */
    private javafx.scene.Group createPokemonStylePlatform(final double theCenterX, final double theCenterY,
                                                          final double theRadiusX, final double theRadiusY) {
        javafx.scene.Group platformGroup = new javafx.scene.Group();

        javafx.scene.shape.Ellipse baseEllipse = new javafx.scene.shape.Ellipse(theCenterX, theCenterY, theRadiusX, theRadiusY);

        // A semi-transparent, slightly glowing pad
        baseEllipse.setFill(javafx.scene.paint.Color.rgb(150, 150, 220, 0.3)); // Light blue/purple, semi-transparent
        baseEllipse.setStroke(javafx.scene.paint.Color.rgb(200, 200, 255, 0.7)); // Lighter stroke
        baseEllipse.setStrokeWidth(2);

        javafx.scene.effect.Bloom bloomEffect = new javafx.scene.effect.Bloom();
        bloomEffect.setThreshold(0.6);
        baseEllipse.setEffect(bloomEffect);

        platformGroup.getChildren().add(baseEllipse);

        // subtle inner pattern or texture if desired,
        javafx.scene.shape.Ellipse innerAccent = new javafx.scene.shape.Ellipse(theCenterX, theCenterY, theRadiusX * 0.7, theRadiusY * 0.7);
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
        myTopBattlefield.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("platform-layer"));

        javafx.scene.layout.Pane platformLayer = new javafx.scene.layout.Pane();
        platformLayer.setUserData("platform-layer");
        platformLayer.setMouseTransparent(true);

        double heroPlatformCenterX = 160;
        double heroPlatformCenterY = 288;
        double heroPlatformRadiusX = 90;
        double heroPlatformRadiusY = 45;

        myHeroPlatform = createPokemonStylePlatform(
                heroPlatformCenterX, heroPlatformCenterY, heroPlatformRadiusX, heroPlatformRadiusY);


        // Monster platform (top-rightish, or centered opposite hero)
        double monsterPlatformCenterX = 604;
        double monsterPlatformCenterY = 256;
        double monsterPlatformRadiusX = 100;
        double monsterPlatformRadiusY = 50;

        myMonsterPlatform = createPokemonStylePlatform(
                monsterPlatformCenterX, monsterPlatformCenterY, monsterPlatformRadiusX, monsterPlatformRadiusY);

        platformLayer.getChildren().addAll(myHeroPlatform, myMonsterPlatform);
        // Ensure platforms are drawn but don't force VBox height
        platformLayer.setPrefHeight(350); // Match battlefield height
        myTopBattlefield.getChildren().add(0, platformLayer);
    }
}