import View.util.UIConstants;
import javafx.application.Application;
import javafx.stage.Stage;



/**
 * Main class where the program is run.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/4/2025
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
     * Entry to the java fx program.
     *
     * @param thePrimaryStage the top level container for JavaFX
     */
    @Override
    public void start(Stage thePrimaryStage) {

        UIConstants UIConst = new UIConstants(thePrimaryStage);
        UIConst.MY_GAME_UI.showGameScreen();
       
    }
}