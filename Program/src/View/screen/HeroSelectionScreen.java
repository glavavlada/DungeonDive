package View.screen;

import Controller.Controller;
import View.GameUI;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Class for the HeroSelectionScreen.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class HeroSelectionScreen extends Screen {
    /**
     * Calls Screen constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public HeroSelectionScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }
    /**
     * showScreen for the HeroSelectionScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        // Pane type and Scene.
        VBox root = new VBox(5);
        Scene heroSelection = new Scene(root, 600, 500);

        // Buttons, textboxes, etc.
        ToggleGroup heroButtons = new ToggleGroup();
        // The spaces in the names help the radio buttons align while
        // the VBox is centered. There is probably a better solution
        // but this works I guess.
        RadioButton warrior = new RadioButton("Warrior   ");
        warrior.setToggleGroup(heroButtons);
        RadioButton priestess = new RadioButton("Priestess ");
        priestess.setToggleGroup(heroButtons);
        RadioButton thief = new RadioButton("Theif       ");
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
                getController().heroDescription(1, description));
        priestess.setOnAction(event ->
                getController().heroDescription(2, description));
        thief.setOnAction(event ->
                getController().heroDescription(3, description));

        nameBox.setOnAction(event -> getController().setHeroName());
        startGameBtn.setOnAction(event ->
                getController().startGame(new ActionEvent(), theUI, heroButtons));

        // Adds stuff to root, then shows on screen.
        root.getChildren().addAll(nameBox, warrior, priestess, thief,
                description, startGameBtn);

        getStage().setScene(heroSelection);
        getStage().show();
    }
}
