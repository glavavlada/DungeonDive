package main.Controller;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import main.Controller.StateController.GameState;
import main.Model.util.Direction;

/**
 * Handles all keyboard input for game and routes commands to appropriate controllers
 * This controller processes key press and release events, manages input based on current
 * game state, and provides state-specific input handling for exploration, combat,
 * inventory management, and other game modes
 */
public class InputController {

    /**
     * Reference to main game controller that handles game logic and operations
     */
    private final GameController myGameController;

    /**
     * Reference to state controller that manages overall game state transitions
     */
    private final StateController myStateController;

    /**
     * Tracks previous game state to detect state changes
     */
    private GameState myPreviousState;

    /**
     * Timestamp of when last combat encounter ended
     */
    private long myLastCombatEndTime = 0;

    /**
     * Duration of the cooldown period after combat ends, in milliseconds
     */
    private static final long COMBAT_END_COOLDOWN = 500;

    /**
     * Constructs new InputController with specified game and state controllers
     *
     * @param theGameController GameController to route game actions to, cannot be null
     * @param theStateController StateController to check game state, cannot be null
     * @throws IllegalArgumentException if either controller parameter is null
     */
    public InputController(final GameController theGameController, final StateController theStateController) {
        if (theGameController == null) {
            throw new IllegalArgumentException("GameController cannot be null for InputController.");
        }
        if (theStateController == null) {
            throw new IllegalArgumentException("StateController cannot be null for InputController.");
        }
        this.myGameController = theGameController;
        this.myStateController = theStateController;
        this.myPreviousState = theStateController.getCurrentState();
        System.out.println("InputController initialized with GameController and StateController.");
    }


    /**
     * Handles key press events and routes them to appropriate state specific handlers
     *
     * @param theEvent KeyEvent containing information about pressed key
     */
    public void handleKeyPress(final KeyEvent theEvent) {
        KeyCode code = theEvent.getCode();
        GameState currentState = myStateController.getCurrentState();

        System.out.println("DEBUG: InputController received key: " + code + " in state: " + currentState);

        //check if state changed and stop all movement if transitioning from EXPLORING
        if (myPreviousState != currentState) {
            if (myPreviousState == GameState.EXPLORING) {
                myGameController.stopPlayerMovement();
                System.out.println("Stopped all player movement due to state transition from " + myPreviousState + " to " + currentState);
            }
            //track when combat ends
            if (myPreviousState == GameState.COMBAT && currentState == GameState.EXPLORING) {
                myLastCombatEndTime = System.currentTimeMillis();
                System.out.println("Combat ended, starting cooldown period");
            }
            myPreviousState = currentState;
        }

        System.out.println("Key Pressed: " + code + " in state: " + currentState);

        //handle input based on current game state
        switch (currentState) {
            case EXPLORING:
                handleExplorationInput(code);
                break;
            case COMBAT:
                handleCombatInput(code);
                break;
            case INVENTORY:
                handleInventoryInput(code);
                break;
            case CHEST:
                handleChestInput(code);
                break;
            case PAUSED:
                // Handle pause menu inputs if needed
                break;
            case GAME_OVER:
            case VICTORY:
                // Handle game over/victory screen inputs
                break;
        }

        theEvent.consume();
    }

    /**
     * Handles key release events, primarily for movement controls
     *
     * @param theEvent KeyEvent containing information about released key
     */
    public void handleKeyRelease(final KeyEvent theEvent) {
        KeyCode code = theEvent.getCode();
        GameState currentState = myStateController.getCurrentState();

        // ONLY handle movement key releases in EXPLORING state
        if (currentState == GameState.EXPLORING && !isInPostCombatCooldown()) {
            handleMovementKeyRelease(code);
        }

        theEvent.consume();
    }

    /**
     * Handles movement key releases specifically for directional movement
     *
     * @param theCode KeyCode of the released key
     */
    private void handleMovementKeyRelease(final KeyCode theCode) {
        switch (theCode) {
            case UP:
            case W:
                myGameController.stopPlayerMovementNorth();
                break;
            case DOWN:
            case S:
                myGameController.stopPlayerMovementSouth();
                break;
            case LEFT:
            case A:
                myGameController.stopPlayerMovementWest();
                break;
            case RIGHT:
            case D:
                myGameController.stopPlayerMovementEast();
                break;
        }
    }

    /**
     * Checks if game is currently in cooldown period after combat ended
     *
     * @return true if still in post-combat cooldown, false otherwise
     */
    private boolean isInPostCombatCooldown() {
        if (myLastCombatEndTime == 0) {
            return false; // No combat has ended yet
        }

        long timeSinceCombatEnd = System.currentTimeMillis() - myLastCombatEndTime;
        return timeSinceCombatEnd < COMBAT_END_COOLDOWN;
    }

    /**
     * Handles input during exploration state
     *
     * @param theCode KeyCode of pressed key
     */
    private void handleExplorationInput(final KeyCode theCode) {
        // Check if we're in cooldown period after combat
        if (isInPostCombatCooldown()) {
            System.out.println("Ignoring input during post-combat cooldown");
            return;
        }

        switch (theCode) {
            case UP:
            case W:
                myGameController.startPlayerMovementNorth();
                break;
            case DOWN:
            case S:
                myGameController.startPlayerMovementSouth();
                break;
            case LEFT:
            case A:
                myGameController.startPlayerMovementWest();
                break;
            case RIGHT:
            case D:
                myGameController.startPlayerMovementEast();
                break;
            case E:
            case ENTER:
                myGameController.interact();
                break;
            case I:
                myGameController.openInventory();
                break;
            case ESCAPE:
                myGameController.pauseGame();
                break;
            default:
                break;
        }
    }

    /**
     * Handles input during combat state
     *
     * @param theCode KeyCode of pressed key
     */
    private void handleCombatInput(final KeyCode theCode) {
        // Make sure we're actually in combat state
        if (!myStateController.isInState(GameState.COMBAT)) {
            System.out.println("Ignoring combat input - not in combat state");
            return;
        }

        switch (theCode) {
            case A:
                myGameController.playerAttack();
                break;
            case S:
                myGameController.playerSpecialAttack();
                break;
            case R:
                myGameController.playerRun();
                break;
            case I:
                myGameController.openInventory();
                break;
            case ESCAPE:
                myGameController.pauseGame();
                break;
            // Don't handle W, A, S, D as movement in combat
            default:
                System.out.println("Unhandled key in combat: " + theCode);
                break;
        }
    }

    /**
     * Handles input during inventory state
     *
     * @param theCode KeyCode of pressed key
     */
    private void handleInventoryInput(final KeyCode theCode) {
        switch (theCode) {
            case UP:
            case W:
                myGameController.scrollInventoryUp();
                break;
            case DOWN:
            case S:
                myGameController.scrollInventoryDown();
                break;
            case E:
            case ENTER:
                myGameController.useSelectedItem();
                break;
            case I:
            case ESCAPE:
                myGameController.closeInventory();
                break;
            default:
                break;
        }
    }

    /**
     * Handles input during chest interaction state
     *
     * @param theCode KeyCode of pressed key
     */
    private void handleChestInput(final KeyCode theCode) {
        switch (theCode) {
            case E:
            case ENTER:
                myGameController.openChest();
                break;
            case ESCAPE:
                myGameController.cancelChestInteraction();
                break;
            default:
                break;
        }
    }
}