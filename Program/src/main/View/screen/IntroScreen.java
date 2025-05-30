package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

import java.io.InputStream;

/**
 * Class for the IntroScreen.
 * Displays the main menu with Dungeon Dive title and menu buttons.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/29/2025
 */
public class IntroScreen extends Screen {

    // Constants to eliminate magic numbers
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;
    private static final int TITLE_FONT_SIZE = 72;
    private static final int BUTTON_FONT_SIZE = 28;
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 60;
    private static final int TITLE_SPACING = 10;
    private static final int BUTTON_SPACING = 20;
    private static final int CONTENT_SPACING = 80;
    private static final int CONTENT_MARGIN = 50;

    /**
     * Constructor for IntroScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public IntroScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * showScreen for the IntroScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        Scene introScene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        setupBackground(root);
        VBox titleBox = createTitle();
        VBox buttonBox = createButtons(theUI);

        VBox centerContent = new VBox(CONTENT_SPACING);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getChildren().addAll(titleBox, buttonBox);

        root.setCenter(centerContent);
        BorderPane.setMargin(centerContent, new Insets(CONTENT_MARGIN));

        getStage().setScene(introScene);
        getStage().setTitle("Dungeon Dive - Main Menu");
        getStage().show();
    }

    /**
     * Sets up the background image for the screen.
     */
    private void setupBackground(BorderPane root) {
        root.setStyle("-fx-background-image: url('/sprites/backgrounds/brick_wall_background.png'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center;");
    }

    /**
     * Creates the title section with drop shadows.
     */
    private VBox createTitle() {
        Font titleFont = loadFont(TITLE_FONT_SIZE, Font.font("Impact", TITLE_FONT_SIZE));
        String shadowStyle = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

        VBox titleBox = new VBox(TITLE_SPACING);
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

    /**
     * Creates the button section with styling and event handlers.
     */
    private VBox createButtons(GameUI theUI) {
        Font buttonFont = loadFont(BUTTON_FONT_SIZE, Font.font("Courier New", BUTTON_FONT_SIZE));

        VBox buttonBox = new VBox(BUTTON_SPACING);
        buttonBox.setAlignment(Pos.CENTER);

        Button newGameBtn = createStyledButton("NEW GAME", buttonFont);
        Button loadGameBtn = createStyledButton("LOAD GAME", buttonFont);
        Button quitBtn = createStyledButton("QUIT", buttonFont);

        // Set button actions
        newGameBtn.setOnAction(event -> getController().newGame(event, theUI));
        loadGameBtn.setOnAction(event -> getController().savedGames(event, theUI));
        quitBtn.setOnAction(event -> getController().exitProgram(event));

        buttonBox.getChildren().addAll(newGameBtn, loadGameBtn, quitBtn);
        return buttonBox;
    }

    /**
     * Creates a styled button with consistent appearance and hover effects.
     */
    private Button createStyledButton(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);

        String buttonStyle = "-fx-background-color: #2C2C2C; " +
                "-fx-text-fill: #E0E0E0; " +
                "-fx-border-color: #8B4513; " +
                "-fx-border-width: 4px; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 0; " +
                "-fx-border-radius: 0; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);";

        button.setStyle(buttonStyle);
        button.setMinWidth(BUTTON_WIDTH);
        button.setMinHeight(BUTTON_HEIGHT);

        // Add hover effects
        button.setOnMouseEntered(e ->
                button.setStyle(buttonStyle + "-fx-background-color: #3C3C3C;"));
        button.setOnMouseExited(e ->
                button.setStyle(buttonStyle));

        return button;
    }

    /**
     * Loads a custom font with fallback support.
     */
    private Font loadFont(int size, Font fallback) {
        Font customFont = Font.loadFont(getResourceStream("/main/View/fonts/PixelFont.ttf"), size);
        return customFont != null ? customFont : fallback;
    }

    /**
     * Helper method to get an InputStream for a resource.
     */
    private InputStream getResourceStream(String path) {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Resource not found: " + path);
        }
        return stream;
    }
}