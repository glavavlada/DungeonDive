package test.Model.Util;

import main.Model.util.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Direction enum.
 * Tests all enum values and the opposite() method to ensure 100% coverage.
 */
class DirectionTest {

    @Test
    void opposite_north_returnsSouth() {
        assertEquals(Direction.SOUTH, Direction.NORTH.opposite(),
                    "NORTH opposite should be SOUTH.");
    }

    @Test
    void opposite_south_returnsNorth() {
        assertEquals(Direction.NORTH, Direction.SOUTH.opposite(),
                    "SOUTH opposite should be NORTH.");
    }

    @Test
    void opposite_east_returnsWest() {
        assertEquals(Direction.WEST, Direction.EAST.opposite(),
                    "EAST opposite should be WEST.");
    }

    @Test
    void opposite_west_returnsEast() {
        assertEquals(Direction.EAST, Direction.WEST.opposite(),
                    "WEST opposite should be EAST.");
    }

    @Test
    void enumValues_containsAllExpectedDirections() {
        Direction[] directions = Direction.values();
        assertEquals(4, directions.length, "Direction enum should have exactly 4 values.");

        // Verify all expected directions are present
        assertTrue(java.util.Arrays.asList(directions).contains(Direction.NORTH),
                  "Direction enum should contain NORTH.");
        assertTrue(java.util.Arrays.asList(directions).contains(Direction.SOUTH),
                  "Direction enum should contain SOUTH.");
        assertTrue(java.util.Arrays.asList(directions).contains(Direction.EAST),
                  "Direction enum should contain EAST.");
        assertTrue(java.util.Arrays.asList(directions).contains(Direction.WEST),
                  "Direction enum should contain WEST.");
    }

    @Test
    void valueOf_validDirectionNames_returnsCorrectEnum() {
        assertEquals(Direction.NORTH, Direction.valueOf("NORTH"),
                    "valueOf should return NORTH for 'NORTH' string.");
        assertEquals(Direction.SOUTH, Direction.valueOf("SOUTH"),
                    "valueOf should return SOUTH for 'SOUTH' string.");
        assertEquals(Direction.EAST, Direction.valueOf("EAST"),
                    "valueOf should return EAST for 'EAST' string.");
        assertEquals(Direction.WEST, Direction.valueOf("WEST"),
                    "valueOf should return WEST for 'WEST' string.");
    }

    @Test
    void valueOf_invalidDirectionName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Direction.valueOf("INVALID"),
                    "valueOf should throw IllegalArgumentException for invalid direction name.");
        assertThrows(IllegalArgumentException.class, () -> Direction.valueOf("north"),
                    "valueOf should throw IllegalArgumentException for lowercase direction name.");
        assertThrows(IllegalArgumentException.class, () -> Direction.valueOf(""),
                    "valueOf should throw IllegalArgumentException for empty string.");
    }

    @Test
    void valueOf_nullDirectionName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Direction.valueOf(null),
                    "valueOf should throw NullPointerException for null input.");
    }

    @Test
    void opposite_doubleOpposite_returnsOriginal() {
        // Test that applying opposite twice returns the original direction
        assertEquals(Direction.NORTH, Direction.NORTH.opposite().opposite(),
                    "Double opposite of NORTH should return NORTH.");
        assertEquals(Direction.SOUTH, Direction.SOUTH.opposite().opposite(),
                    "Double opposite of SOUTH should return SOUTH.");
        assertEquals(Direction.EAST, Direction.EAST.opposite().opposite(),
                    "Double opposite of EAST should return EAST.");
        assertEquals(Direction.WEST, Direction.WEST.opposite().opposite(),
                    "Double opposite of WEST should return WEST.");
    }

    @Test
    void toString_returnsCorrectStringRepresentation() {
        assertEquals("NORTH", Direction.NORTH.toString(),
                    "NORTH toString should return 'NORTH'.");
        assertEquals("SOUTH", Direction.SOUTH.toString(),
                    "SOUTH toString should return 'SOUTH'.");
        assertEquals("EAST", Direction.EAST.toString(),
                    "EAST toString should return 'EAST'.");
        assertEquals("WEST", Direction.WEST.toString(),
                    "WEST toString should return 'WEST'.");
    }
}
