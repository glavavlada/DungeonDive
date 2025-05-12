package View.screen;

import View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The screen showing save files.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class SavesScreen extends Screen {

    /**
     * Constructor calling Screen constructor and setting Buttons.
     *
     * @param thePrimaryStage the stage to be assigned to MY_PRIMARY_STAGE.
     */
    public SavesScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    /**
     * showScreen for the SavesScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        BorderPane root = new BorderPane();
        VBox buttons = tripleButtonVBox();
        Scene saves = new Scene(root, 600, 500);

        root.setCenter(buttons);

        Button saveOne = (Button) buttons.getChildren().getFirst();
        Button saveTwo = (Button) buttons.getChildren().get(1);
        Button saveThree = (Button) buttons.getChildren().get(2);
        Button quitToMenuBtn = (Button) buttons.getChildren().getLast();

        quitToMenuBtn.setOnAction(event ->
                getController().quitToMenu(theUI));

        getStage().setScene(saves);
        getStage().show();
    }

    /**
     * Changes button names to Empty Save and adds an extra quit to
     * menu button.
     *
     * @return VBox with saves and quit to menu buttons.
     */
    @Override
    public VBox tripleButtonVBox() {
        VBox buttons = super.tripleButtonVBox();
        Button saveOne = (Button) buttons.getChildren().getFirst();
        Button saveTwo = (Button) buttons.getChildren().get(1);
        Button saveThree = (Button) buttons.getChildren().getLast();
        saveOne.setText("Empty Save");
        saveTwo.setText("Empty Save");
        saveThree.setText("Empty Save");

        Button quitToMenuBtn = new Button("Quit To Menu");
        setButtonSize(quitToMenuBtn);
        buttons.getChildren().add(quitToMenuBtn);

        return buttons;
    }


}
