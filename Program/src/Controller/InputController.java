package Controller;

import javafx.scene.input.KeyEvent;

/**
 * handles user input and passes it to GameController
 */
public class InputController {
    private GameController gameController;

    /**
     * constructor for InputController
     *
     * @param gameController The game controller
     */
    public InputController(GameController gameController) {
        this.gameController = gameController;
        System.out.println("InputController initialized with GameController");
    }

    /**
     *handle keyboard input
     *
     * @param event The key event
     */
    public void handleKeyInput(KeyEvent event) {
        System.out.println("Key pressed: " + event.getCode());
    }
}
