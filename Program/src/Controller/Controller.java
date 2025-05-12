package Controller;

import View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;

/**
 * Controls how user input is passed from view to model.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/10/2025
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
     * @param theUI the GameUI which can show different screens.
     */
    public void newGame(ActionEvent theEvent, GameUI theUI) {
        theUI.showHeroSelection();
    }

    /**
     * Goes to the SavesScreen after clicking Resume Game button in the intro.
     *
     * @param theEvent the ActionEvent.
     * @param theUI the GameUI which can show different screens.
     */
    public void savedGames(ActionEvent theEvent, GameUI theUI) {
        theUI.showSavesScreen();
    }

    /**
     * Fully exits the program after clicking exit button in intro screen.
     *
     * @param theEvent the ActionEvent.
     */
    public void exitProgram(ActionEvent theEvent) {
        System.exit(0);
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
     * Swaps hero description in the HeroSelectionScreen depending on which
     * hero type has been selected.
     *
     * @param heroType integer representing which hero.
     * @param theDescription the Label whose text will be changed.
     */
    public void heroDescription(int heroType, Label theDescription) {
        // HeroType 1 is warrior. 2 is Priestess. 3 is thief.
        if (heroType == 1) {
            theDescription.setText("A heavy hitter with lots of health but " +
                    "low attack speed.");
        } else if (heroType == 2) {
            theDescription.setText("A healer with average health and " +
                    "average attack (heal) speed.");
        } else if (heroType == 3) {
            theDescription.setText("A sneaky attacker with average health" +
                    " and high attack speed.");
        }
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

    /**
     * Leads to PauseScreen after clicking pause game in the GameScreen.
     *
     * @param theEvent
     * @param theUI
     */
    public void pauseGame(ActionEvent theEvent, GameUI theUI) {
        theUI.showPauseMenu();
    }

    /**
     * Resumes the current game while in the PauseScreen.
     *
     * @param theUI the GameUI that can show different screens.
     */
    public void resumeCurrentGame(GameUI theUI) {
        theUI.showGameScreen();
    }

    /**
     *  Goes to the intro screen after clicking quit to menu button in the
     *  PauseScreen.
     *
     * @param theUI the GameUI that can show different screens.
     */
    public void quitToMenu(GameUI theUI) {
        theUI.showIntroScreen();
    }

}