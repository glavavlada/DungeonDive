package main.Controller;

import main.Model.Model;
import main.View.GameUI;

public class StateController {
    private GameState currentState;

    public enum GameState {
        EXPLORING,
        COMBAT,
        INVENTORY,
        CHEST,
        PAUSED,
        GAME_OVER,
        VICTORY
    }

    /**
     *constructor initializes game state to exploring
     */
    public StateController() {
        this.currentState = GameState.EXPLORING;
        System.out.println("StateController initialized with EXPLORING state");
    }

    /**
     * Changes current game state
     *
     * @param newState new state to change to
     */
    public void changeState(GameState newState) {
        if (newState != currentState) {
            System.out.println("Game state changing from " + currentState + " to " + newState);
            this.currentState = newState;
        }
    }

    /**
     * get current game state
     * @return current game state
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * checks if game is currently in specified state.
     *
     * @param state state to check against
     * @return True if current state matches specified state
     */
    public boolean isInState(GameState state) {
        return currentState == state;
    }

    public boolean isInCombat() {
        return currentState == GameState.COMBAT;
    }

}

