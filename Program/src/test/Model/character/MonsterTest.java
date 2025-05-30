package test.Model.character;

import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.character.Monster;
import main.Model.character.MonsterFactory;
import main.Model.element.HealthPotion;
import main.Model.element.Item;
import main.Model.util.HeroType;
import main.Model.util.MonsterType;
import main.Model.util.Point;
import main.Model.util.Direction; // Added for move tests

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Monster class.
 */
class MonsterTest {

    private Monster regularMonster;
    private Monster eliteMonster;
    private Hero dummyHero; // For attack target
    private HeroFactory myHeroFactory;
    private MonsterFactory myMonsterFactory;

    // TODO: Add tests for stuff in factories, builders, etc.

    @BeforeEach
    void setUp() {
        myHeroFactory = new HeroFactory();
        myMonsterFactory = new MonsterFactory();
        regularMonster = myMonsterFactory.getMonster(MonsterType.GOBLIN, new Point(1,1));
        eliteMonster = myMonsterFactory.getMonster(MonsterType.ORC, new Point(2, 2));
        dummyHero = myHeroFactory.getHero("Dummy", HeroType.WARRIOR, new Point(0, 0));
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        assertEquals("Goblin", regularMonster.getName(), "Monster name should be initialized.");
        assertEquals(MonsterType.GOBLIN, regularMonster.getType(), "Monster type should be initialized.");
        assertFalse(regularMonster.isElite(), "Monster should not be elite by default in this setup.");
        assertEquals(30, regularMonster.getHealth(), "Monster initial health should be set.");
        assertEquals(new Point(1, 1), regularMonster.getPosition(), "Monster initial position should be set.");
        assertNotNull(regularMonster.getRewardOnDefeat(), "Rewards list should be initialized.");
        assertTrue(regularMonster.getRewardOnDefeat().isEmpty(), "Rewards list should be initially empty.");

        assertTrue(eliteMonster.isElite(), "Elite monster should be set as elite.");
    }

    @Test
    void attack_regularMonster_dealsBaseDamage() {
        int damageDealt = regularMonster.attack(dummyHero);
        // Current Monster.attack() logic: baseDamage = 5. If elite, baseDamage *= 2;
        assertEquals(5, damageDealt, "Regular monster should deal base damage.");
    }

    @Test
    void attack_eliteMonster_dealsDoubleDamage() {
        int damageDealt = eliteMonster.attack(dummyHero);
        assertEquals(10, damageDealt, "Elite monster should deal double base damage.");
    }

    @Test
    void addReward_addsItemToRewardsList() {
        Item potion = new HealthPotion("Monster Potion", "A potion dropped by a monster", 10);
        assertTrue(regularMonster.getRewardOnDefeat().isEmpty(), "Rewards should be empty initially.");
        regularMonster.addReward(potion);
        assertEquals(1, regularMonster.getRewardOnDefeat().size(), "Rewards list should have one item.");
        assertTrue(regularMonster.getRewardOnDefeat().contains(potion), "Rewards list should contain the added item.");
    }

    @Test
    void setElite_changesEliteStatus() {
        assertFalse(regularMonster.isElite(), "Monster should initially not be elite.");
        regularMonster.setElite(true);
        assertTrue(regularMonster.isElite(), "Monster should be elite after setting.");
    }

    // Inherited Character methods
    @Test
    void monsterTakeDamage_reducesHealth() {
        regularMonster.takeDamage(10);
        assertEquals(20, regularMonster.getHealth());
    }

    @Test
    void monsterIsAlive_returnsFalseWhenHealthIsZero() {
        regularMonster.takeDamage(30);
        assertFalse(regularMonster.isAlive());
    }

    @Test
    void monsterMove_updatesPosition() {
        regularMonster.move(Direction.SOUTH);
        assertEquals(new Point(1, 2), regularMonster.getPosition());
    }
}
