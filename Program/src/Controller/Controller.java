package Controller;

import View.GameUI;
import javafx.event.ActionEvent;
import View.util.UIConstants;

/**
 * Controls how user input is passed from view to model.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/4/2025
 */
public class Controller {

    /**
     * Takes in the event, but also the UI. The UI is used to switch to the
     * next screen.
     *
     * @param theEvent the ActionEvent.
     * @param theUI the UI used for switching screens after an event.
     */
    public void newGame(ActionEvent theEvent, GameUI theUI) {
        theUI.showHeroSelection();
    }

    public void exitProgram(ActionEvent theEvent) {
        System.exit(0);
    }

}
