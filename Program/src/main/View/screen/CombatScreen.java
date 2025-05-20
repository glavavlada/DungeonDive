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
import main.View.GameUI;

public class CombatScreen extends Screen {

    private Label myName;
    private Label myHealth;
    private Label myBaseDamage;
    private Label mySpecialDamage;
    private Label myBlockChance;
    private VBox myCombatMessages;

    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(GameUI theUI) {
        BorderPane root = new BorderPane();
        Scene CombatScene = new Scene(root, 800, 500);

        // Player and monster stats on the left.
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        myName = new Label("Name: ");
        myHealth = new Label("Health: ");
        myBlockChance = new Label("Block Chance: ");
        Label enemyName = new Label("Name: ");
        Label enemyHealth = new Label("Health: ");
        Label enemyAttackSpeed = new Label("Attack Speed: ");
        Label enemyHitChance = new Label("Hit Chance: ");
        Label enemyBlockChance = new Label("Block Chance: ");
        statsBox.getChildren().addAll(
                new Label("--- Player Stats ---"),
                myName,
                myHealth,
                myBlockChance,
                new Label("--- Monster Stats ---"),
                enemyName,
                enemyHealth,
                enemyAttackSpeed,
                enemyHitChance,
                enemyBlockChance
        );
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
        myCombatMessages.setPrefHeight(100);
        ScrollPane messageScrollPane = new ScrollPane(myCombatMessages);
        messageScrollPane.setFitToWidth(true);
        root.setBottom(messageScrollPane);


        updateCombatStats();
        addGameMessage(getController().getGameController().getCombatDescription());
        getStage().setScene(CombatScene);
        getStage().setTitle("Combat Screen");
        getStage().show();
    }

    /**
     * Updates stats during combat.
     */
    public void updateCombatStats() {
        if (getController() != null && getController().getPlayer() != null) {
            main.Model.character.Hero player = getController().getPlayer();
            myName.setText("Name: " + player.getName());
            myHealth.setText("Health: " + player.getHealth() + " / " + player.getMaxHealth());


            myBaseDamage.setText("Damage: " + player.getType().getBaseAttack());
            mySpecialDamage.setText("Damage: " + player.getType().getSpecialAttackDamage());



        }
    }

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
}
