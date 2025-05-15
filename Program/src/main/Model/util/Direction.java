package main.Model.util;

/**
 * Enum representing the four cardinal directions.
 * Used for character movement and defining room connections.
 */
public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    /**
     * Gets the opposite direction.
     *
     * @return The opposite direction.
     */
    public Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST:  return WEST;
            case WEST:  return EAST;
            default:    throw new IllegalStateException("Unexpected direction: " + this);
        }
    }
}
