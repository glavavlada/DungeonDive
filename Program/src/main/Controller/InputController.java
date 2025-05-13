package main.Controller;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

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

    /**
     * Constructor for InputController.
     *
     * @param theGameController The game controller that will process game actions.
     */
    public InputController(final GameController theGameController) {
        if (theGameController == null) {
            throw new IllegalArgumentException("GameController cannot be null for InputController.");
        }
        this.myGameController = theGameController;
        System.out.println("InputController initialized with GameController.");
    }

    /**
     * Handles key pressed events.
     * This method is typically set as the OnKeyPressed event handler for a scene.
     *
     * @param event The KeyEvent representing the key press.
     */
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        System.out.println("Key Pressed: " + code);

        // Delegate to GameController to handle the actual game logic for the key press
        // For example:
        // myGameController.processPlayerAction(code, true); // true for pressed
        // Or more specific methods:
        switch (code) {
            case UP:
            case W:
                // myGameController.movePlayerNorth();
                System.out.println("InputController: Forward/Up action initiated.");
                break;
            case DOWN:
            case S:
                // myGameController.movePlayerSouth();
                System.out.println("InputController: Backward/Down action initiated.");
                break;
            case LEFT:
            case A:
                // myGameController.movePlayerWest();
                System.out.println("InputController: Left action initiated.");
                break;
            case RIGHT:
            case D:
                // myGameController.movePlayerEast();
                System.out.println("InputController: Right action initiated.");
                break;
            case SPACE:
                // myGameController.playerAttack();
                System.out.println("InputController: Attack action initiated.");
                break;
            case E:
                // myGameController.playerInteract();
                System.out.println("InputController: Interact action initiated.");
                break;
            // Add other key bindings as needed (e.g., for inventory, using items)
            default:
                // Optional: handle other keys or do nothing
                break;
        }
        event.consume(); // Consume the event if it's handled here
    }

    /**
     * Handles key released events.
     * This method is typically set as the OnKeyReleased event handler for a scene.
     *
     * @param event The KeyEvent representing the key release.
     */
    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        // System.out.println("Key Released: " + code); // Often less critical to log releases

        // Delegate to GameController if specific release actions are needed
        // For example, to stop continuous movement:
        // myGameController.processPlayerAction(code, false); // false for released
        // Or if movement is state-based (e.g., isMovingUp = false), GameController handles that.

        event.consume(); // Consume the event if it's handled here
    }
}
