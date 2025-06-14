package main.View.screen.gamescreen;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.util.Point;

class GameScreenMinimapRenderer {
    private final Controller controller;
    private Canvas minimapCanvas;
    private GraphicsContext minimapGraphics;

    private static final double MINIMAP_GRID_SIZE = 7.0;

    public GameScreenMinimapRenderer(Controller controller) {
        this.controller = controller;
    }

    public void initialize(Canvas minimapCanvas) {
        this.minimapCanvas = minimapCanvas;
        this.minimapGraphics = minimapCanvas.getGraphicsContext2D();

        minimapCanvas.widthProperty().addListener((obs, oldVal, newVal) -> updateMinimap());
        minimapCanvas.heightProperty().addListener((obs, oldVal, newVal) -> updateMinimap());
    }

    public void updateMinimap() {
        if (controller == null || controller.getDungeon() == null || minimapGraphics == null) {
            return;
        }
        clearMinimap();
        drawMinimapBorder();
        drawMinimapRooms();
    }

    private void clearMinimap() {
        double size = minimapCanvas.getWidth();
        minimapGraphics.clearRect(0, 0, size, size);
        minimapGraphics.setFill(Color.BLACK);
        minimapGraphics.fillRect(0, 0, size, size);
    }

    private void drawMinimapBorder() {
        double size = minimapCanvas.getWidth();
        minimapGraphics.setStroke(Color.rgb(139, 69, 19));
        minimapGraphics.setLineWidth(2);
        minimapGraphics.strokeRect(1, 1, size - 2, size - 2);
    }

    private void drawMinimapRooms() {
        Dungeon dungeon = controller.getDungeon();
        Hero player = controller.getPlayer();
        if (player == null) return;

        Point playerPos = player.getPosition();
        int gridSize = (int) MINIMAP_GRID_SIZE;

        double canvasSize = minimapCanvas.getWidth();
        double roomDisplaySize = canvasSize / (gridSize + 2);
        double totalGridSize = roomDisplaySize * gridSize;
        double startDrawX = (canvasSize - totalGridSize) / 2;
        double startDrawY = (canvasSize - totalGridSize) / 2;

        int startX = playerPos.getX() - gridSize / 2;
        int startY = playerPos.getY() - gridSize / 2;

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int mapX = startX + x;
                int mapY = startY + y;
                Room room = dungeon.getRoom(mapX, mapY);

                if (room != null && room.isVisited()) {
                    boolean isPlayerHere = (mapX == playerPos.getX() && mapY == playerPos.getY());
                    double roomDrawX = startDrawX + x * roomDisplaySize;
                    double roomDrawY = startDrawY + y * roomDisplaySize;
                    drawMinimapRoom(room, roomDrawX, roomDrawY, roomDisplaySize, isPlayerHere);
                }
            }
        }
    }

    private void drawMinimapRoom(Room room, double x, double y, double size, boolean isPlayerPosition) {
        Color roomColor = getMinimapRoomColor(room, isPlayerPosition);
        minimapGraphics.setFill(roomColor);
        minimapGraphics.fillRect(x, y, size, size);

        if (isPlayerPosition) {
            minimapGraphics.setStroke(Color.WHITE);
            minimapGraphics.setLineWidth(1.5);
            minimapGraphics.strokeRect(x, y, size, size);
        }
    }

    private Color getMinimapRoomColor(Room room, boolean isPlayerPosition) {
        if (isPlayerPosition) return Color.LIME;
        if (!room.isVisited()) return Color.TRANSPARENT;

        switch (room.getRoomType()) {
            case ENTRANCE: return Color.LIGHTGREEN;
            case EXIT: return Color.ORANGE;
            case BOSS: return Color.DARKRED;
            case PILLAR: return room.hasPillar() && room.getPillar().isActivated() ? Color.PURPLE : Color.MEDIUMPURPLE;
            case MONSTER: return room.getMonsters().isEmpty() ? Color.LIGHTGRAY : Color.RED;
            case TREASURE: return room.isChestOpened() ? Color.LIGHTGRAY : Color.GOLD;
            case TRAP: return room.hasTrap() && room.getTrap().isSprung() ? Color.LIGHTGRAY : Color.ORANGERED;
            case EMPTY:
            default: return Color.LIGHTGRAY;
        }
    }
}
