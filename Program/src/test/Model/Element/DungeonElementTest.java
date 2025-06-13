package test.Model.Element;

import main.Model.element.DungeonElement;
import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.util.HeroType;
import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the abstract DungeonElement class.
 * Tests the abstract class behavior through concrete implementations.
 */
class DungeonElementTest {

    // Concrete test implementations of DungeonElement for testing purposes
    private static class TestDungeonElement extends DungeonElement {
        private final String myName;
        private boolean myInteractCalled;
        private Hero myLastHeroInteracted;

        public TestDungeonElement(final String theName) {
            this.myName = theName;
            this.myInteractCalled = false;
            this.myLastHeroInteracted = null;
        }

        @Override
        public void interact(final Hero theHero) {
            this.myInteractCalled = true;
            this.myLastHeroInteracted = theHero;
        }

        @Override
        public String toString() {
            return myName;
        }

        // Test helper methods
        public boolean wasInteractCalled() {
            return myInteractCalled;
        }

        public Hero getLastHeroInteracted() {
            return myLastHeroInteracted;
        }

        public void resetInteraction() {
            myInteractCalled = false;
            myLastHeroInteracted = null;
        }
    }

    // Another concrete implementation for testing different behaviors
    private static class AnotherTestDungeonElement extends DungeonElement {
        private int myInteractionCount;

        public AnotherTestDungeonElement() {
            this.myInteractionCount = 0;
        }

        @Override
        public void interact(final Hero theHero) {
            myInteractionCount++;
            if (theHero != null) {
                // Simulate some interaction effect
                theHero.takeDamage(5); // Example interaction
            }
        }

        @Override
        public String toString() {
            return "AnotherTestElement[interactions=" + myInteractionCount + "]";
        }

        public int getInteractionCount() {
            return myInteractionCount;
        }
    }

    // Null interaction test element
    private static class NullInteractionElement extends DungeonElement {
        @Override
        public void interact(final Hero theHero) {
            // Does nothing - tests null interaction behavior
        }

        @Override
        public String toString() {
            return "NullInteractionElement";
        }
    }

    private TestDungeonElement testElement;
    private AnotherTestDungeonElement anotherElement;
    private NullInteractionElement nullElement;
    private Hero testHero;
    private HeroFactory heroFactory;

    @BeforeEach
    void setUp() {
        testElement = new TestDungeonElement("TestElement");
        anotherElement = new AnotherTestDungeonElement();
        nullElement = new NullInteractionElement();

        heroFactory = new HeroFactory();
        testHero = heroFactory.getHero("TestHero", HeroType.WARRIOR, new Point(0, 0));
    }

    @Test
    void interact_withValidHero_callsInteractMethod() {
        assertFalse(testElement.wasInteractCalled(), "Interact should not be called initially.");
        assertNull(testElement.getLastHeroInteracted(), "No hero should have interacted initially.");

        testElement.interact(testHero);

        assertTrue(testElement.wasInteractCalled(), "Interact should be called after interaction.");
        assertEquals(testHero, testElement.getLastHeroInteracted(), "Hero should be recorded as last interacted.");
    }

    @Test
    void interact_withNullHero_handlesGracefully() {
        // Test that concrete implementations can handle null heroes
        assertDoesNotThrow(() -> testElement.interact(null),
                          "Interact with null hero should not throw exception.");
        assertTrue(testElement.wasInteractCalled(), "Interact should still be called even with null hero.");
        assertNull(testElement.getLastHeroInteracted(), "Last hero interacted should be null.");
    }

    @Test
    void interact_multipleInteractions_worksCorrectly() {
        Hero anotherHero = heroFactory.getHero("AnotherHero", HeroType.WARRIOR, new Point(1, 1));

        // First interaction
        testElement.interact(testHero);
        assertTrue(testElement.wasInteractCalled(), "First interaction should be recorded.");
        assertEquals(testHero, testElement.getLastHeroInteracted(), "First hero should be recorded.");

        // Second interaction
        testElement.interact(anotherHero);
        assertTrue(testElement.wasInteractCalled(), "Second interaction should be recorded.");
        assertEquals(anotherHero, testElement.getLastHeroInteracted(), "Second hero should be recorded.");
    }

    @Test
    void interact_differentImplementations_behaveDifferently() {
        int initialHeroHealth = testHero.getHealth();

        // Test first implementation (no health effect)
        testElement.interact(testHero);
        assertEquals(initialHeroHealth, testHero.getHealth(), "TestElement should not affect hero health.");

        // Test second implementation (damages hero)
        anotherElement.interact(testHero);
        assertEquals(initialHeroHealth - 5, testHero.getHealth(), "AnotherTestElement should damage hero.");
        assertEquals(1, anotherElement.getInteractionCount(), "Interaction count should be incremented.");
    }

    @Test
    void interact_countsInteractions_correctly() {
        assertEquals(0, anotherElement.getInteractionCount(), "Initial interaction count should be 0.");

        anotherElement.interact(testHero);
        assertEquals(1, anotherElement.getInteractionCount(), "Interaction count should be 1 after first interaction.");

        anotherElement.interact(testHero);
        assertEquals(2, anotherElement.getInteractionCount(), "Interaction count should be 2 after second interaction.");

        anotherElement.interact(null);
        assertEquals(3, anotherElement.getInteractionCount(), "Interaction count should be 3 even with null hero.");
    }

    @Test
    void toString_returnsCorrectStringRepresentation() {
        assertEquals("TestElement", testElement.toString(), "TestElement toString should return correct name.");
        assertTrue(anotherElement.toString().contains("AnotherTestElement"),
                  "AnotherTestElement toString should contain class name.");
        assertTrue(anotherElement.toString().contains("interactions=0"),
                  "AnotherTestElement toString should show interaction count.");
        assertEquals("NullInteractionElement", nullElement.toString(),
                    "NullInteractionElement toString should return correct name.");
    }

    @Test
    void toString_isConsistent() {
        String firstCall = testElement.toString();
        String secondCall = testElement.toString();
        assertEquals(firstCall, secondCall, "toString should be consistent across multiple calls.");
    }

    @Test
    void toString_afterInteraction_mayChange() {
        String beforeInteraction = anotherElement.toString();
        anotherElement.interact(testHero);
        String afterInteraction = anotherElement.toString();

        assertNotEquals(beforeInteraction, afterInteraction,
                       "toString may change after interaction for stateful elements.");
        assertTrue(afterInteraction.contains("interactions=1"),
                  "toString should reflect updated interaction count.");
    }

    @Test
    void abstractMethods_mustBeImplemented() {
        // Verify that concrete classes must implement abstract methods
        assertDoesNotThrow(() -> testElement.interact(testHero),
                          "Concrete implementation should provide interact method.");
        assertDoesNotThrow(() -> testElement.toString(),
                          "Concrete implementation should provide toString method.");

        assertDoesNotThrow(() -> anotherElement.interact(testHero),
                          "Another concrete implementation should provide interact method.");
        assertDoesNotThrow(() -> anotherElement.toString(),
                          "Another concrete implementation should provide toString method.");
    }

    @Test
    void nullInteractionElement_handlesInteractionSafely() {
        int initialHealth = testHero.getHealth();

        assertDoesNotThrow(() -> nullElement.interact(testHero),
                          "Null interaction element should handle interaction safely.");
        assertEquals(initialHealth, testHero.getHealth(),
                    "Null interaction should not affect hero.");

        assertDoesNotThrow(() -> nullElement.interact(null),
                          "Null interaction element should handle null hero safely.");
    }

    @Test
    void dungeonElement_inheritance_worksCorrectly() {
        // Test that our concrete implementations are indeed DungeonElements
        assertTrue(testElement instanceof DungeonElement,
                  "TestDungeonElement should be instance of DungeonElement.");
        assertTrue(anotherElement instanceof DungeonElement,
                  "AnotherTestDungeonElement should be instance of DungeonElement.");
        assertTrue(nullElement instanceof DungeonElement,
                  "NullInteractionElement should be instance of DungeonElement.");
    }

    @Test
    void dungeonElement_polymorphism_worksCorrectly() {
        // Test polymorphic behavior
        DungeonElement[] elements = {testElement, anotherElement, nullElement};

        for (DungeonElement element : elements) {
            assertDoesNotThrow(() -> element.interact(testHero),
                              "Polymorphic interact call should work for " + element.getClass().getSimpleName());
            assertDoesNotThrow(() -> element.toString(),
                              "Polymorphic toString call should work for " + element.getClass().getSimpleName());
            assertNotNull(element.toString(),
                         "toString should not return null for " + element.getClass().getSimpleName());
        }
    }

    @Test
    void interact_withDifferentHeroTypes_worksCorrectly() {
        // Test interaction with different hero types if available
        Hero warrior = heroFactory.getHero("Warrior", HeroType.WARRIOR, new Point(0, 0));

        assertDoesNotThrow(() -> testElement.interact(warrior),
                          "Should interact with warrior hero type.");
        assertEquals(warrior, testElement.getLastHeroInteracted(),
                    "Should record warrior hero interaction.");
    }

    @Test
    void resetInteraction_clearsInteractionState() {
        // Test helper method for resetting interaction state
        testElement.interact(testHero);
        assertTrue(testElement.wasInteractCalled(), "Interaction should be recorded.");
        assertEquals(testHero, testElement.getLastHeroInteracted(), "Hero should be recorded.");

        testElement.resetInteraction();
        assertFalse(testElement.wasInteractCalled(), "Interaction should be reset.");
        assertNull(testElement.getLastHeroInteracted(), "Hero should be cleared.");
    }

    @Test
    void dungeonElement_contractCompliance() {
        // Test that the abstract class contract is properly followed
        for (DungeonElement element : new DungeonElement[]{testElement, anotherElement, nullElement}) {
            // Every DungeonElement must have a toString implementation
            String stringRep = element.toString();
            assertNotNull(stringRep, "toString must not return null for " + element.getClass().getSimpleName());

            // Every DungeonElement must have an interact implementation
            assertDoesNotThrow(() -> element.interact(testHero),
                              "interact must be implemented for " + element.getClass().getSimpleName());
            assertDoesNotThrow(() -> element.interact(null),
                              "interact must handle null hero for " + element.getClass().getSimpleName());
        }
    }
}
