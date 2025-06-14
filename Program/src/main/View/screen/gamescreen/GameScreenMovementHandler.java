package main.View.screen.gamescreen;

import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.dungeon.Room;

class GameScreenMovementHandler {
    private final Controller controller;
    private final GameScreenRenderer renderer;
    private final GameScreenCanvasDimensions canvasDimensions;

    public GameScreenMovementHandler(Controller controller, GameScreenRenderer renderer, GameScreenCanvasDimensions canvasDimensions) {
        this.controller = controller;
        this.renderer = renderer;
        this.canvasDimensions = canvasDimensions;
    }

    public boolean checkRoomTransition() {
        Hero player = controller.getPlayer();
        Room currentRoom = getCurrentRoom();
        if (player == null || currentRoom == null) return false;

        double px = player.getPixelX();
        double py = player.getPixelY();
        double heroSize = canvasDimensions.getHeroSize();
        boolean transitioned = false;

        if (py <= canvasDimensions.getBoundaryTop() && currentRoom.hasNorthDoor()) {
            if (isPlayerInDoorRange(px, heroSize)) {
                controller.getGameController().movePlayerNorth();
                player.setPixelPosition(px, canvasDimensions.getBoundaryBottom() - 1);
                transitioned = true;
            }
        } else if (py >= canvasDimensions.getBoundaryBottom() && currentRoom.hasSouthDoor()) {
            if (isPlayerInDoorRange(px, heroSize)) {
                controller.getGameController().movePlayerSouth();
                player.setPixelPosition(px, canvasDimensions.getBoundaryTop() + 1);
                transitioned = true;
            }
        } else if (px <= canvasDimensions.getBoundaryLeft() && currentRoom.hasWestDoor()) {
            if (isPlayerInDoorRange(py, heroSize)) {
                controller.getGameController().movePlayerWest();
                player.setPixelPosition(canvasDimensions.getBoundaryRight() - 1, py);
                transitioned = true;
            }
        } else if (px >= canvasDimensions.getBoundaryRight() && currentRoom.hasEastDoor()) {
            if (isPlayerInDoorRange(py, heroSize)) {
                controller.getGameController().movePlayerEast();
                player.setPixelPosition(canvasDimensions.getBoundaryLeft() + 1, py);
                transitioned = true;
            }
        }

        if (!transitioned) {
            constrainPlayerPosition(player, px, py);
        }

        return transitioned;
    }

    private void constrainPlayerPosition(Hero player, double px, double py) {
        double constrainedX = Math.max(canvasDimensions.getBoundaryLeft(),
                Math.min(canvasDimensions.getBoundaryRight(), px));
        double constrainedY = Math.max(canvasDimensions.getBoundaryTop(),
                Math.min(canvasDimensions.getBoundaryBottom(), py));
        player.setPixelPosition(constrainedX, constrainedY);
    }

    private boolean isPlayerInDoorRange(double playerPos, double heroSize) {
        double playerCenter = playerPos + heroSize / 2;
        double doorWidth = canvasDimensions.getDoorWidth();
        double canvasSize = canvasDimensions.getSize();
        double doorStart = (canvasSize - doorWidth) / 2;
        double doorEnd = doorStart + doorWidth;

        return playerCenter >= doorStart && playerCenter <= doorEnd;
    }

    private Room getCurrentRoom() {
        if (controller == null || controller.getPlayer() == null || controller.getDungeon() == null) {
            return null;
        }
        return controller.getDungeon().getRoom(controller.getPlayer().getPosition());
    }
}