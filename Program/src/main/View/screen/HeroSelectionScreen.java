package main.View.screen;

import javafx.scene.text.TextAlignment;
import main.Controller.Controller;
import main.View.GameUI;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class for the HeroSelectionScreen.
 * Allows the player to choose a hero type and enter a name.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class HeroSelectionScreen extends Screen {

    /**
     * Calls Screen constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     * @param theController The main controller instance.
     */
    public HeroSelectionScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * showScreen for the HeroSelectionScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        VBox root = new VBox(10); // Increased spacing a bit
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;"); // Add some padding

        Scene heroSelectionScene = new Scene(root, 600, 500);
        // Apply a stylesheet (option in the future)
        // heroSelectionScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());


        // Hero Name Input
        Label nameLabel = new Label("Enter Your Hero's Name:");
        TextField nameBox = new TextField();
        nameBox.setPromptText("Hero Name");
        nameBox.setMaxWidth(200);

        // Hero Type Selection
        Label heroSelectionLabel = new Label("Select Your Hero:");
        ToggleGroup heroToggleGroup = new ToggleGroup();

        RadioButton warriorButton = new RadioButton("Warrior");
        warriorButton.setToggleGroup(heroToggleGroup);
        warriorButton.setUserData(main.Model.util.HeroType.WARRIOR); // Store HeroType enum directly

        RadioButton priestessButton = new RadioButton("Priestess");
        priestessButton.setToggleGroup(heroToggleGroup);
        priestessButton.setUserData(main.Model.util.HeroType.PRIESTESS);

        RadioButton thiefButton = new RadioButton("Thief"); // Corrected spelling
        thiefButton.setToggleGroup(heroToggleGroup);
        thiefButton.setUserData(main.Model.util.HeroType.THIEF);

        // Description Label
        Label descriptionLabel = new Label("Select a hero to see their description.");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(300);
        descriptionLabel.setMinHeight(60); // Ensure space for description
        descriptionLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setTextAlignment(TextAlignment.CENTER); // Alternative for alignment


        // Event Handlers for RadioButtons to update description
        warriorButton.setOnAction(event -> {
            if (getController() != null) {
                // Pass the HeroType directly if heroDescription is updated to accept it
                // getController().heroDescription(main.Model.util.HeroType.WARRIOR, descriptionLabel);
                getController().heroDescription(1, descriptionLabel); // Using int for now as per current Controller
            }
        });
        priestessButton.setOnAction(event -> {
            if (getController() != null) {
                getController().heroDescription(2, descriptionLabel);
            }
        });
        thiefButton.setOnAction(event -> {
            if (getController() != null) {
                getController().heroDescription(3, descriptionLabel);
            }
        });

        // Event Handler for NameBox (e.g., when Enter is pressed or focus is lost)
        // This updates the name in the Controller as the user types or confirms.
        nameBox.setOnAction(event -> { // When enter is pressed in the TextField
            if (getController() != null) {
                getController().setHeroName(nameBox.getText());
            }
        });
        // It's also good to update on focus lost, or have the Start Game button read it directly.
        nameBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && getController() != null) { // Focus lost
                getController().setHeroName(nameBox.getText());
            }
        });


        // Start Game Button
        Button startGameBtn = new Button("Start Game");
        setButtonSize(startGameBtn); // Your custom styling method
        startGameBtn.setOnAction(event -> {
            if (getController() != null) {
                // Ensure the latest name is passed, especially if not using onAction for TextField
                getController().setHeroName(nameBox.getText());
                getController().startGame(event, theUI, heroToggleGroup);
            }
        });

        // Layout
        VBox radioBox = new VBox(10, warriorButton, priestessButton, thiefButton);
        radioBox.setAlignment(Pos.CENTER_LEFT); // Align radio buttons to the left
        radioBox.setMaxWidth(150); // Give radio buttons a max width

        root.getChildren().addAll(nameLabel, nameBox, heroSelectionLabel, radioBox, descriptionLabel, startGameBtn);

        getStage().setScene(heroSelectionScene);
        getStage().setTitle("Select Your Hero - Dungeon Dive");
        getStage().show();
    }
}
