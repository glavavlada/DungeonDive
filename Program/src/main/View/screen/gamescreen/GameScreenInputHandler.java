package main.View.screen.gamescreen;
import main.Controller.Controller;

public class GameScreenInputHandler {
    private final Controller controller;
    private final GameScreenGameLoop gameLoop;

    public GameScreenInputHandler(Controller controller, GameScreenGameLoop gameLoop) {
        this.controller = controller;
        this.gameLoop = gameLoop;
    }

    public void handleEscapeKey() {
        // Stop game loop when pausing
        gameLoop.stop();
    }

    public void handleInventoryKey() {
        if (controller.getGameController() != null) {
            controller.getGameController().openInventory();
        }
    }

    public void handleHelpKey() {
        gameLoop.stop();
    }
}
