package main.View.screen;
import main.Controller.Controller;
import main.View.GameUI;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class for the LoseScreen.
 * Displayed when the player is defeated.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class LoseScreen extends Screen {

    /**
     * Constructor for LoseScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public LoseScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * showScreen for the LoseScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        Scene loseScene = new Scene(root, 600, 500);
        // Apply a stylesheet (option in the future)
        // loseScene.getStylesheets().add(getClass().getResource("/main/View/css/style.css").toExternalForm());

        Text title = new Text("You Have Been Defeated!");
        title.setFont(Font.font("Verdana", 30));
        title.setStyle("-fx-fill: red;"); // Make it stand out

        // Use the tripleButtonVBox from Screen superclass
        VBox buttons = tripleButtonVBox(theUI); // Pass theUI for button actions

        // Use the tripleButtonStructure from Screen superclass
        tripleButtonStructure(title, buttons, root);

        getStage().setScene(loseScene);
        getStage().setTitle("Dungeon Dive - Game Over");
        getStage().show();
    }
}
