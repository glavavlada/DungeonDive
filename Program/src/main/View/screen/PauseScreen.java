package main.View.screen;

import main.Controller.Controller;
import main.View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class for the PauseScreen.
 * Shown when the player pauses the game.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class PauseScreen extends Screen {

    /**
     * Constructor for PauseScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public PauseScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * showScreen for the PauseScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");
        Scene pauseScene = new Scene(root, 600, 500);
        // Apply a stylesheet (option in the future)
        // pauseScene.getStylesheets().add(getClass().getResource("/main/View/css/style.css").toExternalForm());

        Text title = new Text("Game Paused");
        title.setFont(Font.font("Verdana", 30));

        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button resumeBtn = new Button("Resume Game");
        Button saveGameBtn = new Button("Save Game"); // Placeholder
        Button quitToMenuBtn = new Button("Quit to Menu");

        setButtonSize(resumeBtn);
        setButtonSize(saveGameBtn);
        setButtonSize(quitToMenuBtn);

        resumeBtn.setOnAction(event -> getController().resumeCurrentGame(theUI));
        saveGameBtn.setOnAction(event -> {
            // TODO: Implement save game functionality
            getController().getGameController().saveGame();
        });
        quitToMenuBtn.setOnAction(event -> getController().quitToMenu(theUI));

        buttons.getChildren().addAll(resumeBtn, saveGameBtn, quitToMenuBtn);

        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);
        BorderPane.setMargin(title, new javafx.geometry.Insets(50,0,0,0));

        root.setCenter(buttons);

        getStage().setScene(pauseScene);
        getStage().setTitle("Dungeon Dive - Paused");
        getStage().show();
    }
}
