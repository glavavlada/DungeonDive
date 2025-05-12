package View.screen;

import View.GameUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
        VBox buttons = new VBox(10);
        HBox message = new HBox();
        Scene scene = new Scene(root, 600, 500);

        Text text = new Text("You Won!");
        Button newGameBtn = new Button("New Game");
        Button savedGamesBtn = new Button("Saved Games");
        Button quitToMenuBtn = new Button("Quit to Menu");
        setButtonSize(newGameBtn);
        setButtonSize(savedGamesBtn);
        setButtonSize(quitToMenuBtn);


//        BorderPane.setAlignment(text, Pos.TOP_CENTER);
//        root.setTop(text);
//        BorderPane.setAlignment(buttons, Pos.BOTTOM_CENTER);
//        root.setCenter(buttons);

        text.setFont(Font.font( "Impact", 70));
        text.setFill(Color.GOLD);

        root.setTop(message);
        root.setCenter(buttons);

//        message.getChildren().add(text);
//        buttons.getChildren().addAll(newGameBtn, savedGamesBtn, quitToMenuBtn);
        root.getChildren().addAll(message, buttons);

        getStage().setScene(scene);
        getStage().show();
    }

}
