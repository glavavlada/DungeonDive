package main.View.screen; // Ensure this is your package structure

import main.Controller.Controller; // Ensure correct import
import main.View.GameUI;           // Ensure correct import
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class for the WinScreen.
 * Displayed when the player successfully completes the game.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class WinScreen extends Screen {

    /**
     * Constructor for WinScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public WinScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * showScreen for the WinScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        Scene winScene = new Scene(root, 600, 500);
        // Apply a stylesheet (option in the future)
        // winScene.getStylesheets().add(getClass().getResource("/main/View/css/style.css").toExternalForm());

        Text title = new Text("Congratulations! You Won!");
        title.setFont(Font.font("Verdana", 30));
        title.setStyle("-fx-fill: green;"); // Make it stand out

        // Use the tripleButtonVBox from Screen superclass
        VBox buttons = tripleButtonVBox(theUI); // Pass theUI for button actions

        // Use the tripleButtonStructure from Screen superclass
        tripleButtonStructure(title, buttons, root);

        getStage().setScene(winScene);
        getStage().setTitle("Dungeon Dive - Victory!");
        getStage().show();
    }
}
