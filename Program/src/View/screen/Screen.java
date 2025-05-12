package View.screen;

import Controller.Controller;
import View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;

/**
 * An abstract class for the various Screens.
 *
 * @author Jacob Hilliker
 * @version 5/5/2025
 */
abstract class Screen {
    /**
     * Primary Stage needed to show every scene.
     */
    private final Stage PRIMARY_STAGE;
    /**
     * Controller used for event handlers.
     */
    private final Controller CONTROLLER;
    /**
     * Width for consistent button widths.
     */
    private final double BUTTON_WIDTH = 100;
    /**
     * Height for consistent button heights.
     */
    private final double BUTTON_HEIGHT = 20;

    /**
     * Assigns Stage and Controller used by Screen subclasses.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    Screen(Stage thePrimaryStage) {
        PRIMARY_STAGE = thePrimaryStage;
        PRIMARY_STAGE.setTitle("DungeonDive");
        CONTROLLER = new Controller();
    }

    /**
     * Abstract method for showing the current screen.
     */
    abstract void showScreen(GameUI theUI);

    /**
     * Method for all screen classes to create button sizes quickly
     *  and consistently.
     *
     * @param theButton the Button to be given a size.
     */
    public void setButtonSize(Button theButton) {
        theButton.setPrefWidth(BUTTON_WIDTH);
        theButton.setPrefHeight(BUTTON_HEIGHT);
    }

    /**
     * Gives a VBox of buttons that are given text for the win and
     * lose screens.
     *
     * @return A VBox of buttons.
     */
    public VBox tripleButtonVBox() {
        VBox buttons = new VBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button newGameBtn = new Button("New Game");
        Button savedGamesBtn = new Button("Saved Games");
        Button quitToMenuBtn = new Button("Quit to Menu");

        setButtonSize(newGameBtn);
        setButtonSize(savedGamesBtn);
        setButtonSize(quitToMenuBtn);

        buttons.getChildren().addAll(newGameBtn, savedGamesBtn, quitToMenuBtn);
        return buttons;
    }

    /**
     * Sets the placements of buttons and title for the intro, win and
     * lose screens.
     */
    public void tripleButtonStructure(Text theTitle, VBox theButtons,
                                      BorderPane theRoot) {
        BorderPane.setAlignment(theTitle, Pos.TOP_CENTER);
        theRoot.setTop(theTitle);
        BorderPane.setAlignment(theButtons, Pos.CENTER);
        theRoot.setCenter(theButtons);
    }

    /**
     * Getter for PRIMARY_STAGE.
     *
     * @return PRIMARY_STAGE.
     */
    public Stage getStage() {
        return PRIMARY_STAGE;
    }

    /**
     * Getter for CONTROLLER.
     *
     * @return CONTROLLER.
     */
    public Controller getController() {
        return CONTROLLER;
    }


}
