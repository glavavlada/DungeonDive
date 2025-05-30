package test.Model.character;

import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.character.Monster;
import main.Model.character.MonsterFactory;
import main.Model.element.HealthPotion; // Make sure this is your actual HealthPotion class
import main.Model.util.HeroType;
import main.Model.util.MonsterType; // For dummyMonster
import main.Model.util.Point;
import main.Model.util.Direction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Hero class.
 */
class HeroTest {

    private Hero myWarrior;
    private Monster myDummyMonster;
    private HealthPotion myHealthPotion; // Use the concrete HealthPotion type
    private HeroFactory myHeroFactory;
    private MonsterFactory myMonsterFactory;


    // TODO: Add tests for stuff in factories, builders, etc.

    @BeforeEach
    void setUp() {

        myHeroFactory = new HeroFactory();
        myMonsterFactory = new MonsterFactory();

        myWarrior = myHeroFactory.getHero("Conan", HeroType.WARRIOR, new Point(0, 0));
        // Ensure MonsterType.GOBLIN is defined in your MonsterType enum
        // If MonsterType is empty, this test or others might fail.
        // Add at least one type: public enum MonsterType { GOBLIN; }
        myDummyMonster = myMonsterFactory.getMonster(MonsterType.GOBLIN, new Point(1,0));
        myHealthPotion = new HealthPotion("Minor Healing Potion", "Restores a small amount of health.", 25);
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        assertEquals("Conan", myWarrior.getName());
        assertEquals(HeroType.WARRIOR, myWarrior.getType());
        assertEquals(100, myWarrior.getHealth());
        assertEquals(100, myWarrior.getMaxHealth(), "Max health should be initialized.");
        assertEquals(new Point(0, 0), myWarrior.getPosition());
        assertNotNull(myWarrior.getInventory());
        assertTrue(myWarrior.getInventory().isEmpty());
        assertEquals(0, myWarrior.getPillarsActivated());
    }

    @Test
    void attack_dealsDamageToTarget() {
        int initialMonsterHealth = myDummyMonster.getHealth();
        myWarrior.attack(myDummyMonster);
        // Assuming warrior's base attack is 10 as per Hero.attack() current logic
        assertEquals(initialMonsterHealth - 10, myDummyMonster.getHealth(), "Monster health should be reduced by hero's attack damage.");
    }

    @Test
    void pickupItem_addsItemToInventory_whenInventoryNotFull() {
        assertTrue(myWarrior.getInventory().isEmpty());
        boolean pickedUp = myWarrior.pickupItem(myHealthPotion);
        assertTrue(pickedUp, "Item should be picked up.");
        assertEquals(1, myWarrior.getInventory().size());
        assertTrue(myWarrior.getInventory().contains(myHealthPotion));
    }

    @Test
    void pickupItem_doesNotAddItemToInventory_whenInventoryIsFull() {
        // Fill inventory (assuming limit is 10 as per Hero.pickupItem example)
        for (int i = 0; i < 10; i++) {
            myWarrior.pickupItem(new HealthPotion("Potion " + i, "Desc", 10));
        }
        assertEquals(10, myWarrior.getInventory().size(), "Inventory should be full.");

        HealthPotion extraPotion = new HealthPotion("Extra Potion", "Desc", 5);
        boolean pickedUp = myWarrior.pickupItem(extraPotion);

        assertFalse(pickedUp, "Item should not be picked up when inventory is full.");
        assertEquals(10, myWarrior.getInventory().size(), "Inventory size should remain 10.");
        assertFalse(myWarrior.getInventory().contains(extraPotion), "Full inventory should not contain the extra potion.");
    }


    @Test
    void useItem_appliesEffectAndRemovesFromInventory() {
        myWarrior.pickupItem(myHealthPotion);
        myWarrior.setHealth(50); // Hero takes some damage
        int healthBeforePotion = myWarrior.getHealth();

        assertTrue(myWarrior.getInventory().contains(myHealthPotion));
        myWarrior.useItem(myHealthPotion);

        assertFalse(myWarrior.getInventory().contains(myHealthPotion));
        // Health should be current health + healing amount, capped at max health
        int expectedHealth = Math.min(myWarrior.getMaxHealth(), healthBeforePotion + myHealthPotion.getHealingAmount());
        assertEquals(expectedHealth, myWarrior.getHealth());
    }

    @Test
    void useItem_notInInventory_doesNothing() {
        int initialHealth = myWarrior.getHealth();
        // Create a new potion instance that is not in the inventory
        HealthPotion anotherPotion = new HealthPotion("Another Potion", "Desc", 10);
        myWarrior.useItem(anotherPotion);
        assertEquals(initialHealth, myWarrior.getHealth());
        assertTrue(myWarrior.getInventory().isEmpty());
    }


    @Test
    void setPillarsActivated_updatesPillarCount() {
        myWarrior.setPillarsActivated(3);
        assertEquals(3, myWarrior.getPillarsActivated());
    }

    @Test
    void heroTakeDamage_reducesHealth() {
        myWarrior.takeDamage(20);
        assertEquals(80, myWarrior.getHealth());
    }

    @Test
    void heroMove_updatesPosition() {
        myWarrior.move(Direction.EAST);
        assertEquals(new Point(1,0), myWarrior.getPosition());
    }

//    @Test
//    void setMaxHealth_updatesMaxHealthAndClampsCurrentHealth() {
//        myWarrior.setHealth(100);
//        myWarrior.setMaxHealth(120);
//        assertEquals(120, myWarrior.getMaxHealth(), "Max health should be updated.");
//        assertEquals(100, myWarrior.getHealth(), "Current health should remain unchanged if below new max.");
//
//        myWarrior.setHealth(130); // Try to set health above new max (should be clamped by setHealth or takeDamage)
//        myWarrior.setHealth(Math.min(myWarrior.getHealth(), myWarrior.getMaxHealth())); // Manual clamp if setHealth doesn't
//        assertEquals(120, myWarrior.getHealth(), "Current health should be clamped to new max if it was higher.");
//
//        myWarrior.setMaxHealth(80);
//        assertEquals(80, myWarrior.getMaxHealth(), "Max health should be updated to a lower value.");
//        assertEquals(80, myWarrior.getHealth(), "Current health should be clamped to the new lower max health.");
//    }
}
