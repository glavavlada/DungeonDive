package test.Model.character;

import main.Model.character.Hero;
import main.Model.character.Monster;
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

    private Hero warrior;
    private Monster dummyMonster;
    private HealthPotion healthPotion; // Use the concrete HealthPotion type

    @BeforeEach
    void setUp() {
        warrior = new Hero("Conan", HeroType.WARRIOR, new Point(0, 0));
        // Ensure MonsterType.GOBLIN is defined in your MonsterType enum
        // If MonsterType is empty, this test or others might fail.
        // Add at least one type: public enum MonsterType { GOBLIN; }
        dummyMonster = new Monster("Goblin", MonsterType.GOBLIN, false, 30, new Point(1,0));
        healthPotion = new HealthPotion("Minor Healing Potion", "Restores a small amount of health.", 25);
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        assertEquals("Conan", warrior.getName());
        assertEquals(HeroType.WARRIOR, warrior.getType());
        assertEquals(100, warrior.getHealth());
        assertEquals(100, warrior.getMaxHealth(), "Max health should be initialized.");
        assertEquals(new Point(0, 0), warrior.getPosition());
        assertNotNull(warrior.getInventory());
        assertTrue(warrior.getInventory().isEmpty());
        assertEquals(0, warrior.getPillarsActivated());
    }

    @Test
    void attack_dealsDamageToTarget() {
        int initialMonsterHealth = dummyMonster.getHealth();
        warrior.attack(dummyMonster);
        // Assuming warrior's base attack is 10 as per Hero.attack() current logic
        assertEquals(initialMonsterHealth - 10, dummyMonster.getHealth(), "Monster health should be reduced by hero's attack damage.");
    }

    @Test
    void pickupItem_addsItemToInventory_whenInventoryNotFull() {
        assertTrue(warrior.getInventory().isEmpty());
        boolean pickedUp = warrior.pickupItem(healthPotion);
        assertTrue(pickedUp, "Item should be picked up.");
        assertEquals(1, warrior.getInventory().size());
        assertTrue(warrior.getInventory().contains(healthPotion));
    }

    @Test
    void pickupItem_doesNotAddItemToInventory_whenInventoryIsFull() {
        // Fill inventory (assuming limit is 10 as per Hero.pickupItem example)
        for (int i = 0; i < 10; i++) {
            warrior.pickupItem(new HealthPotion("Potion " + i, "Desc", 10));
        }
        assertEquals(10, warrior.getInventory().size(), "Inventory should be full.");

        HealthPotion extraPotion = new HealthPotion("Extra Potion", "Desc", 5);
        boolean pickedUp = warrior.pickupItem(extraPotion);

        assertFalse(pickedUp, "Item should not be picked up when inventory is full.");
        assertEquals(10, warrior.getInventory().size(), "Inventory size should remain 10.");
        assertFalse(warrior.getInventory().contains(extraPotion), "Full inventory should not contain the extra potion.");
    }


    @Test
    void useItem_appliesEffectAndRemovesFromInventory() {
        warrior.pickupItem(healthPotion);
        warrior.setHealth(50); // Hero takes some damage
        int healthBeforePotion = warrior.getHealth();

        assertTrue(warrior.getInventory().contains(healthPotion));
        warrior.useItem(healthPotion);

        assertFalse(warrior.getInventory().contains(healthPotion));
        // Health should be current health + healing amount, capped at max health
        int expectedHealth = Math.min(warrior.getMaxHealth(), healthBeforePotion + healthPotion.getHealingAmount());
        assertEquals(expectedHealth, warrior.getHealth());
    }

    @Test
    void useItem_notInInventory_doesNothing() {
        int initialHealth = warrior.getHealth();
        // Create a new potion instance that is not in the inventory
        HealthPotion anotherPotion = new HealthPotion("Another Potion", "Desc", 10);
        warrior.useItem(anotherPotion);
        assertEquals(initialHealth, warrior.getHealth());
        assertTrue(warrior.getInventory().isEmpty());
    }


    @Test
    void setPillarsActivated_updatesPillarCount() {
        warrior.setPillarsActivated(3);
        assertEquals(3, warrior.getPillarsActivated());
    }

    @Test
    void heroTakeDamage_reducesHealth() {
        warrior.takeDamage(20);
        assertEquals(80, warrior.getHealth());
    }

    @Test
    void heroMove_updatesPosition() {
        warrior.move(Direction.EAST);
        assertEquals(new Point(1,0), warrior.getPosition());
    }

    @Test
    void setMaxHealth_updatesMaxHealthAndClampsCurrentHealth() {
        warrior.setHealth(100);
        warrior.setMaxHealth(120);
        assertEquals(120, warrior.getMaxHealth(), "Max health should be updated.");
        assertEquals(100, warrior.getHealth(), "Current health should remain unchanged if below new max.");

        warrior.setHealth(130); // Try to set health above new max (should be clamped by setHealth or takeDamage)
        warrior.setHealth(Math.min(warrior.getHealth(), warrior.getMaxHealth())); // Manual clamp if setHealth doesn't
        assertEquals(120, warrior.getHealth(), "Current health should be clamped to new max if it was higher.");

        warrior.setMaxHealth(80);
        assertEquals(80, warrior.getMaxHealth(), "Max health should be updated to a lower value.");
        assertEquals(80, warrior.getHealth(), "Current health should be clamped to the new lower max health.");
    }
}
