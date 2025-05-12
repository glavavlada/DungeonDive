package View.screen;

import View.GameUI;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
        BorderPane root = new BorderPane();
        VBox buttons = tripleButtonVBox();
        Scene gameScreen = new Scene(root, 600, 500);

        root.setCenter(buttons);

        Button resumeBtn = (Button) buttons.getChildren().getFirst();
        Button saveGameBtn = (Button) buttons.getChildren().get(1);
        Button quitToMenuBtn = (Button) buttons.getChildren().getLast();

        resumeBtn.setOnAction(event -> getController().resumeCurrentGame(theUI));
        quitToMenuBtn.setOnAction(event -> getController().quitToMenu(theUI));

        getStage().setScene(gameScreen);
        getStage().show();
    }

    /**
     * Creates VBox buttons but with the first two names changed to Resume
     * and Save.
     *
     * @return VBox with updated Button names for the PauseScreen.
     */
    @Override
    public VBox tripleButtonVBox() {
        VBox buttons = super.tripleButtonVBox();
        Button resumeBtn = (Button) buttons.getChildren().getFirst();
        Button saveGameBtn = (Button) buttons.getChildren().get(1);
        resumeBtn.setText("Resume Game");
        saveGameBtn.setText("Save Game");
        return buttons;
    }
}
