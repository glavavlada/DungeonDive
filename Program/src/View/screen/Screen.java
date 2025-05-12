package View.screen;

import Controller.Controller;
import View.GameUI;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
