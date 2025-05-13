import main.Controller.Controller; // Import Controller
import main.View.util.UIConstants;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class where the program is run.
 * Entry point for the JavaFX application.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class Main extends Application {
    /**
     * main method where the program is run.
     *
     * @param theArgs command line argument.
     */
    public static void main(String[] theArgs) {
        launch(theArgs);
    }

    /**
     * Entry point for the JavaFX application.
     * Initializes the primary stage, controller, and UI constants, then shows the intro screen.
     *
     * @param thePrimaryStage the top-level container for JavaFX.
     */
    @Override
    public void start(Stage thePrimaryStage) {
        // 1. Create the single Controller instance
        Controller mainController = new Controller();

        // 2. Create UIConstants, passing the stage and the controller
        //    UIConstants will, in turn, create GameUI and pass the controller to it.
        UIConstants uiConstants = new UIConstants(thePrimaryStage, mainController);

        // 3. Access GameUI from UIConstants and show the initial screen
        //    The GameUI instance now has the correct controller.
        //    And all Screen instances created by GameUI will also receive this controller.
        if (uiConstants.getGameUI() != null) {
            uiConstants.getGameUI().showIntroScreen();
        } else {
            System.err.println("Error: GameUI could not be initialized via UIConstants.");
            // Handle error, perhaps show a basic error dialog
        }
    }
}
