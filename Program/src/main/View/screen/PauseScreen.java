package main.View.screen;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import main.Controller.Controller;
import main.View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.Optional;

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

        Text title = new Text("Game Paused");
        title.setFont(Font.font("Verdana", 30));

        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button resumeBtn = new Button("Resume Game");
        Button saveBtn = new Button("Save Game");
        Button loadBtn = new Button("Load Game");
        Button quitToMenuBtn = new Button("Quit to Menu");

        setButtonSize(resumeBtn);
        setButtonSize(saveBtn);
        setButtonSize(loadBtn);
        setButtonSize(quitToMenuBtn);

        resumeBtn.setOnAction(event -> getController().resumeCurrentGame(theUI));

        saveBtn.setOnAction(event -> {
            showSaveDialog(theUI);
        });

        loadBtn.setOnAction(event -> {
            theUI.showSavesScreen();
        });

        quitToMenuBtn.setOnAction(event -> getController().quitToMenu(theUI));

        buttons.getChildren().addAll(resumeBtn, saveBtn, loadBtn, quitToMenuBtn);

        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);
        BorderPane.setMargin(title, new javafx.geometry.Insets(50,0,0,0));

        root.setCenter(buttons);

        getStage().setScene(pauseScene);
        getStage().setTitle("Dungeon Dive - Paused");
        getStage().show();
    }

    /**
     * Shows save dialog with custom naming
     * Prompts the user to enter custom save name and handles overwrite confirmation
     * if save exists
     *
     * @param theUI GameUI instance for screen transitions and UI updates
     */
    private void showSaveDialog(GameUI theUI) {
        TextInputDialog dialog = new TextInputDialog("My Save");
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your save:");
        dialog.setContentText("Save name:");
        dialog.initOwner(getStage());

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String saveName = result.get().trim();

            //check if name already exists
            if (saveNameExists(saveName)) {
                //show confirmation for overwrite
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Overwrite Save");
                alert.setHeaderText("Save name already exists");
                alert.setContentText("A save with the name '" + saveName + "' already exists. Overwrite it?");
                alert.initOwner(getStage());

                Optional<ButtonType> confirmResult = alert.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    getController().getGameController().saveGameWithName(saveName);
                }
            } else {
                getController().getGameController().saveGameWithName(saveName);
            }
        } else {
            System.out.println("Save cancelled or no name provided");
        }
    }

    /**
     *checks if a save name already exists
     */
    private boolean saveNameExists(String saveName) {
        try {
            ResultSet rs = getController().getGameModel().getDatabase().loadGameData(saveName);
            return rs != null && rs.next();
        } catch (Exception e) {
            System.err.println("Error checking if save exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Formats the save date for display
     */
    private String formatSaveDate(String saveDate) {
        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(saveDate);
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("MMM dd - HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return saveDate;
        }
    }

    /**
     * Shows a load dialog similar to SavesScreen but for pause menu
     */
    private void showLoadDialog(GameUI theUI) {
        Stage loadStage = new Stage();
        loadStage.setTitle("Load Game");
        loadStage.initOwner(getStage());

        VBox loadRoot = new VBox(15);
        loadRoot.setAlignment(Pos.CENTER);
        loadRoot.setStyle("-fx-padding: 20;");

        Label loadTitle = new Label("Load Game");
        loadTitle.setFont(Font.font("Verdana", 18));
        loadRoot.getChildren().add(loadTitle);

        // Create a scrollable area for save files
        VBox savesList = new VBox(10);
        savesList.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(savesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background-color: transparent;");

        //get actual save games from database
        try {
            ResultSet saves = getController().getGameModel().getDatabase().getAllSaveGames();
            boolean hasSaves = false;

            while (saves != null && saves.next()) {
                hasSaves = true;
                String saveName = saves.getString("save_name");
                String saveDate = saves.getString("save_date");

                //format the date
                String formattedDate = formatSaveDate(saveDate);

                //create a horizontal box for each save (load button and delete button)
                HBox saveRow = new HBox(10);
                saveRow.setAlignment(Pos.CENTER);

                Button loadButton = new Button(saveName + "\n" + formattedDate);
                loadButton.setPrefWidth(250);
                loadButton.setPrefHeight(60);
                loadButton.setStyle("-fx-font-size: 12px; -fx-alignment: center; -fx-background-color: #4caf50; -fx-text-fill: white;");

                Button deleteButton = new Button("Delete");
                deleteButton.setPrefWidth(80);
                deleteButton.setPrefHeight(60);
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");

                loadButton.setOnAction(event -> {
                    if (loadGameFromDatabase(saveName, theUI)) {
                        loadStage.close(); // Close load dialog
                        theUI.showGameScreen(); // Show game screen
                    } else {
                        System.err.println("Failed to load game: " + saveName);
                    }
                });

                deleteButton.setOnAction(event -> {
                    //confirm deletion
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Save");
                    alert.setHeaderText("Delete save file?");
                    alert.setContentText("Are you sure you want to delete '" + saveName + "'?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        if (getController().getGameModel().getDatabase().deleteSaveGame(saveName)) {
                            System.out.println("Save deleted: " + saveName);
                            //refresh the dialog
                            loadStage.close();
                            showLoadDialog(theUI);
                        } else {
                            System.err.println("Failed to delete save: " + saveName);
                        }
                    }
                });

                saveRow.getChildren().addAll(loadButton, deleteButton);
                savesList.getChildren().add(saveRow);
            }

            if (!hasSaves) {
                Label noSavesLabel = new Label("No saved games found");
                noSavesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                savesList.getChildren().add(noSavesLabel);
            }

        } catch (Exception e) {
            System.err.println("Error loading save games: " + e.getMessage());
            Label errorLabel = new Label("Error loading save games");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            savesList.getChildren().add(errorLabel);
        }

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(150);
        cancelButton.setOnAction(event -> loadStage.close());

        loadRoot.getChildren().addAll(loadTitle, scrollPane, cancelButton);

        Scene loadScene = new Scene(loadRoot, 400, 450);
        loadStage.setScene(loadScene);
        loadStage.show();
    }

    /**
     * Loads a game directly from the database and initializes the game state
     */
    private boolean loadGameFromDatabase(String saveName, GameUI theUI) {
        try {
            ResultSet rs = getController().getGameModel().getDatabase().loadGameData(saveName);

            if (rs != null && rs.next()) {
                String playerData = rs.getString("player_data");
                String dungeonData = rs.getString("dungeon_data");
                String gameStateData = rs.getString("game_state");

                //initialize game controllers first with GameUI
                initializeGameControllersForLoad(theUI);

                //now load game data using GameController
                boolean loaded = getController().getGameController().loadGameFromSaveData(
                        playerData, dungeonData, gameStateData);

                if (loaded) {
                    System.out.println("Game loaded successfully from PauseScreen");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading game from PauseScreen: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Initialize game controllers for a loaded game
     */
    private void initializeGameControllersForLoad(GameUI theUI) {
        //pass GameUI to Controller's method
        getController().initializeGameControllersForLoadedGame(theUI);
    }


}
