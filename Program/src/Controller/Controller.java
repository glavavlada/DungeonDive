package Controller;

import View.GameUI;
import javafx.event.ActionEvent;
import View.util.UIConstants;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

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
     * Tracks if name has been set for startGame().
     */
    private Boolean nameSet = false;

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

    public void selectHero(ToggleGroup theHeroButtons) {
        RadioButton heroChoice;
        heroChoice = (RadioButton) theHeroButtons.getSelectedToggle();
    }

    /**
     * Sets the hero name and sets nameSet to true.
     */
    public void setHeroName() {
        // The actual hero name value in Model package would be set here.
        // But that is a job for another day...
        nameSet = true;
    }

    /**
     * Starts the actual game after hero and name options done.
     */
    public void startGame(ActionEvent theEvent, GameUI theUI,
                          ToggleGroup theHeroes) {
        if (theHeroes.getSelectedToggle() != null && nameSet) {
            theUI.showGameScreen();
        }
    }

    public void pauseGame(ActionEvent theEvent, GameUI theUI) {
        theUI.showPauseMenu();
    }

    public void resumeGame(GameUI theUI) {
        theUI.showGameScreen();
    }

    public void quitToMenu(GameUI theUI) {
        theUI.showIntroScreen();
    }

}
