package main.Model.util;

/**
 * Enum representing the different types of rooms in the dungeon.
 */
public enum RoomType {
    EMPTY("Empty", "An empty room with nothing of interest."),
    TRAP("Trap", "A room with a dangerous trap that damages the player."),
    TREASURE("Treasure", "A room containing a treasure chest with items or gold."), // Renamed from Chest for clarity
    MONSTER("Monster", "A room with monsters to defeat."),
    PILLAR("Pillar", "A room containing a magical pillar, possibly guarded."),
    ENTRANCE("Entrance", "The starting point of the dungeon."),
    EXIT("Exit", "The way out of the current dungeon level, or to the boss."),
    BOSS("Boss", "The final room where a powerful boss awaits.");

    private final String myDisplayName;
    private final String myDescription;

    RoomType(final String theDisplayName, final String theDescription) {
        this.myDisplayName = theDisplayName;
        this.myDescription = theDescription;
    }

    public String getDisplayName() {
        return myDisplayName;
    }

    public String getDescription() {
        return myDescription;
    }

    @Override
    public String toString() {
        return myDisplayName;
    }
}
