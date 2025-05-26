package main.View.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.View.GameUI;

import java.util.ArrayList;
import java.util.List;

public class CombatScreen extends Screen {

    private Label myName;
    private Label myHealth;
    private Label myBaseDamage;
    private Label mySpecialDamage;
    private Label myBlockChance;

    private Label enemyNameLabel;
    private Label enemyHealthLabel;
    private Label enemyAttackLabel;

    private VBox myCombatMessages;
    private Monster currentMonster;

    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    public void showScreen(GameUI theUI, List<Monster> theMonsters) {
        BorderPane root = new BorderPane();
        Scene CombatScene = new Scene(root, 800, 500);
        root.setStyle("-fx-padding: 10;");

        // Player stats vbox
        VBox playerStatsBox = new VBox(10);
        myName = new Label("Name: ");
        myHealth = new Label("Health: ");
        myBlockChance = new Label("Block Chance: ?");
        playerStatsBox.getChildren().addAll(new Label("--- Player ---"), myName, myHealth, myBlockChance);
        playerStatsBox.setAlignment(Pos.CENTER_LEFT);

// Monster stats
        VBox monsterStatsBox = new VBox(5);
        enemyNameLabel = new Label("Name: ");
        enemyHealthLabel = new Label("Health: ");
        enemyAttackLabel = new Label("Attack: "); // Example
        monsterStatsBox.getChildren().addAll(new Label("--- Monster ---"), enemyNameLabel, enemyHealthLabel, enemyAttackLabel);
        monsterStatsBox.setAlignment(Pos.CENTER_LEFT);

        VBox statsBox = new VBox(20, playerStatsBox, monsterStatsBox);
        statsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        statsBox.setAlignment(Pos.CENTER);
        root.setLeft(statsBox);

        // Combat Options on the right.
        VBox combatOptions = new VBox(10);
        combatOptions.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        Label combatOptionsLabel = new Label("--- Combat Options ---");
        Button attackButton = new Button("Base Attack");
        Button specialAttackButton = new Button(getController().getPlayer().getType().getSpecialAttackName());
        Button runButton = new Button("Run");
        setButtonSize(attackButton);
        setButtonSize(specialAttackButton);
        setButtonSize(runButton);

        attackButton.setOnAction(event -> getController().getGameController().playerAttack());
        specialAttackButton.setOnAction(event -> getController().getGameController().playerSpecialAttack());
        runButton.setOnAction(event -> getController().getGameController().playerRun());

        myBaseDamage = new Label("Damage: ");
        mySpecialDamage = new Label("Damage: ");
        combatOptions.getChildren().addAll(combatOptionsLabel, attackButton, myBaseDamage,
                                           specialAttackButton, mySpecialDamage, runButton);
        combatOptions.setAlignment(Pos.CENTER);
        root.setRight(combatOptions);

        // Combat message log at the bottom.
        myCombatMessages = new VBox(5);
        myCombatMessages.setStyle("-fx-padding: 10; -fx-border-color: silver; -fx-border-width: 1; -fx-background-color: #f0f0f0;");
        ScrollPane messageScrollPane = new ScrollPane(myCombatMessages);
        messageScrollPane.setPrefHeight(100);
        messageScrollPane.setFitToWidth(true);
        root.setBottom(messageScrollPane);

        // Set current monster and update stats
        if (theMonsters != null && !theMonsters.isEmpty()) {
            this.currentMonster = theMonsters.get(0); // For now, focus on the first monster
        } else {
            this.currentMonster = null;
        }

        updateCombatStats();
        addGameMessage(getController().getGameController().getCombatDescription());
        getStage().setScene(CombatScene);
        getStage().setTitle("Combat Screen");
        getStage().show();
    }

//    /**
//     * Updates stats during combat.
//     */
//    public void updateCombatStats() {
//        Hero player = getController().getPlayer();
//        if (player != null) {
//            myName.setText("Name: " + player.getName());
//            myHealth.setText("Health: " + player.getHealth() + " / " + player.getMaxHealth());
//            myBaseDamage.setText("Damage: " + player.getType().getBaseAttack());
//            mySpecialDamage.setText("Damage: " + player.getType().getSpecialAttackDamage());
//            // Add block chance if available in Hero/HeroType
//            // myBlockChance.setText("Block Chance: " + player.getBlockChance() + "%");
//        }
//
//        // Update monster stats
//        if (currentMonster != null && currentMonster.isAlive()) {
//            enemyNameLabel.setText("Name: " + currentMonster.getName());
//            enemyHealthLabel.setText("Health: " + currentMonster.getHealth() + " / " + currentMonster.getMaxHealth());
//            enemyAttackLabel.setText("Attack: " + currentMonster.getType().getBaseAttack()); // Assuming Monster has getType() and MonsterType has getBaseAttack()
//        } else {
//            enemyNameLabel.setText("Name: - DEFEATED -");
//            enemyHealthLabel.setText("Health: -");
//            enemyAttackLabel.setText("Attack: -");
//            this.currentMonster = null; // Clear if dead
//        }
//    }

    public void addGameMessage(String message) {
        if (myCombatMessages != null) {
            Label messageLabel = new Label(message);
            myCombatMessages.getChildren().add(messageLabel);
            // Auto-scroll to bottom
            if (myCombatMessages.getParent() instanceof ScrollPane) {
                ((ScrollPane)myCombatMessages.getParent()).setVvalue(1.0);
            }
        }
    }

    /**
     * Updates which monster is being displayed (if you have multiple).
     * @param monster The new monster to display.
     */
    public void updateCurrentMonster(Monster monster) {
        this.currentMonster = monster;
        updateCombatStats();
    }

    /**
     * Updates the display with the current state of monsters.
     * Call this when monster health changes or a new monster is targeted.
     * @param monsters The current list of monsters.
     */
    public void updateDisplay(List<Monster> monsters) {
        System.out.println("CombatScreen: Updating display."); // Log to see if it's called
        if (monsters != null && !monsters.isEmpty()) {
            // Update to the first monster (or implement logic for multiple)
            this.currentMonster = monsters.get(0);
        } else {
            // No monsters left, set to null
            this.currentMonster = null;
        }
        updateCombatStats(); // Call the method that redraws labels

    }

    public void updateCombatStats() {
        Hero player = getController().getPlayer();
        if (player != null) {
            myName.setText("Name: " + player.getName());
            myHealth.setText("Health: " + player.getHealth() + " / " + player.getMaxHealth());
            myBaseDamage.setText("Damage: " + player.getType().getBaseAttack());
            mySpecialDamage.setText("Damage: " + player.getType().getSpecialAttackDamage());
        }

        // Update monster stats
        if (currentMonster != null && currentMonster.isAlive()) {
            enemyNameLabel.setText("Name: " + currentMonster.getName());
            enemyHealthLabel.setText("Health: " + currentMonster.getHealth() + " / " + currentMonster.getMaxHealth()); // Uses getMaxHealth()
            enemyAttackLabel.setText("Attack: " + currentMonster.getType().getBaseAttack());
        } else {
            enemyNameLabel.setText("Name: - DEFEATED -");
            enemyHealthLabel.setText("Health: -");
            enemyAttackLabel.setText("Attack: -");
        }
    }

    // You need to override the abstract showScreen(GameUI theUI) from Screen.
    // Since we now have showScreen(GameUI theUI, List<Monster> theMonsters),
    // we can provide a default implementation or throw an error.
    @Override
    public void showScreen(GameUI theUI) {
        // This shouldn't be called directly for CombatScreen anymore.
        // Or, you could show it with an empty monster list / error.
        System.err.println("CombatScreen.showScreen(GameUI) called without monster list! Showing empty screen.");
        showScreen(theUI, new ArrayList<>()); // Show with empty list as fallback.
    }
}
