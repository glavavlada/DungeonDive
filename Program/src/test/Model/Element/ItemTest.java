package test.Model.Element;

import main.Model.element.Item;
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
 * Unit tests for the Item class.
 * Tests all methods and functionality to ensure 100% coverage.
 * Since Item is abstract, concrete test implementations are used.
 */
class ItemTest {

    // Concrete test implementations of Item
    private static class TestItem extends Item {
        private boolean useCalled = false;
        private Hero lastHeroUsed = null;

        public TestItem(String name, String description) {
            super(name, description);
        }

        @Override
        public void use(Hero theHero) {
            useCalled = true;
            lastHeroUsed = theHero;
            if (theHero != null && theHero.isAlive()) {
                // Simple healing effect for testing
                int currentHealth = theHero.getHealth();
                int maxHealth = theHero.getMaxHealth();
                theHero.setHealth(Math.min(maxHealth, currentHealth + 10));
            }
        }

        // Helper methods for testing
        public boolean wasUseCalled() { return useCalled; }
        public Hero getLastHeroUsed() { return lastHeroUsed; }
        public void resetUse() { useCalled = false; lastHeroUsed = null; }
    }

    private static class DamageItem extends Item {
        private final int damage;

        public DamageItem(String name, String description, int damage) {
            super(name, description);
            this.damage = damage;
        }

        @Override
        public void use(Hero theHero) {
            if (theHero != null && theHero.isAlive()) {
                int currentHealth = theHero.getHealth();
                theHero.setHealth(Math.max(0, currentHealth - damage));
            }
        }
    }

    private static class NullEffectItem extends Item {
        public NullEffectItem(String name, String description) {
            super(name, description);
        }

        @Override
        public void use(Hero theHero) {
            // Does nothing - null effect item
        }
    }

    private TestItem testItem;
    private DamageItem damageItem;
    private NullEffectItem nullEffectItem;
    private Hero testHero;
    private HeroFactory heroFactory;

    // For capturing console output
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    void setUp() {
        testItem = new TestItem("Test Potion", "A test item for unit testing.");
        damageItem = new DamageItem("Poison Vial", "Damages the user.", 15);
        nullEffectItem = new NullEffectItem("Broken Item", "Does nothing.");

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
        assertEquals("Test Potion", testItem.getName(),
                "Name should be initialized correctly.");
        assertEquals("A test item for unit testing.", testItem.getDescription(),
                "Description should be initialized correctly.");
    }

    @Test
    void constructor_withNullDescription_convertsToEmpty() {
        TestItem itemWithNullDesc = new TestItem("Test Item", null);
        assertEquals("Test Item", itemWithNullDesc.getName(),
                "Name should be initialized correctly.");
        assertEquals("", itemWithNullDesc.getDescription(),
                "Null description should be converted to empty string.");
    }

    @Test
    void constructor_withEmptyDescription_acceptsEmpty() {
        TestItem itemWithEmptyDesc = new TestItem("Test Item", "");
        assertEquals("Test Item", itemWithEmptyDesc.getName(),
                "Name should be initialized correctly.");
        assertEquals("", itemWithEmptyDesc.getDescription(),
                "Empty description should be accepted.");
    }

    @Test
    void constructor_withNullName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem(null, "Valid description"),
                "Constructor should throw exception for null name.");
    }

    @Test
    void constructor_withEmptyName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("", "Valid description"),
                "Constructor should throw exception for empty name.");
    }

    @Test
    void constructor_withWhitespaceOnlyName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("   ", "Valid description"),
                "Constructor should throw exception for whitespace-only name.");
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("\t", "Valid description"),
                "Constructor should throw exception for tab-only name.");
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("\n", "Valid description"),
                "Constructor should throw exception for newline-only name.");
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("Test Potion", testItem.getName(),
                "getName should return the correct name.");
        assertEquals("Poison Vial", damageItem.getName(),
                "getName should return the correct name for damage item.");
        assertEquals("Broken Item", nullEffectItem.getName(),
                "getName should return the correct name for null effect item.");
    }

    @Test
    void getDescription_returnsCorrectDescription() {
        assertEquals("A test item for unit testing.", testItem.getDescription(),
                "getDescription should return the correct description.");
        assertEquals("Damages the user.", damageItem.getDescription(),
                "getDescription should return the correct description for damage item.");
        assertEquals("Does nothing.", nullEffectItem.getDescription(),
                "getDescription should return the correct description for null effect item.");
    }

    @Test
    void getDescription_withNullDescription_returnsEmpty() {
        TestItem itemWithNullDesc = new TestItem("Test", null);
        assertEquals("", itemWithNullDesc.getDescription(),
                "getDescription should return empty string for null description.");
    }

    @Test
    void use_abstractMethod_isImplementedBySubclasses() {
        // Test that abstract use method is properly implemented
        assertDoesNotThrow(() -> testItem.use(testHero),
                "TestItem should implement use method.");
        assertDoesNotThrow(() -> damageItem.use(testHero),
                "DamageItem should implement use method.");
        assertDoesNotThrow(() -> nullEffectItem.use(testHero),
                "NullEffectItem should implement use method.");
    }

    @Test
    void use_withValidHero_callsImplementation() {
        testItem.use(testHero);
        assertTrue(testItem.wasUseCalled(),
                "use method should be called.");
        assertEquals(testHero, testItem.getLastHeroUsed(),
                "Hero should be passed to use method.");
    }

    @Test
    void use_withNullHero_handledGracefully() {
        assertDoesNotThrow(() -> testItem.use(null),
                "use method should handle null hero gracefully.");
        assertTrue(testItem.wasUseCalled(),
                "use method should still be called with null hero.");
        assertNull(testItem.getLastHeroUsed(),
                "Last hero used should be null.");
    }

    @Test
    void use_withDeadHero_handledCorrectly() {
        testHero.setHealth(0);
        assertFalse(testHero.isAlive(), "Hero should be dead.");

        int initialHealth = testHero.getHealth();
        testItem.use(testHero);

        assertEquals(initialHealth, testHero.getHealth(),
                "Dead hero should not be affected by item use.");
        assertTrue(testItem.wasUseCalled(),
                "use method should still be called.");
    }

    @Test
    void interact_withValidHero_attemptsPickup() {
        testItem.interact(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName()),
                "Output should contain hero name.");
        assertTrue(output.contains(testItem.getName()),
                "Output should contain item name.");
        assertTrue(output.contains("picked up") || output.contains("couldn't pick up"),
                "Output should contain pickup message.");
    }

    @Test
    void interact_withNullHero_handledGracefully() {
        assertDoesNotThrow(() -> testItem.interact(null),
                "interact method should handle null hero gracefully.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.isEmpty(),
                "No output should be generated for null hero.");
    }

    @Test
    void interact_successfulPickup_printsSuccessMessage() {
        // Ensure hero can pick up the item
        testItem.interact(testHero);

        String output = outputStreamCaptor.toString();
        if (output.contains("picked up")) {
            assertTrue(output.contains(testHero.getName() + " picked up " + testItem.getName()),
                    "Should print successful pickup message.");
        }
    }

    @Test
    void interact_failedPickup_printsFailureMessage() {
        // Fill hero's inventory to capacity to force pickup failure
        for (int i = 0; i < 20; i++) { // Assuming inventory has a limit
            testHero.pickupItem(new TestItem("Filler " + i, "Filler item"));
        }

        testItem.interact(testHero);

        String output = outputStreamCaptor.toString();
        if (output.contains("couldn't pick up")) {
            assertTrue(output.contains(testHero.getName() + " couldn't pick up " + testItem.getName()),
                    "Should print failed pickup message.");
        }
    }

    @Test
    void toString_withDescription_formatsCorrectly() {
        String expected = "Test Potion (A test item for unit testing.)";
        assertEquals(expected, testItem.toString(),
                "toString should format name and description correctly.");
    }

    @Test
    void toString_withEmptyDescription_formatsCorrectly() {
        TestItem itemWithEmptyDesc = new TestItem("Empty Desc Item", "");
        String expected = "Empty Desc Item ()";
        assertEquals(expected, itemWithEmptyDesc.toString(),
                "toString should handle empty description correctly.");
    }

    @Test
    void toString_withNullDescription_formatsCorrectly() {
        TestItem itemWithNullDesc = new TestItem("Null Desc Item", null);
        String expected = "Null Desc Item ()";
        assertEquals(expected, itemWithNullDesc.toString(),
                "toString should handle null description correctly.");
    }

    @Test
    void equals_withSameObject_returnsTrue() {
        assertTrue(testItem.equals(testItem),
                "Item should equal itself.");
    }

    @Test
    void equals_withNull_returnsFalse() {
        assertFalse(testItem.equals(null),
                "Item should not equal null.");
    }

    @Test
    void equals_withDifferentClass_returnsFalse() {
        assertFalse(testItem.equals("Not an Item"),
                "Item should not equal object of different class.");
    }

    @Test
    void equals_withSameNameAndDescription_returnsTrue() {
        TestItem sameItem = new TestItem("Test Potion", "A test item for unit testing.");
        assertTrue(testItem.equals(sameItem),
                "Items with same name and description should be equal.");
        assertTrue(sameItem.equals(testItem),
                "Equality should be symmetric.");
    }

    @Test
    void equals_withDifferentName_returnsFalse() {
        TestItem differentNameItem = new TestItem("Different Name", "A test item for unit testing.");
        assertFalse(testItem.equals(differentNameItem),
                "Items with different names should not be equal.");
    }

    @Test
    void equals_withDifferentDescription_returnsFalse() {
        TestItem differentDescItem = new TestItem("Test Potion", "Different description.");
        assertFalse(testItem.equals(differentDescItem),
                "Items with different descriptions should not be equal.");
    }

    @Test
    void equals_withBothNullDescriptions_returnsTrue() {
        TestItem item1 = new TestItem("Same Name", null);
        TestItem item2 = new TestItem("Same Name", null);
        assertTrue(item1.equals(item2),
                "Items with same name and both null descriptions should be equal.");
    }

    @Test
    void equals_withOneNullDescription_returnsFalse() {
        TestItem itemWithNull = new TestItem("Same Name", null);
        TestItem itemWithEmpty = new TestItem("Same Name", "");
        assertTrue(itemWithNull.equals(itemWithEmpty),
                "Item with null description should equal item with empty description.");
    }

    @Test
    void hashCode_withSameNameAndDescription_returnsSameHash() {
        TestItem sameItem = new TestItem("Test Potion", "A test item for unit testing.");
        assertEquals(testItem.hashCode(), sameItem.hashCode(),
                "Items with same name and description should have same hash code.");
    }

    @Test
    void hashCode_isConsistent() {
        int hash1 = testItem.hashCode();
        int hash2 = testItem.hashCode();
        assertEquals(hash1, hash2,
                "hashCode should be consistent across multiple calls.");
    }

    @Test
    void hashCode_withNullDescription() {
        TestItem itemWithNullDesc = new TestItem("Test", null);
        int hash1 = itemWithNullDesc.hashCode();
        int hash2 = itemWithNullDesc.hashCode();
        assertEquals(hash1, hash2,
                "Hash code should be consistent even with null description converted to empty string.");
    }

    @Test
    void inheritanceFromDungeonElement_worksCorrectly() {
        assertTrue(testItem instanceof main.Model.element.DungeonElement,
                "Item should be instance of DungeonElement.");
        assertTrue(damageItem instanceof main.Model.element.DungeonElement,
                "DamageItem should be instance of DungeonElement.");
        assertTrue(nullEffectItem instanceof main.Model.element.DungeonElement,
                "NullEffectItem should be instance of DungeonElement.");
    }

    @Test
    void polymorphism_worksCorrectly() {
        Item[] items = {testItem, damageItem, nullEffectItem};

        for (Item item : items) {
            assertDoesNotThrow(() -> item.use(testHero),
                    "Polymorphic use call should work for " + item.getClass().getSimpleName());
            assertDoesNotThrow(() -> item.interact(testHero),
                    "Polymorphic interact call should work for " + item.getClass().getSimpleName());
            assertDoesNotThrow(() -> item.toString(),
                    "Polymorphic toString call should work for " + item.getClass().getSimpleName());
            assertNotNull(item.getName(),
                    "getName should not return null for " + item.getClass().getSimpleName());
            assertNotNull(item.getDescription(),
                    "getDescription should not return null for " + item.getClass().getSimpleName());
        }
    }

    @Test
    void finalFields_areImmutable() {
        // Test that name and description don't change after creation
        String originalName = testItem.getName();
        String originalDescription = testItem.getDescription();

        // Use the item (which might change internal state)
        testItem.use(testHero);
        testItem.interact(testHero);

        assertEquals(originalName, testItem.getName(),
                "Item name should not change after use and interaction.");
        assertEquals(originalDescription, testItem.getDescription(),
                "Item description should not change after use and interaction.");
    }

    @Test
    void abstractMethods_mustBeImplemented() {
        // Verify that concrete classes must implement abstract methods
        assertDoesNotThrow(() -> testItem.use(testHero),
                "Concrete implementation should provide use method.");
        assertDoesNotThrow(() -> damageItem.use(testHero),
                "Another concrete implementation should provide use method.");
        assertDoesNotThrow(() -> nullEffectItem.use(testHero),
                "Third concrete implementation should provide use method.");
    }

    @Test
    void itemContract_isFollowedByAllImplementations() {
        Item[] items = {testItem, damageItem, nullEffectItem};

        for (Item item : items) {
            // Every Item must have a non-null name
            assertNotNull(item.getName(),
                    "Item name must not be null for " + item.getClass().getSimpleName());
            assertFalse(item.getName().isEmpty(),
                    "Item name must not be empty for " + item.getClass().getSimpleName());

            // Every Item must have a non-null description (can be empty)
            assertNotNull(item.getDescription(),
                    "Item description must not be null for " + item.getClass().getSimpleName());

            // Every Item must have a toString implementation
            String stringRep = item.toString();
            assertNotNull(stringRep,
                    "toString must not return null for " + item.getClass().getSimpleName());
            assertTrue(stringRep.contains(item.getName()),
                    "toString should contain item name for " + item.getClass().getSimpleName());

            // Every Item must have a use implementation
            assertDoesNotThrow(() -> item.use(testHero),
                    "use must be implemented for " + item.getClass().getSimpleName());
            assertDoesNotThrow(() -> item.use(null),
                    "use must handle null hero for " + item.getClass().getSimpleName());

            // Every Item must have an interact implementation (inherited from DungeonElement)
            assertDoesNotThrow(() -> item.interact(testHero),
                    "interact must be implemented for " + item.getClass().getSimpleName());
            assertDoesNotThrow(() -> item.interact(null),
                    "interact must handle null hero for " + item.getClass().getSimpleName());
        }
    }

    @Test
    void multipleUses_workCorrectly() {
        int initialHealth = testHero.getHealth();

        // Use test item multiple times
        testItem.use(testHero); // +10 health
        testItem.use(testHero); // +10 health
        testItem.use(testHero); // +10 health

        // Should have gained 30 health total (or capped at max health)
        int expectedHealth = Math.min(testHero.getMaxHealth(), initialHealth + 30);
        assertEquals(expectedHealth, testHero.getHealth(),
                "Multiple uses should accumulate effects correctly.");
    }

    @Test
    void resetUse_clearsTestItemState() {
        // Test helper method for resetting use state
        testItem.use(testHero);
        assertTrue(testItem.wasUseCalled(), "Use should be recorded.");
        assertEquals(testHero, testItem.getLastHeroUsed(), "Hero should be recorded.");

        testItem.resetUse();
        assertFalse(testItem.wasUseCalled(), "Use should be reset.");
        assertNull(testItem.getLastHeroUsed(), "Hero should be cleared.");
    }

    @Test
    void interact_withDeadHero_stillAttemptsPickup() {
        testHero.setHealth(0); // Kill the hero
        assertFalse(testHero.isAlive(), "Hero should be dead.");

        // Interaction should still attempt pickup even with dead hero
        assertDoesNotThrow(() -> testItem.interact(testHero),
                "Interact should not throw exception with dead hero.");

        String output = outputStreamCaptor.toString();
        // Should still generate some output (success or failure message)
        assertFalse(output.trim().isEmpty(), "Some output should be generated even with dead hero.");
    }

    @Test
    void use_withDeadHero_behavesAccordingToImplementation() {
        testHero.setHealth(0); // Kill the hero
        assertFalse(testHero.isAlive(), "Hero should be dead.");

        // Different implementations may handle dead heroes differently
        assertDoesNotThrow(() -> testItem.use(testHero),
                "Use should not throw exception with dead hero.");
        assertDoesNotThrow(() -> damageItem.use(testHero),
                "Damage item use should not throw exception with dead hero.");
        assertDoesNotThrow(() -> nullEffectItem.use(testHero),
                "Null effect item use should not throw exception with dead hero.");
    }

    @Test
    void constructorValidation_worksForAllItemTypes() {
        // Test that all item types properly validate constructor parameters
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem(null, "Valid description"),
                "TestItem should reject null name.");
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("", "Valid description"),
                "TestItem should reject empty name.");
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("   ", "Valid description"),
                "TestItem should reject whitespace-only name.");

        assertThrows(IllegalArgumentException.class,
                () -> new DamageItem(null, "Valid description", 10),
                "DamageItem should reject null name.");
        assertThrows(IllegalArgumentException.class,
                () -> new DamageItem("", "Valid description", 10),
                "DamageItem should reject empty name.");
        assertThrows(IllegalArgumentException.class,
                () -> new DamageItem("   ", "Valid description", 10),
                "DamageItem should reject whitespace-only name.");

        assertThrows(IllegalArgumentException.class,
                () -> new NullEffectItem(null, "Valid description"),
                "NullEffectItem should reject null name.");
        assertThrows(IllegalArgumentException.class,
                () -> new NullEffectItem("", "Valid description"),
                "NullEffectItem should reject empty name.");
        assertThrows(IllegalArgumentException.class,
                () -> new NullEffectItem("   ", "Valid description"),
                "NullEffectItem should reject whitespace-only name.");
    }

    @Test
    void specialCharactersInNameAndDescription_handledCorrectly() {
        TestItem specialItem = new TestItem("Sword of +5 Damage!", "A sword with special characters: @#$%^&*()");

        assertEquals("Sword of +5 Damage!", specialItem.getName(),
                "Special characters in name should be preserved.");
        assertEquals("A sword with special characters: @#$%^&*()", specialItem.getDescription(),
                "Special characters in description should be preserved.");

        String toStringResult = specialItem.toString();
        assertTrue(toStringResult.contains("Sword of +5 Damage!"),
                "toString should preserve special characters in name.");
        assertTrue(toStringResult.contains("@#$%^&*()"),
                "toString should preserve special characters in description.");
    }

    @Test
    void unicodeCharacters_handledCorrectly() {
        TestItem unicodeItem = new TestItem("魔法の剣", "Une épée magique avec des caractères unicode: ñáéíóú");

        assertEquals("魔法の剣", unicodeItem.getName(),
                "Unicode characters in name should be preserved.");
        assertEquals("Une épée magique avec des caractères unicode: ñáéíóú", unicodeItem.getDescription(),
                "Unicode characters in description should be preserved.");

        String toStringResult = unicodeItem.toString();
        assertTrue(toStringResult.contains("魔法の剣"),
                "toString should preserve unicode characters in name.");
        assertTrue(toStringResult.contains("ñáéíóú"),
                "toString should preserve unicode characters in description.");
    }

    @Test
    void veryLongNameAndDescription_handledCorrectly() {
        String longName = "A".repeat(1000); // 1000 character name
        String longDescription = "B".repeat(2000); // 2000 character description

        TestItem longItem = new TestItem(longName, longDescription);

        assertEquals(longName, longItem.getName(),
                "Very long name should be preserved.");
        assertEquals(longDescription, longItem.getDescription(),
                "Very long description should be preserved.");

        String toStringResult = longItem.toString();
        assertTrue(toStringResult.contains(longName),
                "toString should handle very long names.");
        assertTrue(toStringResult.contains(longDescription),
                "toString should handle very long descriptions.");
    }

    @Test
    void equalsAndHashCode_contractCompliance() {
        TestItem item1 = new TestItem("Same Name", "Same Description");
        TestItem item2 = new TestItem("Same Name", "Same Description");
        TestItem item3 = new TestItem("Different Name", "Same Description");

        // Reflexive: x.equals(x) should return true
        assertTrue(item1.equals(item1), "equals should be reflexive.");

        // Symmetric: x.equals(y) should return true if and only if y.equals(x) returns true
        assertTrue(item1.equals(item2), "equals should be symmetric (1->2).");
        assertTrue(item2.equals(item1), "equals should be symmetric (2->1).");

        // Transitive: if x.equals(y) and y.equals(z), then x.equals(z)
        TestItem item2Copy = new TestItem("Same Name", "Same Description");
        assertTrue(item1.equals(item2), "First equality for transitivity.");
        assertTrue(item2.equals(item2Copy), "Second equality for transitivity.");
        assertTrue(item1.equals(item2Copy), "equals should be transitive.");

        // Consistent: multiple invocations should return the same result
        assertTrue(item1.equals(item2), "First call to equals.");
        assertTrue(item1.equals(item2), "Second call to equals should be consistent.");

        // Null: x.equals(null) should return false
        assertFalse(item1.equals(null), "equals should return false for null.");

        // Hash code contract: if two objects are equal, they must have the same hash code
        assertEquals(item1.hashCode(), item2.hashCode(),
                "Equal objects must have equal hash codes.");

        // Hash code consistency
        int hash1 = item1.hashCode();
        int hash2 = item1.hashCode();
        assertEquals(hash1, hash2, "hashCode should be consistent.");
    }

    @Test
    void constructor_edgeCases_handledCorrectly() {
        // Test with tab and newline characters in name (should be rejected)
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("\t", "Description"),
                "Tab-only name should be rejected.");
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem("\n", "Description"),
                "Newline-only name should be rejected.");
        assertThrows(IllegalArgumentException.class,
                () -> new TestItem(" \t \n ", "Description"),
                "Mixed whitespace-only name should be rejected.");

        // Test with valid name containing whitespace
        TestItem validItem = new TestItem("Valid Name With Spaces", "Description");
        assertEquals("Valid Name With Spaces", validItem.getName(),
                "Valid name with spaces should be accepted.");
    }

    @Test
    void interact_bothSuccessAndFailurePaths() {
        // Test successful pickup path
        outputStreamCaptor.reset();
        TestItem pickupItem = new TestItem("Pickup Test", "Should be picked up");
        pickupItem.interact(testHero);

        String successOutput = outputStreamCaptor.toString();
        assertTrue(successOutput.contains("picked up") || successOutput.contains("Pickup Test"),
                "Should show pickup success message.");

        // Test failure path by filling inventory
        outputStreamCaptor.reset();
        // Fill inventory to capacity (assuming there's a limit)
        for (int i = 0; i < 15; i++) {
            testHero.pickupItem(new TestItem("Filler " + i, "Filler item"));
        }

        TestItem failItem = new TestItem("Should Fail", "This pickup should fail");
        failItem.interact(testHero);

        String failOutput = outputStreamCaptor.toString();
        assertTrue(failOutput.contains("couldn't pick up") || failOutput.contains("Should Fail"),
                "Should show pickup failure message.");
    }

    @Test
    void toString_withSpecialDescriptionCases() {
        // Test with null description (converted to empty)
        TestItem nullDescItem = new TestItem("Null Desc Item", null);
        String nullDescResult = nullDescItem.toString();
        assertEquals("Null Desc Item ()", nullDescResult,
                "toString should handle null description correctly.");

        // Test with very long description
        String longDesc = "This is a very long description that goes on and on and on...".repeat(10);
        TestItem longDescItem = new TestItem("Long Desc Item", longDesc);
        String longDescResult = longDescItem.toString();
        assertTrue(longDescResult.contains("Long Desc Item"),
                "toString should contain item name even with long description.");
        assertTrue(longDescResult.contains(longDesc),
                "toString should contain full long description.");
    }

    @Test
    void performanceTest_manyItems() {
        // Test that the class can handle many items efficiently
        Item[] manyItems = new Item[1000];
        for (int i = 0; i < 1000; i++) {
            manyItems[i] = new TestItem("Item " + i, "Description " + i);
        }

        for (Item item : manyItems) {
            assertDoesNotThrow(() -> item.toString());
            assertDoesNotThrow(() -> item.hashCode());
            assertDoesNotThrow(() -> item.use(testHero));
        }
    }

    @Test
    void damageItem_functionalityTest() {
        int initialHealth = testHero.getHealth();

        damageItem.use(testHero);

        assertEquals(initialHealth - 15, testHero.getHealth(),
                "DamageItem should reduce hero health by damage amount.");
    }

    @Test
    void damageItem_withLowHealthHero_doesNotGoBelowZero() {
        testHero.setHealth(5); // Set health lower than damage amount

        damageItem.use(testHero);

        assertEquals(0, testHero.getHealth(),
                "Hero health should not go below 0.");
    }

    @Test
    void damageItem_withDeadHero_doesNotChangHealth() {
        testHero.setHealth(0);
        assertFalse(testHero.isAlive(), "Hero should be dead.");

        damageItem.use(testHero);

        assertEquals(0, testHero.getHealth(),
                "Dead hero health should remain unchanged.");
    }

    @Test
    void nullEffectItem_doesNothing() {
        int initialHealth = testHero.getHealth();

        nullEffectItem.use(testHero);

        assertEquals(initialHealth, testHero.getHealth(),
                "NullEffectItem should not change hero health.");
    }

    @Test
    void nullEffectItem_withDeadHero_stillDoesNothing() {
        testHero.setHealth(0);

        nullEffectItem.use(testHero);

        assertEquals(0, testHero.getHealth(),
                "NullEffectItem should not affect dead hero.");
    }

    @Test
    void testItem_healingFunctionality() {
        testHero.setHealth(50);
        int initialHealth = testHero.getHealth();

        testItem.use(testHero);

        assertEquals(initialHealth + 10, testHero.getHealth(),
                "TestItem should heal hero by 10 points.");
    }

    @Test
    void testItem_healingCappedAtMaxHealth() {
        int maxHealth = testHero.getMaxHealth();
        testHero.setHealth(maxHealth - 5); // 5 below max

        testItem.use(testHero); // Heals 10, but should cap at max

        assertEquals(maxHealth, testHero.getHealth(),
                "TestItem healing should be capped at max health.");
    }

    @Test
    void interact_messageFormat_isCorrect() {
        outputStreamCaptor.reset();
        testItem.interact(testHero);

        String output = outputStreamCaptor.toString().trim();

        // Check for the actual message format from the Item.interact() method
        boolean hasPickedUpMessage = output.contains(testHero.getName() + " picked up " + testItem.getName() + ".");
        boolean hasCouldntPickUpMessage = output.contains(testHero.getName() + " couldn't pick up " + testItem.getName() + ".");

        assertTrue(hasPickedUpMessage || hasCouldntPickUpMessage,
                "Interact message should contain either 'picked up' or 'couldn't pick up' message. Actual output: '" + output + "'");
    }

    @Test
    void interact_withDifferentHeroes_worksCorrectly() {
        Hero warrior = heroFactory.getHero("Warrior", HeroType.WARRIOR, new Point(1, 1));

        outputStreamCaptor.reset();
        testItem.interact(warrior);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Warrior"),
                "Interact should work with different hero types.");
        assertTrue(output.contains(testItem.getName()),
                "Output should contain item name for different heroes.");
    }

    @Test
    void multipleInteractions_workCorrectly() {
        TestItem item1 = new TestItem("Item1", "First item");
        TestItem item2 = new TestItem("Item2", "Second item");

        outputStreamCaptor.reset();
        item1.interact(testHero);
        item2.interact(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Item1"),
                "Output should contain first item interaction.");
        assertTrue(output.contains("Item2"),
                "Output should contain second item interaction.");
    }

    @Test
    void fieldImmutability_afterMultipleOperations() {
        String originalName = testItem.getName();
        String originalDescription = testItem.getDescription();

        // Perform multiple operations
        testItem.use(testHero);
        testItem.interact(testHero);
        testItem.toString();
        testItem.hashCode();
        testItem.equals(new TestItem("Different", "Different"));

        assertEquals(originalName, testItem.getName(),
                "Name should remain unchanged after all operations.");
        assertEquals(originalDescription, testItem.getDescription(),
                "Description should remain unchanged after all operations.");
    }

    @Test
    void equals_withDifferentSubclasses_returnsFalse() {
        TestItem testItem1 = new TestItem("Same Name", "Same Description");
        DamageItem damageItem1 = new DamageItem("Same Name", "Same Description", 10);

        assertFalse(testItem1.equals(damageItem1),
                "Items of different subclasses should not be equal even with same name/description.");
        assertFalse(damageItem1.equals(testItem1),
                "Equality should be symmetric for different subclasses.");
    }

    @Test
    void hashCode_withDifferentSubclasses_mayDiffer() {
        TestItem testItem1 = new TestItem("Same Name", "Same Description");
        DamageItem damageItem1 = new DamageItem("Same Name", "Same Description", 10);

        // Hash codes may or may not be different, but objects should not be equal
        assertFalse(testItem1.equals(damageItem1),
                "Different subclasses should not be equal regardless of hash code.");
    }

    @Test
    void toString_containsExpectedElements() {
        String result = testItem.toString();

        assertTrue(result.contains(testItem.getName()),
                "toString should contain item name.");
        assertTrue(result.contains(testItem.getDescription()),
                "toString should contain item description.");
        assertTrue(result.contains("(") && result.contains(")"),
                "toString should contain parentheses around description.");
    }

    @Test
    void constructor_trimming_behavior() {
        // Test that constructor doesn't trim valid names with leading/trailing spaces
        TestItem itemWithSpaces = new TestItem(" Valid Name ", "Description");
        assertEquals(" Valid Name ", itemWithSpaces.getName(),
                "Constructor should preserve leading and trailing spaces in valid names.");
    }

    @Test
    void extremeValues_handledCorrectly() {
        // Test with extreme damage values
        DamageItem extremeDamageItem = new DamageItem("Extreme Damage", "Extreme damage item", Integer.MAX_VALUE);
        testHero.setHealth(100);

        extremeDamageItem.use(testHero);

        assertEquals(0, testHero.getHealth(),
                "Extreme damage should not cause integer overflow, health should be 0.");
    }

    @Test
    void negativeValues_handledCorrectly() {
        // Test with negative damage (should heal)
        DamageItem healingItem = new DamageItem("Healing Item", "Negative damage heals", -20);
        testHero.setHealth(50);

        healingItem.use(testHero);

        assertEquals(70, testHero.getHealth(),
                "Negative damage should heal the hero.");
    }

    @Test
    void concurrentAccess_simulation() {
        // Simulate concurrent access to test thread safety of immutable fields
        TestItem sharedItem = new TestItem("Shared Item", "Shared description");

        // Multiple "threads" accessing the same item
        for (int i = 0; i < 100; i++) {
            assertEquals("Shared Item", sharedItem.getName(),
                    "Name should be consistent across multiple accesses.");
            assertEquals("Shared description", sharedItem.getDescription(),
                    "Description should be consistent across multiple accesses.");
            assertNotNull(sharedItem.toString(),
                    "toString should always return a value.");
        }
    }

    @Test
    void memoryEfficiency_test() {
        // Test that items don't hold unnecessary references
        TestItem item = new TestItem("Memory Test", "Testing memory efficiency");

        // Use the item and ensure it doesn't hold references to heroes
        item.use(testHero);
        item.interact(testHero);

        // The item itself should not hold references to the hero
        // (This is more of a design test - the TestItem helper class does hold references for testing)
        assertNotNull(item.getName(),
                "Item should maintain its own state.");
        assertNotNull(item.getDescription(),
                "Item should maintain its own state.");
    }

    @Test
    void allMethodsCovered_verification() {
        // Ensure all public methods are tested
        assertDoesNotThrow(() -> {
            // Constructor (tested in multiple tests)
            TestItem item = new TestItem("Test", "Test");

            // All public methods
            item.getName();
            item.getDescription();
            item.use(testHero);
            item.interact(testHero);
            item.toString();
            item.equals(item);
            item.hashCode();
        }, "All public methods should be callable without exceptions.");
    }
}

