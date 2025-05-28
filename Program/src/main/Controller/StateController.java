package main.Controller;

public class StateController {
    private GameState myCurrentState;

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
        this.myCurrentState = GameState.EXPLORING;
        System.out.println("StateController initialized with EXPLORING state");
    }

    /**
     * Changes current game state
     *
     * @param theNewState new state to change to
     */
    public void changeState(final GameState theNewState) {
        if (theNewState != myCurrentState) {
            System.out.println("Game state changing from " + myCurrentState + " to " + theNewState);
            this.myCurrentState = theNewState;
        }
    }

    /**
     * get current game state
     * @return current game state
     */
    public GameState getCurrentState() {
        return myCurrentState;
    }

    /**
     * checks if game is currently in specified state.
     *
     * @param theState state to check against
     * @return True if current state matches specified state
     */
    public boolean isInState(final GameState theState) {
        return myCurrentState == theState;
    }

    public boolean isInCombat() {
        return myCurrentState == GameState.COMBAT;
    }

}

