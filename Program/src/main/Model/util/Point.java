package main.Model.util;

import java.util.Objects;

/**
 * Represents a 2D point with x and y coordinates.
 * This class is used to define positions within the dungeon.
 */
public class Point {
    private final int myX;
    private final int myY;

    /**
     * Constructs a new Point with specified x and y coordinates.
     *
     * @param theX The x-coordinate.
     * @param theY The y-coordinate.
     */
    public Point(final int theX, final int theY) {
        this.myX = theX;
        this.myY = theY;
    }

    /**
     * Gets the x-coordinate of this point.
     *
     * @return The x-coordinate.
     */
    public int getX() {
        return myX;
    }

    /**
     * Gets the y-coordinate of this point.
     *
     * @return The y-coordinate.
     */
    public int getY() {
        return myY;
    }

    /**
     * Calculates the Euclidean distance between this point and another point.
     *
     * @param theOther The other point.
     * @return The distance between this point and the other point.
     */
    public double distance(final Point theOther) {
        final int dx = this.myX - theOther.myX;
        final int dy = this.myY - theOther.myY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks if this Point is equal to another object.
     * Two points are considered equal if their x and y coordinates are the same.
     *
     * @param theOther The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(final Object theOther) {
        if (this == theOther) {
            return true;
        }
        if (theOther == null || getClass() != theOther.getClass()) {
            return false;
        }
        final Point point = (Point) theOther;
        return myX == point.myX && myY == point.myY;
    }

    /**
     * Generates a hash code for this Point.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(myX, myY);
    }

    /**
     * Returns a string representation of this Point.
     *
     * @return A string in the format "(x,y)".
     */
    @Override
    public String toString() {
        return "(" + myX + "," + myY + ")";
    }
}
