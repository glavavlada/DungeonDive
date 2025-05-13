package View.screen;

import View.GameUI;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Class for the GameScreen.
 *
 * @author Jacob Hilliker
 * @version 5/10/2025
 */
public class GameScreen extends Screen {
    private Canvas gameCanvas;
    private GraphicsContext gc;

    /**
     * Calls Screen constructor.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public GameScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    /**
     * showScreen for the GameScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff.
     */
    @Override
    public void showScreen(GameUI theUI) {

        BorderPane root = new BorderPane();
        Pane playerOptions = playerOptionsPane();
        Scene gameScene = new Scene(root, 800, 600);


        //create game canvas for rendering dungeon
        gameCanvas = new Canvas(600, 400);
        gc = gameCanvas.getGraphicsContext2D();

        //draw initial game state
        drawGame();

        //create UI elements
        Button pauseBtn = new Button("Pause Game");
        Label statusLabel = new Label("Explore the dungeon and find the pillars!");


        //create top panel with pause button
        HBox topPanel = new HBox(10);
        topPanel.getChildren().addAll(statusLabel, pauseBtn);

        //create side panel for inventory and stats
        VBox sidePanel = createSidePanel();


        //set up layout
        root.setTop(topPanel);
        root.setLeft(gameCanvas);
        // Player options centered for now because it was the only spot the
        // custom placements did not get crammed and messed up.
        root.setCenter(playerOptions);
        root.setRight(sidePanel);

        //use controller from GameUI instead of creating a new one
        pauseBtn.setOnAction(event ->
                theUI.getController().pauseGame(new ActionEvent(), theUI));

        getStage().setScene(gameScene);
        getStage().show();

        buttonPanePlacements(playerOptions);

        //it allows the GameUI to set up key event handlers
        theUI.setGameScene(gameScene);


    }

    /**
     * Pane with player options like movement and potions. Pane because finding
     * something for specific placements of these items was hard.
     *
     * @return a Pane with player options.
     */
    private Pane playerOptionsPane() {
        Pane playerOptions = new Pane();

        Button up = new Button("\u2191");
        Button down = new Button("\u2193");
        Button left = new Button("\u2190");
        Button right = new Button("\u2192");

        setMoveButtons(up);
        setMoveButtons(down);
        setMoveButtons(left);
        setMoveButtons(right);

        playerOptions.setMaxWidth(55);
        playerOptions.setMaxHeight(55);

        playerOptions.getChildren().addAll(up, down, left, right);

        return playerOptions;
    }

    /**
     * Helper method that puts the move buttons in a specific format.
     *
     * @param thePane the Pane of Buttons.
     */
    private void buttonPanePlacements(Pane thePane) {
        Button up =  (Button) thePane.getChildren().get(0);
        Button down = (Button) thePane.getChildren().get(1);
        Button left = (Button) thePane.getChildren().get(2);
        Button right = (Button) thePane.getChildren().get(3);

        up.setLayoutX(thePane.getWidth() / 2);
        up.setLayoutY(0);
        down.setLayoutX(thePane.getWidth() / 2);
        down.setLayoutY(thePane.getHeight());
        left.setLayoutX(0);
        left.setLayoutY(thePane.getHeight() / 2);
        right.setLayoutX(thePane.getWidth());
        right.setLayoutY(thePane.getHeight() /2);
    }

    /**
     * Helper method for playerOptionsPane() which gives move buttons their
     * proper size.
     *
     * @param theButton the Button to be sized.
     */
    private void setMoveButtons(Button theButton) {
        // 27 by 27 here because that was the minimum for the arrows to show.
        theButton.setPrefHeight(27);
        theButton.setPrefWidth(27);
    }


    /**
     *creates side panel with inventory and stats
     *
     * @return side panel
     */
    private VBox createSidePanel() {
        VBox panel = new VBox(10);

        //player stats
        Label healthLabel = new Label("Health: 100");

        //inventory section
        Label inventoryLabel = new Label("Inventory:");

        //add components to panel
        panel.getChildren().addAll(healthLabel, inventoryLabel);

        return panel;
    }

    /**
     * draws game state on the canvas
     */
    private void drawGame() {
        //clear canvas
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        //draw dungeon grid
        drawDungeonGrid();

        //draw player
        drawPlayer();
    }

    /**
     * Draws the dungeon grid.
     */
    private void drawDungeonGrid() {
        gc.setStroke(javafx.scene.paint.Color.BLACK);

        double cellWidth = gameCanvas.getWidth() / 5;
        double cellHeight = gameCanvas.getHeight() / 5;

        // Draw horizontal lines
        for (int i = 0; i <= 3; i++) {
            gc.strokeLine(0, i * cellHeight, cellWidth * 3, i * cellHeight);
        }

        // Draw vertical lines
        for (int i = 0; i <= 3; i++) {
            gc.strokeLine(i * cellWidth, 0, i * cellWidth, cellHeight * 3);
        }
    }

    /**
     * Draws the player character.
     */
    private void drawPlayer() {
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillOval(gameCanvas.getWidth() / 2 - 10, gameCanvas.getHeight() / 2 - 10, 20, 20);
    }

    /**
     * Updates the game display based on the current game state.
     */
    public void updateDisplay() {
        drawGame();
        // Update other UI elements based on game state
    }


}
