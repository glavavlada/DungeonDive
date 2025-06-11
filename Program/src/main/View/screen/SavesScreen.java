package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

import java.io.InputStream;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Class for the SavesScreen.
 * Allows players to load previously saved games.
 * Now properly using parent Screen methods for consistency.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 */
public class SavesScreen extends Screen {

    // Font sizes
    private static final int BASE_TITLE_FONT_SIZE = 45;
    private static final int BASE_BUTTON_FONT_SIZE = 22;
    private static final int BASE_SAVE_BUTTON_FONT_SIZE = 14;
    private static final int BASE_DELETE_BUTTON_FONT_SIZE = 16;
    private static final int BASE_MESSAGE_FONT_SIZE = 18;

    // Layout dimensions
    private static final int BASE_CONTENT_SPACING = 20;
    private static final int BASE_TOP_PADDING = 60;
    private static final int BASE_SAVE_LIST_SPACING = 15;
    private static final int BASE_SAVE_ROW_SPACING = 15;

    // Button dimensions
    private static final int BASE_BACK_BUTTON_WIDTH = 420;
    private static final int BASE_BACK_BUTTON_HEIGHT = 45;
    private static final int BASE_SAVE_BUTTON_WIDTH = 450;
    private static final int BASE_SAVE_BUTTON_HEIGHT = 80;
    private static final int BASE_DELETE_BUTTON_WIDTH = 160;
    private static final int BASE_DELETE_BUTTON_HEIGHT = 80;

    // Scroll area dimensions
    private static final int BASE_SCROLL_AREA_HEIGHT = 300;

    // Paths and styling constants
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

    // Delete button style (unique to this screen)
    private static final String DELETE_BUTTON_STYLE =
            "-fx-background-color: #8B0000; -fx-text-fill: #E0E0E0; -fx-border-color: #8B4513; " +
                    "-fx-border-width: 4px; -fx-padding: 10px 15px; -fx-background-radius: 0; -fx-border-radius: 0; " +
                    "-fx-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);";

    private static final Color TAN_COLOR = Color.rgb(222, 184, 135);

    private Scene scene;

    public SavesScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(final GameUI theUI) {
        VBox contentPane = createRootLayout();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(contentPane);
        Stage currentStage = getStage();

        double width = currentStage.getScene() != null ?
                currentStage.getScene().getWidth() : BASE_WIDTH;
        double height = currentStage.getScene() != null ?
                currentStage.getScene().getHeight() : BASE_HEIGHT;

        scene = new Scene(root, width, height);

        setupBackground(root);
        setupComponents(contentPane, theUI);

        // Use parent's scaling method
        applyScaling(contentPane, scene);

        boolean wasFullScreen = currentStage.isFullScreen();

        currentStage.setScene(scene);
        currentStage.setTitle("Dungeon Dive - Load Game");

        if (wasFullScreen && !currentStage.isFullScreen()) {
            currentStage.setFullScreen(true);
        }

        if (!currentStage.isShowing()) {
            currentStage.show();
        }
    }

    private VBox createRootLayout() {
        VBox root = new VBox(BASE_CONTENT_SPACING);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(BASE_TOP_PADDING, 20, 20, 20));
        root.setMaxWidth(BASE_WIDTH * 1.2);
        root.setMaxHeight(BASE_HEIGHT * 1.2);
        root.setMinHeight(BASE_HEIGHT * 0.8);
        return root;
    }

    private void setupBackground(javafx.scene.layout.Pane root) {
        try (InputStream bgStream = getResourceStream(BACKGROUND_PATH)) {
            if (bgStream != null) {
                root.setStyle(buildBackgroundStyle());
            } else {
                root.setStyle("-fx-background-color: #202020;");
                logError("Background image not found: " + BACKGROUND_PATH + ". Using fallback color.");
            }
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #202020;");
            logError("Error loading background: " + e.getMessage());
        }
    }

    private String buildBackgroundStyle() {
        return "-fx-background-image: url('" + BACKGROUND_PATH + "'); " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;";
    }

    private void setupComponents(VBox root, GameUI theUI) {
        FontBundle fonts = loadFonts();

        Text titleText = createTitle(fonts.title);
        VBox titleContainer = new VBox();
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setPadding(new Insets(0, 0, 20, 0));
        titleContainer.getChildren().add(titleText);

        ScrollPane savesList = createSavesListArea(fonts, theUI);
        Button backButton = createBackButton(fonts.button, theUI);

        root.getChildren().addAll(titleContainer, savesList, backButton);
    }

    private static class FontBundle {
        final Font title, button, saveButton, deleteButton, message;

        FontBundle(Font title, Font button, Font saveButton, Font deleteButton, Font message) {
            this.title = title;
            this.button = button;
            this.saveButton = saveButton;
            this.deleteButton = deleteButton;
            this.message = message;
        }
    }

    private FontBundle loadFonts() {
        return new FontBundle(
                // Use parent's loadFont method
                loadFont(FONT_PATH, BASE_TITLE_FONT_SIZE, "Impact"),
                loadFont(FONT_PATH, BASE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(FONT_PATH, BASE_SAVE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(FONT_PATH, BASE_DELETE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(FONT_PATH, BASE_MESSAGE_FONT_SIZE, "Arial")
        );
    }

    private Text createTitle(Font titleFont) {
        Text titleText = new Text("LOAD GAME");
        titleText.setFont(titleFont);
        titleText.setFill(Color.ORANGE);
        titleText.setStyle(SHADOW_STYLE);
        titleText.setVisible(true);
        titleText.setManaged(true);
        return titleText;
    }

    private ScrollPane createSavesListArea(FontBundle fonts, GameUI theUI) {
        VBox savesList = new VBox(BASE_SAVE_LIST_SPACING);
        savesList.setAlignment(Pos.TOP_CENTER);
        savesList.setPadding(new Insets(20));

        populateSavesList(savesList, fonts, theUI);

        ScrollPane scrollPane = new ScrollPane(savesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setPrefViewportHeight(BASE_SCROLL_AREA_HEIGHT);
        scrollPane.setMinHeight(150);
        scrollPane.setMaxHeight(BASE_SCROLL_AREA_HEIGHT * 1.5);

        scrollPane.setStyle(
                "-fx-background: rgba(42, 42, 42, 0.9); " +
                        "-fx-background-color: rgba(42, 42, 42, 0.9); " +
                        "-fx-border-color: #8B4513; " +
                        "-fx-border-width: 4px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 8, 0.3, 3, 3);"
        );

        return scrollPane;
    }

    private void populateSavesList(VBox savesList, FontBundle fonts, GameUI theUI) {
        try (ResultSet saves = getController().getGameModel().getDatabase().getAllSaveGames()) {
            boolean hasSaves = false;
            if (saves != null) {
                while (saves.next()) {
                    hasSaves = true;
                    String saveName = saves.getString("save_name");
                    String saveDate = saves.getString("save_date");
                    HBox saveRow = createSaveRow(saveName, formatSaveDate(saveDate), fonts, theUI);
                    savesList.getChildren().add(saveRow);
                }
            }
            if (!hasSaves) {
                savesList.getChildren().add(createNoSavesLabel(fonts.message));
            }
        } catch (Exception e) {
            System.err.println("Error loading save games: " + e.getMessage());
            e.printStackTrace();
            savesList.getChildren().add(createErrorLabel(fonts.message));
        }
    }

    private HBox createSaveRow(String saveName, String formattedDate, FontBundle fonts, GameUI theUI) {
        HBox saveRow = new HBox(BASE_SAVE_ROW_SPACING);
        saveRow.setAlignment(Pos.CENTER);

        Button saveButton = createSaveButton(saveName, formattedDate, fonts.saveButton, theUI);
        Button deleteButton = createDeleteButton(saveName, fonts.deleteButton, theUI);

        saveRow.getChildren().addAll(saveButton, deleteButton);
        return saveRow;
    }

    private Button createSaveButton(String saveName, String formattedDate, Font buttonFont, GameUI theUI) {
        // Use parent's createStyledButton as base, then customize
        Button saveButton = createStyledButton(saveName + "\n" + formattedDate, buttonFont);

        // Override dimensions for save buttons
        saveButton.setPrefWidth(BASE_SAVE_BUTTON_WIDTH);
        saveButton.setPrefHeight(BASE_SAVE_BUTTON_HEIGHT);
        saveButton.setMinWidth(BASE_SAVE_BUTTON_WIDTH);
        saveButton.setMinHeight(BASE_SAVE_BUTTON_HEIGHT);
        saveButton.setWrapText(true);

        // Customize styling while keeping parent's base style
        String customStyle =
                "-fx-background-color: #2C2C2C; " +
                        "-fx-text-fill: #E0E0E0; " +
                        "-fx-border-color: #DAA520; " +
                        "-fx-border-width: 4px; " +
                        "-fx-padding: 10px 15px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-alignment: center; " +
                        "-fx-text-alignment: center; " +
                        "-fx-content-display: center; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);";

        saveButton.setStyle(customStyle);

        // Add hover effects
        saveButton.setOnMouseEntered(e ->
                saveButton.setStyle(customStyle + "-fx-background-color: #3C3C3C;"));
        saveButton.setOnMouseExited(e ->
                saveButton.setStyle(customStyle));

        saveButton.setOnAction(event -> {
            if (loadGameFromDatabase(saveName, theUI)) {
                theUI.showGameScreen();
            } else {
                showErrorAlert("Load Failed", "Failed to load game: " + saveName);
            }
        });

        return saveButton;
    }

    private Button createDeleteButton(String saveName, Font buttonFont, GameUI theUI) {
        Button deleteButton = new Button("DELETE");
        deleteButton.setFont(buttonFont);
        deleteButton.setPrefWidth(BASE_DELETE_BUTTON_WIDTH);
        deleteButton.setPrefHeight(BASE_DELETE_BUTTON_HEIGHT);
        deleteButton.setMinWidth(BASE_DELETE_BUTTON_WIDTH);
        deleteButton.setMinHeight(BASE_DELETE_BUTTON_HEIGHT);

        deleteButton.setStyle(DELETE_BUTTON_STYLE);

        // Add hover effects
        deleteButton.setOnMouseEntered(e ->
                deleteButton.setStyle(DELETE_BUTTON_STYLE + "-fx-background-color: #A52A2A;"));
        deleteButton.setOnMouseExited(e ->
                deleteButton.setStyle(DELETE_BUTTON_STYLE));

        deleteButton.setOnAction(event -> handleDeleteSave(saveName, theUI));
        return deleteButton;
    }

    private Label createNoSavesLabel(Font messageFont) {
        Label noSavesLabel = new Label("No saved games found.");
        noSavesLabel.setFont(messageFont);
        noSavesLabel.setTextFill(TAN_COLOR);
        noSavesLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 3, 0, 1, 1);");
        return noSavesLabel;
    }

    private Label createErrorLabel(Font messageFont) {
        Label errorLabel = new Label("Error: Could not load save games from database.");
        errorLabel.setFont(messageFont);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 3, 0, 1, 1);");
        return errorLabel;
    }

    private Button createBackButton(Font buttonFont, GameUI theUI) {
        // Use parent's createStyledButton method
        Button backButton = createStyledButton("BACK TO MENU", buttonFont);

        // Override dimensions for this specific button
        backButton.setPrefWidth(BASE_BACK_BUTTON_WIDTH);
        backButton.setPrefHeight(BASE_BACK_BUTTON_HEIGHT);

        backButton.setOnAction(event -> theUI.showIntroScreen());
        return backButton;
    }

    private void handleDeleteSave(String saveName, GameUI theUI) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Save");
        alert.setHeaderText("Are you sure you want to delete this save?");
        alert.setContentText("This action cannot be undone.\nSave: '" + saveName + "'");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (getController().getGameModel().getDatabase().deleteSaveGame(saveName)) {
                showScreen(theUI); // Refresh the screen
            } else {
                showErrorAlert("Delete Failed", "Could not delete the save file from the database.");
            }
        }
    }

    private boolean loadGameFromDatabase(String saveName, GameUI theUI) {
        try (ResultSet rs = getController().getGameModel().getDatabase().loadGameData(saveName)) {
            if (rs != null && rs.next()) {
                String playerData = rs.getString("player_data");
                String dungeonData = rs.getString("dungeon_data");
                String gameStateData = rs.getString("game_state");
                getController().initializeGameControllersForLoadedGame(theUI);
                return getController().getGameController().loadGameFromSaveData(playerData, dungeonData, gameStateData);
            }
        } catch (Exception e) {
            System.err.println("Error loading game from SavesScreen: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private String formatSaveDate(String saveDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(saveDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return saveDate;
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    private void logError(String message) {
        System.err.println(message);
    }
}