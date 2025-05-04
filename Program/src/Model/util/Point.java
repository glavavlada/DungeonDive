package Model.util;

/**
 * Represents a position in 2D space.
 */
public class Point {
    private int x;
    private int y;

    /**
     * Constructor for Point.
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters and setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}