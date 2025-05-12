package View.screen;

import View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Class for the IntroScreen, the screen shown at program start up.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class IntroScreen extends Screen {
    /**
     * Constructor calling Screen Constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public IntroScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    /**
     * showScreen for the IntroScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        // Creates pane as root and Scene for intro screen.
        Pane root = new Pane();
        Scene intro = new Scene(root, 600, 500);

        // Buttons resuming, new game, and exit.
        Button newGameBtn = new Button();
        Button exitBtn = new Button();
        Button gameSavesBtn = new Button();
        newGameBtn.setText("New Game");
        exitBtn.setText("Exit");
        gameSavesBtn.setText("Saved Games");

        setButtonSize(newGameBtn);
        setButtonSize(exitBtn);
        setButtonSize(gameSavesBtn);

        // Adds buttons to root.
        root.getChildren().addAll(newGameBtn, gameSavesBtn, exitBtn);

        // Event Handler stuff here.
        // Takes in ActionEvent (click), and passes itself to newGame().
        newGameBtn.setOnAction(event ->
                getController().newGame(new ActionEvent(), theUI));
        exitBtn.setOnAction(event ->
                getController().exitProgram(new ActionEvent()));
        gameSavesBtn.setOnAction(event ->
                getController().savedGames(new ActionEvent(), theUI));

        // Sets scene and shows screen.
        getStage().setScene(intro);
        getStage().show();

        // The setLayout is after .show(), because button size is needed for
        // location calculation, and the getWidth and getHeight return 0 until
        // button in shown on the screen.
        newGameBtn.setLayoutX((root.getWidth() - newGameBtn.getWidth()) / 2);
        newGameBtn.setLayoutY(((root.getHeight() - newGameBtn.getHeight()) / 2) + 50);
        gameSavesBtn.setLayoutX(newGameBtn.getLayoutX());
        gameSavesBtn.setLayoutY(newGameBtn.getLayoutY() + 30);
        exitBtn.setLayoutX(newGameBtn.getLayoutX());
        exitBtn.setLayoutY(gameSavesBtn.getLayoutY() + 30);
    }
}
