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
        Button resumeGameBtn = new Button();
        newGameBtn.setText("New Game");
        exitBtn.setText("Exit");
        resumeGameBtn.setText("Resume Game");

        setButtonSize(newGameBtn);
        setButtonSize(exitBtn);
        setButtonSize(resumeGameBtn);

        // Adds buttons to root.
        root.getChildren().addAll(newGameBtn, resumeGameBtn, exitBtn);

        // Event Handler stuff here.
        // Takes in ActionEvent (click), and passes itself to newGame().
        newGameBtn.setOnAction(event ->
                CONTROLLER.newGame(new ActionEvent(), theUI));
        exitBtn.setOnAction(event ->
                CONTROLLER.exitProgram(new ActionEvent()));
        resumeGameBtn.setOnAction(event ->
                CONTROLLER.resumeGame(new ActionEvent(), theUI));

        // Sets scene and shows screen.
        PRIMARY_STAGE.setScene(intro);
        PRIMARY_STAGE.show();

        // The setLayout is after .show(), because button size is needed for
        // location calculation, and the getWidth and getHeight return 0 until
        // button in shown on the screen.

        // Also my code here could be made way less ugly, but it works and
        // I want to worry about more important stuff for now.
        newGameBtn.setLayoutX((root.getWidth() - newGameBtn.getWidth()) / 2);
        newGameBtn.setLayoutY((root.getHeight() - newGameBtn.getHeight()) / 2);
        resumeGameBtn.setLayoutX(((root.getWidth() - newGameBtn.getWidth()) / 2));
        resumeGameBtn.
                setLayoutY(((root.getHeight() - newGameBtn.getHeight()) / 2) + 30);
        exitBtn.setLayoutX(root.getWidth() - exitBtn.getWidth());
        exitBtn.setLayoutY(0);
    }
}
