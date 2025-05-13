package main.Controller;

import main.Model.Model;
import main.View.GameUI;

/**
 * control main game logic
 */
public class GameController {
    private Model gameModel;
    private GameUI gameUI;

    /**
     *constructor for GameController
     *
     * @param gameModel game model
     * @param gameUI game UI
     */
    public GameController(Model gameModel, GameUI gameUI) {
        this.gameModel = gameModel;
        this.gameUI = gameUI;
        System.out.println("GameController initialized with model and UI");
    }

    /**
     * Temporary method to demonstrate GameController is working
     */
    public void printStatus() {
        System.out.println("Game is working");
    }
}
