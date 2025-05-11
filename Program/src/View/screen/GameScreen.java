package View.screen;

import Controller.Controller;
import View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
      //  Pane root = new Pane();
      //  Scene gameScreen = new Scene(root, 600, 500);

      //  Button pauseBtn = new Button();
      //  pauseBtn.setText("Pause Game");

      //  setButtonSize(pauseBtn);

      //  root.getChildren().addAll(pauseBtn);

      //  Controller controller = new Controller();
      //  pauseBtn.setOnAction(event ->
      //          controller.pauseGame(new ActionEvent(), theUI));

      //  PRIMARY_STAGE.setScene(gameScreen);
      //  PRIMARY_STAGE.show();

      //  pauseBtn.setLayoutX(600 - pauseBtn.getWidth());
      //  pauseBtn.setLayoutY(0);



        BorderPane root = new BorderPane();
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
        root.setCenter(gameCanvas);
        root.setRight(sidePanel);

        //use controller from GameUI instead of creating a new one
        pauseBtn.setOnAction(event ->
                theUI.getController().pauseGame(new ActionEvent(), theUI));

        PRIMARY_STAGE.setScene(gameScene);
        PRIMARY_STAGE.show();

        //it allows the GameUI to set up key event handlers
        theUI.setGameScene(gameScene);


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
        for (int i = 0; i <= 5; i++) {
            gc.strokeLine(0, i * cellHeight, gameCanvas.getWidth(), i * cellHeight);
        }

        // Draw vertical lines
        for (int i = 0; i <= 5; i++) {
            gc.strokeLine(i * cellWidth, 0, i * cellWidth, gameCanvas.getHeight());
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
