package main.View;

import main.Controller.Controller;
import main.Controller.InputController; // Make sure InputController is imported
// import main.Controller.GameController; // If GameController is used by GameUI or passed around
import main.View.screen.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Manages the overall UI of the game, including screen transitions.
 * It holds references to the primary stage and the main controller.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class GameUI {
    private final Stage myPrimaryStage;
    private final Controller myController;
    private InputController myInputController; // Field to store the InputController

    /**
     * Constructor for GameUI.
     *
     * @param thePrimaryStage The primary stage of the application.
     * @param theController   The main controller instance for the application.
     */
    public GameUI(final Stage thePrimaryStage, final Controller theController) {
        if (thePrimaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null for GameUI.");
        }
        if (theController == null) {
            throw new IllegalArgumentException("Controller cannot be null for GameUI.");
        }
        this.myPrimaryStage = thePrimaryStage;
        this.myController = theController;
    }

    public void showIntroScreen() {
        IntroScreen intro = new IntroScreen(myPrimaryStage, myController);
        intro.showScreen(this);
    }

    public void showHeroSelection() {
        HeroSelectionScreen heroSelection = new HeroSelectionScreen(myPrimaryStage, myController);
        heroSelection.showScreen(this);
    }

    public void showGameScreen() {
        GameScreen gameScreen = new GameScreen(myPrimaryStage, myController);
        gameScreen.showScreen(this);
        // After game screen is shown, its scene is set. Now attach key listeners.
        attachKeyListenersToScene();
    }

    public void showPauseMenu() {
        PauseScreen pauseScreen = new PauseScreen(myPrimaryStage, myController);
        pauseScreen.showScreen(this);
    }

    public void showSavesScreen() {
        SavesScreen savesScreen = new SavesScreen(myPrimaryStage, myController);
        savesScreen.showScreen(this);
    }

    public void showWinScreen() {
        WinScreen winScreen = new WinScreen(myPrimaryStage, myController);
        winScreen.showScreen(this);
    }

    public void showLoseScreen() {
        LoseScreen loseScreen = new LoseScreen(myPrimaryStage, myController);
        loseScreen.showScreen(this);
    }

    public Controller getController() {
        return myController;
    }

    public Stage getPrimaryStage() {
        return myPrimaryStage;
    }

    /**
     * Sets the input controller and attaches its listeners to the current scene.
     * This method should be called after a scene that requires input (like GameScreen) is set on the stage.
     * @param theInputController The input controller for game actions.
     */
    public void setInputController(InputController theInputController) {
        this.myInputController = theInputController;
        attachKeyListenersToScene(); // Attempt to attach listeners immediately
    }

    /**
     * Attaches key listeners from the InputController to the current scene on the primary stage.
     * This should be called whenever the scene changes to one that needs key input (e.g., GameScreen).
     */
    private void attachKeyListenersToScene() {
        Scene currentScene = myPrimaryStage.getScene();
        if (currentScene != null && this.myInputController != null) {
            // Remove old listeners first to prevent duplicates if called multiple times on same scene
            currentScene.setOnKeyPressed(null);
            currentScene.setOnKeyReleased(null);

            // Add new listeners
            currentScene.setOnKeyPressed(this.myInputController::handleKeyPress);
            currentScene.setOnKeyReleased(this.myInputController::handleKeyRelease);
            System.out.println("InputController key listeners attached to scene.");
        } else if (currentScene == null) {
            System.err.println("GameUI: Cannot attach key listeners, current scene is null.");
        } else { // myInputController is null
            System.err.println("GameUI: Cannot attach key listeners, InputController is null.");
        }
    }


    public InputController getInputController() {
        return myInputController;
    }
}
