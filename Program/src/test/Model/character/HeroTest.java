package test.Model.character;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.scene.image.Image;
import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.character.Monster;
import main.Model.character.MonsterFactory;
import main.Model.element.HealthPotion; // Make sure this is your actual HealthPotion class
import main.Model.element.Pillar;
import main.Model.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Hero class.
 * (like)
 */
class HeroTest {

    private Hero myWarrior;
    private Monster myDummyMonster;
    private HealthPotion myHealthPotion; // Use the concrete HealthPotion type
    private HeroFactory myHeroFactory;
    private MonsterFactory myMonsterFactory;


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
        assertEquals(new Point(0, 0), myWarrior.getPosition());
        assertNotNull(myWarrior.getInventory());
        assertTrue(myWarrior.getInventory().isEmpty());
        assertEquals(0, myWarrior.getPillarsActivated());
        assertEquals(0, myWarrior.getGold());
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
    void heroMove_updatesPosition() {
        myWarrior.move(Direction.EAST);
        assertEquals(new Point(1,0), myWarrior.getPosition());
    }

    @Test
    void attack_properDamage() {
        myWarrior.attack(myDummyMonster);
        // Crit chance is random and hard to test, so this checks both scenarios.
        assertTrue(25 == myDummyMonster.getHealth() || 10 == myDummyMonster.getHealth());
    }

    @Test
    void activatePillar_properPillarCount() {
        myWarrior.activatePillar(new Pillar(PillarType.ABSTRACTION));
        assertEquals(1, myWarrior.getPillarsActivated());
    }

    @Test
    void hasActivatedAllPillars_properBoolean() {
        assertFalse(myWarrior.hasActivatedAllPillars());
        myWarrior.activatePillar(new Pillar(PillarType.ABSTRACTION));
        myWarrior.activatePillar(new Pillar(PillarType.ENCAPSULATION));
        myWarrior.activatePillar(new Pillar(PillarType.INHERITANCE));
        myWarrior.activatePillar(new Pillar(PillarType.POLYMORPHISM));
        assertTrue(myWarrior.hasActivatedAllPillars());
    }

    @Test
    void specialAttack_properDamage() {
        // This works a bit different from the attack, but it works, and it's
        // too deeply wired into the code to change.
        int damage = myWarrior.specialAttack();
        assertTrue(25 == damage || 50 == damage);
    }

    @Test
    void specialAttack_properMana() {
        assertEquals(2, myWarrior.getSpecialMana());
        myWarrior.specialAttack();
        assertEquals(0, myWarrior.getSpecialMana());
    }

    @Test
    void canUseSpecialAttack_properUseBoolean() {
        assertTrue(myWarrior.canUseSpecialAttack());
        myWarrior.specialAttack();
        assertFalse(myWarrior.canUseSpecialAttack());
    }

    @Test
    void canUseSpecialAttack_withBuff() {
        myWarrior.setManaBuff(true);
        assertTrue(myWarrior.canUseSpecialAttack());
        myWarrior.specialAttack();
        assertTrue(myWarrior.canUseSpecialAttack());
        myWarrior.specialAttack();
        assertFalse(myWarrior.canUseSpecialAttack());
    }

    @Test
    void addMana_properMana() {
        myWarrior.addMana();
        assertEquals(3, myWarrior.getSpecialMana());
        myWarrior.addMana();
        myWarrior.addMana();
        assertEquals(4, myWarrior.getSpecialMana());
    }

    @Test
    void takeDamage_properHealth() {
        myWarrior.takeDamage(30);
        assertEquals(95, myWarrior.getHealth());
    }

    @Test
    void addGold_properGold() {
        myWarrior.addGold(20);
        assertEquals(20, myWarrior.getGold());
    }

    @Test
    void spendGold_properGold() {
        myWarrior.addGold(20);
        assertTrue(myWarrior.spendGold(9));
        assertEquals(11, myWarrior.getGold());
    }

    @Test
    void spendGold_noGold() {
        assertFalse(myWarrior.spendGold(9));
    }

    @Test
    void setPixelPosition_properPosition() {
        myWarrior.setPixelPosition(2, 5);
        assertEquals(2, myWarrior.getPixelX());
        assertEquals(5, myWarrior.getPixelY());
    }

    // This tests all movement here because they are short, and also because
    // they help test each other.
    @Test
    void stopMovingMethods_properMovement() {
        myWarrior.startMovingNorth();
        assertTrue(myWarrior.isMoving());
        myWarrior.startMovingSouth();
        myWarrior.stopMovingNorth();
        myWarrior.stopMovingSouth();
        assertFalse(myWarrior.isMoving());
        myWarrior.startMovingEast();
        assertTrue(myWarrior.isMoving());
        myWarrior.stopMovingEast();
        assertFalse(myWarrior.isMoving());
        myWarrior.startMovingWest();
        assertTrue(myWarrior.isMoving());
        myWarrior.stopMovingWest();
        assertFalse(myWarrior.isMoving());
    }

    @Test
    void setMovementSpeedForCanvasSize_properSpeed() {
        myWarrior.setPixelPosition(50, 50);
        myWarrior.setMovementSpeedForCanvasSize(100);
        myWarrior.startMovingNorth();
        myWarrior.updatePixelPosition();
        assertEquals(49, myWarrior.getPixelY());
    }

    @Test
    void updatePixelPosition_properCoordinate() {
        myWarrior.setPixelPosition(50, 50);
        myWarrior.startMovingWest();
        myWarrior.updatePixelPosition();
        assertEquals(48, myWarrior.getPixelX());
    }

    @Test
    void isMoving_properBoolean() {
        myWarrior.startMovingWest();
        assertTrue(myWarrior.isMoving());
    }

    @Test
    void updateAnimation_properRow() {
        myWarrior.startMovingWest();
        myWarrior.updateAnimation(4);
        assertEquals(3 , myWarrior.getAnimationRow());
    }

    @Test
    void getSpriteSheet_sameWidthsAndHeights() {
        String spritePath = "/sprites/heroes/warrior_walk_spritesheet.png";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(spritePath)));
        Image warriorImage = myWarrior.getSpriteSheet();
        assertEquals(image.getWidth(), warriorImage.getWidth());
        assertEquals(image.getHeight(), warriorImage.getHeight());
    }

    @Test
    void getCurrentFrameX_properFrame() {
        myWarrior.updateAnimation(76);
        // Should set to 1 during update method due to hero not moving.
        assertEquals(1, myWarrior.getCurrentFrameX());
    }

    @Test
    void getAnimationRow_() {
        myWarrior.startMovingNorth();
        myWarrior.updateAnimation(23);
        assertEquals(2, myWarrior.getAnimationRow());
    }

    @Test
    void getHealthPercentage_properPercent() {
        assertEquals(1, myWarrior.getHealthPercentage());
        myWarrior.takeDamage(35);
        assertEquals(0.72, myWarrior.getHealthPercentage());
    }

    @Test
    void getHealthDisplay_properDisplay() {
        assertTrue("125 / 125".equals(myWarrior.getHealthDisplay()));
        myWarrior.takeDamage(15);
        assertTrue("110 / 125".equals(myWarrior.getHealthDisplay()));
    }

    @Test
    void getSpecialMana_properMana() {
        assertEquals(2, myWarrior.getSpecialMana());
        myWarrior.addMana();
        assertEquals(3, myWarrior.getSpecialMana());
        myWarrior.specialAttack();
        assertEquals(1, myWarrior.getSpecialMana());
    }

    @Test
    void getType_properType() {
        assertEquals(HeroType.WARRIOR, myWarrior.getType());
    }

    @Test
    void getGold_properGold() {
        assertEquals(0, myWarrior.getGold());
        myWarrior.addGold(23);
        assertEquals(23, myWarrior.getGold());
    }

    @Test
    void getPixel_properSpot() {
        myWarrior.setPixelPosition(34, 23.2);
        assertEquals(34, myWarrior.getPixelX());
        assertEquals(23.2, myWarrior.getPixelY());
    }

    @Test
    void setManaBuff_buffActivated() {
        myWarrior.setManaBuff(true);
        myWarrior.specialAttack();
        assertEquals(1, myWarrior.getSpecialMana());
        myWarrior.setManaBuff(false);
        assertFalse(myWarrior.canUseSpecialAttack());
    }

    @Test
    void addAttackBuff_getsAndAddsBuff() {
        assertEquals(0, myWarrior.getAttackBuff());
        myWarrior.addAttackBuff(15);
        assertEquals(15, myWarrior.getAttackBuff());
    }

    @Test
    void getBossSlain_setAndGet() {
        assertFalse(myWarrior.getBossSlain());
        myWarrior.setBossSlain(true);
        assertTrue(myWarrior.getBossSlain());
    }

    @Test
    void synchronizePositions_correctCoordinate() {
        myWarrior.synchronizePositions(20, 20);
        assertEquals(10, myWarrior.getPixelX());
        assertEquals(10, myWarrior.getPixelX());
    }

    @Test
    void resetMovementState() {
        myWarrior.startMovingWest();
        myWarrior.startMovingSouth();
        myWarrior.startMovingNorth();
        myWarrior.startMovingEast();
        myWarrior.resetMovementState();
        assertFalse(myWarrior.isMoving());
    }

    // The MovementState and AnimationState inner classes are tested indirectly through
    // the movement and animation related tests above.

    @Test
    void setHeroType_properType() {
        Hero hero = new Hero.HeroBuilder().setHeroType(HeroType.THIEF).build();
        assertEquals(hero.getType(), HeroType.THIEF);
    }

    @Test
    void build_properBuild() {
        Hero hero = new Hero.HeroBuilder().
                             setHeroType(HeroType.THIEF).
                             setMaxHealth(2).
                             build();
        // Null means the build did not assign it, so it's correct here.
        assertNull(hero.getPosition());
        assertEquals(hero.getMaxHealth(), 2);
    }

    @Test
    void toJson_properJson() {
        String json = myWarrior.toJson();
        String properJson = "{\"name\":\"Conan\",\"heroType\":\"WARRIOR\",\"health\":125,\"maxHealth\":125," +
                "\"attackBuff\":0,\"specialMana\":2,\"manaBuff\":false,\"gold\":0,\"pillarsActivated\":0,\"positionX\"" +
                ":0,\"positionY\":0,\"pixelX\":0.0,\"pixelY\":0.0,\"inventoryItems\":[]}";
        assertTrue(json.equals(properJson));
    }

    @Test
    void fromJson_properJson() {
        Hero hero = Hero.fromJson(myWarrior.toJson());
        String heroJson = hero.toJson();
        assertTrue(heroJson.equals(myWarrior.toJson()));
    }

    @Test
    void fromJson_nullCheck() {
        assertNull(Hero.fromJson("This will give error hopefully"));
    }
}
