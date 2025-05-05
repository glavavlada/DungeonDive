package View;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Controller.Controller;



/**
 * Holds methods which display the different screens in the game.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/4/2025
 */
public class GameUI {

    /**
     * Primary Stage needed to show every scene.
     */
    private final Stage MY_PRIMARY_STAGE;
    /**
     * Controller used for event handlers.
     */
    private final Controller MY_CONTROLLER;

    /**
     * Constructor that takes Stage from Main, assigning it to the
     * MY_PRIMARY_STAGE final.
     *
     * @param thePrimaryStage the Stage to be assigned to MY_PRIMARY_STAGE.
     */
    public GameUI(Stage thePrimaryStage) {
        MY_PRIMARY_STAGE = thePrimaryStage;
        MY_PRIMARY_STAGE.setTitle("DungeonDive");
        MY_CONTROLLER = new Controller();
    }

    /**
     * Displays the game intro screen.
     */
    public void showIntroScreen() {
        // Creates pane as root and Scene for intro screen.
        Pane root = new Pane();
        Scene intro = new Scene(root, 600, 500);

        // Buttons for exiting and new game.
        Button newGameBtn = new Button();
        Button exitBtn = new Button();
        newGameBtn.setText("New Game");
        exitBtn.setText("Exit");

        // Adds buttons to root.
        root.getChildren().addAll(newGameBtn, exitBtn);

        // Event Handler stuff here.
        Controller controller = new Controller();
        // Takes in ActionEvent (click), and passes itself to newGame().
        newGameBtn.setOnAction(event -> controller.newGame(new ActionEvent(), this));
        exitBtn.setOnAction(event -> controller.exitProgram(new ActionEvent()));

        // Sets scene and shows screen.
        MY_PRIMARY_STAGE.setScene(intro);
        MY_PRIMARY_STAGE.show();

        // The setLayout is after .show(), because button size is needed for
        // location calculation, and the getWidth and getHeight return 0 until
        // button in shown on the screen.
        newGameBtn.setLayoutX((root.getWidth() - newGameBtn.getWidth()) / 2);
        newGameBtn.setLayoutY((root.getHeight() - newGameBtn.getHeight()) / 2);
        exitBtn.setLayoutX(root.getWidth() - exitBtn.getWidth());
        exitBtn.setLayoutY(0);
    }

    /**
     * Displays the hero selection screen.
     */
    public void showHeroSelection() {
        // Pane type and Scene.
        VBox root = new VBox();
        Scene heroSelection = new Scene(root, 600, 500);

        // Buttons, textboxes, etc.
        ToggleGroup heroButtons = new ToggleGroup();
        RadioButton warrior = new RadioButton("Warrior");
        warrior.setToggleGroup(heroButtons);
        RadioButton priestess = new RadioButton("Priestess");
        priestess.setToggleGroup(heroButtons);
        RadioButton thief = new RadioButton("Theif");
        thief.setToggleGroup(heroButtons);
        TextField nameBox = new TextField();
        nameBox.setPromptText("Enter Your Hero's Name");
        Button startGameBtn = new Button();
        startGameBtn.setText("Start Game");

        // First Event Handler stops multiple radio buttons from
        // being selected at once. Details in Controller selectHero().
        Button button = new Button();
        Controller controller = new Controller();
        button.setOnAction(event -> controller.selectHero(heroButtons));

        nameBox.setOnAction(event -> controller.setHeroName());
        startGameBtn.setOnAction(event ->
                controller.startGame(new ActionEvent(), this, heroButtons));

        // Adds stuff to root, then shows on screen.
        root.getChildren().addAll(nameBox, warrior, priestess, thief,
                                  startGameBtn);
        MY_PRIMARY_STAGE.setScene(heroSelection);
        MY_PRIMARY_STAGE.show();

    }

    /**
     * Displays the game screen, where the actual game is played.
     */
    public void showGameScreen() {
        Pane root = new Pane();
        Scene gameScreen = new Scene(root, 600, 500);

        Button pauseBtn = new Button();
        pauseBtn.setText("Pause Game");

        root.getChildren().addAll(pauseBtn);

        Controller controller = new Controller();
        pauseBtn.setOnAction(event ->
                controller.pauseGame(new ActionEvent(), this));

        MY_PRIMARY_STAGE.setScene(gameScreen);
        MY_PRIMARY_STAGE.show();

        pauseBtn.setLayoutX(600 - pauseBtn.getWidth());
        pauseBtn.setLayoutY(0);

    }

    /**
     * Displays pause menu.
     */
    public void showPauseMenu() {
        Pane root = new Pane();
        Scene gameScreen = new Scene(root, 600, 500);

        Button resumeBtn = new Button();
        resumeBtn.setText("Resume Game");
        Button quitToMenuBtn = new Button();
        quitToMenuBtn.setText("Quit to Menu");

        root.getChildren().addAll(resumeBtn, quitToMenuBtn);

        resumeBtn.setOnAction(event -> MY_CONTROLLER.resumeGame(this));
        quitToMenuBtn.setOnAction(event -> MY_CONTROLLER.quitToMenu(this));

        MY_PRIMARY_STAGE.setScene(gameScreen);
        MY_PRIMARY_STAGE.show();

        resumeBtn.setLayoutX((600 - resumeBtn.getWidth()) / 2);
        resumeBtn.setLayoutY((500 - resumeBtn.getHeight()) / 2);
        quitToMenuBtn.setLayoutX((600 - quitToMenuBtn.getWidth()) / 2);
        quitToMenuBtn.setLayoutY((440 - quitToMenuBtn.getHeight()) / 2);
    }

    public void showWinScreen() {

    }

    public void showLoseScreen() {

    }

    public void updateDisplay() {

    }

}
