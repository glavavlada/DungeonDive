package main.View.screen.gamescreen;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.dungeon.Room;
import main.Model.element.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameScreenRenderer {
    private final Controller controller;
    private final GameScreenCanvasDimensions canvasDimensions;

    private Canvas roomCanvas;
    private GraphicsContext graphicsContext;
    private Canvas minimapCanvas;
    private GraphicsContext minimapGraphics;
    private Canvas portraitCanvas;
    private GraphicsContext portraitGraphics;

    // Sprites and animations
    private Image pillarSprite;
    private Image chestSprite;
    private double pillarGlowAnimation = 0.0;
    private double chestGlowAnimation = 0.0;
    private boolean hasTriedChestCollection = false;
    private boolean hasShownGoldWarning = false;

    // Constants
    private static final double PILLAR_GLOW_SPEED = 0.05;
    private static final double CHEST_GLOW_SPEED = 0.04;
    private static final double PILLAR_COLLECTION_DISTANCE = 30.0;
    private static final double CHEST_COLLECTION_DISTANCE = 40.0;

    public GameScreenRenderer(Controller controller, GameScreenCanvasDimensions canvasDimensions) {
        this.controller = controller;
        this.canvasDimensions = canvasDimensions;
    }

    public void initialize(Scene scene) {
        roomCanvas = new Canvas(480.0, 480.0); // DEFAULT_CANVAS_SIZE
        graphicsContext = roomCanvas.getGraphicsContext2D();
        setupCanvasBindings(scene);
    }

    private void setupCanvasBindings(Scene scene) {
        // Canvas responsive bindings
        javafx.beans.binding.NumberBinding availableWidth = scene.widthProperty()
                .subtract(280) // Approximate panel widths
                .subtract(40);
        javafx.beans.binding.NumberBinding availableHeight = scene.heightProperty().multiply(0.70);
        javafx.beans.binding.NumberBinding canvasSizeBinding = javafx.beans.binding.Bindings.max(300.0,
                javafx.beans.binding.Bindings.min(availableWidth, availableHeight));

        roomCanvas.widthProperty().bind(canvasSizeBinding);
        roomCanvas.heightProperty().bind(canvasSizeBinding);
        roomCanvas.widthProperty().addListener((obs, oldVal, newVal) -> scaleCanvas());
    }

    public void setupCanvas() {
        scaleCanvas();
    }

    public void scaleCanvas() {
        canvasDimensions.updateSize(roomCanvas.getWidth());

        Hero player = controller.getPlayer();
        if (player != null) {
            player.setMovementSpeedForCanvasSize(canvasDimensions.getSize());
        }

        Platform.runLater(this::renderRoom);
    }

    public void loadSprites() {
        loadPillarSprite();
        loadChestSprite();
    }

    private void loadPillarSprite() {
        try {
            pillarSprite = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/sprites/icons/pillar.png")));
        } catch (Exception e) {
            System.err.println("Could not load pillar sprite: " + e.getMessage());
            pillarSprite = null;
        }
    }

    private void loadChestSprite() {
        try {
            chestSprite = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/sprites/icons/chest.png")));
        } catch (Exception e) {
            System.err.println("Could not load chest sprite: " + e.getMessage());
            chestSprite = null;
        }
    }

    public void initializeHeroPosition() {
        Hero player = controller.getPlayer();
        if (player != null && player.getPixelX() == 0 && player.getPixelY() == 0) {
            // Place the player at the bottom entrance of the first room instead of the center.
            double startX = canvasDimensions.getSize() / 2 - canvasDimensions.getHeroSize() / 2;
            double startY = canvasDimensions.getBoundaryBottom() - 1;
            player.setPixelPosition(startX, startY);
        }
    }

    public void renderRoom() {
        if (graphicsContext == null) return;

        clearCanvas();
        drawFloor();
        drawWalls();
        drawPillar();
        drawChest();
        drawPlayer();
    }

    private void clearCanvas() {
        graphicsContext.setFill(Color.rgb(20, 20, 25));
        graphicsContext.fillRect(0, 0, canvasDimensions.getSize(), canvasDimensions.getSize());
    }

    private void drawFloor() {
        Color darkStone = Color.rgb(40, 40, 45);
        Color lightStone = Color.rgb(50, 50, 55);
        int tilesPerSide = (int)(canvasDimensions.getSize() / canvasDimensions.getTileSize());

        for (int y = 0; y < tilesPerSide; y++) {
            for (int x = 0; x < tilesPerSide; x++) {
                graphicsContext.setFill(((x + y) % 2 == 0) ? darkStone : lightStone);
                graphicsContext.fillRect(x * canvasDimensions.getTileSize(), y * canvasDimensions.getTileSize(),
                        canvasDimensions.getTileSize(), canvasDimensions.getTileSize());
            }
        }
    }

    private void drawWalls() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) return;

        Color wallColor = Color.rgb(80, 85, 100);
        Color doorColor = Color.rgb(25, 25, 30);

        double size = canvasDimensions.getSize();
        double thickness = canvasDimensions.getWallThickness();
        double doorWidth = canvasDimensions.getDoorWidth();

        // Draw walls
        drawWall(0, 0, size, thickness, currentRoom.hasNorthDoor(), true, doorWidth, wallColor, doorColor);
        drawWall(0, size - thickness, size, thickness, currentRoom.hasSouthDoor(), true, doorWidth, wallColor, doorColor);
        drawWall(0, 0, thickness, size, currentRoom.hasWestDoor(), false, doorWidth, wallColor, doorColor);
        drawWall(size - thickness, 0, thickness, size, currentRoom.hasEastDoor(), false, doorWidth, wallColor, doorColor);
    }

    private void drawWall(double x, double y, double width, double height, boolean hasDoor,
                          boolean isHorizontal, double doorSize, Color wallColor, Color doorColor) {
        graphicsContext.setFill(wallColor);
        graphicsContext.fillRect(x, y, width, height);

        if (hasDoor) {
            graphicsContext.setFill(doorColor);
            if (isHorizontal) {
                double doorStart = x + (width - doorSize) / 2;
                graphicsContext.fillRect(doorStart, y, doorSize, height);
            } else {
                double doorStart = y + (height - doorSize) / 2;
                graphicsContext.fillRect(x, doorStart, width, doorSize);
            }
        }
    }

    private void drawPillar() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null || !currentRoom.hasPillar() || currentRoom.getPillar().isActivated()) {
            return;
        }

        double pillarSize = canvasDimensions.getTileSize() * 4;
        double pillarX = (canvasDimensions.getSize() - pillarSize) / 2;
        double pillarY = (canvasDimensions.getSize() - pillarSize) / 2;

        if (pillarSprite != null) {
            double glowIntensity = (Math.sin(pillarGlowAnimation) + 1.0) / 2.0;
            double alpha = 0.7 + (glowIntensity * 0.3);
            double originalAlpha = graphicsContext.getGlobalAlpha();

            graphicsContext.setGlobalAlpha(alpha);
            graphicsContext.drawImage(pillarSprite, pillarX, pillarY, pillarSize, pillarSize);

            graphicsContext.setGlobalAlpha(glowIntensity * 0.3);
            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.fillOval(pillarX - 5, pillarY - 5, pillarSize + 10, pillarSize + 10);

            graphicsContext.setGlobalAlpha(originalAlpha);
        } else {
            drawFallbackPillar(pillarX, pillarY, pillarSize);
        }
    }

    private void drawFallbackPillar(double pillarX, double pillarY, double pillarSize) {
        double glowIntensity = (Math.sin(pillarGlowAnimation) + 1.0) / 2.0;

        graphicsContext.setFill(Color.rgb(255, 255, 0, glowIntensity * 0.3));
        graphicsContext.fillOval(pillarX - 10, pillarY - 10, pillarSize + 20, pillarSize + 20);

        graphicsContext.setFill(Color.rgb(200, 200, 200));
        graphicsContext.fillRect(pillarX, pillarY, pillarSize, pillarSize);

        Color pillarColor = Color.rgb(
                (int)(255 * (0.7 + glowIntensity * 0.3)),
                (int)(255 * (0.7 + glowIntensity * 0.3)),
                0
        );
        graphicsContext.setFill(pillarColor);
        graphicsContext.fillRect(pillarX + 5, pillarY + 5, pillarSize - 10, pillarSize - 10);
    }

    private void drawChest() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null || !currentRoom.hasChest() || currentRoom.isChestOpened()) {
            return;
        }

        double chestSize = canvasDimensions.getTileSize() * 3;
        double[] chestPosition = calculateChestPosition(currentRoom, chestSize);
        double chestX = chestPosition[0];
        double chestY = chestPosition[1];

        if (chestSprite != null) {
            drawChestSprite(chestX, chestY, chestSize);
        } else {
            drawFallbackChest(chestX, chestY, chestSize);
        }
    }

    private double[] calculateChestPosition(Room currentRoom, double chestSize) {
        double chestX, chestY;
        if (currentRoom.hasPillar() && !currentRoom.getPillar().isActivated()) {
            chestX = (canvasDimensions.getSize() / 2 + canvasDimensions.getTileSize() * 3) - (chestSize / 2);
            chestY = (canvasDimensions.getSize() / 2 - canvasDimensions.getTileSize() * 2) - (chestSize / 2);
        } else {
            chestX = (canvasDimensions.getSize() - chestSize) / 2;
            chestY = (canvasDimensions.getSize() - chestSize) / 2;
        }
        return new double[]{chestX, chestY};
    }

    private void drawChestSprite(double chestX, double chestY, double chestSize) {
        double glowIntensity = (Math.sin(chestGlowAnimation) + 1.0) / 2.0;
        double alpha = 0.7 + (glowIntensity * 0.3);
        double originalAlpha = graphicsContext.getGlobalAlpha();

        graphicsContext.setGlobalAlpha(alpha);
        graphicsContext.drawImage(chestSprite, chestX, chestY, chestSize, chestSize);

        graphicsContext.setGlobalAlpha(glowIntensity * 0.3);
        graphicsContext.setFill(Color.GOLD);
        graphicsContext.fillOval(chestX - 5, chestY - 5, chestSize + 10, chestSize + 10);

        graphicsContext.setGlobalAlpha(originalAlpha);
    }

    private void drawFallbackChest(double chestX, double chestY, double chestSize) {
        double glowIntensity = (Math.sin(chestGlowAnimation) + 1.0) / 2.0;

        graphicsContext.setFill(Color.rgb(255, 215, 0, glowIntensity * 0.3));
        graphicsContext.fillOval(chestX - 10, chestY - 10, chestSize + 20, chestSize + 20);

        graphicsContext.setFill(Color.rgb(139, 69, 19));
        graphicsContext.fillRect(chestX, chestY, chestSize, chestSize);

        Color chestColor = Color.rgb(
                (int)(255 * (0.7 + glowIntensity * 0.3)),
                (int)(215 * (0.7 + glowIntensity * 0.3)),
                0
        );
        graphicsContext.setFill(chestColor);
        graphicsContext.fillRect(chestX + 5, chestY + 5, chestSize - 10, chestSize - 10);

        graphicsContext.setFill(Color.DARKGRAY);
        graphicsContext.fillOval(chestX + chestSize/2 - 5, chestY + chestSize/2 - 5, 10, 10);
    }

    private void drawPlayer() {
        Hero player = controller.getPlayer();
        if (player == null) return;

        double heroSize = canvasDimensions.getHeroSize();
        Image spriteSheet = player.getSpriteSheet();

        if (spriteSheet != null) {
            graphicsContext.drawImage(spriteSheet,
                    player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH,
                    player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    player.getPixelX(), player.getPixelY(), heroSize, heroSize);
        } else {
            graphicsContext.setFill(Color.BLUE);
            graphicsContext.fillRect(player.getPixelX(), player.getPixelY(), heroSize, heroSize);
        }
    }

    public void updateAnimations() {
        pillarGlowAnimation += PILLAR_GLOW_SPEED;
        if (pillarGlowAnimation > Math.PI * 2) {
            pillarGlowAnimation = 0.0;
        }

        chestGlowAnimation += CHEST_GLOW_SPEED;
        if (chestGlowAnimation > Math.PI * 2) {
            chestGlowAnimation = 0.0;
        }
    }

    public void checkCollectibles() {
        checkPillarCollection();
        checkChestCollection();
    }

    private void checkPillarCollection() {
        Room currentRoom = getCurrentRoom();
        Hero player = controller.getPlayer();

        if (currentRoom == null || player == null || !currentRoom.hasPillar()
                || currentRoom.getPillar().isActivated()) {
            return;
        }

        if (isPlayerNearPillar(player)) {
            collectPillar(currentRoom);
        }
    }

    private boolean isPlayerNearPillar(Hero player) {
        double pillarCenterX = canvasDimensions.getSize() / 2;
        double pillarCenterY = canvasDimensions.getSize() / 2;
        double playerCenterX = player.getPixelX() + canvasDimensions.getHeroSize() / 2;
        double playerCenterY = player.getPixelY() + canvasDimensions.getHeroSize() / 2;

        double distance = Math.sqrt(
                Math.pow(playerCenterX - pillarCenterX, 2) +
                        Math.pow(playerCenterY - pillarCenterY, 2)
        );

        return distance <= PILLAR_COLLECTION_DISTANCE;
    }

    private void collectPillar(Room room) {
        try {
            Hero player = controller.getPlayer();
            room.getPillar().activate(player);
            player.setPillarsActivated(player.getPillarsActivated() + 1);
        } catch (Exception e) {
            System.err.println("Error collecting pillar: " + e.getMessage());
        }
    }

    private void checkChestCollection() {
        Room currentRoom = getCurrentRoom();
        Hero player = controller.getPlayer();

        if (currentRoom == null || player == null || !currentRoom.hasChest()) {
            hasTriedChestCollection = false;
            return;
        }

        if (currentRoom.isChestOpened() || hasTriedChestCollection) {
            return;
        }

        if (isPlayerNearChest(currentRoom, player)) {
            collectChest(currentRoom);
        } else {
            hasShownGoldWarning = false;
        }
    }

    private boolean isPlayerNearChest(Room currentRoom, Hero player) {
        double chestSize = canvasDimensions.getTileSize() * 3;
        double[] chestPosition = calculateChestPosition(currentRoom, chestSize);
        double chestCenterX = chestPosition[0] + chestSize / 2;
        double chestCenterY = chestPosition[1] + chestSize / 2;

        double playerCenterX = player.getPixelX() + canvasDimensions.getHeroSize() / 2;
        double playerCenterY = player.getPixelY() + canvasDimensions.getHeroSize() / 2;

        double distance = Math.sqrt(
                Math.pow(playerCenterX - chestCenterX, 2) +
                        Math.pow(playerCenterY - chestCenterY, 2)
        );

        return distance <= CHEST_COLLECTION_DISTANCE;
    }

    private void collectChest(Room room) {
        try {
            Hero player = controller.getPlayer();

            if (player.getGold() < 5) {
                if (!hasShownGoldWarning) {
                    hasShownGoldWarning = true;
                }
                return;
            }

            int originalGold = player.getGold();
            int originalInventorySize = player.getInventory().size();

            room.openChest(player);

            if (player.getGold() < originalGold) {
                hasTriedChestCollection = true;

                List<Item> currentInventory = player.getInventory();
                List<Item> newlyCollectedItems = new ArrayList<>();

                for (int i = originalInventorySize; i < currentInventory.size(); i++) {
                    newlyCollectedItems.add(currentInventory.get(i));
                }
            }
        } catch (Exception e) {
            System.err.println("Error collecting chest: " + e.getMessage());
        }
    }

    public void onRoomChanged() {
        hasTriedChestCollection = false;
    }

    private Room getCurrentRoom() {
        if (controller == null || controller.getPlayer() == null || controller.getDungeon() == null) {
            return null;
        }
        return controller.getDungeon().getRoom(controller.getPlayer().getPosition());
    }

    // Getters
    public Canvas getRoomCanvas() { return roomCanvas; }
    public boolean hasShownGoldWarning() { return hasShownGoldWarning; }
    public boolean hasTriedChestCollection() { return hasTriedChestCollection; }
}