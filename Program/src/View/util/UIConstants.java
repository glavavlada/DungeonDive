package View.util;

import View.GameUI;
import javafx.stage.Stage;

/**
 * Holds constants for UI stuff.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/4/2025
 */
public class UIConstants {

    public final GameUI MY_GAME_UI;

    public UIConstants(Stage thePrimaryStage) {
        MY_GAME_UI = new GameUI(thePrimaryStage);
    }

}
