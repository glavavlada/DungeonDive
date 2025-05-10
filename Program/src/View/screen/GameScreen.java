package View.screen;

import Controller.Controller;
import View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Class for the GameScreen.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class GameScreen extends Screen {
    /**
     * Calls Screen constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public GameScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    /**
     * showScreen for the GameScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        Pane root = new Pane();
        Scene gameScreen = new Scene(root, 600, 500);

        Button pauseBtn = new Button();
        pauseBtn.setText("Pause Game");

        setButtonSize(pauseBtn);

        root.getChildren().addAll(pauseBtn);

        Controller controller = new Controller();
        pauseBtn.setOnAction(event ->
                controller.pauseGame(new ActionEvent(), theUI));

        PRIMARY_STAGE.setScene(gameScreen);
        PRIMARY_STAGE.show();

        pauseBtn.setLayoutX(600 - pauseBtn.getWidth());
        pauseBtn.setLayoutY(0);
    }
}
