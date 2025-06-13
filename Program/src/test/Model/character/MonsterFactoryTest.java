package test.Model.character;
import main.Model.character.Monster;
import main.Model.character.MonsterFactory;
import main.Model.util.MonsterType;
import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MonsterFactoryTest {
    private MonsterFactory myMonsterFactory;

    @BeforeEach
    void setUp() {
        myMonsterFactory = new MonsterFactory();
    }

    @Test
    void getMonster_goblin() {
        Monster monster = myMonsterFactory.getMonster(MonsterType.GOBLIN, new Point(0, 0));
        assertEquals(8, monster.getBaseAttackDamage());
        assertEquals("Goblin", monster.getName());
    }

    @Test
    void getMonster_skeleton() {
        Monster monster = myMonsterFactory.getMonster(MonsterType.SKELETON, new Point(0, 0));
        assertEquals(10, monster.getBaseAttackDamage());
        assertEquals("Skeleton", monster.getName());
    }

    @Test
    void getMonster_slime() {
        Monster monster = myMonsterFactory.getMonster(MonsterType.SLIME, new Point(0, 0));
        assertEquals(5, monster.getBaseAttackDamage());
        assertEquals("Slime", monster.getName());
    }

    @Test
    void getMonster_orc() {
        Monster monster = myMonsterFactory.getMonster(MonsterType.ORC, new Point(0, 0));
        assertEquals(15, monster.getBaseAttackDamage());
        assertEquals("Orc", monster.getName());
    }

    @Test
    void getMonster_big_Slime() {
        Monster monster = myMonsterFactory.getMonster(MonsterType.BIG_SLIME, new Point(0, 0));
        assertEquals(12, monster.getBaseAttackDamage());
        assertEquals("Big Slime", monster.getName());
    }

    @Test
    void getMonster_wizard() {
        Monster monster = myMonsterFactory.getMonster(MonsterType.WIZARD, new Point(0, 0));
        assertEquals(20, monster.getBaseAttackDamage());
        assertEquals("Wizard", monster.getName());
    }

    @Test
    void getMonster_giant() {
        Monster monster = myMonsterFactory.getMonster(MonsterType.GIANT, new Point(0, 0));
        assertEquals(25, monster.getBaseAttackDamage());
        assertEquals("Giant", monster.getName());
    }

    // buildMonster is indirectly tested through the above methods, and builder tests in Monster and Character tests.
}
