package main.View.screen;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

/**
 * Class for the WinScreen.
 * Displayed when the player successfully completes the game.
 * Now with responsive scaling and enhanced visual design.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 */
public class WinScreen extends Screen {

    private static final int TITLE_FONT_SIZE = 40;
    private static final int BUTTON_FONT_SIZE = 24;
    private static final int CONTENT_SPACING = 60;

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
        // Use a StackPane to hold the content and apply scaling
        StackPane scalableRoot = new StackPane();

        // The actual content pane
        BorderPane contentPane = new BorderPane();
        contentPane.setStyle("-fx-padding: 50;");

        setupBackground(contentPane);

        Text title = createTitle();
        VBox buttons = tripleButtonVBox(theUI);

        // Use the tripleButtonStructure from Screen superclass
        tripleButtonStructure(title, buttons, contentPane);

        scalableRoot.getChildren().add(contentPane);

        Scene winScene = new Scene(scalableRoot, getStage().getScene().getWidth(), getStage().getScene().getHeight());
        applyScaling(scalableRoot, winScene);

        getStage().setScene(winScene);
        getStage().setTitle("Dungeon Dive - Victory!");
        getStage().show();
    }

    private void setupBackground(BorderPane root) {
        // Bright, celebratory background for victory screen
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #003300, #001100); " +
                "-fx-border-color: #228B22; " +
                "-fx-border-width: 3px;");
    }

    private Text createTitle() {
        Font titleFont = loadFont("/main/View/fonts/PixelFont.ttf", TITLE_FONT_SIZE, "Impact");

        Text title = new Text("CONGRATULATIONS! YOU WON!");
        title.setFont(titleFont);
        title.setFill(Color.LIGHTGREEN);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(34,139,34,0.8), 10, 0.5, 4, 4); " +
                "-fx-stroke: #228B22; " +
                "-fx-stroke-width: 2px;");

        return title;
    }
}