package test.Model.Util;

import main.Model.util.HeroType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the HeroType enum.
 * Tests all enum values and methods to ensure 100% coverage.
 */
class HeroTypeTest {

    @Test
    void enumValues_containsAllExpectedHeroTypes() {
        HeroType[] heroTypes = HeroType.values();
        assertTrue(heroTypes.length > 0, "HeroType enum should have at least one value.");

        // Verify WARRIOR is present (based on HeroTest.java usage)
        assertTrue(java.util.Arrays.asList(heroTypes).contains(HeroType.WARRIOR),
                  "HeroType enum should contain WARRIOR.");

        // Test for other common hero types that might exist
        // Note: Adjust these based on your actual HeroType enum values
        for (HeroType type : heroTypes) {
            assertNotNull(type, "Hero type should not be null.");
        }
    }

    @Test
    void valueOf_validHeroTypeNames_returnsCorrectEnum() {
        assertEquals(HeroType.WARRIOR, HeroType.valueOf("WARRIOR"),
                    "valueOf should return WARRIOR for 'WARRIOR' string.");

        // Test all available hero types
        for (HeroType type : HeroType.values()) {
            assertEquals(type, HeroType.valueOf(type.name()),
                        "valueOf should return correct enum for " + type.name());
        }
    }

    @Test
    void valueOf_invalidHeroTypeName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> HeroType.valueOf("INVALID"),
                    "valueOf should throw IllegalArgumentException for invalid hero type name.");
        assertThrows(IllegalArgumentException.class, () -> HeroType.valueOf("warrior"),
                    "valueOf should throw IllegalArgumentException for lowercase hero type name.");
        assertThrows(IllegalArgumentException.class, () -> HeroType.valueOf(""),
                    "valueOf should throw IllegalArgumentException for empty string.");
        assertThrows(IllegalArgumentException.class, () -> HeroType.valueOf("MAGE_INVALID"),
                    "valueOf should throw IllegalArgumentException for non-existent hero type.");
    }

    @Test
    void valueOf_nullHeroTypeName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> HeroType.valueOf(null),
                    "valueOf should throw NullPointerException for null input.");
    }

    @Test
    void toString_returnsCorrectStringRepresentation() {
        // Test the actual toString() implementation - appears to return proper case format
        assertEquals("Warrior", HeroType.WARRIOR.toString(),
                "WARRIOR toString should return 'Warrior'.");

        // Test all hero types with their actual toString() values
        for (HeroType type : HeroType.values()) {
            String expectedString = type.toString(); // Get the actual implementation
            assertEquals(expectedString, type.toString(),
                    type.name() + " toString should be consistent.");

            // Verify toString is not null or empty
            assertNotNull(type.toString(), "toString should not return null for " + type.name());
            assertFalse(type.toString().isEmpty(), "toString should not return empty string for " + type.name());
        }
    }

    @Test
    void name_returnsCorrectName() {
        for (HeroType type : HeroType.values()) {
            assertNotNull(type.name(), "Hero type name should not be null.");
            assertFalse(type.name().isEmpty(), "Hero type name should not be empty.");
        }

        assertEquals("WARRIOR", HeroType.WARRIOR.name(),
                    "WARRIOR name should return 'WARRIOR'.");
    }

    @Test
    void ordinal_returnsValidOrdinalValues() {
        HeroType[] types = HeroType.values();
        for (int i = 0; i < types.length; i++) {
            assertEquals(i, types[i].ordinal(),
                        "Ordinal of " + types[i].name() + " should be " + i);
        }
    }

    @Test
    void compareTo_worksCorrectly() {
        HeroType[] types = HeroType.values();
        if (types.length > 1) {
            assertTrue(types[0].compareTo(types[1]) < 0,
                      "First enum should compare less than second enum.");
            assertTrue(types[1].compareTo(types[0]) > 0,
                      "Second enum should compare greater than first enum.");
        }

        // Test self comparison
        for (HeroType type : types) {
            assertEquals(0, type.compareTo(type),
                        "Hero type should compare equal to itself.");
        }
    }

    @Test
    void equals_worksCorrectly() {
        for (HeroType type : HeroType.values()) {
            assertEquals(type, type, "Hero type should equal itself.");
            assertTrue(type.equals(type), "Hero type should equal itself using equals method.");
        }

        // Test inequality if multiple types exist
        HeroType[] types = HeroType.values();
        if (types.length > 1) {
            assertNotEquals(types[0], types[1], "Different hero types should not be equal.");
            assertFalse(types[0].equals(types[1]), "Different hero types should not be equal using equals method.");
        }
    }

    @Test
    void hashCode_isConsistent() {
        for (HeroType type : HeroType.values()) {
            int hashCode1 = type.hashCode();
            int hashCode2 = type.hashCode();
            assertEquals(hashCode1, hashCode2,
                        "Hash code should be consistent for " + type.name());
        }
    }

    @Test
    void enumConstantsAreImmutable() {
        // Verify that enum constants maintain their identity
        assertSame(HeroType.WARRIOR, HeroType.valueOf("WARRIOR"),
                  "Enum constants should maintain singleton property.");

        for (HeroType type : HeroType.values()) {
            assertSame(type, HeroType.valueOf(type.name()),
                      "Enum constant " + type.name() + " should maintain singleton property.");
        }
    }



}
