package View.screen;

import View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

/**
 * Class for the LoseScreen.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class LoseScreen extends Screen {
    /**
     * Calls Screen constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public LoseScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    /**
     * showScreen for the LoseScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {
        BorderPane root = new BorderPane();
        VBox buttons = tripleButtonVBox();
        Scene loseScreen = new Scene(root, 600, 500);
        root.setBackground(Background.fill(Color.BLACK));

        Text text = new Text("You Lost!");
        text.setFont(Font.font( "Impact", 70));
        text.setFill(Color.RED);

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

        getStage().setScene(loseScreen);
        getStage().show();
    }

}
