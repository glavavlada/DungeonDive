package View.screen;

import View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
/**
 * Class for the WinScreen.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class WinScreen extends Screen {
    /**
     * Calls Screen constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public WinScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    /**
     * showScreen for the WinScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        BorderPane root = new BorderPane();
        VBox buttons = tripleButtonVBox();
        Scene scene = new Scene(root, 600, 500);

        Text text = new Text("You Won!");
        text.setFont(Font.font( "Impact", 70));
        text.setFill(Color.GOLD);

        // Gets buttons from the VBox to set Event handler stuff.
        Button newGameBtn = (Button) buttons.getChildren().getFirst();
        Button gameSavesBtn = (Button) buttons.getChildren().get(1);
        Button quitToMenuBtn = (Button) buttons.getChildren().getLast();

        newGameBtn.setOnAction(event ->
                getController().newGame(new ActionEvent(), theUI));
        gameSavesBtn.setOnAction(event ->
                getController().savedGames(new ActionEvent(), theUI));
        quitToMenuBtn.setOnAction(event ->
                getController().quitToMenu(theUI));

        tripleButtonStructure(text, buttons, root);

        getStage().setScene(scene);
        getStage().show();
    }

}
