package View;
import View.screen.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Controller.Controller;
import Controller.InputController;



/**
 * Holds methods which display the different screens in the game.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/4/2025
 */
public class GameUI {

    /**
     * Primary Stage needed to show every scene.
     */
    private final Stage PRIMARY_STAGE;
    /**
     * Controller used for event handlers.
     */
    private final Controller CONTROLLER;

    /**
     * Input controller for handling key events.
     */
    private InputController inputController;

    /**
     * The current game scene.
     */
    private Scene gameScene;

    /**
     * Constructor that takes Stage from Main, assigning it to the
     * MY_PRIMARY_STAGE final.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public GameUI(Stage thePrimaryStage) {
        PRIMARY_STAGE = thePrimaryStage;
        PRIMARY_STAGE.setTitle("DungeonDive");
        CONTROLLER = new Controller();
    }

    /**
     * Displays the game intro screen.
     */
    public void showIntroScreen() {
        IntroScreen screen = new IntroScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Displays the saves screen.
     */
    public void showSavesScreen() {
        SavesScreen screen = new SavesScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Displays the hero selection screen.
     */
    public void showHeroSelection() {
        HeroSelectionScreen screen = new HeroSelectionScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Displays the game screen, where the actual game is played.
     */
    public void showGameScreen() {
        GameScreen screen = new GameScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Displays pause menu.
     */
    public void showPauseMenu() {
        PauseScreen screen = new PauseScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Displays win screen.
     */
    public void showWinScreen() {
        WinScreen screen = new WinScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Displays lose screen.
     */
    public void showLoseScreen() {
        LoseScreen screen = new LoseScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Updates the game display.
     */
    public void updateDisplay() {
        // This will be implemented to update the game view
        // based on the current state of the model
    }

    /**
     * set input controller and sets up event handlers
     *
     * @param inputController input controller to use
     */
    public void setInputController(InputController inputController) {
        this.inputController = inputController;
        System.out.println("Input controller set in GameUI");

        // If we already have a game scene, set up the input handlers
        if (PRIMARY_STAGE.getScene() != null) {
            PRIMARY_STAGE.getScene().setOnKeyPressed(event ->
                    inputController.handleKeyInput(event));
            System.out.println("Key event handlers set up");
        }
    }

    /**
     * set up input handlers for game scene
     */
    private void setupInputHandlers() {
        //set up key event handlers
        gameScene.setOnKeyPressed(event -> inputController.handleKeyInput(event));
    }

    /**
     * set current game scene for input handling
     *
     * @param scene game scene
     */
    public void setGameScene(Scene scene) {
        this.gameScene = scene;

        //set up input handling if controller is available
        if (inputController != null) {
            setupInputHandlers();
        }
    }

    /**
     * gets the controller instance
     *
     * @return controller instance
     */
    public Controller getController() {
        return CONTROLLER;
    }

}





