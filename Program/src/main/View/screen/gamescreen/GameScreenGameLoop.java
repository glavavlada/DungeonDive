package main.View.screen.gamescreen;
import javafx.animation.AnimationTimer;
import main.Controller.Controller;
import main.Controller.StateController;
import main.Model.character.Hero;

public class GameScreenGameLoop {
    private final Controller controller;
    private final GameScreenRenderer renderer;
    private final GameScreenUIManager uiManager;
    private final GameScreenMovementHandler movementHandler;

    private AnimationTimer gameLoop;
    private boolean justTransitioned = false;

    public GameScreenGameLoop(Controller controller, GameScreenRenderer renderer, GameScreenUIManager uiManager, GameScreenCanvasDimensions canvasDimensions) {
        this.controller = controller;
        this.renderer = renderer;
        this.uiManager = uiManager;
        this.movementHandler = new GameScreenMovementHandler(controller, renderer, canvasDimensions);
    }

    public void start() {
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (shouldUpdateGame()) {
                        updateGame();
                    }
                    renderer.renderRoom();
                }
            };
        }
        gameLoop.start();
    }

    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private boolean shouldUpdateGame() {
        return controller != null &&
                controller.getGameController() != null &&
                controller.getPlayer() != null &&
                controller.getGameController().getStateController().isInState(StateController.GameState.EXPLORING);
    }

    private void updateGame() {
        Hero player = controller.getPlayer();
        if (player != null) {
            player.updatePixelPosition();
            player.updateAnimation(System.nanoTime());

            uiManager.updateCharacterPortrait();

            if (!justTransitioned) {
                renderer.checkCollectibles();
            }

            renderer.updateAnimations();

            if (movementHandler.checkRoomTransition()) {
                onRoomTransition();
            }

            uiManager.updatePlayerStats();

            justTransitioned = false;
        }
    }

    private void onRoomTransition() {
        justTransitioned = true;
        renderer.onRoomChanged();
        uiManager.onRoomChanged();
    }
}