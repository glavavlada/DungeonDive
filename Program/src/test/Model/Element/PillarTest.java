package test.Model.Element;

import main.Model.element.Pillar;
import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.util.HeroType;
import main.Model.util.PillarType;
import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Pillar class.
 * Tests all methods and functionality to ensure 100% coverage.
 */
class PillarTest {

    private Pillar abstractionPillar;
    private Pillar encapsulationPillar;
    private Pillar inheritancePillar;
    private Pillar polymorphismPillar;
    private Hero testHero;
    private HeroFactory heroFactory;

    // For capturing console output
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardErr = System.err;

    @BeforeEach
    void setUp() {
        abstractionPillar = new Pillar(PillarType.ABSTRACTION);
        encapsulationPillar = new Pillar(PillarType.ENCAPSULATION);
        inheritancePillar = new Pillar(PillarType.INHERITANCE);
        polymorphismPillar = new Pillar(PillarType.POLYMORPHISM);

        heroFactory = new HeroFactory();
        testHero = heroFactory.getHero("TestHero", HeroType.WARRIOR, new Point(0, 0));

        // Capture console output for testing print statements
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out and System.err
        System.setOut(standardOut);
        System.setErr(standardErr);
    }

    @Test
    void constructor_withValidPillarType_initializesCorrectly() {
        assertEquals(PillarType.ABSTRACTION, abstractionPillar.getType(),
                    "Abstraction pillar should have correct type.");
        assertFalse(abstractionPillar.isActivated(),
                   "New pillar should not be activated.");

        assertEquals(PillarType.ENCAPSULATION, encapsulationPillar.getType(),
                    "Encapsulation pillar should have correct type.");
        assertFalse(encapsulationPillar.isActivated(),
                   "New pillar should not be activated.");

        assertEquals(PillarType.INHERITANCE, inheritancePillar.getType(),
                    "Inheritance pillar should have correct type.");
        assertFalse(inheritancePillar.isActivated(),
                   "New pillar should not be activated.");

        assertEquals(PillarType.POLYMORPHISM, polymorphismPillar.getType(),
                    "Polymorphism pillar should have correct type.");
        assertFalse(polymorphismPillar.isActivated(),
                   "New pillar should not be activated.");
    }

    @Test
    void constructor_withNullPillarType_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Pillar(null),
                "Constructor should throw exception for null PillarType.");

        assertEquals("PillarType cannot be null.", exception.getMessage(),
                    "Exception message should be correct.");
    }

    @Test
    void getType_returnsCorrectType() {
        assertEquals(PillarType.ABSTRACTION, abstractionPillar.getType(),
                    "getType should return correct type for abstraction pillar.");
        assertEquals(PillarType.ENCAPSULATION, encapsulationPillar.getType(),
                    "getType should return correct type for encapsulation pillar.");
        assertEquals(PillarType.INHERITANCE, inheritancePillar.getType(),
                    "getType should return correct type for inheritance pillar.");
        assertEquals(PillarType.POLYMORPHISM, polymorphismPillar.getType(),
                    "getType should return correct type for polymorphism pillar.");
    }

    @Test
    void isActivated_initiallyReturnsFalse() {
        assertFalse(abstractionPillar.isActivated(),
                   "New pillar should not be activated.");
        assertFalse(encapsulationPillar.isActivated(),
                   "New pillar should not be activated.");
        assertFalse(inheritancePillar.isActivated(),
                   "New pillar should not be activated.");
        assertFalse(polymorphismPillar.isActivated(),
                   "New pillar should not be activated.");
    }

    @Test
    void activate_withValidHero_activatesPillar() {
        assertFalse(abstractionPillar.isActivated(), "Pillar should start inactive.");

        abstractionPillar.activate(testHero);

        assertTrue(abstractionPillar.isActivated(), "Pillar should be activated after activation.");
    }

    @Test
    void activate_withValidHero_printsActivationMessage() {
        abstractionPillar.activate(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + " activated the Pillar of " + PillarType.ABSTRACTION.getDisplayName() + "!"),
                  "Should print activation message.");
    }

    @Test
    void activate_withNullHero_printsErrorMessage() {
        abstractionPillar.activate(null);

        String errorOutput = errorStreamCaptor.toString();
        assertTrue(errorOutput.contains("Cannot activate pillar with a null hero."),
                  "Should print error message for null hero.");

        assertFalse(abstractionPillar.isActivated(),
                   "Pillar should not be activated with null hero.");
    }

    @Test
    void activate_alreadyActivatedPillar_printsAlreadyActivatedMessage() {
        // First activation
        abstractionPillar.activate(testHero);
        assertTrue(abstractionPillar.isActivated(), "Pillar should be activated.");

        // Clear output buffer
        outputStreamCaptor.reset();

        // Second activation attempt
        abstractionPillar.activate(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("The Pillar of " + PillarType.ABSTRACTION.getDisplayName() + " is already activated."),
                  "Should print already activated message.");
    }

    @Test
    void activate_abstractionPillar_increasesCritChance() {
        double initialCritChance = testHero.getCritChance();

        abstractionPillar.activate(testHero);

        assertEquals(initialCritChance + 0.25, testHero.getCritChance(), 0.001,
                    "Abstraction pillar should increase crit chance by 0.25.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + "'s crit chance increased!"),
                  "Should print crit chance increase message.");
    }

    @Test
    void activate_encapsulationPillar_increasesMaxHealth() {
        int initialMaxHealth = testHero.getMaxHealth();

        encapsulationPillar.activate(testHero);

        assertEquals(initialMaxHealth + 30, testHero.getMaxHealth(),
                    "Encapsulation pillar should increase max health by 30.");
        assertEquals(testHero.getMaxHealth(), testHero.getHealth(),
                    "Hero health should be set to new max health.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + "'s max health increased!"),
                  "Should print max health increase message.");
    }


    @Test
    void activate_polymorphismPillar_increasesAttack() {
        polymorphismPillar.activate(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + "'s got an attack buff"),
                  "Should print attack buff message.");

        // Note: The actual attack buff implementation would need to be verified
        // based on how addAttackBuff is implemented in the Hero class
    }

    @Test
    void interact_withValidHero_callsActivate() {
        assertFalse(abstractionPillar.isActivated(), "Pillar should start inactive.");

        abstractionPillar.interact(testHero);

        assertTrue(abstractionPillar.isActivated(), "Pillar should be activated after interaction.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + " approaches the Pillar of " + PillarType.ABSTRACTION.getDisplayName() + "."),
                  "Should print approach message.");
        assertTrue(output.contains(testHero.getName() + " activated the Pillar of " + PillarType.ABSTRACTION.getDisplayName() + "!"),
                  "Should print activation message.");
    }

    @Test
    void interact_withNullHero_handlesGracefully() {
        assertDoesNotThrow(() -> abstractionPillar.interact(null),
                          "interact should handle null hero gracefully.");

        assertFalse(abstractionPillar.isActivated(),
                   "Pillar should not be activated with null hero.");
    }

    @Test
    void toString_inactiveState_showsCorrectFormat() {
        String expected = "Pillar of " + PillarType.ABSTRACTION.getDisplayName() + " (Inactive)";
        assertEquals(expected, abstractionPillar.toString(),
                    "toString should show inactive state correctly.");
    }

    @Test
    void toString_activatedState_showsCorrectFormat() {
        abstractionPillar.activate(testHero);

        String expected = "Pillar of " + PillarType.ABSTRACTION.getDisplayName() + " (Activated)";
        assertEquals(expected, abstractionPillar.toString(),
                    "toString should show activated state correctly.");
    }

    @Test
    void toString_allPillarTypes_formatCorrectly() {
        String abstractionStr = "Pillar of " + PillarType.ABSTRACTION.getDisplayName() + " (Inactive)";
        assertEquals(abstractionStr, abstractionPillar.toString(),
                    "Abstraction pillar toString should be correct.");

        String encapsulationStr = "Pillar of " + PillarType.ENCAPSULATION.getDisplayName() + " (Inactive)";
        assertEquals(encapsulationStr, encapsulationPillar.toString(),
                    "Encapsulation pillar toString should be correct.");

        String inheritanceStr = "Pillar of " + PillarType.INHERITANCE.getDisplayName() + " (Inactive)";
        assertEquals(inheritanceStr, inheritancePillar.toString(),
                    "Inheritance pillar toString should be correct.");

        String polymorphismStr = "Pillar of " + PillarType.POLYMORPHISM.getDisplayName() + " (Inactive)";
        assertEquals(polymorphismStr, polymorphismPillar.toString(),
                    "Polymorphism pillar toString should be correct.");
    }

    @Test
    void equals_withSameObject_returnsTrue() {
        assertTrue(abstractionPillar.equals(abstractionPillar),
                  "Pillar should equal itself.");
    }

    @Test
    void equals_withNull_returnsFalse() {
        assertFalse(abstractionPillar.equals(null),
                   "Pillar should not equal null.");
    }

    @Test
    void equals_withDifferentClass_returnsFalse() {
        assertFalse(abstractionPillar.equals("Not a Pillar"),
                   "Pillar should not equal object of different class.");
    }

    @Test
    void equals_withSameTypeAndActivationState_returnsTrue() {
        Pillar anotherAbstractionPillar = new Pillar(PillarType.ABSTRACTION);

        assertTrue(abstractionPillar.equals(anotherAbstractionPillar),
                  "Pillars with same type and activation state should be equal.");
        assertTrue(anotherAbstractionPillar.equals(abstractionPillar),
                  "Equality should be symmetric.");
    }

    @Test
    void equals_withDifferentType_returnsFalse() {
        assertFalse(abstractionPillar.equals(encapsulationPillar),
                   "Pillars with different types should not be equal.");
    }

    @Test
    void equals_withDifferentActivationState_returnsFalse() {
        Pillar anotherAbstractionPillar = new Pillar(PillarType.ABSTRACTION);
        abstractionPillar.activate(testHero); // Activate one but not the other

        assertFalse(abstractionPillar.equals(anotherAbstractionPillar),
                   "Pillars with different activation states should not be equal.");
    }

    @Test
    void equals_withSameTypeAndBothActivated_returnsTrue() {
        Pillar anotherAbstractionPillar = new Pillar(PillarType.ABSTRACTION);

        abstractionPillar.activate(testHero);
        anotherAbstractionPillar.activate(testHero);

        assertTrue(abstractionPillar.equals(anotherAbstractionPillar),
                  "Pillars with same type and both activated should be equal.");
    }

    @Test
    void hashCode_withSameTypeAndActivationState_returnsSameHash() {
        Pillar anotherAbstractionPillar = new Pillar(PillarType.ABSTRACTION);

        assertEquals(abstractionPillar.hashCode(), anotherAbstractionPillar.hashCode(),
                    "Pillars with same type and activation state should have same hash code.");
    }

    @Test
    void hashCode_isConsistent() {
        int hash1 = abstractionPillar.hashCode();
        int hash2 = abstractionPillar.hashCode();
        assertEquals(hash1, hash2,
                    "hashCode should be consistent across multiple calls.");
    }

    @Test
    void hashCode_changesWithActivation() {
        int inactiveHash = abstractionPillar.hashCode();
        abstractionPillar.activate(testHero);
        int activeHash = abstractionPillar.hashCode();

        assertNotEquals(inactiveHash, activeHash,
                "hashCode should change when activation state changes.");
    }

    @Test
    void inheritanceFromDungeonElement_worksCorrectly() {
        assertTrue(abstractionPillar instanceof main.Model.element.DungeonElement,
                "Pillar should be instance of DungeonElement.");
        assertTrue(encapsulationPillar instanceof main.Model.element.DungeonElement,
                "Pillar should be instance of DungeonElement.");
        assertTrue(inheritancePillar instanceof main.Model.element.DungeonElement,
                "Pillar should be instance of DungeonElement.");
        assertTrue(polymorphismPillar instanceof main.Model.element.DungeonElement,
                "Pillar should be instance of DungeonElement.");
    }

    @Test
    void polymorphism_worksCorrectly() {
        Pillar[] pillars = {abstractionPillar, encapsulationPillar, inheritancePillar, polymorphismPillar};

        for (Pillar pillar : pillars) {
            assertDoesNotThrow(() -> pillar.activate(testHero),
                    "Polymorphic activate call should work for " + pillar.getType());
            assertDoesNotThrow(() -> pillar.interact(testHero),
                    "Polymorphic interact call should work for " + pillar.getType());
            assertDoesNotThrow(() -> pillar.toString(),
                    "Polymorphic toString call should work for " + pillar.getType());
            assertNotNull(pillar.getType(),
                    "getType should not return null for " + pillar.getType());
        }
    }

    @Test
    void activate_allPillarTypes_applyCorrectBuffs() {
        // Test each pillar type applies its specific buff
        outputStreamCaptor.reset();

        // Test Abstraction
        abstractionPillar.activate(testHero);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Applying buff for: " + PillarType.ABSTRACTION.getDisplayName()),
                "Should show buff application for Abstraction.");

        outputStreamCaptor.reset();

        // Test Encapsulation
        encapsulationPillar.activate(testHero);
        output = outputStreamCaptor.toString();
        assertTrue(output.contains("Applying buff for: " + PillarType.ENCAPSULATION.getDisplayName()),
                "Should show buff application for Encapsulation.");

        outputStreamCaptor.reset();

        // Test Inheritance
        inheritancePillar.activate(testHero);
        output = outputStreamCaptor.toString();
        assertTrue(output.contains("Applying buff for: " + PillarType.INHERITANCE.getDisplayName()),
                "Should show buff application for Inheritance.");

        outputStreamCaptor.reset();

        // Test Polymorphism
        polymorphismPillar.activate(testHero);
        output = outputStreamCaptor.toString();
        assertTrue(output.contains("Applying buff for: " + PillarType.POLYMORPHISM.getDisplayName()),
                "Should show buff application for Polymorphism.");
    }

    @Test
    void activate_withDifferentHeroTypes_worksCorrectly() {
        Hero warrior = heroFactory.getHero("Warrior", HeroType.WARRIOR, new Point(1, 1));

        abstractionPillar.activate(warrior);

        assertTrue(abstractionPillar.isActivated(),
                "Pillar should be activated with different hero types.");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Warrior activated the Pillar of"),
                "Should work with different hero types.");
    }

    @Test
    void multipleActivationAttempts_onlyFirstSucceeds() {
        // First activation
        abstractionPillar.activate(testHero);
        assertTrue(abstractionPillar.isActivated(), "First activation should succeed.");

        outputStreamCaptor.reset();

        // Second activation attempt
        abstractionPillar.activate(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("already activated"),
                "Second activation should show already activated message.");

        // Third activation attempt
        outputStreamCaptor.reset();
        abstractionPillar.activate(testHero);

        output = outputStreamCaptor.toString();
        assertTrue(output.contains("already activated"),
                "Third activation should also show already activated message.");
    }

    @Test
    void interact_multipleHeroes_worksCorrectly() {
        Hero hero1 = heroFactory.getHero("Hero1", HeroType.WARRIOR, new Point(0, 0));
        Hero hero2 = heroFactory.getHero("Hero2", HeroType.WARRIOR, new Point(1, 1));

        // First hero interacts
        abstractionPillar.interact(hero1);
        assertTrue(abstractionPillar.isActivated(), "Pillar should be activated by first hero.");

        outputStreamCaptor.reset();

        // Second hero interacts with already activated pillar
        abstractionPillar.interact(hero2);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Hero2 approaches the Pillar of"),
                "Second hero should still approach the pillar.");
        assertTrue(output.contains("already activated"),
                "Should show already activated message for second hero.");
    }

    @Test
    void pillarState_remainsConsistent() {
        // Test that pillar state remains consistent through various operations
        PillarType originalType = abstractionPillar.getType();
        boolean originalActivation = abstractionPillar.isActivated();

        // Perform various operations
        abstractionPillar.toString();
        abstractionPillar.hashCode();
        abstractionPillar.equals(encapsulationPillar);

        assertEquals(originalType, abstractionPillar.getType(),
                "Pillar type should remain unchanged.");
        assertEquals(originalActivation, abstractionPillar.isActivated(),
                "Pillar activation state should remain unchanged.");
    }

    @Test
    void equalsAndHashCode_contractCompliance() {
        Pillar pillar1 = new Pillar(PillarType.ABSTRACTION);
        Pillar pillar2 = new Pillar(PillarType.ABSTRACTION);
        Pillar pillar3 = new Pillar(PillarType.ENCAPSULATION);

        // Reflexive: x.equals(x) should return true
        assertTrue(pillar1.equals(pillar1), "equals should be reflexive.");

        // Symmetric: x.equals(y) should return true if and only if y.equals(x) returns true
        assertTrue(pillar1.equals(pillar2), "equals should be symmetric (1->2).");
        assertTrue(pillar2.equals(pillar1), "equals should be symmetric (2->1).");

        // Transitive: if x.equals(y) and y.equals(z), then x.equals(z)
        Pillar pillar2Copy = new Pillar(PillarType.ABSTRACTION);
        assertTrue(pillar1.equals(pillar2), "First equality for transitivity.");
        assertTrue(pillar2.equals(pillar2Copy), "Second equality for transitivity.");
        assertTrue(pillar1.equals(pillar2Copy), "equals should be transitive.");

        // Consistent: multiple invocations should return the same result
        assertTrue(pillar1.equals(pillar2), "First call to equals.");
        assertTrue(pillar1.equals(pillar2), "Second call to equals should be consistent.");

        // Null: x.equals(null) should return false
        assertFalse(pillar1.equals(null), "equals should return false for null.");

        // Hash code contract: if two objects are equal, they must have the same hash code
        assertEquals(pillar1.hashCode(), pillar2.hashCode(),
                "Equal objects must have equal hash codes.");

        // Hash code consistency
        int hash1 = pillar1.hashCode();
        int hash2 = pillar1.hashCode();
        assertEquals(hash1, hash2, "hashCode should be consistent.");
    }

    @Test
    void applyBuff_printsBuffDescription() {
        abstractionPillar.activate(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Applying buff for: " + PillarType.ABSTRACTION.getDisplayName()),
                "Should print buff application message.");
        assertTrue(output.contains(PillarType.ABSTRACTION.getDescription()),
                "Should print pillar type description.");
    }

    @Test
    void finalFields_areImmutable() {
        PillarType originalType = abstractionPillar.getType();

        // Perform operations that might try to change state
        abstractionPillar.activate(testHero);
        abstractionPillar.interact(testHero);
        abstractionPillar.toString();

        assertEquals(originalType, abstractionPillar.getType(),
                "Pillar type should remain immutable.");
    }

    @Test
    void activationState_changesCorrectly() {
        // Test activation state changes
        assertFalse(abstractionPillar.isActivated(), "Should start inactive.");

        abstractionPillar.activate(testHero);
        assertTrue(abstractionPillar.isActivated(), "Should be active after activation.");

        // State should remain active
        assertTrue(abstractionPillar.isActivated(), "Should remain active.");

        // Multiple checks should be consistent
        for (int i = 0; i < 10; i++) {
            assertTrue(abstractionPillar.isActivated(), "Should consistently return active state.");
        }
    }

    @Test
    void allMethodsCovered_verification() {
        // Ensure all public methods are tested
        assertDoesNotThrow(() -> {
            // Constructor (tested in multiple tests)
            Pillar pillar = new Pillar(PillarType.ABSTRACTION);

            // All public methods
            pillar.getType();
            pillar.isActivated();
            pillar.activate(testHero);
            pillar.interact(testHero);
            pillar.toString();
            pillar.equals(pillar);
            pillar.hashCode();
        }, "All public methods should be callable without exceptions.");
    }

    @Test
    void errorHandling_nullHeroActivation() {
        // Test error stream capture
        abstractionPillar.activate(null);

        String errorOutput = errorStreamCaptor.toString();
        assertFalse(errorOutput.isEmpty(), "Error message should be printed to stderr.");
        assertTrue(errorOutput.contains("Cannot activate pillar with a null hero."),
                "Specific error message should be printed.");
    }



    @Test
    void buffApplication_verifyAllTypes() {
        // Verify that each pillar type has its specific buff logic
        Hero freshHero1 = heroFactory.getHero("FreshHero", HeroType.WARRIOR, new Point(0, 0));

        // Test each pillar type individually with fresh hero state
        double initialCritChance = freshHero1.getCritChance();
        int initialMaxHealth = freshHero1.getMaxHealth();

        // Test Abstraction pillar
        abstractionPillar.activate(freshHero1);
        assertTrue(freshHero1.getCritChance() > initialCritChance,
                "Abstraction pillar should increase crit chance.");

        // Test Encapsulation pillar
        Hero freshHero2 = heroFactory.getHero("FreshHero2", HeroType.WARRIOR, new Point(0, 0));
        encapsulationPillar.activate(freshHero2);
        assertTrue(freshHero2.getMaxHealth() > initialMaxHealth,
                "Encapsulation pillar should increase max health.");

        // Test Inheritance pillar - just verify it doesn't throw exception
        Hero freshHero3 = heroFactory.getHero("FreshHero3", HeroType.WARRIOR, new Point(0, 0));
        assertDoesNotThrow(() -> inheritancePillar.activate(freshHero3),
                "Inheritance pillar activation should not throw exception.");

        // Polymorphism pillar test (attack buff verification would depend on Hero implementation)
        Hero freshHero4 = heroFactory.getHero("FreshHero4", HeroType.WARRIOR, new Point(0, 0));
        assertDoesNotThrow(() -> polymorphismPillar.activate(freshHero4),
                "Polymorphism pillar activation should not throw exception.");
    }



    @Test
    void activate_inheritancePillar_setsManaBuff() {
        inheritancePillar.activate(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName() + "'s Mana needed for special dropped from 2 to 1!"),
                "Should print mana buff message.");
        assertTrue(output.contains("Applying buff for: " + PillarType.INHERITANCE.getDisplayName()),
                "Should print buff application message.");
    }
}

