package test.Model.Util;

import main.Model.util.PillarType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PillarType enum.
 * Tests all enum values and methods to ensure 100% coverage.
 */
class PillarTypeTest {

    @Test
    void enumValues_containsAllExpectedPillarTypes() {
        PillarType[] pillarTypes = PillarType.values();
        assertEquals(4, pillarTypes.length, "PillarType enum should have exactly 4 values.");

        // Verify all expected pillar types are present
        assertTrue(java.util.Arrays.asList(pillarTypes).contains(PillarType.ABSTRACTION),
                  "PillarType enum should contain ABSTRACTION.");
        assertTrue(java.util.Arrays.asList(pillarTypes).contains(PillarType.ENCAPSULATION),
                  "PillarType enum should contain ENCAPSULATION.");
        assertTrue(java.util.Arrays.asList(pillarTypes).contains(PillarType.INHERITANCE),
                  "PillarType enum should contain INHERITANCE.");
        assertTrue(java.util.Arrays.asList(pillarTypes).contains(PillarType.POLYMORPHISM),
                  "PillarType enum should contain POLYMORPHISM.");
    }

    @Test
    void getDisplayName_returnsCorrectNames() {
        assertEquals("Abstraction", PillarType.ABSTRACTION.getDisplayName(),
                    "ABSTRACTION getDisplayName should return 'Abstraction'.");
        assertEquals("Encapsulation", PillarType.ENCAPSULATION.getDisplayName(),
                    "ENCAPSULATION getDisplayName should return 'Encapsulation'.");
        assertEquals("Inheritance", PillarType.INHERITANCE.getDisplayName(),
                    "INHERITANCE getDisplayName should return 'Inheritance'.");
        assertEquals("Polymorphism", PillarType.POLYMORPHISM.getDisplayName(),
                    "POLYMORPHISM getDisplayName should return 'Polymorphism'.");
    }

    @Test
    void getDescription_returnsCorrectDescriptions() {
        assertEquals("Grants enhanced understanding of complex systems, increasing critical hit chance.",
                    PillarType.ABSTRACTION.getDescription(),
                    "ABSTRACTION should have correct description.");
        assertEquals("Shields the hero, increasing max health.",
                    PillarType.ENCAPSULATION.getDescription(),
                    "ENCAPSULATION should have correct description.");
        assertEquals("Hero learns old techniques, lowering mana needed for special attacks by 1.",
                    PillarType.INHERITANCE.getDescription(),
                    "INHERITANCE should have correct description.");
        assertEquals("Grants the power of other forms, increasing attack damage.",
                    PillarType.POLYMORPHISM.getDescription(),
                    "POLYMORPHISM should have correct description.");
    }

    @Test
    void toString_returnsDisplayName() {
        assertEquals("Abstraction", PillarType.ABSTRACTION.toString(),
                    "ABSTRACTION toString should return display name.");
        assertEquals("Encapsulation", PillarType.ENCAPSULATION.toString(),
                    "ENCAPSULATION toString should return display name.");
        assertEquals("Inheritance", PillarType.INHERITANCE.toString(),
                    "INHERITANCE toString should return display name.");
        assertEquals("Polymorphism", PillarType.POLYMORPHISM.toString(),
                    "POLYMORPHISM toString should return display name.");
    }

    @Test
    void valueOf_validPillarTypeNames_returnsCorrectEnum() {
        assertEquals(PillarType.ABSTRACTION, PillarType.valueOf("ABSTRACTION"),
                    "valueOf should return ABSTRACTION for 'ABSTRACTION' string.");
        assertEquals(PillarType.ENCAPSULATION, PillarType.valueOf("ENCAPSULATION"),
                    "valueOf should return ENCAPSULATION for 'ENCAPSULATION' string.");
        assertEquals(PillarType.INHERITANCE, PillarType.valueOf("INHERITANCE"),
                    "valueOf should return INHERITANCE for 'INHERITANCE' string.");
        assertEquals(PillarType.POLYMORPHISM, PillarType.valueOf("POLYMORPHISM"),
                    "valueOf should return POLYMORPHISM for 'POLYMORPHISM' string.");
    }

    @Test
    void valueOf_invalidPillarTypeName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PillarType.valueOf("INVALID"),
                    "valueOf should throw IllegalArgumentException for invalid pillar type name.");
        assertThrows(IllegalArgumentException.class, () -> PillarType.valueOf("abstraction"),
                    "valueOf should throw IllegalArgumentException for lowercase pillar type name.");
        assertThrows(IllegalArgumentException.class, () -> PillarType.valueOf(""),
                    "valueOf should throw IllegalArgumentException for empty string.");
        assertThrows(IllegalArgumentException.class, () -> PillarType.valueOf("COMPOSITION"),
                    "valueOf should throw IllegalArgumentException for non-existent pillar type.");
    }

    @Test
    void valueOf_nullPillarTypeName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PillarType.valueOf(null),
                    "valueOf should throw NullPointerException for null input.");
    }

    @Test
    void name_returnsCorrectName() {
        assertEquals("ABSTRACTION", PillarType.ABSTRACTION.name(),
                    "ABSTRACTION name should return 'ABSTRACTION'.");
        assertEquals("ENCAPSULATION", PillarType.ENCAPSULATION.name(),
                    "ENCAPSULATION name should return 'ENCAPSULATION'.");
        assertEquals("INHERITANCE", PillarType.INHERITANCE.name(),
                    "INHERITANCE name should return 'INHERITANCE'.");
        assertEquals("POLYMORPHISM", PillarType.POLYMORPHISM.name(),
                    "POLYMORPHISM name should return 'POLYMORPHISM'.");
    }

    @Test
    void ordinal_returnsValidOrdinalValues() {
        PillarType[] types = PillarType.values();
        for (int i = 0; i < types.length; i++) {
            assertEquals(i, types[i].ordinal(),
                        "Ordinal of " + types[i].name() + " should be " + i);
        }

        // Test specific ordinals based on enum declaration order
        assertEquals(0, PillarType.ABSTRACTION.ordinal(), "ABSTRACTION should have ordinal 0.");
        assertEquals(1, PillarType.ENCAPSULATION.ordinal(), "ENCAPSULATION should have ordinal 1.");
        assertEquals(2, PillarType.INHERITANCE.ordinal(), "INHERITANCE should have ordinal 2.");
        assertEquals(3, PillarType.POLYMORPHISM.ordinal(), "POLYMORPHISM should have ordinal 3.");
    }

    @Test
    void compareTo_worksCorrectly() {
        // Test comparison based on ordinal values
        assertTrue(PillarType.ABSTRACTION.compareTo(PillarType.ENCAPSULATION) < 0,
                  "ABSTRACTION should compare less than ENCAPSULATION.");
        assertTrue(PillarType.ENCAPSULATION.compareTo(PillarType.INHERITANCE) < 0,
                  "ENCAPSULATION should compare less than INHERITANCE.");
        assertTrue(PillarType.INHERITANCE.compareTo(PillarType.POLYMORPHISM) < 0,
                  "INHERITANCE should compare less than POLYMORPHISM.");

        assertTrue(PillarType.POLYMORPHISM.compareTo(PillarType.ABSTRACTION) > 0,
                  "POLYMORPHISM should compare greater than ABSTRACTION.");

        // Test self comparison
        for (PillarType type : PillarType.values()) {
            assertEquals(0, type.compareTo(type),
                        "Pillar type should compare equal to itself.");
        }
    }

    @Test
    void equals_worksCorrectly() {
        for (PillarType type : PillarType.values()) {
            assertEquals(type, type, "Pillar type should equal itself.");
            assertTrue(type.equals(type), "Pillar type should equal itself using equals method.");
        }

        // Test inequality
        assertNotEquals(PillarType.ABSTRACTION, PillarType.ENCAPSULATION,
                       "Different pillar types should not be equal.");
        assertFalse(PillarType.ABSTRACTION.equals(PillarType.ENCAPSULATION),
                   "Different pillar types should not be equal using equals method.");

        // Test equals with non-enum objects
        assertFalse(PillarType.ABSTRACTION.equals("ABSTRACTION"),
                   "PillarType should not equal a String with the same name.");
        assertFalse(PillarType.ABSTRACTION.equals(null),
                   "PillarType should not equal null.");
    }

    @Test
    void hashCode_isConsistent() {
        for (PillarType type : PillarType.values()) {
            int hashCode1 = type.hashCode();
            int hashCode2 = type.hashCode();
            assertEquals(hashCode1, hashCode2,
                        "Hash code should be consistent for " + type.name());
        }
    }

    @Test
    void enumConstantsAreImmutable() {
        // Verify that enum constants maintain their identity
        assertSame(PillarType.ABSTRACTION, PillarType.valueOf("ABSTRACTION"),
                  "Enum constants should maintain singleton property.");

        for (PillarType type : PillarType.values()) {
            assertSame(type, PillarType.valueOf(type.name()),
                      "Enum constant " + type.name() + " should maintain singleton property.");
        }
    }

    @Test
    void allGettersReturnNonNullValues() {
        for (PillarType type : PillarType.values()) {
            assertNotNull(type.getDisplayName(), type.name() + " getDisplayName should not return null.");
            assertNotNull(type.getDescription(), type.name() + " getDescription should not return null.");

            assertFalse(type.getDisplayName().isEmpty(), type.name() + " getDisplayName should not return empty string.");
            assertFalse(type.getDescription().isEmpty(), type.name() + " getDescription should not return empty string.");
        }
    }

    @Test
    void displayNamesAreProperlyFormatted() {
        for (PillarType type : PillarType.values()) {
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
    void descriptionsContainGameplayInformation() {
        for (PillarType type : PillarType.values()) {
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
        for (PillarType type : PillarType.values()) {
            assertEquals(type.getDisplayName(), type.toString(),
                        type.name() + " toString should equal getDisplayName.");
        }
    }

    @Test
    void pillarTypesRepresentOOPConcepts() {
        // Verify that all four fundamental OOP pillars are represented
        String[] expectedConcepts = {"Abstraction", "Encapsulation", "Inheritance", "Polymorphism"};

        for (String concept : expectedConcepts) {
            boolean found = false;
            for (PillarType type : PillarType.values()) {
                if (type.getDisplayName().equals(concept)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Should contain pillar for OOP concept: " + concept);
        }
    }
}
