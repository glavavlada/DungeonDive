package main.Controller;

/**
 * Manages the overall state of the game application
 * This controller tracks current game phase and handles transitions between
 * different states such as exploration, combat, inventory management, pause states,
 * and game end conditions
 */
public class StateController {

    /**
     * The current state of game
     */
    private GameState myCurrentState;

    /**
     * Enumeration of all possible game states
     */
    public enum GameState {
        /** Player is exploring dungeon, moving between rooms and interacting with objects */
        EXPLORING,
        /** Player is engaged in combat with monsters */
        COMBAT,
        /** Player is viewing or managing their inventory */
        INVENTORY,
        /** Player is interacting with chest */
        CHEST,
        /** Game is paused, showing pause menu */
        PAUSED,
        /** Game has ended due to player death */
        GAME_OVER,
        /** Game has ended due to player achieving victory conditions */
        VICTORY
    }

    /**
     * Constructs new StateController and initializes game to exploration state
     */
    public StateController() {
        this.myCurrentState = GameState.EXPLORING;
        System.out.println("StateController initialized with EXPLORING state");
    }

    /**
     * Changes current game state to specified new state
     *
     * @param theNewState new GameState to transition to
     */
    public void changeState(final GameState theNewState) {
        if (theNewState != myCurrentState) {
            System.out.println("Game state changing from " + myCurrentState + " to " + theNewState);
            this.myCurrentState = theNewState;
        }
    }

    /**
     * Gets current game state
     *
     * @return current GameState
     */
    public GameState getCurrentState() {
        return myCurrentState;
    }

    /**
     * Checks if game is currently in specified state
     *
     * @param theState GameState to check against current state
     * @return true if current state matches specified state, false otherwise
     */
    public boolean isInState(final GameState theState) {
        return myCurrentState == theState;
    }

}

