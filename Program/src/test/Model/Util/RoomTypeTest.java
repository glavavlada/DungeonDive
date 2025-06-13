package test.Model.Util;

import main.Model.util.RoomType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the RoomType enum.
 * Tests all enum values and methods to ensure 100% coverage.
 */
class RoomTypeTest {

    @Test
    void enumValues_containsAllExpectedRoomTypes() {
        RoomType[] roomTypes = RoomType.values();
        assertEquals(8, roomTypes.length, "RoomType enum should have exactly 8 values.");

        // Verify all expected room types are present
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.EMPTY),
                  "RoomType enum should contain EMPTY.");
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.TRAP),
                  "RoomType enum should contain TRAP.");
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.TREASURE),
                  "RoomType enum should contain TREASURE.");
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.MONSTER),
                  "RoomType enum should contain MONSTER.");
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.PILLAR),
                  "RoomType enum should contain PILLAR.");
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.ENTRANCE),
                  "RoomType enum should contain ENTRANCE.");
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.EXIT),
                  "RoomType enum should contain EXIT.");
        assertTrue(java.util.Arrays.asList(roomTypes).contains(RoomType.BOSS),
                  "RoomType enum should contain BOSS.");
    }

    @Test
    void getDisplayName_returnsCorrectNames() {
        assertEquals("Empty", RoomType.EMPTY.getDisplayName(),
                    "EMPTY getDisplayName should return 'Empty'.");
        assertEquals("Trap", RoomType.TRAP.getDisplayName(),
                    "TRAP getDisplayName should return 'Trap'.");
        assertEquals("Treasure", RoomType.TREASURE.getDisplayName(),
                    "TREASURE getDisplayName should return 'Treasure'.");
        assertEquals("Monster", RoomType.MONSTER.getDisplayName(),
                    "MONSTER getDisplayName should return 'Monster'.");
        assertEquals("Pillar", RoomType.PILLAR.getDisplayName(),
                    "PILLAR getDisplayName should return 'Pillar'.");
        assertEquals("Entrance", RoomType.ENTRANCE.getDisplayName(),
                    "ENTRANCE getDisplayName should return 'Entrance'.");
        assertEquals("Exit", RoomType.EXIT.getDisplayName(),
                    "EXIT getDisplayName should return 'Exit'.");
        assertEquals("Boss", RoomType.BOSS.getDisplayName(),
                    "BOSS getDisplayName should return 'Boss'.");
    }

    @Test
    void getDescription_returnsCorrectDescriptions() {
        assertEquals("An empty room with nothing of interest.",
                    RoomType.EMPTY.getDescription(),
                    "EMPTY should have correct description.");
        assertEquals("A room with a dangerous trap that damages the player.",
                    RoomType.TRAP.getDescription(),
                    "TRAP should have correct description.");
        assertEquals("A room containing a treasure chest with items or gold.",
                    RoomType.TREASURE.getDescription(),
                    "TREASURE should have correct description.");
        assertEquals("A room with monsters to defeat.",
                    RoomType.MONSTER.getDescription(),
                    "MONSTER should have correct description.");
        assertEquals("A room containing a magical pillar, possibly guarded.",
                    RoomType.PILLAR.getDescription(),
                    "PILLAR should have correct description.");
        assertEquals("The starting point of the dungeon.",
                    RoomType.ENTRANCE.getDescription(),
                    "ENTRANCE should have correct description.");
        assertEquals("The way out of the current dungeon level, or to the boss.",
                    RoomType.EXIT.getDescription(),
                    "EXIT should have correct description.");
        assertEquals("The final room where a powerful boss awaits.",
                    RoomType.BOSS.getDescription(),
                    "BOSS should have correct description.");
    }

    @Test
    void toString_returnsDisplayName() {
        assertEquals("Empty", RoomType.EMPTY.toString(),
                    "EMPTY toString should return display name.");
        assertEquals("Trap", RoomType.TRAP.toString(),
                    "TRAP toString should return display name.");
        assertEquals("Treasure", RoomType.TREASURE.toString(),
                    "TREASURE toString should return display name.");
        assertEquals("Monster", RoomType.MONSTER.toString(),
                    "MONSTER toString should return display name.");
        assertEquals("Pillar", RoomType.PILLAR.toString(),
                    "PILLAR toString should return display name.");
        assertEquals("Entrance", RoomType.ENTRANCE.toString(),
                    "ENTRANCE toString should return display name.");
        assertEquals("Exit", RoomType.EXIT.toString(),
                    "EXIT toString should return display name.");
        assertEquals("Boss", RoomType.BOSS.toString(),
                    "BOSS toString should return display name.");
    }

    @Test
    void valueOf_validRoomTypeNames_returnsCorrectEnum() {
        assertEquals(RoomType.EMPTY, RoomType.valueOf("EMPTY"),
                    "valueOf should return EMPTY for 'EMPTY' string.");
        assertEquals(RoomType.TRAP, RoomType.valueOf("TRAP"),
                    "valueOf should return TRAP for 'TRAP' string.");
        assertEquals(RoomType.TREASURE, RoomType.valueOf("TREASURE"),
                    "valueOf should return TREASURE for 'TREASURE' string.");
        assertEquals(RoomType.MONSTER, RoomType.valueOf("MONSTER"),
                    "valueOf should return MONSTER for 'MONSTER' string.");
        assertEquals(RoomType.PILLAR, RoomType.valueOf("PILLAR"),
                    "valueOf should return PILLAR for 'PILLAR' string.");
        assertEquals(RoomType.ENTRANCE, RoomType.valueOf("ENTRANCE"),
                    "valueOf should return ENTRANCE for 'ENTRANCE' string.");
        assertEquals(RoomType.EXIT, RoomType.valueOf("EXIT"),
                    "valueOf should return EXIT for 'EXIT' string.");
        assertEquals(RoomType.BOSS, RoomType.valueOf("BOSS"),
                    "valueOf should return BOSS for 'BOSS' string.");
    }

    @Test
    void valueOf_invalidRoomTypeName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> RoomType.valueOf("INVALID"),
                    "valueOf should throw IllegalArgumentException for invalid room type name.");
        assertThrows(IllegalArgumentException.class, () -> RoomType.valueOf("empty"),
                    "valueOf should throw IllegalArgumentException for lowercase room type name.");
        assertThrows(IllegalArgumentException.class, () -> RoomType.valueOf(""),
                    "valueOf should throw IllegalArgumentException for empty string.");
        assertThrows(IllegalArgumentException.class, () -> RoomType.valueOf("CHEST"),
                    "valueOf should throw IllegalArgumentException for old 'CHEST' name.");
        assertThrows(IllegalArgumentException.class, () -> RoomType.valueOf("CORRIDOR"),
                    "valueOf should throw IllegalArgumentException for non-existent room type.");
    }

    @Test
    void valueOf_nullRoomTypeName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> RoomType.valueOf(null),
                    "valueOf should throw NullPointerException for null input.");
    }

    @Test
    void name_returnsCorrectName() {
        assertEquals("EMPTY", RoomType.EMPTY.name(),
                    "EMPTY name should return 'EMPTY'.");
        assertEquals("TRAP", RoomType.TRAP.name(),
                    "TRAP name should return 'TRAP'.");
        assertEquals("TREASURE", RoomType.TREASURE.name(),
                    "TREASURE name should return 'TREASURE'.");
        assertEquals("MONSTER", RoomType.MONSTER.name(),
                    "MONSTER name should return 'MONSTER'.");
        assertEquals("PILLAR", RoomType.PILLAR.name(),
                    "PILLAR name should return 'PILLAR'.");
        assertEquals("ENTRANCE", RoomType.ENTRANCE.name(),
                    "ENTRANCE name should return 'ENTRANCE'.");
        assertEquals("EXIT", RoomType.EXIT.name(),
                    "EXIT name should return 'EXIT'.");
        assertEquals("BOSS", RoomType.BOSS.name(),
                    "BOSS name should return 'BOSS'.");
    }

    @Test
    void ordinal_returnsValidOrdinalValues() {
        RoomType[] types = RoomType.values();
        for (int i = 0; i < types.length; i++) {
            assertEquals(i, types[i].ordinal(),
                        "Ordinal of " + types[i].name() + " should be " + i);
        }

        // Test specific ordinals based on enum declaration order
        assertEquals(0, RoomType.EMPTY.ordinal(), "EMPTY should have ordinal 0.");
        assertEquals(1, RoomType.TRAP.ordinal(), "TRAP should have ordinal 1.");
        assertEquals(2, RoomType.TREASURE.ordinal(), "TREASURE should have ordinal 2.");
        assertEquals(3, RoomType.MONSTER.ordinal(), "MONSTER should have ordinal 3.");
        assertEquals(4, RoomType.PILLAR.ordinal(), "PILLAR should have ordinal 4.");
        assertEquals(5, RoomType.ENTRANCE.ordinal(), "ENTRANCE should have ordinal 5.");
        assertEquals(6, RoomType.EXIT.ordinal(), "EXIT should have ordinal 6.");
        assertEquals(7, RoomType.BOSS.ordinal(), "BOSS should have ordinal 7.");
    }

    @Test
    void compareTo_worksCorrectly() {
        // Test comparison based on ordinal values
        assertTrue(RoomType.EMPTY.compareTo(RoomType.TRAP) < 0,
                  "EMPTY should compare less than TRAP.");
        assertTrue(RoomType.TRAP.compareTo(RoomType.TREASURE) < 0,
                  "TRAP should compare less than TREASURE.");
        assertTrue(RoomType.ENTRANCE.compareTo(RoomType.EXIT) < 0,
                  "ENTRANCE should compare less than EXIT.");
        assertTrue(RoomType.EXIT.compareTo(RoomType.BOSS) < 0,
                  "EXIT should compare less than BOSS.");

        assertTrue(RoomType.BOSS.compareTo(RoomType.EMPTY) > 0,
                  "BOSS should compare greater than EMPTY.");

        // Test self comparison
        for (RoomType type : RoomType.values()) {
            assertEquals(0, type.compareTo(type),
                        "Room type should compare equal to itself.");
        }
    }

    @Test
    void equals_worksCorrectly() {
        for (RoomType type : RoomType.values()) {
            assertEquals(type, type, "Room type should equal itself.");
            assertTrue(type.equals(type), "Room type should equal itself using equals method.");
        }

        // Test inequality
        assertNotEquals(RoomType.EMPTY, RoomType.TRAP,
                       "Different room types should not be equal.");
        assertFalse(RoomType.EMPTY.equals(RoomType.TRAP),
                   "Different room types should not be equal using equals method.");

        // Test equals with non-enum objects
        assertFalse(RoomType.EMPTY.equals("EMPTY"),
                   "RoomType should not equal a String with the same name.");
        assertFalse(RoomType.EMPTY.equals(null),
                   "RoomType should not equal null.");
    }

    @Test
    void hashCode_isConsistent() {
        for (RoomType type : RoomType.values()) {
            int hashCode1 = type.hashCode();
            int hashCode2 = type.hashCode();
            assertEquals(hashCode1, hashCode2,
                        "Hash code should be consistent for " + type.name());
        }
    }

    @Test
    void enumConstantsAreImmutable() {
        // Verify that enum constants maintain their identity
        assertSame(RoomType.EMPTY, RoomType.valueOf("EMPTY"),
                  "Enum constants should maintain singleton property.");

        for (RoomType type : RoomType.values()) {
            assertSame(type, RoomType.valueOf(type.name()),
                      "Enum constant " + type.name() + " should maintain singleton property.");
        }
    }

    @Test
    void allGettersReturnNonNullValues() {
        for (RoomType type : RoomType.values()) {
            assertNotNull(type.getDisplayName(), type.name() + " getDisplayName should not return null.");
            assertNotNull(type.getDescription(), type.name() + " getDescription should not return null.");

            assertFalse(type.getDisplayName().isEmpty(), type.name() + " getDisplayName should not return empty string.");
            assertFalse(type.getDescription().isEmpty(), type.name() + " getDescription should not return empty string.");
        }
    }

    @Test
    void displayNamesAreProperlyFormatted() {
        for (RoomType type : RoomType.values()) {
            String displayName = type.getDisplayName();

            // Should start with uppercase letter
            assertTrue(Character.isUpperCase(displayName.charAt(0)),
                      type.name() + " display name should start with uppercase letter.");

            // Should not contain underscores (unlike enum name)
            assertFalse(displayName.contains("_"),
                       type.name() + " display name should not contain underscores.");
        }
    }

    @Test
    void descriptionsContainMeaningfulInformation() {
        for (RoomType type : RoomType.values()) {
            String description = type.getDescription();

            // Descriptions should be meaningful (more than just a few characters)
            assertTrue(description.length() > 10,
                      type.name() + " description should be meaningful and descriptive.");

            // Should end with proper punctuation
            assertTrue(description.endsWith("."),
                      type.name() + " description should end with a period.");
        }
    }

    @Test
    void toStringEqualsDisplayName() {
        for (RoomType type : RoomType.values()) {
            assertEquals(type.getDisplayName(), type.toString(),
                        type.name() + " toString should equal getDisplayName.");
        }
    }

    @Test
    void roomTypesRepresentDungeonConcepts() {
        // Verify that essential dungeon room types are represented
        String[] expectedRoomConcepts = {"Empty", "Trap", "Treasure", "Monster", "Pillar", "Entrance", "Exit", "Boss"};

        for (String concept : expectedRoomConcepts) {
            boolean found = false;
            for (RoomType type : RoomType.values()) {
                if (type.getDisplayName().equals(concept)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Should contain room type for dungeon concept: " + concept);
        }
    }

    @Test
    void specialRoomTypes_haveAppropriateDescriptions() {
        // Test that special room types have descriptions that match their purpose
        assertTrue(RoomType.ENTRANCE.getDescription().toLowerCase().contains("starting"),
                "ENTRANCE description should mention starting point.");
        assertTrue(RoomType.EXIT.getDescription().toLowerCase().contains("out") ||
                        RoomType.EXIT.getDescription().toLowerCase().contains("way"),
                "EXIT description should mention way out.");
        assertTrue(RoomType.BOSS.getDescription().toLowerCase().contains("boss") ||
                        RoomType.BOSS.getDescription().toLowerCase().contains("final"),
                "BOSS description should mention boss or final room.");
        assertTrue(RoomType.TRAP.getDescription().toLowerCase().contains("trap") ||
                        RoomType.TRAP.getDescription().toLowerCase().contains("damage"),
                "TRAP description should mention trap or damage.");
        assertTrue(RoomType.TREASURE.getDescription().toLowerCase().contains("treasure") ||
                        RoomType.TREASURE.getDescription().toLowerCase().contains("chest"),
                "TREASURE description should mention treasure or chest.");
        assertTrue(RoomType.MONSTER.getDescription().toLowerCase().contains("monster"),
                "MONSTER description should mention monsters.");
        assertTrue(RoomType.PILLAR.getDescription().toLowerCase().contains("pillar"),
                "PILLAR description should mention pillar.");
    }

    @Test
    void roomTypeCategories_canBeIdentified() {
        // Test that we can categorize room types appropriately

        // Navigation rooms
        assertTrue(RoomType.ENTRANCE.getDisplayName().equals("Entrance"),
                "Should have entrance room type.");
        assertTrue(RoomType.EXIT.getDisplayName().equals("Exit"),
                "Should have exit room type.");

        // Content rooms
        assertTrue(RoomType.EMPTY.getDisplayName().equals("Empty"),
                "Should have empty room type.");
        assertTrue(RoomType.TREASURE.getDisplayName().equals("Treasure"),
                "Should have treasure room type.");
        assertTrue(RoomType.PILLAR.getDisplayName().equals("Pillar"),
                "Should have pillar room type.");

        // Challenge rooms
        assertTrue(RoomType.TRAP.getDisplayName().equals("Trap"),
                "Should have trap room type.");
        assertTrue(RoomType.MONSTER.getDisplayName().equals("Monster"),
                "Should have monster room type.");
        assertTrue(RoomType.BOSS.getDisplayName().equals("Boss"),
                "Should have boss room type.");
    }

    @Test
    void getDeclaringClass_returnsRoomTypeClass() {
        // Test the getDeclaringClass method that all enums have
        for (RoomType type : RoomType.values()) {
            assertEquals(RoomType.class, type.getDeclaringClass(),
                    "getDeclaringClass should return RoomType.class for " + type.name());
        }
    }

    @Test
    void enumConstantSpecificTests() {
        // Test each enum constant individually to ensure all are covered
        for (RoomType type : RoomType.values()) {
            // Test that each enum constant can be retrieved by name
            assertEquals(type, RoomType.valueOf(type.name()),
                    "Should be able to retrieve " + type.name() + " by name.");

            // Test ordinal is non-negative
            assertTrue(type.ordinal() >= 0,
                    "Ordinal should be non-negative for " + type.name());

            // Test name is not null or empty
            assertNotNull(type.name(), "Name should not be null for enum constant.");
            assertFalse(type.name().trim().isEmpty(), "Name should not be empty for enum constant.");

            // Ensure toString doesn't throw exceptions
            assertDoesNotThrow(() -> type.toString(),
                    "toString should not throw exception for " + type.name());

            // Ensure hashCode doesn't throw exceptions
            assertDoesNotThrow(() -> type.hashCode(),
                    "hashCode should not throw exception for " + type.name());
        }
    }

    @Test
    void treasureRoomType_hasCorrectNaming() {
        // Verify that TREASURE is used instead of the old CHEST naming
        assertEquals("Treasure", RoomType.TREASURE.getDisplayName(),
                "Should use 'Treasure' as display name, not 'Chest'.");
        assertEquals("TREASURE", RoomType.TREASURE.name(),
                "Should use 'TREASURE' as enum name, not 'CHEST'.");

        // Verify the comment in the enum about renaming is reflected
        assertTrue(RoomType.TREASURE.getDescription().contains("treasure chest"),
                "Description should still mention 'treasure chest' for clarity.");
    }

    @Test
    void roomTypeDescriptions_areGameAppropriate() {
        // Verify descriptions are appropriate for a dungeon game context
        for (RoomType type : RoomType.values()) {
            String description = type.getDescription();

            // Should not contain inappropriate content (basic check)
            assertFalse(description.toLowerCase().contains("error"),
                    type.name() + " description should not contain error messages.");
            assertFalse(description.toLowerCase().contains("todo"),
                    type.name() + " description should not contain TODO markers.");
            assertFalse(description.toLowerCase().contains("fixme"),
                    type.name() + " description should not contain FIXME markers.");

            // Should be properly capitalized
            assertTrue(Character.isUpperCase(description.charAt(0)),
                    type.name() + " description should start with capital letter.");
        }
    }
}
