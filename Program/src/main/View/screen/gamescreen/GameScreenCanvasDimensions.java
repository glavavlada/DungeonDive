package main.View.screen.gamescreen;

public class GameScreenCanvasDimensions {
    private double size;
    private double tileSize;
    private double heroSize;
    private double wallThickness;
    private double doorWidth;

    // Configuration constants
    private static final double CANVAS_MIN_SIZE = 300.0;
    private static final double CANVAS_MAX_SIZE = 800.0;
    private static final double TILES_PER_CANVAS = 30.0;
    private static final double HERO_SIZE_MULTIPLIER = 2.0;
    private static final double DOOR_WIDTH_MULTIPLIER = 2.0;

    public GameScreenCanvasDimensions() {
        updateSize(480.0); // Default size
    }

    public void updateSize(double canvasSize) {
        this.size = Math.max(CANVAS_MIN_SIZE, Math.min(CANVAS_MAX_SIZE, canvasSize));
        this.tileSize = this.size / TILES_PER_CANVAS;
        this.heroSize = tileSize * HERO_SIZE_MULTIPLIER;
        this.wallThickness = tileSize;
        this.doorWidth = wallThickness * DOOR_WIDTH_MULTIPLIER;
    }

    // Getters
    public double getSize() { return size; }
    public double getTileSize() { return tileSize; }
    public double getHeroSize() { return heroSize; }
    public double getWallThickness() { return wallThickness; }
    public double getDoorWidth() { return doorWidth; }
    public double getBoundaryTop() { return wallThickness; }
    public double getBoundaryBottom() { return size - wallThickness - heroSize; }
    public double getBoundaryLeft() { return wallThickness; }
    public double getBoundaryRight() { return size - wallThickness - heroSize; }
}
