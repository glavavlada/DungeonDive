package View.screen;

import View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SavesScreen extends Screen {

    private Button saveOne;
    private Button saveTwo;
    private Button saveThree;

    public SavesScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
        saveOne = new Button();
        saveTwo = new Button();
        saveThree = new Button();
    }

    public void showScreen(GameUI theUI) {
        VBox root = new VBox();
        Scene saves = new Scene(root, 600, 500);

        setButtonSize(saveOne);
        setButtonSize(saveTwo);
        setButtonSize(saveThree);

        root.getChildren().addAll(saveOne, saveTwo, saveThree);

        PRIMARY_STAGE.setScene(saves);
        PRIMARY_STAGE.show();

        root.setAlignment(Pos.CENTER);
    }
}
