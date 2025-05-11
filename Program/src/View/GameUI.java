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
     * Constructor that takes Stage from Main, assigning it to the
     * MY_PRIMARY_STAGE final.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public GameUI(Stage thePrimaryStage) {
        PRIMARY_STAGE = thePrimaryStage;
        PRIMARY_STAGE.setTitle("DungeonDive");
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
     * Displays the win screen.
     */
    public void showWinScreen() {
        WinScreen screen = new WinScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    /**
     * Displays the loss screen.
     */
    public void showLoseScreen() {
        LoseScreen screen = new LoseScreen(PRIMARY_STAGE);
        screen.showScreen(this);
    }

    public void updateDisplay() {

    }

}
