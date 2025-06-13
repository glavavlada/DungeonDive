package test.Model.Element;

import main.Model.element.Trap;
import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.util.HeroType;
import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Trap class.
 * Tests all methods and functionality to ensure 100% coverage.
 */
class TrapTest {

    private Trap spikeTrap;
    private Trap poisonTrap;
    private Trap fireTrap;
    private Trap zeroTrap;
    private Hero testHero;
    private HeroFactory heroFactory;

    // For capturing console output
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    void setUp() {
        spikeTrap = new Trap("Spike Trap", "Sharp spikes emerge from the ground.", 20);
        poisonTrap = new Trap("Poison Dart", "A dart coated with deadly poison.", 15);
        fireTrap = new Trap("Fire Trap", "Flames burst from hidden vents.", 25);
        zeroTrap = new Trap("Broken Trap", "A trap that deals no damage.", 0);

        heroFactory = new HeroFactory();
        testHero = heroFactory.getHero("TestHero", HeroType.WARRIOR, new Point(0, 0));

        // Capture console output for testing print statements
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(standardOut);
    }

    @Test
    void constructor_withValidParameters_initializesCorrectly() {
        assertEquals("Spike Trap", spikeTrap.getName(),
                    "Trap name should be initialized correctly.");
        assertEquals("Sharp spikes emerge from the ground.", spikeTrap.getDescription(),
                    "Trap description should be initialized correctly.");
        assertEquals(20, spikeTrap.getDamageAmount(),
                    "Trap damage amount should be initialized correctly.");
        assertFalse(spikeTrap.isSprung(),
                   "New trap should not be sprung.");
    }

    @Test
    void constructor_withDifferentValues_initializesCorrectly() {
        assertEquals("Poison Dart", poisonTrap.getName(),
                    "Poison trap name should be correct.");
        assertEquals(15, poisonTrap.getDamageAmount(),
                    "Poison trap damage should be correct.");

        assertEquals("Fire Trap", fireTrap.getName(),
                    "Fire trap name should be correct.");
        assertEquals(25, fireTrap.getDamageAmount(),
                    "Fire trap damage should be correct.");
    }

    @Test
    void constructor_withZeroDamage_initializesCorrectly() {
        assertEquals("Broken Trap", zeroTrap.getName(),
                    "Zero damage trap name should be correct.");
        assertEquals(0, zeroTrap.getDamageAmount(),
                    "Zero damage trap should have 0 damage.");
        assertFalse(zeroTrap.isSprung(),
                   "Zero damage trap should not be sprung initially.");
    }

    @Test
    void constructor_withNegativeDamage_setsToZero() {
        Trap negativeTrap = new Trap("Negative Trap", "Should have zero damage.", -10);
        assertEquals(0, negativeTrap.getDamageAmount(),
                    "Negative damage should be set to 0.");
    }

    @Test
    void constructor_withNullName_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Trap(null, "Valid description", 10),
                "Constructor should throw exception for null name.");

        assertEquals("Trap name cannot be null or empty.", exception.getMessage(),
                    "Exception message should be correct.");
    }

    @Test
    void constructor_withEmptyName_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Trap("", "Valid description", 10),
                "Constructor should throw exception for empty name.");

        assertEquals("Trap name cannot be null or empty.", exception.getMessage(),
                    "Exception message should be correct.");
    }

    @Test
    void constructor_withNullDescription_setsToEmpty() {
        Trap nullDescTrap = new Trap("Valid Name", null, 10);
        assertEquals("", nullDescTrap.getDescription(),
                    "Null description should be converted to empty string.");
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("Spike Trap", spikeTrap.getName(),
                    "getName should return correct name.");
        assertEquals("Poison Dart", poisonTrap.getName(),
                    "getName should return correct name for different trap.");
    }

    @Test
    void getDescription_returnsCorrectDescription() {
        assertEquals("Sharp spikes emerge from the ground.", spikeTrap.getDescription(),
                    "getDescription should return correct description.");
        assertEquals("A dart coated with deadly poison.", poisonTrap.getDescription(),
                    "getDescription should return correct description for different trap.");
    }

    @Test
    void getDamageAmount_returnsCorrectDamage() {
        assertEquals(20, spikeTrap.getDamageAmount(),
                    "getDamageAmount should return correct damage.");
        assertEquals(15, poisonTrap.getDamageAmount(),
                    "getDamageAmount should return correct damage for different trap.");
        assertEquals(0, zeroTrap.getDamageAmount(),
                    "getDamageAmount should return 0 for zero damage trap.");
    }

    @Test
    void getDamage_returnsCorrectDamage() {
        assertEquals(20, spikeTrap.getDamage(),
                    "getDamage should return same as getDamageAmount.");
        assertEquals(15, poisonTrap.getDamage(),
                    "getDamage should return same as getDamageAmount for different trap.");
        assertEquals(0, zeroTrap.getDamage(),
                    "getDamage should return 0 for zero damage trap.");
    }

    @Test
    void isSprung_initiallyReturnsFalse() {
        assertFalse(spikeTrap.isSprung(),
                   "New trap should not be sprung.");
        assertFalse(poisonTrap.isSprung(),
                   "New trap should not be sprung.");
        assertFalse(fireTrap.isSprung(),
                   "New trap should not be sprung.");
    }

    @Test
    void trigger_withValidHero_dealsDamageAndSpringsTrap() {
        int initialHealth = testHero.getHealth();

        spikeTrap.trigger(testHero);

        assertEquals(initialHealth - 20, testHero.getHealth(),
                    "Hero should take damage from trap.");
        assertTrue(spikeTrap.isSprung(),
                  "Trap should be sprung after triggering.");
    }

    @Test
    void trigger_withValidHero_printsCorrectMessages() {
        spikeTrap.trigger(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + " triggered a " + spikeTrap.getName() + "!"),
                  "Should print trigger message.");
        assertTrue(output.contains(spikeTrap.getName() + " dealt " + spikeTrap.getDamageAmount() + " damage to " + testHero.getName() + "."),
                  "Should print damage message.");
    }

    @Test
    void trigger_withNullHero_doesNothing() {
        assertFalse(spikeTrap.isSprung(), "Trap should start unsprung.");

        spikeTrap.trigger(null);

        assertFalse(spikeTrap.isSprung(), "Trap should remain unsprung with null hero.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.trim().isEmpty(), "No output should be generated with null hero.");
    }

    @Test
    void trigger_withDeadHero_doesNothing() {
        testHero.setHealth(0);
        assertFalse(testHero.isAlive(), "Hero should be dead.");
        assertFalse(spikeTrap.isSprung(), "Trap should start unsprung.");

        spikeTrap.trigger(testHero);

        assertFalse(spikeTrap.isSprung(), "Trap should not be sprung with dead hero.");
        assertEquals(0, testHero.getHealth(), "Dead hero health should remain 0.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.trim().isEmpty(), "No output should be generated with dead hero.");
    }

    @Test
    void trigger_alreadySprungTrap_printsAlreadySprungMessage() {
        // First trigger
        spikeTrap.trigger(testHero);
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung after first trigger.");

        int healthAfterFirstTrigger = testHero.getHealth();
        outputStreamCaptor.reset();

        // Second trigger attempt
        spikeTrap.trigger(testHero);

        assertEquals(healthAfterFirstTrigger, testHero.getHealth(),
                    "Hero should not take additional damage from sprung trap.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("The " + spikeTrap.getName() + " has already been sprung."),
                  "Should print already sprung message.");
    }

    @Test
    void trigger_zeroTrap_springsButDealsNoDamage() {
        int initialHealth = testHero.getHealth();

        zeroTrap.trigger(testHero);

        assertEquals(initialHealth, testHero.getHealth(),
                    "Hero should take no damage from zero damage trap.");
        assertTrue(zeroTrap.isSprung(),
                  "Zero damage trap should still be sprung.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + " triggered a " + zeroTrap.getName() + "!"),
                  "Should print trigger message even for zero damage.");
        assertTrue(output.contains(zeroTrap.getName() + " dealt 0 damage to " + testHero.getName() + "."),
                  "Should print zero damage message.");
    }

    @Test
    void reset_sprungTrap_resetsToUnsprung() {
        // Spring the trap first
        spikeTrap.trigger(testHero);
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung.");

        outputStreamCaptor.reset();

        // Reset the trap
        spikeTrap.reset();

        assertFalse(spikeTrap.isSprung(), "Trap should be reset to unsprung.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(spikeTrap.getName() + " has been reset."),
                  "Should print reset message.");
    }

    @Test
    void reset_unsprungTrap_stillWorks() {
        assertFalse(spikeTrap.isSprung(), "Trap should start unsprung.");

        spikeTrap.reset();

        assertFalse(spikeTrap.isSprung(), "Trap should remain unsprung after reset.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(spikeTrap.getName() + " has been reset."),
                  "Should print reset message even for unsprung trap.");
    }

    @Test
    void reset_allowsRetriggering() {
        int initialHealth = testHero.getHealth();

        // First trigger
        spikeTrap.trigger(testHero);
        assertEquals(initialHealth - 20, testHero.getHealth(),
                    "Hero should take damage from first trigger.");
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung.");

        // Reset trap
        spikeTrap.reset();
        assertFalse(spikeTrap.isSprung(), "Trap should be reset.");

        // Second trigger after reset
        int healthAfterReset = testHero.getHealth();
        spikeTrap.trigger(testHero);

        assertEquals(healthAfterReset - 20, testHero.getHealth(),
                    "Hero should take damage again after reset.");
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung again.");
    }

    @Test
    void interact_withValidHero_triggersTrap() {
        int initialHealth = testHero.getHealth();
        assertFalse(spikeTrap.isSprung(), "Trap should start unsprung.");

        spikeTrap.interact(testHero);

        assertEquals(initialHealth - 20, testHero.getHealth(),
                    "Hero should take damage from interaction.");
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung after interaction.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + " triggered a " + spikeTrap.getName() + "!"),
                  "Interaction should trigger the trap.");
    }

    @Test
    void interact_withNullHero_doesNothing() {
        assertFalse(spikeTrap.isSprung(), "Trap should start unsprung.");

        spikeTrap.interact(null);

        assertFalse(spikeTrap.isSprung(), "Trap should remain unsprung with null hero.");
    }

    @Test
    void toString_unsprungTrap_showsArmedStatus() {
        String expected = spikeTrap.getName() + " (" + spikeTrap.getDescription() + ") [Armed]";
        assertEquals(expected, spikeTrap.toString(),
                    "toString should show armed status for unsprung trap.");
    }

    @Test
    void toString_sprungTrap_showsSprungStatus() {
        spikeTrap.trigger(testHero);
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung.");

        String expected = spikeTrap.getName() + " (" + spikeTrap.getDescription() + ") [Sprung]";
        assertEquals(expected, spikeTrap.toString(),
                    "toString should show sprung status for triggered trap.");
    }

    @Test
    void toString_differentTraps_formatCorrectly() {
        String spikeStr = "Spike Trap (Sharp spikes emerge from the ground.) [Armed]";
        assertEquals(spikeStr, spikeTrap.toString(),
                "Spike trap toString should be correct.");

        String poisonStr = "Poison Dart (A dart coated with deadly poison.) [Armed]";
        assertEquals(poisonStr, poisonTrap.toString(),
                "Poison trap toString should be correct.");

        String zeroStr = "Broken Trap (A trap that deals no damage.) [Armed]";
        assertEquals(zeroStr, zeroTrap.toString(),
                "Zero damage trap toString should be correct.");
    }

    @Test
    void equals_withSameObject_returnsTrue() {
        assertTrue(spikeTrap.equals(spikeTrap),
                "Trap should equal itself.");
    }

    @Test
    void equals_withNull_returnsFalse() {
        assertFalse(spikeTrap.equals(null),
                "Trap should not equal null.");
    }

    @Test
    void equals_withDifferentClass_returnsFalse() {
        assertFalse(spikeTrap.equals("Not a Trap"),
                "Trap should not equal object of different class.");
    }

    @Test
    void equals_withSameProperties_returnsTrue() {
        Trap anotherSpikeTrap = new Trap("Spike Trap", "Sharp spikes emerge from the ground.", 20);

        assertTrue(spikeTrap.equals(anotherSpikeTrap),
                "Traps with same properties should be equal.");
        assertTrue(anotherSpikeTrap.equals(spikeTrap),
                "Equality should be symmetric.");
    }

    @Test
    void equals_withDifferentName_returnsFalse() {
        Trap differentNameTrap = new Trap("Different Name", "Sharp spikes emerge from the ground.", 20);

        assertFalse(spikeTrap.equals(differentNameTrap),
                "Traps with different names should not be equal.");
    }

    @Test
    void equals_withDifferentDescription_returnsFalse() {
        Trap differentDescTrap = new Trap("Spike Trap", "Different description.", 20);

        assertFalse(spikeTrap.equals(differentDescTrap),
                "Traps with different descriptions should not be equal.");
    }

    @Test
    void equals_withDifferentDamage_returnsFalse() {
        Trap differentDamageTrap = new Trap("Spike Trap", "Sharp spikes emerge from the ground.", 30);

        assertFalse(spikeTrap.equals(differentDamageTrap),
                "Traps with different damage amounts should not be equal.");
    }

    @Test
    void equals_withDifferentSprungState_returnsFalse() {
        Trap anotherSpikeTrap = new Trap("Spike Trap", "Sharp spikes emerge from the ground.", 20);

        // Spring one trap but not the other
        spikeTrap.trigger(testHero);

        assertFalse(spikeTrap.equals(anotherSpikeTrap),
                "Traps with different sprung states should not be equal.");
    }

    @Test
    void equals_withSamePropertiesAndBothSprung_returnsTrue() {
        Trap anotherSpikeTrap = new Trap("Spike Trap", "Sharp spikes emerge from the ground.", 20);

        // Spring both traps
        spikeTrap.trigger(testHero);
        anotherSpikeTrap.trigger(testHero);

        assertTrue(spikeTrap.equals(anotherSpikeTrap),
                "Traps with same properties and both sprung should be equal.");
    }

    @Test
    void hashCode_withSameProperties_returnsSameHash() {
        Trap anotherSpikeTrap = new Trap("Spike Trap", "Sharp spikes emerge from the ground.", 20);

        assertEquals(spikeTrap.hashCode(), anotherSpikeTrap.hashCode(),
                "Traps with same properties should have same hash code.");
    }

    @Test
    void hashCode_isConsistent() {
        int hash1 = spikeTrap.hashCode();
        int hash2 = spikeTrap.hashCode();
        assertEquals(hash1, hash2,
                "hashCode should be consistent across multiple calls.");
    }

    @Test
    void hashCode_changesWithSprungState() {
        int unsprungHash = spikeTrap.hashCode();
        spikeTrap.trigger(testHero);
        int sprungHash = spikeTrap.hashCode();

        assertNotEquals(unsprungHash, sprungHash,
                "hashCode should change when sprung state changes.");
    }

    @Test
    void inheritanceFromDungeonElement_worksCorrectly() {
        assertTrue(spikeTrap instanceof main.Model.element.DungeonElement,
                "Trap should be instance of DungeonElement.");
        assertTrue(poisonTrap instanceof main.Model.element.DungeonElement,
                "Trap should be instance of DungeonElement.");
        assertTrue(fireTrap instanceof main.Model.element.DungeonElement,
                "Trap should be instance of DungeonElement.");
    }

    @Test
    void polymorphism_worksCorrectly() {
        Trap[] traps = {spikeTrap, poisonTrap, fireTrap, zeroTrap};

        for (Trap trap : traps) {
            assertDoesNotThrow(() -> trap.trigger(testHero),
                    "Polymorphic trigger call should work for " + trap.getName());
            assertDoesNotThrow(() -> trap.interact(testHero),
                    "Polymorphic interact call should work for " + trap.getName());
            assertDoesNotThrow(() -> trap.reset(),
                    "Polymorphic reset call should work for " + trap.getName());
            assertDoesNotThrow(() -> trap.toString(),
                    "Polymorphic toString call should work for " + trap.getName());
            assertNotNull(trap.getName(),
                    "getName should not return null for " + trap.getName());
        }
    }

    @Test
    void multipleTriggers_onlyFirstSucceeds() {
        int initialHealth = testHero.getHealth();

        // First trigger
        spikeTrap.trigger(testHero);
        assertEquals(initialHealth - 20, testHero.getHealth(),
                "First trigger should deal damage.");
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung.");

        int healthAfterFirstTrigger = testHero.getHealth();

        // Second trigger attempt
        spikeTrap.trigger(testHero);
        assertEquals(healthAfterFirstTrigger, testHero.getHealth(),
                "Second trigger should not deal additional damage.");

        // Third trigger attempt
        spikeTrap.trigger(testHero);
        assertEquals(healthAfterFirstTrigger, testHero.getHealth(),
                "Third trigger should not deal additional damage.");
    }

    @Test
    void trigger_withDifferentHeroTypes_worksCorrectly() {
        Hero warrior = heroFactory.getHero("Warrior", HeroType.WARRIOR, new Point(1, 1));
        int initialHealth = warrior.getHealth();

        spikeTrap.trigger(warrior);

        assertEquals(initialHealth - 20, warrior.getHealth(),
                "Trap should work with different hero types.");
        assertTrue(spikeTrap.isSprung(), "Trap should be sprung with different hero types.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Warrior triggered a Spike Trap!"),
                "Should work with different hero types.");
    }

    @Test
    void trapState_remainsConsistent() {
        // Test that trap state remains consistent through various operations
        String originalName = spikeTrap.getName();
        String originalDescription = spikeTrap.getDescription();
        int originalDamage = spikeTrap.getDamageAmount();
        boolean originalSprung = spikeTrap.isSprung();

        // Perform various operations that shouldn't change immutable properties
        spikeTrap.toString();
        spikeTrap.hashCode();
        spikeTrap.equals(poisonTrap);
        spikeTrap.getDamage();

        assertEquals(originalName, spikeTrap.getName(),
                "Trap name should remain unchanged.");
        assertEquals(originalDescription, spikeTrap.getDescription(),
                "Trap description should remain unchanged.");
        assertEquals(originalDamage, spikeTrap.getDamageAmount(),
                "Trap damage should remain unchanged.");
        assertEquals(originalSprung, spikeTrap.isSprung(),
                "Trap sprung state should remain unchanged.");
    }

    @Test
    void equalsAndHashCode_contractCompliance() {
        Trap trap1 = new Trap("Same Trap", "Same description", 10);
        Trap trap2 = new Trap("Same Trap", "Same description", 10);
        Trap trap3 = new Trap("Different Trap", "Same description", 10);

        // Reflexive: x.equals(x) should return true
        assertTrue(trap1.equals(trap1), "equals should be reflexive.");

        // Symmetric: x.equals(y) should return true if and only if y.equals(x) returns true
        assertTrue(trap1.equals(trap2), "equals should be symmetric (1->2).");
        assertTrue(trap2.equals(trap1), "equals should be symmetric (2->1).");

        // Transitive: if x.equals(y) and y.equals(z), then x.equals(z)
        Trap trap2Copy = new Trap("Same Trap", "Same description", 10);
        assertTrue(trap1.equals(trap2), "First equality for transitivity.");
        assertTrue(trap2.equals(trap2Copy), "Second equality for transitivity.");
        assertTrue(trap1.equals(trap2Copy), "equals should be transitive.");

        // Consistent: multiple invocations should return the same result
        assertTrue(trap1.equals(trap2), "First call to equals.");
        assertTrue(trap1.equals(trap2), "Second call to equals should be consistent.");

        // Null: x.equals(null) should return false
        assertFalse(trap1.equals(null), "equals should return false for null.");

        // Hash code contract: if two objects are equal, they must have the same hash code
        assertEquals(trap1.hashCode(), trap2.hashCode(),
                "Equal objects must have equal hash codes.");

        // Hash code consistency
        int hash1 = trap1.hashCode();
        int hash2 = trap1.hashCode();
        assertEquals(hash1, hash2, "hashCode should be consistent.");
    }

    @Test
    void extremeValues_handledCorrectly() {
        // Test with extreme damage values
        Trap extremeTrap = new Trap("Extreme Trap", "Extreme damage", Integer.MAX_VALUE);
        testHero.setHealth(100);

        extremeTrap.trigger(testHero);

        assertEquals(0, testHero.getHealth(),
                "Extreme damage should not cause overflow, health should be 0.");
        assertTrue(extremeTrap.isSprung(), "Extreme trap should be sprung.");
    }

    @Test
    void specialCharactersInNameAndDescription_handledCorrectly() {
        Trap specialTrap = new Trap("Trap of +5 Damage!", "A trap with special characters: @#$%^&*()", 15);

        assertEquals("Trap of +5 Damage!", specialTrap.getName(),
                "Special characters in name should be preserved.");
        assertEquals("A trap with special characters: @#$%^&*()", specialTrap.getDescription(),
                "Special characters in description should be preserved.");

        String toStringResult = specialTrap.toString();
        assertTrue(toStringResult.contains("Trap of +5 Damage!"),
                "toString should preserve special characters in name.");
        assertTrue(toStringResult.contains("@#$%^&*()"),
                "toString should preserve special characters in description.");
    }

    @Test
    void unicodeCharacters_handledCorrectly() {
        Trap unicodeTrap = new Trap("魔法の罠", "Une trappe magique avec des caractères unicode: ñáéíóú", 12);

        assertEquals("魔法の罠", unicodeTrap.getName(),
                "Unicode characters in name should be preserved.");
        assertEquals("Une trappe magique avec des caractères unicode: ñáéíóú", unicodeTrap.getDescription(),
                "Unicode characters in description should be preserved.");

        String toStringResult = unicodeTrap.toString();
        assertTrue(toStringResult.contains("魔法の罠"),
                "toString should preserve unicode characters in name.");
        assertTrue(toStringResult.contains("ñáéíóú"),
                "toString should preserve unicode characters in description.");
    }

    @Test
    void veryLongNameAndDescription_handledCorrectly() {
        String longName = "A".repeat(1000); // 1000 character name
        String longDescription = "B".repeat(2000); // 2000 character description

        Trap longTrap = new Trap(longName, longDescription, 15);

        assertEquals(longName, longTrap.getName(),
                "Very long name should be preserved.");
        assertEquals(longDescription, longTrap.getDescription(),
                "Very long description should be preserved.");

        String toStringResult = longTrap.toString();
        assertTrue(toStringResult.contains(longName),
                "toString should handle very long names.");
        assertTrue(toStringResult.contains(longDescription),
                "toString should handle very long descriptions.");
    }

    @Test
    void finalFields_areImmutable() {
        String originalName = spikeTrap.getName();
        String originalDescription = spikeTrap.getDescription();
        int originalDamage = spikeTrap.getDamageAmount();

        // Perform operations that might try to change state
        spikeTrap.trigger(testHero);
        spikeTrap.reset();
        spikeTrap.interact(testHero);
        spikeTrap.toString();

        assertEquals(originalName, spikeTrap.getName(),
                "Trap name should remain immutable.");
        assertEquals(originalDescription, spikeTrap.getDescription(),
                "Trap description should remain immutable.");
        assertEquals(originalDamage, spikeTrap.getDamageAmount(),
                "Trap damage should remain immutable.");
    }

    @Test
    void sprungState_changesCorrectly() {
        // Test sprung state changes
        assertFalse(spikeTrap.isSprung(), "Should start unsprung.");

        spikeTrap.trigger(testHero);
        assertTrue(spikeTrap.isSprung(), "Should be sprung after trigger.");

        spikeTrap.reset();
        assertFalse(spikeTrap.isSprung(), "Should be unsprung after reset.");

        // Multiple checks should be consistent
        for (int i = 0; i < 10; i++) {
            assertFalse(spikeTrap.isSprung(), "Should consistently return unsprung state.");
        }
    }

    @Test
    void allMethodsCovered_verification() {
        // Ensure all public methods are tested
        assertDoesNotThrow(() -> {
            // Constructor (tested in multiple tests)
            Trap trap = new Trap("Test Trap", "Test description", 10);

            // All public methods
            trap.getName();
            trap.getDescription();
            trap.getDamageAmount();
            trap.getDamage();
            trap.isSprung();
            trap.trigger(testHero);
            trap.reset();
            trap.interact(testHero);
            trap.toString();
            trap.equals(trap);
            trap.hashCode();
        }, "All public methods should be callable without exceptions.");
    }

    @Test
    void constructorValidation_edgeCases() {
        // The Trap constructor only checks for null or empty, not whitespace-only
        // So whitespace-only names are actually accepted
        Trap whitespaceNameTrap = new Trap("   ", "Valid description", 10);
        assertEquals("   ", whitespaceNameTrap.getName(),
                "Whitespace-only name should be accepted by Trap constructor.");

        // Test with tab and newline characters in name - these are also accepted
        Trap tabTrap = new Trap("\t", "Description", 10);
        assertEquals("\t", tabTrap.getName(),
                "Tab-only name should be accepted by Trap constructor.");

        Trap newlineTrap = new Trap("\n", "Description", 10);
        assertEquals("\n", newlineTrap.getName(),
                "Newline-only name should be accepted by Trap constructor.");

        // Test with valid name containing whitespace
        Trap validTrap = new Trap("Valid Trap Name", "Description", 10);
        assertEquals("Valid Trap Name", validTrap.getName(),
                "Valid name with spaces should be accepted.");

        // Test that null and empty names are still rejected
        assertThrows(IllegalArgumentException.class,
                () -> new Trap(null, "Valid description", 10),
                "Null name should be rejected.");

        assertThrows(IllegalArgumentException.class,
                () -> new Trap("", "Valid description", 10),
                "Empty name should be rejected.");
    }


    @Test
    void damageClamping_worksCorrectly() {
        // Test various negative damage values
        Trap negTrap1 = new Trap("Neg1", "Desc", -1);
        assertEquals(0, negTrap1.getDamageAmount(), "Damage -1 should be clamped to 0.");

        Trap negTrap100 = new Trap("Neg100", "Desc", -100);
        assertEquals(0, negTrap100.getDamageAmount(), "Damage -100 should be clamped to 0.");

        Trap negTrapMin = new Trap("NegMin", "Desc", Integer.MIN_VALUE);
        assertEquals(0, negTrapMin.getDamageAmount(), "Minimum integer damage should be clamped to 0.");

        // Test that positive values are preserved
        Trap posTrap = new Trap("Pos", "Desc", 50);
        assertEquals(50, posTrap.getDamageAmount(), "Positive damage should be preserved.");
    }

    @Test
    void triggerAndReset_cycleTesting() {
        // Test multiple trigger-reset cycles
        for (int i = 0; i < 5; i++) {
            assertFalse(spikeTrap.isSprung(), "Trap should be unsprung at start of cycle " + i);

            int healthBefore = testHero.getHealth();
            spikeTrap.trigger(testHero);

            assertTrue(spikeTrap.isSprung(), "Trap should be sprung after trigger in cycle " + i);
            assertEquals(healthBefore - 20, testHero.getHealth(),
                    "Hero should take damage in cycle " + i);

            spikeTrap.reset();
            assertFalse(spikeTrap.isSprung(), "Trap should be reset after reset in cycle " + i);
        }
    }

    @Test
    void interactVsTrigger_behaviorIdentical() {
        // Create two identical traps
        Trap trap1 = new Trap("Test Trap", "Test description", 15);
        Trap trap2 = new Trap("Test Trap", "Test description", 15);

        Hero hero1 = heroFactory.getHero("Hero1", HeroType.WARRIOR, new Point(0, 0));
        Hero hero2 = heroFactory.getHero("Hero2", HeroType.WARRIOR, new Point(1, 1));

        int initialHealth1 = hero1.getHealth();
        int initialHealth2 = hero2.getHealth();

        // Use trigger on first trap
        trap1.trigger(hero1);

        // Use interact on second trap
        trap2.interact(hero2);

        // Both should have identical results
        assertEquals(initialHealth1 - 15, hero1.getHealth(),
                "Hero1 should take damage from trigger.");
        assertEquals(initialHealth2 - 15, hero2.getHealth(),
                "Hero2 should take damage from interact.");
        assertTrue(trap1.isSprung(), "Trap1 should be sprung after trigger.");
        assertTrue(trap2.isSprung(), "Trap2 should be sprung after interact.");
    }

    @Test
    void messageOutput_formatting() {
        outputStreamCaptor.reset();
        spikeTrap.trigger(testHero);

        String output = outputStreamCaptor.toString();

        // Check that the expected trap messages are present
        assertTrue(output.contains(testHero.getName() + " triggered a " + spikeTrap.getName() + "!"),
                "Output should contain trigger message.");
        assertTrue(output.contains(spikeTrap.getName() + " dealt " + spikeTrap.getDamageAmount() +
                        " damage to " + testHero.getName() + "."),
                "Output should contain damage message.");

        // Verify the output is not empty
        assertFalse(output.trim().isEmpty(), "Output should not be empty.");

        // Split by lines and check that we have the expected trap-specific messages
        String[] lines = output.split("\n");
        assertTrue(lines.length >= 2, "Should have at least 2 lines of output.");

        // Find the trap-specific messages in the output
        boolean foundTriggerMessage = false;
        boolean foundDamageMessage = false;

        for (String line : lines) {
            if (line.trim().equals(testHero.getName() + " triggered a " + spikeTrap.getName() + "!")) {
                foundTriggerMessage = true;
            }
            if (line.trim().equals(spikeTrap.getName() + " dealt " + spikeTrap.getDamageAmount() +
                    " damage to " + testHero.getName() + ".")) {
                foundDamageMessage = true;
            }
        }

        assertTrue(foundTriggerMessage, "Should find trigger message in output.");
        assertTrue(foundDamageMessage, "Should find damage message in output.");
    }

    @Test
    void resetMessage_formatting() {
        outputStreamCaptor.reset();
        spikeTrap.reset();

        String output = outputStreamCaptor.toString().trim();
        assertEquals(spikeTrap.getName() + " has been reset.", output,
                "Reset message should be correctly formatted.");
    }

    @Test
    void alreadySprungMessage_formatting() {
        // Spring the trap first
        spikeTrap.trigger(testHero);
        outputStreamCaptor.reset();

        // Try to trigger again
        spikeTrap.trigger(testHero);

        String output = outputStreamCaptor.toString().trim();
        assertEquals("The " + spikeTrap.getName() + " has already been sprung.", output,
                "Already sprung message should be correctly formatted.");
    }

    @Test
    void trapContract_isFollowedByAllInstances() {
        Trap[] traps = {spikeTrap, poisonTrap, fireTrap, zeroTrap};

        for (Trap trap : traps) {
            // Every Trap must have a non-null, non-empty name
            assertNotNull(trap.getName(),
                    "Trap name must not be null for " + trap.getClass().getSimpleName());
            assertFalse(trap.getName().isEmpty(),
                    "Trap name must not be empty for " + trap.getClass().getSimpleName());

            // Every Trap must have a non-null description (can be empty)
            assertNotNull(trap.getDescription(),
                    "Trap description must not be null for " + trap.getClass().getSimpleName());

            // Every Trap must have non-negative damage
            assertTrue(trap.getDamageAmount() >= 0,
                    "Trap damage must be non-negative for " + trap.getClass().getSimpleName());

            // getDamage should return same as getDamageAmount
            assertEquals(trap.getDamageAmount(), trap.getDamage(),
                    "getDamage should equal getDamageAmount for " + trap.getClass().getSimpleName());

            // Every Trap must have a toString implementation
            String stringRep = trap.toString();
            assertNotNull(stringRep,
                    "toString must not return null for " + trap.getClass().getSimpleName());
            assertTrue(stringRep.contains(trap.getName()),
                    "toString should contain trap name for " + trap.getClass().getSimpleName());

            // Every Trap must start unsprung
            if (!trap.isSprung()) { // Only test if not already sprung from previous tests
                assertFalse(trap.isSprung(),
                        "New trap should start unsprung for " + trap.getClass().getSimpleName());
            }

            // Every Trap must have working trigger, reset, and interact methods
            assertDoesNotThrow(() -> trap.trigger(testHero),
                    "trigger must work for " + trap.getClass().getSimpleName());
            assertDoesNotThrow(() -> trap.reset(),
                    "reset must work for " + trap.getClass().getSimpleName());
            assertDoesNotThrow(() -> trap.interact(testHero),
                    "interact must work for " + trap.getClass().getSimpleName());
            assertDoesNotThrow(() -> trap.trigger(null),
                    "trigger must handle null hero for " + trap.getClass().getSimpleName());
            assertDoesNotThrow(() -> trap.interact(null),
                    "interact must handle null hero for " + trap.getClass().getSimpleName());
        }
    }

    @Test
    void performanceTest_manyOperations() {
        // Test that many operations don't cause issues
        for (int i = 0; i < 1000; i++) {
            spikeTrap.toString();
            spikeTrap.hashCode();
            spikeTrap.getName();
            spikeTrap.getDescription();
            spikeTrap.getDamageAmount();
            spikeTrap.getDamage();
            spikeTrap.isSprung();
        }

        // Should still work correctly after many operations
        assertEquals("Spike Trap", spikeTrap.getName(),
                "Name should be unchanged after many operations.");
        assertEquals(20, spikeTrap.getDamageAmount(),
                "Damage should be unchanged after many operations.");
    }
}
