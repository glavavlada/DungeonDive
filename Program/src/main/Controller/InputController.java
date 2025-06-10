package main.Controller;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import main.Controller.StateController.GameState;
import main.Model.util.Direction;

public class InputController {
    private final GameController myGameController;
    private final StateController myStateController;

    // Track the previous state to detect state changes
    private GameState myPreviousState;

    // Add cooldown tracking
    private long myLastCombatEndTime = 0;
    private static final long COMBAT_END_COOLDOWN = 500; // 500ms cooldown after combat

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

    public void handleKeyPress(final KeyEvent theEvent) {
        KeyCode code = theEvent.getCode();
        GameState currentState = myStateController.getCurrentState();

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
     * Handles movement key releases specifically
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
     * Check if we're still in the cooldown period after combat ended
     */
    private boolean isInPostCombatCooldown() {
        if (myLastCombatEndTime == 0) {
            return false; // No combat has ended yet
        }

        long timeSinceCombatEnd = System.currentTimeMillis() - myLastCombatEndTime;
        return timeSinceCombatEnd < COMBAT_END_COOLDOWN;
    }

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
            // Don't handle W, A, S, D as movement in combat!
            default:
                System.out.println("Unhandled key in combat: " + theCode);
                break;
        }
    }

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