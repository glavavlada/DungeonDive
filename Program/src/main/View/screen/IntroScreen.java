package main.View.screen;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import main.Controller.Controller;
import main.View.GameUI;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class for the IntroScreen.
 * Displays the main menu options like New Game, Resume Game, Exit.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class IntroScreen extends Screen {

    /**
     * Constructor for IntroScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public IntroScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController); // Pass both to the superclass (Screen)
    }

    /**
     * showScreen for the IntroScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        Scene introScene = new Scene(root, 600, 500);
        // Apply a stylesheet (option in the future)
        // introScene.getStylesheets().add(getClass().getResource("/main/View/css/style.css").toExternalForm());


        Text title = new Text("Dungeon Dive");
        title.setFont(Font.font( "Impact", 70));
        title.setFill(Color.RED);

        VBox buttons = new VBox(15); // Increased spacing
        buttons.setAlignment(Pos.CENTER);

        Button newGameBtn = new Button("New Game");
        Button resumeGameBtn = new Button("Resume Game"); // Was "Saved Games" in tripleButtonVBox
        Button exitBtn = new Button("Exit Program");

        // Use the setButtonSize from the Screen superclass
        setButtonSize(newGameBtn);
        setButtonSize(resumeGameBtn);
        setButtonSize(exitBtn);

        // Set actions using the controller from the Screen superclass
        newGameBtn.setOnAction(event -> getController().newGame(event, theUI));
        resumeGameBtn.setOnAction(event -> getController().savedGames(event, theUI)); // Assuming this leads to saves screen
        exitBtn.setOnAction(event -> getController().exitProgram(event));


        buttons.getChildren().addAll(newGameBtn, resumeGameBtn, exitBtn);

        // Use the tripleButtonStructure from Screen superclass if it fits, or layout manually
        // tripleButtonStructure(title, buttons, root);
        BorderPane.setAlignment(title, Pos.CENTER); // Center title if not using top
        root.setTop(title); // Or root.setCenter(new VBox(title, buttons)) for different layout
        BorderPane.setMargin(title, new javafx.geometry.Insets(50,0,0,0)); // Add top margin to title

        root.setCenter(buttons);

        getStage().setScene(introScene);
        getStage().setTitle("Dungeon Dive - Main Menu");
        getStage().show();
    }
}

