package View.screen;

import View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The screen showing save files.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class SavesScreen extends Screen {
    /**
     * First save file Button.
     */
    private Button saveOne;
    /**
     * Second save file Button.
     */
    private Button saveTwo;
    /**
     * Third save file Button.
     */
    private Button saveThree;

    /**
     * Constructor calling Screen constructor and setting Buttons.
     *
     * @param thePrimaryStage the stage to be assigned to MY_PRIMARY_STAGE.
     */
    public SavesScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
        saveOne = new Button();
        saveTwo = new Button();
        saveThree = new Button();
    }

    /**
     * showScreen for the SavesScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        VBox root = new VBox();
        Scene saves = new Scene(root, 600, 500);

        setButtonSize(saveOne);
        setButtonSize(saveTwo);
        setButtonSize(saveThree);

        root.getChildren().addAll(saveOne, saveTwo, saveThree);

        getStage().setScene(saves);
        getStage().show();

        root.setAlignment(Pos.CENTER);
    }
}
