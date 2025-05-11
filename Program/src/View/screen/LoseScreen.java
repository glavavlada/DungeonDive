package View.screen;

import View.GameUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;

import java.awt.*;

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
        VBox root = new VBox(10);
        Scene loseScreen = new Scene(root, 600, 500);
        root.setBackground(Background.fill(Color.BLACK));

        Text text = new Text("You Lost!");
        Button newGameBtn = new Button("New Game");
        Button savedGamesBtn = new Button("Saved Games");
        Button quitToMenuBtn = new Button("Quit to Menu");
        setButtonSize(newGameBtn);
        setButtonSize(savedGamesBtn);
        setButtonSize(quitToMenuBtn);

        text.setFont(Font.font( "Impact", 70));
        text.setFill(Color.RED);
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(text, newGameBtn, savedGamesBtn, quitToMenuBtn);
        VBox.setMargin(newGameBtn, new Insets(200, 0, 0, 0));

        getStage().setScene(loseScreen);
        getStage().show();

    }

}
