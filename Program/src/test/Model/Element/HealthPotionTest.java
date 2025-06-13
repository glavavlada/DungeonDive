package test.Model.Element;

import main.Model.element.HealthPotion;
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
 * Unit tests for the HealthPotion class.
 * Tests all methods and functionality to ensure 100% coverage.
 */
class HealthPotionTest {

    private HealthPotion smallPotion;
    private HealthPotion mediumPotion;
    private HealthPotion largePotion;
    private HealthPotion zeroHealingPotion;
    private Hero testHero;
    private HeroFactory heroFactory;

    // For capturing console output
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    void setUp() {
        smallPotion = new HealthPotion("Minor Healing Potion", "Restores a small amount of health.", 25);
        mediumPotion = new HealthPotion("Healing Potion", "Restores a moderate amount of health.", 50);
        largePotion = new HealthPotion("Greater Healing Potion", "Restores a large amount of health.", 100);
        zeroHealingPotion = new HealthPotion("Broken Potion", "A broken potion that heals nothing.", 0);

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
    void constructor_initializesPropertiesCorrectly() {
        assertEquals("Minor Healing Potion", smallPotion.getName(),
                    "Name should be initialized correctly.");
        assertEquals("Restores a small amount of health.", smallPotion.getDescription(),
                    "Description should be initialized correctly.");
        assertEquals(25, smallPotion.getHealingAmount(),
                    "Healing amount should be initialized correctly.");
    }

    @Test
    void constructor_withDifferentValues_initializesCorrectly() {
        assertEquals("Healing Potion", mediumPotion.getName(),
                    "Medium potion name should be correct.");
        assertEquals(50, mediumPotion.getHealingAmount(),
                    "Medium potion healing amount should be correct.");

        assertEquals("Greater Healing Potion", largePotion.getName(),
                    "Large potion name should be correct.");
        assertEquals(100, largePotion.getHealingAmount(),
                    "Large potion healing amount should be correct.");
    }

    @Test
    void constructor_withZeroHealing_initializesCorrectly() {
        assertEquals("Broken Potion", zeroHealingPotion.getName(),
                    "Zero healing potion name should be correct.");
        assertEquals(0, zeroHealingPotion.getHealingAmount(),
                    "Zero healing potion should have 0 healing amount.");
    }

    @Test
    void constructor_withNegativeHealing_initializesCorrectly() {
        HealthPotion negativePotion = new HealthPotion("Poison", "Actually damages you.", -10);
        assertEquals(-10, negativePotion.getHealingAmount(),
                    "Negative healing amount should be allowed.");
    }

    @Test
    void getHealingAmount_returnsCorrectValue() {
        assertEquals(25, smallPotion.getHealingAmount(),
                    "Small potion should return correct healing amount.");
        assertEquals(50, mediumPotion.getHealingAmount(),
                    "Medium potion should return correct healing amount.");
        assertEquals(100, largePotion.getHealingAmount(),
                    "Large potion should return correct healing amount.");
        assertEquals(0, zeroHealingPotion.getHealingAmount(),
                    "Zero healing potion should return 0.");
    }

    @Test
    void use_withHealthyHero_healsCorrectAmount() {
        // Set hero to partial health
        testHero.setHealth(50);
        int initialHealth = testHero.getHealth();

        smallPotion.use(testHero);

        assertEquals(initialHealth + 25, testHero.getHealth(),
                    "Hero health should increase by healing amount.");
    }

    @Test
    void use_withFullHealthHero_doesNotExceedMaxHealth() {
        // Assume hero starts at full health (100)
        int maxHealth = testHero.getMaxHealth();
        testHero.setHealth(maxHealth);

        smallPotion.use(testHero);

        assertEquals(maxHealth, testHero.getHealth(),
                    "Hero health should not exceed max health.");
    }

    @Test
    void use_withNearMaxHealthHero_capsAtMaxHealth() {
        int maxHealth = testHero.getMaxHealth();
        testHero.setHealth(maxHealth - 10); // 10 points below max

        smallPotion.use(testHero); // Heals 25, but should cap at max

        assertEquals(maxHealth, testHero.getHealth(),
                    "Hero health should be capped at max health.");
    }

    @Test
    void use_withLowHealthHero_healsCorrectly() {
        testHero.setHealth(1); // Very low health

        mediumPotion.use(testHero); // Heals 50

        assertEquals(51, testHero.getHealth(),
                    "Hero should be healed from low health correctly.");
    }

    @Test
    void use_withZeroHealingPotion_doesNotChangeHealth() {
        testHero.setHealth(50);
        int initialHealth = testHero.getHealth();

        zeroHealingPotion.use(testHero);

        assertEquals(initialHealth, testHero.getHealth(),
                    "Zero healing potion should not change hero health.");
    }

    @Test
    void use_withNegativeHealingPotion_damagesHero() {
        HealthPotion poisonPotion = new HealthPotion("Poison", "Damages you.", -20);
        testHero.setHealth(50);

        poisonPotion.use(testHero);

        assertEquals(30, testHero.getHealth(),
                    "Negative healing should damage the hero.");
    }

    @Test
    void use_withNullHero_doesNotThrowException() {
        assertDoesNotThrow(() -> smallPotion.use(null),
                          "Using potion with null hero should not throw exception.");
    }

    @Test
    void use_withDeadHero_doesNotHeal() {
        testHero.setHealth(0); // Dead hero
        assertFalse(testHero.isAlive(), "Hero should be dead.");

        smallPotion.use(testHero);

        assertEquals(0, testHero.getHealth(),
                    "Dead hero should not be healed.");
    }

    @Test
    void use_printsCorrectMessage() {
        testHero.setHealth(50);

        smallPotion.use(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testHero.getName()),
                  "Output should contain hero name.");
        assertTrue(output.contains(smallPotion.getName()),
                  "Output should contain potion name.");
        assertTrue(output.contains("25"),
                  "Output should contain healing amount.");
        assertTrue(output.contains("restored"),
                  "Output should contain 'restored' message.");
    }

    @Test
    void use_withDifferentPotions_printsDifferentMessages() {
        testHero.setHealth(50);

        // Test small potion
        smallPotion.use(testHero);
        String smallPotionOutput = outputStreamCaptor.toString();

        // Reset output capture
        outputStreamCaptor.reset();

        // Test medium potion
        mediumPotion.use(testHero);
        String mediumPotionOutput = outputStreamCaptor.toString();

        assertNotEquals(smallPotionOutput, mediumPotionOutput,
                       "Different potions should produce different output messages.");
        assertTrue(mediumPotionOutput.contains("50"),
                  "Medium potion output should contain its healing amount.");
    }

    @Test
    void use_withNullHero_doesNotPrintMessage() {
        smallPotion.use(null);

        String output = outputStreamCaptor.toString();
        assertTrue(output.isEmpty(),
                  "No message should be printed when hero is null.");
    }

    @Test
    void use_withDeadHero_doesNotPrintMessage() {
        testHero.setHealth(0);

        smallPotion.use(testHero);

        String output = outputStreamCaptor.toString();
        assertTrue(output.isEmpty(),
                  "No message should be printed when hero is dead.");
    }

    @Test
    void use_multipleUses_worksCorrectly() {
        testHero.setHealth(10);

        // Use multiple potions
        smallPotion.use(testHero); // +25, total = 35
        assertEquals(35, testHero.getHealth(), "First potion use should work.");

        smallPotion.use(testHero); // +25, total = 60
        assertEquals(60, testHero.getHealth(), "Second potion use should work.");

        largePotion.use(testHero); // +100, but capped at max health
        assertEquals(testHero.getMaxHealth(), testHero.getHealth(),
                    "Large potion should cap at max health.");
    }

    @Test
    void inheritanceFromItem_worksCorrectly() {
        // Test that HealthPotion properly inherits from Item
        assertTrue(smallPotion instanceof main.Model.element.Item,
                  "HealthPotion should be instance of Item.");

        // Test inherited methods work
        assertNotNull(smallPotion.getName(), "getName should work from Item inheritance.");
        assertNotNull(smallPotion.getDescription(), "getDescription should work from Item inheritance.");
    }

    @Test
    void use_overridesItemUseMethod() {
        // Test that the use method is properly overridden
        testHero.setHealth(50);
        int initialHealth = testHero.getHealth();

        // Call use method (should be overridden version)
        smallPotion.use(testHero);

        assertNotEquals(initialHealth, testHero.getHealth(),
                       "Overridden use method should change hero health.");
    }

    @Test
    void potionProperties_areImmutable() {
        // Test that potion properties don't change after use
        String originalName = smallPotion.getName();
        String originalDescription = smallPotion.getDescription();
        int originalHealingAmount = smallPotion.getHealingAmount();

        smallPotion.use(testHero);

        assertEquals(originalName, smallPotion.getName(),
                    "Potion name should not change after use.");
        assertEquals(originalDescription, smallPotion.getDescription(),
                    "Potion description should not change after use.");
        assertEquals(originalHealingAmount, smallPotion.getHealingAmount(),
                    "Potion healing amount should not change after use.");
    }

    @Test
    void extremeHealingValues_workCorrectly() {
        HealthPotion extremePotion = new HealthPotion("Extreme Potion", "Extreme healing.", Integer.MAX_VALUE);
        testHero.setHealth(1);

        extremePotion.use(testHero);

        assertEquals(testHero.getMaxHealth(), testHero.getHealth(),
                    "Extreme healing should be capped at max health.");
    }

    @Test
    void extremeNegativeHealingValues_workCorrectly() {
        HealthPotion extremePoisonPotion = new HealthPotion("Extreme Poison", "Extreme damage.", Integer.MIN_VALUE);
        testHero.setHealth(50);

        extremePoisonPotion.use(testHero);

        assertEquals(0, testHero.getHealth(),
                    "Extreme negative healing should not make health go below 0.");
    }

    @Test
    void use_withDifferentHeroTypes_worksCorrectly() {
        // Test with different hero types if available
        Hero warrior = heroFactory.getHero("Warrior", HeroType.WARRIOR, new Point(0, 0));
        warrior.setHealth(50);

        smallPotion.use(warrior);

        assertEquals(75, warrior.getHealth(),
                    "Potion should work with different hero types.");
    }

    @Test
    void mathMinFunction_worksCorrectlyInUse() {
        // Test the Math.min logic in the use method
        testHero.setHealth(90); // 10 below max (assuming max is 100)
        int maxHealth = testHero.getMaxHealth();

        largePotion.use(testHero); // Would heal 100, but should cap at max

        assertEquals(maxHealth, testHero.getHealth(),
                    "Math.min should properly cap health at maximum.");
    }

    @Test
    void finalFields_areProperlyInitialized() {
        // Test that the final field myHealingAmount is properly set
        HealthPotion testPotion = new HealthPotion("Test", "Test description", 42);
        assertEquals(42, testPotion.getHealingAmount(),
                    "Final healing amount field should be properly initialized.");
    }

    @Test
    void superConstructor_isCalledCorrectly() {
        // Test that the super constructor (Item) is called correctly
        HealthPotion testPotion = new HealthPotion("SuperTest", "Super description", 30);

        assertEquals("SuperTest", testPotion.getName(),
                    "Super constructor should set name correctly.");
        assertEquals("Super description", testPotion.getDescription(),
                    "Super constructor should set description correctly.");
    }
}
