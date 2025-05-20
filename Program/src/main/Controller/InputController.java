package main.Controller;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import main.Controller.StateController.GameState;


/**
 * Handles raw keyboard input events and translates them into actions
 * by calling methods on the GameController.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class InputController {
    private final GameController myGameController;
    private final StateController myStateController;

    /**
     * Constructor for InputController
     *
     * @param theGameController game controller will process game actions
     * @param theStateController state controller tracks game state
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
        System.out.println("InputController initialized with GameController and StateController.");
    }

    /**
     * Handles key pressed events based on the current game state.
     *
     * @param event The KeyEvent representing the key press.
     */
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        System.out.println("Key Pressed: " + code + " in state: " + myStateController.getCurrentState());

        // Handle input based on current game state
        switch (myStateController.getCurrentState()) {
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

        event.consume();
    }

    /**
     * Handles input during exploration mode.
     *
     * @param code The key code pressed
     */
    private void handleExplorationInput(KeyCode code) {
        switch (code) {
            case UP:
            case W:
                myGameController.movePlayerNorth();
                break;
            case DOWN:
            case S:
                myGameController.movePlayerSouth();
                break;
            case LEFT:
            case A:
                myGameController.movePlayerWest();
                break;
            case RIGHT:
            case D:
                myGameController.movePlayerEast();
                break;
            case I:
                // Open inventory
                myGameController.openInventory();
                break;
            case E:
                // Interact with objects in the room (like chests)
                myGameController.interact();
                break;
            case ESCAPE:
                // Pause game
                myGameController.pauseGame();
                break;
            default:
                // Ignore other keys
                break;
        }
    }

    /**
     * Handles input during combat mode.
     *
     * @param code The key code pressed
     */
    private void handleCombatInput(KeyCode code) {
        switch (code) {
            case A:
                // Regular attack
                myGameController.playerAttack();
                break;
            case S:
                // Special attack
                myGameController.playerSpecialAttack();
                break;
            case R:
                // Run from combat
                myGameController.playerRun();
                break;
            case I:
                // Open inventory during combat
                myGameController.openInventory();
                break;
            case ESCAPE:
                // Pause game
                myGameController.pauseGame();
                break;
            default:
                // Ignore other keys
                break;
        }
    }

    /**
     * Handles input during inventory mode.
     *
     * @param code The key code pressed
     */
    private void handleInventoryInput(KeyCode code) {
        switch (code) {
            case UP:
            case W:
                // Scroll inventory up
                myGameController.scrollInventoryUp();
                break;
            case DOWN:
            case S:
                // Scroll inventory down
                myGameController.scrollInventoryDown();
                break;
            case E:
            case ENTER:
                // Use selected item
                myGameController.useSelectedItem();
                break;
            case I:
            case ESCAPE:
                // Close inventory
                myGameController.closeInventory();
                break;
            default:
                // Ignore other keys
                break;
        }
    }

    /**
     * Handles input when interacting with a chest.
     *
     * @param code The key code pressed
     */
    private void handleChestInput(KeyCode code) {
        switch (code) {
            case E:
            case ENTER:
                // Open chest and collect items
                myGameController.openChest();
                break;
            case ESCAPE:
                // Cancel chest interaction
                myGameController.cancelChestInteraction();
                break;
            default:
                // Ignore other keys
                break;
        }
    }


    /**
     * Handles key released events.
     *
     * @param event The KeyEvent representing the key release.
     */
    public void handleKeyRelease(KeyEvent event) {
        // For most actions in this game, we only care about key presses
        // But we could handle key releases for continuous actions if needed
        event.consume();
    }
}
