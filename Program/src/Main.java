import main.Controller.Controller; // Import Controller
import main.Model.Database;
import main.View.util.UIConstants;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.util.logging.Level;

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

    // Static block runs before JavaFX initialization to suppress CSS warnings
    static {
        try {
            // Suppress JavaFX CSS warnings that occur during startup
            Logger cssLogger = Logger.getLogger("javafx.scene.CssStyleHelper");
            cssLogger.setLevel(Level.SEVERE);
            cssLogger.setUseParentHandlers(false); // Prevent propagation to parent loggers

            // Also suppress related JavaFX CSS loggers
            Logger.getLogger("javafx.css").setLevel(Level.SEVERE);
            Logger.getLogger("com.sun.javafx.css").setLevel(Level.SEVERE);

            // Keep other JavaFX logging at reasonable level
            Logger.getLogger("javafx").setLevel(Level.WARNING);

        } catch (Exception e) {
            System.err.println("Failed to configure CSS logging: " + e.getMessage());
        }
    }

    /**
     * main method where the program is run.
     *
     * @param theArgs command line argument.
     */
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }

    /**
     * Entry point for the JavaFX application.
     * Initializes the primary stage, controller, and UI constants, then shows the intro screen.
     *
     * @param thePrimaryStage the top-level container for JavaFX.
     */
    @Override
    public void start(final Stage thePrimaryStage) {
        // 1. Create the single Controller instance
        Controller mainController = new Controller();

        Database database = new Database();
        database.closeConnection();

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