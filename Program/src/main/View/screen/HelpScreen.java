package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

import java.io.InputStream;

public class HelpScreen extends Screen {

    // Style constants to match Intro/Hero Selection
    private static final int TITLE_FONT_SIZE = 45;
    private static final int SUBTITLE_FONT_SIZE = 22;
    private static final int CONTENT_FONT_SIZE = 18;
    private static final int BUTTON_FONT_SIZE = 28;

    private static final int CONTENT_SPACING = 30;
    private static final int SECTION_SPACING = 20;

    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

    // Colors
    private static final String PANEL_COLOR = "rgba(42, 42, 42, 0.9)";
    private static final String BORDER_COLOR = "#8B4513";
    private static final String TEXT_COLOR = "#E0E0E0";
    private static final String SUBTITLE_COLOR = "#DAA520";


    public HelpScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(final GameUI theUI) {
        // Use a StackPane to hold the content and apply scaling
        StackPane scalableRoot = new StackPane();

        // The actual content pane, similar to IntroScreen
        VBox contentPane = new VBox(CONTENT_SPACING);
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setPadding(new Insets(50));
        contentPane.setMaxWidth(BASE_WIDTH - 100); // Set a max width for content

        setupBackground(scalableRoot); // Apply background to the root pane

        VBox titleSection = createTitleSection();
        ScrollPane contentArea = createContentArea();
        VBox buttonSection = createButtonSection(theUI);

        contentPane.getChildren().addAll(titleSection, contentArea, buttonSection);
        scalableRoot.getChildren().add(contentPane);

        // Get current scene dimensions, or use defaults
        double currentWidth = getStage().getScene() != null ? getStage().getScene().getWidth() : BASE_WIDTH;
        double currentHeight = getStage().getScene() != null ? getStage().getScene().getHeight() : BASE_HEIGHT;

        Scene helpScene = new Scene(scalableRoot, currentWidth, currentHeight);

        // This call handles the window size adaptability
        applyScaling(scalableRoot, helpScene);

        getStage().setScene(helpScene);
        getStage().setTitle("Help - Dungeon Dive");
        getStage().show();
    }

    private void setupBackground(StackPane root) {
        try (InputStream bgStream = getClass().getResourceAsStream(BACKGROUND_PATH)) {
            if (bgStream != null) {
                // Use the same background as IntroScreen for consistency
                root.setStyle("-fx-background-image: url('" + getClass().getResource(BACKGROUND_PATH).toExternalForm() + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            } else {
                // Fallback to solid color if background image fails
                System.err.println("Could not load background image, using fallback color");
                root.setStyle("-fx-background-color: #2a2a2a;");
            }
        } catch (Exception e) {
            System.err.println("Could not load background image, using fallback color: " + e.getMessage());
            root.setStyle("-fx-background-color: #2a2a2a;");
        }
    }

    private VBox createTitleSection() {
        VBox titleSection = new VBox();
        titleSection.setAlignment(Pos.CENTER);
        titleSection.setPadding(new Insets(0, 0, 20, 0));

        // Use Text object for title like IntroScreen
        Text titleLabel = new Text("GAME INSTRUCTIONS");
        Font titleFont = loadFont(FONT_PATH, TITLE_FONT_SIZE, "Impact");
        titleLabel.setFont(titleFont);
        titleLabel.setFill(Color.ORANGE);
        titleLabel.setStyle(SHADOW_STYLE);

        titleSection.getChildren().add(titleLabel);
        return titleSection;
    }

    private ScrollPane createContentArea() {
        VBox contentContainer = new VBox(SECTION_SPACING);
        contentContainer.setAlignment(Pos.TOP_LEFT);
        contentContainer.setPadding(new Insets(25));
        contentContainer.setStyle("-fx-background-color: " + PANEL_COLOR + "; " +
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 3px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px;");

        // Game objective section
        VBox objectiveSection = createContentSection(
                "GAME OBJECTIVE",
                "Find rooms with pillars and click interact to collect them. After all 4 have been collected, " +
                        "find the exit to win. You can also find chests and items in some rooms. Some rooms have traps " +
                        "and monsters. If your health gets to 0, you will lose the game."
        );

        // Controls sections
        VBox movementSection = createContentSection(
                "DUNGEON MOVEMENT",
                "W/↑ - Move Up\n" +
                        "A/← - Move Left\n" +
                        "D/→ - Move Right\n" +
                        "S/↓ - Move Down"
        );

        VBox combatSection = createContentSection(
                "COMBAT CONTROLS",
                "A - Attack\n" +
                        "S - Special Attack\n" +
                        "R - Run\n" +
                        "I - Inventory\n" +
                        "ESC - Pause Game"
        );

        VBox inventorySection = createContentSection(
                "INVENTORY CONTROLS",
                "W - Scroll Up\n" +
                        "S - Scroll Down\n" +
                        "ENTER - Use Selected Item\n" +
                        "ESC - Close Inventory"
        );

        contentContainer.getChildren().addAll(objectiveSection, movementSection, combatSection, inventorySection);

        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefViewportHeight(300); // Set a base height, scaling will handle the rest

        return scrollPane;
    }

    private VBox createContentSection(String title, String content) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.TOP_LEFT);

        // Section title using PixelFont
        Label titleLabel = new Label(title);
        Font subtitleFont = loadFont(FONT_PATH, SUBTITLE_FONT_SIZE, "Courier New");
        titleLabel.setFont(subtitleFont);
        titleLabel.setTextFill(Color.web(SUBTITLE_COLOR));
        titleLabel.setStyle("-fx-underline: true;");

        // Section content using PixelFont
        Label contentLabel = new Label(content);
        Font contentFont = loadFont(FONT_PATH, CONTENT_FONT_SIZE, "Courier New");
        contentLabel.setFont(contentFont);
        contentLabel.setTextFill(Color.web(TEXT_COLOR));
        contentLabel.setWrapText(true);
        contentLabel.setLineSpacing(5); // Add some line spacing for readability

        section.getChildren().addAll(titleLabel, contentLabel);
        return section;
    }

    private VBox createButtonSection(GameUI theUI) {
        VBox buttonSection = new VBox();
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.setPadding(new Insets(20, 0, 0, 0));

        // Use the createStyledButton method from the Screen superclass
        Font buttonFont = loadFont(FONT_PATH, BUTTON_FONT_SIZE, "Courier New");
        Button resumeGameButton = createStyledButton("RESUME GAME", buttonFont);
        resumeGameButton.setOnAction(event -> getController().resumeCurrentGame(theUI));

        buttonSection.getChildren().add(resumeGameButton);
        return buttonSection;
    }
}