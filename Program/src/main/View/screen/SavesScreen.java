package main.View.screen;


import javafx.scene.layout.HBox;
import main.Controller.Controller;
import main.View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import java.sql.ResultSet;
import java.util.Optional;

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

        Label titleLabel = new Label("Load Game");
        titleLabel.setFont(javafx.scene.text.Font.font("Verdana", 24));

        //Create scrollable area for save files
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

                //format the date nicely
                String formattedDate = formatSaveDate(saveDate);

                //create horizontal box for each save (load button with delete button)
                HBox saveRow = new HBox(10);
                saveRow.setAlignment(Pos.CENTER);

                Button saveButton = new Button(saveName + "\n" + formattedDate);
                saveButton.setPrefWidth(350);
                saveButton.setPrefHeight(60);
                saveButton.setStyle("-fx-font-size: 12px; -fx-alignment: center;");

                Button deleteButton = new Button("Delete");
                deleteButton.setPrefWidth(80);
                deleteButton.setPrefHeight(60);
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");

                saveButton.setOnAction(event -> {
                    if (loadGameFromDatabase(saveName, theUI)) {
                        theUI.showGameScreen();
                    } else {
                        System.err.println("Failed to load game: " + saveName);
                    }
                });

                deleteButton.setOnAction(event -> {
                    //confirm deletion
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Save");
                    alert.setHeaderText("Delete save file?");
                    alert.setContentText("Are you sure you want to delete '" + saveName + "'?");

                    Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        if (getController().getGameModel().getDatabase().deleteSaveGame(saveName)) {
                            System.out.println("Save deleted: " + saveName);
                            //refresh screen to update list
                            showScreen(theUI);
                        } else {
                            System.err.println("Failed to delete save: " + saveName);
                        }
                    }
                });

                saveRow.getChildren().addAll(saveButton, deleteButton);
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

        Button backButton = new Button("Back to Main Menu");
        setButtonSize(backButton);
        backButton.setOnAction(event -> theUI.showIntroScreen());

        root.getChildren().addAll(titleLabel, scrollPane, backButton);

        getStage().setScene(savesScene);
        getStage().setTitle("Dungeon Dive - Load Game");
        getStage().show();
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

                //load game data using GameController
                boolean loaded = getController().getGameController().loadGameFromSaveData(
                        playerData, dungeonData, gameStateData);

                if (loaded) {
                    System.out.println("Game loaded successfully from SavesScreen");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading game from SavesScreen: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Format save date string for display purposes
     * Converts ISO date format from database to more readable format
     *
     * @param saveDate ISO formatted date string from database
     * @return formatted date string (MMM dd, yyyy - HH:mm)
     */
    private String formatSaveDate(String saveDate) {
        try {
            //Parse ISO date string and format it nicely
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(saveDate);
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return saveDate; //return original if parsing fails
        }
    }



    /**
     * initialize game controllers for loaded game
     */
    private void initializeGameControllersForLoad(GameUI theUI) {
        //pass the GameUI to the Controller's method
        getController().initializeGameControllersForLoadedGame(theUI);
    }
}
