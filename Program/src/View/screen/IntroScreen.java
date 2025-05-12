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
        BorderPane root = new BorderPane();
        VBox buttons = tripleButtonVBox();
        Scene intro = new Scene(root, 600, 500);

        Text text = new Text("Dungeon Dive");
        text.setFont(Font.font( "Impact", 70));
        text.setFill(Color.RED);

        tripleButtonStructure(text, buttons, root);

        // Gets buttons from the VBox to set Event handler stuff.
        Button newGameBtn = (Button) buttons.getChildren().getFirst();
        Button gameSavesBtn = (Button) buttons.getChildren().get(1);
        Button exitBtn = (Button) buttons.getChildren().getLast();

        newGameBtn.setOnAction(event ->
                getController().newGame(new ActionEvent(), theUI));
        gameSavesBtn.setOnAction(event ->
                getController().savedGames(new ActionEvent(), theUI));
        exitBtn.setOnAction(event ->
                getController().exitProgram(new ActionEvent()));

        // Sets scene and shows screen.
        getStage().setScene(intro);
        getStage().show();

    }

    /**
     * Calls tripleButtonVBox then changes Final button to fit the intro
     * screen.
     *
     * @return VBox with an exit program button instead of a quit to menu.
     */
    @Override
    public VBox tripleButtonVBox() {
        VBox buttons = super.tripleButtonVBox();
        Button exitBtn = (Button) buttons.getChildren().getLast();
        exitBtn.setText("Exit");
        return buttons;
    }
}
