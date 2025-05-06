package View.screen;

import Controller.Controller;
import View.GameUI;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HeroSelectionScreen extends Screen {

    public HeroSelectionScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    @Override
    public void showScreen(GameUI theUI) {
        // Pane type and Scene.
        VBox root = new VBox();
        Scene heroSelection = new Scene(root, 600, 500);


        // Buttons, textboxes, etc.
        ToggleGroup heroButtons = new ToggleGroup();
        RadioButton warrior = new RadioButton("Warrior");
        warrior.setToggleGroup(heroButtons);
        RadioButton priestess = new RadioButton("Priestess");
        priestess.setToggleGroup(heroButtons);
        RadioButton thief = new RadioButton("Theif");
        thief.setToggleGroup(heroButtons);
        TextField nameBox = new TextField();
        nameBox.setPromptText("Enter Your Hero's Name");
        Button startGameBtn = new Button();
        startGameBtn.setText("Start Game");

        Label description = new Label();

        root.setAlignment(Pos.CENTER);
        setButtonSize(startGameBtn);
        nameBox.setMaxWidth(100);

        warrior.setOnAction(event ->
                CONTROLLER.heroDescription(1, description));
        priestess.setOnAction(event ->
                CONTROLLER.heroDescription(2, description));
        thief.setOnAction(event ->
                CONTROLLER.heroDescription(3, description));

        nameBox.setOnAction(event -> CONTROLLER.setHeroName());
        startGameBtn.setOnAction(event ->
                CONTROLLER.startGame(new ActionEvent(), theUI, heroButtons));

        // Adds stuff to root, then shows on screen.
        root.getChildren().addAll(nameBox, warrior, priestess, thief,
                description, startGameBtn);

        PRIMARY_STAGE.setScene(heroSelection);
        PRIMARY_STAGE.show();


    }


}
