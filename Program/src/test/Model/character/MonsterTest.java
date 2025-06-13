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
        assertEquals(40, regularMonster.getHealth(), "Monster initial health should be set.");
        assertEquals(new Point(1, 1), regularMonster.getPosition(), "Monster initial position should be set.");
        assertNotNull(regularMonster.getRewardOnDefeat(), "Rewards list should be initialized.");
        assertTrue(regularMonster.getRewardOnDefeat().isEmpty(), "Rewards list should be initially empty.");
        assertTrue(eliteMonster.isElite(), "Elite monster should be set as elite.");
        assertEquals(5, regularMonster.getGoldReward());
        assertEquals(10, eliteMonster.getGoldReward());
    }

    @Test
    void attack_regularMonster_dealsBaseDamage() {
        // Checks for all four damage scenarios. Loops to make sure.
        for (int i = 0; i < 100; i++) {
            int damageDealt = regularMonster.attack(dummyHero);
            assertTrue(8 == damageDealt || 10 == damageDealt || 16 == damageDealt || 20 == damageDealt);
        }
    }

    @Test
    void attack_eliteMonster_dealsDoubleDamage() {
        for (int i = 0; i < 100; i++) {
            int damageDealt = eliteMonster.attack(dummyHero);
            assertTrue(22 == damageDealt || 44 == damageDealt || 27 == damageDealt || 54 == damageDealt);
        }
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
        assertEquals(30, regularMonster.getHealth());
    }

    @Test
    void monsterIsAlive_returnsFalseWhenHealthIsZero() {
        regularMonster.takeDamage(40);
        assertFalse(regularMonster.isAlive());
    }

    @Test
    void monsterMove_updatesPosition() {
        regularMonster.move(Direction.SOUTH);
        assertEquals(new Point(1, 2), regularMonster.getPosition());
    }

    @Test
    void getGoldReward_properGold() {
        assertEquals(5, regularMonster.getGoldReward());
        assertEquals(10, eliteMonster.getGoldReward());
    }

    @Test
    void getGoldReward_isDefeated() {
        regularMonster.getGoldReward();
        assertTrue(regularMonster.getIsDefeated());
    }

    @Test
    void getIsDefeated_properBoolean() {
        assertFalse(regularMonster.getIsDefeated());
        regularMonster.getGoldReward();
        assertTrue(regularMonster.getIsDefeated());
    }

    @Test
    void getHealthDisplay_properDisplay() {
        assertTrue("40 / 40".equals(regularMonster.getHealthDisplay()));
        assertTrue("80 / 80".equals(eliteMonster.getHealthDisplay()));
        regularMonster.takeDamage(15);
        eliteMonster.takeDamage(17);
        assertTrue("25 / 40".equals(regularMonster.getHealthDisplay()));
        assertTrue("63 / 80".equals(eliteMonster.getHealthDisplay()));
    }

    @Test
    void setMonsterType_properBuild() {
        // setIsElite is needed here because boolean tries to auto set to null and gives error
        Monster monster = new Monster.MonsterBuilder().setMonsterType(MonsterType.GIANT).setIsElite(false).build();
        assertEquals(MonsterType.GIANT, monster.getType());
    }

    @Test
    void setIsElite_properBuild() {
        Monster monster = new Monster.MonsterBuilder().setIsElite(true).build();
        assertTrue(monster.isElite());
    }

    @Test
    void setGoldReward_properBuild() {
        Monster monster = new Monster.MonsterBuilder().setGoldReward(32423).setIsElite(false).build();
        assertEquals(32423, monster.getGoldReward());
    }

    @Test
    void build_properMonsterBuild() {
        Monster monster = new Monster.MonsterBuilder().
                setGoldReward(32423).
                setIsElite(false).
                setMaxHealth(4).
                build();
        // Non-set int fields should automatically be 0.
        assertEquals(monster.getBaseAttackDamage(), 0);
        assertEquals(0, monster.getHealth());
        // Checks assigned values here.
        assertEquals(monster.getGoldReward(), 32423);
        assertFalse(monster.isElite());
        assertEquals(4, monster.getMaxHealth());

    }
}
