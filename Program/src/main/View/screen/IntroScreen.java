package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

/**
 * Class for the IntroScreen.
 * Displays the main menu with Dungeon Dive title and menu buttons.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 */
public class IntroScreen extends Screen {

    private static final int TITLE_FONT_SIZE = 72;
    private static final int BUTTON_FONT_SIZE = 28;
    private static final int CONTENT_SPACING = 80;

    // Default dimensions if no scene exists yet
    private static final double DEFAULT_WIDTH = 800.0;
    private static final double DEFAULT_HEIGHT = 600.0;

    public IntroScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(final GameUI theUI) {
        // Use a StackPane to hold the content and apply scaling
        StackPane scalableRoot = new StackPane();

        // The actual content pane
        VBox contentPane = new VBox(CONTENT_SPACING);
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setPadding(new Insets(50));

        setupBackground(contentPane);
        VBox titleBox = createTitle();
        VBox buttonBox = createButtons(theUI);

        contentPane.getChildren().addAll(titleBox, buttonBox);
        scalableRoot.getChildren().add(contentPane);

        // Get current scene dimensions, or use defaults if no scene exists
        double currentWidth = DEFAULT_WIDTH;
        double currentHeight = DEFAULT_HEIGHT;

        if (getStage().getScene() != null) {
            currentWidth = getStage().getScene().getWidth();
            currentHeight = getStage().getScene().getHeight();
        }

        Scene introScene = new Scene(scalableRoot, currentWidth, currentHeight);
        applyScaling(scalableRoot, introScene);

        getStage().setScene(introScene);
        getStage().setTitle("Dungeon Dive - Main Menu");
        getStage().show();
    }

    private void setupBackground(VBox root) {
        try {
            root.setStyle("-fx-background-image: url('/sprites/backgrounds/brick_wall_background.png'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center;");
        } catch (Exception e) {
            // Fallback to solid color if background image fails to load
            System.err.println("Could not load background image, using fallback color");
            root.setStyle("-fx-background-color: #2a2a2a;");
        }
    }

    private VBox createTitle() {
        Font titleFont = loadFont("/main/View/fonts/PixelFont.ttf", TITLE_FONT_SIZE, "Impact");
        String shadowStyle = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);

        Text dungeonText = new Text("DUNGEON");
        dungeonText.setFont(titleFont);
        dungeonText.setFill(Color.ORANGE);
        dungeonText.setStyle(shadowStyle);

        Text diveText = new Text("DIVE");
        diveText.setFont(titleFont);
        diveText.setFill(Color.ORANGE);
        diveText.setStyle(shadowStyle);

        titleBox.getChildren().addAll(dungeonText, diveText);
        return titleBox;
    }

    private VBox createButtons(GameUI theUI) {
        Font buttonFont = loadFont("/main/View/fonts/PixelFont.ttf", BUTTON_FONT_SIZE, "Courier New");

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button newGameBtn = createStyledButton("NEW GAME", buttonFont);
        Button loadGameBtn = createStyledButton("LOAD GAME", buttonFont);
        Button quitBtn = createStyledButton("QUIT", buttonFont);

        newGameBtn.setOnAction(event -> getController().newGame(event, theUI));
        loadGameBtn.setOnAction(event -> getController().savedGames(event, theUI));
        quitBtn.setOnAction(event -> getController().exitProgram(event));

        buttonBox.getChildren().addAll(newGameBtn, loadGameBtn, quitBtn);
        return buttonBox;
    }
}