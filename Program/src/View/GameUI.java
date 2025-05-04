package View;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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

    private final Stage MY_PRIMARY_STAGE;

    /**
     * Constructor that takes Stage from Main, assigning it to the
     * MY_PRIMARY_STAGE final.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public GameUI(Stage thePrimaryStage) {
        MY_PRIMARY_STAGE = thePrimaryStage;
        MY_PRIMARY_STAGE.setTitle("DungeonDive");
    }

    /**
     * Displays the game intro screen.
     */
    public void showIntroScreen() {

        StackPane root = new StackPane();
        Scene intro = new Scene(root, 600, 500);

        Button newGameBtn = new Button();
        newGameBtn.setText("New Game");
        Button exitBtn = new Button();
        exitBtn.setText("Exit");

        root.getChildren().add(newGameBtn);
        root.getChildren().add(exitBtn);
        root.setAlignment(exitBtn, Pos.TOP_RIGHT);

        MY_PRIMARY_STAGE.setScene(intro);

        Controller controller = new Controller();
        // Takes in ActionEvent (click), and passes itself to newGame().
        newGameBtn.setOnAction(event -> controller.newGame(new ActionEvent(), this));
        exitBtn.setOnAction(event -> controller.exitProgram(new ActionEvent()));

        MY_PRIMARY_STAGE.show();
    }

    /**
     * Displays the hero selection screen.
     */
    public void showHeroSelection() {
        VBox root = new VBox();
        Scene heroSelection = new Scene(root, 600, 500);

        ToggleGroup heroButtons = new ToggleGroup();
        RadioButton warrior = new RadioButton("Warrior");
        RadioButton priestess = new RadioButton("Priestess");
        RadioButton thief = new RadioButton("Theif");

        root.getChildren().addAll(warrior, priestess, thief);

        MY_PRIMARY_STAGE.setScene(heroSelection);

        MY_PRIMARY_STAGE.show();
    }

    public void showGameScreen() {

    }

    public void showPauseMenu() {

    }

    public void showWinScreen() {

    }

    public void showLoseScreen() {

    }

    public void updateDisplay() {

    }

}
