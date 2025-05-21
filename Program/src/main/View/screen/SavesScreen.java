package main.View.screen;

import main.Controller.Controller;
import main.View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class for the SavesScreen.
 * Allows players to load previously saved games.
 * (Currently a placeholder)
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class SavesScreen extends Screen {

    /**
     * Constructor for SavesScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public SavesScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * showScreen for the SavesScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");
        Scene savesScene = new Scene(root, 600, 500);
        // Apply a stylesheet (option in the future)
        // savesScene.getStylesheets().add(getClass().getResource("/main/View/css/style.css").toExternalForm());

        Label titleLabel = new Label("Load Game");
        titleLabel.setFont(javafx.scene.text.Font.font("Verdana", 24));

        Button save1 = new Button("Empty Save");
        Button save2 = new Button("Empty Save");
        Button save3 = new Button("Empty Save");
        setButtonSize(save1);
        setButtonSize(save2);
        setButtonSize(save3);



        Button backButton = new Button("Back to Main Menu");
        setButtonSize(backButton);
        backButton.setOnAction(event -> theUI.showIntroScreen()); // Directly go back

        root.getChildren().addAll(titleLabel, save1, save2, save3, backButton);

        getStage().setScene(savesScene);
        getStage().setTitle("Dungeon Dive - Load Game");
        getStage().show();
    }
}
