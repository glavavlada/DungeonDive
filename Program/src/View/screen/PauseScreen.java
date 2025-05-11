package View.screen;

import View.GameUI;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Class for the PauseScreen.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class PauseScreen extends Screen {
    /**
     * Calls Screen constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public PauseScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    /**
     * showScreen for the PauseScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        Pane root = new Pane();
        Scene gameScreen = new Scene(root, 600, 500);

        Button resumeBtn = new Button();
        resumeBtn.setText("Resume Game");
        Button quitToMenuBtn = new Button();
        quitToMenuBtn.setText("Quit to Menu");

        setButtonSize(resumeBtn);
        setButtonSize(quitToMenuBtn);

        root.getChildren().addAll(resumeBtn, quitToMenuBtn);

        resumeBtn.setOnAction(event -> getController().resumeCurrentGame(theUI));
        quitToMenuBtn.setOnAction(event -> getController().quitToMenu(theUI));

        getStage().setScene(gameScreen);
        getStage().show();

        // Stuff for centering things in the Pane. Probably could be made better,
        // but it works for now.
        resumeBtn.setLayoutX((600 - resumeBtn.getWidth()) / 2);
        resumeBtn.setLayoutY((500 - resumeBtn.getHeight()) / 2);
        quitToMenuBtn.setLayoutX((600 - quitToMenuBtn.getWidth()) / 2);
        quitToMenuBtn.setLayoutY((440 - quitToMenuBtn.getHeight()) / 2);
    }
}
